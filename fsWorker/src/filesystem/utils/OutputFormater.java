package filesystem.utils;

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
}
