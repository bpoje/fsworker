package filesystem.fat.fat16;

import java.io.FileNotFoundException;
import java.io.IOException;

import filesystem.FileSystem;
import filesystem.fat.FileAllocationTable;
import filesystem.fat.FileSystemFat;

public class FileSystemFat16 extends FileSystemFat{
	
	public FileSystemFat16(String filenameFSImage) throws IllegalArgumentException, IOException
	{		
		super(filenameFSImage, new FileAllocationTable16(), new BootBlock16());
	}
}
