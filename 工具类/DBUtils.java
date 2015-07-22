package com.gzmob.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.gzmob.model.DetectInfoQuickCheckListData;
import com.gzmob.model.KeyValueData;
import com.gzmob.model.SqlData;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzmob.model.FoodTypeData;

/**
 * 关于数据库的封装类
 */
public class DBUtils {

	public interface CheckDBListener {
		public void callback();
	}

	public interface DeleteDBListener {
		public void callback();
	}

	public interface SelectDBListener {

		public void callback(SQLiteDatabase sqLiteDatabase, Cursor cursor);

	}

	public interface UpdateDBListener {
		public void callback();
	}

	public interface PostDBListener {
		public void callback();
	}

	public final static String password = "gzmob"; // 加密的密码
	public final static String db_user = "user.db"; // “用户”数据库
	public final static String db_check_org = "check_org.db"; // “监管对象”数据库
	public final static String db_food = "food.db"; // “食品”数据库
	public final static String db_detect_project = "detect_project.db"; // “检测项目”数据库
	public final static String db_detect_info = "detect_info.db"; // “检测数据”数据库
	public final static String db_rating = "rating.db"; // “动态等级”数据库
	public final static String db_health = "health.db"; // “健康档案”数据库
	public final static String db_policy = "policy.db"; // “政策”数据库
	public final static String db_consign = "consign.db"; // “送检”数据库
	public final static String db_work = "work.db"; // “工作”数据库
	public final static String db_msg = "msg.db"; // “消息”数据库
	public final static String db_oe = "oe.db"; // “照相取证”数据库
	public final static String page_size = "50"; // 分页数量
    public final static String db_checktime="tb_base_checktime.db";
	/**
	 * 根据表名获取最后的更新时间
	 */
	public String getLastUpdateTime(Context context, String db_name) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (sp.getString(db_name, "").equals("")) {

			if (db_name.equals(db_user)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_check_org)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_food)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_detect_project)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_detect_info)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_rating)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_health)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_policy)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_consign)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_work)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_msg)) {
				return "2000-12-10 00:00:00";
			} else if (db_name.equals(db_oe)) {
				return "2000-12-10 00:00:00";
			} else {
				return "未知时间";
			}

		} else {
			return sp.getString(db_name, "");
		}
	}

	/**
	 * 新增查询-------锦鹏
	 */
	public void loadDBSelectDialog_new(final Context context,
			final TextView textView12, final TextView textView11,
			final TextView textView, final String d_name, final String name_db,
			final String name_table, final String name_column) {

		final EditText editText_02 = new EditText(context);
		editText_02.setHint("请输入样品名称关键字");

		new AlertDialog.Builder(context)
				.setView(editText_02)
				.setPositiveButton("模糊搜索",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String str = String.valueOf(
										editText_02.getText()).trim();
								if (!"".equals(str)) {

									// 默认查询
									new DBUtils().selectDB(context, name_db,
											"select * from " + name_table
													+ " where " + name_column
													+ " like ?",
											new String[] { "%" + str + "%" },
											new SelectDBListener() {
												@Override
												public void callback(
														SQLiteDatabase sqLiteDatabase,
														Cursor cursor) {

													if (cursor.moveToFirst()) {

														final String[] temp = new String[cursor
																.getCount()];

														for (; !cursor
																.isAfterLast(); cursor
																.moveToNext()) {
															temp[cursor
																	.getPosition()] = cursor
																	.getString(cursor
																			.getColumnIndex(name_column));
														}

														new AlertDialog.Builder(
																context)
																.setItems(
																		temp,
																		new DialogInterface.OnClickListener() {
																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {
																				textView.setText(temp[which]);

																				if (textView11 == null) {
																					biaozhun(
																							context,
																							temp[which],
																							textView12,
																							null,
																							d_name);

																				} else {
																					biaozhun(
																							context,
																							temp[which],
																							textView12,
																							textView11,
																							d_name);

																				}
																			}
																		})
																.show();

													} else {
														Toast.makeText(
																context,
																"暂无数据",
																Toast.LENGTH_SHORT)
																.show();
													}

													// 关闭数据库
													{
														cursor.close();
														sqLiteDatabase.close();
													}
												}
											});

								} else {
									Toast.makeText(context, "输入内容不能为空",
											Toast.LENGTH_LONG).show();
								}
							}
						}).show();

	}

	/**
	 * 新增查询-------锦鹏---------标准值和单位
	 */
	public void biaozhun(final Context context, final String food_name,
			final TextView textView, final TextView textView11,
			final String d_name) {

		final ArrayList<FoodTypeData> temps = new ArrayList<FoodTypeData>();

		// 食品种类ID
		new DBUtils().selectDB(context, DBUtils.db_food,
				"select FID from tb_base_foodtype where FTYPENAME=?",
				new String[] { food_name },// data.getFID()
				new SelectDBListener() {
					@Override
					public void callback(SQLiteDatabase sqLiteDatabase,
							Cursor cursor) {

						if (cursor.moveToFirst()) {

							for (; !cursor.isAfterLast(); cursor.moveToNext()) {

								final FoodTypeData foodTypeData1 = new FoodTypeData();
								foodTypeData1.setFID(cursor
										.getString(cursor.getColumnIndex("FID")));

								temps.add(foodTypeData1);

							}

						}

						{ // 关闭数据库
							cursor.close();
							sqLiteDatabase.close();
						}

						if (temps.size() > 0) {

							for (final FoodTypeData data : temps) {

								// 检测项目关联食品种类的ID
								new DBUtils()
										.selectDB(
												context,
												DBUtils.db_food,
												"select * from tb_base_food_checkitems where FID=? and DNAME=?",
												new String[] { data.getFID(),
														d_name },
												new SelectDBListener() {
													@Override
													public void callback(
															SQLiteDatabase sqLiteDatabase,
															Cursor cursor) {

														if (cursor
																.moveToFirst()) {
															for (; !cursor
																	.isAfterLast(); cursor
																	.moveToNext()) {
																String biaozhun = null;
																biaozhun = cursor
																		.getString(cursor
																				.getColumnIndex("CHECKSIGN"))
																		+ cursor.getString(cursor
																				.getColumnIndex("CHECKVALUE"));

																if (textView11 == null) {
																	textView.setText(biaozhun);

																} else {

																	textView.setText(biaozhun);
																	textView11
																			.setText(cursor
																					.getString(cursor
																							.getColumnIndex("CHECKVALUEUNT")));
																}

															}
														} else {
															// textView.setText("");
															// textView11.setText("");
															Toast.makeText(
																	context,
																	"检测项目没有标准值，因为没有关联！",
																	Toast.LENGTH_SHORT)
																	.show();

														}

														// 关闭数据库
														{
															cursor.close();
															sqLiteDatabase
																	.close();
														}

													}
												});
							}
						}
					}
				});

	}

	/**
	 * 新增，查询 -----锦鹏
	 */
	public void loadDBSelectDialogWithRole_new(final Context context,
			
			final TextView textView, final String name_db,
			final String name_table, final String name_column, final String key) {

		final EditText editText_02 = new EditText(context);
		editText_02.setHint("请输入抽样地点关键字");

		final SharedPreferences sp;

		sp = PreferenceManager.getDefaultSharedPreferences(context);

		new AlertDialog.Builder(context)
				.setView(editText_02)
				.setPositiveButton("模糊搜索",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String str = String.valueOf(
										editText_02.getText()).trim();
								if (!"".equals(str)) {

									// 默认查询
									new DBUtils().selectDB(
											context,
											name_db,
											"select * from "
													+ name_table
													+ " where "
													+ name_column
													+ " like ? and "
													+ sp.getString("sql_oname",
															"").replaceAll(
															"ONAME", key),
											new String[] { "%" + str + "%" },
											new SelectDBListener() {
												@Override
												public void callback(
														SQLiteDatabase sqLiteDatabase,
														Cursor cursor) {

													if (cursor.moveToFirst()) {

														final String[] temp = new String[cursor
																.getCount()];

														for (; !cursor
																.isAfterLast(); cursor
																.moveToNext()) {
															temp[cursor
																	.getPosition()] = cursor
																	.getString(cursor
																			.getColumnIndex(name_column));
														}

														new AlertDialog.Builder(
																context)
																.setItems(
																		temp,
																		new DialogInterface.OnClickListener() {
																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {
																				textView.setText(temp[which]);
																			
																		
																			}
																		})
																.show();

													} else {
														Toast.makeText(
																context,
																"暂无数据",
																Toast.LENGTH_SHORT)
																.show();
													}

													/* 关闭数据库 */
													{
														cursor.close();
														sqLiteDatabase.close();
													}
												}
											});

								} else {
									Toast.makeText(context, "输入内容不能为空",
											Toast.LENGTH_LONG).show();
								}
							}
						}).show();
	}


	
	/**
	 * 新增查询-------锦鹏---------根据检测项目名称----查检测依据
	 */
	public void jiance_YJ(final Context context, final String dname,
			 final TextView textView) {
	
		// 食品种类IDTB_BASE_DETECT_ITEMS
		new DBUtils().selectDB(context, DBUtils.db_detect_project,
				"select CHECKDNAME from tb_base_detect_item_detail where DNAME=\""+dname+"\"",
				null, new SelectDBListener() {
					@Override
					public void callback(SQLiteDatabase sqLiteDatabase,
							Cursor cursor) {

						if (cursor.moveToFirst()) {
							for (; !cursor
									.isAfterLast(); cursor
									.moveToNext()) {

						
									textView.setText(cursor.getString(cursor
											.getColumnIndex("CHECKDNAME")));
							}
						} else {

							textView.setText(""
									);
							Toast.makeText(context, "无检测依据", Toast.LENGTH_LONG)
									.show();

						}

						{ // 关闭数据库
							cursor.close();
							sqLiteDatabase.close();
						}

					}
				});

	}
	
	

	/**
	 * 新增查询————————-检测项目和检测依据
	 */
	public void loadDBSelectDialog_jiance(final Context context,
			final TextView textView13, final TextView textView,
			final String name_db, final String name_table,
			final String name_column) {

		final EditText editText_02 = new EditText(context);
		editText_02.setHint("请输入搜索内容");

		new AlertDialog.Builder(context)
				.setView(editText_02)
				.setPositiveButton("模糊搜索",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String str = String.valueOf(
										editText_02.getText()).trim();
								if (!"".equals(str)) {

									// 默认查询
									new DBUtils().selectDB(context, name_db,
											"select * from " + name_table
													+ " where " + name_column
													+ " like ?",
											new String[] { "%" + str + "%" },
											new SelectDBListener() {
												@Override
												public void callback(
														SQLiteDatabase sqLiteDatabase,
														Cursor cursor) {

													if (cursor.moveToFirst()) {

														final String[] temp = new String[cursor
																.getCount()];

														for (; !cursor
																.isAfterLast(); cursor
																.moveToNext()) {
															temp[cursor
																	.getPosition()] = cursor
																	.getString(cursor
																			.getColumnIndex(name_column));
														}

														new AlertDialog.Builder(
																context)
																.setItems(
																		temp,
																		new DialogInterface.OnClickListener() {
																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {

																				textView.setText(temp[which]);
																				
																				jiance_YJ(context,temp[which],textView13);
																			

																			}
																		})
																.show();

													} else {
														Toast.makeText(
																context,
																"暂无数据",
																Toast.LENGTH_SHORT)
																.show();
													}

													// 关闭数据库
													{
														cursor.close();
														sqLiteDatabase.close();
													}
												}
											});

								} else {
									Toast.makeText(context, "输入内容不能为空",
											Toast.LENGTH_LONG).show();
								}
							}
						}).show();
	}

	/**
	 * 新增同步上传数据库 -----以时间的范围的集合
	 */
	public void postDB_new_time(final Context context, final String tag_action,
			final String db_name, final String table_name,
			final String startime, final String endtime,
			final PostDBListener postDBListener) {

		if (endtime == null || startime == null || endtime.equals("")
				|| startime.equals("")) {
			Toast.makeText(context, "没有选到上传数据！", Toast.LENGTH_SHORT).show();

		} else {

			if (!Utils.NetIsConnect(context)) {

				Toast.makeText(context, "请检测网络设置", Toast.LENGTH_SHORT).show();
			} else {

				String sql = "select * from " + table_name
						+ " where CKISUPLOAD=1 and CKDATE between '" + startime
						+ "' and '" + endtime + "'";

				Log.e("kke", "sql = " + sql);

				selectDB(context, db_name, sql, null, new SelectDBListener() {
					@Override
					public void callback(SQLiteDatabase sqLiteDatabase,
							final Cursor cursor) {

						if (cursor.moveToFirst()) {

							final ArrayList<String> id = new ArrayList<String>();
							final ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

							for (; !cursor.isAfterLast(); cursor.moveToNext()) {

								id.add(cursor.getString(cursor
										.getColumnIndex("CKID")));

								HashMap<String, Object> temp_hashMap = new HashMap<String, Object>();

								temp_hashMap.put("action", "1");

								for (int i = 0; i < cursor.getColumnCount(); i++) {
									temp_hashMap.put(cursor.getColumnName(i)
											.toLowerCase(), cursor.getString(i));

								}

								arrayList.add(temp_hashMap);

							}

							// 关闭数据库
							{
								cursor.close();
								sqLiteDatabase.close();
							}

							if (id.size() > 0) {

								Gson gson = new Gson();

								SharedPreferences sp = PreferenceManager
										.getDefaultSharedPreferences(context);
								HashMap<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("id", sp.getString("id", ""));
								hashMap.put("list", gson.toJson(arrayList));

								JsonTask jsonTask = new JsonTask(context,
										Utils.url_joint_post, tag_action,
										new JsonTask.JsonCallBack() {

											@Override
											public void getJsonCallBack(
													int tag_switch,
													String str_json,
													boolean isPageLoad,
													int page,
													HashMap<String, Object> hashMap,
													ProgressDialog progressDialog) {

												try {
													JSONObject jsonObject = new JSONObject(
															str_json);

													if ("1".equals(jsonObject
															.getString("status"))) {

														/**
														 * 上传本地操作后，是ISDEL为1就删掉，
														 * CKISUPLOAD 改为0
														 */
														SQLiteDatabase sqLiteDatabase = getDB(
																context,
																db_name);
														sqLiteDatabase
																.beginTransaction();

														// 修改
														try {
															ContentValues contentValues = new ContentValues();
															contentValues
																	.put("CKISUPLOAD",
																			"0");
															if (endtime == null
																	|| startime == null
																	|| endtime
																			.equals("")
																	|| startime
																			.equals("")) {

															} else {

																sqLiteDatabase
																		.update(table_name,
																				contentValues,
																				"CKISUPLOAD=1 and CKDATE>=" +"\'"+startime+"\'" +" and CKDATE<=" 
																				+"\'"+endtime+"\'",
																						
																				null);

															}

															sqLiteDatabase
																	.setTransactionSuccessful();
														} catch (Exception e) {
															Log4Trace.show(e);
														} finally {
															sqLiteDatabase
																	.endTransaction();
															sqLiteDatabase
																	.close();
														}

														postDBListener
																.callback();

														Toast.makeText(
																context,
																"上传成功,本次上传了"
																		+ id.size()
																		+ "条数据！",
																Toast.LENGTH_SHORT)
																.show();
													}
												} catch (Exception e) {
													Log4Trace.show(e);
												}
											}

											@Override
											public void getError(int tag_switch) {

												postDBListener.callback();

												Toast.makeText(context,
														"上传失败" + id.size(),
														Toast.LENGTH_SHORT)
														.show();
											}

										}, 1);
								jsonTask.setStr_progress("正在上传数据，请稍候...");
								jsonTask.asyncJson(hashMap);

							}

						}

						else {
							postDBListener.callback();
							Toast.makeText(context, "这段时间范围无数据", Toast.LENGTH_SHORT)
									.show();
						}

					}
				});

			}

		}

	}

	/**
	 * 新增同步上传数据库 -----以ID的集合
	 */
	public void postDB_new(final Context context, final String tag_action,
			final String db_name, final String table_name,
			final String table_name_Id,
			final ArrayList<DetectInfoQuickCheckListData> datas,
			final PostDBListener postDBListener) {

		final ArrayList<String> array_id = new ArrayList<String>();
		for (DetectInfoQuickCheckListData data : datas) {
			if (data.isSelectId()) {

				array_id.add(data.getId());

			}

		}

		if (array_id.size() == 0 || table_name_Id == null
				|| table_name_Id.equals("")) {
			Toast.makeText(context, "没有选到上传数据！", Toast.LENGTH_SHORT).show();

		} else {

			if (!Utils.NetIsConnect(context)) {

				Toast.makeText(context, "请检测网络设置", Toast.LENGTH_SHORT).show();
			} else {

				String sql = "select * from " + table_name + " where ";
				final String[] idArray = new String[array_id.size()];
				for (int j = 0; j < array_id.size(); j++) {
					if (j > 0) {
						sql += " or ";
					}
					sql += table_name_Id + " = ? ";
					idArray[j] = array_id.get(j);
				}
				Log.e("kke", "sql = " + sql);

				selectDB(context, db_name, sql, idArray,
						new SelectDBListener() {
							@Override
							public void callback(SQLiteDatabase sqLiteDatabase,
									Cursor cursor) {

								if (cursor.moveToFirst()) {

									ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

									for (; !cursor.isAfterLast(); cursor
											.moveToNext()) {

										HashMap<String, Object> temp_hashMap = new HashMap<String, Object>();

										temp_hashMap.put("action", "1");

										for (int i = 0; i < cursor
												.getColumnCount(); i++) {
											temp_hashMap.put(cursor
													.getColumnName(i)
													.toLowerCase(), cursor
													.getString(i));
										}

										arrayList.add(temp_hashMap);

									}

									// 关闭数据库
									{
										cursor.close();
										sqLiteDatabase.close();
									}

									Gson gson = new Gson();

									SharedPreferences sp = PreferenceManager
											.getDefaultSharedPreferences(context);
									HashMap<String, Object> hashMap = new HashMap<String, Object>();
									hashMap.put("id", sp.getString("id", ""));
									hashMap.put("list", gson.toJson(arrayList));

									JsonTask jsonTask = new JsonTask(context,
											Utils.url_joint_post, tag_action,
											new JsonTask.JsonCallBack() {

												@Override
												public void getJsonCallBack(
														int tag_switch,
														String str_json,
														boolean isPageLoad,
														int page,
														HashMap<String, Object> hashMap,
														ProgressDialog progressDialog) {

													try {
														JSONObject jsonObject = new JSONObject(
																str_json);

														if ("1".equals(jsonObject
																.getString("status"))) {

															/**
															 * 上传本地操作后，
															 * 是ISDEL为1就删掉
															 * ，CKISUPLOAD 改为0
															 */
															SQLiteDatabase sqLiteDatabase = getDB(
																	context,
																	db_name);
															sqLiteDatabase
																	.beginTransaction();

															// 修改
															try {
																ContentValues contentValues = new ContentValues();
																contentValues
																		.put("CKISUPLOAD",
																				"0");
																if (array_id
																		.size() == 0
																		|| table_name_Id == null
																		|| table_name_Id
																				.equals("")) {

																} else {

																	for (int i = 0; i < idArray.length; i++) {

																		sqLiteDatabase
																				.update(table_name,
																						contentValues,
																						table_name_Id
																								+ "= ? ",
																						new String[] { idArray[i] });

																	}

																}

																sqLiteDatabase
																		.setTransactionSuccessful();
															} catch (Exception e) {
																Log4Trace
																		.show(e);
															} finally {
																sqLiteDatabase
																		.endTransaction();
																sqLiteDatabase
																		.close();
															}

															postDBListener
																	.callback();

															Toast.makeText(
																	context,
																	"上传成功,本次上传了"
																			+ idArray.length
																			+ "条数据！",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													} catch (Exception e) {
														Log4Trace.show(e);
													}
												}

												@Override
												public void getError(
														int tag_switch) {

													postDBListener.callback();

													Toast.makeText(context,
															"上传失败",
															Toast.LENGTH_SHORT)
															.show();
												}

											}, 1);
									jsonTask.setStr_progress("正在上传数据，请稍候...");
									jsonTask.asyncJson(hashMap);

								}

								else {
									postDBListener.callback();
									Toast.makeText(context, "上传成功",
											Toast.LENGTH_SHORT).show();
								}

							}
						});

			}

		}

	}

	/**
	 * 模糊查询数据表，返回选中内容到文本控件
	 */
	public void loadDBSelectDialog(final Context context,
			final TextView textView, final String name_db,
			final String name_table, final String name_column) {

		final EditText editText_02 = new EditText(context);
		editText_02.setHint("请输入搜索内容");

		new AlertDialog.Builder(context)
				.setView(editText_02)
				.setPositiveButton("模糊搜索",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String str = String.valueOf(
										editText_02.getText()).trim();
								if (!"".equals(str)) {

									// 默认查询
									new DBUtils().selectDB(context, name_db,
											"select * from " + name_table
													+ " where " + name_column
													+ " like ?",
											new String[] { "%" + str + "%" },
											new SelectDBListener() {
												@Override
												public void callback(
														SQLiteDatabase sqLiteDatabase,
														Cursor cursor) {

													if (cursor.moveToFirst()) {

														final String[] temp = new String[cursor
																.getCount()];

														for (; !cursor
																.isAfterLast(); cursor
																.moveToNext()) {
															temp[cursor
																	.getPosition()] = cursor
																	.getString(cursor
																			.getColumnIndex(name_column));
														}

														new AlertDialog.Builder(
																context)
																.setItems(
																		temp,
																		new DialogInterface.OnClickListener() {
																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {

																				textView.setText(temp[which]);

																			}
																		})
																.show();

													} else {
														Toast.makeText(
																context,
																"暂无数据",
																Toast.LENGTH_SHORT)
																.show();
													}

													// 关闭数据库
													{
														cursor.close();
														sqLiteDatabase.close();
													}
												}
											});

								} else {
									Toast.makeText(context, "输入内容不能为空",
											Toast.LENGTH_LONG).show();
								}
							}
						}).show();
	}

	/**
	 * 模糊查询数据表，返回选中内容到文本控件（包括权限限制）
	 */
	public void loadDBSelectDialogWithRole(final Context context,
			final TextView textView, final String name_db,
			final String name_table, final String name_column, final String key) {

		final EditText editText_02 = new EditText(context);
		editText_02.setHint("请输入搜索内容");

		final SharedPreferences sp;

		sp = PreferenceManager.getDefaultSharedPreferences(context);

		new AlertDialog.Builder(context)
				.setView(editText_02)
				.setPositiveButton("模糊搜索",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String str = String.valueOf(
										editText_02.getText()).trim();
								if (!"".equals(str)) {

									// 默认查询
									new DBUtils().selectDB(
											context,
											name_db,
											"select * from "
													+ name_table
													+ " where "
													+ name_column
													+ " like ? and "
													+ sp.getString("sql_oname",
															"").replaceAll(
															"ONAME", key),
											new String[] { "%" + str + "%" },
											new SelectDBListener() {
												@Override
												public void callback(
														SQLiteDatabase sqLiteDatabase,
														Cursor cursor) {

													if (cursor.moveToFirst()) {

														final String[] temp = new String[cursor
																.getCount()];

														for (; !cursor
																.isAfterLast(); cursor
																.moveToNext()) {
															temp[cursor
																	.getPosition()] = cursor
																	.getString(cursor
																			.getColumnIndex(name_column));
														}

														new AlertDialog.Builder(
																context)
																.setItems(
																		temp,
																		new DialogInterface.OnClickListener() {
																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {
																				textView.setText(temp[which]);
																			}
																		})
																.show();

													} else {
														Toast.makeText(
																context,
																"暂无数据",
																Toast.LENGTH_SHORT)
																.show();
													}

													/* 关闭数据库 */
													{
														cursor.close();
														sqLiteDatabase.close();
													}
												}
											});

								} else {
									Toast.makeText(context, "输入内容不能为空",
											Toast.LENGTH_LONG).show();
								}
							}
						}).show();
	}

	/**
	 * 检测sdcard里面对应的那些数据库文件
	 */
	public void checkDB(Context context, CheckDBListener checkDBListener) {
		new Task_check_db(context, checkDBListener).execute("");
	}

	/**
	 * 删除未上传以外的数据
	 */
	public void deleteDB(Context context, String db_name,
			DeleteDBListener deleteDBListener) {

		// 网络正常才删除本地数据
		if (Utils.NetIsConnect(context)) {

			// 得到该数据库的全部表名
			SQLiteDatabase sqLiteDatabase = getDB(context, db_name);
			Cursor cursor = sqLiteDatabase.rawQuery(
					"select name from sqlite_master where type='table'", null);

			/* 数据库执行事务 */
			sqLiteDatabase.beginTransaction();
			try {
				if (cursor.moveToFirst()) {
					for (; !cursor.isAfterLast(); cursor.moveToNext()) {

						if (!cursor.getString(cursor.getColumnIndex("name"))
								.equals("android_metadata")) {
							sqLiteDatabase.delete(cursor.getString(cursor
									.getColumnIndex("name")), "ISLOC is not ?",
									new String[] { "1" });
						}
					}
				}
				sqLiteDatabase.setTransactionSuccessful();
			} catch (Exception e) {
				Log4Trace.show(e);
			} finally {
				sqLiteDatabase.endTransaction();
				cursor.close();
				sqLiteDatabase.close();
			}

			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(context);
			Editor editor = sp.edit();
			editor.remove(db_name);
			editor.commit();
		}

		deleteDBListener.callback();
	}

	/**
	 * 获取对应的数据库
	 */
	public SQLiteDatabase getDB(Context context, String db_name) {
		SQLiteDatabase.loadLibs(context);
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(
				Utils.filePath_res_db + db_name, password, null);
		return sqLiteDatabase;
	}

	/**
	 * 异步查询数据库
	 */
	public void selectDB(Context context, String db_name, String select_sql,
			String[] array_replace, SelectDBListener selectDBListener) {
		new Task_select_db(context, db_name, select_sql, array_replace,
				selectDBListener).execute("");
	}

	/**
	 * 异步更新数据库
	 */
	public void updateDB(Context context, String db_name,
			ArrayList<SqlData> sqlDatas, String timestamp,
			UpdateDBListener updateDBListener) {
		new Task_update_db(context, db_name, sqlDatas, timestamp,
				updateDBListener).execute("");
	}

	/**
	 * 上传本地数据库操作
	 */
	public void postDB(final Context context, final String tag_action,
			final String db_name, final String table_name,
			final String where_id, final PostDBListener postDBListener) {

		String str_sql;
		if (where_id == null || where_id.trim().equals("")) {
			str_sql = "select * from " + table_name + " where ISLOC='1'";
		} else {
			str_sql = "select * from " + table_name + " where ISLOC='1' and "
					+ where_id;
		}

		selectDB(context, db_name, str_sql, null, new SelectDBListener() {
			@Override
			public void callback(SQLiteDatabase sqLiteDatabase, Cursor cursor) {

				if (!Utils.NetIsConnect(context)) {

					Toast.makeText(context, "请检测网络设置", Toast.LENGTH_SHORT)
							.show();
				} else {

					if (cursor.moveToFirst()) {

						Gson gson = new Gson();

						SharedPreferences sp = PreferenceManager
								.getDefaultSharedPreferences(context);

						ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

						for (; !cursor.isAfterLast(); cursor.moveToNext()) {

							HashMap<String, Object> temp_hashMap = new HashMap<String, Object>();

							temp_hashMap.put("action", "1");

							for (int i = 0; i < cursor.getColumnCount(); i++) {
								temp_hashMap.put(cursor.getColumnName(i)
										.toLowerCase(), cursor.getString(i));
							}

							arrayList.add(temp_hashMap);
						}

						/* 关闭数据库 */
						{
							cursor.close();
							sqLiteDatabase.close();
						}

						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("id", sp.getString("id", ""));
						hashMap.put("list", gson.toJson(arrayList));

						JsonTask jsonTask = new JsonTask(context,
								Utils.url_joint_post, tag_action,
								new JsonTask.JsonCallBack() {

									@Override
									public void getJsonCallBack(int tag_switch,
											String str_json,
											boolean isPageLoad, int page,
											HashMap<String, Object> hashMap,
											ProgressDialog progressDialog) {

										try {
											JSONObject jsonObject = new JSONObject(
													str_json);

											if ("1".equals(jsonObject
													.getString("status"))) {

												/**
												 * 上传本地操作后，是ISDEL为1就删掉， ISLOC改为0
												 */
												SQLiteDatabase sqLiteDatabase = getDB(
														context, db_name);
												sqLiteDatabase
														.beginTransaction();

												// 修改
												try {
													ContentValues contentValues = new ContentValues();
													contentValues.put("ISLOC",
															"0");

													if (where_id == null
															|| where_id.trim()
																	.equals("")) {
														sqLiteDatabase
																.update(table_name,
																		contentValues,
																		"ISLOC=?",
																		new String[] { "1" });
													} else {
														sqLiteDatabase
																.update(table_name,
																		contentValues,
																		"ISLOC=? and "
																				+ where_id,
																		new String[] { "1" });
													}

													sqLiteDatabase
															.setTransactionSuccessful();
												} catch (Exception e) {
													Log4Trace.show(e);
												} finally {
													sqLiteDatabase
															.endTransaction();
													sqLiteDatabase.close();
												}

												postDBListener.callback();

												Toast.makeText(context, "上传成功",
														Toast.LENGTH_SHORT)
														.show();
											}
										} catch (Exception e) {
											Log4Trace.show(e);
										}
									}

									@Override
									public void getError(int tag_switch) {

										postDBListener.callback();

										Toast.makeText(context, "上传失败",
												Toast.LENGTH_SHORT).show();
									}

								}, 1);
						jsonTask.setStr_progress("正在上传数据，请稍候...");
						jsonTask.asyncJson(hashMap);
					} else {
						postDBListener.callback();
						Toast.makeText(context, "上传失败，无数据！", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}

	/**
	 * 异步从assets复制到sdcard还原数据库文件（不存在的才复制过去）
	 */
	private class Task_check_db extends AsyncTask<String, Integer, String> {

		private Task_check_db(Context context, CheckDBListener checkDBListener) {
			this.context = context;
			this.checkDBListener = checkDBListener;
		}

		private Context context;
		private CheckDBListener checkDBListener;
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(context, null, "初始化数据，请稍候",
					true, false);
		}

		@Override
		protected String doInBackground(String... arg0) {

			new Utils().createDir(Utils.filePath_res_db);

			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(context);
			Editor editor = sp.edit();

			// “用户”模块
			if (!new File(Utils.filePath_res_db + db_user).exists()) {
				Utils.copyAssetsToSdcard(context, db_user,
						Utils.filePath_res_db + db_user);
				editor.remove(db_user);
			}

			// “监管对象”模块
			if (!new File(Utils.filePath_res_db + db_check_org).exists()) {
				Utils.copyAssetsToSdcard(context, db_check_org,
						Utils.filePath_res_db + db_check_org);
				editor.remove(db_check_org);
			}

			// “食品”模块
			if (!new File(Utils.filePath_res_db + db_food).exists()) {
				Utils.copyAssetsToSdcard(context, db_food,
						Utils.filePath_res_db + db_food);
				editor.remove(db_food);
			}

			// “检测项目”模块
			if (!new File(Utils.filePath_res_db + db_detect_project).exists()) {
				Utils.copyAssetsToSdcard(context, db_detect_project,
						Utils.filePath_res_db + db_detect_project);
				editor.remove(db_detect_project);
			}

			// “检测数据”模块
			if (!new File(Utils.filePath_res_db + db_detect_info).exists()) {
				Utils.copyAssetsToSdcard(context, db_detect_info,
						Utils.filePath_res_db + db_detect_info);
				editor.remove(db_detect_info);
			}

			// “动态等级”模块
			if (!new File(Utils.filePath_res_db + db_rating).exists()) {
				Utils.copyAssetsToSdcard(context, db_rating,
						Utils.filePath_res_db + db_rating);
				editor.remove(db_rating);
			}

			// “健康档案”模块
			if (!new File(Utils.filePath_res_db + db_health).exists()) {
				Utils.copyAssetsToSdcard(context, db_health,
						Utils.filePath_res_db + db_health);
				editor.remove(db_health);
			}

			// “政策”模块
			if (!new File(Utils.filePath_res_db + db_policy).exists()) {
				Utils.copyAssetsToSdcard(context, db_policy,
						Utils.filePath_res_db + db_policy);
				editor.remove(db_policy);
			}

			// “送检”模块
			if (!new File(Utils.filePath_res_db + db_consign).exists()) {
				Utils.copyAssetsToSdcard(context, db_consign,
						Utils.filePath_res_db + db_consign);
				editor.remove(db_consign);
			}

			// “工作”模块
			if (!new File(Utils.filePath_res_db + db_work).exists()) {
				Utils.copyAssetsToSdcard(context, db_work,
						Utils.filePath_res_db + db_work);
				editor.remove(db_work);
			}

			// “消息”模块
			if (!new File(Utils.filePath_res_db + db_msg).exists()) {
				Utils.copyAssetsToSdcard(context, db_msg, Utils.filePath_res_db
						+ db_msg);
				editor.remove(db_msg);
			}

			// “照相取证”模块
			if (!new File(Utils.filePath_res_db + db_oe).exists()) {
				Utils.copyAssetsToSdcard(context, db_oe, Utils.filePath_res_db
						+ db_oe);
				editor.remove(db_oe);
			}

			editor.commit();

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			progressDialog.dismiss();

			if (checkDBListener != null) {
				checkDBListener.callback();
			}
		}
	}

	/**
	 * 异步更新数据库
	 */
	private class Task_update_db extends AsyncTask<String, Integer, String> {

		private Task_update_db(Context context, String db_name,
				ArrayList<SqlData> sqlDatas, String timestamp,
				UpdateDBListener updateDBListener) {
			this.context = context;
			this.db_name = db_name;
			this.sqlDatas = sqlDatas;
			this.timestamp = timestamp;
			this.updateDBListener = updateDBListener;
		}

		private Context context;
		private String db_name = "";
		private ArrayList<SqlData> sqlDatas;
		private UpdateDBListener updateDBListener;
		// private ProgressDialog progressDialog;
		private SQLiteDatabase sqLiteDatabase;
		private boolean isSucceed = true;
		private String timestamp;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressDialog = ProgressDialog.show(context, null, "更新数据，请稍候",
			// true, false);
		}

		@Override
		protected String doInBackground(String... arg0) {

			// 判断是否要更新
			if (sqlDatas != null && sqlDatas.size() > 0) {

				sqLiteDatabase = getDB(context, db_name);

				/* 数据库执行事务 */
				sqLiteDatabase.beginTransaction();
				try {

					for (SqlData data : sqlDatas) {

						String primary_key = data.getKey_value_list().get(0)
								.getKey();
						Log4Trace.e("primary:"+primary_key);
						String primary_value = data.getKey_value_list().get(0)
								.getValue();
						String where = primary_key + "=?";
						String[] array_where = new String[] { primary_value };

						ContentValues contentValues = new ContentValues();
						for (KeyValueData keyValueData : data
								.getKey_value_list()) {
							contentValues.put(keyValueData.getKey(),
									keyValueData.getValue());
						}
						
						addKeyDefault(contentValues,data.getTable_name());
						

						// 不管本地有没有，直接先插入数据，报错才变回正规
						if (sqLiteDatabase.insert(data.getTable_name(), null,
								contentValues) == -1L) {
							isSucceed = false;
						}
					}

					sqLiteDatabase.setTransactionSuccessful();

				} catch (Exception e) {
					isSucceed = false;

					Log4Trace.show(e);

				} finally {

					sqLiteDatabase.endTransaction();
				}

				// 不成功才变回正规
				if (!isSucceed) {

					Log4Trace.show("不成功，变回正规");

					sqLiteDatabase.beginTransaction();
					for (SqlData data : sqlDatas) {
						String primary_key = data.getKey_value_list().get(0)
								.getKey();
						String primary_value = data.getKey_value_list().get(0)
								.getValue();
						String where = primary_key + "=?";
						String[] array_where = new String[] { primary_value };

						ContentValues contentValues = new ContentValues();
						for (KeyValueData keyValueData : data
								.getKey_value_list()) {
							contentValues.put(keyValueData.getKey(),
									keyValueData.getValue());
						}
						
						addKeyDefault(contentValues,data.getTable_name());

						sqLiteDatabase.delete(data.getTable_name(), where,
								array_where);
						sqLiteDatabase.insert(data.getTable_name(), null,
								contentValues);
					}
					sqLiteDatabase.setTransactionSuccessful();
					sqLiteDatabase.endTransaction();
					isSucceed = true;
				}

			} else {
				isSucceed = false;
				Log4Trace.show("更新: " + db_name);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// progressDialog.dismiss();
			updateDBListener.callback();// 处理完更新回调此方法

			if (sqLiteDatabase != null) {
				if (sqLiteDatabase.isOpen()) {
					sqLiteDatabase.close(); // 关闭数据库
				}
			}

			if (isSucceed) {

				// 缓存时间戳（根据数据库的文件名）
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(context);
				Editor editor = sp.edit();
				editor.putString(db_name, timestamp);
				editor.commit();

				Log4Trace.show("更新成功: " + db_name);
			}
		}
	}

	private ContentValues addKeyDefault(ContentValues values,String table_name){
		if (table_name.equals("tb_base_checkitme")) {
			values.put("YES",1);
		}
		return values;
	}
	
	
	
	/**
	 * 异步查询数据库
	 */
	private class Task_select_db extends AsyncTask<String, Integer, String> {

		private Task_select_db(Context context, String db_name,
				String select_sql, String[] array_replace,
				SelectDBListener selectDBListener) {
			this.context = context;
			this.db_name = db_name;
			this.select_sql = select_sql;
			this.array_replace = array_replace;
			this.selectDBListener = selectDBListener;
		}

		private Context context;
		private String db_name;
		private String select_sql = "";
		private String[] array_replace;
		private SelectDBListener selectDBListener;
		// private ProgressDialog progressDialog;
		private SQLiteDatabase sqLiteDatabase;
		private Cursor cursor;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressDialog = ProgressDialog.show(context, null, "读取数据，请稍候",
			// true, false);
		}

		@Override
		protected String doInBackground(String... arg0) {

			sqLiteDatabase = getDB(context, db_name);
            Log.e("db_name",db_name);
            Log.e("select_sql",select_sql);
			cursor = sqLiteDatabase.rawQuery(select_sql, array_replace);
          
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// if (progressDialog != null && progressDialog.isShowing()) {
			// try {
			// progressDialog.dismiss();
			// } catch (Exception e) {
			// Log4Trace.show(e);
			// }
			// }

			selectDBListener.callback(sqLiteDatabase, cursor);// 回调查询结果
		}
	}
}
