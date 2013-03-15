package filesystem.fat;

import java.io.IOException;
import java.io.RandomAccessFile;

import fat.DataConverter;
import filesystem.FileSystem;
import filesystem.FileSystemType;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.io.FileSystemIO;

public abstract class BootBlock {

	// Common
	protected String BS_OEMName;
	protected char BPB_BytsPerSec;
	protected char BPB_SecPerClus;
	protected char BPB_RsvdSecCnt;
	protected char BPB_NumFATs;
	protected char BPB_RootEntCnt;
	protected char BPB_TotSec16;
	protected char BPB_Media;
	protected char BPB_FATSz16;
	protected char BPB_SecPerTrk;
	protected char BPB_NumHeads;
	protected long BPB_HiddSec;
	protected long BPB_TotSec32;

	// number of sectors in data region
	//long numberOfDataSectors;

	// number of clusters in data region
	//private long CountofClustersInDataRegion;

	//private FileSystemType type;

	// count of sectors occupied by ONE FAT
	//private long FATSz;

	// FAT12 or FAT16

	// FAT32
	//private long BPB_FATSz32;

	public BootBlock(FileSystemIO fileSystemIO) throws IOException,
			NotEnoughBytesReadException {
		
		//byte buffer[] = new byte[36];
		//fs.read(buffer);
		byte buffer[] = fileSystemIO.readFSImage(0, 36);

		initMemberVariables(buffer);

		// BPB_FATSz16
		// This field is the FAT12/FAT16 16-bit count of sectors occupied by
		// ONE FAT. On FAT32 volumes this field must be 0, and
		// BPB_FATSz32 contains the FAT size count.

		
		
		
		/*
		// FAT12 or FAT16
		if (BPB_FATSz16 != 0) {
			buffer = new byte[26];
			fs.read(buffer);
		} else // FAT32
		{
			buffer = new byte[54];
			fs.read(buffer);

			// BPB_FATSz32
			// This field is only defined for FAT32 media and does not exist on
			// FAT12 and FAT16 media. This field is the FAT32 32-bit count of
			// sectors occupied by ONE FAT. BPB_FATSz16 must be 0.

			BPB_FATSz32 = DataConverter.getValueFrom4Bytes(buffer, 0);
			System.out.println("BPB_FATSz32: " + BPB_FATSz32);
		}
		
		// FAT Type Determination
		// The FAT type —one of FAT12, FAT16, or FAT32— is determined by the
		// count of clusters on the volume and nothing else.

		// determine the count of sectors occupied by the root directory
		// RootDirSectors = ((BPB_RootEntCnt * 32) + (BPB_BytsPerSec – 1)) /
		// BPB_BytsPerSec;
		long RootDirSectors = (((long) BPB_RootEntCnt * 32) + ((long) BPB_BytsPerSec - 1))
				/ (long) BPB_BytsPerSec;
		System.out.println("RootDirSectors: " + RootDirSectors);

		// determine the count of sectors in the data region of the volume

		// BPB_FATSz16
		// This field is the FAT12/FAT16 16-bit count of sectors occupied by
		// ONE FAT. On FAT32 volumes this field must be 0, and
		// BPB_FATSz32 contains the FAT size count.

		// BPB_FATSz32
		// This field is only defined for FAT32 media and does not exist on
		// FAT12 and FAT16 media. This field is the FAT32 32-bit count of
		// sectors occupied by ONE FAT. BPB_FATSz16 must be 0.

		// long FATSz;
		if (BPB_FATSz16 != 0)
			FATSz = BPB_FATSz16;
		else
			FATSz = BPB_FATSz32;

		long TotSec;
		if (BPB_TotSec16 != 0)
			TotSec = BPB_TotSec16;
		else
			TotSec = BPB_TotSec32;

		// the count of sectors in data region
		numberOfDataSectors = TotSec
				- (BPB_RsvdSecCnt + (BPB_NumFATs * FATSz) + RootDirSectors);
		System.out.println("numberOfDataSectors: " + numberOfDataSectors);

		// determine the count of clusters in data region
		CountofClustersInDataRegion = numberOfDataSectors / BPB_SecPerClus;
		System.out.println("CountofClusters: " + CountofClustersInDataRegion);

		// Now we can determine the FAT type
		if (CountofClustersInDataRegion < 4085) {
			// Volume is FAT12
			type = FileSystemType.FAT12;
			System.out.println("Volume is FAT12");
		} else if (CountofClustersInDataRegion < 65525) {
			// Volume is FAT16
			type = FileSystemType.FAT16;
			System.out.println("Volume is FAT16");
		} else {
			// Volume is FAT32
			type = FileSystemType.FAT32;
			System.out.println("Volume is FAT32");
		}

		// This is the one and only way that FAT type is determined. There is no
		// such thing as a FAT12 volume
		// that has more than 4084 clusters. There is no such thing as a FAT16
		// volume that has less than 4085
		// clusters or more than 65,524 clusters. There is no such thing as a
		// FAT32 volume that has less than
		// 65,525 clusters. If you try to make a FAT volume that violates this
		// rule, Microsoft operating systems
		// will not handle them correctly because they will think the volume has
		// a different type of FAT than
		// what you think it does.
		*/
	}

