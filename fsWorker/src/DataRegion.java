
public class DataRegion {
	private long dataRegionAddress;
	
	DataRegion(BIOSParameterBlock biosParameterBlock)
	{	
		long rootDirectoryAddress = RootDirectory.calculateRootDirectoryAddress(biosParameterBlock);
		
		System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
		
		long rootDirectorySizeInBytes = RootDirectory.calculateRootDirectorySizeInBytes(biosParameterBlock);
		
		System.out.println("rootDirectorySizeInBytes: " + rootDirectorySizeInBytes);
		System.out.printf("rootDirectorySizeInBytes: 0x%02Xh\n", rootDirectorySizeInBytes);
		
		dataRegionAddress = rootDirectoryAddress + rootDirectorySizeInBytes;
		
		System.out.println("dataRegionAddress: " + dataRegionAddress);
		System.out.printf("dataRegionAddress: 0x%02Xh\n", dataRegionAddress);
	}
}
