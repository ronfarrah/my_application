package com.example.test;

import java.util.Calendar;
import java.util.HashMap;


public class StringToWant {

	public static void main(String[] args) {
		// CJString("01 03 08 13 88 00 88 11 88 12 34 95 D7");
		//switchLiang("01 03 02 00 03 B8 44");
		//readCongjiDateTime("01 03 0C 01 05 00 07 01 06 01 04 05 01 03 06 DF 6C");
		//readInterRecords("01 03 02 00 06 38 46");
		
		
/*		int i = 6;
		String s = (" 01 03 0E 3A 01 03 05 00 01 01 00 00 35 00 00 00 68 83 D0").replace(" ", "");
//		String order = ConcatOrder("11100500");
		String str= "01 03 10 00 00 04";//01 03 12 FF 00 01-->010313FF00014EF1
		str=str.replace(" ", "");
		byte[] tmpByte = StringHexUtils.getHexBytes(str);
		tmpByte = CRC16.crc(tmpByte); //计算CRC
		String sendStr = StringHexUtils.Bytes2HexString(tmpByte);
		System.out.println(sendStr);*/

		/**
		 * 1	// 十进制转化为十六进制，结果为C8。
		 2	Integer.toHexString(200);
		 3
		 4	// 十六进制转化为十进制，结果140。
		 5	Integer.parseInt("8C",16);

		 */
		System.out.println(Integer.toHexString(10));
		String time="15年07月13日17时09分28秒";//--01	05	00	07	01	03	01	07	00	09	02	08
		System.out.println(concatSetTimeOrder(time));
	}

