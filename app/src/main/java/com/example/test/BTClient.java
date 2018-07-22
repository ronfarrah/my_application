package com.example.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.view.Menu;            //如使用菜单加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;

public class BTClient extends Activity {

	private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄

	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号

	private InputStream is; // 输入流，用来接收蓝牙数据
	private TextView text0; // 提示栏解句柄
	private EditText edit0; // 发送数据输入句柄
	private TextView dis; // 接收数据显示句柄
	private ScrollView sv; // 翻页句柄
	private String smsg = ""; // 显示用数据缓存
	private String fmsg = ""; // 保存用数据缓存

	private String tmpStr = "";
	private String sendStr = "";// 发送字符串

	private int count = 0;
	private int readcount = 0;

	public String filename = ""; // 用来保存存储的文件名
	BluetoothDevice _device = null; // 蓝牙设备
	BluetoothSocket _socket = null; // 蓝牙通信socket
	boolean _discoveryFinished = false;
	boolean bRun = true;
	boolean bThread = false;

	private Spinner spinner;
	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备

	private boolean sendFlag = true;
	private String sendOrderStr = "";
	private String readAll="";
	private String eventAll="";
	private OutputStream myos;

	private LinearLayout mylayout;
	private LinearLayout top;
	private ListView lv ;
	private Spinner ttSpinner;

	private LinearLayout setTime;
	String setBhttOrderStr = "";
	String setBhdzOrderStr = "";
	String setBhsxOrderStr = "";
	private String dateTime = "";
	private String setKind = "";


	public HashMap<String, String> bhttMap = new HashMap<String, String>();//保护投退MAP
	public HashMap<String, String> bhdzMap = new HashMap<String, String>();//保护定值map
	public HashMap<String, String> bhsxMap = new HashMap<String, String>();//保护时限map
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // 设置画面为主画面 main.xml

		// text0 = (TextView) findViewById(R.id.Text0); // 得到提示栏句柄
		edit0 = (EditText) findViewById(R.id.Edit0); // 得到输入框句柄
		sv = (ScrollView) findViewById(R.id.ScrollView01); // 得到翻页句柄
		dis = (TextView) findViewById(R.id.in); // 得到数据显示句柄
		mylayout = (LinearLayout) findViewById(R.id.mylayout);
		top = (LinearLayout) findViewById(R.id.top);
		lv = (ListView) findViewById(R.id.listView1);

		setTime = (LinearLayout) findViewById(R.id.setTime);
		/*spinner = (Spinner) findViewById(R.id.spinner);
		// 数据
		data_list = new ArrayList<String>();
		data_list.add("01 03 10 00 00 04 读取从机模拟量");
		data_list.add("01 03 12 20 00 01 读取从机开关量");
		data_list.add("01 03 12 00 00 06 读取从机时间");
		data_list.add("01 03 13 FF 00 01 读取从机事件个数");
		data_list.add("01 03 14 00 00 06 读取从机编号为6的事件记录");
		data_list.add("01 03 11 00 00 04 读取从机 保护投退状态");
		data_list.add("01 03 11 10 00 04 读取从机保护定值");
		data_list.add("01 03 11 20 00 04 读取从机保护时限");

		data_list.add("1100   0000设置从机保护退出");
		data_list.add("1110   0500设置从机保护定值");
		data_list.add("1120   0300设置从机保护时限");

		data_list.add("1200 0105 0007 0103 0107 0009 0208 校验从机时间");

		// 适配器
		arr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data_list);
		// 设置样式
		arr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String text = arr_adapter.getItem(position).toString();
				if ("01 03 10 00 00 04 读取从机模拟量".equals(text)) {
					edit0.setText("01 03 10 00 00 04 40 C9");
				} else if ("01 03 12 20 00 01 读取从机开关量".equals(text)) {
					edit0.setText("01 03 12 20 00 01 80 B8");
				} else if ("01 03 12 00 00 06 读取从机时间".equals(text)) {
					edit0.setText("010312000006C0B0");
				} else if ("01 03 13 FF 00 01 读取从机事件个数".equals(text)) {
					edit0.setText("010313FF0001B0BE");
				} else if ("01 03 14 00 00 06 读取从机编号为6的事件记录".equals(text)) {
					edit0.setText("01 03 14 00 00 06");// 可设置为 03 0A等，CRC自动计算
				} else if ("01 03 11 00 00 04 读取从机 保护投退状态".equals(text)) {
					edit0.setText("01 03 11 00 00 04 41 35");
				} else if ("01 03 11 10 00 04 读取从机保护定值".equals(text)) {
					edit0.setText("01 03 11 10 00 04 40 F0");
				} else if ("01 03 11 20 00 04 读取从机保护时限".equals(text)) {
					edit0.setText("01 03 11 20 00 04 40 FF");
				}

				else if ("1100   0000设置从机保护退出".equals(text)) {
					edit0.setText("11 00 0000");
				} else if ("1110   0500设置从机保护定值".equals(text)) {
					edit0.setText("11 10 0500");
				} else if ("1120   0300设置从机保护时限".equals(text)) {
					edit0.setText("11 20 0300");
				}

				else if ("1200 0105 0007 0103 0107 0009 0208 校验从机时间"
						.equals(text)) {
					edit0.setText("1200 0105 0007 0103 0107 0009 0208");
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});*/

