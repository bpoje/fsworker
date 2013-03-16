package filesystem.fat;

import filesystem.io.FileSystemIO;

public abstract class FatDirectory {
	protected FileSystemIO fileSystemIO;
	
	public FatDirectory(FileSystemIO fileSystemIO)
	{
		this.fileSystemIO = fileSystemIO;
	}
	
	abstract public void initFatDirectory(BootBlock bootBlock, FileAllocationTable fileAllocationTable, DataRegion dataRegion);

	public FileSystemIO getFileSystemIO() {
		return fileSystemIO;
	}
}