	public static String getAppTime(){
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR)).substring(2,4);
		String month = CRC16.addZeroForNum(String.valueOf(c.get(Calendar.MONTH)+1), 2);
		String day = CRC16.addZeroForNum(String.valueOf(c.get(Calendar.DATE)),2);
		String hour = CRC16.addZeroForNum(String.valueOf(c.get(Calendar.HOUR)),2);
		String minute = CRC16.addZeroForNum(String.valueOf(c.get(Calendar.MINUTE)),2);
		String seconds = CRC16.addZeroForNum(String.valueOf(c.get(Calendar.SECOND)),2);
		System.out.println(year+"年"+month+"月"+day+"日"+hour+"时" + minute+"分" + seconds + "秒");
		return year+"年"+month+"月"+day+"日"+hour+"时" + minute+"分" + seconds + "秒";
	}

	public static String concatSetTimeOrder(String time){
		String year = twoToFour(time.substring(0, 2));
		String month = twoToFour(time.substring(3, 5));
		String day = twoToFour(time.substring(6, 8));
		String hour = twoToFour(time.substring(9, 11));
		String minute = twoToFour(time.substring(12, 14));
		String seconds = twoToFour(time.substring(15, 17));
		return year + month + day + hour + minute + seconds;
	}
	/**
	 * 两位转换为4位
	 * @return
	 */
	public static String twoToFour(String two){
		String four = "";
		if("0".equals(two.substring(0, 1))){
			four+="00";
		}else{
			four+="0"+two.substring(0, 1);
		}

		if("0".equals(two.substring(1, 2))){
			four+="00";
		}else{
			four+="0"+two.substring(1, 2);
		}
		return four;
	}

	/**
	 * 主机	01	03	11	00	00	04	41  35

	 从机				限时速断	定时限	零序过流	反时限	CRC16
	 01	03	08		0000	0000	0000	0000	95  D7
	 限时速断、定时限、零序过流   0000=退出   0001=投入
	 反时限  0000=退出 /0001=C1 /0002=C2 /0003=C3 /0004=C4 /0005=C5
	 */
	public static HashMap<String,String> readCJBhttStatus(String str){
		//限时速断
		String qssd = getStatus(str.substring(6, 10));
		//定时限
		String dsx = getStatus(str.substring(10, 14));
		//零序过流
		String lxgl = getStatus(str.substring(14, 18));
		//反时限
		String fsx = str.substring(18, 22);
		if("0000".equals(fsx)){
			fsx = "退出";
		}else if("0001".equals(fsx)){
			fsx = "C1";
		}else if("0002".equals(fsx)){
			fsx = "C2";
		}else if("0003".equals(fsx)){
			fsx = "C3";
		}else if("0004".equals(fsx)){
			fsx = "C4";
		}else if("0005".equals(fsx)){
			fsx = "C5";
		}
		HashMap<String,String> bhttMap = new HashMap<String, String>();
		bhttMap.put("限时速断保护投退", qssd);
		bhttMap.put("定时限保护投退", dsx);
		bhttMap.put("零序过流保护投退", lxgl);
		bhttMap.put("反时限保护投退", fsx);
		/*String returnStr =  "---从机保护投退状态----\n" +
							"限时速断:" + qssd + "\n" + 
						   "定时限:" + dsx + "\n" + 
						   "零序过流:" + lxgl + "\n" + 
						   "反时限:" + fsx ;*/
		return bhttMap;
	}

	/**读取从机保护定值
	 * 主机	01	03	11	10	00	04	40  F0
	 从机				限时速断	定时限	零序过流	反时限	CRC16
	 01	03	08	     07D0	0053	004B	0010	F0 2A
	 限时速断 保护定值=0x07D0=2000	显示20.00 A
	 定时限   保护定值=0x0053=83    	显示00.83 A
	 * @param str
	 * @return
	 */
	public static HashMap<String,String> getBhValue(String str) {
		// 限时速断
		String qssd = str.substring(6, 10);
		// 定时限
		String dsx = str.substring(10, 14);
		// 零序过流
		String lxgl = str.substring(14, 18);
		// 反时限
		String fsx = str.substring(18, 22);
		float fqssd=0.0f,fdsx=0.0f,flxgl=0.0f,ffsx=0.0f;
		fqssd = Integer.parseInt(qssd, 16);//十六进制转化为十进制
		fqssd = fqssd / 100;

		fdsx = Integer.parseInt(dsx, 16);//十六进制转化为十进制
		fdsx = fdsx / 100;

		flxgl = Integer.parseInt(lxgl, 16);//十六进制转化为十进制
		flxgl = flxgl / 100;

		ffsx = Integer.parseInt(fsx, 16);//十六进制转化为十进制
		ffsx = ffsx / 100;
		/*String returnStr =  "---从机保护定值----\n" +
				"限时速断:" + fqssd + "A\n" + 
			   "定时限:" + fdsx + "A\n" + 
			   "零序过流:" + flxgl + "A\n" + 
			   "反时限:" + ffsx +"A";*/
		HashMap<String,String> bhdzMap = new HashMap<String, String>();
		bhdzMap.put("限时速断保护定值", String.valueOf(fqssd)+"A");
		bhdzMap.put("定时限保护定值", String.valueOf(fdsx)+"A");
		bhdzMap.put("零序过流保护定值", String.valueOf(flxgl+"A"));
		bhdzMap.put("反时限保护定值", String.valueOf(ffsx)+"A");
		return bhdzMap;
	}

	/**读取保护时限
	 * 主机	01	03	11	20	00	04	40  FF
	 从机				限时速断	定时限	零序过流	反时限	CRC16
	 01	03	08			00	04		00	04	00	0A		00	00	01 D5
	 限时速断 保护时限=0x0004=4	显示00.04 S
	 * @param str
	 * @return
	 */
	public static HashMap<String,String> getBhSq(String str) {
		// 限时速断
		String qssd = str.substring(6, 10);
		// 定时限
		String dsx = str.substring(10, 14);
		// 零序过流
		String lxgl = str.substring(14, 18);
		// 反时限
		String fsx = str.substring(18, 22);
		float fqssd = 0.0f, fdsx = 0.0f, flxgl = 0.0f, ffsx = 0.0f;
		fqssd = Integer.parseInt(qssd, 16);// 十六进制转化为十进制
		fqssd = fqssd / 100;

		fdsx = Integer.parseInt(dsx, 16);// 十六进制转化为十进制
		fdsx = fdsx / 100;

		flxgl = Integer.parseInt(lxgl, 16);// 十六进制转化为十进制
		flxgl = flxgl / 100;

		ffsx = Integer.parseInt(fsx, 16);// 十六进制转化为十进制
		ffsx = ffsx / 100;
		/*String returnStr = "---从机保护时限----\n" + "限时速断:" + fqssd + "S\n" + "定时限:"
				+ fdsx + "S\n" + "零序过流:" + flxgl + "S\n" + "反时限:" + ffsx + "S";*/
		HashMap<String,String> bhsxMap = new HashMap<String, String>();
		bhsxMap.put("限时速断保护时限", String.valueOf(fqssd)+"S");
		bhsxMap.put("定时限保护时限", String.valueOf(fdsx)+"S");
		bhsxMap.put("零序过流保护时限", String.valueOf(flxgl+"S"));
		bhsxMap.put("反时限保护时限", String.valueOf(ffsx)+"S");
		return bhsxMap;
	}

	public static String getStatus(String fourStr){
		if("0000".equals(fourStr)){
			fourStr = "退出";
		}else if("0001".equals(fourStr)){
			fourStr = "投入";
		}
		return fourStr;
	}


	/**
	 * 读取从机模拟量
	 *
	 * @param s
	 */
	public static String CJString(String s) {
		// s = "01 03 08 13 88 00 88 11 88 00 00 95 D7";
		int length = s.length();
		String rs = s.replace(" ", "");// 去除空格
		float fa=0.0f,fb=0.0f,fc=0.0f,fo=0.0f;
		if(length>=10){
			String IA = rs.substring(6, 10);
			try{
				if(!"0000".equals(IA)){
					fa= Integer.parseInt(IA, 16);//十六进制转化为十进制
					fa = fa / 1000;
				}

				if(length>=14){
					String IB = rs.substring(10, 14);
					if(!"0000".equals(IB)){
						fb = Integer.parseInt(IB, 16);
						fb = fb / 1000;
					}
				}

				if(length>=18){
					String IC = rs.substring(14, 18);
					if(!"0000".equals(IC)){
						fc = Integer.parseInt(IC, 16);
						fc = fc / 1000;
					}
				}

				if(length>=22){
					String IO = rs.substring(18, 22);
					if(!"0000".equals(IO)){
						fo = Integer.parseInt(IO, 16);
						fo = fo / 1000;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		// System.out.println(Integer.toHexString(5000));//toHexString
		// 把十进制转化为十六进制
		// System.out.println(Integer.parseInt("1388", 16));
		String retStr = "IA=" + fa +"A\n" + "IB=" + fb+"A\n"+"IC=" + fc+"A\n"+"IO=" + fo +"A";
		return retStr;
	}

	/**
	 * 获取开关量数据
	 *
	 * 发送： 01 03 12 20 00 01 80 B8 返回：01 03 02 00 00 B8 44 01 从机地址 03 命令 02
	 * 返回数据字节数 00 00 开关量数据 高8位在前 低8位在后 B8 44 ....... 例如：开关量数据为 00
	 * 03（16进制值=0x0003）(2进制值=0000 0011 B） 其中bit0=0显示为（外跳：0） bit0=1显示为（外跳：1）
	 * 其中bit1=0显示为（蓝牙：0） bit1=1显示为（蓝牙：1）
	 *
	 * @param s
	 *            01 03 02 00 03 B8 44 0103020003B844
	 */
	public static String switchLiang(String s) {
		s = s.replace(" ", "");// 去除空格
		String switchStr = s.substring(6, 10);// 开关量数据 高8位在前 低8位在
		// 转换成二进制
		String binaryStr = hexString2binaryString(switchStr);
		System.out.println(binaryStr);// 0x0003-->0000000000000011
		String last = binaryStr.substring(binaryStr.length() - 1,
				binaryStr.length());
		String lastBefore = binaryStr.substring(binaryStr.length() - 2,
				binaryStr.length() - 1);
		System.out.println("last=" + last + " lastBefore=" + lastBefore);
		String returnLast = "外跳：" + last;
		String returnLastBefore = "             蓝牙:" + lastBefore;
		String returnStr = "外跳：" + last + "             蓝牙:" + lastBefore;
		System.out.println(returnStr);
		return returnStr;
	}

	/**
	 * 16进制转二进制
	 *
	 * @param hexString
	 * @return
	 */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
					hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * 发送 01 03 12 00 00 06 C0 B0
	 * 收到01 03 0C 01 05 00 07 01 06 01 04 05 01 03 06 DF 6C
	 * 地址	命令	字节数	年H	年L	月H	月L	日H	日L	时H	时L	分H	分L	秒H	秒L	CRC16校验低8位   CRC16校验高8位
	 01	03	0C	    01	05	00	07	01	06	01	04	05	00	03	06	  DF	         6C
	 显示时间为     1   5年 0   7月  1   6日 1   4时 5   0分  3    6秒
	 * @param s
	 * @return
	 */
	public static String readCongjiDateTime(String s){
		//System.out.println(("01 03 0C 01 05 00 07 01 06 01 04 05 01 03 06 DF 6C").replace(" ", ""));
		//01030C010500070106010405010306DF6C
		//String s = ("01 03 0C 01 05 00 08 02 00 02 00 03 08 03 03 DF 6C").replace(" ", "");
		s = s.replace(" ", "");
		String year = getuCJTime(s.substring(6, 10));
		String month = getuCJTime(s.substring(10, 14));//0100=10月
		String day = getuCJTime(s.substring(14, 18));//0200->20 0205 = 25  0001 = 1 0100=10 0300=30
		String hour = getuCJTime(s.substring(18, 22));//小时00-24
		String minute = getuCJTime(s.substring(22, 26).replace("0", ""));//分钟0-60
		String seconds = getuCJTime(s.substring(26, 30).replace("0", ""));
		String returnStr =
				"时间："+year+
						"年"+ CRC16.addZeroForNum(month,2)+
						"月"+CRC16.addZeroForNum(day,2)+
						"日"+CRC16.addZeroForNum(hour,2)+
						"时"+CRC16.addZeroForNum(minute,2)+
						"分"+CRC16.addZeroForNum(seconds,2)+"秒";
		System.out.println(returnStr);
		//CRC16.addZeroForNum
		return returnStr;
	}

	public static String getuCJTime(String fourStr){
		if("0100".equals(fourStr)){//
			fourStr = "10";
		}else if("0200".equals(fourStr)){
			fourStr = "20";
		}else if("0300".equals(fourStr)){
			fourStr = "30";
		}else if("0400".equals(fourStr)){
			fourStr = "40";
		}else if("0500".equals(fourStr)){
			fourStr = "50";
		}else{
			fourStr = fourStr.replace("0", "");
		}
		return fourStr;
	}

	/**
	 * 01 03 13 FF 00 01 B0 BE
	 * 从机地址	命令	返回数据字节数	事件个数( 高8位在前 低8位在后)	CRC16校验低8位 CRC16校验高8位
	 01	03	 02	                  00	06	      38	       46
	 事件个数为 00 06（16进制值=0x0006）(10进制值=6）表示有6个事件记录
	 确定事件记录的编号为6、5、 4、 3、 2、 1
	 * 读从机事件个数
	 *
	 * @param s
	 * @return
	 */
	public static int readInterRecords(String s){
		//String s = "01 03 02 00 06 38 46";
		s = s .replace(" ", "");
		String countStr = s.substring(6, 10);
		int count = Integer.parseInt(countStr, 16);
		return count;
	}

	/**
	 *  发送数据：读取一条事件记录
	 * 从机	命令	寄存器地址高8位	寄存器地址低8位	事件编号高8位	事件编号低8位	CRC16校验低8位     CRC16校验高8位							
	 01	03	14				00			00			06			C0				38
	 接收数据：
	 地址	命令	字节数	Ecode	Eabc	Emax	Etot	年	月	日	时	分	秒	事件	          CRC16
	 01	 03	 0E	     3A	     01	     03	      05	00	01	01	00	00	35	00 00 00 68	  83  D0

	 * @param recordNo
	 * @return
	 * Integer.toBinaryString(int i) 转二进制

	Integer.toHexString(int i)转十六进制

	Integer.toOctalString(int i)转八进制
	 */
	public static String readInterRecordByNo(String s){
//		String noStr = Integer.toHexString(recordNo);
		s = s.replace(" ", "");
		String ecode = s.substring(6, 8);
		String ecodeStr="";
		if("31".equals(ecode)){
			ecodeStr = "蓝牙限时速断";
		}else if("32".equals(ecode)){
			ecodeStr = "蓝牙定时限";
		}else if("33".equals(ecode)){
			ecodeStr = "蓝牙零序";
		}else if("34".equals(ecode)){
			ecodeStr = "蓝牙反时限C1";
		}else if("35".equals(ecode)){
			ecodeStr = "蓝牙反时限C2";
		}else if("36".equals(ecode)){
			ecodeStr = "蓝牙反时限C3";
		}else if("37".equals(ecode)){
			ecodeStr = "蓝牙反时限C4";
		}else if("38".equals(ecode)){
			ecodeStr = "蓝牙反时限C5";
		}else if("39".equals(ecode)){
			ecodeStr = "拨码限时速断";
		}else if("3A".equals(ecode)){
			ecodeStr = "拨码定时限";
		}else if("3B".equals(ecode)){
			ecodeStr = "拨码零序";
		}else if("3C".equals(ecode)){
			ecodeStr = "拨码反时限C1";
		}else if("3D".equals(ecode)){
			ecodeStr = "拨码反时限C2";
		}else if("3E".equals(ecode)){
			ecodeStr = "拨码反时限C3";
		}else if("3F".equals(ecode)){
			ecodeStr = "拨码反时限C4";
		}else if("40".equals(ecode)){
			ecodeStr = "拨码反时限C5";
		}else if("41".equals(ecode)){
			ecodeStr = "外部跳闸";
		}

		//故障相
		String eabc = s.substring(8, 10);
		String eabcStr = "";
		if("00".equals(eabc)){
			eabcStr = "无故障相";
		}else if("01".equals(eabc)){
			eabcStr = "C相故障";
		}else if("02".equals(eabc)){
			eabcStr = "B相故障";
		}else if("03".equals(eabc)){
			eabcStr = "BC相故障";
		}else if("04".equals(eabc)){
			eabcStr = "A相故障";
		}else if("05".equals(eabc)){
			eabcStr = "AC相故障";
		}else if("06".equals(eabc)){
			eabcStr = "AB相故障";
		}else if("07".equals(eabc)){
			eabcStr = "ABC相故障";
		}

		//最大项
		String emax = s.substring(10, 12);
		String emaxStr="";
		if("00".equals(emax)){
			emaxStr = "最大故障相";
		}else if("01".equals(emax)){
			emaxStr = "A相";
		}else if("02".equals(emax)){
			emaxStr = "B相";
		}else if("03".equals(emax)){
			emaxStr = "C相";
		}else if("04".equals(emax)){
			emaxStr = "I0相";
		}

		//本类型故障发生的次数
		String etot =  s.substring(12, 14);
		//十六进制转十进制
		int etotTimes = Integer.parseInt(etot, 16);

		String year = CRC16.addZeroForNum(Integer.parseInt(s.substring(14, 16),16)+"",2);
		String month = CRC16.addZeroForNum(Integer.parseInt(s.substring(16, 18),16)+"",2);
		String day = CRC16.addZeroForNum(Integer.parseInt(s.substring(18, 20),16)+"",2);
		String hour = CRC16.addZeroForNum(Integer.parseInt(s.substring(20, 22),16)+"",2);
		String minute = CRC16.addZeroForNum(Integer.parseInt(s.substring(22, 24),16)+"",2);
		String seconds = CRC16.addZeroForNum(Integer.parseInt(s.substring(24, 26),16)+"",2);
		String bcd = year+"年"+ month+"月"+day+"日"+hour+"时"+minute+"分"+seconds+"秒";;

		//事件数据
		String data = s.substring(26, 34);
		int dataInt = Integer.parseInt(data, 16);

		String retStr =  bcd + "\n" +
				"事件Ecode："+ ecodeStr + "\n" +
				"故障相Ecabc："+ eabcStr + "\n" +
				"最大相Emax："+ emaxStr + "\n"+
				"累计Etot:"+ etotTimes + "次\n" +
				"数据：" + dataInt+"\n";
		return retStr;
	}

	/**
	 * 设置保护定值，设置保护时限组合命令
	 * 1110 0500-->01 10 11 10 00 01 02 01 F4 CRC16
	 * @param str
	 * @return
	 */
	public static String ConcatOrder(String str){
		str=str.replace(" ", "");
		String lastFour = str.substring(4, 8);
		String hexLastFour = CRC16.addZeroForNum(Integer.toHexString(Integer.valueOf(lastFour)), 4);
		String order = "0110"+str.substring(0, 4)+"000102"+hexLastFour.replace(" ", "").toUpperCase(); //before
		return order;
	}

	/**
	 * 1200 0105 0007 0103 0107 0009 0208 组合设置时间命令
	 * -->01 10	12	00	00	06	0C	01	05	00	07	01	03	01	07	00	09	02	08
	 * @param str
	 * @return
	 */
	public static String concatSetTime(String str){
		String order = "0110"+str.substring(0, 4) +"00060C"+ str.substring(4, 28);
		return order;
	}

	/**
	 * 设置从机时间:返回设置字符串
	 * @param s 01	10	12	00	00	06	0C	01	05	00	07	01	03	01	07	00	09	02	08	FA  34
	 * @return 15年07月13日17时09分28秒
	 */
	public static String setCongjiDateTime(String s){
		String year = getuCJTime(s.substring(14, 18));
		String month = getuCJTime(s.substring(18, 22));//0100=10月
		String day = getuCJTime(s.substring(22, 26));//0200->20 0205 = 25  0001 = 1 0100=10 0300=30
		String hour = getuCJTime(s.substring(26, 30));//小时00-24
		String minute = getuCJTime(s.substring(30, 34).replace("0", ""));//分钟0-60
		String seconds = getuCJTime(s.substring(34, 38).replace("0", ""));
		String returnStr = "设置从机时间为："+year+"年"+ month+"月"+day+"日"+hour+"时"+minute+"分"+seconds+"秒成功";
		return returnStr;
	}




}
