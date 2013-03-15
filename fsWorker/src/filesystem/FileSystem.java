package filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import filesystem.exception.NotEnoughBytesReadException;

public abstract class FileSystem {
	
	private String pathToFSImage;
	private File fileFSImage;
	private RandomAccessFile randomAccessFSImage;
	private FileSystemType fileSystemType;
	
	public FileSystem(String pathToFSImage, FileSystemType fileSystemType) throws FileNotFoundException, IllegalArgumentException
	{
		this.pathToFSImage = pathToFSImage;
		this.fileFSImage = new File(pathToFSImage);
		this.fileSystemType = fileSystemType;
		
		//Check if file exists
		//This check is necessary as RandomAccessFile (with mode rw)
		//will auto create the file, if it does not exist)
		if (!fileFSImage.isFile())
		{
			throw new IllegalArgumentException(fileFSImage + " is not a valid file");
		}
		
		this.randomAccessFSImage = new RandomAccessFile(pathToFSImage,"rw");
	}
	
	public byte [] readFSImage(long position, int numberOfBytes) throws IOException, NotEnoughBytesReadException
	{
		randomAccessFSImage.seek(position);
		
		byte [] buffer = new byte [numberOfBytes];
		
		int numberOfBytesRead = randomAccessFSImage.read(buffer);
		
		if (numberOfBytesRead != numberOfBytes)
			throw new NotEnoughBytesReadException("Not enough bytes read!");
		
		return buffer;
	}

	public String getPathToFSImage() {
		return pathToFSImage;
	}

	public void setPathToFSImage(String pathToFSImage) {
		this.pathToFSImage = pathToFSImage;
	}

	public File getFileFSImage() {
		return fileFSImage;
	}

	public void setFileFSImage(File fileFSImage) {
		this.fileFSImage = fileFSImage;
	}

	public RandomAccessFile getRandomAccessFSImage() {
		return randomAccessFSImage;
	}

	public void setRandomAccessFSImage(RandomAccessFile randomAccessFSImage) {
		this.randomAccessFSImage = randomAccessFSImage;
	}

	public FileSystemType getFileSystemType() {
		return fileSystemType;
	}

	public void setFileSystemType(FileSystemType fileSystemType) {
		this.fileSystemType = fileSystemType;
	}
}
