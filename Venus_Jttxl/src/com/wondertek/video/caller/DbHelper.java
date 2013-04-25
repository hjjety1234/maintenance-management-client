package com.wondertek.video.caller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = "DbHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Department table name
	private static final String TABLE_DEPARTMENT = "tb_c_department";

	// Employee table name
	private static final String TABLE_EMPLOYEE = "tb_c_employee";

	// Call history table name
	private static final String TABLE_CALL_HIS = "tb_c_callhis";

	// Department Table Columns names
	private static final String KEY_DEPARTMENT_ID = "department_id";
	private static final String KEY_DEPARTMENT_NAME = "department_name";
	private static final String KEY_PARENT_DEPARTMENT_ID = "parent_department_id";
	private static final String KEY_DEPARTMENT_LEVEL = "department_level";

	// Employee table Columns names
	private static final String KEY_EMPLOYEE_ID = "employee_id";
	private static final String KEY_EMPLOYEE_NAME = "employee_name";
	private static final String KEY_HEADSHIP_ID = "headship_id";
	private static final String KEY_HEADSHIP_NAME = "headship_name";
	private static final String KEY_MOBILE = "mobile";
	private static final String KEY_MOBILE_SHORT = "mobile_short";
	private static final String KEY_TEL = "tel";
	private static final String KEY_TEL_SHORT = "tel_short";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_VERSIONNUM = "versionNum";
	private static final String KEY_PICTURE = "picture";
	private static final String KEY_EMPLOYEE_FIRSTWORD = "employee_firstword";

	// Call history table Columns names
	private static final String KEY_CALLHIS_ID = "callhis_id"; // auto increment
																// primary key
	private static final String KEY_CALLHIS_EMPID = "callhis_empid"; // employee
																		// id
	private static final String KEY_CALLHIS_NUM = "callhis_num"; // phone num
	private static final String KEY_CALLHIS_DATE = "callhis_date"; // last call
																	// date
	private static final String KEY_CALLHIS_COUNT = "callhis_count"; // call
																		// count
	private static final String KEY_CALLHIS_TYPE = "callhis_type"; // when 0
																	// recevive
																	// call,when
																	// 1 call
																	// other

	public DbHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "[onCreate]");
		String CREATE_DEPARTMENT_TABLE = String.format(
				"CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s, %s, %s)",
				TABLE_DEPARTMENT, KEY_DEPARTMENT_ID, KEY_DEPARTMENT_NAME,
				KEY_PARENT_DEPARTMENT_ID, KEY_DEPARTMENT_LEVEL);
		Log.d(TAG, CREATE_DEPARTMENT_TABLE);
		db.execSQL(CREATE_DEPARTMENT_TABLE);

		String CREATE_EMPLOYEE_TABLE = String.format(
				"CREATE TABLE %s(%s INTEGER PRIMARY KEY, "
						+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
				TABLE_EMPLOYEE, KEY_EMPLOYEE_ID, KEY_EMPLOYEE_NAME,
				KEY_DEPARTMENT_ID, KEY_DEPARTMENT_NAME, KEY_HEADSHIP_ID,
				KEY_HEADSHIP_NAME, KEY_MOBILE, KEY_MOBILE_SHORT, KEY_TEL,
				KEY_TEL_SHORT, KEY_EMAIL, KEY_VERSIONNUM, KEY_PICTURE,
				KEY_EMPLOYEE_FIRSTWORD);
		Log.d(TAG, CREATE_EMPLOYEE_TABLE);
		db.execSQL(CREATE_EMPLOYEE_TABLE);

		String CREATE_CALLHIS_TABLE = String
				.format("CREATE TABLE %s(%s integer PRIMARY KEY autoincrement,"
						+ "%s,%s datetime default (datetime('now', 'localtime')),%s,%s,%s)",
						TABLE_CALL_HIS, KEY_CALLHIS_ID, KEY_CALLHIS_NUM,
						KEY_CALLHIS_DATE, KEY_CALLHIS_TYPE, KEY_CALLHIS_COUNT,
						KEY_CALLHIS_EMPID);
		Log.d(TAG, CREATE_CALLHIS_TABLE);
		db.execSQL(CREATE_CALLHIS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "[onUpgrade]");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
		onCreate(db);
	}

	// Getting single employee
	public Employee getEmployee(String number) {
		SQLiteDatabase db = null;
		Log.d(TAG, "[getEmployee] number: " + number);
		try {
			// db = this.getReadableDatabase();
			db = SQLiteDatabase.openDatabase(Constants.DATABASE_NAME, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = db.query(TABLE_EMPLOYEE,
					new String[] { KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
							KEY_DEPARTMENT_NAME, KEY_HEADSHIP_NAME, KEY_MOBILE,
							KEY_PICTURE, KEY_EMPLOYEE_ID }, KEY_MOBILE
							+ "=? or " + KEY_MOBILE_SHORT + "=? or " + KEY_TEL
							+ "=? or " + KEY_TEL_SHORT + "=?", new String[] {
							number, number, number, number }, null, null, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				String name = cursor.getString(0);
				Log.d(TAG, "[getEmployee] name: " + name);
				String departmentId = cursor.getString(1);
				Log.d(TAG, "[getEmployee] departmentId: " + departmentId);
				String departmentName = cursor.getString(2);
				Log.d(TAG, "[getEmployee] departmentName: " + departmentName);
				String qualifiedDeptName = getQualifiedDeptName(db,
						departmentId);
				if (qualifiedDeptName.substring(0, 1).equals("\\"))
					qualifiedDeptName = qualifiedDeptName.substring(1,
							qualifiedDeptName.length());
				Log.d(TAG, "[getEmployee] qualifiedDeptName: "
						+ qualifiedDeptName);
				String headshipName = cursor.getString(3);
				Log.d(TAG, "[getEmployee] headshipName: " + headshipName);
				String mobile = cursor.getString(4);
				Log.d(TAG, "[getEmployee] mobile: " + mobile);
				String picture = cursor.getString(5);
				Log.d(TAG, "[getEmployee] picutre: " + picture);
				String empid = cursor.getString(6);
				Log.d(TAG, "[getEmployee] employee_id:" + empid);
				cursor.close();
				db.close();
				return new Employee(empid, name, mobile, headshipName,
						qualifiedDeptName, picture);
			} else {
				Log.d(TAG,
						"[getEmployee] can't find the caller in sqlite database.");
				db.close();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (db != null)
				db.close();
			return null;
		}
	}

	// get employee id by phone number
	public String getEmployeeId(String number) {
		SQLiteDatabase db = null;
		Log.d(TAG, "[getEmployeeId] number: " + number);
		try {
			// db = this.getReadableDatabase();
			db = SQLiteDatabase.openDatabase(Constants.DATABASE_NAME, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = db.query(TABLE_EMPLOYEE,
					new String[] { KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
							KEY_DEPARTMENT_NAME, KEY_HEADSHIP_NAME, KEY_MOBILE,
							KEY_PICTURE, KEY_EMPLOYEE_ID }, KEY_MOBILE
							+ "=? or " + KEY_MOBILE_SHORT + "=? or " + KEY_TEL
							+ "=? or " + KEY_TEL_SHORT + "=?", new String[] {
							number, number, number, number }, null, null, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				String empid = cursor.getString(6);
				Log.d(TAG, "[getEmployeeId] employee_id:" + empid);
				cursor.close();
				db.close();
				return empid;
			} else {
				Log.d(TAG,
						"[getEmployeeId] can't find the employee in sqlite database.");
				db.close();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (db != null)
				db.close();
			return null;
		}
	}

	public int getHisCount() {
		Log.d(TAG, ">>>getHisCount<<<");
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openDatabase(Constants.DATABASE_NAME, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = db.query(TABLE_CALL_HIS,
					new String[] { KEY_CALLHIS_ID }, null, null, null, null,
					null);
			Log.d(TAG, "[getHisCount] call history count: " + cursor.getCount());
			return cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}

	// record the history when receive calls or call to other
	public void recordCallHistory(String phoneNum, String type, String empid,
			String datetime) {
		Log.d(TAG, "[recordCallHistory] phoneNum:" + phoneNum);
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openDatabase(Constants.DATABASE_NAME, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			try {
				// 如果数据表不存在，则创建之。有问题，先try catch掉
				String CREATE_CALLHIS_TABLE = String
						.format("CREATE TABLE %s(%s integer PRIMARY KEY autoincrement,"
								+ "%s,%s datetime default (datetime('now', 'localtime')),%s, %s, %s)",
								TABLE_CALL_HIS, KEY_CALLHIS_ID,
								KEY_CALLHIS_NUM, KEY_CALLHIS_DATE,
								KEY_CALLHIS_TYPE, KEY_CALLHIS_COUNT,
								KEY_CALLHIS_EMPID);
				Log.d(TAG, CREATE_CALLHIS_TABLE);
				db.execSQL(CREATE_CALLHIS_TABLE);
			} catch (Exception e) {
				// e.printStackTrace();
			}

			// if the phone num exist,add count + 1
			Cursor cursor = db.query(TABLE_CALL_HIS, new String[] {
					KEY_CALLHIS_ID, KEY_CALLHIS_NUM },
					KEY_CALLHIS_EMPID + "=?", new String[] { empid }, null,
					null, null);
			Log.d(TAG, KEY_CALLHIS_NUM + "=" + phoneNum);
			if (cursor != null && cursor.moveToFirst()) {
				if (datetime == null)
					db.execSQL(
							String.format(
									"UPDATE %s SET callhis_count=callhis_count+1,callhis_date=datetime('now','localtime'),callhis_type=? WHERE %s=?",
									TABLE_CALL_HIS, KEY_CALLHIS_EMPID),
							new String[] { type, empid });
				else
					db.execSQL(
							String.format(
									"UPDATE %s SET callhis_count=callhis_count+1,callhis_date=?,callhis_type=? WHERE %s=?",
									TABLE_CALL_HIS, KEY_CALLHIS_EMPID),
							new String[] { datetime, type, empid });
			} else {
				if (datetime == null)
					db.execSQL(String.format(
							"INSERT INTO %s(%s,%s,%s,%s) VALUES (%s,%s,%s,?)",
							TABLE_CALL_HIS, KEY_CALLHIS_NUM, KEY_CALLHIS_TYPE,
							KEY_CALLHIS_COUNT, KEY_CALLHIS_EMPID, phoneNum,
							type, 1), new String[] { empid });
				else
					db.execSQL(
							String.format(
									"INSERT INTO %s(%s,%s,%s,%s,%s) VALUES (%s,%s,%s,?,?)",
									TABLE_CALL_HIS, KEY_CALLHIS_NUM,
									KEY_CALLHIS_TYPE, KEY_CALLHIS_COUNT,
									KEY_CALLHIS_EMPID, KEY_CALLHIS_DATE,
									phoneNum, type, 1), new String[] { empid,
									datetime });
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}

	// Get full qualified department name
	private String getQualifiedDeptName(SQLiteDatabase db, String departmentId) {
		Log.d(TAG, "[getQualifiedDeptName] departmentId: " + departmentId);
		Cursor cursor = db.query(TABLE_DEPARTMENT, new String[] {
				KEY_DEPARTMENT_ID, KEY_DEPARTMENT_NAME,
				KEY_PARENT_DEPARTMENT_ID }, KEY_DEPARTMENT_ID + "=?",
				new String[] { departmentId }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			return getQualifiedDeptName(db, cursor.getString(2)) + "\\"
					+ cursor.getString(1);
		} else {
			cursor.close();
			return "";
		}
	}
}
