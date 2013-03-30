package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.fat16.FileSystemFat16;
import filesystem.utils.OutputFormater;

public class FatTableComponent extends MouseAdapter {
	
	private Container container;
	private JLabel label1 = new JLabel("First FAT:");
	private JLabel label2 = new JLabel();
	private FileSystemFat16 fileSystemFAT16;
	private JTable table;
	private DefaultTableModel model = new DefaultTableModel();
	//private long numberOfDataClusters;
	
	public FatTableComponent(Container container, FileSystemFat16 fileSystemFAT16)
	{
		this.container = container;
		this.fileSystemFAT16 = fileSystemFAT16;
		//this.numberOfDataClusters = fileSystemFAT16.getCountofClustersInDataRegion();
		
		model.addColumn("Entry number");
		model.addColumn("Address");
		model.addColumn("Pointing to entry number");
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
	    
	    table.addMouseListener(this);
	    
	    /*
	    table.getSelectionModel().addListSelectionListener(
	            new ListSelectionListener() {
	                public void valueChanged(ListSelectionEvent event) {
	                    int viewRow = table.getSelectedRow();
	                    int viewColumn = table.getSelectedColumn();
	                    if (viewRow < 0 || viewColumn < 0) {
	                        //Selection got filtered away.
	                        label2.setText("");
	                    } else {
	                        int modelRow = 
	                            table.convertRowIndexToModel(viewRow);
	                        int modelColumn = 
	                        		table.convertColumnIndexToModel(viewColumn);
	                        label2.setText(
	                            String.format("Selected Row in view: %d. " +
	                                "Selected Row in model: %d." +
	                            	"Selected Column in view: %d. " +
	                                "Selected Column in model: %d.", 
	                                viewRow, modelRow, viewColumn, modelColumn)
	                                );
	                    }
	                }
	            }
	    );
	    */
	    
	  	table.setCellEditor(new DefaultCellEditor(new JTextField()));
	  	container.setLayout(new BorderLayout());
	  	container.add(label1, BorderLayout.NORTH);
	  	container.add(label2, BorderLayout.SOUTH);
	  	container.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	public void fillModel() throws IOException, NotEnoughBytesReadException
	{
		//Output FAT table & clusters markings
		//for (int iClusterNumber = 0; iClusterNumber < fileSystemFAT16.getCountofClustersInDataRegion(); iClusterNumber++)
		////-------for (int iClusterNumber = 0; iClusterNumber < fileSystemFAT16.getFATSizeInEntries(); iClusterNumber++)
		//{
		//	long fatEntryAddress = fileSystemFAT16.getFATPointerAddress((char)iClusterNumber);	
		//	System.out.printf("clusterNumber: %d, fatEntryAddress: 0x%02Xh, isAvailable: %b, isClusterBad: %b\n", (int)iClusterNumber, fatEntryAddress, fileSystemFAT16.isClusterAvailable((char)iClusterNumber), fileSystemFAT16.isClusterBad((char)iClusterNumber));
		//}
		
		System.out.println("fillModel()");
		
		for (int iClusterNumber = 0; iClusterNumber < fileSystemFAT16.getFATSizeInEntries(); iClusterNumber++)
		{
			String sClusterNUmber = " (" + OutputFormater.charToHexString((char)iClusterNumber) + ") " + (int)iClusterNumber;
			
			long fatEntryAddress = fileSystemFAT16.getFATPointerAddress((char)iClusterNumber);
			String sFatEntryAddress = OutputFormater.longToHexString(fatEntryAddress);
			
			char fatEntryValue = fileSystemFAT16.getFATPointerValue((char)iClusterNumber);
			String sFatEntryValue = "";
			
			//FAT Code Range	Meaning
			//0000h				Available Cluster
			//0002h-FFEFh		Used, Next Cluster in File
			//FFF0h-FFF6h		Reserved Cluster
			//FFF7h				BAD Cluster
			//FFF8h-FFFF		Used, Last Cluster in File
			switch((int)fatEntryValue)
			{
			case 0:
				sFatEntryValue = "Available Cluster";
				break;
			
			case (int)0xFFF7:
				sFatEntryValue = "BAD Cluster";
				break;
			
			case (int)0xFFF0:
			case (int)0xFFF1:
			case (int)0xFFF2:
			case (int)0xFFF3:
			case (int)0xFFF4:
			case (int)0xFFF5:
			case (int)0xFFF6:
				sFatEntryValue = "Reserved Cluster";
				break;
			
			case (int)0xFFF8:
			case (int)0xFFF9:
			case (int)0xFFFA:
			case (int)0xFFFB:
			case (int)0xFFFC:
			case (int)0xFFFD:
			case (int)0xFFFE:
			case (int)0xFFFF:
				//In the first byte of the first entry a copy of the media descriptor is stored. The remaining bits of
				//this entry are 1. In the second entry the end-of-file marker is stored.
				if (iClusterNumber == 0)
					sFatEntryValue = "Media descriptor";
				else if (iClusterNumber == 1)
					sFatEntryValue = "End-of-file marker";
				else
					sFatEntryValue = "Used, Last Cluster in File";
				break;
				
			default:
				sFatEntryValue = Integer.toString((int)fatEntryValue);
			}
			
			sFatEntryValue = " (" + OutputFormater.charToHexString(fatEntryValue) + ") " + sFatEntryValue;
			
			model.addRow(new Object[]{sClusterNUmber, sFatEntryAddress,sFatEntryValue});
			
			//System.out.printf("clusterNumber: %d, fatEntryAddress: 0x%02Xh, isAvailable: %b, isClusterBad: %b\n", (int)iClusterNumber, fatEntryAddress, fileSystemFAT16.isClusterAvailable((char)iClusterNumber), fileSystemFAT16.isClusterBad((char)iClusterNumber));
		}
		
		//Select third row
		table.setRowSelectionInterval(2, 2);
		
		//model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
	}
	
	@Override  
    public void mouseClicked(MouseEvent e)  
    {  
        //System.out.println(e.getSource().getClass());
		
		int viewRow = table.getSelectedRow();
        int viewColumn = table.getSelectedColumn();
        if (viewRow < 0 || viewColumn < 0) {
            //Selection got filtered away.
            label2.setText("");
        } else {
            int modelRow = 
                table.convertRowIndexToModel(viewRow);
            int modelColumn = 
            		table.convertColumnIndexToModel(viewColumn);
            label2.setText(
                String.format("Selected Row in view: %d. " +
                    "Selected Row in model: %d." +
                	"Selected Column in view: %d. " +
                    "Selected Column in model: %d.", 
                    viewRow, modelRow, viewColumn, modelColumn)
                    );
        }
    } 
}