	void initMemberVariables(byte[] buffer) {
		// BS_OEMName
		// It is only a name string. Microsoft operating systems don’t pay any
		// attention to this field. Some FAT drivers do.
		BS_OEMName = DataConverter.getStringFrom8Bytes(buffer, 3);
		System.out.println("BS_OEMName: " + BS_OEMName);

		// for (int i = 0; i < subArray.length; i++)
		// System.out.printf("0x%02X\n", subArray[i]);

		// BPB_BytsPerSec
		// Count of bytes per sector. This value may take on only the
		// following values: 512, 1024, 2048 or 4096.
		BPB_BytsPerSec = DataConverter.getValueFrom2Bytes(buffer, 11);
		System.out.println("BPB_BytsPerSec: " + (int) BPB_BytsPerSec);

		// BPB_SecPerClus
		// Number of sectors per allocation unit. This value must be a power
		// of 2 that is greater than 0. The legal values are 1, 2, 4, 8, 16, 32,
		// 64,
		// and 128.
		BPB_SecPerClus = DataConverter.getValueFrom1Byte(buffer, 13);
		System.out.println("BPB_SecPerClus: " + (int) BPB_SecPerClus);

		// BPB_RsvdSecCnt
		// Number of reserved blocks. This is the number of blocks on the disk
		// that are not actually part of the file system; in most cases this is
		// exactly 1, being the allowance for the boot block.

		// Number of reserved sectors in the Reserved region of the volume
		// starting at the first sector of the volume. This field must not be 0.
		// For FAT12 and FAT16 volumes, this value should never be
		// anything other than 1. For FAT32 volumes, this value is typically 32.
		BPB_RsvdSecCnt = DataConverter.getValueFrom2Bytes(buffer, 14);
		System.out.println("BPB_RsvdSecCnt: " + (int) BPB_RsvdSecCnt);

		// BPB_NumFATs
		// The count of FAT data structures on the volume. This field should
		// always contain the value 2 for any FAT volume of any type.
		// Although any value greater than or equal to 1 is perfectly valid,
		// many software programs and a few operating systems’ FAT file
		// system drivers may not function properly if the value is something
		// other than 2. All Microsoft file system drivers will support a value
		// other than 2, but it is still highly recommended that no value other
		// than 2 be used in this field.

		// The reason the standard value for this field is 2 is to provide
		// redundancy
		// for the FAT data structure so that if a sector goes bad in one
		// of the FATs, that data is not lost because it is duplicated in the
		// other
		// FAT. On non-disk-based media, such as FLASH memory cards,
		// where such redundancy is a useless feature, a value of 1 may be
		// used to save the space that a second copy of the FAT uses, but
		// some FAT file system drivers might not recognize such a volume
		// properly.

		BPB_NumFATs = DataConverter.getValueFrom1Byte(buffer, 16);
		System.out.println("BPB_NumFATs: " + (int) BPB_NumFATs);

		// BPB_RootEntCnt
		BPB_RootEntCnt = DataConverter.getValueFrom2Bytes(buffer, 17);
		System.out.println("BPB_RootEntCnt: " + (int) BPB_RootEntCnt);

		// BPB_TotSec16
		BPB_TotSec16 = DataConverter.getValueFrom2Bytes(buffer, 19);
		System.out.println("BPB_TotSec16: " + (int) BPB_TotSec16);

		// BPB_Media
		BPB_Media = DataConverter.getValueFrom1Byte(buffer, 21);
		System.out.println("BPB_Media: " + (int) BPB_Media);

		// BPB_FATSz16
		// This field is the FAT12/FAT16 16-bit count of sectors occupied by
		// ONE FAT. On FAT32 volumes this field must be 0, and
		// BPB_FATSz32 contains the FAT size count.
		BPB_FATSz16 = DataConverter.getValueFrom2Bytes(buffer, 22);
		System.out.println("BPB_FATSz16: " + (int) BPB_FATSz16);

		// BPB_SecPerTrk
		BPB_SecPerTrk = DataConverter.getValueFrom2Bytes(buffer, 24);
		System.out.println("BPB_SecPerTrk: " + (int) BPB_SecPerTrk);

		// BPB_NumHeads
		BPB_NumHeads = DataConverter.getValueFrom2Bytes(buffer, 26);
		System.out.println("BPB_NumHeads: " + (int) BPB_NumHeads);

		// BPB_HiddSec
		BPB_HiddSec = DataConverter.getValueFrom4Bytes(buffer, 28);
		System.out.println("BPB_HiddSec: " + BPB_HiddSec);

		// BPB_TotSec32
		BPB_TotSec32 = DataConverter.getValueFrom4Bytes(buffer, 32);
		System.out.println("BPB_TotSec32: " + BPB_TotSec32);
	}

