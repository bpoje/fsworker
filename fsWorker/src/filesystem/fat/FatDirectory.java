package filesystem.fat;

public abstract class FatDirectory {
	
	public FatDirectory()
	{
		
	}
	
	abstract public void initFatDirectory(BootBlock bootBlock, FileAllocationTable fileAllocationTable, DataRegion dataRegion);
}
