package gui;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import filesystem.fat.fat16.BootBlock16;
import filesystem.utils.OutputFormater;

public class InfoView  extends JFrame {
	
	private Container infoContainer = new Container();
	private JTable table;
	private DefaultTableModel model = new DefaultTableModel();
	
	public InfoView(BootBlock16 bootBlock16, long totalSlackFileSizeInBytes)
	{
		setSize(700, 200);
		
		Container container = getContentPane();
		
		model.addColumn("Info");
		model.addColumn("Value");
		
		table = new JTable(model) {
			private static final long serialVersionUID = 1L;
			
			//Disable editing
			public boolean isCellEditable(int row, int column) {                
				return false;               
				};
		};
		
        container.add(new JScrollPane(table));
        
        addRow("Total file slack size in bytes: ", OutputFormater.formatOutput(totalSlackFileSizeInBytes));
        addRow("FAT type: ", bootBlock16.getType().toString());
        addRow("Bytes per sector: ", OutputFormater.formatOutput((long)bootBlock16.getBPB_BytsPerSec()));
        addRow("Number of sectors per allocation unit (cluster): ", Long.toString((long)bootBlock16.getBPB_SecPerClus()));
        
        //Cluster <=> AllocationUnit
        long bytesPerAllocationUnit = (long)bootBlock16.getBPB_BytsPerSec() * (long)bootBlock16.getBPB_SecPerClus();
        addRow("Number of Bytes per allocation unit (cluster): ", OutputFormater.formatOutput(bytesPerAllocationUnit));
        addRow("Number of reserved blocks: ", Long.toString((long)bootBlock16.getBPB_RsvdSecCnt()));
        addRow("Number of FAT data structures on the volume: ", Long.toString((long)bootBlock16.getBPB_NumFATs()));
        addRow("Number of sectors occupied by ONE FAT: ", Long.toString((long)bootBlock16.getFATSz()));
        addRow("Number of sectors in data region: ", Long.toString((long)bootBlock16.getNumberOfDataSectors()));
        addRow("Number of clusters in data region: ", Long.toString((long)bootBlock16.getCountofClustersInDataRegion()));
	}
	
	public void addRow(String info, String value)
	{
        model.addRow(new Object[]{info,value});
	}
}
