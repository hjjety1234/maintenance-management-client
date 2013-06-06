package com.wondertek.video.caller;

import java.io.File;
import java.io.IOException;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;

import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = "DbHelper";

	private static final String password = "jttxl";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Department table name
	private static final String TABLE_DEPARTMENT = "tb_c_department";

	// Employee table name
	private static final String TABLE_EMPLOYEE = "tb_c_employee";

	// Call history table name
	private static final String TABLE_CALL_HIS = "tb_c_callhis";

	// system user table
	private static final String TABLE_SYSTEM = "tb_c_system";
	
	// company table
	public static final String TABLE_COMPANY = "tb_c_company";

	// Department Table Columns names
	private static final String KEY_DEPARTMENT_ID = "department_id";
	private static final String KEY_DEPARTMENT_NAME = "department_name";
	private static final String KEY_PARENT_DEPARTMENT_ID = "parent_department_id";
	private static final String KEY_DEPARTMENT_LEVEL = "department_level";

	// Employee table Columns names
	private static final String KEY_EMPLOYEE_ID = "employee_id";
	private static final String KEY_EMPLOYEE_NAME = "employee_name";
	private static final String KEY_EMPLOYEE_HEADSHIP_ID = "headship_id";
	private static final String KEY_EMPLOYEE_HEADSHIP_NAME = "headship_name";
	private static final String KEY_EMPLOYEE_MOBILE = "mobile";
	private static final String KEY_EMPLOYEE_MOBILE_SHORT = "mobile_short";
	private static final String KEY_EMPLOYEE_TEL = "tel";
	private static final String KEY_EMPLOYEE_TEL_SHORT = "tel_short";
	private static final String KEY_EMPLOYEE_EMAIL = "email";
	private static final String KEY_EMPLOYEE_VERSIONNUM = "versionNum";
	private static final String KEY_EMPLOYEE_PICTURE = "picture";
	private static final String KEY_EMPLOYEE_FIRSTWORD = "employee_firstword";
	private static final String KEY_EMPLOYEE_DEPARTMENT_FAX = "department_fax";
	private static final String KEY_EMPLOYEE_COMPANY_ID = "company_id";

	// Call history table Columns names
	private static final String KEY_CALLHIS_ID = "callhis_id";
	private static final String KEY_CALLHIS_EMPID = "callhis_empid";
	private static final String KEY_CALLHIS_NUM = "callhis_num";
	private static final String KEY_CALLHIS_DATE = "callhis_date";
	private static final String KEY_CALLHIS_COUNT = "callhis_count";
	private static final String KEY_CALLHIS_TYPE = "callhis_type";

	// system user columns names
	private static final String KEY_SYSTEM_ID = "system_id";
	private static final String KEY_SYSTEM_REGISTER_FLAG = "register_flag";
	private static final String KEY_SYSTEM_DEPARTMENT_VERSION = "department_version";
	private static final String KEY_SYSTEM_EMPLOYEE_VERSION = "employee_version";
	private static final String KEY_SYSTEM_USER_ID = "user_id";
	private static final String KEY_SYSTEM_RIGHT_CONFIG = "right_config";
	private static final String KEY_SYSTEM_EMPLOYEE_ID = "employee_id";
	private static final String KEY_SYSTEM_EMPLOYEE_NAME = "employee_name";
	private static final String KEY_SYSTEM_DEPARTMENT_NAME = "department_name";
	private static final String KEY_SYSTEM_DEPARTMENT_LEVEL = "department_level";
	
	// company table columns names
	private static final String KEY_COMPANY_ID = "company_id";
	private static final String KEY_COMPANY_INDEX_LOGO = "index_logo";
	private static final String KEY_COMPANY_NAME = "company_name";

	private static Employee systemUser = null;

	public DbHelper(Context context) {
		super(context, Constants.getDatabaseName(), null, DATABASE_VERSION);
		systemUser = getSystemUser();
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
				KEY_DEPARTMENT_ID, KEY_DEPARTMENT_NAME,
				KEY_EMPLOYEE_HEADSHIP_ID, KEY_EMPLOYEE_HEADSHIP_NAME,
				KEY_EMPLOYEE_MOBILE, KEY_EMPLOYEE_MOBILE_SHORT,
				KEY_EMPLOYEE_TEL, KEY_EMPLOYEE_TEL_SHORT, KEY_EMPLOYEE_EMAIL,
				KEY_EMPLOYEE_VERSIONNUM, KEY_EMPLOYEE_PICTURE,
				KEY_EMPLOYEE_FIRSTWORD, KEY_EMPLOYEE_DEPARTMENT_FAX);
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
			db = SQLiteDatabase.openDatabase(Constants.getDatabaseName(), password,
					null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = null;

			// check if this is the short number
			if (number.length() == 6) {
				Log.d(TAG, "[getEmployee] " + number + " is short number!");
				// get current system user
				if (systemUser != null && systemUser.getDepartmentFax() != "") {
					Log.d(TAG, "[getEmployee] system user deparment fax is: "
							+ systemUser.getDepartmentFax());
					cursor = db.query(TABLE_EMPLOYEE, new String[] {
							KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
							KEY_DEPARTMENT_NAME, KEY_EMPLOYEE_HEADSHIP_NAME,
							KEY_EMPLOYEE_MOBILE, KEY_EMPLOYEE_PICTURE,
							KEY_EMPLOYEE_ID, KEY_EMPLOYEE_DEPARTMENT_FAX }, "("
							+ KEY_EMPLOYEE_DEPARTMENT_FAX + "=?) and ("
							+ KEY_EMPLOYEE_MOBILE_SHORT + "=? or "
							+ KEY_EMPLOYEE_TEL_SHORT + "=? )", new String[] {
							systemUser.getDepartmentFax(), number, number },
							null, null, null, null);
				} else {
					Log.w(TAG,
							"[getEmployee] system user's deparment fax is empty, return null!");
					db.close();
					return null;
				}
			} else {
				Log.d(TAG, "[getEmployee] " + number + " is not short number!");
				cursor = db.query(TABLE_EMPLOYEE, new String[] {
						KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
						KEY_DEPARTMENT_NAME, KEY_EMPLOYEE_HEADSHIP_NAME,
						KEY_EMPLOYEE_MOBILE, KEY_EMPLOYEE_PICTURE,
						KEY_EMPLOYEE_ID, KEY_EMPLOYEE_DEPARTMENT_FAX, 
						KEY_EMPLOYEE_COMPANY_ID },
						KEY_EMPLOYEE_MOBILE + "=? or " + KEY_EMPLOYEE_TEL
								+ "=?", new String[] { number, number }, null,
						null, null, null);
			}

			if (cursor != null && cursor.moveToFirst()) {
				String name = cursor.getString(0);
				Log.d(TAG, "[getEmployee] name: " + name);
				String departmentId = cursor.getString(1);
				Log.d(TAG, "[getEmployee] departmentId: " + departmentId);
				String departmentName = cursor.getString(2);
				Log.d(TAG, "[getEmployee] departmentName: " + departmentName);
				String qualifiedDeptName = "";
				if (departmentId != null && !departmentId.trim().equals("")) {
					qualifiedDeptName = getQualifiedDeptName(db, departmentId);
					if (qualifiedDeptName.substring(0, 1).equals("\\"))
						qualifiedDeptName = qualifiedDeptName.substring(1, qualifiedDeptName.length());
				}else {
					// no department info, see if we can find any company information
					qualifiedDeptName = "未知群组";
					String companyId = cursor.getString(cursor.getColumnIndex(KEY_EMPLOYEE_COMPANY_ID));
					if (companyId != null && !companyId.equals("")) {
						Cursor c = db.query(TABLE_COMPANY, new String[]{KEY_COMPANY_ID, KEY_COMPANY_NAME}, 
								KEY_COMPANY_ID + "=?", new String[]{ companyId }, null, null, null);
						if (c != null && c.moveToFirst()) {
							qualifiedDeptName = c.getString(c.getColumnIndex(KEY_COMPANY_NAME));
						}
						c.close();
					}
				}
				Log.d(TAG, "[getEmployee] qualifiedDeptName: " + qualifiedDeptName);
				String headshipName = cursor.getString(3);
				Log.d(TAG, "[getEmployee] headshipName: " + headshipName);
				String mobile = cursor.getString(4);
				Log.d(TAG, "[getEmployee] mobile: " + mobile);
				String picture = cursor.getString(5);
				Log.d(TAG, "[getEmployee] picutre: " + picture);
				String empolyeeId = cursor.getString(6);
				Log.d(TAG, "[getEmployee] employee_id:" + empolyeeId);
				String departmentFax = cursor.getString(7);
				Log.d(TAG, "[getEmployee] department fax:" + departmentFax);
				cursor.close();
				db.close();
				return new Employee(empolyeeId, name, mobile, headshipName,
						qualifiedDeptName, picture, departmentFax);
			} else {
				Log.d(TAG,
						"[getEmployee] can't find the caller in sqlite database.");
				if (cursor != null)
					cursor.close();
				db.close();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}

	// get employee id by phone number
	public String getEmployeeId(String number) {
		SQLiteDatabase db = null;
		Log.d(TAG, "[getEmployeeId] number: " + number);
		try {
			// db = this.getReadableDatabase();
			db = SQLiteDatabase.openDatabase(Constants.getDatabaseName(), password,
					null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = null;

			// check if this is the short number
			if (number.length() == 6) {
				Log.d(TAG, "[getEmployeeId] " + number + " is short number!");
				// get current system user
				if (systemUser != null && systemUser.getDepartmentFax() != "") {
					Log.d(TAG, "[getEmployeeId] system user deparment fax is: "
							+ systemUser.getDepartmentFax());
					cursor = db.query(TABLE_EMPLOYEE, new String[] {
							KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
							KEY_DEPARTMENT_NAME, KEY_EMPLOYEE_HEADSHIP_NAME,
							KEY_EMPLOYEE_MOBILE, KEY_EMPLOYEE_PICTURE,
							KEY_EMPLOYEE_ID, KEY_EMPLOYEE_DEPARTMENT_FAX }, "("
							+ KEY_EMPLOYEE_DEPARTMENT_FAX + "=?) and ("
							+ KEY_EMPLOYEE_MOBILE_SHORT + "=? )", new String[] {
							systemUser.getDepartmentFax(), number }, null,
							null, null, null);
				} else {
					Log.w(TAG,
							"[getEmployeeId] system user's deparment fax is empty, do nothing!");
					db.close();
					return null;
				}
			} else {
				Log.d(TAG, "[getEmployeeId] " + number
						+ " is not short number!");
				cursor = db.query(TABLE_EMPLOYEE, new String[] {
						KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
						KEY_DEPARTMENT_NAME, KEY_EMPLOYEE_HEADSHIP_NAME,
						KEY_EMPLOYEE_MOBILE, KEY_EMPLOYEE_PICTURE,
						KEY_EMPLOYEE_ID, KEY_EMPLOYEE_DEPARTMENT_FAX },
						KEY_EMPLOYEE_MOBILE + "=? or " + KEY_EMPLOYEE_TEL
								+ "=?", new String[] { number, number }, null,
						null, null, null);
			}
			if (cursor != null && cursor.moveToFirst()) {
				String empid = cursor.getString(6);
				Log.d(TAG, "[getEmployeeId] employee_id:" + empid);
				cursor.close();
				db.close();
				return empid;
			} else {
				Log.d(TAG,
						"[getEmployeeId] can't find the employee in sqlite database.");
				if (cursor != null)
					cursor.close();
				db.close();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}

	// get current system login user
	public Employee getSystemUser() {
		Log.d(TAG, ">>>getSystemUser<<< ");
		if (systemUser != null) {
			Log.d(TAG, "[getSystemUser] system user is already found...");
			return systemUser;
		}
		SQLiteDatabase db = null;
		try {
			// db = this.getReadableDatabase();
			db = SQLiteDatabase.openDatabase(Constants.getDatabaseName(), password,
					null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = db.query(TABLE_SYSTEM,
					new String[] { KEY_SYSTEM_EMPLOYEE_ID }, null, null, null,
					null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				String employeeId = cursor.getString(0);
				Log.d(TAG, "[getSystemUser] system user employee id: "
						+ employeeId);
				cursor.close();
				cursor = db.query(TABLE_EMPLOYEE, new String[] {
						KEY_EMPLOYEE_NAME, KEY_DEPARTMENT_ID,
						KEY_DEPARTMENT_NAME, KEY_EMPLOYEE_HEADSHIP_NAME,
						KEY_EMPLOYEE_MOBILE, KEY_EMPLOYEE_PICTURE,
						KEY_EMPLOYEE_ID, KEY_EMPLOYEE_DEPARTMENT_FAX },
						KEY_EMPLOYEE_ID + "=?", new String[] { employeeId },
						null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					String name = cursor.getString(0);
					Log.d(TAG, "[getSystemUser] name: " + name);
					String departmentId = cursor.getString(1);
					Log.d(TAG, "[getSystemUser] departmentId: " + departmentId);
					String qualifiedDeptName = "";
					if (departmentId != null && !departmentId.trim().equals("")) {
						qualifiedDeptName = getQualifiedDeptName(db, departmentId);
						if (qualifiedDeptName.substring(0, 1).equals("\\"))
							qualifiedDeptName = qualifiedDeptName.substring(1, qualifiedDeptName.length());
					}
					Log.d(TAG, "[getSystemUser] qualifiedDeptName: " + qualifiedDeptName);
					String headshipName = cursor.getString(3);
					Log.d(TAG, "[getSystemUser] headshipName: " + headshipName);
					String mobile = cursor.getString(4);
					Log.d(TAG, "[getSystemUser] mobile: " + mobile);
					String picture = cursor.getString(5);
					Log.d(TAG, "[getSystemUser] picutre: " + picture);
					String empid = cursor.getString(6);
					Log.d(TAG, "[getSystemUser] employee_id:" + empid);
					String departmentFax = cursor.getString(7);
					Log.d(TAG, "[getSystemUser] department fax:"
							+ departmentFax);
					systemUser = new Employee(empid, name, mobile,
							headshipName, qualifiedDeptName, picture,
							departmentFax);
					cursor.close();
					Log.d(TAG, ">>>[getSystemUser] finished<<<");
					return systemUser;
				} else {
					if (cursor != null)
						cursor.close();
					Log.d(TAG,
							"[getSystemUser] can't find the system user info in employee table.");
				}
				db.close();
				return null;
			} else {
				Log.d(TAG,
						"[getSystemUser] can't find the system user in sqlite database.");
				if (cursor != null)
					cursor.close();
				db.close();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}

	public int getHisCount() {
		Log.d(TAG, ">>>getHisCount<<<");
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openDatabase(Constants.getDatabaseName(), password,
					null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);

			// create call history table if not exist
			String CREATE_CALLHIS_TABLE = String
					.format("CREATE TABLE IF NOT EXISTS %s(%s integer PRIMARY KEY autoincrement,"
							+ "%s varchar(20), %s datetime default (datetime('now', 'localtime')),%s, %s, %s)",
							TABLE_CALL_HIS, KEY_CALLHIS_ID, KEY_CALLHIS_NUM,
							KEY_CALLHIS_DATE, KEY_CALLHIS_TYPE,
							KEY_CALLHIS_COUNT, KEY_CALLHIS_EMPID);
			db.execSQL(CREATE_CALLHIS_TABLE);

			Cursor cursor = db.query(TABLE_CALL_HIS,
					new String[] { KEY_CALLHIS_ID }, null, null, null, null,
					null);
			Log.d(TAG, "[getHisCount] call history count: " + cursor.getCount());
			int count = cursor.getCount();
			cursor.close();
			return count;
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
		Log.d(TAG, "[recordCallHistory] phoneNum: " + phoneNum
				+ " employee id: " + empid);
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openDatabase(Constants.getDatabaseName(), password,
					null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);

			// create call history table if not exist
			String CREATE_CALLHIS_TABLE = String
					.format("CREATE TABLE IF NOT EXISTS %s(%s integer PRIMARY KEY autoincrement,"
							+ "%s varchar(20), %s datetime default (datetime('now', 'localtime')),%s, %s, %s)",
							TABLE_CALL_HIS, KEY_CALLHIS_ID, KEY_CALLHIS_NUM,
							KEY_CALLHIS_DATE, KEY_CALLHIS_TYPE,
							KEY_CALLHIS_COUNT, KEY_CALLHIS_EMPID);
			Log.d(TAG, CREATE_CALLHIS_TABLE);
			db.execSQL(CREATE_CALLHIS_TABLE);

			// if the phone number exist, add count + 1
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
							"INSERT INTO %s(%s,%s,%s,%s) VALUES (?,%s,%s,?)",
							TABLE_CALL_HIS, KEY_CALLHIS_NUM, KEY_CALLHIS_TYPE,
							KEY_CALLHIS_COUNT, KEY_CALLHIS_EMPID, type, 1),
							new String[] { phoneNum, empid });
				else
					db.execSQL(
							String.format(
									"INSERT INTO %s(%s,%s,%s,%s,%s) VALUES (?,%s,%s,?,?)",
									TABLE_CALL_HIS, KEY_CALLHIS_NUM,
									KEY_CALLHIS_TYPE, KEY_CALLHIS_COUNT,
									KEY_CALLHIS_EMPID, KEY_CALLHIS_DATE, type,
									1), new String[] { phoneNum, empid,
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
			String param1 = cursor.getString(2);
			String param2 = cursor.getString(1);
			cursor.close();
			return getQualifiedDeptName(db, param1) + "\\" + param2;
		} else {
			if (cursor != null)
				cursor.close();
			return "";
		}
	}

	// encrypt plain text database
	public static boolean encryptPlainTextDatabase() {
		File unencryptedDatabase = new File(Constants.getDatabaseName());
		File encryptedDatabase = new File(Constants.getTempDatabaseName());
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openOrCreateDatabase(unencryptedDatabase,
					"", null);
			database.rawExecSQL(String.format(
					"ATTACH DATABASE '%s' AS encrypted KEY '%s'",
					encryptedDatabase.getAbsolutePath(), password));
			database.rawExecSQL("select sqlcipher_export('encrypted')");
			database.rawExecSQL("DETACH DATABASE encrypted");
			database.close();
			encryptedDatabase.renameTo(unencryptedDatabase);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (database != null)
				database.close();
		}
		return true;
	}
	
	/**
	 * 获取LOGO图标的名称
	 * @return 图标名称
	 */
	public static String getLogoImg(String employeeId) {
		Log.d(TAG, "[getLogoImg] employeeId: " + employeeId);
		if (employeeId == null || employeeId.trim().equals("")) return null;
		
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openDatabase(Constants.getDatabaseName(), password,
					null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = db.query(TABLE_EMPLOYEE,
					new String[] { KEY_EMPLOYEE_COMPANY_ID }, 
					KEY_EMPLOYEE_ID + "=?", new String[]{employeeId}, null, null, null);
			
			// try to find company id for this employee 
			String companyId = null;
			if (cursor != null && cursor.moveToFirst()) {
				companyId = cursor.getString(cursor.getColumnIndex(KEY_EMPLOYEE_COMPANY_ID));
				Log.d(TAG, "[getLogoImg] companyId: " + companyId);
				cursor.close();
			}
			
			// get this company's logo file name 
			if (companyId != null){
				cursor = db.query(TABLE_COMPANY, 
						new String[] {KEY_COMPANY_INDEX_LOGO}, KEY_COMPANY_ID + "=?", 
						new String[]{companyId}, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					String indexLogo = cursor.getString(cursor.getColumnIndex(KEY_COMPANY_INDEX_LOGO));
					cursor.close();
					Log.i(TAG, "[getLogoImg] index logo is: " + indexLogo);
					if (indexLogo != null && indexLogo.trim().equals("")) return null;
					return indexLogo;
				}else {
					Log.w(TAG, "[getLogoImg] can't find index logo for company: " + companyId);
					return null;
				}
			}else {
				Log.w(TAG, "[getLogoImg] can't find company for employee: " + employeeId);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}
}
