package filesystem.fat.fat16;

import java.io.IOException;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.DataRegion;
import filesystem.fat.FilenameStatus;
import filesystem.fat.fat16.FileAllocationTable16;
import filesystem.fat.FatEntry;
import filesystem.hash.Hash;
import filesystem.io.DataConverter;
import filesystem.io.FileSystemIO;

public class Fat16Entry extends FatEntry {
	private String filename;
	private String filenameExtension;
	// private char fileAttributes;
	private char startingClusterNumber;
	private long filesizeInBytes;

	/*
	 * boolean isReadOnlyFile; boolean isHiddenFile; boolean isSystemFile;
	 * boolean isSpecialEntry; boolean isSubdirectoryEntry; boolean
	 * isArchiveFlag;
	 */

	public Fat16Entry(char entryNumber, long entryAddress, FileSystemIO fileSystemIO) throws IOException, NotEnoughBytesReadException {
		super(entryNumber, entryAddress, fileSystemIO);

		/*
		 * long entryAddress = (long)rootDirAddress + (long)entryNumber *
		 * RootDirectoryEntry.rootDirectoryEntrySize;
		 * 
		 * System.out.println("entryAddress: " + entryAddress);
		 * System.out.printf("entryAddress: 0x%02Xh\n", entryAddress);
		 */

		/*
		 * //Check first byte of filename //(The first byte of the filename
		 * indicates its status. Usually, it contains a normal filename
		 * //character (e.g. 'A'), but there are some special values) byte
		 * filenameStatus = buffer[(int)entryAddress];
		 * 
		 * //System.out.println("filenameStatus: " + filenameStatus);
		 * //System.out.printf("filenameStatus: 0x%02Xh\n", filenameStatus);
		 * 
		 * switch (filenameStatus) { //Entry is available and no subsequent
		 * entry is in use case (byte)0x00: //System.out.println(
		 * "filenameStatus: Entry is available and no subsequent entry is in use"
		 * ); break;
		 * 
		 * //Entry has been previously erased and is not available. File
		 * undelete utilities must replace this character with a regular
		 * character as part of the undeletion process case (byte)0xe5:
		 * //System.out.println(
		 * "filenameStatus: Entry has been previously erased and is not available. File undelete utilities must replace this character with a regular character as part of the undeletion process"
		 * ); break;
		 * 
		 * //Initial character is actually 0xE5 case (byte)0x05:
		 * //System.out.println
		 * ("filenameStatus: Initial character is actually 0xE5"); break;
		 * 
		 * //'Dot' entry; either '.' or '..' case (byte)0x2e:
		 * //System.out.println
		 * ("filenameStatus: 'Dot' entry; either '.' or '..'"); break;
		 * 
		 * //Any other character //This is the first character of a real
		 * filename. //default: //
		 * System.out.println("filenameStatus: Real filename entry"); }
		 */

		// Get filename
		// If a filename is fewer than eight characters in length, it is padded
		// with space characters.
		//filename = DataConverter
		//		.getStringFrom8Bytes(buffer, (int) entryAddress);
		byte buffer[] = fileSystemIO.readFSImage(entryAddress, (int)rootDirectoryEntrySize);
		filename = DataConverter
						.getStringFrom8Bytes(buffer, 0);

		// Remove padding
		int numberOfAdditionalSpaces = 0;
		for (int i = filename.length() - 1; i >= 0 && filename.charAt(i) == ' '; i--)
			numberOfAdditionalSpaces++;

		// System.out.println("numberOfAdditionalSpaces: " +
		// numberOfAdditionalSpaces);

		filename = filename.substring(0, filename.length()
				- numberOfAdditionalSpaces);

		// System.out.println("filename: " + filename);

		// If the filename extension is fewer than three characters in length,
		// it is padded with space characters.
		//filenameExtension = DataConverter.getStringFrom3Bytes(buffer,
		//		(int) entryAddress + 8);
		filenameExtension = DataConverter.getStringFrom3Bytes(buffer,
						(int) 8);

		// Remove padding
		numberOfAdditionalSpaces = 0;
		for (int i = filenameExtension.length() - 1; i >= 0
				&& filenameExtension.charAt(i) == ' '; i--)
			numberOfAdditionalSpaces++;

		// System.out.println("numberOfAdditionalSpaces: " +
		// numberOfAdditionalSpaces);

		filenameExtension = filenameExtension.substring(0,
				filenameExtension.length() - numberOfAdditionalSpaces);

		// System.out.println("filenameExtension: " + filenameExtension);

		// Note that the dot used to separate the filename and the filename
		// extension is implied,
		// and is not actually stored anywhere; it is just used when referring
		// to the file. If the
		// filename extension is fewer than three characters in length, it is
		// padded with space
		// characters.
		
		//System.out.println("file: " + filename + "." + filenameExtension);

		/*
		 * //File atributes fileAttributes =
		 * DataConverter.getValueFrom1Byte(buffer, (int)entryAddress + 11);
		 * 
		 * //System.out.println("fileAttributes: " + (int)fileAttributes);
		 * //System.out.printf("fileAttributes: 0x%02Xh\n",
		 * (int)fileAttributes);
		 * 
		 * //0x01 Indicates that the file is read only. isReadOnlyFile =
		 * (fileAttributes & (0x01 << 0)) != 0;
		 * 
		 * //0x02 Indicates a hidden file. Such files can be displayed if it is
		 * really required. isHiddenFile = (fileAttributes & (0x01 << 1)) != 0;
		 * 
		 * //0x04 Indicates a system file. These are hidden as well.
		 * isSystemFile = (fileAttributes & (0x01 << 2)) != 0;
		 * 
		 * //0x08 Indicates a special entry containing the disk's volume label,
		 * instead of //describing a file. This kind of entry appears only in
		 * the root directory. isSpecialEntry = (fileAttributes & (0x01 << 3))
		 * != 0;
		 * 
		 * //0x10 The entry describes a subdirectory. isSubdirectoryEntry =
		 * (fileAttributes & (0x01 << 4)) != 0;
		 * 
		 * //0x20 This is the archive flag. This can be set and cleared by the
		 * programmer or //user, but is always set when the file is modified. It
		 * is used by backup programs. isArchiveFlag = (fileAttributes & (0x01
		 * << 5)) != 0;
		 * 
		 * 
		 * System.out.println(); System.out.println("isReadOnly: " +
		 * isReadOnlyFile); System.out.println("isHidden: " + isHiddenFile);
		 * System.out.println("isSystemFile: " + isSystemFile);
		 * System.out.println("isSpecialEntry: " + isSpecialEntry);
		 * System.out.println("isSubdirectoryEntry: " + isSubdirectoryEntry);
		 * System.out.println("isArchiveFlag: " + isArchiveFlag);
		 * System.out.println();
		 */

		// Starting cluster number for file
		//startingClusterNumber = DataConverter.getValueFrom2Bytes(buffer,
		//		(int) entryAddress + 26);
		startingClusterNumber = DataConverter.getValueFrom2Bytes(buffer,
						(int) 26);

		//System.out.println("startingClusterNumber: "
		//		+ (int) startingClusterNumber);
		//System.out.printf("startingClusterNumber: 0x%02Xh\n",
		//		(int) startingClusterNumber);

		// File size in bytes
		//filesizeInBytes = DataConverter.getValueFrom4Bytes(buffer,
		//		(int) entryAddress + 28);
		filesizeInBytes = DataConverter.getValueFrom4Bytes(buffer,
						(int) 28);

		//System.out.println("filesizeInBytes: " + filesizeInBytes);

	}

