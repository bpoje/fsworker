package filesystem.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;

public class OutputFormater {
	
	public static void printArrayHex(byte [] byteArray)
	{
		printArrayHex(byteArray, "");
	}
	
	public static void printArrayHex(byte [] byteArray, String title)
	{
		System.out.println(title);
		for (int i = 0; i < byteArray.length; i++)
			System.out.printf("0x%02Xh ", byteArray[i]);
		System.out.println();
	}
	
	public static void printArrayHex(byte [] byteArray, String title, String prefix)
	{
		System.out.println(prefix + title);
		System.out.print(prefix);
		for (int i = 0; i < byteArray.length; i++)
			System.out.printf("0x%02Xh ", byteArray[i]);
		System.out.println();
	}
	
	public static String formatOutput(long numberOfBytes)
	{
		StringBuffer output = new StringBuffer();
		
        DecimalFormat df = new DecimalFormat("#.##");
		
		output.append(Long.toString(numberOfBytes) + " B");
		
		if (numberOfBytes >= 1024)
		{
			double KB = (double)numberOfBytes / (double)1024;
			
			output.append(" = " + df.format(KB) + " KB");
			
			if (numberOfBytes >= (long)1024 * (long)1024)
			{
				double MB = (double)KB / (double)1024;
				
				output.append(" = " + df.format(MB) + " MB");
			}
		}
		
		return output.toString();
	}
	
	public static String byteArrayToHexString(byte[] byteArray)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		for(byte b : byteArray)
			stringBuilder.append(String.format("0x%02x ", b & 0xff));
		
		return stringBuilder.toString();
	}
	
	public static String byteToHexString(byte byteValue)
	{
		return String.format("0x%02x", byteValue & 0xff);
	}
	
	public static String longToHexString(long longValue)
	{
		byte byteArray[] = new byte[8];
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putLong(longValue);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("0x");
		
		for(byte b : byteArray)
			stringBuilder.append(String.format("%02x", b & 0xff));
		
		return stringBuilder.toString();
	}
	
	public static String charToHexString(char charValue)
	{
		byte byteArray[] = new byte[2];
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putChar(charValue);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("0x");
		
		for(byte b : byteArray)
			stringBuilder.append(String.format("%02x", b & 0xff));
		
		return stringBuilder.toString();
	}
}
