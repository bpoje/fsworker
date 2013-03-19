package filesystem.fat.fat16;

import java.io.IOException;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.BootBlock;
import filesystem.io.FileSystemIO;

public class BootBlock16 extends BootBlock {

	
	public BootBlock16(FileSystemIO fileSystemIO) throws IOException,
			NotEnoughBytesReadException {
		super(fileSystemIO);
		
		
	}
}