	public long getTotalClustersNeededForData(DataRegion16 dataRegion16) {
		long bytesPerCluster = dataRegion16.getBytesPerCluster();
		long clustersNeeded = (long) Math.ceil((float) filesizeInBytes
				/ (float) bytesPerCluster);
		return clustersNeeded;
	}

	public void writeToFileSlack(DataRegion16 dataRegion16, FileAllocationTable16 fileAllocationTable16,
			byte[] writeBuffer) throws IOException, NotEnoughBytesReadException {
		if (filesizeInBytes <= 0)
			return;
		
		long bytesPerCluster = dataRegion16.getBytesPerCluster();
		long totalClustersNeededForData = (long) Math
				.ceil((float) filesizeInBytes / (float) bytesPerCluster);

		long totalAllocatedSizeInBytes = totalClustersNeededForData
				* bytesPerCluster;
		long fileSlackSizeInBytes = totalAllocatedSizeInBytes - filesizeInBytes;

		if (writeBuffer.length != fileSlackSizeInBytes) {
			System.out.println("Length not equal to slack size!");
			return;
		}

		char lastFATPointerValue = fileAllocationTable16
				.getLastFATPointerValue(startingClusterNumber);

		long dataClusterAddress = dataRegion16
				.getClusterAddress(lastFATPointerValue);

		System.out.println("dataClusterAddress: " + dataClusterAddress);
		System.out.printf("dataClusterAddress: 0x%02Xh\n", dataClusterAddress);

		byte cluster[] = dataRegion16.getClusterData(dataClusterAddress);
		
		 for (int i = 0; i < cluster.length; i++)
		 {
		 System.out.printf("0x%02Xh, ", cluster[i]);
		 }
		 System.out.println();
		int firstEmptyLocation = (int) (cluster.length - fileSlackSizeInBytes);

		System.out.println("firstEmptyLocation" + firstEmptyLocation);
		System.out.printf("firstEmptyLocation: 0x%02Xh\n", firstEmptyLocation);

		// TODO
		// 1. write to file slack to physical file
		// 2. implement a method that calculates file slack size for a file (you
		// need it to determine writeBuffer length)
		// 3. replace fileSlackSize calculations with 2 in scanFileSystem() and
		// here
		
		this.fileSystemIO.writeFSImage(dataClusterAddress + firstEmptyLocation, writeBuffer);

		//try {
		//	Thread.sleep(20000);
		//} catch (InterruptedException e) {
		//	// Auto-generated catch block
		//	e.printStackTrace();
		//}

	}
	
