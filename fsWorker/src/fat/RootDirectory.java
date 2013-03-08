package fat;

import hash.Hash;

import java.security.MessageDigest;
import java.util.ArrayList;


public class RootDirectory {
	
	private long rootDirectoryAddress;
	private char maxEntriesInRootDirectory;
	
	private long rootDirectorySizeInBytes;
	private long rootDirectorySizeInBlocks;
	
	private DataRegion dataRegion;
	private FAT12_16 fat12_16;
	
	private byte buffer[];
	
	public RootDirectory(BIOSParameterBlock biosParameterBlock, byte buffer[], DataRegion dataRegion, FAT12_16 fat12_16)
	{
		this.dataRegion = dataRegion;
		this.fat12_16 = fat12_16;
		
		this.buffer = buffer;
		
		//count of sectors occupied by ONE FAT
		long sizeOfOneFAT = biosParameterBlock.getFATSz();
		
		//count of FAT data structures on the volume
		char numberOfFATs = biosParameterBlock.getBPB_NumFATs();
		
		//Number of reserved sectors in the Reserved region of the volume
		//starting at the first sector of the volume.
		char numberOfReservedSectors = biosParameterBlock.getBPB_RsvdSecCnt();
		
		char bytesPerSector = biosParameterBlock.getBPB_BytsPerSec();
		
		//Address of root directory
		rootDirectoryAddress = calculateRootDirectoryAddress(sizeOfOneFAT, numberOfFATs, numberOfReservedSectors, bytesPerSector);
		
		System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
		
		//long add = calculateRootDirectoryAddress(biosParameterBlock);
		//System.out.println("x: " + rootDirectoryAddress + " y: " + add);
		
		//Maximum number of entries in the root directory
		maxEntriesInRootDirectory = biosParameterBlock.getBPB_RootEntCnt();
		
		//Calculate total space occupied by the root directory
		rootDirectorySizeInBytes = calculateRootDirectorySizeInBytes(biosParameterBlock);
		rootDirectorySizeInBlocks = calculateRootDirectorySizeInBlocks(biosParameterBlock);
		//-----
		
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
		
		
		
	}
	