	public String getBS_OEMName() {
		return BS_OEMName;
	}

	public void setBS_OEMName(String bS_OEMName) {
		BS_OEMName = bS_OEMName;
	}

	public char getBPB_BytsPerSec() {
		return BPB_BytsPerSec;
	}

	public void setBPB_BytsPerSec(char bPB_BytsPerSec) {
		BPB_BytsPerSec = bPB_BytsPerSec;
	}

	public char getBPB_SecPerClus() {
		return BPB_SecPerClus;
	}

	public void setBPB_SecPerClus(char bPB_SecPerClus) {
		BPB_SecPerClus = bPB_SecPerClus;
	}

	public char getBPB_RsvdSecCnt() {
		return BPB_RsvdSecCnt;
	}

	public void setBPB_RsvdSecCnt(char bPB_RsvdSecCnt) {
		BPB_RsvdSecCnt = bPB_RsvdSecCnt;
	}

	public char getBPB_NumFATs() {
		return BPB_NumFATs;
	}

	public void setBPB_NumFATs(char bPB_NumFATs) {
		BPB_NumFATs = bPB_NumFATs;
	}

	public char getBPB_RootEntCnt() {
		return BPB_RootEntCnt;
	}

	public void setBPB_RootEntCnt(char bPB_RootEntCnt) {
		BPB_RootEntCnt = bPB_RootEntCnt;
	}

	public char getBPB_TotSec16() {
		return BPB_TotSec16;
	}

	public void setBPB_TotSec16(char bPB_TotSec16) {
		BPB_TotSec16 = bPB_TotSec16;
	}

	public char getBPB_Media() {
		return BPB_Media;
	}

	public void setBPB_Media(char bPB_Media) {
		BPB_Media = bPB_Media;
	}

	public char getBPB_FATSz16() {
		return BPB_FATSz16;
	}

	public void setBPB_FATSz16(char bPB_FATSz16) {
		BPB_FATSz16 = bPB_FATSz16;
	}

	public char getBPB_SecPerTrk() {
		return BPB_SecPerTrk;
	}

	public void setBPB_SecPerTrk(char bPB_SecPerTrk) {
		BPB_SecPerTrk = bPB_SecPerTrk;
	}

	public char getBPB_NumHeads() {
		return BPB_NumHeads;
	}

	public void setBPB_NumHeads(char bPB_NumHeads) {
		BPB_NumHeads = bPB_NumHeads;
	}

	public long getBPB_HiddSec() {
		return BPB_HiddSec;
	}

	public void setBPB_HiddSec(long bPB_HiddSec) {
		BPB_HiddSec = bPB_HiddSec;
	}

	public long getBPB_TotSec32() {
		return BPB_TotSec32;
	}

	public void setBPB_TotSec32(long bPB_TotSec32) {
		BPB_TotSec32 = bPB_TotSec32;
	}
}
