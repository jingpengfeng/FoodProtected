/*
 * 时间选择页面
 * 
 */


package com.gzmob.dayuan.activity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



import java.util.Date;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gzmob.adapter.CustomProgressDialog;
import com.gzmob.dayuan.R;
import com.gzmob.utils.DBUtils;
import com.gzmob.utils.DateRange;

import com.gzmob.utils.DateRange.DateTimeListener;
import com.gzmob.utils.Utils;
import com.gzmob.write.middel.CmdEntity;
import com.gzmob.write.middel.DySerialManager;
import com.gzmob.write.middel.LogEntity;
import com.gzmob.write.middel.DySerialManager.UpdateReceivedDataCallback;
import com.gzmob.write.middel.ProItems;
import com.hoho.android.usbserial.driver.UsbSerialPort;


//此类采集仪器的数据并保存到数据先（之后会删除）
@SuppressLint("HandlerLeak")
public class DetectInfoQuickCheckAutoCollect_yiqiActivity<object> extends Activity implements OnClickListener,UpdateReceivedDataCallback {
	private final String TAG = DetectInfoQuickCheckAutoCollect_yiqi_caijiActivity.class.getSimpleName();	
	Context context = this;
private	TextView	startText ;
private	TextView	endText ;     
private 	TextView	titleText;
private TextView spn1;
	private int month,date,year;
	private int month1,date1,year1;
	private static int Id;
	private Spinner spinner;
	private View view;
	private ArrayAdapter<String>  adapter;
	 private static UsbSerialPort port1;
	
