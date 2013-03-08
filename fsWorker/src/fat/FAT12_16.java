package fat;

import java.util.ArrayList;


public class FAT12_16 {
	private ArrayList<Long> FATAddresses = new ArrayList<Long>();
	private long sizeOfOneFAT;
	private char numberOfFATs;
	private byte[] buffer;
	
	public FAT12_16(BIOSParameterBlock biosParameterBlock, byte[] buffer)
	{
		//count of sectors occupied by ONE FAT
		sizeOfOneFAT = biosParameterBlock.getFATSz();
		
		//count of FAT data structures on the volume
		numberOfFATs = biosParameterBlock.getBPB_NumFATs();
		
		//Number of reserved sectors in the Reserved region of the volume
		//starting at the first sector of the volume.
		char numberOfReservedSectors = biosParameterBlock.getBPB_RsvdSecCnt();
		
		char bytesPerSector = biosParameterBlock.getBPB_BytsPerSec();
		
		//Address of FAT
		//long rootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs + (long)numberOfReservedSectors) * (long)bytesPerSector;
		//System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		//System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
		//long FAT1Address = (long)numberOfReservedSectors * (long)bytesPerSector;
		//long FAT2Address = (sizeOfOneFAT + (long)numberOfReservedSectors) * (long)bytesPerSector;
		
		//System.out.println("xFAT1Address: " + FAT1Address);
		//System.out.printf("xFAT1Address: 0x%02Xh\n", FAT1Address);
		
		//System.out.println("xFAT2Address: " + FAT2Address);
		//System.out.printf("xFAT2Address: 0x%02Xh\n", FAT2Address);
		
		
		for (int i = 0; i < (int)numberOfFATs; i++)
		{
			long address = (i * sizeOfOneFAT + (long)numberOfReservedSectors) * (long)bytesPerSector;
			FATAddresses.add(new Long(address));
		}
		
		System.out.println("Display FAT addresses:");
		for (int i = 0; i < (int)numberOfFATs; i++)
		{
			System.out.println("FATTableAddresses.get(" + i + "): " + FATAddresses.get(i));
			System.out.printf("FATTableAddresses.get(%d): 0x%02Xh\n", i, FATAddresses.get(i));
		}
		
		this.buffer = buffer;
	}
	
	public long getFATPointerAddress(char clusterNumber)
	{
		//Get pointer from FAT
		//long FATPointerAddress = FAT1Address + (long)startingClusterNumber * 2;
		long FATPointerAddress = FATAddresses.get(0) + (long)clusterNumber * (long)2;
		
		return FATPointerAddress;
	}
	
	public char getFATPointerValue(long fatPointerAddress)
	{
		char newClusterNumber = DataConverter.getValueFrom2Bytes(buffer, (int)fatPointerAddress);
		return newClusterNumber;
	}

	public ArrayList<Long> getFATAddresses() {
		return FATAddresses;
	}

	public long getSizeOfOneFAT() {
		return sizeOfOneFAT;
	}

	public char getNumberOfFATs() {
		return numberOfFATs;
	}

	public byte[] getBuffer() {
		return buffer;
	}
}
