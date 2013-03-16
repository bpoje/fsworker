package filesystem.fat;

public enum FilenameStatus {
	entryIsAvailableAndNoSubsequentEntryIsInUse,
	initialCharacterIsActuallyE5,
	dotEntry,
	entryHasBeenPreviouslyErasedAndIsNotAvailable,
	noSpecialValue;
}