	public ArrayList<RootDirectoryEntry> directory()
	{
		ArrayList<RootDirectoryEntry> arrayListFiles = new ArrayList<RootDirectoryEntry>();
		
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
				boolean isSubdirectory = dosFilename.isSubdirectoryEntry();
				
				arrayListFiles.add(dosFilename);
				
				if (isSubdirectory)
				{
					System.out.println("isSubdirectory: " + isSubdirectory);
					long adr = dataRegion.getClusterAddress(dosFilename.getStartingClusterNumber());
					
					System.out.println("adr: " + adr);
					System.out.printf("adr: 0x%02Xh\n", adr);
					
					byte temp[] = dataRegion.getClusterData(adr);
					for (int i = 0; i < temp.length; i++)
					{
						System.out.printf("0x%02Xh ", temp[i]);
					}
					System.out.println();
					
					System.out.println("---------------------------------------------------------");
					subDirectory(adr);
					System.out.println("---------------------------------------------------------");
				}
				
				byte fileData[] = dosFilename.getData(dataRegion, fat12_16);
				
				//If not folder
				if (fileData != null)
				{
					System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
					System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
					
					String md5 = Hash.getMd5FromFileData(fileData);
					System.out.println("MD5 digest(in hex format):: " + md5);
				}
			}
		}
		
		return arrayListFiles;
	}
	
	public ArrayList<RootDirectoryEntry> subDirectory(long address)
	{
		ArrayList<RootDirectoryEntry> arrayListFiles = new ArrayList<RootDirectoryEntry>();
		
		for (int entryNumber = 0; entryNumber < (int)maxEntriesInRootDirectory; entryNumber++)
		{
			//long entryAddress = calculateRootDirectoryEntryAddress((char)entryNumber, buffer);
			
			//calculateSubDirectoryEntryAddress(long address, char entryNumber, byte buffer[])
			long entryAddress = calculateSubDirectoryEntryAddress(address, (char)entryNumber, buffer);
			
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
				boolean isSubdirectory = dosFilename.isSubdirectoryEntry();
				
				arrayListFiles.add(dosFilename);
				
				if (isSubdirectory)
				{
					System.out.println("isSubdirectory: " + isSubdirectory);
					long adr = dataRegion.getClusterAddress(dosFilename.getStartingClusterNumber());
					
					System.out.println("adr: " + adr);
					System.out.printf("adr: 0x%02Xh\n", adr);
					
					byte temp[] = dataRegion.getClusterData(adr);
					for (int i = 0; i < temp.length; i++)
					{
						System.out.printf("0x%02Xh ", temp[i]);
					}
					System.out.println();
				}
				
				byte fileData[] = dosFilename.getData(dataRegion, fat12_16);
				
				//If not folder
				if (fileData != null)
				{
					System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
					System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
					
					String md5 = Hash.getMd5FromFileData(fileData);
					System.out.println("MD5 digest(in hex format):: " + md5);
				}
			}
		}
		
		return arrayListFiles;
	}
	
	
	
	public static long calculateRootDirectoryAddress(BIOSParameterBlock biosParameterBlock)
	{
		//BB
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
				long myRootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs + (long)numberOfReservedSectors) * (long)bytesPerSector;
				//System.out.println("myRootDirectoryAddress: " + myRootDirectoryAddress);
				//System.out.printf("myRootDirectoryAddress: 0x%02Xh\n", myRootDirectoryAddress);
				
				return myRootDirectoryAddress;
	}
	
	//sizeOfOneFAT - count of sectors occupied by ONE FAT
	//numberOfFATs - count of FAT data structures on the volume
	//numberOfReservedSectors - Number of reserved sectors in the Reserved region of the volume starting at the first sector of the volume.
	//bytesPerSector
	public static long calculateRootDirectoryAddress(long sizeOfOneFAT, char numberOfFATs, char numberOfReservedSectors, char bytesPerSector)
	{
		//Address of root directory
		long myRootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs + (long)numberOfReservedSectors) * (long)bytesPerSector;
		//System.out.println("myRootDirectoryAddress: " + myRootDirectoryAddress);
		//System.out.printf("myRootDirectoryAddress: 0x%02Xh\n", myRootDirectoryAddress);
				
		return myRootDirectoryAddress;
	}
	
	public static long calculateRootDirectorySizeInBytes(BIOSParameterBlock biosParameterBlock)
	{
		char myMaxEntriesInRootDirectory = biosParameterBlock.getBPB_RootEntCnt();
		long myRootDirectorySizeInBytes = (long)myMaxEntriesInRootDirectory * RootDirectoryEntry.rootDirectoryEntrySize;
		return myRootDirectorySizeInBytes;
	}
	
	public static long calculateRootDirectorySizeInBlocks(BIOSParameterBlock biosParameterBlock)
	{
		long myRootDirectorySizeInBytes = calculateRootDirectorySizeInBytes(biosParameterBlock);
		char bytesPerSector = biosParameterBlock.getBPB_BytsPerSec();
		long myRootDirectorySizeInBlocks = myRootDirectorySizeInBytes / (long)bytesPerSector;
		
		return myRootDirectorySizeInBlocks;
	}
	
	public long calculateRootDirectoryEntryAddress(char entryNumber, byte buffer[])
	{
		long entryAddress = (long)rootDirectoryAddress + (long)entryNumber * RootDirectoryEntry.rootDirectoryEntrySize;
		
		//System.out.println("entryAddress: " + entryAddress);
		//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
		
		return entryAddress;
	}
	
	public long calculateSubDirectoryEntryAddress(long address, char entryNumber, byte buffer[])
	{
		long entryAddress = (long)address + (long)entryNumber * RootDirectoryEntry.rootDirectoryEntrySize;
		
		//System.out.println("entryAddress: " + entryAddress);
		//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
		
		return entryAddress;
	}
}
