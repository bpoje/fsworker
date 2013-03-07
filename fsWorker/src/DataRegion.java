import java.util.Arrays;


public class DataRegion {
	private long dataRegionAddress;
	private long bytesPerCluster;
	private byte[] buffer;
	
	DataRegion(BIOSParameterBlock biosParameterBlock, byte[] buffer)
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
		
		bytesPerCluster = biosParameterBlock.getBPB_BytsPerSec() * biosParameterBlock.getBPB_SecPerClus();
		System.out.println("bytesPerCluster: " + bytesPerCluster);
		
		this.buffer = buffer;
	}
	
	public long getClusterAddress(char clusterNumber)
	{
		//long address1 = address + bytesPerCluster * 1;
		long address = dataRegionAddress + bytesPerCluster * ((long)clusterNumber - 2);
		return address;
	}
	
	public byte[] getClusterData(long address)
	{
		byte cluster [] = Arrays.copyOfRange(buffer, (int)address, (int)address + (int)bytesPerCluster);
		System.out.printf("from address: 0x%02Xh to including address: 0x%02Xh\n", (int)address, (int)address + (int)bytesPerCluster - 1);
		return cluster;
	}

	public long getDataRegionAddress() {
		return dataRegionAddress;
	}

	public long getBytesPerCluster() {
		return bytesPerCluster;
	}

	public byte[] getBuffer() {
		return buffer;
	}
}
