package filesystem.fat.fat16;

import fat.RootDirectoryEntry;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.fat.FatDirectory;
import filesystem.fat.FileAllocationTable;

public class Fat16Directory extends FatDirectory {
	private long rootDirectoryAddress = 0;
	private char maxEntriesInRootDirectory = 0;
	
	private long rootDirectorySizeInBytes = 0;
	private long rootDirectorySizeInBlocks = 0;

	public Fat16Directory()
	{
		super();
	}
	
	@Override
	public void initFatDirectory(BootBlock bootBlock,
			FileAllocationTable fileAllocationTable, DataRegion dataRegion) {

		
		
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
		long myRootDirectorySizeInBytes = (long)myMaxEntriesInRootDirectory * RootDirectoryEntry.rootDirectoryEntrySize;
		return myRootDirectorySizeInBytes;
	}
	
	public static long calculateRootDirectorySizeInBlocks(BootBlock16 bootBlock16)
	{
		long myRootDirectorySizeInBytes = calculateRootDirectorySizeInBytes(bootBlock16);
		char bytesPerSector = bootBlock16.getBPB_BytsPerSec();
		long myRootDirectorySizeInBlocks = myRootDirectorySizeInBytes / (long)bytesPerSector;
		
		return myRootDirectorySizeInBlocks;
	}

	

	
}
