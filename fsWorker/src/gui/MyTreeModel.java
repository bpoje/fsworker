package gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class MyTreeModel extends AbstractTreeTableModel {
	private String[] titles = { "File", "Short Filename", "Extension",
			"Long Filename (VFAT)",
			"Directory Path",
			"Time created or last updated",
			"Date created or last updated",
			"Starting Cluster Number", "File size in Bytes",
			"Total clusters needed for data", "Total allocated size in Bytes",
			"File slack size in Bytes", "Md5 of data", "Md5 of file slack"};

	public MyTreeModel(DefaultMutableTreeNode root) {
		super(root);
	}

	/**
	 * Table Columns
	 */
	public String getColumnName(int column) {
		if (column < titles.length)
			return (String) titles[column];
		else
			return "";
	}

	public int getColumnCount() {
		return titles.length;
	}

	public Class getColumnClass(int column) {
		return String.class;
	}

	public Object getValueAt(Object arg0, int arg1) {
		if (arg0 instanceof TableRowData) {
			TableRowData data = (TableRowData) arg0;
			if (data != null) {
				switch (arg1) {
				case 0:
					return data.getFile();
				case 1:
					return data.getFilename();
				case 2:
					return data.getFilenameExtension();
				case 3:
					return data.getLongFilename();
				case 4:
					return data.getDirectoryPath();
				case 5:
					return data.getTime();
				case 6:
					return data.getDate();
				case 7:
					return data.getStartingClusterNumber();
				case 8:
					return data.getFilesizeInBytes();
				case 9:
					return data.getTotalClustersNeededForData();
				case 10:
					return data.getTotalAllocatedSizeInBytes();
				case 11:
					return data.getFileSlackSizeInBytes();
				case 12:
					return data.getMd5OfData();
				case 13:
					return data.getMd5OfFileSlack();
				}
			}

		}

		if (arg0 instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;
			TableRowData data = (TableRowData) dataNode.getUserObject();
			if (data != null) {
				switch (arg1) {
				case 0:
					return data.getFile();
				case 1:
					return data.getFilename();
				case 2:
					return data.getFilenameExtension();
				case 3:
					return data.getLongFilename();
				case 4:
					return data.getDirectoryPath();
				case 5:
					return data.getTime();
				case 6:
					return data.getDate();
				case 7:
					return data.getStartingClusterNumber();
				case 8:
					return data.getFilesizeInBytes();
				case 9:
					return data.getTotalClustersNeededForData();
				case 10:
					return data.getTotalAllocatedSizeInBytes();
				case 11:
					return data.getFileSlackSizeInBytes();
				case 12:
					return data.getMd5OfData();
				case 13:
					return data.getMd5OfFileSlack();
				}
			}

		}
		return null;
	}

	public Object getChild(Object arg0, int arg1) {

		if (arg0 instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) arg0;
			return nodes.getChildAt(arg1);
		}
		return null;
	}

	public int getChildCount(Object arg0) {

		if (arg0 instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) arg0;
			return nodes.getChildCount();
		}
		return 0;
	}

	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

}