package fat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class LongFileNameEntry extends RootDirectoryEntry {
	
	private boolean isLast;
	private char LFNNumber;
	
	public LongFileNameEntry(char entryNumber, long entryAddress, byte buffer[])
	{
		super(entryNumber, entryAddress, buffer);
		
		readLFNNumber(buffer);
	}
	
	public void readLFNNumber(byte buffer[])
	{
		byte ordinalField = buffer[(int)entryAddress + 0];
		
		System.out.println("ordinalField = : " + ordinalField);
		System.out.printf("ordinalField: 0x%02Xh\n", ordinalField);
		
		byte LFNNumberByte = (byte) (ordinalField & 0x3F);
		
		System.out.println("LFNNumberByte = : " + LFNNumberByte);
		System.out.printf("LFNNumberByte: 0x%02Xh\n", LFNNumberByte);
		
		LFNNumber = DataConverter.getValueFrom1Byte(LFNNumberByte);
		
		System.out.println("LFNNumber = : " + (int)LFNNumber);
		System.out.printf("LFNNumber: 0x%02Xh\n", (int)LFNNumber);
		
		isLast = (ordinalField & 0x40) != 0;
		
		System.out.println("isLast = : " + isLast);
	}

	public boolean isLast() {
		return isLast;
	}

	public char getLFNNumber() {
		return LFNNumber;
	}
}
