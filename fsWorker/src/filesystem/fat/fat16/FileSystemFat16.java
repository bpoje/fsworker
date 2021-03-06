package filesystem.fat.fat16;


import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import filesystem.FileSystemEntry;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.FatEntry;
import filesystem.fat.FileSystemFat;
import filesystem.hash.Hash;
import filesystem.io.DataConverter;
import filesystem.io.DataTransfer;
import filesystem.io.FileSystemIO;

public class FileSystemFat16 extends FileSystemFat{

	protected int currentDirectoryDepth = 0;
	protected long currentDirectoryAddress = 0;
	protected String currentDirectoryPath = "/";
	
	public FileSystemFat16(String filenameFSImage, FileSystemIO fileSystemIO) throws IllegalArgumentException, IOException, NotEnoughBytesReadException
	{		
		super(filenameFSImage, fileSystemIO, new FileAllocationTable16(fileSystemIO), new BootBlock16(fileSystemIO), new Fat16Directory(fileSystemIO), new DataRegion16(fileSystemIO));
		
		fileAllocationTable.initFileAllocationTable(bootBlock);
		dataRegion.initDataRegion(bootBlock);
		fatDirectory.initFatDirectory(bootBlock, fileAllocationTable, dataRegion);
		
		this.currentDirectoryAddress = ((Fat16Directory)fatDirectory).getRootDirectoryAddress();
		
		System.out.println("End of Fat16 init => OK\n");
	}

	@Override public ArrayList<FatEntry> ls() throws IOException, NotEnoughBytesReadException
	{
		//System.out.printf("LS currentDirectoryAddress: 0x%02Xh\n", currentDirectoryAddress);
		
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		Fat16Directory fat16Directory = (Fat16Directory)fatDirectory;
		
		//ArrayList<FatEntry> filesInFolder = fat16Directory.directory();
		
		ArrayList<FatEntry> filesInFolder = fat16Directory.subDirectory(currentDirectoryAddress,currentDirectoryPath);
		
		//System.out.println(filesInFolder.size());
		
		return filesInFolder;
	}

	@Override
	public DataTransfer getData(FileSystemEntry entry) throws IOException, NotEnoughBytesReadException {
		if (entry == null)
			return new DataTransfer(null, "");
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		
		if (dosFilename.isLongFilenameEntry() || dosFilename.isSubdirectoryEntry())
			return new DataTransfer(null, "");
		
		DataTransfer dataTransfer = dosFilename.getData();
		return dataTransfer;
	}
	
