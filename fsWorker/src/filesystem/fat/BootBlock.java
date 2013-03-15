package filesystem.fat;

import java.io.IOException;
import java.io.RandomAccessFile;

import fat.DataConverter;
import fat.FATType;
import filesystem.FileSystem;
import filesystem.FileSystemType;
import filesystem.exception.NotEnoughBytesReadException;

public abstract class BootBlock {
	// Common
	private String BS_OEMName;
	private char BPB_BytsPerSec;
	private char BPB_SecPerClus;
	private char BPB_RsvdSecCnt;
	private char BPB_NumFATs;
	private char BPB_RootEntCnt;
	private char BPB_TotSec16;
	private char BPB_Media;
	private char BPB_FATSz16;
	private char BPB_SecPerTrk;
	private char BPB_NumHeads;
	private long BPB_HiddSec;
	private long BPB_TotSec32;

	// number of sectors in data region
	long numberOfDataSectors;

	// number of clusters in data region
	private long CountofClustersInDataRegion;

	private FATType type;

	// count of sectors occupied by ONE FAT
	private long FATSz;

	// FAT12 or FAT16

	// FAT32
	//private long BPB_FATSz32;
	
	public BootBlock()
	{
		
	}
}
