package gui;

public class TableRowData
{
	private String filename = null;
	private String filenameExtension;
	private String startingClusterNumber;
	private String filesizeInBytes;
	private boolean isRoot;
	/**
	 * Created on: Feb 23, 2008
	 * @Author: sandarenu
	 * @param source
	 * @param client
	 * @param spouse
	 * @param family
	 * @param isRoot
	 */
	public TableRowData(String filename, String filenameExtension, String startingClusterNumber, String filesizeInBytes, boolean isLeaf)
	{
		this.filename = filename;
		this.filenameExtension = filenameExtension;
		this.startingClusterNumber = startingClusterNumber;
		this.filesizeInBytes = filesizeInBytes;
		this.isRoot = isLeaf;
	}
	/**
	 * @return the client
	 */
	public String getFilenameExtension()
	{
		return filenameExtension;
	}
	/**
	 * @param client the client to set
	 */
	public void setFilenameExtension(String filenameExtension)
	{
		this.filenameExtension = filenameExtension;
	}
	/**
	 * @return the family
	 */
	public String getFilesizeInBytes()
	{
		return filesizeInBytes;
	}
	/**
	 * @param family the family to set
	 */
	public void setFilesizeInBytes(String filesizeInBytes)
	{
		this.filesizeInBytes = filesizeInBytes;
	}
	/**
	 * @return the isRoot
	 */
	public boolean isRoot()
	{
		return isRoot;
	}
	/**
	 * @param isRoot the isRoot to set
	 */
	public void setRoot(boolean isLeaf)
	{
		this.isRoot = isLeaf;
	}
	/**
	 * @return the source
	 */
	public String getFilename()
	{
		return filename;
	}
	/**
	 * @param source the source to set
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	/**
	 * @return the spouse
	 */
	public String getStartingClusterNumber()
	{
		return startingClusterNumber;
	}
	/**
	 * @param spouse the spouse to set
	 */
	public void setStartingClusterNumber(String startingClusterNumber)
	{
		this.startingClusterNumber = startingClusterNumber;
	}
	
}
