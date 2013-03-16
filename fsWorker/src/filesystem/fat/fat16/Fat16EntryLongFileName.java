package filesystem.fat.fat16;

import java.io.IOException;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.fat.FatEntry;
import filesystem.fat.FileAllocationTable;
import filesystem.io.DataConverter;
import filesystem.io.FileSystemIO;

//http://www.maverick-os.dk/FileSystemFormats/VFAT_LongFileNames.html

//VFAT (Virtual FAT)
//VFAT, a variant of FAT with an extended directory format,
//was introduced in Windows 95 and Windows NT 3.5. It allowed
//mixed-case Unicode long filenames (LFNs) in addition to
//classic 8.3 names.

//To maintain backward-compatibility with legacy applications
//(on DOS and Windows 3.1), on FAT and VFAT filesystems an 8.3
//filename is automatically generated for every LFN, through which
//the file can still be renamed, deleted or opened; on NTFS
//filesystems the generation of 8.3 filenames can be turned off[2].
//The 8.3 filename can be obtained using the Kernel32.dll function
//GetShortPathName.

//VFAT long file names
//VFAT Long File Names (LFN) are stored on a FAT file system using a
//trick—adding (possibly multiple) additional entries into the directory
//before the normal file entry. The additional entries are marked with
//the Volume Label, System, Hidden, and Read Only attributes (yielding 0x0F),
//which is a combination that is not expected in the MS-DOS environment,
//and therefore ignored by MS-DOS programs and third-party utilities.

public class Fat16EntryLongFileName extends FatEntry {
	private boolean isLast;
	private boolean isDeleted;
	private char LFNNumber;
	private char unicodeChar[] = new char[13];
	private String unicodeString = "";

	public Fat16EntryLongFileName(BootBlock bootBlock, FileAllocationTable fileAllocationTable, DataRegion dataRegion, char entryNumber, long entryAddress,
			FileSystemIO fileSystemIO) throws IOException, NotEnoughBytesReadException {
		super(bootBlock, fileAllocationTable, dataRegion, entryNumber, entryAddress, fileSystemIO);

		readLFNNumber();
	}

	public void readLFNNumber() throws IOException, NotEnoughBytesReadException {
		//byte ordinalField = buffer[(int) entryAddress + 0];
		byte buffer[] = fileSystemIO.readFSImage(entryAddress + 0, 1);
		byte ordinalField = buffer[0];

		//System.out.println("ordinalField = : " + ordinalField);
		//System.out.printf("ordinalField: 0x%02Xh\n", ordinalField);

		byte LFNNumberByte = (byte) (ordinalField & 0x3F);

		//System.out.println("LFNNumberByte = : " + LFNNumberByte);
		//System.out.printf("LFNNumberByte: 0x%02Xh\n", LFNNumberByte);

		LFNNumber = DataConverter.getValueFrom1Byte(LFNNumberByte);

		//System.out.println("LFNNumber = : " + (int) LFNNumber);
		//System.out.printf("LFNNumber: 0x%02Xh\n", (int) LFNNumber);

		isLast = (ordinalField & 0x40) != 0;

		//System.out.println("isLast = : " + isLast);
		
		isDeleted = (ordinalField & 0x80) != 0;
		
		//System.out.println("isDeleted = : " + isDeleted);
		
		//Read unicode character 1 to 5
		buffer = fileSystemIO.readFSImage(entryAddress + 1, 10);
		
		
		for (int i = 0; i < 5; i++)
		{
			unicodeChar[i] = DataConverter.getValueFrom2Bytes(buffer, 2*i);
		}
		
		//Read unicode character 6 to 11
		buffer = fileSystemIO.readFSImage(entryAddress + 14, 12);
		for (int i = 0; i < 6; i++)
		{
			unicodeChar[5+i] = DataConverter.getValueFrom2Bytes(buffer, 2*i);
		}
		
		//Read unicode character 12 to 13
		buffer = fileSystemIO.readFSImage(entryAddress + 28, 4);
		for (int i = 0; i < 2; i++)
		{
			unicodeChar[5+6+i] = DataConverter.getValueFrom2Bytes(buffer, 2*i);
		}
		
		//System.out.println("unicodeChar:");
		//for (int i = 0; i < unicodeChar.length; i++)
		//	System.out.print((int)unicodeChar[i] + " ");
		//System.out.println();
		
		//Unicode character array to string
		for (int i = 0; i < unicodeChar.length; i++)
		{
			unicodeString += unicodeChar[i];
			
			if ((int)unicodeChar[i] == 0)
				break;
		}
		
		//System.out.println("unicodeString: " + unicodeString);
		
		//Flag byte is on the same offset as in 8.3 filename
		//as VFAT needs to maintain backwards compatibility
		//0Bh	1 bytes	Flag byte
		
		//System.out.println();
	}

	public boolean isLast() {
		return isLast;
	}

	public char getLFNNumber() {
		return LFNNumber;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public String getUnicodeString() {
		return unicodeString;
	}
}
