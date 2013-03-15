package filesystem.fat;

import filesystem.io.FileSystemIO;

public abstract class DataRegion {
	protected FileSystemIO fileSystemIO;
	
	public DataRegion(FileSystemIO fileSystemIO)
	{
		this.fileSystemIO = fileSystemIO;
	}
	
	abstract public void initDataRegion(BootBlock bootBlock);
}
