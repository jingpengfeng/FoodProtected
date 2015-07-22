package com.gzmob.dayuan.activity;




import java.util.ArrayList;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gzmob.adapter.yiqi_collect_Adapater;
import com.gzmob.dayuan.R;
import com.gzmob.model.DetectInfoQuickCheckListData;
import com.gzmob.utils.DBUtils;
import com.gzmob.utils.ListViewWithMore;
import com.gzmob.utils.DBUtils.SelectDBListener;
import com.gzmob.utils.ListViewWithMore.FootViewListener;


public class DetectInfoQuickCheckAutoCollect_yiqi_caijiActivity extends Activity implements OnClickListener{
	Context context = this;
	
	 TextView titileText;
	ListViewWithMore   listViewWithMore;
	RelativeLayout search_count;
	TextView count;
	 String startTime;
	 String endTime;
	String kind;
	TextView line;
	TextView t;
	int Id;
	 ArrayList<DetectInfoQuickCheckListData> detectInfoQuickCheckListDatas = new ArrayList<DetectInfoQuickCheckListData>();
	
	yiqi_collect_Adapater adapter; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yiqi_collect);
		titileText=(TextView)findViewById(R.id.textview_title);
		 count=(TextView )findViewById(R.id.textview_count);
		listViewWithMore=(ListViewWithMore)findViewById(R.id.listView_yiqi_collect);
		
	 search_count=(RelativeLayout)findViewById(R.id.search_count);
		 line=(TextView)findViewById(R.id.textview_line);
		
		Bundle b=getIntent().getExtras();//得到传来的时间和仪器类型
		   Id=b.getInt("yiqi_id");
		 startTime =b.getString("startTime");
		endTime =b.getString("endTime");
		
		adapter=new yiqi_collect_Adapater(  //仪器类型传给适配器
				context,detectInfoQuickCheckListDatas,Id);
		
		 if(Id==1){
			 titileText.setText("DY-6600手持式现场执法快检通自动采集");
			 kind=b.getString("kind");
			 loadOrderData_DY6600(startTime,endTime,kind);  //dy6600的加载
			
		
						 
		 }else if(Id==0){
			 titileText.setText("DY-3300食品综合分析仪自动采集");
			 
			 //dy3300加载方法
			 
			 loadOrderData_DY3300(startTime,endTime);  
		 }
	
	
		
		
		 }
	


	/**
	 * 重新加载
	 */
	public void loadOrderData_DY6600(final String start,final String end,final String kind) {//时间用处取消了

		detectInfoQuickCheckListDatas.removeAll(detectInfoQuickCheckListDatas);

	

		// 默认查询
		new DBUtils().selectDB(
				
			context,    
				DBUtils.db_detect_info,
				"select * from tb_base_check_checkrec where CKISUPLOAD=2"+" and CKKINDNAME=\""+kind+"\""
					    + " order by CKDATE desc"
						+ " limit " + DBUtils.page_size + " offset "
						+ detectInfoQuickCheckListDatas.size(), null,
				new SelectDBListener() {
					@Override
					public void callback(SQLiteDatabase sqLiteDatabase,
							Cursor cursor) {

						if (cursor.moveToFirst()) {
							for (; !cursor.isAfterLast(); cursor.moveToNext()) {
								detectInfoQuickCheckListDatas
										.add(new DetectInfoQuickCheckListData(
												cursor));
							}
							

							if (detectInfoQuickCheckListDatas.size() >= Integer
									.valueOf(DBUtils.page_size)) {
								
								listViewWithMore.showFootView();
							}

							listViewWithMore.setAdapter(adapter);
							 int count1 = detectInfoQuickCheckListDatas.size();
								 count.setText(""+count1);

						} else {
							Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT)
									.show();
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
				new DBUtils().selectDB(
						context,
						DBUtils.db_detect_info,
						"select * from tb_base_check_checkrec where CKISUPLOAD=2"
						
						+" and CKKINDNAME=\""+kind+"\""
					    + " order by CKDATE desc"
						+ " limit " + DBUtils.page_size + " offset "
						+ detectInfoQuickCheckListDatas.size(), null,
						new SelectDBListener() {
							@Override
							public void callback(SQLiteDatabase sqLiteDatabase,
									Cursor cursor) {

								if (cursor.moveToFirst()) {
									for (; !cursor.isAfterLast(); cursor
											.moveToNext()) {
										detectInfoQuickCheckListDatas
												.add(new DetectInfoQuickCheckListData(
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

	/**
	 * 重新加载
	 */
	public void loadOrderData_DY3300(final String start,final String end) {

		detectInfoQuickCheckListDatas.removeAll(detectInfoQuickCheckListDatas);

	

		// 默认查询
		new DBUtils().selectDB(
				context,    
				DBUtils.db_detect_info,
				"select * from tb_base_check_checkrec where CKISUPLOAD=3"
				
						
					    + " order by CKDATE desc"
						+ " limit " + DBUtils.page_size + " offset "
						+ detectInfoQuickCheckListDatas.size(), null,
				new SelectDBListener() {
					@Override
					public void callback(SQLiteDatabase sqLiteDatabase,
							Cursor cursor) {

						if (cursor.moveToFirst()) {
							for (; !cursor.isAfterLast(); cursor.moveToNext()) {
								detectInfoQuickCheckListDatas
										.add(new DetectInfoQuickCheckListData(
												cursor));
							}
							

							if (detectInfoQuickCheckListDatas.size() >= Integer
									.valueOf(DBUtils.page_size)) {
								
								listViewWithMore.showFootView();
							}

							listViewWithMore.setAdapter(adapter);
							 int count1 = detectInfoQuickCheckListDatas.size();
								 count.setText(""+count1);

						} else {
							Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT)
									.show();
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
				new DBUtils().selectDB(
						context,
						DBUtils.db_detect_info,
						"select * from tb_base_check_checkrec where CKISUPLOAD=3"
						
						
					    + " order by CKDATE desc"
						+ " limit " + DBUtils.page_size + " offset "
						+ detectInfoQuickCheckListDatas.size(), null,
						new SelectDBListener() {
							@Override
							public void callback(SQLiteDatabase sqLiteDatabase,
									Cursor cursor) {

								if (cursor.moveToFirst()) {
									for (; !cursor.isAfterLast(); cursor
											.moveToNext()) {
										detectInfoQuickCheckListDatas
												.add(new DetectInfoQuickCheckListData(
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
	
	
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		

		
		case R.id.img_back://退出这页面删除采集到的这些数据以避免重复采集
			
		SQLiteDatabase sqLiteDatabase;
			sqLiteDatabase = new DBUtils().getDB(context,
					DBUtils.db_detect_info);

			// 数据库执行事务 
			sqLiteDatabase.beginTransaction();

		
			try {
				
			sqLiteDatabase.delete("tb_base_check_checkrec",
								"CKISUPLOAD='2' or CKISUPLOAD='3'", null);
					
					
				
				sqLiteDatabase.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sqLiteDatabase.endTransaction();
			}

			sqLiteDatabase.close();
			
			finish();
		break;
		case R.id.textview_setup:
	
			Intent intent1 =new Intent();
			intent1.setClass(context, DetectInfoQuickCheckAutoSetup_yiqi_kind_Activity.class);
			Bundle b=new Bundle();
			if(Id==1){
			b.putInt("id", 2);
			}else if(Id==0){
				b.putInt("id", 1);
			}
			intent1.putExtras(b);
        startActivity(intent1);
			break;	
			 
		case R.id.collect_update://重新读取按钮
		
			
			if(Id==1){
				adapter.notifyDataSetChanged();

				 loadOrderData_DY6600(startTime,endTime,kind);  //dy6600的加载
				Toast.makeText(context, "重新读取完毕", Toast.LENGTH_SHORT)
					.show();
							 
			 }else if(Id==0){
					adapter.notifyDataSetChanged();
				 loadOrderData_DY3300(startTime,endTime);  //dy3300的加载
					Toast.makeText(context, "重新读取完毕", Toast.LENGTH_SHORT)
						.show();
			
			 }
		
			 
	
			
			break;
		
		default:
			break;
		}
		
	}
	
	
	
	
	


	
	
	
}
