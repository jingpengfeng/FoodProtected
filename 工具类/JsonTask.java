package com.gzmob.utils;

import java.util.HashMap;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

/**
 * 异步读取网络数据
 */
public class JsonTask {

	/**
	 * 回调接口
	 */
	public interface JsonCallBack {

		public void getJsonCallBack(int tag_switch, String str_json,
				boolean isPageLoad, int page, HashMap<String, Object> hashMap,
				ProgressDialog progressDialog);

		public void getError(int tag_switch);
	}

	AQuery aq;
	Context context;
	JsonCallBack jsonCallBack;
	ProgressDialog progressDialog;
	boolean isShowProgress = true;// 是否需要显示进度提示
	int tag_switch = 0;// 数据分流的标签
	String url;// 接口的地址
	int page = 1;
	String pageSize = "50";
	AjaxCallback<String> ajaxCallback;
	boolean isPageLoad = false;
	String str_progress = "正在读取数据，请稍候";

	public void setStr_progress(String str_progress) {
		this.str_progress = str_progress;
	}

	/**
	 * 读取JSON，默认带进度提示
	 */
	public JsonTask(Context context, String url_joint, String tag_action,
			JsonCallBack jsonCallBack, int tag_switch) {
		aq = new AQuery(context);
		this.context = context;
		this.jsonCallBack = jsonCallBack;
		this.tag_switch = tag_switch;
		this.url = Utils.url_root + url_joint + tag_action;
	}

	/**
	 * 读取JSON，可设置进度提示
	 */
	public JsonTask(Context context, String url_joint, String tag_action,
			JsonCallBack jsonCallBack, int tag_switch, boolean isShowProgress) {
		aq = new AQuery(context);
		this.context = context;
		this.jsonCallBack = jsonCallBack;
		this.isShowProgress = isShowProgress;
		this.tag_switch = tag_switch;
		this.url = Utils.url_root + url_joint + tag_action;
	}

	/**
	 * 读取JSON，针对大数据用分页方式来递归遍历读取
	 */
	public JsonTask(Context context, String url_joint, String tag_action,
			JsonCallBack jsonCallBack, int tag_switch, boolean isShowProgress,
			int page, String str_progress, ProgressDialog progressDialog) {
		aq = new AQuery(context);
		this.context = context;
		this.jsonCallBack = jsonCallBack;
		this.isShowProgress = isShowProgress;
		this.tag_switch = tag_switch;
		this.url = Utils.url_root + url_joint + tag_action;
		this.page = page;
		if (str_progress != null) {
			this.str_progress = str_progress;
		}
		this.progressDialog = progressDialog;
	}

	/**
	 * 使用AQuery获取JSON
	 */
	public void asyncJson(final HashMap<String, Object> hashMap) {

		// 读取进度提示
		if (isShowProgress && progressDialog == null
				&& Utils.NetIsConnect(context)) {
			progressDialog = ProgressDialog.show(context, null, str_progress,
					true, false);
		}

		ajaxCallback = new AjaxCallback<String>() {
			@Override
			public void callback(String str_url, String str_json,
					AjaxStatus status) {

				Log4Trace.i("Request URL:");
				Log4Trace.d(str_url);
				Log4Trace.i("Request Method:POST");
				Log4Trace.d(String.valueOf(hashMap));
				if (str_json != null) {

					if (str_json != null && str_json.startsWith("\ufeff")) { // 去除BOM头部
						str_json = str_json.substring(1);
					}

					Log4Trace.i("Response:");
					Log4Trace.d(str_json);

					try {
						JSONObject jsonObject = new JSONObject(str_json);
						if ("0".equals(jsonObject.getString("status"))) {
							Toast.makeText(context,
									jsonObject.getString("error_msg"),
									Toast.LENGTH_SHORT).show();
						} else {

							try {
								// 返回数据进行分页判断
								if (jsonObject.getString("list") != null
										&& jsonObject.getString("list")
												.length() > 3) {

									page++;
									isPageLoad = true;
								}
							} catch (Exception e) {
								Log4Trace.show(e);
							}

							// 如果JSON返回的数据不符合要求，isPageLoad就是默认false终结这次的递归请求
							jsonCallBack.getJsonCallBack(tag_switch, str_json,
									isPageLoad, page, hashMap, progressDialog);
						}
					} catch (Exception e) {
						Log4Trace.show(e);
						jsonCallBack.getError(tag_switch);
					}
				} else {
					Log4Trace.show("Error:" + status.getCode());
					jsonCallBack.getError(tag_switch);
				}
				Log4Trace.v("... ...");
				Log4Trace.v(" ");

				// 不再递归，执行到最后一次了
				if (isPageLoad == false && isShowProgress
						&& progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		};

		// 临时添加，用作获取只属于该用户的数据
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		HashMap<String, Object> hashMap_temp = hashMap;
		hashMap_temp.put("uoids", sp.getString("uoids", ""));

		aq.ajax(url + "?pageSize=" + pageSize + "&page=" + String.valueOf(page),
				hashMap_temp, String.class, ajaxCallback);
	}
}