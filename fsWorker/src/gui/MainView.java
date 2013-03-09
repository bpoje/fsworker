package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.treetable.*;
import org.jdesktop.swingx.decorator.*;

import fat.BIOSParameterBlock;
import fat.DOSFilename;
import fat.DataRegion;
import fat.FAT12_16;
import fat.RootDirectory;
import fat.RootDirectoryEntry;

import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainView extends JFrame {
	private BIOSParameterBlock biosParameterBlock;
	private RootDirectory rootDir;
	private DataRegion dataRegion;
	private FAT12_16 fat12_16;
	
	private long totalSlackFileSizeInBytes = 0;
	
	private Container container;
	private Container infoContainer;
	
	public MainView(String title, BIOSParameterBlock biosParameterBlock, RootDirectory rootDir, DataRegion dataRegion, FAT12_16 fat12_16)
	{
		super(title);
		
		this.biosParameterBlock = biosParameterBlock;
		this.rootDir = rootDir;
		this.dataRegion = dataRegion;
		this.fat12_16 = fat12_16;
		
		setSize(800, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		container = getContentPane();
		container.setLayout(new GridLayout(2, 1));
		
		//-------------------------------------------------------------------------------
		/*
		//JTable example
		DefaultTableModel model  = new DefaultTableModel();
		model.addColumn("CF Source");
		model.addColumn("Client");
		model.addColumn("Spouse");
		model.addColumn("Family");
		model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
		model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
		model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
		model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
		model.addRow(new Object[]{"Salary1","250001","50001","3000001"});
  	    
		JTable table = new JTable(model); 
		table.setCellEditor(new DefaultCellEditor(new JTextField()));
  		
		this.getContentPane().add(new JScrollPane(table));
		*/
		//-------------------------------------------------------------------------------
		
		//-------------------------------------------------------------------------------
		/*
		//JXTreeTable example
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData("CF","","","",true));
		
		DefaultMutableTreeNode incomeNode = new DefaultMutableTreeNode(new TableRowData("Income","25000","5000","300000",true));
    	incomeNode.add(new DefaultMutableTreeNode(new TableRowData("Salary1","250001","50001","3000001",false)));
    	incomeNode.add(new DefaultMutableTreeNode(new TableRowData("Salary2","250002","50002","3000002",false)));
    	incomeNode.add(new DefaultMutableTreeNode(new TableRowData("Salary3","250003","50003","3000003",false)));
    	incomeNode.add(new DefaultMutableTreeNode(new TableRowData("Salary4","250004","50004","3000004",false)));
    	incomeNode.add(new DefaultMutableTreeNode(new TableRowData("Salary5","250005","50005","3000005",false)));
    	
    	//----
    	DefaultMutableTreeNode incomeNode1 = new DefaultMutableTreeNode(new TableRowData("a","1","2","3",true));
    	incomeNode1.add(new DefaultMutableTreeNode(new TableRowData("Salary1","250001","50001","3000001",false)));
    	incomeNode1.add(new DefaultMutableTreeNode(new TableRowData("Salary2","250002","50002","3000002",false)));
    	incomeNode.add(incomeNode1);
    	//----
    	
    	rootNode.add(incomeNode);
    	rootNode.add(new DefaultMutableTreeNode());
    	
    	DefaultMutableTreeNode expenseNode = new DefaultMutableTreeNode(new TableRowData("Expenses","25000","5000","300000",true));
    	expenseNode.add(new DefaultMutableTreeNode(new TableRowData("Salary1","250001","50001","3000001",false)));
    	expenseNode.add(new DefaultMutableTreeNode(new TableRowData("Salary2","250002","50002","3000002",false)));
    	expenseNode.add(new DefaultMutableTreeNode(new TableRowData("Salary3","250003","50003","3000003",false)));
    	expenseNode.add(new DefaultMutableTreeNode(new TableRowData("Salary4","250004","50004","3000004",false)));
    	expenseNode.add(new DefaultMutableTreeNode(new TableRowData("Salary5","250005","50005","3000005",false)));
    	
    	rootNode.add(expenseNode);
	
    	JXTreeTable binTree = new JXTreeTable(new MyTreeModel(rootNode));
    	
    	Highlighter highligher = HighlighterFactory.createSimpleStriping(HighlighterFactory.BEIGE);
    	binTree.setHighlighters(highligher);
        binTree.setShowGrid(false);
        binTree.setShowsRootHandles(true);
        configureCommonTableProperties(binTree);
        binTree.setTreeCellRenderer(new TreeTableCellRenderer());
        
        this.getContentPane().add(new JScrollPane(binTree));
        */
      //-------------------------------------------------------------------------------
		
		
		
		ArrayList<RootDirectoryEntry> filesInFolder = rootDir.directory();
		
		System.out.println("files.size(): " + filesInFolder.size());
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData("CF","","","","","","","",true));
		
		//Recursively
		totalSlackFileSizeInBytes = 0;
		scanFileSystem(filesInFolder, rootNode);
		
		//rootNode.add(incomeNode);
		
		JXTreeTable binTree = new JXTreeTable(new MyTreeModel(rootNode));
    	
    	Highlighter highligher = HighlighterFactory.createSimpleStriping(HighlighterFactory.BEIGE);
    	binTree.setHighlighters(highligher);
        binTree.setShowGrid(false);
        binTree.setShowsRootHandles(true);
        configureCommonTableProperties(binTree);
        binTree.setTreeCellRenderer(new TreeTableCellRenderer());
        
        //this.getContentPane().add(new JScrollPane(binTree));
        container.add(new JScrollPane(binTree));
        
        //Bottom container with general informations
        infoContainer = new Container();
        infoContainer.setLayout(new GridLayout(10, 1));
        JLabel label1 = new JLabel("Total file slack size in bytes: " + formatOutput(totalSlackFileSizeInBytes));
        infoContainer.add(label1);
        
        JLabel label2 = new JLabel("FAT type: " + biosParameterBlock.getType());
        infoContainer.add(label2);
        
        JLabel label3 = new JLabel("Bytes per sector: " + formatOutput((long)biosParameterBlock.getBPB_BytsPerSec()));
        infoContainer.add(label3);
        
        JLabel label4 = new JLabel("Number of sectors per allocation unit (cluster): " + (long)biosParameterBlock.getBPB_SecPerClus());
        infoContainer.add(label4);
        
        //Cluster <=> AllocationUnit
        long bytesPerAllocationUnit = (long)biosParameterBlock.getBPB_BytsPerSec() * (long)biosParameterBlock.getBPB_SecPerClus();
        JLabel label5 = new JLabel("Number of Bytes per allocation unit (cluster): " + formatOutput(bytesPerAllocationUnit));
        infoContainer.add(label5);
        
        JLabel label6 = new JLabel("Number of reserved blocks: " + (long)biosParameterBlock.getBPB_RsvdSecCnt());
        infoContainer.add(label6);
        
        JLabel label7 = new JLabel("Number of FAT data structures on the volume: " + (long)biosParameterBlock.getBPB_NumFATs());
        infoContainer.add(label7);
        
        JLabel label8 = new JLabel("Number of sectors occupied by ONE FAT: " + (long)biosParameterBlock.getFATSz());
        infoContainer.add(label8);
        
        JLabel label9 = new JLabel("Number of sectors in data region: " + (long)biosParameterBlock.getNumberOfDataSectors());
        infoContainer.add(label9);
        
        JLabel label10 = new JLabel("Number of clusters in data region: " + (long)biosParameterBlock.getCountofClustersInDataRegion());
        infoContainer.add(label10);
        
        container.add(infoContainer);
	}
	
	public String formatOutput(long numberOfBytes)
	{
		StringBuffer output = new StringBuffer();
		
        DecimalFormat df = new DecimalFormat("#.##");
		
		output.append(Long.toString(numberOfBytes) + " B");
		
		if (numberOfBytes >= 1024)
		{
			double KB = (double)numberOfBytes / (double)1024;
			
			output.append(" = " + df.format(KB) + " KB");
			
			if (numberOfBytes >= (long)1024 * (long)1024)
			{
				double MB = (double)KB / (double)1024;
				
				output.append(" = " + df.format(MB) + " MB");
			}
		}
		
		return output.toString();
	}
	
	private void scanFileSystem(ArrayList<RootDirectoryEntry> files, DefaultMutableTreeNode treeNode)
	{
		for (int i = 0; i < files.size(); i++)
		{
			DOSFilename file = (DOSFilename)files.get(i);
			
			String filename = file.getFilename();
			String filenameExtension = file.getFilenameExtension();
			char startingClusterNumber = file.getStartingClusterNumber();
			long filesizeInBytes = file.getFilesizeInBytes();
			String md5 = file.calculateMd5(dataRegion, fat12_16);
			long totalClustersNeededForData = file.getTotalClustersNeededForData(dataRegion);
			long totalAllocatedSizeInBytes = totalClustersNeededForData * dataRegion.getBytesPerCluster();
			long fileSlackSizeInBytes = totalAllocatedSizeInBytes - filesizeInBytes;
			
			totalSlackFileSizeInBytes += fileSlackSizeInBytes;
			
			String sStartingClusterNumber = formatOutput((long)startingClusterNumber);
			String sFilesizeInBytes = formatOutput(filesizeInBytes);
			String sTotalClustersNeededForData = Long.toString(totalClustersNeededForData);
			String sTotalAllocatedSizeInBytes = formatOutput(totalAllocatedSizeInBytes);
			String sFileSlackSizeInBytes = formatOutput(fileSlackSizeInBytes);
			
			
			if (!file.isSubdirectoryEntry())
			{
				treeNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5,false)));
			}
			else
			{
				//DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,"","",true));
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5,true));
				
				long adr = dataRegion.getClusterAddress(file.getStartingClusterNumber());
				ArrayList<RootDirectoryEntry> filesInSubdir = rootDir.subDirectory(adr);
				
				System.out.println("filesInSubdir.size(): " + filesInSubdir.size());
				
				/*
				//DEBUG
				for (int j = 0; j < filesInSubdir.size(); j++)
				{
					RootDirectoryEntry temp = filesInSubdir.get(j);
					System.out.println(j);
					System.out.println("temp.isArchiveFlag(): " + temp.isArchiveFlag());
					System.out.println("temp.isHiddenFile(): " + temp.isHiddenFile());
					System.out.println("temp.isLongFilenameEntry(): " + temp.isLongFilenameEntry());
					System.out.println("temp.isReadOnlyFile(): " + temp.isReadOnlyFile());
					System.out.println("temp.isSpecialEntry(): " + temp.isSpecialEntry());
					System.out.println("temp.isSubdirectoryEntry(): " + temp.isSubdirectoryEntry());
					System.out.println("temp.isSystemFile(): " + temp.isSystemFile());
					System.out.println();
					
					DOSFilename temp1 = (DOSFilename)temp;
					System.out.println("temp1.getFilename(): " + temp1.getFilename());
				}
				*/
				
				//Ignore . and .. in search
				if (filename.compareToIgnoreCase(".") != 0 && filename.compareToIgnoreCase("..") != 0)
					scanFileSystem(filesInSubdir, subNode);
				
				//subNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,false)));
				treeNode.add(subNode);
			}
		}
	}
	
	private void  configureCommonTableProperties(JXTable table) {
        table.setColumnControlVisible(true);
        StringValue toString = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Point) {
                    Point p = (Point) value;
                    return createString(p.x, p.y);
                } else if (value instanceof Dimension) {
                    Dimension dim = (Dimension) value;
                    return createString(dim.width, dim.height);
                }
               return "";
            }

            private String createString(int width, int height) {
                return "(" + width + ", " + height + ")";
            }
            
        };
        TableCellRenderer renderer = new DefaultTableRenderer(toString);
        table.setDefaultRenderer(Point.class, renderer);
        table.setDefaultRenderer(Dimension.class, renderer);
    }
}
