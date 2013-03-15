package filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.io.FileSystemIO;

public abstract class FileSystem {
	
	protected FileSystemType fileSystemType;
	protected FileSystemIO fileSystemIO;
	
	public FileSystem(FileSystemType fileSystemType, FileSystemIO fileSystemIO)
	{
		this.fileSystemType = fileSystemType;
		this.fileSystemIO = fileSystemIO;
	}
	
	public abstract void ls() throws IOException, NotEnoughBytesReadException;
}
