package filesystem.fat.fat16;

import hash.Hash;

import java.io.IOException;
import java.util.ArrayList;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.fat16.Fat16Entry;
import fat.FilenameStatus;
import fat.RootDirectoryEntry;
import filesystem.fat.fat16.Fat16EntryLongFileName;
import filesystem.fat.FatDirectory;
import filesystem.io.FileSystemIO;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.fat.FatEntry;
import filesystem.fat.FileAllocationTable;

public class Fat16Directory extends FatDirectory {
	protected BootBlock bootBlock;
	protected FileAllocationTable fileAllocationTable;
	protected DataRegion dataRegion;
	
	protected long rootDirectoryAddress = 0;
	protected char maxEntriesInRootDirectory = 0;
	
	protected long rootDirectorySizeInBytes = 0;
	protected long rootDirectorySizeInBlocks = 0;

	public Fat16Directory(FileSystemIO fileSystemIO)
	{
		super(fileSystemIO);
		System.out.println("ccc fileSystemIO:" + fileSystemIO);
	}
	
	@Override
	public void initFatDirectory(BootBlock bootBlock,
			FileAllocationTable fileAllocationTable, DataRegion dataRegion) {

		this.bootBlock = bootBlock;
		this.fileAllocationTable = fileAllocationTable;
		this.dataRegion = dataRegion;
		
		BootBlock16 bootBlock16 = (BootBlock16)bootBlock;

		// count of sectors occupied by ONE FAT
		long sizeOfOneFAT = bootBlock16.getFATSz();

		// count of FAT data structures on the volume
		char numberOfFATs = bootBlock16.getBPB_NumFATs();

		// Number of reserved sectors in the Reserved region of the volume
		// starting at the first sector of the volume.
		char numberOfReservedSectors = bootBlock16.getBPB_RsvdSecCnt();

		char bytesPerSector = bootBlock16.getBPB_BytsPerSec();

		// Address of root directory
		rootDirectoryAddress = calculateRootDirectoryAddress(sizeOfOneFAT,
				numberOfFATs, numberOfReservedSectors, bytesPerSector);

		System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		System.out.printf("rootDirectoryAddress: 0x%02Xh\n",
				rootDirectoryAddress);

		// long add = calculateRootDirectoryAddress(biosParameterBlock);
		// System.out.println("x: " + rootDirectoryAddress + " y: " + add);

		// Maximum number of entries in the root directory
		maxEntriesInRootDirectory = bootBlock16.getBPB_RootEntCnt();

		// Calculate total space occupied by the root directory
		rootDirectorySizeInBytes = calculateRootDirectorySizeInBytes(bootBlock16);
		rootDirectorySizeInBlocks = calculateRootDirectorySizeInBlocks(bootBlock16);
		// -----

		System.out.println("rootDirectorySizeInBytes: "
				+ rootDirectorySizeInBytes);
		System.out.println("rootDirectorySizeInKBytes: "
				+ rootDirectorySizeInBytes / 1024);
		System.out.println("rootDirectorySizeInBlocks: "
				+ rootDirectorySizeInBlocks);

		// RootDirectoryEntry rootDirectoryEntry = new
		// RootDirectoryEntry((char)1,rootDirectoryAddress, buffer);

		/*
		 * char entryNumber = (char)0; long entryAddress =
		 * calculateRootDirectoryEntryAddress(entryNumber, buffer);
		 * 
		 * RootDirectoryEntry entry = new RootDirectoryEntry(entryNumber,
		 * entryAddress, buffer);
		 * System.out.println("entry.isLongFilenameEntry(): " +
		 * entry.isLongFilenameEntry());
		 * System.out.println("entry.getFilenameStatus(): " +
		 * entry.getFilenameStatus());
		 * 
		 * if (entry.isLongFilenameEntry()) { entry = new
		 * LongFileNameEntry(entryNumber, entryAddress, buffer); } else { entry
		 * = new DOSFilename(entryNumber, entryAddress, buffer); }
		 */

	}
	
