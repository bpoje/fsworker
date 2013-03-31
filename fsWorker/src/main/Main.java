package main;

import filesystem.FileSystemType;
import filesystem.fat.FatEntry;
import filesystem.fat.FileSystemFat;
import filesystem.fat.fat16.DataRegion16;
import filesystem.fat.fat16.Fat16Entry;
import filesystem.fat.fat16.FileSystemFat16;
import filesystem.hash.Hash;
import filesystem.io.DataTransfer;
import filesystem.io.FileSystemIO;
import filesystem.utils.OutputFormater;
import gui.MainView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formattable;
import java.util.Random;


public class Main {
	public static void main(String [] args)
	{
		if (args.length < 1)
		{
			System.out.println("Usage: FileWithPartition");
			return;
		}
		
		//System.out.println(new File(args[0]).getAbsolutePath());
		
		try
		{
			String filename = args[0];
			//-----------------------------------------------------------------------------
			//Get file system type
			FileSystemType type = FileSystemFat.getFatType(filename);
			System.out.println("Volume type: " + type);
			
			//Exit if file system is not supported
			if (type != FileSystemType.FAT16)
			{
				System.out.println("Unsuported file system! Exiting.");
				return;
			}
			
			//Create instances of filesystem and its filesystemio class
			FileSystemIO fileSystemIO = new FileSystemIO(filename);
			FileSystemFat16 fileSystemFAT16 = new FileSystemFat16(filename, fileSystemIO);
			//-----------------------------------------------------------------------------
			//Random testing
			/*
			ArrayList<FatEntry> filesInDirectory = fileSystemFAT16.ls();
			System.out.println("filesInDirectory.size(): " + filesInDirectory.size());
			
			for (int i = 0; i < filesInDirectory.size(); i++)
			{
				Fat16Entry fat16Entry = (Fat16Entry)filesInDirectory.get(i);
				
				System.out.println("Filename: " + fat16Entry.getFilename());
			}
			*/
			//-----------------------------------------------------------------------------
			byte[] data = new byte[(int) fileSystemFAT16.getBytesPerCluster()];
			
			for (int i = 0; i < data.length; i++)
				data[i] = (byte)(i % 256);
			
			char clusterNumber = 398;
			
			/*
			boolean success = fileSystemFAT16.writeFakeBadCluster(clusterNumber, data);
			System.out.println("success writeFakeBadCluster: " + success);
			
			DataTransfer dt = fileSystemFAT16.readFakeBadCluster(clusterNumber);
			
			if (dt.getPayload() != null)
			{
				byte [] temp = dt.getPayload();
				//OutputFormater.printArrayHex(dt.getPayload());
				//System.out.println("dt.getMd5(): " + dt.getMd5());
				
				for (int i = 0; i < temp.length; i++)
					if (data[i] != temp[i])
					{
						System.out.println("Read test failed!");
						break;
					}
			}
			*/
			
			//Clear cluster bad marking
			//fileSystemFAT16.clearFakeBadCluster(clusterNumber);
			
			//Output FAT table & clusters markings
			//for (int iClusterNumber = 0; iClusterNumber < fileSystemFAT16.getCountofClustersInDataRegion(); iClusterNumber++)
			////-------for (int iClusterNumber = 0; iClusterNumber < fileSystemFAT16.getFATSizeInEntries(); iClusterNumber++)
			//{
			//	long fatEntryAddress = fileSystemFAT16.getFATPointerAddress((char)iClusterNumber);	
			//	System.out.printf("clusterNumber: %d, fatEntryAddress: 0x%02Xh, isAvailable: %b, isClusterBad: %b\n", (int)iClusterNumber, fatEntryAddress, fileSystemFAT16.isClusterAvailable((char)iClusterNumber), fileSystemFAT16.isClusterBad((char)iClusterNumber));
			//}
			
			//Output data region
			
			//long numberOfDataClusters = fileSystemFAT16.getCountofClustersInDataRegion();
			//System.out.println("numberOfDataClusters: " + numberOfDataClusters);
			
			//The first cluster of the data area is cluster #2. That leaves the first two entries of the FAT unused.
			//for (int iClusterNumber = 2; iClusterNumber < 2 + numberOfDataClusters; iClusterNumber++)
			//{
			//	long dataClusterAddress = fileSystemFAT16.getDataClusterAddress((char) iClusterNumber);
			//	System.out.printf("clusterNumber: %d, dataClusterAddress: 0x%02Xh\n", (int)iClusterNumber, dataClusterAddress);
			//}
			//-----------------------------------------------------------------------------
			/*
			//EXAMPLE
			//A pointer to directory entry
			Fat16Entry directory = null;
			
			//Get the contents of the first (root) directory
			System.out.println("(ls /)");
			ArrayList<FatEntry> filesInDirectory = fileSystemFAT16.ls();
			
			//For all files/directories in the first (root) directory
			for (int i = 0; i < filesInDirectory.size(); i++)
			{
				Fat16Entry fat16Entry = (Fat16Entry)filesInDirectory.get(i);
				
				//Is file
				if(!fat16Entry.isSubdirectoryEntry())
				{
					System.out.println("Filename: " + fat16Entry.getFilename() + "." + fat16Entry.getFilenameExtension());
					
					//Read file data
					DataTransfer fileData = fat16Entry.getData();
					System.out.println("---: data length in bytes: " + fileData.getPayload().length + ", md5: " + fileData.getMd5());
					
					//Read file slack
					DataTransfer fileSlack = fat16Entry.readFromFileSlack();
					System.out.println("---: file slack length in bytes: " + fileSlack.getPayload().length + ", md5: " + fileSlack.getMd5());
					
					//Display entire file slack as sequence of bytes
					OutputFormater.printArrayHex(fileSlack.getPayload(), "File slack bytes:", "---: ");
					
					//Fill table with data we want to hide
					byte dataIWantToHide[] = new byte[(int)fat16Entry.getFileSlackSizeInBytes()];
					for (int j = 0; j < dataIWantToHide.length; j++)
						dataIWantToHide[j] = (byte)0xAA;
					
					//Display table that contains the data we are trying to hide
					OutputFormater.printArrayHex(dataIWantToHide, "Data i'm trying to hide:", "---: ");
					
					//Get md5 from secret data
					String md5DataIWantToHide = Hash.getMd5FromByteArray(dataIWantToHide);
					
					//Write secret data to file slack
					fat16Entry.writeToFileSlack(dataIWantToHide);
					System.out.println("---: Secret data writen to file slack");
					
					//Read file slack to test if write was successful
					DataTransfer testFileSlack = fat16Entry.readFromFileSlack();
					String testReadMd5 = testFileSlack.getMd5();
					
					if (md5DataIWantToHide.compareToIgnoreCase(testReadMd5) != 0)
						System.out.println("---: HASH NOT EQUAL! WRITE TO FILE SLACK FAILED!");
					else
						System.out.println("---: Write OK!");
					
					//Display file slack
					OutputFormater.printArrayHex(testFileSlack.getPayload(), "Data that was read from file slack:","---: ");
					
					System.out.println();
				}
				//Is folder
				else
				{
					System.out.println("Directory: " + fat16Entry.getFilename());
					
					//Remember the directory we just found
					directory = fat16Entry;
				}
			}
			
			//If some directory was found open it
			if (directory != null)
			{
				System.out.println("(cd " + directory.getFilename() + ")");
				fileSystemFAT16.cd(directory);
			}
			
			//Get the contents of the directory
			System.out.println("(ls)");
			filesInDirectory = fileSystemFAT16.ls();
			
			//For all files/directories in the directory
			for (int i = 0; i < filesInDirectory.size(); i++)
			{
				Fat16Entry fat16Entry = (Fat16Entry)filesInDirectory.get(i);
				
				//Is file
				if(!fat16Entry.isSubdirectoryEntry())
				{
					System.out.println("\tFilename: " + fat16Entry.getFilename() + "." + fat16Entry.getFilenameExtension());
				}
				//Is folder
				else
				{
					System.out.println("\tDirectory: " + fat16Entry.getFilename());
				}
			}
			*/
			//-----------------------------------------------------------------------------
			//Open GUI
			MainView mainView = new MainView("MainView", fileSystemFAT16);
			mainView.setVisible(true);
			//-----------------------------------------------------------------------------
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
