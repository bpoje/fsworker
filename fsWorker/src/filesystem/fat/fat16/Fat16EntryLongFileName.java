package filesystem.fat.fat16;

import java.io.IOException;

import fat.DataConverter;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.FatEntry;
import filesystem.io.FileSystemIO;

public class Fat16EntryLongFileName extends FatEntry {
	private boolean isLast;
	private char LFNNumber;

	public Fat16EntryLongFileName(char entryNumber, long entryAddress,
			FileSystemIO fileSystemIO) throws IOException, NotEnoughBytesReadException {
		super(entryNumber, entryAddress, fileSystemIO);

		readLFNNumber();
	}

	public void readLFNNumber() throws IOException, NotEnoughBytesReadException {
		//byte ordinalField = buffer[(int) entryAddress + 0];
		byte buffer[] = fileSystemIO.readFSImage(entryAddress + 0, 1);
		byte ordinalField = buffer[0];

		System.out.println("ordinalField = : " + ordinalField);
		System.out.printf("ordinalField: 0x%02Xh\n", ordinalField);

		byte LFNNumberByte = (byte) (ordinalField & 0x3F);

		System.out.println("LFNNumberByte = : " + LFNNumberByte);
		System.out.printf("LFNNumberByte: 0x%02Xh\n", LFNNumberByte);

		LFNNumber = DataConverter.getValueFrom1Byte(LFNNumberByte);

		System.out.println("LFNNumber = : " + (int) LFNNumber);
		System.out.printf("LFNNumber: 0x%02Xh\n", (int) LFNNumber);

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
