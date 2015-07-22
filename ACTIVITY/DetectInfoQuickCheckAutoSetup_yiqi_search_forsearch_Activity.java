package com.gzmob.dayuan.activity;

import java.util.ArrayList;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




import com.gzmob.dayuan.R;
import com.gzmob.model.DetectProjectSearchListData;
import com.gzmob.utils.DBUtils;
import com.gzmob.utils.ListViewWithMore;
import com.gzmob.utils.Utils;
import com.gzmob.utils.DBUtils.SelectDBListener;
import com.gzmob.utils.ListViewWithMore.FootViewListener;

public class DetectInfoQuickCheckAutoSetup_yiqi_search_forsearch_Activity extends Activity implements
OnClickListener{
Context context=this;

ListViewWithMore listViewWithMore;
EditText edit_search_key;
TextView textview_count_result;

ArrayList<DetectProjectSearchListData> detectProjectSearchListDatas = new ArrayList<DetectProjectSearchListData>();
DetectProjectSearchListAdapter adapter = new DetectProjectSearchListAdapter(
		context, detectProjectSearchListDatas);

String str_search_key = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yiqi_setup_search_list);
		
		listViewWithMore = (ListViewWithMore) findViewById(R.id.listViewWithMore);
		edit_search_key = (EditText) findViewById(R.id.edit_search_key);
		textview_count_result = (TextView) findViewById(R.id.textview_count_result);
	
	  
	
	
	
	}
	
	public class DetectProjectSearchListAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<DetectProjectSearchListData> datas;

		public DetectProjectSearchListAdapter(Context context,
				ArrayList<DetectProjectSearchListData> datas) {
			this.context = context;
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position,  View view, ViewGroup parent) {

			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_food_search_list, null);
			}

			TextView name = (TextView) view.findViewById(R.id.name);

			name.setText(datas.get(position).getName());
			
		
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					
					
					
					String correlation_dname= datas.get(position).getName();
					{SQLiteDatabase sqLiteDatabase;
					sqLiteDatabase = new DBUtils().getDB(context,
							DBUtils.db_detect_info);
				
					ContentValues contentValues = new ContentValues();
					contentValues.put("CNAME",correlation_dname );
					//contentValues.put("YQDNAME",correlation_dname );
					contentValues.put("YES", 1); //项目名称是否对应,这里不管对不对应只要CNAME有值就给1
			/*		if(Id.equals("1")){
					contentValues.put("YQKIND", "DY3300");
					}else if(Id.equals("2")){
						contentValues.put("YQKIND", "DY6600");
					}
					contentValues.put("YQKINDKIND", "金属检测");*/
					
				
			
			
	            	contentValues.put("UDATE", new Utils().getDate_sqlite());//更新时间
				
	            	sqLiteDatabase.update("tb_base_checkitme",
							contentValues, "CID=?",
							new String[] { getIntent().getStringExtra("Id")
									 });
	            	
				
				
				
					sqLiteDatabase.close();
					}
					{
						Intent intent = new Intent();
						intent.setAction("update_list_setup");
						intent.putExtra("isupdate", true);
						sendBroadcast(intent);
						
					}
					
                finish();
					
					
					
				
				}
			});

			return view;
		}

	}

	
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// 左上角
		case R.id.img_back:
			finish();
			break;
		case R.id.button_search:
			str_search_key = String.valueOf(edit_search_key.getText()).trim();

			if (str_search_key.equals("")) {
				Toast.makeText(context, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
			} else {

				detectProjectSearchListDatas
						.removeAll(detectProjectSearchListDatas);

				// 默认查询
				new DBUtils()
						.selectDB(
								context,
								DBUtils.db_detect_project,
								"select * from tb_base_detect_item_detail where DNAME like ?",
								new String[] { "%" + str_search_key + "%" },
								new SelectDBListener() {
									@Override
									public void callback(
											SQLiteDatabase sqLiteDatabase,
											Cursor cursor) {

										textview_count_result
												.setVisibility(View.VISIBLE);
										textview_count_result.setText("关于“"
												+ str_search_key + "”共搜索到"
												+ cursor.getCount() + "个结果");

										if (cursor.moveToFirst()) {

											for (; !cursor.isAfterLast(); cursor
													.moveToNext()) {

												if (String
														.valueOf(
																cursor.getPosition())
														.equals(DBUtils.page_size)) {
													break;
												}

												detectProjectSearchListDatas
														.add(new DetectProjectSearchListData(
																cursor));
											}

											if (detectProjectSearchListDatas
													.size() >= Integer
													.valueOf(DBUtils.page_size)) {
												listViewWithMore.showFootView();
											}

											listViewWithMore
													.setAdapter(adapter);

										} else {

											listViewWithMore.removeFootView();
											listViewWithMore
													.setAdapter(adapter);
											adapter.notifyDataSetChanged();

											Toast.makeText(context, "暂无数据",
													Toast.LENGTH_SHORT).show();
										}

										/* 关闭数据库 */
										{
											cursor.close();
											sqLiteDatabase.close();
										}
									}
								});

				// 点击“更多”
				listViewWithMore.setFootViewListener(new FootViewListener() {
					@Override
					public void onClick() {
						new DBUtils().selectDB(context,
								DBUtils.db_detect_project,
								"select * from tb_base_detect_item_detail where DNAME like ? limit "
										+ DBUtils.page_size + " offset "
										+ detectProjectSearchListDatas.size(),
								new String[] { "%" + str_search_key + "%" },
								new SelectDBListener() {
									@Override
									public void callback(
											SQLiteDatabase sqLiteDatabase,
											Cursor cursor) {

										if (cursor.moveToFirst()) {
											for (; !cursor.isAfterLast(); cursor
													.moveToNext()) {
												detectProjectSearchListDatas
														.add(new DetectProjectSearchListData(
																cursor));
											}

											adapter.notifyDataSetChanged();

										} else {
											Toast.makeText(context, "没有更多",
													Toast.LENGTH_SHORT).show();
											listViewWithMore.removeFootView();
										}

										/* 关闭数据库 */
										{
											cursor.close();
											sqLiteDatabase.close();
										}
									}
								});
					}
				});
			}

			
			
			break;
		default:
			break;
		}
		
	}

}
