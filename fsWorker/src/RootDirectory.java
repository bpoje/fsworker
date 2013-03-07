
public class RootDirectory {
	
	private long rootDirectoryAddress;
	private char maxEntriesInRootDirectory;
	
	private long rootDirectorySizeInBytes;
	private long rootDirectorySizeInBlocks;
	
	public RootDirectory(BIOSParameterBlock biosParameterBlock, byte buffer[])
	{
		
		//AA
		//count of sectors occupied by ONE FAT
		long sizeOfOneFAT = biosParameterBlock.getFATSz();
		
		//count of FAT data structures on the volume
		char numberOfFATs = biosParameterBlock.getBPB_NumFATs();
		//-AA
		
		//Number of reserved sectors in the Reserved region of the volume
		//starting at the first sector of the volume.
		char numberOfReservedSectors = biosParameterBlock.getBPB_RsvdSecCnt();
		
		char bytesPerSector = biosParameterBlock.getBPB_BytsPerSec();
		
		//Address of root directory
		rootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs + (long)numberOfReservedSectors) * (long)bytesPerSector;
		System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
		
		//Maximum number of entries in the root directory
		maxEntriesInRootDirectory = biosParameterBlock.getBPB_RootEntCnt();
		
		//Calculate total space occupied by the root directory
		rootDirectorySizeInBytes = (long)maxEntriesInRootDirectory * RootDirectoryEntry.rootDirectoryEntrySize;
		rootDirectorySizeInBlocks = rootDirectorySizeInBytes / (long)bytesPerSector;
		
		System.out.println("rootDirectorySizeInBytes: " + rootDirectorySizeInBytes);
		System.out.println("rootDirectorySizeInKBytes: " + rootDirectorySizeInBytes / 1024);
		System.out.println("rootDirectorySizeInBlocks: " + rootDirectorySizeInBlocks);
		
		//RootDirectoryEntry rootDirectoryEntry = new RootDirectoryEntry((char)1,rootDirectoryAddress, buffer);
		
		
		/*
		char entryNumber = (char)0;
		long entryAddress = calculateRootDirectoryEntryAddress(entryNumber, buffer);
		
		RootDirectoryEntry entry = new RootDirectoryEntry(entryNumber, entryAddress, buffer);
		System.out.println("entry.isLongFilenameEntry(): " + entry.isLongFilenameEntry());
		System.out.println("entry.getFilenameStatus(): " + entry.getFilenameStatus());
		
		if (entry.isLongFilenameEntry())
		{
			entry = new LongFileNameEntry(entryNumber, entryAddress, buffer);
		}
		else
		{
			entry = new DOSFilename(entryNumber, entryAddress, buffer);
		}
		*/
		
		for (int entryNumber = 0; entryNumber < (int)maxEntriesInRootDirectory; entryNumber++)
		{
			long entryAddress = calculateRootDirectoryEntryAddress((char)entryNumber, buffer);
			
			RootDirectoryEntry entry = new RootDirectoryEntry((char) entryNumber, entryAddress, buffer);
			
			FilenameStatus filenameStatus = entry.getFilenameStatus();
			//Break for loop if end-of-list
			if (filenameStatus == FilenameStatus.entryIsAvailableAndNoSubsequentEntryIsInUse)
				break;
			
			System.out.println("\t entryNumber = : " + entryNumber);
			System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			System.out.println("entry.isLongFilenameEntry(): " + entry.isLongFilenameEntry());
			System.out.println("entry.getFilenameStatus(): " + entry.getFilenameStatus());
			
			if (entry.isLongFilenameEntry())
			{
				LongFileNameEntry longFileNameEntry = new LongFileNameEntry((char)entryNumber, entryAddress, buffer);
				System.out.println("longFileNameEntry.isLast(): " + longFileNameEntry.isLast());
			}
			else
			{
				DOSFilename dosFilename = new DOSFilename((char)entryNumber, entryAddress, buffer);
			}
		}
		
	}
	
	public long calculateRootDirectoryEntryAddress(char entryNumber, byte buffer[])
	{
		long entryAddress = (long)rootDirectoryAddress + (long)entryNumber * RootDirectoryEntry.rootDirectoryEntrySize;
		
		//System.out.println("entryAddress: " + entryAddress);
		//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
		
		return entryAddress;
	}
	
	
	
	
}
