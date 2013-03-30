package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.fat16.FileSystemFat16;
import filesystem.utils.OutputFormater;

public class DataTableComponent {
	private Container container;
	private JLabel label1 = new JLabel("Data region (The first cluster of the data area is cluster 2. That leaves the first two entries of the FAT unused):");
	private FileSystemFat16 fileSystemFAT16;
	private JTable table;
	private DefaultTableModel model = new DefaultTableModel();
	//private long numberOfDataClusters;
	
	public DataTableComponent(Container container, FileSystemFat16 fileSystemFAT16)
	{
		this.container = container;
		this.fileSystemFAT16 = fileSystemFAT16;
		//this.numberOfDataClusters = fileSystemFAT16.getCountofClustersInDataRegion();
		
		model.addColumn("Entry number");
		model.addColumn("Address");
		//model.addColumn("Pointing to entry number");
	  	//model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
	  	//model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
	  	//model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
	  	//model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
	  	
	  	table = new JTable(model){
	        private static final long serialVersionUID = 1L;
	        
	        //Disable editing
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
	    
	  	table.setCellEditor(new DefaultCellEditor(new JTextField()));
	  	container.setLayout(new BorderLayout());
	  	container.add(label1, BorderLayout.NORTH);
	  	container.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	public void fillModel() throws IOException, NotEnoughBytesReadException
	{
		long numberOfDataClusters = fileSystemFAT16.getCountofClustersInDataRegion();
		
		//The first cluster of the data area is cluster #2. That leaves the first two entries of the FAT unused.	
		for (int iClusterNumber = 2; iClusterNumber < 2 + numberOfDataClusters; iClusterNumber++)
		{
			String sClusterNUmber = " (" + OutputFormater.charToHexString((char)iClusterNumber) + ") " + (int)iClusterNumber;
			
			long dataClusterAddress = fileSystemFAT16.getDataClusterAddress((char) iClusterNumber);
			String sDataClusterAddress = OutputFormater.longToHexString(dataClusterAddress);
			
			model.addRow(new Object[]{sClusterNUmber, sDataClusterAddress});
			
			//System.out.printf("clusterNumber: %d, fatEntryAddress: 0x%02Xh, isAvailable: %b, isClusterBad: %b\n", (int)iClusterNumber, fatEntryAddress, fileSystemFAT16.isClusterAvailable((char)iClusterNumber), fileSystemFAT16.isClusterBad((char)iClusterNumber));
		}
		
		//Select first row
		table.setRowSelectionInterval(0, 0);
		
		//model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
	}
}
