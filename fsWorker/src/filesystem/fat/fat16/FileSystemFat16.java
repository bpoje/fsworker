package filesystem.fat.fat16;


import java.io.IOException;
import java.util.ArrayList;

import filesystem.FileSystemEntry;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.FatEntry;
import filesystem.fat.FileSystemFat;
import filesystem.hash.Hash;
import filesystem.io.FileSystemIO;

public class FileSystemFat16 extends FileSystemFat{

	protected int currentDirectoryDepth = 0;
	protected long currentDirectoryAddress = 0;
	
	public FileSystemFat16(String filenameFSImage, FileSystemIO fileSystemIO) throws IllegalArgumentException, IOException, NotEnoughBytesReadException
	{		
		super(filenameFSImage, fileSystemIO, new FileAllocationTable16(fileSystemIO), new BootBlock16(fileSystemIO), new Fat16Directory(fileSystemIO), new DataRegion16(fileSystemIO));
		
		fileAllocationTable.initFileAllocationTable(bootBlock);
		dataRegion.initDataRegion(bootBlock);
		fatDirectory.initFatDirectory(bootBlock, fileAllocationTable, dataRegion);
		
		this.currentDirectoryAddress = ((Fat16Directory)fatDirectory).getRootDirectoryAddress();
		
		System.out.println("End of Fat16 init!\n");
	}

	@Override public ArrayList<FatEntry> ls() throws IOException, NotEnoughBytesReadException
	{
		System.out.printf("LS currentDirectoryAddress: 0x%02Xh\n", currentDirectoryAddress);
		
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		Fat16Directory fat16Directory = (Fat16Directory)fatDirectory;
		
		//ArrayList<FatEntry> filesInFolder = fat16Directory.directory();
		
		ArrayList<FatEntry> filesInFolder = fat16Directory.subDirectory(currentDirectoryAddress);
		
		//System.out.println(filesInFolder.size());
		
		return filesInFolder;
	}

	@Override
	public byte [] getData(FileSystemEntry entry) throws IOException, NotEnoughBytesReadException {
		if (entry == null)
			return null;
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		
		if (dosFilename.isLongFilenameEntry() || dosFilename.isSubdirectoryEntry())
			return null;
		
		byte fileData[] = dosFilename.getData((DataRegion16)dataRegion, (FileAllocationTable16)fileAllocationTable);
		
		//If not folder
		if (fileData != null)
		{
			//System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
			//System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
			
			String md5 = Hash.getMd5FromFileData(fileData);
			System.out.println("MD5 digest(in hex format):: " + md5);
		}
		
		return fileData;
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
		
		System.out.printf("BEFORE currentDirectoryAddress: 0x%02Xh\n", currentDirectoryAddress);
		
		//System.out.println("isSubdirectoryEntry: " + dosFilename.isSubdirectoryEntry());
		
		//Adjust directory depth
		if (dosFilename.getFilename().compareToIgnoreCase("..") == 0)
		{
			currentDirectoryDepth--;
		}
		else if (dosFilename.getFilename().compareToIgnoreCase(".") == 0)
		{
			;	//Do nothing
		}
		else
		{
			currentDirectoryDepth++;
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
		
		System.out.printf("AFTER currentDirectoryAddress: 0x%02Xh\n", currentDirectoryAddress);
		
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
		
		return true;
	}

	@Override
	public void writeToSlack(FileSystemEntry entry, byte [] buffer) throws IOException, NotEnoughBytesReadException {
		if (entry == null || buffer == null)
			return;
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		
		if (dosFilename.isLongFilenameEntry() || dosFilename.isSubdirectoryEntry())
			return;
		
		dosFilename.writeToFileSlack((DataRegion16)this.dataRegion, (FileAllocationTable16)this.fileAllocationTable, buffer);
		
		return;
	}

	@Override
	public byte[] readFromSlack(FileSystemEntry entry) throws IOException,
			NotEnoughBytesReadException {
		if (entry == null)
			return null;
		
		Fat16Entry dosFilename = (Fat16Entry)entry;
		
		if (dosFilename.isLongFilenameEntry() || dosFilename.isSubdirectoryEntry())
			return null;
		
		byte fileSlackData[] = dosFilename.readFromFileSlack((DataRegion16)this.dataRegion, (FileAllocationTable16)this.fileAllocationTable);
		
		//If not folder
		if (fileSlackData != null)
		{
			//System.out.println("\t\t\t\t\t\t\t\tfileData.length: " + (fileData.length));
			//System.out.println("\t\t\t\t\t\t\t\tdosFilename.getFilesizeInBytes(): " + dosFilename.getFilesizeInBytes());
			
			String md5 = Hash.getMd5FromFileData(fileSlackData);
			System.out.println("MD5 digest(in hex format):: " + md5);
		}
		
		return fileSlackData;
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
	
	
}
