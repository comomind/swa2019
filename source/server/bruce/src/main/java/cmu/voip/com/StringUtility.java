package cmu.voip.com;

import java.util.Calendar;

public class StringUtility {

	public static void main(String[] args) {
		System.out.println(StringUtility.getStringPadding("abcdef",10));
		System.out.println(StringUtility.getStringPadding("abcdefghijk",3));
		System.out.println(StringUtility.getNumberPadding(10000,6));
		System.out.println(StringUtility.getNumberPadding(10000,3));
		System.out.println(Calendar.getInstance().getTimeInMillis());
	}
	
	public static String getString0LeftPadding(String value, int length) {
		
		String result = value;
		
		int lengthOfValue = value.length();

		if (lengthOfValue < length) {
			for (int i = lengthOfValue; i < length; i++) {
				result = "0" + result;
			}
		} else {
			result = result.substring(0, length);
		}

		return result;
	}

	public static String getStringPadding(String value, int length) {
		
		String result = value;
		
		int lengthOfValue = value.length();

		if (lengthOfValue < length) {
			for (int i = lengthOfValue; i < length; i++) {
				result = result + " ";
			}
		} else {
			result = result.substring(0, length);
		}

		return result;
	}
	
	public static String getNumberPadding(long value, int length) {
		String result = "";
		
		result = new Long(value).toString();
		
		int lengthOfValue = result.length();

		if (lengthOfValue < length) {
			for (int i = lengthOfValue; i < length; i++) {
				result = "0"+result;
			}
		} else {
			result = result.substring(0, length);
		}

		return result;
	}
}