	public byte[] readFromFileSlack(DataRegion16 dataRegion16, FileAllocationTable16 fileAllocationTable16) throws IOException, NotEnoughBytesReadException
	{
		if (filesizeInBytes <= 0)
			return null;
		
		long bytesPerCluster = dataRegion16.getBytesPerCluster();
		long totalClustersNeededForData = (long) Math
				.ceil((float) filesizeInBytes / (float) bytesPerCluster);

		long totalAllocatedSizeInBytes = totalClustersNeededForData
				* bytesPerCluster;
		long fileSlackSizeInBytes = totalAllocatedSizeInBytes - filesizeInBytes;

		//if (writeBuffer.length != fileSlackSizeInBytes) {
		//	System.out.println("Length not equal to slack size!");
		//	return;
		//}

		char lastFATPointerValue = fileAllocationTable16
				.getLastFATPointerValue(startingClusterNumber);

		long dataClusterAddress = dataRegion16
				.getClusterAddress(lastFATPointerValue);

		System.out.println("dataClusterAddress: " + dataClusterAddress);
		System.out.printf("dataClusterAddress: 0x%02Xh\n", dataClusterAddress);

		byte cluster[] = dataRegion16.getClusterData(dataClusterAddress);
		
		 for (int i = 0; i < cluster.length; i++)
		 {
		 System.out.printf("0x%02Xh, ", cluster[i]);
		 }
		 System.out.println();
		int firstEmptyLocation = (int) (cluster.length - fileSlackSizeInBytes);

		System.out.println("firstEmptyLocation" + firstEmptyLocation);
		System.out.printf("firstEmptyLocation: 0x%02Xh\n", firstEmptyLocation);

		// TODO
		// 1. write to file slack to physical file
		// 2. implement a method that calculates file slack size for a file (you
		// need it to determine writeBuffer length)
		// 3. replace fileSlackSize calculations with 2 in scanFileSystem() and
		// here
		
		return this.fileSystemIO.readFSImage(dataClusterAddress + firstEmptyLocation, (int)fileSlackSizeInBytes);
	}

