import java.util.ArrayList;


public class FAT12_16 {
	private ArrayList<Long> FATAddresses = new ArrayList<Long>();
	private long sizeOfOneFAT;
	private char numberOfFATs;
	
	public FAT12_16(BIOSParameterBlock biosParameterBlock)
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
		
		
		
	}
}
