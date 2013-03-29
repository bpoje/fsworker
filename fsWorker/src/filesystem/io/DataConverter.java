package filesystem.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

//There are several code fragments in this document that freely mix 32-bit and 16-bit data elements. It is
//assumed that you are a programmer who understands how to properly type such operations so that
//data is not lost due to truncation of 32-bit values to 16-bit values.

//Also take note that all data types are UNSIGNED.

//FAT file system on disk data structure is all “little endian.”
public class DataConverter {
	//Convert 1 byte to java char data type (2 bytes, UNSIGNED, Unicode, 0 to 65,535)
	public static char getValueFrom1Byte(byte [] buffer, int offsetByte)
	{
		byte temp[] = new byte[2];
		temp[0] = buffer[offsetByte];
		temp[1] = 0;
		
		//for (int i = 0; i < temp.length; i++)
		//	System.out.printf("0x%02X\n", temp[i]);
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(temp);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		char value = byteBuffer.getChar();
		return value;
	}
	
	public static char getValueFrom1Byte(byte oneByte)
	{
		byte temp[] = new byte[2];
		temp[0] = oneByte;
		temp[1] = 0;
		
		//for (int i = 0; i < temp.length; i++)
		//	System.out.printf("0x%02X\n", temp[i]);
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(temp);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		char value = byteBuffer.getChar();
		return value;
	}
	
	//Convert 2 bytes to java char data type (2 bytes, UNSIGNED, Unicode, 0 to 65,535)
	public static char getValueFrom2Bytes(byte [] buffer, int offsetByte)
	{
		byte subArray [] = Arrays.copyOfRange(buffer, offsetByte, offsetByte + 2);
		ByteBuffer byteBuffer = ByteBuffer.wrap(subArray);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		char value = byteBuffer.getChar();
		return value;
	}
	
	//Convert 4 bytes to java long data type (8 bytes signed)
	public static long getValueFrom4Bytes(byte [] buffer, int offsetByte)
	{
		byte temp[] = new byte[8];
		temp[0] = buffer[offsetByte];
		temp[1] = buffer[offsetByte + 1];
		temp[2] = buffer[offsetByte + 2];
		temp[3] = buffer[offsetByte + 3];
		temp[4] = 0;
		temp[5] = 0;
		temp[6] = 0;
		temp[7] = 0;
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(temp);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		long value = byteBuffer.getLong();
		return value;
	}
	
	//Convert to String
	public static String getStringFrom3Bytes(byte [] buffer, int offsetByte)
	{
		byte subArray [] = Arrays.copyOfRange(buffer, offsetByte, offsetByte + 3);
		String value = new String(subArray);
		return value;
	}
	
	//Convert to String
	public static String getStringFrom8Bytes(byte [] buffer, int offsetByte)
	{
		byte subArray [] = Arrays.copyOfRange(buffer, offsetByte, offsetByte + 8);
		String value = new String(subArray);
		return value;
	}
	
	//Convert java char data type (2 bytes, UNSIGNED, Unicode, 0 to 65,535) to 2 bytes
	public static byte [] get2BytesFromValue(char value)
	{
		byte temp[] = new byte[2];
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(temp);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		byteBuffer.putChar(value);
		
		return byteBuffer.array();
	}
}
