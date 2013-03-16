package filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.FatEntry;
import filesystem.io.FileSystemIO;

public abstract class FileSystem {
	
	protected FileSystemType fileSystemType;
	protected FileSystemIO fileSystemIO;
	
	public FileSystem(FileSystemType fileSystemType, FileSystemIO fileSystemIO)
	{
		this.fileSystemType = fileSystemType;
		this.fileSystemIO = fileSystemIO;
	}
	
	public abstract ArrayList<FatEntry> ls() throws IOException, NotEnoughBytesReadException;
	public abstract byte [] getData(FileSystemEntry entry) throws IOException, NotEnoughBytesReadException;
	public abstract ArrayList<FatEntry> cd(FileSystemEntry entry) throws IOException, NotEnoughBytesReadException;
}