	public ArrayList<FatEntry> directory() throws IOException, NotEnoughBytesReadException
	{
		//ArrayList<RootDirectoryEntry> arrayListFiles = new ArrayList<RootDirectoryEntry>();
		ArrayList<FatEntry> arrayListFiles = new ArrayList<FatEntry>();
		
		for (int entryNumber = 0; entryNumber < (int)maxEntriesInRootDirectory; entryNumber++)
		{
			long entryAddress = calculateRootDirectoryEntryAddress((char)entryNumber);
			
			FatEntry entry = new FatEntry((char) entryNumber, entryAddress, fileSystemIO);
			
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
				Fat16EntryLongFileName longFileNameEntry = new Fat16EntryLongFileName((char)entryNumber, entryAddress, fileSystemIO);
				System.out.println("longFileNameEntry.isLast(): " + longFileNameEntry.isLast());
			}
			else
			{				
				Fat16Entry dosFilename = new Fat16Entry((char)entryNumber, entryAddress, fileSystemIO);
				
				boolean isSubdirectory = dosFilename.isSubdirectoryEntry();
				
				arrayListFiles.add(dosFilename);
				
				//if (isSubdirectory)
				//{
				//	System.out.println("isSubdirectory: " + isSubdirectory);
				//	long adr = dataRegion.getClusterAddress(dosFilename.getStartingClusterNumber());
				//	
				//	System.out.println("adr: " + adr);
				//	System.out.printf("adr: 0x%02Xh\n", adr);
				//	
				//	byte temp[] = dataRegion.getClusterData(adr);
				//	for (int i = 0; i < temp.length; i++)
				//	{
				//		System.out.printf("0x%02Xh ", temp[i]);
				//	}
				//	System.out.println();
				//	
				//	System.out.println("---------------------------------------------------------");
				//	subDirectory(adr);
				//	System.out.println("---------------------------------------------------------");
				//}
				
				
				/*
				byte fileData[] = dosFilename.getData((DataRegion16)dataRegion, (FileAllocationTable16)fileAllocationTable);
				
				//If not folder
				if (fileData != null)
				{
					System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
					System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
					
					String md5 = Hash.getMd5FromFileData(fileData);
					System.out.println("MD5 digest(in hex format):: " + md5);
				}
				*/
			}
			
		}
		
