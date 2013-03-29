package filesystem.fat.fat16;

import java.io.IOException;
import java.util.ArrayList;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.BootBlock;
import filesystem.fat.FileAllocationTable;
import filesystem.io.DataConverter;
import filesystem.io.FileSystemIO;

public class FileAllocationTable16 extends FileAllocationTable {

	protected ArrayList<Long> FATAddresses = new ArrayList<Long>();
	protected long sizeOfOneFAT;
	protected char numberOfFATs;
	
	public FileAllocationTable16(FileSystemIO fileSystemIO) {
		super(fileSystemIO);
	}
	
	@Override
	public void initFileAllocationTable(BootBlock bootBlock) {

		BootBlock16 bootBlock16 = (BootBlock16) bootBlock;

		// count of sectors occupied by ONE FAT
		sizeOfOneFAT = bootBlock16.getFATSz();

		// count of FAT data structures on the volume
		numberOfFATs = bootBlock16.getBPB_NumFATs();

		// Number of reserved sectors in the Reserved region of the volume
		// starting at the first sector of the volume.
		char numberOfReservedSectors = bootBlock16.getBPB_RsvdSecCnt();

		char bytesPerSector = bootBlock16.getBPB_BytsPerSec();

		// Address of FAT
		// long rootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs +
		// (long)numberOfReservedSectors) * (long)bytesPerSector;
		// System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		// System.out.printf("rootDirectoryAddress: 0x%02Xh\n",
		// rootDirectoryAddress);
		// long FAT1Address = (long)numberOfReservedSectors *
		// (long)bytesPerSector;
		// long FAT2Address = (sizeOfOneFAT + (long)numberOfReservedSectors) *
		// (long)bytesPerSector;

		// System.out.println("xFAT1Address: " + FAT1Address);
		// System.out.printf("xFAT1Address: 0x%02Xh\n", FAT1Address);

		// System.out.println("xFAT2Address: " + FAT2Address);
		// System.out.printf("xFAT2Address: 0x%02Xh\n", FAT2Address);

		for (int i = 0; i < (int) numberOfFATs; i++) {
			long address = (i * sizeOfOneFAT + (long) numberOfReservedSectors) * (long) bytesPerSector;
			System.out.println("address123: " + address + "," + " i: " + i + ", " + "sizeOfOneFAT: " + sizeOfOneFAT);
			FATAddresses.add(new Long(address));
		}

		System.out.println("Display FAT addresses:");
		for (int i = 0; i < (int) numberOfFATs; i++) {
			System.out.println("FATTableAddresses.get(" + i + "): "
					+ FATAddresses.get(i));
			System.out.printf("FATTableAddresses.get(%d): 0x%02Xh\n", i,
					FATAddresses.get(i));
		}
	}
	
	// Finds address in FAT for a certain data clusterNumber
	//The first cluster of the data area is cluster #2. That leaves the first two entries of the FAT unused.
	//In the first byte of the first entry a copy of the media descriptor is stored. The remaining bits of
	//this entry are 1. In the second entry the end-of-file marker is stored.
	public long getFATPointerAddress(char clusterNumber) {
		// Get pointer from FAT
		// long FATPointerAddress = FAT1Address + (long)startingClusterNumber *
		// 2;
		long FATPointerAddress = FATAddresses.get(0) + (long) clusterNumber
				* (long) 2;

		return FATPointerAddress;
	}
	
	// Reads entry from FAT at certain address
	public char getFATPointerValue(long fatPointerAddress) throws IOException, NotEnoughBytesReadException {
		//char newClusterNumber = DataConverter.getValueFrom2Bytes(buffer,
		//		(int) fatPointerAddress);
		
		byte buffer[] = fileSystemIO.readFSImage(fatPointerAddress, 2);
		char newClusterNumber = DataConverter.getValueFrom2Bytes(buffer,0);
		
		return newClusterNumber;
	}
	
	// Reads entry from FAT at certain address
	public char getFATPointerValue(char clusterNumber) throws IOException, NotEnoughBytesReadException {
		
		long fatPointerAddress = getFATPointerAddress(clusterNumber);
		byte buffer[] = fileSystemIO.readFSImage(fatPointerAddress, 2);
		char newClusterNumber = DataConverter.getValueFrom2Bytes(buffer,0);
		
		return newClusterNumber;
	}
	
	// Writes entry to FAT at certain address
	public void setFATPointerValue(long fatPointerAddress, char newClusterNumber) throws IOException, NotEnoughBytesReadException {
		
		byte buffer[] = DataConverter.get2BytesFromValue(newClusterNumber);
		fileSystemIO.writeFSImage(fatPointerAddress, buffer);
	}
	
	// Get the number of last data cluster for our file
	public char getLastFATPointerValue(char firstClusterNumber) throws IOException, NotEnoughBytesReadException {
		long address = 0;
		char nextClusterNumber = firstClusterNumber;

		do {
			firstClusterNumber = nextClusterNumber;
			address = getFATPointerAddress(firstClusterNumber);
			nextClusterNumber = getFATPointerValue(address);
		} while ((int) nextClusterNumber != (int) 0xFFFF);

		return firstClusterNumber;
	}
	
	
	//FAT Code Range	Meaning
	//0000h				Available Cluster
	//0002h-FFEFh		Used, Next Cluster in File
	//FFF0h-FFF6h		Reserved Cluster
	//FFF7h				BAD Cluster
	//FFF8h-FFFF		Used, Last Cluster in File
	public boolean isClusterAvailable(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		long fatEntryAddress = getFATPointerAddress(clusterNumber);
		//System.out.printf("clusterNumber: %d, fatEntryAddress: 0x%02Xh\n", (int)clusterNumber, fatEntryAddress);
		
		char numberOfDataCluster = getFATPointerValue(fatEntryAddress);
		return (numberOfDataCluster == 0x0000);
	}
	
	public boolean isClusterBad(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		long fatEntryAddress = getFATPointerAddress(clusterNumber);
		//System.out.printf("clusterNumber: %d, fatEntryAddress: 0x%02Xh\n", (int)clusterNumber, fatEntryAddress);
		
		char numberOfDataCluster = getFATPointerValue(fatEntryAddress);
		return (numberOfDataCluster == 0xFFF7);
	}
	
	public void setClusterAvailable(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		long fatEntryAddress = getFATPointerAddress(clusterNumber);
		setFATPointerValue(fatEntryAddress, (char) 0x0000);
	}
	
	public void setClusterBad(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		long fatEntryAddress = getFATPointerAddress(clusterNumber);
		setFATPointerValue(fatEntryAddress, (char) 0xFFF7);
	}
}
