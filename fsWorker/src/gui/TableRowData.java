package gui;

import filesystem.fat.fat16.Fat16Entry;

public class TableRowData {
	private Fat16Entry file = null;
	private String filename;
	private String filenameExtension;
	private String longFilename;
	private String directoryPath;
	private String time;
	private String date;
	private String startingClusterNumber;
	private String filesizeInBytes;
	private String totalClustersNeededForData;
	private String totalAllocatedSizeInBytes;
	private String fileSlackSizeInBytes;
	private String md5OfData;
	private String md5OfFileSlack;
	private boolean isRoot;
	
	public TableRowData(Fat16Entry file,
			String filename,
			String filenameExtension,
			String longFilename,
			String directoryPath,
			String time,
			String date,
			String startingClusterNumber, String filesizeInBytes,
			String totalClustersNeededForData,
			String totalAllocatedSizeInBytes, String fileSlackSizeInBytes,
			String md5OfData, String md5OfFileSlack, boolean isLeaf) {
		this.file = file;
		this.filename = filename;
		this.filenameExtension = filenameExtension;
		this.longFilename = longFilename;
		this.directoryPath = directoryPath;
		this.time = time;
		this.date = date;
		this.startingClusterNumber = startingClusterNumber;
		this.filesizeInBytes = filesizeInBytes;
		this.totalClustersNeededForData = totalClustersNeededForData;
		this.totalAllocatedSizeInBytes = totalAllocatedSizeInBytes;
		this.fileSlackSizeInBytes = fileSlackSizeInBytes;
		this.md5OfData = md5OfData;
		this.md5OfFileSlack = md5OfFileSlack;
		this.isRoot = isLeaf;
	}

	public Fat16Entry getFile() {
		return file;
	}
	
	public void setFile(Fat16Entry file) {
		this.file = file;
	}
	
	public String getFilenameExtension() {
		return filenameExtension;
	}
	
	public void setFilenameExtension(String filenameExtension) {
		this.filenameExtension = filenameExtension;
	}
	
	public String getFilesizeInBytes() {
		return filesizeInBytes;
	}
	
	public void setFilesizeInBytes(String filesizeInBytes) {
		this.filesizeInBytes = filesizeInBytes;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	
	public void setRoot(boolean isLeaf) {
		this.isRoot = isLeaf;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getStartingClusterNumber() {
		return startingClusterNumber;
	}
	
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
	public String getDirectoryPath() {
		return directoryPath;
	}
	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