		return arrayListFiles;
	}
	
	public ArrayList<FatEntry> subDirectory(long address) throws IOException, NotEnoughBytesReadException
	{
		DataRegion16 dataRegion16 = (DataRegion16)dataRegion;
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		
		ArrayList<FatEntry> arrayListFiles = new ArrayList<FatEntry>();
		
		for (int entryNumber = 0; entryNumber < (int)maxEntriesInRootDirectory; entryNumber++)
		{
			//long entryAddress = calculateRootDirectoryEntryAddress((char)entryNumber, buffer);
			
			//calculateSubDirectoryEntryAddress(long address, char entryNumber, byte buffer[])
			long entryAddress = calculateSubDirectoryEntryAddress(address, (char)entryNumber);
			
			FatEntry entry = new FatEntry((char) entryNumber, entryAddress, this.fileSystemIO);
			
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
				Fat16EntryLongFileName longFileNameEntry = new Fat16EntryLongFileName((char)entryNumber, entryAddress, this.fileSystemIO);
				System.out.println("longFileNameEntry.isLast(): " + longFileNameEntry.isLast());
			}
			else
			{
				Fat16Entry dosFilename = new Fat16Entry((char)entryNumber, entryAddress, this.fileSystemIO);
				boolean isSubdirectory = dosFilename.isSubdirectoryEntry();
				
				arrayListFiles.add(dosFilename);
				
				if (isSubdirectory)
				{
					System.out.println("isSubdirectory: " + isSubdirectory);
					long adr = dataRegion16.getClusterAddress(dosFilename.getStartingClusterNumber());
					
					System.out.println("adr: " + adr);
					System.out.printf("adr: 0x%02Xh\n", adr);
					
					byte temp[] = dataRegion16.getClusterData(adr);
					for (int i = 0; i < temp.length; i++)
					{
						System.out.printf("0x%02Xh ", temp[i]);
					}
					System.out.println();
				}
				
				byte fileData[] = dosFilename.getData(dataRegion16, fileAllocationTable16);
				
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
	
	
	public static long calculateRootDirectoryAddress(
			BootBlock16 bootBlock16) {
		// BB
		// AA
		// count of sectors occupied by ONE FAT
		long sizeOfOneFAT = bootBlock16.getFATSz();

		// count of FAT data structures on the volume
		char numberOfFATs = bootBlock16.getBPB_NumFATs();
		// -AA

		// Number of reserved sectors in the Reserved region of the volume
		// starting at the first sector of the volume.
		char numberOfReservedSectors = bootBlock16.getBPB_RsvdSecCnt();

		char bytesPerSector = bootBlock16.getBPB_BytsPerSec();

		// Address of root directory
		long myRootDirectoryAddress = (sizeOfOneFAT * (long) numberOfFATs + (long) numberOfReservedSectors)
				* (long) bytesPerSector;
		// System.out.println("myRootDirectoryAddress: " +
		// myRootDirectoryAddress);
		// System.out.printf("myRootDirectoryAddress: 0x%02Xh\n",
		// myRootDirectoryAddress);

		return myRootDirectoryAddress;
	}

	// sizeOfOneFAT - count of sectors occupied by ONE FAT
	// numberOfFATs - count of FAT data structures on the volume
	// numberOfReservedSectors - Number of reserved sectors in the Reserved
	// region of the volume starting at the first sector of the volume.
	// bytesPerSector
	public static long calculateRootDirectoryAddress(long sizeOfOneFAT,
			char numberOfFATs, char numberOfReservedSectors, char bytesPerSector) {
		// Address of root directory
		long myRootDirectoryAddress = (sizeOfOneFAT * (long) numberOfFATs + (long) numberOfReservedSectors)
				* (long) bytesPerSector;
		// System.out.println("myRootDirectoryAddress: " +
		// myRootDirectoryAddress);
		// System.out.printf("myRootDirectoryAddress: 0x%02Xh\n",
		// myRootDirectoryAddress);

		return myRootDirectoryAddress;
	}
	
	public static long calculateRootDirectorySizeInBytes(BootBlock16 bootBlock16)
	{
		char myMaxEntriesInRootDirectory = bootBlock16.getBPB_RootEntCnt();
		long myRootDirectorySizeInBytes = (long)myMaxEntriesInRootDirectory * Fat16Entry.rootDirectoryEntrySize;
		return myRootDirectorySizeInBytes;
	}
	
	public static long calculateRootDirectorySizeInBlocks(BootBlock16 bootBlock16)
	{
		long myRootDirectorySizeInBytes = calculateRootDirectorySizeInBytes(bootBlock16);
		char bytesPerSector = bootBlock16.getBPB_BytsPerSec();
		long myRootDirectorySizeInBlocks = myRootDirectorySizeInBytes / (long)bytesPerSector;
		
		return myRootDirectorySizeInBlocks;
	}

	//public long calculateRootDirectoryEntryAddress(char entryNumber, byte buffer[])
		public long calculateRootDirectoryEntryAddress(char entryNumber)
		{
			long entryAddress = (long)rootDirectoryAddress + (long)entryNumber * RootDirectoryEntry.rootDirectoryEntrySize;
			
			//System.out.println("entryAddress: " + entryAddress);
			//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			return entryAddress;
		}
		
		public long calculateSubDirectoryEntryAddress(long address, char entryNumber)
		{
			long entryAddress = (long)address + (long)entryNumber * RootDirectoryEntry.rootDirectoryEntrySize;
			
			//System.out.println("entryAddress: " + entryAddress);
			//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			return entryAddress;
		}
}
