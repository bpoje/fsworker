package filesystem.fat.fat16;


import java.io.IOException;
import java.util.ArrayList;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.fat16.Fat16Entry;
import filesystem.fat.fat16.Fat16EntryLongFileName;
import filesystem.fat.FatDirectory;
import filesystem.hash.Hash;
import filesystem.io.FileSystemIO;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.fat.FatEntry;
import filesystem.fat.FileAllocationTable;
import filesystem.fat.FilenameStatus;

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
	
	/*
	public ArrayList<FatEntry> directory() throws IOException, NotEnoughBytesReadException
	{
		//ArrayList<RootDirectoryEntry> arrayListFiles = new ArrayList<RootDirectoryEntry>();
		ArrayList<FatEntry> arrayListFiles = new ArrayList<FatEntry>();
		
		for (int entryNumber = 0; entryNumber < (int)maxEntriesInRootDirectory; entryNumber++)
		{
			long entryAddress = calculateRootDirectoryEntryAddress((char)entryNumber);
			
			//FatEntry entry = new FatEntry((char) entryNumber, entryAddress, fileSystemIO);
			FatEntry entry = new FatEntry(bootBlock, fileAllocationTable, dataRegion, (char) entryNumber, entryAddress, fileSystemIO);
			
			FilenameStatus filenameStatus = entry.getFilenameStatus();
			//Break for loop if end-of-list
			if (filenameStatus == FilenameStatus.entryIsAvailableAndNoSubsequentEntryIsInUse)
				break;
			
			//System.out.println("\t entryNumber = : " + entryNumber);
			//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			//System.out.println("entry.isLongFilenameEntry(): " + entry.isLongFilenameEntry());
			//System.out.println("entry.getFilenameStatus(): " + entry.getFilenameStatus());
			
			
			
			if (entry.isLongFilenameEntry())
			{			
				Fat16EntryLongFileName longFileNameEntry = new Fat16EntryLongFileName(bootBlock, fileAllocationTable, dataRegion, (char)entryNumber, entryAddress, fileSystemIO);
				//System.out.println("longFileNameEntry.isLast(): " + longFileNameEntry.isLast());
			}
			else
			{				
				Fat16Entry dosFilename = new Fat16Entry(bootBlock, fileAllocationTable, dataRegion, (char)entryNumber, entryAddress, fileSystemIO);
				
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
				
				
				
				//byte fileData[] = dosFilename.getData((DataRegion16)dataRegion, (FileAllocationTable16)fileAllocationTable);
				//
				//If not folder
				//if (fileData != null)
				//{
				//	System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
				//	System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
				//	
				//	String md5 = Hash.getMd5FromFileData(fileData);
				//	System.out.println("MD5 digest(in hex format):: " + md5);
				//}
				
			}
			
		}
		
		return arrayListFiles;
	}
	*/
	
	public ArrayList<FatEntry> subDirectory(long address) throws IOException, NotEnoughBytesReadException
	{
		DataRegion16 dataRegion16 = (DataRegion16)dataRegion;
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		
		ArrayList<FatEntry> arrayListFiles = new ArrayList<FatEntry>();
		
		//VFAT: We need to concatenate long file name entries to get long file name
		ArrayList<Fat16EntryLongFileName> lfnEntryList = new ArrayList<Fat16EntryLongFileName>();
		
		for (int entryNumber = 0; entryNumber < (int)maxEntriesInRootDirectory; entryNumber++)
		{
			//long entryAddress = calculateRootDirectoryEntryAddress((char)entryNumber, buffer);
			
			//calculateSubDirectoryEntryAddress(long address, char entryNumber, byte buffer[])
			long entryAddress = calculateSubDirectoryEntryAddress(address, (char)entryNumber);
			
			//FatEntry entry = new FatEntry((char) entryNumber, entryAddress, this.fileSystemIO);
			FatEntry entry = new FatEntry(bootBlock, fileAllocationTable, dataRegion, (char) entryNumber, entryAddress, this.fileSystemIO);
			
			//bootBlock, fileAllocationTable, dataRegion, 
			
			FilenameStatus filenameStatus = entry.getFilenameStatus();
			
			//System.out.println("filenameStatus: " + filenameStatus);
			
			//Break for loop if end-of-list
			if (filenameStatus == FilenameStatus.entryIsAvailableAndNoSubsequentEntryIsInUse)
				break;
			
			//System.out.println("\t entryNumber = : " + entryNumber);
			//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			//System.out.println("entry.isLongFilenameEntry(): " + entry.isLongFilenameEntry());
			//System.out.println("entry.getFilenameStatus(): " + entry.getFilenameStatus());
			
			if (filenameStatus != FilenameStatus.entryHasBeenPreviouslyErasedAndIsNotAvailable)
			{
				if (entry.isLongFilenameEntry())
				{
					Fat16EntryLongFileName longFileNameEntry = new Fat16EntryLongFileName(bootBlock, fileAllocationTable, dataRegion, (char)entryNumber, entryAddress, this.fileSystemIO);
					//System.out.println("longFileNameEntry.isLast(): " + longFileNameEntry.isLast());
					
					//Add long file name entry to arraylist
					lfnEntryList.add(longFileNameEntry);
					
					char lfnNumber = longFileNameEntry.getLFNNumber();
					boolean lfnLast = longFileNameEntry.isLast();
					System.out.println("lfnNumber: " + (int)lfnNumber + ", lfnLast: " + lfnLast);
				}
				else
				{
					Fat16Entry dosFilename = new Fat16Entry(bootBlock, fileAllocationTable, dataRegion, (char)entryNumber, entryAddress, this.fileSystemIO);
					boolean isSubdirectory = dosFilename.isSubdirectoryEntry();
					
					//----------------------------------------------------------------
					//VFAT
					System.out.println("lfnEntryList.size(): " + lfnEntryList.size() + " " + dosFilename.getFilename());
					
					//Depending on the length of the long filename, the system will create a number of
					//invalid 8.3 entries in the Directory Table, these are the LFN (Long Filename)
					//entries. These LFN entries are stored with the with the last LFN entry topmost,
					//and the first LFN entry just above a valid Directory Entry.
					//
					//					Directory Example
					//	Entry Nr. 	Without LFN Entries 	With LFN Entries
					//	...			...						...
					//	n			Normal 1				Normal 1
					//	n+1			Normal 2				LFN for Normal 2 - Part 3
					//	n+2			Normal 3				LFN for Normal 2 - Part 2
					//	n+3			Normal 4				LFN for Normal 2 - Part 1
					//	n+4			Normal 5				Normal 2
					//	n+5			Normal 6				Normal 3
					//	...			...						...
					
					//There were no LFN Entries before this 8.3 entry => no long file name exists
					//Directory . and .. have none. When VFAT is used even very short names like
					//"1.txt" use LFN Entries to store the LongFileName (in this example the
					//same name is stored in 8.3 entry since "1.txt" fits into max 8byte field)
					if (lfnEntryList.size() <= 0)
					{
						//Directory . and .. have none
						dosFilename.setLongFileName(dosFilename.getFilename());
					}
					//long file name exists
					else
					{
						String longFileName = "";
						for (int i = lfnEntryList.size() - 1; i >= 0; i--)
						{
							longFileName += lfnEntryList.get(i).getUnicodeString();
						}
						System.out.println("longFileName: " + longFileName);
						
						dosFilename.setLongFileName(longFileName);
					}
					
					//All LFN Entries so far belong to this 8.3 entry => clear the arraylist to start anew
					lfnEntryList.clear();
					//------------------------------------------------------------------
					
					arrayListFiles.add(dosFilename);
					
					if (isSubdirectory)
					{
						//System.out.println("isSubdirectory: " + isSubdirectory);
						long adr = dataRegion16.getClusterAddress(dosFilename.getStartingClusterNumber());
						
						//System.out.println("adr: " + adr);
						//System.out.printf("adr: 0x%02Xh\n", adr);
						
						byte temp[] = dataRegion16.getClusterData(adr);
						//for (int i = 0; i < temp.length; i++)
						//{
						//	System.out.printf("0x%02Xh ", temp[i]);
						//}
						//System.out.println();
					}
					
					/*
					byte fileData[] = dosFilename.getData(dataRegion16, fileAllocationTable16);
					
					//If not folder
					if (fileData != null)
					{
						//System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
						//System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
						
						String md5 = Hash.getMd5FromFileData(fileData);
						System.out.println("MD5 digest(in hex format):: " + md5);
					}
					*/
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
			long entryAddress = (long)rootDirectoryAddress + (long)entryNumber * FatEntry.rootDirectoryEntrySize;
			
			//System.out.println("entryAddress: " + entryAddress);
			//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			return entryAddress;
		}
		
		public long calculateSubDirectoryEntryAddress(long address, char entryNumber)
		{
			long entryAddress = (long)address + (long)entryNumber * FatEntry.rootDirectoryEntrySize;
			
			//System.out.println("entryAddress: " + entryAddress);
			//System.out.printf("\t sentryAddress: 0x%02Xh\n", entryAddress);
			
			return entryAddress;
		}

		public BootBlock getBootBlock() {
			return bootBlock;
		}

		public FileAllocationTable getFileAllocationTable() {
			return fileAllocationTable;
		}

		public DataRegion getDataRegion() {
			return dataRegion;
		}

		public long getRootDirectoryAddress() {
			return rootDirectoryAddress;
		}

		public char getMaxEntriesInRootDirectory() {
			return maxEntriesInRootDirectory;
		}

		public long getRootDirectorySizeInBytes() {
			return rootDirectorySizeInBytes;
		}

		public long getRootDirectorySizeInBlocks() {
			return rootDirectorySizeInBlocks;
		}
}
