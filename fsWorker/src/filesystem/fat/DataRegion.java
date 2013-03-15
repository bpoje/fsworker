package filesystem.fat;

import filesystem.io.FileSystemIO;

public abstract class DataRegion {
	protected FileSystemIO fileSystemIO;
	protected BootBlock bootBlock;
	
	public DataRegion()
	{
	}
	
	public void initDataRegion(FileSystemIO fileSystemIO, BootBlock bootBlock)
	{
		this.fileSystemIO = fileSystemIO;
		this.bootBlock = bootBlock;
	}
}