	//Returns true if successful
	@Override
	public boolean cd(FileSystemEntry entry) {
		if (entry == null)
			return false;
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		DataRegion16 dataRegion16 = (DataRegion16)dataRegion;
		
		if (!dosFilename.isSubdirectoryEntry())
			return false;
		
		//System.out.println("cd dosFilename: " + dosFilename.getLongFileName() + ", currentDirectoryPath: " + this.currentDirectoryPath);
		
		/*
		System.out.println("isSubdirectoryEntry: " + dosFilename.isSubdirectoryEntry());
			long adr = dataRegion16.getClusterAddress(dosFilename.getStartingClusterNumber());
			
			System.out.println("adr: " + adr);
			System.out.printf("adr: 0x%02Xh\n", adr);
			
			byte temp[] = dataRegion16.getClusterData(adr);
			//for (int i = 0; i < temp.length; i++)
			//{
			//	System.out.printf("0x%02Xh ", temp[i]);
			//}
			//System.out.println();
			
			
			System.out.println("---------------------------------------------------------");
			Fat16Directory fat16Directory = (Fat16Directory)fatDirectory;
			ArrayList<FatEntry> list = fat16Directory.subDirectory(adr);
			System.out.println("---------------------------------------------------------");
		*/
		
		//System.out.printf("BEFORE currentDirectoryAddress: 0x%02Xh\n", currentDirectoryAddress);
		
		//System.out.println("isSubdirectoryEntry: " + dosFilename.isSubdirectoryEntry());
		
		//Adjust directory depth
		if (dosFilename.getFilename().compareToIgnoreCase("..") == 0)
		{
			currentDirectoryDepth--;
			
			//Adjust path string
			StringBuilder stringBuilder = new StringBuilder(this.currentDirectoryPath);
			
			//Remove '/' at the end of string
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			
			stringBuilder.replace(stringBuilder.lastIndexOf("/"), stringBuilder.length(), "/");
			
			this.currentDirectoryPath = stringBuilder.toString();
		}
		else if (dosFilename.getFilename().compareToIgnoreCase(".") == 0)
		{
			;	//Do nothing
		}
		else
		{
			currentDirectoryDepth++;
			
			//Adjust path string
			this.currentDirectoryPath += dosFilename.getLongFileName() + "/";
		}
		
		//Fat16:
		//Space for the ROOT directory is allocated statically, when the disk is formatted;
		//there is thus a finite upper limit on the number of files that can appear in the root directory
		//
		//Space for the subdirectories is allocated dynamically in data area
		//
		//As "ROOT directory" is not in the data area, we need to set the address manually to "Disk root
		//directory" when directory depth is 0.
		//-------------------------------------------------------------------------------------------------
		//			Area description								Area size
		//	Boot block 											1 block
		//	File Allocation Table (may be multiple copies) 		Depends on file system size
		//	Disk root directory 								Variable (selected when disk is formatted)
		//	File data area 										The rest of the disk
		//-------------------------------------------------------------------------------------------------
		
		if (currentDirectoryDepth <= 0)
		{
			currentDirectoryDepth = 0;
			this.currentDirectoryAddress = ((Fat16Directory)fatDirectory).getRootDirectoryAddress();
		}
		else
		{
			this.currentDirectoryAddress = dataRegion16.getClusterAddress(dosFilename.getStartingClusterNumber());
		}
		
		//System.out.printf("AFTER currentDirectoryAddress: 0x%02Xh\n", currentDirectoryAddress);
		
		//System.out.println("adr: " + currentDirectoryAddress);
		//System.out.printf("adr: 0x%02Xh\n", currentDirectoryAddress);
		
		//byte temp[] = dataRegion16.getClusterData(currentDirectoryAddress);
		//for (int i = 0; i < temp.length; i++)
		//{
		//	System.out.printf("0x%02Xh ", temp[i]);
		//}
		//System.out.println();
		
		
		//System.out.println("---------------------------------------------------------");
		//Fat16Directory fat16Directory = (Fat16Directory)fatDirectory;
		//ArrayList<FatEntry> list = fat16Directory.subDirectory(currentDirectoryAddress);
		//System.out.println("---------------------------------------------------------");
		
		//System.out.println("currentDirectoryPath: " + this.currentDirectoryPath);
		/*
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return true;
	}
	
	//Uses VFAT long file names
	//Returns true if successful
	//Old Windows filesystems (VFAT, FAT32) are not case-sensitive (there cannot be a readme.txt
	//and a Readme.txt in the same folder) but are case-preserving, i.e. remembering the case of
	//the letters.
	private void cdRoot() throws IOException, NotEnoughBytesReadException
	{
		//System.out.println("this.currentDirectoryPath: " + this.currentDirectoryPath);
		
		if (this.currentDirectoryPath.compareToIgnoreCase("/") == 0)
			return;
		
		
		//Do until we reach root directory /
		while (this.currentDirectoryPath.compareToIgnoreCase("/") != 0)
		{
			ArrayList<FatEntry> dirContent = this.ls();
			
			//Go trough all entries in current directory
			for (int i = 0; i < dirContent.size(); i++)
			{
				Fat16Entry fat16Entry = (Fat16Entry)dirContent.get(i);
				
				//If entry is .. => open it
				if (fat16Entry.isSubdirectoryEntry() && fat16Entry.getLongFileName().compareToIgnoreCase("..") == 0)
					this.cd(fat16Entry);
			}
		}
		
		return;
	}
	
	//Uses VFAT long file names
	//Returns true if successful
	//If unsuccessful the current path remains unchanged
	//Old Windows filesystems (VFAT, FAT32) are not case-sensitive (there cannot be a readme.txt
	//and a Readme.txt in the same folder) but are case-preserving, i.e. remembering the case of
	//the letters.
	public boolean cd(String newDirectoryPath) throws IOException, NotEnoughBytesReadException
	{
		if (newDirectoryPath == null || newDirectoryPath.length() < 1 || newDirectoryPath.charAt(0) != '/')
			return false;
		
		StringTokenizer st = new StringTokenizer(newDirectoryPath, "/");
		
		//Open root directory /
		cdRoot();
		
		int numberOfTokens = st.countTokens();
		
		//Go trough all foldernames in newDirectoryPath
		for (int i = 0; i < numberOfTokens; i++)
		{
			String folderName = st.nextToken();
			
			//System.out.println("folderName: " + folderName);
			
			//Get content of current folder
			ArrayList<FatEntry> dirContent = this.ls();
			
			//Go trough all entries in current directory
			//We are searching for folder with name equal to var. folderName
			Fat16Entry folderEntry = null;
			for (int j = 0; j < dirContent.size(); j++)
			{
				Fat16Entry fat16Entry = (Fat16Entry)dirContent.get(j);
				
				//If entry is the directory we seek => store reference and break from loop
				if (fat16Entry.isSubdirectoryEntry() && fat16Entry.getLongFileName().compareToIgnoreCase(folderName) == 0)
				{
					folderEntry = fat16Entry;
					break;
				}
			}
			
			if (folderEntry != null)
				this.cd(folderEntry);	//Folder was found => open it
			else
				return false;	//Folder was not found => return failure
		}
		
		return true;
	}

	@Override
	public void writeToSlack(FileSystemEntry entry, byte [] buffer) throws IOException, NotEnoughBytesReadException {
		if (entry == null || buffer == null)
			return;
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		
		if (dosFilename.isLongFilenameEntry() || dosFilename.isSubdirectoryEntry())
			return;
		
		dosFilename.writeToFileSlack(buffer);
		
		return;
	}

	@Override
	public DataTransfer readFromSlack(FileSystemEntry entry) throws IOException,
			NotEnoughBytesReadException {
		if (entry == null)
			return null;
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		
		if (dosFilename.isLongFilenameEntry() || dosFilename.isSubdirectoryEntry())
			return null;
		
		DataTransfer dataTransfer = dosFilename.readFromFileSlack();
		//System.out.println("MD5 digest(in hex format):: " + md5);
		return dataTransfer;
	}
	
	
	
	/*
	private void scanFileSystem(ArrayList<RootDirectoryEntry> files, DefaultMutableTreeNode treeNode)
	{
		for (int i = 0; i < files.size(); i++)
		{
			DOSFilename file = (DOSFilename)files.get(i);
			
			String filename = file.getFilename();
			String filenameExtension = file.getFilenameExtension();
			char startingClusterNumber = file.getStartingClusterNumber();
			long filesizeInBytes = file.getFilesizeInBytes();
			String md5 = file.calculateMd5(dataRegion, fat12_16);
			long totalClustersNeededForData = file.getTotalClustersNeededForData(dataRegion);
			long totalAllocatedSizeInBytes = totalClustersNeededForData * dataRegion.getBytesPerCluster();
			long fileSlackSizeInBytes = totalAllocatedSizeInBytes - filesizeInBytes;
			
			totalSlackFileSizeInBytes += fileSlackSizeInBytes;
			
			String sStartingClusterNumber = formatOutput((long)startingClusterNumber);
			String sFilesizeInBytes = formatOutput(filesizeInBytes);
			String sTotalClustersNeededForData = Long.toString(totalClustersNeededForData);
			String sTotalAllocatedSizeInBytes = formatOutput(totalAllocatedSizeInBytes);
			String sFileSlackSizeInBytes = formatOutput(fileSlackSizeInBytes);
			
			
			if (!file.isSubdirectoryEntry())
			{
				treeNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5,false)));
				
				//----------------------
				//long add = fat12_16.getLastFATPointerAddress(file.getStartingClusterNumber());
				//System.out.println("add: " + add);
				byte [] writeBuffer = new byte[2002];
				file.writeToFileSlack(dataRegion, fat12_16, writeBuffer);
				//----------------------
			}
			else
			{
				//DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,"","",true));
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5,true));
				
				long adr = dataRegion.getClusterAddress(file.getStartingClusterNumber());
				ArrayList<RootDirectoryEntry> filesInSubdir = rootDir.subDirectory(adr);
				
				System.out.println("filesInSubdir.size(): " + filesInSubdir.size());
				
				//Ignore . and .. in search
				if (filename.compareToIgnoreCase(".") != 0 && filename.compareToIgnoreCase("..") != 0)
					scanFileSystem(filesInSubdir, subNode);
				
				//subNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,false)));
				treeNode.add(subNode);
			}
		}
	}
	*/
	
	//Returns true if successful
	public boolean writeFakeBadCluster(char clusterNumber, byte[] data) throws IOException, NotEnoughBytesReadException
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		DataRegion16 dataRegion16 = (DataRegion16)dataRegion;
		
		//Is cluster available
		boolean isAvailable = fileAllocationTable16.isClusterAvailable(clusterNumber);
		
		//Is cluster bad
		boolean isBad = fileAllocationTable16.isClusterBad(clusterNumber);
		
		//System.out.println("isAvailable: " + isAvailable);
		//System.out.println("isBad: " + isBad);
		
		//If cluster is not available and is not bad cluster => failed
		if (!isAvailable && !isBad)
			return false;
		
		//Mark cluster as bad
		fileAllocationTable16.setClusterBad(clusterNumber);
		
		//System.out.println("isAvailable: " + fileAllocationTable16.isClusterAvailable(clusterNumber));
		//System.out.println("isBad: " + fileAllocationTable16.isClusterBad(clusterNumber));
		
		long dataAddress = dataRegion16.getClusterAddress(clusterNumber);
		//System.out.printf("dataAddress: 0x%02Xh\n", dataAddress);
		boolean writeSuccess = dataRegion16.setClusterData(dataAddress, data);
		
		return writeSuccess;
	}
	