	public byte[] getData(DataRegion16 dataRegion16, FileAllocationTable16 fileAllocationTable16) throws IOException, NotEnoughBytesReadException {
		if (filesizeInBytes <= 0)
			return null;
		
		char clusterNumber = startingClusterNumber;
		//System.out.println("\t\t\tclusterNumber: " + (int) clusterNumber);
		
		long bytesPerCluster = dataRegion16.getBytesPerCluster();
		
		// long remainingDataBytes = filesizeInBytes;
		long clustersNeeded = (long) Math.ceil((float) filesizeInBytes
				/ (float) bytesPerCluster);
		
		//System.out.println("filesizeInBytes: " + filesizeInBytes);
		//System.out.println("bytesPerCluster: " + bytesPerCluster);
		//System.out.println("clustersNeeded: " + clustersNeeded);

		byte fileData[] = new byte[(int) (filesizeInBytes)];
		//System.out.println("fileData.length: " + fileData.length);
		int clusterCounter = 0;

		// while (remainingDataBytes > 0)
		while ((int) clusterNumber != (int) 0xFFFF) {
			long address = dataRegion16.getClusterAddress(clusterNumber);

			//System.out.println("address: " + address);
			//System.out.printf("address: 0x%02Xh\n", address);

			byte cluster[] = dataRegion16.getClusterData(address);

			// Copy cluster to fileData array
			for (int i = 0; i < cluster.length
					&& (int) (clusterCounter * bytesPerCluster) + i < fileData.length; i++) {
				fileData[(int) (clusterCounter * bytesPerCluster) + i] = cluster[i];
				// System.out.printf("0x%02Xh ", cluster[i]);
			}
			// System.out.println();

			clusterCounter++;

			// Get pointer from FAT
			long fatPointerAddress = fileAllocationTable16
					.getFATPointerAddress(clusterNumber);

			//System.out.println("fatPointerAddress: " + (int) fatPointerAddress);
			//System.out.printf("fatPointerAddress: 0x%02Xh\n",
			//		(int) fatPointerAddress);

			clusterNumber = fileAllocationTable16.getFATPointerValue(fatPointerAddress);

			//System.out.println("clusterNumber: " + (int) clusterNumber);
			//System.out.printf("clusterNumber: 0x%02Xh\n", (int) clusterNumber);
		}

		return fileData;
	}

	public String calculateMd5(DataRegion16 dataRegion16, FileAllocationTable16 fileAllocationTable16) throws IOException, NotEnoughBytesReadException {
		byte fileData[] = getData(dataRegion16, fileAllocationTable16);

		String md5 = "";

		// If not folder
		if (fileData != null) {
			//System.out.println("\t\t\t\t\t\t\t\tfileData.length: "
			//		+ (fileData.length));
			//System.out
			//		.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): "
			//				+ getFilesizeInBytes());

			md5 = Hash.getMd5FromFileData(fileData);
			//System.out.println("MD5 digest(in hex format):: " + md5);
		}

		return md5;
	}

	public String getFilename() {
		return filename;
	}

	public String getFilenameExtension() {
		return filenameExtension;
	}

	public char getStartingClusterNumber() {
		return startingClusterNumber;
	}

	public long getFilesizeInBytes() {
		return filesizeInBytes;
	}
}
