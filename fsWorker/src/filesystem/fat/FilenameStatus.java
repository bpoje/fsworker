package filesystem.fat;

//http://en.wikipedia.org/wiki/8.3_filename

public enum FilenameStatus {
	//0x00 	Entry is available and no subsequent entry is in use
	entryIsAvailableAndNoSubsequentEntryIsInUse,
	
	//0x05 	Initial character is actually 0xE5
	initialCharacterIsActuallyE5,
	
	//0x2E 	'Dot' entry; either '.' or '..'
	dotEntry,
	
	//0xE5
	// Entry has been previously erased and is not available. File undelete
	// utilities must replace this character with a regular character as
	// part of the undeletion process
	entryHasBeenPreviouslyErasedAndIsNotAvailable,
	
	//None of above => this byte is a first byte in a DOS filename and
	//contains first byte of filename (no special values)
	noSpecialValue;
}