package cmu.voip.com;

import java.util.Random;

public class CommonUtility {


	public static String getRandomPhoneNumber() {
		Random rand = new Random();
		
		int phoneNumber = rand.nextInt(10000);
		
		return StringUtility.getNumberPadding(phoneNumber,4);
	}
}