	private	 ArrayList<LogEntity> list;
	private	 Handler mHandler ;
	private	 boolean kind=true;
	 private   CustomProgressDialog progressDialog=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qiyi);
		      
			startText = (TextView) findViewById(R.id.textview_startTime);
			endText = (TextView) findViewById(R.id.textview_endTime);
			titleText=(TextView) findViewById(R.id.textview_title);
			spinner = (Spinner) findViewById(R.id.Spinner01); 
			spn1 = (TextView) findViewById(R.id.spn_textview);
		    view=findViewById(R.id.spnlayout);
			
			 
		   
			
			if(Id==1){
				spinner.setVisibility(0);
				titleText.setText("DY-6600手持式现场执法快检通");
				}
				if(Id==0){
					spn1.setVisibility(0);
					titleText.setText("DY-3300智能多工能食品分析仪");
					view.setVisibility(View.GONE);
				}
			

		       
		        //将可选内容与ArrayAdapter连接起来
		        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,Utils.array_instrument_dy6600);
		         
		       //设置下拉列表的风格
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		         
		        //将adapter 添加到spinner中
		        spinner.setAdapter(adapter);
		        //设置默认值
		        spinner.setSelection(0,true); 
		        
		       
				
		         
		        //添加事件Spinner事件监听  
		        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						
						
					}
				});
		         
		     
			 
			 
			 
			
			
	}
	
    public static void show(Context context, UsbSerialPort port,int id) {
    	DySerialManager.sPort = port;
    	 Id=id;
    	  port1=port;
        final Intent intent = new Intent(context, DetectInfoQuickCheckAutoCollect_yiqiActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
	
	

	
	//点击监听
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.img_back:
			finish();
		break;
		case R.id.textview_setup:
			
		
			break;
		case R.id.textview_endTime:
			
			new DateRange().getDateTime(context, new DateTimeListener() {
				@Override
				public void callback(String[] array_date, String[] array_time) {
					year1=Integer.parseInt(array_date[0]);
					 month1=Integer.parseInt(array_date[1]);
					 date1=Integer.parseInt(array_date[2]);
						if(year<Integer.parseInt(array_date[0])||(year==Integer.parseInt(array_date[0])&&month<Integer.parseInt(array_date[1]))||(year==Integer.parseInt(array_date[0])&&month==Integer.parseInt(array_date[1])&&date<=Integer.parseInt(array_date[2]))){
							
							endText.setText(array_date[0] + "-" + array_date[1]
									+ "-" + array_date[2] + " " + array_time[0] + ":"
									+ array_time[1] + ":" + array_time[2]);
							
						
						}else{
							Toast.makeText(context, "结束时间不能小于开始时间", Toast.LENGTH_SHORT).show();
							endText.setText("请选择结束时间");
						}
				}
			});
			
			
			
			
			
		break;
		case R.id.textview_startTime:
			
			new DateRange().getDateTime(context, new DateTimeListener() {
				@Override
				public void callback(String[] array_date, String[] array_time) {
					
					
					year=Integer.parseInt(array_date[0]);
					 month=Integer.parseInt(array_date[1]);
					 date=Integer.parseInt(array_date[2]);
					 if(endText.getText().equals("请选择结束时间")||year1>Integer.parseInt(array_date[0])||(year1==Integer.parseInt(array_date[0])&&month1>Integer.parseInt(array_date[1]))||(year1==Integer.parseInt(array_date[0])&&month1==Integer.parseInt(array_date[1])&&date1>=Integer.parseInt(array_date[2]))){
						startText.setText(array_date[0] + "-" + array_date[1]
									+ "-" + array_date[2] + " " + array_time[0] + ":"
									+ array_time[1] + ":" + array_time[2]);
						
							
							
						}else{
							Toast.makeText(context, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
							startText.setText("请选择开始时间");
						}
				
				}
			});
			
			
				
			break;
		case R.id.button_yiqi: //确定按钮--读数据
			
			
			if(startText.getText().equals("请选择开始时间")||endText.getText().equals("请选择结束时间")){
				
				Toast.makeText(context, "时间选择不能为空！", Toast.LENGTH_SHORT).show();
				
			}else{
				
            	
		        load();
				
			}
			
			break;	
			
		default:
			break;
		}
	
	}
	
	public void load(){
		
		// 监听刷新的消息发第二次指令
		  mHandler = new Handler() {
				@Override
		        public void handleMessage(Message msg) {
		            switch (msg.what) {
		                case 1: //43
		                	
		                	DySerialManager.sendCmd(CmdEntity.sendReadNonPaperProjectCmd());
		                	
		                    break;
		                case 2: //63
		                	
		                	DySerialManager.sendCmd(CmdEntity.sendReadGoldCardProjectCmd()); 
		                	break;
		                case 3://73
		                
		                	DySerialManager.sendCmd(CmdEntity.sendReadDryChemicalProjectCmd());
		                	break;
		                case 4:	 //跳转
		                
		                	dismissProgressDialog();
		                
		                	Intent intent= new Intent(context,DetectInfoQuickCheckAutoCollect_yiqi_caijiActivity.class);
		    				Bundle	 bundle = new Bundle();
		    				if(Id==1){
		    			
		    					bundle.putInt("yiqi_id",1);
		    					bundle.putString("kind",spinner.getSelectedItem().toString());
		    				}else if(Id==0){
		    					bundle.putInt("yiqi_id",0);
		    				}
		    			        bundle.putString("startTime",startText.getText().toString().trim() );
		    			        bundle.putString("endTime",endText.getText().toString().trim() );
		    			      
		    					intent.putExtras(bundle); 
		    					startActivityForResult(intent, 1);
		                	
		    					finish();
		                	
		                break;	
		                default:
		                    super.handleMessage(msg);
		                    break;
		            }
		        }
			};
			
		 
			if(Id==0){ //3300仪器
				
				//Toast.makeText(context,"3300" , Toast.LENGTH_SHORT).show(); 
				showProgressDialog() ;
				DySerialManager.sendCmd(CmdEntity.sendReadNonPaperDeviceCmd(startText.getText().toString(),
						 endText.getText().toString()));
						
				// DySerialManager.sendCmd(CmdEntity.sendReadNonPaperDeviceCmd("2001-01-02 01:22:32", "2014-12-02 21:32:52"));
			
			
			
			}else if(Id==1){//6600仪器
				
				
				
			     String sp=spinner.getSelectedItem().toString();
				 if(sp.equals("非试纸法")){
						 
						
					//  DySerialManager.sendCmd(CmdEntity.sendReadNonPaperDeviceCmd("2001-01-02 01:22:32", "2014-12-02 21:32:52"));
								DySerialManager.sendCmd(CmdEntity.sendReadNonPaperDeviceCmd(startText.getText().toString(),
								 endText.getText().toString()));
								
							
								
							
							 }else if(sp.equals("金标卡法")){
								
								DySerialManager.sendCmd(CmdEntity.sendReadGoldCardDeviceCmd(startText.getText().toString(),
										 endText.getText().toString()));
					         
								 
				// DySerialManager.sendCmd(CmdEntity.sendReadGoldCardDeviceCmd("2002-01-02 01:22:32", "2015-12-02 11:32:12"));
								 
								 
							 }else if(sp.equals("干化学法")){
									
								 
				          DySerialManager.sendCmd(CmdEntity.sendReadDryChemicalDeviceCmd(startText.getText().toString(),
						 endText.getText().toString()));
				       
				     //  	DySerialManager.sendCmd(CmdEntity.sendReadDryChemicalDeviceCmd("2007-02-02 17:23:12", "2015-12-22 01:32:23")); 
				  
				        
							 }
		
			}
		

}

	
	
	@Override
	protected void onPause() {
	    super.onPause();
	    DySerialManager.stopIoManager();
	    if (DySerialManager.sPort != null) {
	        try {
	        	DySerialManager.sPort.close();
	        } catch (IOException e) {
	            // Ignore.
	        }
	        DySerialManager.sPort = null;
	    }
	    finish();
	}

	@Override
	protected void onResume() {//打开与仪器通讯的接口
	
	    super.onResume();
	    Log.d(TAG, "Resumed, port=" + DySerialManager.sPort);
	    DySerialManager.OpenDevices(this, 9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
	    onDeviceStateChange();
	}

	private void onDeviceStateChange() {
		DySerialManager.stopIoManager();
		DySerialManager.startIoManager();
	}

	@Override
	public void updateReceivedData(final String data) {//采集到数据的回调接口
		// TODO Auto-generated method stub
		  Log.d(TAG, "data" + data);
			  
		  //Toast.makeText(this, "第1次", Toast.LENGTH_SHORT).show();  
		
       if(data.equals("")||data==null){
    	   
    	 Toast.makeText(this, "这段时间范围内没有值", Toast.LENGTH_SHORT).show(); 
				
			}else {
		if(kind==true){//判断是否是第一次发的指令
			
			if(Id==1){
			showProgressDialog() ;
			}
			try {
				
			
				 list = new ArrayList<LogEntity>();
				JSONArray dataJson = new JSONArray(data);
				Gson gson = new Gson();
				for(int i=0; i<dataJson.length(); i++) {
					LogEntity item = gson.fromJson(dataJson.getJSONObject(i).toString(), LogEntity.class);
					list.add(item);
					
					
				}
				
			
				if(list.size()==dataJson.length()){
					
					kind=false;	
					if(spinner.getVisibility()==View.GONE){
						 mHandler.sendEmptyMessage(1);
						
						 
					}
					else {
				if(spinner.getSelectedItem().toString().equals("非试纸法")){  
					  mHandler.sendEmptyMessage(1);
					 
					
					}else if(spinner.getSelectedItem().toString().equals("金标卡法")){
						
						 mHandler.sendEmptyMessage(2);
					}else if(spinner.getSelectedItem().toString().equals("干化学法")){
						
						
						 mHandler.sendEmptyMessage(3);
					}
					}	
					
				}
				
				
			} catch (JSONException e) {
				
				 Toast.makeText(context, "出错", Toast.LENGTH_SHORT).show(); 
				
				e.printStackTrace();
		}
	
		}	else {
			//Toast.makeText(this, "第二次", Toast.LENGTH_SHORT).show(); 
			
		      new Thread(new Runnable() {                    
		    	                     @Override
		    	                      public void run() {
		    	                          
		    	                    
		    	               try {
		    	             				
		    	         					
		    	         				
		    	         					 ArrayList<ProItems> list1 = new ArrayList<ProItems>();
		    	         					JSONArray 	dataJson1 = new JSONArray(data);
		    	         					Gson gson = new Gson();
		    	         					for(int i=0; i<dataJson1.length(); i++) {
		    	         						ProItems item1 = gson.fromJson(dataJson1.getJSONObject(i).toString(), ProItems.class);
		    	         						list1.add(item1);
		    	         						
		    	         						
		    	         					}	
		    	         					
		    	         					
		    	         			if(list1.size()==dataJson1.length()&&list.size()>0){
		    	         				
		    	         			
		    	         				for(int i=0; i<list.size(); i++) {
		    	         							
		    	         							//list.get(i).getItemid();//对应项目的第几个
		    	         					 String checkvalue= list.get(i).getCheckValue(); //检测值
		    	         						String checktime=list.get(i).getDatetime();//检测时间
		    	         						String time1 = null;
		    	         						try {//得到字符窜转化为时间再格式化
		    	         							SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
													Date date  = time.parse(checktime);
												 time1=time.format(date);
													    
												} catch (ParseException e) {
													
													 Toast.makeText(context, "转化时间格式出错", Toast.LENGTH_SHORT).show(); 
														
												e.printStackTrace();
												}
		    	         						String num=list.get(i).getNum();
		    	         						
		    	         						if(list.get(i).getItemid()<list1.size()){
		    	         							 
		    	         							ProItems datas = list1.get(list.get(i).getItemid());
		    	         						
		    	         							 String ckkind=datas.getMethods();//类别
		    	         						     String ckname = datas.getProjects(); //检测项目名称
		    	         							String ckunits=datas.getUnits(); //检测单位*/
		    	         							
		    	         							
		    	         							SQLiteDatabase sqLiteDatabase;
		    	         							sqLiteDatabase = new DBUtils().getDB(context,
		    	         									DBUtils.db_detect_info);

		    	         							ContentValues contentValues = new ContentValues();
		    	         						
		    	         							contentValues.put("CKDNAME", ckname); //项目名
		    	         							
		    	         							
		    	         							contentValues.put("CKSIGN", ckunits);//单位
		    	         							contentValues.put("CKCKVALUE", checkvalue);//检测值
		    	         							contentValues.put("CKSAMPLENUM",num ); //样本编号
		    	         							contentValues.put("CKDATE",time1);//检测日期
		    	         							contentValues.put("CKCHECKTYPE", "检测仪自动");
		    	         							if(Id==1){
		    	         							contentValues.put("CKKINDNAME", ckkind ); //检测类型
		    	         						    contentValues.put("CKCHECKDEVICE", "DY-6600手持式现场执法快检通");
		    	         						   contentValues.put("CKISUPLOAD", "2");    //是否上传且没有录入的数据（1没上传录入的）（0代表上传的）--分给6600
			                                        //3分给3300且没有录入的数据（1没上传录入的）（0代表上传的）
		    	         						    
		    	         							}else if(Id==0){
		    	         								//contentValues.put("CKKINDNAME","无" ); //检测类型
		    	         								 contentValues.put("CKISUPLOAD", "3"); 
		    	         							   contentValues.put("CKCHECKDEVICE", "DY-3300食品综合分析仪");
		    	         							}
		    	         							
		    	         							contentValues.put("CKID", new Utils().getOnlyID(context));//唯一id
		    	         			            	contentValues.put("UDATE", new Utils().getDate_sqlite());//更新时间
		    	         							
                                                   
		    	         							sqLiteDatabase.insert("tb_base_check_checkrec", null,
		    	         									contentValues);
		    	         						
		    	         							

		    	         							sqLiteDatabase.close();
		    	         						}
		    	         						
		    	         				
		    	         					}
		    	         				
		    	         				 mHandler.sendEmptyMessage(4); //发消息跳转页面

		    	         			}	
		    	         				} catch (JSONException e) {
		    	         					
		    	         					e.printStackTrace();
		    	         				}
		    	         			 
		    	                         }                               
		    	                     }
		    	                 ).start();
		    
		}
			}
	}

	 private  void showProgressDialog() {
			
		    
		 if (progressDialog == null){
	            progressDialog = CustomProgressDialog.createDialog(context);
	            progressDialog.setMessage("正在采集数据...");
	          
	            progressDialog.show();
	        }
	         
	      
		

}
private   void dismissProgressDialog(){

	if (progressDialog != null) {
		progressDialog.dismiss();
		progressDialog = null;
		
		
	}

}

	
}