
public class DOSFilename extends RootDirectoryEntry {

	private String filename;
	private String filenameExtension;
	private char fileAttributes;
	char startingClusterNumber;
	long filesizeInBytes;
	
	/*
	boolean isReadOnlyFile;
	boolean isHiddenFile;
	boolean isSystemFile;
	boolean isSpecialEntry;
	boolean isSubdirectoryEntry;
	boolean isArchiveFlag;
	*/
	
	public DOSFilename(char entryNumber, long entryAddress, byte buffer[])
	{
		super(entryNumber, entryAddress, buffer);
		
		/*
		long entryAddress = (long)rootDirAddress + (long)entryNumber * RootDirectoryEntry.rootDirectoryEntrySize;
		
		System.out.println("entryAddress: " + entryAddress);
		System.out.printf("entryAddress: 0x%02Xh\n", entryAddress);
		*/
		
		/*
		//Check first byte of filename
		//(The first byte of the filename indicates its status. Usually, it contains a normal filename
		//character (e.g. 'A'), but there are some special values)
		byte filenameStatus = buffer[(int)entryAddress];
		
		//System.out.println("filenameStatus: " + filenameStatus);
		//System.out.printf("filenameStatus: 0x%02Xh\n", filenameStatus);
		
		switch (filenameStatus)
		{
			//Entry is available and no subsequent entry is in use
			case (byte)0x00:
				//System.out.println("filenameStatus: Entry is available and no subsequent entry is in use");
				break;
			
			//Entry has been previously erased and is not available. File undelete utilities must replace this character with a regular character as part of the undeletion process
			case (byte)0xe5:
				//System.out.println("filenameStatus: Entry has been previously erased and is not available. File undelete utilities must replace this character with a regular character as part of the undeletion process");
				break;
			
			//Initial character is actually 0xE5
			case (byte)0x05:
				//System.out.println("filenameStatus: Initial character is actually 0xE5");
				break;
			
			//'Dot' entry; either '.' or '..'
			case (byte)0x2e:
				//System.out.println("filenameStatus: 'Dot' entry; either '.' or '..'");
				break;
			
			//Any other character
		    //This is the first character of a real filename.
			//default:
			//	System.out.println("filenameStatus: Real filename entry");
		}
		
		*/
		
		
		//Get filename
		//If a filename is fewer than eight characters in length, it is padded with space characters.
		filename = DataConverter.getStringFrom8Bytes(buffer, (int)entryAddress);
		
		//Remove padding
		int numberOfAdditionalSpaces = 0;
		for (int i = filename.length() - 1; i >= 0 && filename.charAt(i) == ' '; i--)
			numberOfAdditionalSpaces++;
		
		//System.out.println("numberOfAdditionalSpaces: " + numberOfAdditionalSpaces);
		
		filename = filename.substring(0, filename.length() - numberOfAdditionalSpaces);
		
		//System.out.println("filename: " + filename);
		
		//If the filename extension is fewer than three characters in length, it is padded with space characters.
		filenameExtension = DataConverter.getStringFrom3Bytes(buffer, (int)entryAddress + 8);
		
		//Remove padding
		numberOfAdditionalSpaces = 0;
		for (int i = filenameExtension.length() - 1; i >= 0 && filenameExtension.charAt(i) == ' '; i--)
			numberOfAdditionalSpaces++;
		
		//System.out.println("numberOfAdditionalSpaces: " + numberOfAdditionalSpaces);
		
		filenameExtension = filenameExtension.substring(0, filenameExtension.length() - numberOfAdditionalSpaces);
		
		//System.out.println("filenameExtension: " + filenameExtension);
		
		//Note that the dot used to separate the filename and the filename extension is implied,
		//and is not actually stored anywhere; it is just used when referring to the file. If the
		//filename extension is fewer than three characters in length, it is padded with space
		//characters.
		System.out.println("file: " + filename + "." + filenameExtension);
		
		
		
		
		
		/*
		//File atributes
		fileAttributes = DataConverter.getValueFrom1Byte(buffer, (int)entryAddress + 11);
		
		//System.out.println("fileAttributes: " + (int)fileAttributes);
		//System.out.printf("fileAttributes: 0x%02Xh\n", (int)fileAttributes);
		
		//0x01 Indicates that the file is read only.
		isReadOnlyFile = 		(fileAttributes & (0x01 << 0)) != 0;
		
		//0x02 Indicates a hidden file. Such files can be displayed if it is really required.
		isHiddenFile = 			(fileAttributes & (0x01 << 1)) != 0;
		
		//0x04 Indicates a system file. These are hidden as well.
		isSystemFile = 			(fileAttributes & (0x01 << 2)) != 0;
		
		//0x08 Indicates a special entry containing the disk's volume label, instead of
		//describing a file. This kind of entry appears only in the root directory.
		isSpecialEntry = 		(fileAttributes & (0x01 << 3)) != 0;
		
		//0x10 The entry describes a subdirectory.
		isSubdirectoryEntry = 	(fileAttributes & (0x01 << 4)) != 0;
		
		//0x20 This is the archive flag. This can be set and cleared by the programmer or
		//user, but is always set when the file is modified. It is used by backup programs.
		isArchiveFlag = 		(fileAttributes & (0x01 << 5)) != 0;
		
		
		System.out.println();
		System.out.println("isReadOnly: " + isReadOnlyFile);
		System.out.println("isHidden: " + isHiddenFile);
		System.out.println("isSystemFile: " + isSystemFile);
		System.out.println("isSpecialEntry: " + isSpecialEntry);
		System.out.println("isSubdirectoryEntry: " + isSubdirectoryEntry);
		System.out.println("isArchiveFlag: " + isArchiveFlag);
		System.out.println();
		*/
		
		
		
		
		//Starting cluster number for file
		startingClusterNumber = DataConverter.getValueFrom2Bytes(buffer, (int)entryAddress + 26);
		
		//System.out.println("startingClusterNumber: " + (int)startingClusterNumber);
		//System.out.printf("startingClusterNumber: 0x%02Xh\n", (int)startingClusterNumber);
		
		//File size in bytes
		filesizeInBytes = DataConverter.getValueFrom4Bytes(buffer, (int)entryAddress + 28);
		
		//System.out.println("filesizeInBytes: " + filesizeInBytes);
		
	}
	
	public void getData()
	{
		
	}
	
}