	public DataTransfer readFakeBadCluster(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		DataRegion16 dataRegion16 = (DataRegion16)dataRegion;
		
		//Is cluster available
		boolean isAvailable = fileAllocationTable16.isClusterAvailable(clusterNumber);
		
		//Is cluster bad
		boolean isBad = fileAllocationTable16.isClusterBad(clusterNumber);
		
		//System.out.println("isAvailable: " + isAvailable);
		//System.out.println("isBad: " + isBad);
		
		//If cluster is not bad => failed
		if (!isBad)
			return new DataTransfer(null, "");
		
		long dataAddress = dataRegion16.getClusterAddress(clusterNumber);
		//System.out.printf("dataAddress: 0x%02Xh\n", dataAddress);
		byte data[] = dataRegion16.getClusterData(dataAddress);
		
		return new DataTransfer(data);
	}
	
	//Mark cluster as available
	public void clearFakeBadCluster(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		
		//Is cluster bad
		boolean isBad = fileAllocationTable16.isClusterBad(clusterNumber);
		
		//System.out.println("isBad: " + isBad);
		
		//If cluster is not bad => failed
		if (!isBad)
			return;
		
		//Mark cluster as available
		fileAllocationTable16.setClusterAvailable(clusterNumber);
	}
	
	public long getBytesPerCluster()
	{
		return ((DataRegion16)dataRegion).getBytesPerCluster();
	}
	
