package gui;

public class TableRowData {
	private String filename = null;
	private String filenameExtension;
	private String longFilename;
	private String startingClusterNumber;
	private String filesizeInBytes;
	private String totalClustersNeededForData;
	private String totalAllocatedSizeInBytes;
	private String fileSlackSizeInBytes;
	private String md5OfData;
	private String md5OfFileSlack;
	private boolean isRoot;
	
	public TableRowData(String filename, String filenameExtension,
			String longFilename,
			String startingClusterNumber, String filesizeInBytes,
			String totalClustersNeededForData,
			String totalAllocatedSizeInBytes, String fileSlackSizeInBytes,
			String md5OfData, String md5OfFileSlack, boolean isLeaf) {
		this.filename = filename;
		this.filenameExtension = filenameExtension;
		this.longFilename = longFilename;
		this.startingClusterNumber = startingClusterNumber;
		this.filesizeInBytes = filesizeInBytes;
		this.totalClustersNeededForData = totalClustersNeededForData;
		this.totalAllocatedSizeInBytes = totalAllocatedSizeInBytes;
		this.fileSlackSizeInBytes = fileSlackSizeInBytes;
		this.md5OfData = md5OfData;
		this.md5OfFileSlack = md5OfFileSlack;
		this.isRoot = isLeaf;
	}

	/**
	 * @return the client
	 */
	public String getFilenameExtension() {
		return filenameExtension;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setFilenameExtension(String filenameExtension) {
		this.filenameExtension = filenameExtension;
	}

	/**
	 * @return the family
	 */
	public String getFilesizeInBytes() {
		return filesizeInBytes;
	}

	/**
	 * @param family
	 *            the family to set
	 */
	public void setFilesizeInBytes(String filesizeInBytes) {
		this.filesizeInBytes = filesizeInBytes;
	}

	/**
	 * @return the isRoot
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * @param isRoot
	 *            the isRoot to set
	 */
	public void setRoot(boolean isLeaf) {
		this.isRoot = isLeaf;
	}

	/**
	 * @return the source
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the spouse
	 */
	public String getStartingClusterNumber() {
		return startingClusterNumber;
	}

	/**
	 * @param spouse
	 *            the spouse to set
	 */
	public void setStartingClusterNumber(String startingClusterNumber) {
		this.startingClusterNumber = startingClusterNumber;
	}

	public String getMd5OfData() {
		return md5OfData;
	}

	public void setMd5OfData(String md5OfData) {
		this.md5OfData = md5OfData;
	}

	public String getTotalClustersNeededForData() {
		return totalClustersNeededForData;
	}

	public void setTotalClustersNeededForData(String totalClustersNeededForData) {
		this.totalClustersNeededForData = totalClustersNeededForData;
	}

	public String getTotalAllocatedSizeInBytes() {
		return totalAllocatedSizeInBytes;
	}

	public void setTotalAllocatedSizeInBytes(String totalAllocatedSizeInBytes) {
		this.totalAllocatedSizeInBytes = totalAllocatedSizeInBytes;
	}

	public String getFileSlackSizeInBytes() {
		return fileSlackSizeInBytes;
	}

	public void setFileSlackSizeInBytes(String fileSlackSizeInBytes) {
		this.fileSlackSizeInBytes = fileSlackSizeInBytes;
	}

	public String getMd5OfFileSlack() {
		return md5OfFileSlack;
	}

	public void setMd5OfFileSlack(String md5OfFileSlack) {
		this.md5OfFileSlack = md5OfFileSlack;
	}
	public String getLongFilename() {
		return longFilename;
	}
	public void setLongFilename(String longFilename) {
		this.longFilename = longFilename;
	}
}