		// 如果打开本地蓝牙设备不成功，提示信息，结束程序
		if (_bluetooth == null) {
			Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		// 设置设备可以被搜索
		new Thread() {
			public void run() {
				if (_bluetooth.isEnabled() == false) {
					_bluetooth.enable();
				}
			}
		}.start();
	}

	public class MyAdapter extends SimpleAdapter{

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
						 int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View curRowView = super.getView(position, convertView, parent);
			//设置按钮
			final Button setButton = (Button)curRowView.findViewById(R.id.button2);

			final TextView textView1 = (TextView) curRowView.findViewById(R.id.textView1);//类别
//			final EditText textView2 = (EditText) curRowView.findViewById(R.id.textView2);//获取保护投退
			final Button ttButton = (Button)curRowView.findViewById(R.id.ttButton);//获取保护投退
			final EditText et1 = (EditText) curRowView.findViewById(R.id.et1);//获取保护定值
			final EditText et2 = (EditText) curRowView.findViewById(R.id.et2);//获取保护时限
			final String kind = textView1.getText().toString();
			if("限时速断".equals(kind) || "定时限".equals(kind) || "零序过流".equals(kind)){
				ttButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(BTClient.this) // build AlertDialog
								.setTitle(kind+"投退设定") // title
								.setItems(R.array.thirdArray, new DialogInterface.OnClickListener() { //content
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if(which == 0){//选择第一项退出 第二项 投入
											ttButton.setText("退出");
										}else if(which==1){
											ttButton.setText("投入");
										}
									}
								})
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss(); //关闭alertDialog
									}
								})
								.show();
					}
				});
			}else if("反时限".equals(kind)){
				ttButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(BTClient.this) // build AlertDialog
								.setTitle(kind+"投退设定") // title
								.setItems(R.array.fourthArray, new DialogInterface.OnClickListener() { //content
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if(which == 0){//选择第一项退出 第二项 投入
											ttButton.setText("退出");
										}else if(which==1){
											ttButton.setText("C1");
										}else if(which==2){
											ttButton.setText("C2");
										}else if(which==3){
											ttButton.setText("C3");
										}else if(which==4){
											ttButton.setText("C4");
										}else if(which==5){
											ttButton.setText("C5");
										}
									}
								})
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss(); //关闭alertDialog
									}
								})
								.show();
					}
				});
			}


			OnClickListener onUrlTxtClick = new OnClickListener() {

				public void onClick(View v) {
//					String kind = textView1.getText().toString();
					if( TextUtils.isEmpty(et1.getText())
							|| TextUtils.isEmpty(et2.getText())){
						Toast.makeText(getApplicationContext(), "当前行有设置内容为空！", Toast.LENGTH_LONG)
								.show();
						return ;
					}
					String bhttValue = ttButton.getText().toString().trim().toUpperCase() ;
					String bhdzValue = et1.getText().toString().trim().toUpperCase() ;
					String bhsxValue = et2.getText().toString().trim().toUpperCase();
					if(!kind.equals("保护名称")){
//						Toast.makeText(getApplicationContext(),bhttValue+"  "+bhdzValue+bhsxValue , Toast.LENGTH_SHORT).show();
						setXSSD(kind,bhttValue,bhdzValue,bhsxValue);
					}
					setKind = kind;
				}
			};
			//实现TextView的点击效果
			setButton.setOnClickListener(onUrlTxtClick);
			return curRowView;
		}


		protected void setXSSD(String kind, String bhttValue,
							   String bhdzValue, String bhsxValue) {
			if("限时速断".equals(kind)){
				if("退出".equals(bhttValue)){
					setBhttOrderStr = "11000000";
				}else if("投入".equals(bhttValue)){
					setBhttOrderStr = "11000001";
				}
			}else if("定时限".equals(kind)){
				if("退出".equals(bhttValue)){
					setBhttOrderStr = "11010000";
				}else if("投入".equals(bhttValue)){
					setBhttOrderStr = "11010001";
				}
			}else if("零序过流".equals(kind)){
				if("退出".equals(bhttValue)){
					setBhttOrderStr = "11020000";
				}else if("投入".equals(bhttValue)){
					setBhttOrderStr = "11020001";
				}
			}else if("反时限".equals(kind)){
				if("退出".equals(bhttValue)){
					setBhttOrderStr = "11030000";
				}else if("C1".equals(bhttValue)){
					setBhttOrderStr = "11030001";
				}else if("C2".equals(bhttValue)){
					setBhttOrderStr = "11030002";
				}else if("C3".equals(bhttValue)){
					setBhttOrderStr = "11030003";
				}else if("C4".equals(bhttValue)){
					setBhttOrderStr = "11030004";
				}else if("C5".equals(bhttValue)){
					setBhttOrderStr = "11030005";
				}
			}


			setBhttOrderStr = StringToWant.ConcatOrder(setBhttOrderStr);
			sendOrderStr = CRC16.getOrderByShort(setBhttOrderStr);
			byte[] tmpByte = StringHexUtils.getHexBytes(sendOrderStr.replace(" ", ""));
			OutputStream os = null;
			try {
				os =_socket.getOutputStream();
				if(!"".equals(smsg)){
					smsg="";
				}
				os.write(tmpByte);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				bhdzValue = bhdzValue.replace("A", "");//去掉A
				String bhdzValueInt = String.valueOf(Float
						.valueOf(bhdzValue) * 100);
				bhdzValueInt = bhdzValueInt.substring(0, bhdzValueInt.lastIndexOf("."));
				bhdzValue = CRC16.addZeroForNum(bhdzValueInt, 4);//补0，补成4位


				bhsxValue = bhsxValue.replace("S", "");//去掉A
				String bhsxValueInt = String.valueOf(Float
						.valueOf(bhsxValue) * 100);
				bhsxValueInt = bhsxValueInt.substring(0, bhsxValueInt.lastIndexOf("."));
				bhsxValue = CRC16.addZeroForNum(bhsxValueInt, 4);

				if("限时速断".equals(kind)){
					setBhdzOrderStr = "1110" + bhdzValue;
					setBhsxOrderStr = "1120" + bhsxValue;//1110 +(数值4位)
				}else if("定时限".equals(kind)){
					setBhdzOrderStr = "1111" + bhdzValue;
					setBhsxOrderStr = "1121" + bhsxValue;//1110 +(数值4位)
				}else if("零序过流".equals(kind)){
					setBhdzOrderStr = "1112" + bhdzValue;
					setBhsxOrderStr = "1122" + bhsxValue;//1110 +(数值4位)
				}else if("反时限".equals(kind)){
					setBhdzOrderStr = "1113" + bhdzValue;
					setBhsxOrderStr = "1123" + bhsxValue;//1110 +(数值4位)
				}
				setBhdzOrderStr = StringToWant.ConcatOrder(setBhdzOrderStr);
				setBhsxOrderStr = StringToWant.ConcatOrder(setBhsxOrderStr);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "设置数据错误请检查后重新输入！", 0).show();
				return;
			}
		}
	}

	public void onReadCongjiInfo(View view) {

		sv.setVisibility(View.VISIBLE);
		mylayout.setVisibility(View.GONE);
		top.setVisibility(View.GONE);
		if (_socket == null) {
			Toast.makeText(this, "请先连接蓝牙再发送指令", Toast.LENGTH_SHORT).show();
			return;
		}
		/*if(!"".equals(readAll)){
			readAll = "";
		}*/
		String[] orders = new String[3];
		/**
		 * 01031000000440C9-->010308000000000000000095D7(26) 模拟量
		 * 01031220000180B8-->0103020000B844(14) 开关量
		 * 010312000006C0B0-->01030C010500070106010405000306DF6C(34) 从机时间
		 */
		orders[0] = "01031000000440C9";// 读从机模拟量
		orders[1] = "01031220000180B8";//
		orders[2] = "010312000006C0B0";
		if (!"".equals(sendOrderStr)) {
			sendOrderStr = "";
		}
		OutputStream os = null;
		try {
			os = _socket.getOutputStream();// 蓝牙连接输出流
			myos = os;
			sendOrderStr = "01031000000440C9";
			if(!"".equals(smsg)){
				smsg = "";
			}
			os.write(StringHexUtils.getHexBytes(sendOrderStr));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(dis.getText().toString() != null){
			dis.setText("");
			dis.setText(readAll);
//			dis.setText(eventAll);
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
		setTime.setVisibility(View.VISIBLE);
	}

	/**
	 * 事件记录
	 * @param view
	 */
	public void eventRecord(View view){
		setTime.setVisibility(View.GONE);
		sv.setVisibility(View.VISIBLE);
		mylayout.setVisibility(View.GONE);
		top.setVisibility(View.GONE);

		if (_socket == null) {
			Toast.makeText(this, "请先连接蓝牙再发送指令", Toast.LENGTH_SHORT).show();
			return;
		}
		String eventstr = "010313FF0001B0BE";//读取事件个数
		if (!"".equals(sendOrderStr)) {
			sendOrderStr = "";
		}
		OutputStream os = null;
		try {
			os = _socket.getOutputStream();// 蓝牙连接输出流
			sendOrderStr = eventstr;
			if(!"".equals(smsg)){
				smsg = "";
			}
			os.write(StringHexUtils.getHexBytes(sendOrderStr));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(dis.getText().toString() != null){
			dis.setText("");
//			dis.setText(readAll);
			dis.setText(eventAll);
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页

	}

	/**
	 * 保护设定
	 * @param view
	 */
	public void protect(View view){
		setTime.setVisibility(View.GONE);
		if (_socket == null) {
			Toast.makeText(this, "请先连接蓝牙再发送指令", Toast.LENGTH_SHORT).show();
			return;
		}else{
			if (!"".equals(sendOrderStr)) {
				sendOrderStr = "";
			}
			OutputStream os = null;
			try {
				os = _socket.getOutputStream();// 蓝牙连接输出流
				myos = os;
				sendOrderStr = "0103110000044135";
				if(!"".equals(smsg)){
					smsg = "";
				}
				os.write(StringHexUtils.getHexBytes(sendOrderStr));
			} catch (IOException e) {
				e.printStackTrace();
			}

			ArrayList<HashMap<String, String>> dItems = new ArrayList<HashMap<String, String>>();

//			HashMap<String, String> headItem = new HashMap<String, String>();
//			headItem.put("Name", "保护名称");
//			headItem.put("BHTC", "保护投退");
//			headItem.put("BHDZ", "保护定值");
//			headItem.put("BHSX", "保护时限");
//			dItems.add(headItem);

			HashMap<String, String> xssdItem = new HashMap<String, String>();
			xssdItem.put("Name", "限时速断");
			xssdItem.put("BHTC", bhttMap.get("限时速断保护投退"));
			xssdItem.put("BHDZ", bhdzMap.get("限时速断保护定值"));
			xssdItem.put("BHSX", bhsxMap.get("限时速断保护时限"));
			xssdItem.put("ST", "设置");
			dItems.add(xssdItem);

			HashMap<String, String> dsxItem = new HashMap<String, String>();
			dsxItem.put("Name", "定时限");
			dsxItem.put("BHTC", bhttMap.get("定时限保护投退"));
			dsxItem.put("BHDZ", bhdzMap.get("定时限保护定值"));
			dsxItem.put("BHSX", bhsxMap.get("定时限保护时限"));
			dsxItem.put("ST", "设置");
			dItems.add(dsxItem);

			HashMap<String, String> lxglItem = new HashMap<String, String>();
			lxglItem.put("Name", "零序过流");
			lxglItem.put("BHTC", bhttMap.get("零序过流保护投退"));
			lxglItem.put("BHDZ", bhdzMap.get("零序过流保护定值"));
			lxglItem.put("BHSX", bhsxMap.get("零序过流保护时限"));
			lxglItem.put("ST", "设置");
			dItems.add(lxglItem);

			HashMap<String, String> fsxItem = new HashMap<String, String>();
			fsxItem.put("Name", "反时限");
			fsxItem.put("BHTC", bhttMap.get("反时限保护投退"));
			fsxItem.put("BHDZ", bhdzMap.get("反时限保护定值"));
			fsxItem.put("BHSX", bhsxMap.get("反时限保护时限"));
			fsxItem.put("ST", "设置");
			dItems.add(fsxItem);

			// 下面两行用于切换使用listview_row_basic或者listview_row的行部局
			MyAdapter adapter = new MyAdapter(this, dItems, R.layout.listview_row,
					new String[] { "Name", "BHTC", "BHDZ", "BHSX", "ST" },
					new int[] { R.id.textView1, R.id.ttButton, R.id.et1, R.id.et2,
							R.id.button2 });
			lv.setAdapter(adapter);
		}

		/*ttSpinner =  (Spinner) findViewById(R.id.ttSpinner);
		data_list = new ArrayList<String>();
		data_list.add("退出");
		data_list.add("投入");
		data_list.add("C1");
		data_list.add("C2");
		data_list.add("C3");
		data_list.add("C4");
		data_list.add("C5");
		// 适配器
		arr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data_list);
		// 设置样式
		arr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 加载适配器
		ttSpinner.setAdapter(arr_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String text = arr_adapter.getItem(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});*/

		sv.setVisibility(View.GONE);
		mylayout.setVisibility(View.VISIBLE);
		top.setVisibility(View.VISIBLE);
	}

	// 发送按键响应
	public void onSendButtonClicked(View v) {
		if (_socket == null) {
			Toast.makeText(this, "请先连接蓝牙再发送指令", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(edit0.getText())) {
			Toast.makeText(this, "发送内容为空", Toast.LENGTH_SHORT).show();
			return;
		}
		int i = 0;
		int n = 0;
		try {
			if (!"".equals(smsg)) {
				smsg = "";
			}
			if (!"".equals(sendStr)) {
				sendStr = "";
			}

			OutputStream os = _socket.getOutputStream(); // 蓝牙连接输出流
			sendStr = edit0.getText().toString().trim().replace(" ", "")
					.toUpperCase();
			// byte[] bos = sendStr.getBytes();
			/*
			 * for(i=0;i<bos.length;i++){ if(bos[i]==0x0a)n++; } byte[] bos_new
			 * = new byte[bos.length+n]; n=0; for(i=0;i<bos.length;i++){
			 * //手机中换行为0a,将其改为0d 0a后再发送 if(bos[i]==0x0a){ bos_new[n]=0x0d; n++;
			 * bos_new[n]=0x0a; }else{ bos_new[n]=bos[i]; } n++; }
			 * os.write(bos_new);
			 */
			// 010314000006--------->010314000006C038
			if (sendStr.length() > 10
					&& "0314".equalsIgnoreCase(sendStr.substring(2, 6))) {
				sendStr = sendStr.substring(0, 12);
				byte[] tmpByte = StringHexUtils.getHexBytes(sendStr.replace(
						" ", ""));
				tmpByte = CRC16.crc(tmpByte); // 计算CRC
				sendStr = StringHexUtils.Bytes2HexString(tmpByte);
				os.write(tmpByte);
			} else if (sendStr.length() >= 4
					&& "110".equalsIgnoreCase(sendStr.substring(0, 3))) {// 设置命令指令(01)1011
				if (sendStr.length() == 8) {
					String order = StringToWant.ConcatOrder(sendStr);
					sendStr = CRC16.getOrderByShort(order);
					byte[] tmpByte = StringHexUtils.getHexBytes(sendStr
							.replace(" ", ""));
					os.write(tmpByte);
				}
			} else if (sendStr.length() > 4
					&& "111".equals(sendStr.substring(0, 3))) {// 11100500 1111
				// 1112 1113
				// 设置保护定值命令
				if (sendStr.length() == 8) {
					String order = StringToWant.ConcatOrder(sendStr);
					// byte[] tmpByte = StringHexUtils.getHexBytes(order);
					// tmpByte = CRC16.crc(tmpByte); //计算CRC
					// sendStr = StringHexUtils.Bytes2HexString(tmpByte);
					sendStr = CRC16.getOrderByShort(order);
					byte[] tmpByte = StringHexUtils.getHexBytes(sendStr
							.replace(" ", ""));
					os.write(tmpByte);
				}
			} else if (sendStr.length() > 4
					&& "112".equals(sendStr.substring(0, 3))) {// 11100500 1111
				// 1112 1113
				// 设置保护时限命令 1120
				// 1121 1122
				// 1123
				if (sendStr.length() == 8) {
					String order = StringToWant.ConcatOrder(sendStr);
					// byte[] tmpByte = StringHexUtils.getHexBytes(order);
					// tmpByte = CRC16.crc(tmpByte); //计算CRC
					// sendStr = StringHexUtils.Bytes2HexString(tmpByte);
					sendStr = CRC16.getOrderByShort(order);
					byte[] tmpByte = StringHexUtils.getHexBytes(sendStr
							.replace(" ", ""));
					os.write(tmpByte);
				}
			} else if (sendStr.length() > 4
					&& "120".equals(sendStr.substring(0, 3))) {// 1200 校验时间
				// 1200 0105 0007 0103 0107 0009 0208 设置为15年07月13日17时09分28秒
				if (sendStr.length() == 28) {
					String order = StringToWant.concatSetTime(sendStr);
					Calendar c = Calendar.getInstance();
					sendStr = CRC16.getOrderByShort(order);
					byte[] tmpByte = StringHexUtils.getHexBytes(sendStr
							.replace(" ", ""));
					os.write(tmpByte);
				}
			} else {
				os.write(StringHexUtils.getHexBytes(sendStr.replace(" ", "")));
			}

		} catch (IOException e) {
		}
	}


	public void onSetTime(View view){
		if (_socket == null) {
			Toast.makeText(this, "请先连接蓝牙再发送指令", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(edit0.getText())) {
			Toast.makeText(this, "发送内容为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!"".equals(smsg)) {
			smsg = "";
		}

		try {
			OutputStream os = _socket.getOutputStream();
			sendOrderStr = edit0.getText().toString().trim().replace(" ", "")
					.toUpperCase();
			sendOrderStr = "0110120000060C" + StringToWant.concatSetTimeOrder(sendOrderStr);
			sendOrderStr = CRC16.getOrderByShort(sendOrderStr);//计算CRC
			byte[] tmpByte = StringHexUtils.getHexBytes(sendOrderStr.replace(" ", ""));
			os.write(tmpByte);
		} catch (IOException e) {
			e.printStackTrace();
		} // 蓝牙连接输出流

	}

	// 接收活动结果，响应startActivityForResult()
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE: // 连接结果，由DeviceListActivity设置返回
				// 响应返回结果
				if (resultCode == Activity.RESULT_OK) { // 连接成功，由DeviceListActivity设置返回
					// MAC地址，由DeviceListActivity设置返回
					String address = data.getExtras().getString(
							DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// 得到蓝牙设备句柄
					_device = _bluetooth.getRemoteDevice(address);

					// 用服务号得到socket
					try {
						_socket = _device.createRfcommSocketToServiceRecord(UUID
								.fromString(MY_UUID));
					} catch (IOException e) {
						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
					}
					// 连接socket
					Button btn = (Button) findViewById(R.id.Button03);
					try {
						_socket.connect();
						Toast.makeText(this, "连接" + _device.getName() + "成功！",
								Toast.LENGTH_SHORT).show();
						btn.setText("断开");
					} catch (IOException e) {
						try {
							Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT)
									.show();
							_socket.close();
							_socket = null;
						} catch (IOException ee) {
							Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT)
									.show();
						}

						return;
					}

					// 打开接收线程
					try {
						is = _socket.getInputStream(); // 得到蓝牙数据输入流
					} catch (IOException e) {
						Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
						return;
					}
					if (bThread == false) {
						ReadThread.start();
						bThread = true;
					} else {
						bRun = true;
					}
				}
				break;
			default:
				break;
		}
	}

	// 消息处理队列
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// if (dis.getText().toString() != null) {
			// dis.setText("");
			// } 0823

			Bundle bundle = msg.getData();
			String str = bundle.getString("smsg");

			//模拟量开关量从机时间
			if ("01031000000440C9".equalsIgnoreCase(sendOrderStr)) {
				if (str.length() == 26) {
					Log.i("send", "第一次收到" + str);
					String show = StringToWant.CJString(str);
					readAll="模拟量\n"+show+"\n";
					sendOrderStr = "01031220000180B8";
					try {
						myos = _socket.getOutputStream();
						smsg="";
						str = "";
						myos.write(StringHexUtils.getHexBytes(sendOrderStr));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			//从机开关量
			if ("01031220000180B8".equals(sendOrderStr)) {
				if (str.length() == 14) {// 0103020000B844
					Log.i("send", "第2次收到" + str);
					String show = StringToWant.switchLiang(str);
					readAll+="\n开关量\n"+show+"\n";
					sendOrderStr = "010312000006C0B0";
					try {
						myos = _socket.getOutputStream();
						smsg="";
						str = "";
						myos.write(StringHexUtils.getHexBytes(sendOrderStr));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			//从机时间
			if ("010312000006C0B0".equalsIgnoreCase(sendOrderStr)) {
				if (str.length() == 34) {// 0103020000B844
					Log.i("send", "第3次收到" + str);
					String show = StringToWant.readCongjiDateTime(str);
					String time = StringToWant.getAppTime();
					edit0.setText(time);
					str = "";
					smsg = "";
					readAll+="\n"+show+"\n";
					dateTime = show;
				}
			}


			// 事件记录
			if ("010313FF0001B0BE".equalsIgnoreCase(sendOrderStr)) {
				if (str.length() == 14) {// 01030200063846
					Log.i("send", "事件个数" + str);
					count = StringToWant.readInterRecords(str);
					if (count > 0 && readcount < count) {
						// 读取第一条事件 010314000006CRC
						sendOrderStr = "010314000001";
						sendOrderStr = sendOrderStr.substring(0, 12);
						byte[] tmpByte = StringHexUtils.getHexBytes(sendOrderStr
								.replace(" ", ""));
						tmpByte = CRC16.crc(tmpByte); // 计算CRC
						sendOrderStr = StringHexUtils.Bytes2HexString(tmpByte);
						try {
							myos = _socket.getOutputStream();
							smsg = "";
							str = "";
							//发送读取第一条事件记录
							myos.write(tmpByte);
							readcount++;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			//读取编号为N的事件记录
			if(sendOrderStr.length()==16 && "0314".equals(sendOrderStr.substring(2, 6))
					&& readcount<=count ){
				if(str.length()==38){//01030E3A0103050001010000350000006883D0
					String show = StringToWant.readInterRecordByNo(str);
					eventAll+= "\n事件记录("+readcount+"/"+count+")\n"+show;
					readcount++;//readcount==2
					String lastTwo = CRC16.addZeroForNum(String.valueOf(readcount), 2).toUpperCase();//2-02 a-)A
					sendOrderStr="0103140000"+lastTwo;//010314000002
					byte[] tmpByte = StringHexUtils.getHexBytes(sendOrderStr
							.replace(" ", ""));
					tmpByte = CRC16.crc(tmpByte); // 计算CRC
					sendOrderStr = StringHexUtils.Bytes2HexString(tmpByte);
					try {
						myos = _socket.getOutputStream();
						smsg = "";
						str = "";
						//发送读取第一条事件记录
						myos.write(tmpByte);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			//读取从机退出投入信息
			if(!"".equals(sendOrderStr) && "0103110000044135".equals(sendOrderStr)){
				if(str.length()==26){//获取从机保护投退成功
					bhttMap = StringToWant.readCJBhttStatus(str);
					try {
						myos = _socket.getOutputStream();
						str = "";
						sendOrderStr = "01031110000440F0";//读取保护定值
						if(!"".equals(smsg)){
							smsg = "";
						}
						myos.write(StringHexUtils.getHexBytes(sendOrderStr));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if(!"".equals(sendOrderStr) && "01031110000440F0".equals(sendOrderStr)){
				if(str.length()==26){//获取从机保护定值成功
					bhdzMap = StringToWant.getBhValue(str);//获得保护定值MAP
					try {
						myos = _socket.getOutputStream();
						str = "";
						sendOrderStr = "01031120000440FF";//读取保护时限
						if(!"".equals(smsg)){
							smsg = "";
						}
						myos.write(StringHexUtils.getHexBytes(sendOrderStr));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if(!"".equals(sendOrderStr) && "01031120000440FF".equals(sendOrderStr)){
				if(str.length()==26){//获取从机保护时限成功
					bhsxMap = StringToWant.getBhSq(str);//获得保护时限MAP
					str = "";
					smsg = "";
				}
			}



			//设置限时速断 退出投入
			if(!"".equals(sendOrderStr) && sendOrderStr.length()>6){
				if("10110".equals(sendOrderStr.substring(2, 7))){//01101101 01101102 01101103
					if(str.length()==16){//设置成功
						Log.i("set", "设置限时速断保护投入或退出成功");
						sendOrderStr = CRC16.getOrderByShort(setBhdzOrderStr);//发送设置保护定值指令
						byte[] tmpByte = StringHexUtils.getHexBytes(sendOrderStr
								.replace(" ", ""));
						try {
							myos = _socket.getOutputStream();
							str = "";
							smsg = "";
							//发送读取第一条事件记录
							myos.write(tmpByte);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			//设置限时速断 保护定值
			if(!"".equals(sendOrderStr) && sendOrderStr.length()>6){
				if("10111".equals(sendOrderStr.substring(2, 7))){//01101110 01101111 01101112 01101113
					if(str.length()==16){//设置成功
						Log.i("set", "设置限时速断保护定值成功");
						sendOrderStr = CRC16.getOrderByShort(setBhsxOrderStr);//发送设置保护时限指令
						byte[] tmpByte = StringHexUtils.getHexBytes(sendOrderStr
								.replace(" ", ""));
						try {
							myos = _socket.getOutputStream();
							str = "";
							smsg = "";
							//发送读取第一条事件记录
							myos.write(tmpByte);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			if(!"".equals(sendOrderStr) && sendOrderStr.length()>6){
				if("10112".equals(sendOrderStr.substring(2, 7))){//01101120 01101121 01101122 01101123
					if(str.length()==16){//设置成功
						Log.i("set", "设置限时速断保护时限成功");
						str = "";
						smsg = "";
						Toast.makeText(getApplicationContext(), "设置"+setKind+"数据成功", 0).show();
					}
				}
			}

			//校验时间
			if(!"".equals(sendOrderStr) && sendOrderStr.length()>6){
				if("10120".equals(sendOrderStr.substring(2, 7))){//01101120 01101121 01101122 01101123
					if(str.length()==16){//设置成功
						Log.i("set", "校验时间成功");
						str = "";
						smsg = "";
						Toast.makeText(getApplicationContext(), "校验时间成功", 0).show();
					}
				}
			}
		}
	};

	// 接收数据线程
	Thread ReadThread = new Thread() {

		public void run() {
			int num = 0;
			byte[] buffer = new byte[1024];
			byte[] buffer_new = new byte[1024];
			int i = 0;
			int n = 0;
			bRun = true;

			// 接收线程
			while (true) {
				try {
					while (is.available() == 0) {
						while (bRun == false) {
						}
					}
					while (true) {
						num = is.read(buffer); // 读入数据
						n = 0;

						// String s0 = StringHexUtils.bytesToHexString(buffer);
						// fmsg+=s0; //保存收到数据

						byte[] data = new byte[num];
						for (i = 0; i < data.length; i++) {
							data[i] = buffer[i];
							if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
								buffer_new[n] = 0x0a;
								i++;
							} else {
								buffer_new[n] = buffer[i];
							}
							n++;
						}

						// String s = StringHexUtils.bytesToHexString(buffer);
						String tmp = StringHexUtils.bytesToHexString(data);
						smsg += tmp; // 写入接收缓存
						if (is.available() == 0) {
							// tmpStr = smsg;
							// smsg="";
							break; // 短时间没有数据才跳出进行显示
						}
					}
					// 发送显示消息，进行显示刷新
					if (smsg.equals("1234"))
						text0.setText("123");

					// handler.sendMessage(handler.obtainMessage());
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("smsg", smsg);
					msg.setData(bundle);
					msg.sendToTarget();
					// smsg="";

				} catch (IOException e) {
				}
			}
		}
	};

	/*	public void getBhSq(String str) {
		String show = StringToWant.getBhSq(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
	}

	public void getBhValue(String str) {
		String show = StringToWant.getBhValue(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
	}

	public void readCJBhttStatus(String str) {
		String show = StringToWant.readCJBhttStatus(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis);
		// sv.scrollTo(0, sv.getMeasuredHeight()); // 跳至数据最后一页
	}*/

	public void readInterRecordByNo(String str) {
		String show = StringToWant.readInterRecordByNo(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
	}

	/**
	 * 读取从机事件个数
	 *
	 * @param str
	 */
	public void readInterRecords(String str) {
		count = StringToWant.readInterRecords(str);
		String show = "从机事件个数:" + count;
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
	}

	/**
	 * 读取从机时间
	 *
	 * @param str
	 */
	public void readCongjiDateTime(String str) {
		String show = StringToWant.readCongjiDateTime(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
	}

	/**
	 * 读取模拟量
	 *
	 * @param str
	 */
	public void showCJString(String str) {
		String show = StringToWant.CJString(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页
	}

	/**
	 * 读取开关量
	 *
	 * @param str
	 */
	public void showSwitch(String str) {
		String show = StringToWant.switchLiang(str);
		if (dis.getText().toString() != null) {
			tmpStr = dis.getText().toString();
			dis.setText(tmpStr + "\n\n发送：" + sendStr + "\n接收：" + str
					+ "\n解析指令:" + show); // 显示数据
		} else {
			dis.setText("发送：" + sendStr + "\n接收：" + str + "\n解析指令:" + show); // 显示数据
		}
		scroll2Bottom(sv, dis); // 跳至数据最后一页

	}

	public static void scroll2Bottom(final ScrollView scroll, final View inner) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				// 内层高度超过外层
				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();
				if (offset < 0) {
					System.out.println("定位...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}


	// 关闭程序掉用处理部分
	public void onDestroy() {
		super.onDestroy();
		if (_socket != null) // 关闭连接socket
			try {
				_socket.close();
			} catch (IOException e) {
			}
		// _bluetooth.disable(); //关闭蓝牙服务
	}

	// 菜单处理部分
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {//建立菜单
	 * MenuInflater inflater = getMenuInflater();
	 * inflater.inflate(R.menu.option_menu, menu); return true; }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { //菜单响应函数
	 * switch (item.getItemId()) { case R.id.scan:
	 * if(_bluetooth.isEnabled()==false){ Toast.makeText(this, "Open BT......",
	 * Toast.LENGTH_LONG).show(); return true; } // Launch the
	 * DeviceListActivity to see devices and do scan Intent serverIntent = new
	 * Intent(this, DeviceListActivity.class);
	 * startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); return
	 * true; case R.id.quit: finish(); return true; case R.id.clear: smsg="";
	 * ls.setText(smsg); return true; case R.id.save: Save(); return true; }
	 * return false; }
	 */

	// 连接按键响应函数
	public void onConnectButtonClicked(View v) {
		if (_bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
			Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
			return;
		}

		// 如未连接设备则打开DeviceListActivity进行设备搜索
		Button btn = (Button) findViewById(R.id.Button03);
		if (_socket == null) {
			Intent serverIntent = new Intent(this, DeviceListActivity.class); // 跳转程序设置
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
		} else {
			// 关闭连接socket
			try {

				is.close();
				_socket.close();
				_socket = null;
				bRun = false;
				btn.setText("连接");
			} catch (IOException e) {
			}
		}
		return;
	}

	// 保存按键响应函数
	public void onSaveButtonClicked(View v) {
		Save();
	}

	// 清除按键响应函数
	public void onClearButtonClicked(View v) {
		smsg = "";
		fmsg = "";
		dis.setText(smsg);
		return;
	}

	// 退出按键响应函数
	public void onQuitButtonClicked(View v) {
		finish();
	}

	// 保存功能实现
	private void Save() {
		// 显示对话框输入文件名
		LayoutInflater factory = LayoutInflater.from(BTClient.this); // 图层模板生成器句柄
		final View DialogView = factory.inflate(R.layout.sname, null); // 用sname.xml模板生成视图模板
		new AlertDialog.Builder(BTClient.this).setTitle("文件名")
				.setView(DialogView) // 设置视图模板
				.setPositiveButton("确定", new DialogInterface.OnClickListener() // 确定按键响应函数
				{
					public void onClick(DialogInterface dialog,
										int whichButton) {
						EditText text1 = (EditText) DialogView
								.findViewById(R.id.sname); // 得到文件名输入框句柄
						filename = text1.getText().toString(); // 得到文件名

						try {
							if (Environment.getExternalStorageState()
									.equals(Environment.MEDIA_MOUNTED)) { // 如果SD卡已准备好

								filename = filename + ".txt"; // 在文件名末尾加上.txt
								File sdCardDir = Environment
										.getExternalStorageDirectory(); // 得到SD卡根目录
								File BuildDir = new File(sdCardDir,
										"/data/TQData"); // 打开data目录，如不存在则生成
								if (BuildDir.exists() == false)
									BuildDir.mkdirs();
								File saveFile = new File(BuildDir,
										filename); // 新建文件句柄，如已存在仍新建文档
								FileOutputStream stream = new FileOutputStream(
										saveFile); // 打开文件输入流
								stream.write(fmsg.getBytes());
								stream.close();
								Toast.makeText(BTClient.this, "存储成功！",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(BTClient.this, "没有存储卡！",
										Toast.LENGTH_LONG).show();
							}

						} catch (IOException e) {
							return;
						}

					}
				}).setNegativeButton("取消", // 取消按键响应函数,直接退出对话框不做任何处理
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
										int which) {
					}
				}).show(); // 显示对话框
	}
}