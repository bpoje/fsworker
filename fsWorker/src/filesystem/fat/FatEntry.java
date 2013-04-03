package filesystem.fat;

import java.io.IOException;

import filesystem.FileSystemEntry;
import filesystem.exception.NotEnoughBytesReadException;
import filesystem.io.DataConverter;
import filesystem.io.FileSystemIO;

public class FatEntry extends FileSystemEntry {
	protected char entryNumber;
	protected long entryAddress;
	
	protected String directoryPath;

	protected boolean isReadOnlyFile;
	protected boolean isHiddenFile;
	protected boolean isSystemFile;
	protected boolean isSpecialEntry;
	protected boolean isSubdirectoryEntry;
	protected boolean isArchiveFlag;
	protected boolean isLongFilenameEntry;
	
	protected FileSystemIO fileSystemIO;

	// Check first byte of filename
	// (The first byte of the filename indicates its status. Usually, it
	// contains a normal filename
	// character (e.g. 'A'), but there are some special values)
	protected FilenameStatus filenameStatus;
	
	//----------------------------------------------------------------------
	protected BootBlock bootBlock;
	protected FileAllocationTable fileAllocationTable;
	protected FatDirectory fatDirectory;
	protected DataRegion dataRegion;
	//----------------------------------------------------------------------

	public static final long rootDirectoryEntrySize = 32; // bytes

	//public FatEntry(char entryNumber, long entryAddress, FileSystemIO fileSystemIO) throws IOException, NotEnoughBytesReadException {
	public FatEntry(BootBlock bootBlock, FileAllocationTable fileAllocationTable, DataRegion dataRegion, char entryNumber, long entryAddress, FileSystemIO fileSystemIO, String directoryPath) throws IOException, NotEnoughBytesReadException {
		this.bootBlock = bootBlock;
		this.fileAllocationTable = fileAllocationTable;
		this.dataRegion = dataRegion;
		this.entryNumber = entryNumber;
		this.entryAddress = entryAddress;
		this.fileSystemIO = fileSystemIO;
		this.directoryPath = directoryPath;
		
		decodeAttributes();
		decodeFilenameStatus();
	}

	public void decodeFilenameStatus() throws IOException, NotEnoughBytesReadException {
		// Check first byte of filename
		// (The first byte of the filename indicates its status. Usually, it
		// contains a normal filename
		// character (e.g. 'A'), but there are some special values)
		
		//byte filenameStatusByte = buffer[(int) entryAddress];
		byte buffer[] = fileSystemIO.readFSImage(entryAddress, 1);
		byte filenameStatusByte = buffer[0];
		
		//System.out.println("filenameStatusByte: " + filenameStatusByte);
		//System.out.printf("filenameStatusByte: 0x%02Xh\n", filenameStatusByte);

		switch (filenameStatusByte) {
		// Entry is available and no subsequent entry is in use
		case (byte) 0x00:
			filenameStatus = filenameStatus.entryIsAvailableAndNoSubsequentEntryIsInUse;
			// System.out.println("filenameStatus: Entry is available and no subsequent entry is in use");
			break;

		// Entry has been previously erased and is not available. File undelete
		// utilities must replace this character with a regular character as
		// part of the undeletion process
		case (byte) 0xe5:
			filenameStatus = filenameStatus.entryHasBeenPreviouslyErasedAndIsNotAvailable;
			// System.out.println("filenameStatus: Entry has been previously erased and is not available. File undelete utilities must replace this character with a regular character as part of the undeletion process");
			break;

		// Initial character is actually 0xE5
		case (byte) 0x05:
			filenameStatus = filenameStatus.initialCharacterIsActuallyE5;
			// System.out.println("filenameStatus: Initial character is actually 0xE5");
			break;

		// 'Dot' entry; either '.' or '..'
		case (byte) 0x2e:
			filenameStatus = filenameStatus.dotEntry;
			// System.out.println("filenameStatus: 'Dot' entry; either '.' or '..'");
			break;

		// Any other character
		// This is the first character of a real filename.
		default:
			filenameStatus = filenameStatus.noSpecialValue;
			// System.out.println("filenameStatus: No special value");
		}
	}

