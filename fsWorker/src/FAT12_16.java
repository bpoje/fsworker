
public class FAT12_16 {

	public FAT12_16(BIOSParameterBlock biosParameterBlock)
	{
		//count of sectors occupied by ONE FAT
		long sizeOfOneFAT = biosParameterBlock.getFATSz();
		
		//count of FAT data structures on the volume
		char numberOfFATs = biosParameterBlock.getBPB_NumFATs();
		
		
		
		
		
		
		
		
		
	}
}
