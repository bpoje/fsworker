package filesystem.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import filesystem.exception.NotEnoughBytesReadException;

public class FileSystemIO {
	private String pathToFSImage;
	private File fileFSImage;
	private RandomAccessFile randomAccessFSImage;
	
	public FileSystemIO(String pathToFSImage) throws FileNotFoundException
	{
		this.pathToFSImage = pathToFSImage;
		this.fileFSImage = new File(pathToFSImage);
		
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
	
	public void writeFSImage(long position, byte [] buffer) throws IOException
	{
		randomAccessFSImage.seek(position);
		
		randomAccessFSImage.write(buffer);
	}
}
