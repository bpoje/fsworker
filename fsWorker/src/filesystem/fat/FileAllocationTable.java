package filesystem.fat;

import filesystem.fat.fat16.BootBlock16;
import filesystem.io.FileSystemIO;

public abstract class FileAllocationTable {
	protected FileSystemIO fileSystemIO;
	
	public FileAllocationTable(FileSystemIO fileSystemIO)
	{
		this.fileSystemIO = fileSystemIO;
	}
	
	abstract public void initFileAllocationTable(BootBlock bootBlock);
}
