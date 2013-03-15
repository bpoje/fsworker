package filesystem.fat.fat16;

import java.io.IOException;
import java.util.Arrays;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.fat16.Fat16DirectoryRoot;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.io.FileSystemIO;

public class DataRegion16 extends DataRegion {
	protected long dataRegionAddress;
	protected long bytesPerCluster;
	
	public DataRegion16()
	{
		BootBlock16 bootBlock16 = (BootBlock16)bootBlock;
		
		long rootDirectoryAddress = Fat16DirectoryRoot.calculateRootDirectoryAddress(bootBlock16);
		
		System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
		
		long rootDirectorySizeInBytes = Fat16DirectoryRoot.calculateRootDirectorySizeInBytes(bootBlock16);
		
		System.out.println("rootDirectorySizeInBytes: " + rootDirectorySizeInBytes);
		System.out.printf("rootDirectorySizeInBytes: 0x%02Xh\n", rootDirectorySizeInBytes);
		
		dataRegionAddress = rootDirectoryAddress + rootDirectorySizeInBytes;
		
		System.out.println("dataRegionAddress: " + dataRegionAddress);
		System.out.printf("dataRegionAddress: 0x%02Xh\n", dataRegionAddress);
		
		bytesPerCluster = bootBlock.getBPB_BytsPerSec() * bootBlock.getBPB_SecPerClus();
		System.out.println("bytesPerCluster: " + bytesPerCluster);
	}
	
	public long getClusterAddress(char clusterNumber)
	{
		//long address1 = address + bytesPerCluster * 1;
		long address = dataRegionAddress + bytesPerCluster * ((long)clusterNumber - 2);
		return address;
	}
	
	public byte[] getClusterData(long address) throws IOException, NotEnoughBytesReadException
	{
		//byte cluster [] = Arrays.copyOfRange(buffer, (int)address, (int)address + (int)bytesPerCluster);
		byte cluster [] = fileSystemIO.readFSImage(address, (int)bytesPerCluster);
		
		System.out.printf("from address: 0x%02Xh to including address: 0x%02Xh\n", (int)address, (int)address + (int)bytesPerCluster - 1);
		return cluster;
	}
}
