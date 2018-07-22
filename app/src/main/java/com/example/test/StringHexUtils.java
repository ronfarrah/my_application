package com.example.test;

import java.io.ByteArrayOutputStream;

import android.util.Log;

public class StringHexUtils {

	public static void main(String[] args) {
		String s = "01 03 10 00 00 04 40 C9";
		// byte[] b = s.getBytes();
		// System.out.println(s.getBytes());
		String hexStr = encode(s);
		String str2HexStr = str2HexStr(s);
		byte[] b = str2HexStr.getBytes();


		byte[] c = getHexBytes(s.replace(" ", ""));
		String d = bytesToHexString(c);

		System.out.println(str2HexStr);
		System.out.println(hexStr);
		// byte[] b = hexStr.getBytes();
		System.out.println(b);

		System.out.println(Bytes2HexString(s.getBytes()));
		System.out.println(hexStr);
		System.out.println(s);
		System.out.println(s.trim());// trim去除首尾的空格，replace可以去除所有的空格
		System.out.println(s.replace(" ", ""));

		// System.out.println(hexStr);
		System.out.println(encode(s));
		;
		System.out
				.println(decode("3031203033203130203030203030203034203430204339"));
		System.out.println(encode("01 03 08 00 00 00 00 00 00 00 00 95 D7"));
		// 3031203033203038203030203030203030203030203030203030203030203030203935204437
		System.out
				.println(decode("3031203033203038203030203030203030203030203030203030203030203030203935204437"));
		// 01 03 08 00 00 00 00 00 00 00 00 95 D7
		// System.out.println(hexStr);
		// String normalStr = toStringHex(s);
		// System.out.println(normalStr);
	}

	// 转化字符串为十六进制编码
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	/**
	 * 转化十六进制编码为字符串
	 *
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * 16进制数字字符
	 */
	private static String hexString = "0123456789ABCDEF";

	/**
	 * 将字符串编码转化为16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/**
	 * 16进制数字解码成字符串,适用于所有字符（包括中文)
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static byte[] hexStr2Bytes(String paramString) {
		int i = paramString.length() / 2;
		System.out.println(i);
		byte[] arrayOfByte = new byte[i];
		int j = 0;
		while (true) {
			if (j >= i)
				return arrayOfByte;
			int k = 1 + j * 2;
			int l = k + 1;
			arrayOfByte[j] = (byte) (0xFF & Integer.decode(
					"0x" + paramString.substring(j * 2, k)
							+ paramString.substring(k, l)).intValue());
			++j;
		}
	}

	public static String hexStr2Str(String paramString) {
		char[] arrayOfChar = paramString.toCharArray();
		byte[] arrayOfByte = new byte[paramString.length() / 2];
		int i = 0;
		while (true) {
			if (i >= arrayOfByte.length)
				return new String(arrayOfByte);
			arrayOfByte[i] = (byte) (0xFF & 16
					* "0123456789ABCDEF".indexOf(arrayOfChar[(i * 2)])
					+ "0123456789ABCDEF".indexOf(arrayOfChar[(1 + i * 2)]));
			++i;
		}
	}

	/**
	 * 字符串转换成十六进制字符
	 *
	 * @param String
	 *            str 待转换的ASCII字符
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 字符串转16进制 01 03 10 00 00 04 40 C9
	 *
	 * @param message
	 * @return
	 */
	public static byte[] getHexBytes(String message) {
		int len = message.length() / 2;
		char[] chars = message.toCharArray();
		String[] hexStr = new String[len];
		byte[] bytes = new byte[len];
		for (int i = 0, j = 0; j < len; i += 2, j++) {
			hexStr[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
		}
		return bytes;
	}

	/**
	 * 字符串转16进制 01 03 10 00 00 04 40 C9
	 *
	 * @param message
	 * @return
	 */
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

	/**
	 * 字节数组转化为字符串（无空格)
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xFF);
			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		return result;
	}
}
