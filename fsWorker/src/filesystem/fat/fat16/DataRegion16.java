package filesystem.fat.fat16;

import java.io.IOException;
import java.util.Arrays;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.io.FileSystemIO;

public class DataRegion16 extends DataRegion {
	protected BootBlock bootBlock;
	protected long dataRegionAddress;
	protected long bytesPerCluster;
	
	public DataRegion16(FileSystemIO fileSystemIO)
	{
		super(fileSystemIO);
	}
	
	@Override
	public void initDataRegion(BootBlock bootBlock) {
		this.bootBlock = bootBlock;

		BootBlock16 bootBlock16 = (BootBlock16)bootBlock;
		
		long rootDirectoryAddress = Fat16Directory.calculateRootDirectoryAddress(bootBlock16);
		
		System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
		System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
		
		long rootDirectorySizeInBytes = Fat16Directory.calculateRootDirectorySizeInBytes(bootBlock16);
		
		System.out.println("rootDirectorySizeInBytes: " + rootDirectorySizeInBytes);
		System.out.printf("rootDirectorySizeInBytes: 0x%02Xh\n", rootDirectorySizeInBytes);
		
		dataRegionAddress = rootDirectoryAddress + rootDirectorySizeInBytes;
		
		System.out.println("dataRegionAddress: " + dataRegionAddress);
		System.out.printf("dataRegionAddress: 0x%02Xh\n", dataRegionAddress);
		
		bytesPerCluster = bootBlock.getBPB_BytsPerSec() * bootBlock.getBPB_SecPerClus();
		System.out.println("bytesPerCluster: " + bytesPerCluster);
	}
	// Finds address in FAT for a certain data clusterNumber
	//The first cluster of the data area is cluster #2. That leaves the first two entries of the FAT unused.
	//In the first byte of the first fat entry a copy of the media descriptor is stored. The remaining bits of
	//this fat entry are 1. In the second fat entry the end-of-file marker is stored.
	public long getClusterAddress(char clusterNumber)
	{
		//long address1 = address + bytesPerCluster * 1;
		long address = dataRegionAddress + bytesPerCluster * ((long)clusterNumber - 2);
		return address;
	}
	
	public byte[] getClusterData(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		return getClusterData(getClusterAddress(clusterNumber));
	}
	
	public byte[] getClusterData(long address) throws IOException, NotEnoughBytesReadException
	{
		//byte cluster [] = Arrays.copyOfRange(buffer, (int)address, (int)address + (int)bytesPerCluster);
		byte cluster [] = fileSystemIO.readFSImage(address, (int)bytesPerCluster);
		
		//System.out.printf("from address: 0x%02Xh to including address: 0x%02Xh\n", (int)address, (int)address + (int)bytesPerCluster - 1);
		return cluster;
	}
	
	//Returns true if successful
	public boolean setClusterData(long address, byte[] data) throws IOException
	{
		if (data == null || data.length != bytesPerCluster)
			return false;
		
		fileSystemIO.writeFSImage(address, data);
		return true;
	}

	public BootBlock getBootBlock() {
		return bootBlock;
	}

	public long getDataRegionAddress() {
		return dataRegionAddress;
	}

	public long getBytesPerCluster() {
		return bytesPerCluster;
	}
}
