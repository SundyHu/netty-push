package com.molbase.dts.util;

/**
 * @author kulen
 * @createTime Aug 4, 2014 5:53:38 PM
 * @desc
 */
public class StringUtil {

	public static synchronized String getStackTrace(Exception exception) {
		StringBuffer sb = new StringBuffer();
		sb.append(exception.getMessage());
		sb.append("\r\n");
		for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
			sb.append("        ");
			sb.append(stackTraceElement.toString());
			sb.append("\r\n");
		}
		return sb.toString();
	}

	public static synchronized Integer getNumber(String value, Integer defaultValue) {
		Integer result = defaultValue;
		try {
			result = Integer.parseInt(value);
		} catch (Exception e) {

		}
		return result;
	}

	public static String getFixLenNumStr(int len, int value, String fillStr) {
		return getFixLenNumStr(len, value + "", fillStr);
	}

	public static synchronized String getFixLenNumStr(int len, String value, String fillStr) {
		String resultValue = value;
		while (resultValue.length() < len) {
			resultValue = fillStr + resultValue;
		}
		return resultValue;
	}

	public static synchronized boolean isEmpty(Object v) {
		return v == null || v.toString().trim().length() == 0;
	}

	public static synchronized String getNumber(String v) {
		String ns = "";
		for (int i = 0; i < v.length(); i++) {
			try {
				ns += Integer.parseInt(v.charAt(i) + "") + "";
			} catch (Exception e) {

			}
		}
		return ns;
	}

	public static synchronized String getNumber(String v, String defaultVal) {
		String ns = getNumber(v);
		if (ns == null || ns.length() == 0) {
			ns = defaultVal;
		}
		return ns;
	}

	public static synchronized String decodeUnicode(String v) {
		char aChar;
		int len = v.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = v.charAt(x++);
			if (aChar == '\\') {
				aChar = v.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = v.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