	public void decodeAttributes() throws IOException, NotEnoughBytesReadException {
		// System.out.println("entryAddress: " + entryAddress);
		// System.out.printf("entryAddress: 0x%02Xh\n", entryAddress);
		
		// File atributes
		//char fileAttributes = DataConverter.getValueFrom1Byte(buffer,
		//		(int) entryAddress + 11);
		byte buffer[] = fileSystemIO.readFSImage(entryAddress + 11, 1);
		char fileAttributes = DataConverter.getValueFrom1Byte(buffer,0);
		
		// System.out.println("fileAttributes: " + (int)fileAttributes);
		// System.out.printf("fileAttributes: 0x%02Xh\n", (int)fileAttributes);

		// 0x01 Indicates that the file is read only.
		isReadOnlyFile = (fileAttributes & (0x01 << 0)) != 0;

		// 0x02 Indicates a hidden file. Such files can be displayed if it is
		// really required.
		isHiddenFile = (fileAttributes & (0x01 << 1)) != 0;

		// 0x04 Indicates a system file. These are hidden as well.
		isSystemFile = (fileAttributes & (0x01 << 2)) != 0;

		// 0x08 Indicates a special entry containing the disk's volume label,
		// instead of
		// describing a file. This kind of entry appears only in the root
		// directory.
		isSpecialEntry = (fileAttributes & (0x01 << 3)) != 0;

		// 0x10 The entry describes a subdirectory.
		isSubdirectoryEntry = (fileAttributes & (0x01 << 4)) != 0;

		// 0x20 This is the archive flag. This can be set and cleared by the
		// programmer or
		// user, but is always set when the file is modified. It is used by
		// backup programs.
		isArchiveFlag = (fileAttributes & (0x01 << 5)) != 0;
		
		//VFAT long file names
		//VFAT Long File Names (LFN) are stored on a FAT file system using a
		//trick—adding (possibly multiple) additional entries into the directory
		//before the normal file entry. The additional entries are marked with
		//the Volume Label, System, Hidden, and Read Only attributes (yielding 0x0F),
		//which is a combination that is not expected in the MS-DOS environment,
		//and therefore ignored by MS-DOS programs and third-party utilities.
		//Notably, a directory containing only volume labels is considered as empty
		//and is allowed to be deleted; such a situation appears if files created with
		//long names are deleted from plain DOS. This method is very similar to the
		//DELWATCH method to utilize the volume attribute to hide pending delete files
		//for possible future undeletion since DR DOS 6.0 (1991) and higher.

		//Because older versions of DOS could mistake LFN names in the root directory
		//for the volume label, VFAT was designed to create a blank volume label in the
		//root directory before adding any LFN name entires (if a volume label did not
		//already exist).
		
		// An attribute value of 0x0F is used to designate a long filename
		// entry.
		isLongFilenameEntry = (fileAttributes == 0x0F);

		/*
		 * System.out.println(); System.out.println("isReadOnly: " +
		 * isReadOnlyFile); System.out.println("isHidden: " + isHiddenFile);
		 * System.out.println("isSystemFile: " + isSystemFile);
		 * System.out.println("isSpecialEntry: " + isSpecialEntry);
		 * System.out.println("isSubdirectoryEntry: " + isSubdirectoryEntry);
		 * System.out.println("isArchiveFlag: " + isArchiveFlag);
		 * System.out.println("isLongFilenameEntry: " + isLongFilenameEntry);
		 * System.out.println();
		 */
	}

	public char getEntryNumber() {
		return entryNumber;
	}

	public long getEntryAddress() {
		return entryAddress;
	}

	public boolean isReadOnlyFile() {
		return isReadOnlyFile;
	}

	public boolean isHiddenFile() {
		return isHiddenFile;
	}

	public boolean isSystemFile() {
		return isSystemFile;
	}

	public boolean isSpecialEntry() {
		return isSpecialEntry;
	}

	public boolean isSubdirectoryEntry() {
		return isSubdirectoryEntry;
	}

	public boolean isArchiveFlag() {
		return isArchiveFlag;
	}

	public boolean isLongFilenameEntry() {
		return isLongFilenameEntry;
	}

	public FileSystemIO getFileSystemIO() {
		return fileSystemIO;
	}

	public static long getRootdirectoryentrysize() {
		return rootDirectoryEntrySize;
	}

	public FilenameStatus getFilenameStatus() {
		return filenameStatus;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}
}
