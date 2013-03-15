package filesystem.fat.fat16;

import java.io.IOException;

import filesystem.FileSystemType;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.BootBlock;
import filesystem.io.FileSystemIO;

public class BootBlock16 extends BootBlock {

	// number of sectors in data region
	long numberOfDataSectors;

	// number of clusters in data region
	private long CountofClustersInDataRegion;

	private FileSystemType type;

	// count of sectors occupied by ONE FAT
	private long FATSz;

	// FAT12 or FAT16

	// FAT32
	//private long BPB_FATSz32;

	public BootBlock16(FileSystemIO fileSystemIO) throws IOException,
			NotEnoughBytesReadException {
		super(fileSystemIO);

		//byte buffer[] = null;
		
		//Za FAT16 ze mamo vsa potrebna polja
		//---------------------------------------------------------------------------
		// FAT12 or FAT16
		//if (BPB_FATSz16 != 0) {
		//	buffer = new byte[26];
		//	fs.read(buffer);
		//} else // FAT32
		//{
		//	buffer = new byte[54];
		//	fs.read(buffer);
		//
		//	// BPB_FATSz32
		//	// This field is only defined for FAT32 media and does not exist on
		//	// FAT12 and FAT16 media. This field is the FAT32 32-bit count of
		//	// sectors occupied by ONE FAT. BPB_FATSz16 must be 0.
		//
		//	BPB_FATSz32 = DataConverter.getValueFrom4Bytes(buffer, 0);
		//	System.out.println("BPB_FATSz32: " + BPB_FATSz32);
		//}
		//---------------------------------------------------------------------------
		
		

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
		//if (BPB_FATSz16 != 0)
		long FATSz = BPB_FATSz16;
		//else
		//	FATSz = BPB_FATSz32;

		//long TotSec;
		//if (BPB_TotSec16 != 0)
		long TotSec = BPB_TotSec16;
		//else
		//	TotSec = BPB_TotSec32;

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
	}

	public long getNumberOfDataSectors() {
		return numberOfDataSectors;
	}

	public void setNumberOfDataSectors(long numberOfDataSectors) {
		this.numberOfDataSectors = numberOfDataSectors;
	}

	public long getCountofClustersInDataRegion() {
		return CountofClustersInDataRegion;
	}

	public void setCountofClustersInDataRegion(long countofClustersInDataRegion) {
		CountofClustersInDataRegion = countofClustersInDataRegion;
	}

	public FileSystemType getType() {
		return type;
	}

	public void setType(FileSystemType type) {
		this.type = type;
	}

	public long getFATSz() {
		return FATSz;
	}

	public void setFATSz(long fATSz) {
		FATSz = fATSz;
	}
}
