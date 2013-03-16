package gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class MyTreeModel extends AbstractTreeTableModel {
	private String[] titles = { "Short Filename", "Extension",
			"Long Filename (VFAT)",
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
					return data.getFilename();
				case 1:
					return data.getFilenameExtension();
				case 2:
					return data.getLongFilename();
				case 3:
					return data.getStartingClusterNumber();
				case 4:
					return data.getFilesizeInBytes();
				case 5:
					return data.getTotalClustersNeededForData();
				case 6:
					return data.getTotalAllocatedSizeInBytes();
				case 7:
					return data.getFileSlackSizeInBytes();
				case 8:
					return data.getMd5OfData();
				case 9:
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
					return data.getFilename();
				case 1:
					return data.getFilenameExtension();
				case 2:
					return data.getLongFilename();
				case 3:
					return data.getStartingClusterNumber();
				case 4:
					return data.getFilesizeInBytes();
				case 5:
					return data.getTotalClustersNeededForData();
				case 6:
					return data.getTotalAllocatedSizeInBytes();
				case 7:
					return data.getFileSlackSizeInBytes();
				case 8:
					return data.getMd5OfData();
				case 9:
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