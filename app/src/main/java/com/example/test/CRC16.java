package com.example.test;


public class CRC16 {
	public static byte[] crc(byte[] data) {
		byte[] temdata = new byte[data.length + 2];
		// unsigned char alen = *aStr – 2; //CRC16只计算前两部分
		int xda, xdapoly;
		int i, j, xdabit;
		xda = 0xFFFF;
		xdapoly = 0xA001; // (X**16 + X**15 + X**2 + 1)
		for (i = 0; i < data.length; i++) {
			xda ^= data[i];
			for (j = 0; j < 8; j++) {
				xdabit = (int) (xda & 0x01);
				xda >>= 1;
				if (xdabit == 1)
					xda ^= xdapoly;
			}
		}
		System.arraycopy(data, 0, temdata, 0, data.length);
		temdata[temdata.length - 2] = (byte) (xda & 0xFF);
		temdata[temdata.length - 1] = (byte) (xda >> 8);
		return temdata;
	}

	public static int[] getHexInts(String message) {
		int len = message.length() / 2;
		char[] chars = message.toCharArray();
		String[] hexStr = new String[len];
		int[] bytes = new int[len];
		for (int i = 0, j = 0; j < len; i += 2, j++) {
			hexStr[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = Integer.parseInt(hexStr[j], 16);
		}
		return bytes;
	}

	public static String getOrderByShort(String str){
		int[] tmpByte =  getHexInts(str);
		tmpByte = CRC16.crc(tmpByte); //计算CRC
		String[] strs = new String[tmpByte.length];
		String tmp = "";
		for(int i=0;i<tmpByte.length;i++){//[1, 3, 20, 0, 0, 3, 0, 59]
			strs[i] = Integer.toHexString(tmpByte[i]).replace("ffffff", "");
			strs[i] = addZeroForNum(strs[i],2).toUpperCase();
			tmp+=strs[i];
		}
		return tmp;
	}

	public static int[] crc(int[] data) {
		int[] temdata = new int[data.length + 2];
		// unsigned char alen = *aStr – 2; //CRC16只计算前两部分
		int xda, xdapoly;
		int i, j, xdabit;
		xda = 0xFFFF;
		xdapoly = 0xA001; // (X**16 + X**15 + X**2 + 1)
		for (i = 0; i < data.length; i++) {
			xda ^= data[i];
			for (j = 0; j < 8; j++) {
				xdabit = (int) (xda & 0x01);
				xda >>= 1;
				if (xdabit == 1)
					xda ^= xdapoly;
			}
		}
		System.arraycopy(data, 0, temdata, 0, data.length);
		temdata[temdata.length - 2] = (byte) (xda & 0xFF);
		temdata[temdata.length - 1] = (byte) (xda >> 8);
		return temdata;
	}

	// 这个主函数用来测试用的
	// int[] bytes = new int[]{0x01, 0x03, 0x14, 0x00, 0x00, 0x06};
	public static void main(String args[]) {
		// int[] data={0x01,0x03,0x14,0x00,0x00,0x05};//1 3 14 0 0 5 80 39
		// int[] data={0x01,0x03,0x14,0x00,0x00,0x04};//1 3 14 0 0 4 41 f9
		byte[] data = { 0x01, 0x10, 0x12, 0x00, 0x00, 0x06, 0x0C, 0x01, 0x05, 0x00, 0x07
				, 0x01, 0x03, 0x01, 0x07, 0x00, 0x09, 0x02, 0x08};// 1 3 14 0 0 3 0 3b
		byte[] d1 = crc(data);
		String[] str = new String[d1.length];
		String tmp="";
		for(int i=0;i<d1.length;i++){
			str[i] = String.valueOf(d1[i]);
			str[i] = addZeroForNum(str[i],2);
			tmp+=str[i];
		}
		System.out.println(tmp);
		/*for(int i=0;i<d1.length;i++){//[1, 3, 20, 0, 0, 3, 0, 59]
			str[i] = Integer.toHexString(d1[i]);
			str[i] = addZeroForNum(str[i],2).toUpperCase();
			//System.out.println(str[i]);
			d2[i] = Byte.parseByte(str[i]);
		}*/

	}

	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		StringBuffer sb = null;
		while (strLen < strLength) {
			sb = new StringBuffer();
			sb.append("0").append(str);// 左(前)补0
			str = sb.toString();
			strLen = str.length();
		}
		return str;
	}

}