	public long getCountofClustersInDataRegion()
	{
		return bootBlock.getCountofClustersInDataRegion();
	}
	
	public long getFATSizeInEntries() {
		return ((FileAllocationTable16)fileAllocationTable).getFATSizeInEntries();
	}
	
	public boolean isClusterAvailable(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		return fileAllocationTable16.isClusterAvailable(clusterNumber);
	}
	
	public boolean isClusterBad(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		return fileAllocationTable16.isClusterBad(clusterNumber);
	}
	
	public long getFATPointerAddress(char clusterNumber)
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		return fileAllocationTable16.getFATPointerAddress(clusterNumber);
	}
	
	public char getFATPointerValue(char clusterNumber) throws IOException, NotEnoughBytesReadException {
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		return fileAllocationTable16.getFATPointerValue(clusterNumber);
	}
	
	//The first cluster of the data area is cluster #2. That leaves the first two entries of the FAT unused.
	public long getDataClusterAddress(char clusterNumber)
	{
		return ((DataRegion16)dataRegion).getClusterAddress(clusterNumber);
	}
	
	public byte[] getClusterData(char clusterNumber) throws IOException, NotEnoughBytesReadException
	{
		return ((DataRegion16)dataRegion).getClusterData(clusterNumber);
	}

	public int getCurrentDirectoryDepth() {
		return currentDirectoryDepth;
	}

	public long getCurrentDirectoryAddress() {
		return currentDirectoryAddress;
	}

	public String getCurrentDirectoryPath() {
		return currentDirectoryPath;
	}
	
	public String pwd()
	{
		return currentDirectoryPath;
	}
}
