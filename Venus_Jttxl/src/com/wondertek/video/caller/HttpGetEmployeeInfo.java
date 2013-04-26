package com.wondertek.video.caller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class HttpGetEmployeeInfo {
	private static final String TAG = "HttpGetEmployeeInfo";

	public Employee getEmployee(String number, String strDepartmentFax) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			Log.d(TAG, "[getEmployee] request uri: "
					+ Constants.CALLER_INFO_URL_PREFIX + number
					+ "&department_fax=" + strDepartmentFax);
			response = httpclient.execute(new HttpGet(
					Constants.CALLER_INFO_URL_PREFIX + number
							+ "&department_fax=" + strDepartmentFax));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
						.readLine()) {
					builder.append(s);
				}
				Log.d(TAG, "[getEmployee] response: " + builder.toString());
				JSONObject obj = new JSONObject(builder.toString());
				JSONArray value = obj.getJSONArray("value");
				int total = obj.getInt("total");
				Log.d(TAG, "[getEmployee] total: " + total);
				if (total > 0) {
					JSONObject jsonEmployee = value.getJSONObject(0);
					String name = jsonEmployee.getString("employee_name");
					Log.d(TAG, "[getEmployee] employee_name: " + name);
					String mobile = jsonEmployee.getString("mobile");
					Log.d(TAG, "[getEmployee] mobile: " + mobile);
					String headship = jsonEmployee.getString("headshipName");
					Log.d(TAG, "[getEmployee] headship: " + headship);
					String department = jsonEmployee
							.getString("parent_department_name")
							+ " "
							+ jsonEmployee.getString("department_name");
					Log.d(TAG, "[getEmployee] department: " + department);
					String picture = jsonEmployee.getString("picture");
					Log.d(TAG, "[getEmployee] picture: " + picture);
					String empid = jsonEmployee.getString("employee_id");
					Log.d(TAG, "[getEmployee] employee_id: " + empid);
					String departmentFax = jsonEmployee
							.getString("department_fax");
					Log.d(TAG, "[getEmployee] department_fax: " + departmentFax);
					if (!strDepartmentFax.equals(departmentFax)) {
						Log.d(TAG,
								"[getEmployee] system user's deparment fax is not equals to this employee...");
						return null;
					} else {
						Employee e = new Employee(empid, name, mobile,
								headship, department, picture, departmentFax);
						return e;
					}
				} else {
					Log.d(TAG,
							"[getEmployee] can't find employee's information by http method! ");
					return null;
				}
			} else {
				response.getEntity().getContent().close();
				Log.d(TAG,
						"[getEmployee] status code : "
								+ statusLine.getStatusCode());
				Log.d(TAG,
						"[getEmployee] status phrase : "
								+ statusLine.getReasonPhrase());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
