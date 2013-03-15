package filesystem.fat.fat16;

import java.io.IOException;
import java.util.ArrayList;

import fat.RootDirectoryEntry;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.FatEntry;
import filesystem.fat.FileSystemFat;
import filesystem.io.FileSystemIO;

public class FileSystemFat16 extends FileSystemFat{
	
	public FileSystemFat16(String filenameFSImage, FileSystemIO fileSystemIO) throws IllegalArgumentException, IOException, NotEnoughBytesReadException
	{		
		super(filenameFSImage, fileSystemIO, new FileAllocationTable16(fileSystemIO), new BootBlock16(fileSystemIO), new Fat16Directory(fileSystemIO), new DataRegion16(fileSystemIO));
		
		fileAllocationTable.initFileAllocationTable(bootBlock);
		dataRegion.initDataRegion(bootBlock);
		fatDirectory.initFatDirectory(bootBlock, fileAllocationTable, dataRegion);
		
		System.out.println("x123y:" + fatDirectory.getFileSystemIO());
	}

	@Override public void ls() throws IOException, NotEnoughBytesReadException
	{
		FileAllocationTable16 fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		Fat16Directory fat16Directory = (Fat16Directory)fatDirectory;
		
		ArrayList<FatEntry> filesInFolder = fat16Directory.directory();
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
