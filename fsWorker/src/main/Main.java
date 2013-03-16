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
			
			
			//Open GUI
			//MainView mainView = new MainView("MainView", fileSystemFAT16);
			//mainView.setVisible(true);
			
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		//------------
		
		
		/*if (true)
			return;
		
		try
		{
			String path = new java.io.File(".").getCanonicalPath();
			System.out.println("Current path: " + path);
			
			File file = new File(args[0]);
			FileInputStream fs = new FileInputStream(file);
			
			
			//System.out.println("buffer: " + buffer.toString());
			
			//for (int i = 0; i < buffer.length; i++)
			//	System.out.printf("%d 0x%02X\n", i, buffer[i]);
			
			//Data structure called the BPB (BIOS Parameter Block)
			BIOSParameterBlock biosParameterBlock = new BIOSParameterBlock(fs);
			
			FATType type = biosParameterBlock.getType();
			
			fs.close();
			
			fs = new FileInputStream(file);
			
			byte buffer[] = new byte[20 * 1024 * 1024];
			int numberOfBytesRead = fs.read(buffer);
			
			System.out.println("numberOfBytesRead: " + numberOfBytesRead);
			System.out.println("numberOfkBRead: " + numberOfBytesRead / 1024);
			System.out.println("numberOfMBRead: " + numberOfBytesRead / (1024 * 1024));
			
			switch(type)
			{
				case FAT12:
				case FAT16:
					
					
					
					
					//AA
					
					//count of sectors occupied by ONE FAT
					long sizeOfOneFAT = biosParameterBlock.getFATSz();
					
					//count of FAT data structures on the volume
					char numberOfFATs = biosParameterBlock.getBPB_NumFATs();
					
					
					//Number of reserved sectors in the Reserved region of the volume
					//starting at the first sector of the volume.
					char numberOfReservedSectors = biosParameterBlock.getBPB_RsvdSecCnt();
					
					char bytesPerSector = biosParameterBlock.getBPB_BytsPerSec();
					
					//Address of root directory
					long rootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs + (long)numberOfReservedSectors) * (long)bytesPerSector;
					System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
					System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
					
					//-AA
					
					
					//If a filename is fewer than eight characters in length, it is padded with space characters.
					String filename = DataConverter.getStringFrom8Bytes(buffer, (int)rootDirectoryAddress + 0);
					
					//Remove padding
					int numberOfAdditionalSpaces = 0;
					for (int i = filename.length() - 1; i >= 0 && filename.charAt(i) == ' '; i--)
						numberOfAdditionalSpaces++;
					
					System.out.println("numberOfAdditionalSpaces: " + numberOfAdditionalSpaces);
					
					filename = filename.substring(0, filename.length() - numberOfAdditionalSpaces);
					
					System.out.println("filename: " + filename);
					
					//If the filename extension is fewer than three characters in length, it is padded with space characters.
					String filenameExtension = DataConverter.getStringFrom3Bytes(buffer, (int)rootDirectoryAddress + 0);
					
					//Remove padding
					numberOfAdditionalSpaces = 0;
					for (int i = filenameExtension.length() - 1; i >= 0 && filenameExtension.charAt(i) == ' '; i--)
						numberOfAdditionalSpaces++;
					
					System.out.println("numberOfAdditionalSpaces: " + numberOfAdditionalSpaces);
					
					filenameExtension = filenameExtension.substring(0, filenameExtension.length() - numberOfAdditionalSpaces);
					
					System.out.println("filenameExtension: " + filenameExtension);
					
					//Note that the dot used to separate the filename and the filename extension is implied,
					//and is not actually stored anywhere; it is just used when referring to the file. If the
					//filename extension is fewer than three characters in length, it is padded with space
					//characters.
					System.out.println("file: " + filename + "." + filenameExtension);
					
					//File atributes
					char fileAttributes = DataConverter.getValueFrom1Byte(buffer, (int)rootDirectoryAddress + 11);
					
					System.out.println("fileAttributes: " + (int)fileAttributes);
					
					//0x01 Indicates that the file is read only.
					boolean isReadOnlyFile = 		(fileAttributes & (0x01 << 0)) != 0;
					
					//0x02 Indicates a hidden file. Such files can be displayed if it is really required.
					boolean isHiddenFile = 			(fileAttributes & (0x01 << 1)) != 0;
					
					//0x04 Indicates a system file. These are hidden as well.
					boolean isSystemFile = 			(fileAttributes & (0x01 << 2)) != 0;
					
					//0x08 Indicates a special entry containing the disk's volume label, instead of
					//describing a file. This kind of entry appears only in the root directory.
					boolean isSpecialEntry = 		(fileAttributes & (0x01 << 3)) != 0;
					
					//0x10 The entry describes a subdirectory.
					boolean isSubdirectoryEntry = 	(fileAttributes & (0x01 << 4)) != 0;
					
					//0x20 This is the archive flag. This can be set and cleared by the programmer or
					//user, but is always set when the file is modified. It is used by backup programs.
					boolean isArchiveFlag = 		(fileAttributes & (0x01 << 5)) != 0;
					
					System.out.println();
					System.out.println("isReadOnly: " + isReadOnlyFile);
					System.out.println("isHidden: " + isHiddenFile);
					System.out.println("isSystemFile: " + isSystemFile);
					System.out.println("isSpecialEntry: " + isSpecialEntry);
					System.out.println("isSubdirectoryEntry: " + isSubdirectoryEntry);
					System.out.println("isArchiveFlag: " + isArchiveFlag);
					System.out.println();
					
					//Starting cluster number for file
					char startingClusterNumber = DataConverter.getValueFrom2Bytes(buffer, (int)rootDirectoryAddress + 26);
					
					System.out.println("startingClusterNumber: " + (int)startingClusterNumber);
					System.out.printf("startingClusterNumber: 0x%02Xh\n", (int)startingClusterNumber);
					
					//File size in bytes
					long filesizeInBytes = DataConverter.getValueFrom4Bytes(buffer, (int)rootDirectoryAddress + 28);
					
					System.out.println("filesizeInBytes: " + filesizeInBytes);
					
					
					
					//Maximum number of entries in the root directory
					char maxEntriesInRootDirectory = biosParameterBlock.getBPB_RootEntCnt();
					
					//Calculate total space occupied by the root directory
					final long directoryEntrySize = 32; //bytes
					
					
					long rootDirectorySizeInBytes = (long)maxEntriesInRootDirectory * directoryEntrySize;
					long rootDirectorySizeInBlocks = rootDirectorySizeInBytes / (long)bytesPerSector;
					
					System.out.println("rootDirectorySizeInBytes: " + rootDirectorySizeInBytes);
					System.out.println("rootDirectorySizeInKBytes: " + rootDirectorySizeInBytes / 1024);
					System.out.println("rootDirectorySizeInBlocks: " + rootDirectorySizeInBlocks);
					
					
					DataRegion dataRegion = new DataRegion(biosParameterBlock, buffer);
					FAT12_16 fat12_16 = new FAT12_16(biosParameterBlock, buffer);
					RootDirectory rootDir = new RootDirectory(biosParameterBlock, buffer, dataRegion, fat12_16);
					//rootDir.directory();
					
					MainView mainView = new MainView("MainView", biosParameterBlock, rootDir, dataRegion, fat12_16);
					mainView.setVisible(true);
					
					
					
					
					
					
					
					
					long address = rootDirectoryAddress + rootDirectorySizeInBytes;
					
					System.out.println("address: " + address);
					System.out.printf("address: 0x%02Xh\n", address);
					
					long bytesPerCluster = biosParameterBlock.getBPB_BytsPerSec() * biosParameterBlock.getBPB_SecPerClus();
					System.out.println("bytesPerCluster: " + bytesPerCluster);
					//long address1 = address + bytesPerCluster * 1;
					long address1 = address + bytesPerCluster * ((long)startingClusterNumber - 2);
					
					System.out.println("address1: " + address1);
					System.out.printf("address1: 0x%02Xh\n", address1);
					
					//Get cluster
					long remainingDataBytes = filesizeInBytes;
					
					byte cluster [] = Arrays.copyOfRange(buffer, (int)address1, (int)address1 + (int)bytesPerCluster);
					remainingDataBytes -= bytesPerCluster;
					
					for (int i=0; i < cluster.length; i++)
					{
						System.out.printf("0x%02Xh ", cluster[i]);
					}
					System.out.println();
					
					//Address of FAT
					//long rootDirectoryAddress = (sizeOfOneFAT * (long)numberOfFATs + (long)numberOfReservedSectors) * (long)bytesPerSector;
					//System.out.println("rootDirectoryAddress: " + rootDirectoryAddress);
					//System.out.printf("rootDirectoryAddress: 0x%02Xh\n", rootDirectoryAddress);
					long FAT1Address = (long)numberOfReservedSectors * (long)bytesPerSector;
					long FAT2Address = (sizeOfOneFAT + (long)numberOfReservedSectors) * (long)bytesPerSector;
					
					System.out.println("FAT1Address: " + FAT1Address);
					System.out.printf("FAT1Address: 0x%02Xh\n", FAT1Address);
					
					System.out.println("FAT2Address: " + FAT2Address);
					System.out.printf("FAT2Address: 0x%02Xh\n", FAT2Address);
					
					//If data spans onto another cluster
					if (remainingDataBytes > 0)
					{
						final long pointerSizeInBytes = 2;
						
						//Get pointer from FAT
						long FATPointerAddress = FAT1Address + (long)startingClusterNumber * 2;
						
						System.out.println("FATPointerAddress: " + FATPointerAddress);
						System.out.printf("FATPointerAddress: 0x%02Xh\n", FATPointerAddress);
						
						long newClusterNumber = DataConverter.getValueFrom2Bytes(buffer, (int)FATPointerAddress);
						
						System.out.println("newClusterNumber: " + (int)newClusterNumber);
						System.out.printf("newClusterNumber: 0x%02Xh\n", (int)newClusterNumber);
					}
					
					
					break;
					
				case FAT32:
					break;
			}
			
			
			fs.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}*/
	}
}
