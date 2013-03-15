package filesystem.fat;

import hash.Hash;

import java.util.ArrayList;

import fat.BIOSParameterBlock;
import fat.DOSFilename;
import fat.DataRegion;
import fat.FAT12_16;
import fat.FilenameStatus;
import fat.LongFileNameEntry;
import fat.RootDirectoryEntry;
import filesystem.fat.fat16.FileAllocationTable16;

public abstract class FatDirectory {
	protected BootBlock bootBlock;
	protected FileAllocationTable fileAllocationTable;
	
	public FatDirectory()
	{
		
	}
	
	public void initFatDirectory(BootBlock bootBlock, FileAllocationTable fileAllocationTable)
	{
		this.bootBlock = bootBlock;
		this.fileAllocationTable = fileAllocationTable;
	}
}
