package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
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

import filesystem.exception.NotEnoughBytesReadException;
import filesystem.fat.BootBlock;
import filesystem.fat.DataRegion;
import filesystem.fat.FatDirectory;
import filesystem.fat.FatEntry;
import filesystem.fat.FileAllocationTable;
import filesystem.fat.fat16.BootBlock16;
import filesystem.fat.fat16.DataRegion16;
import filesystem.fat.fat16.Fat16Directory;
import filesystem.fat.fat16.Fat16Entry;
import filesystem.fat.fat16.FileAllocationTable16;
import filesystem.fat.fat16.FileSystemFat16;
import filesystem.utils.OutputFormater;

import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainView extends JFrame {
	private FileSystemFat16 fileSystemFAT16;
	
	private BootBlock bootBlock;
	private FatDirectory rootDirectory;
	private DataRegion dataRegion;
	private FileAllocationTable fileAllocationTable;
	
	//Converted types
	private BootBlock16 bootBlock16;
	private Fat16Directory fat16Directory;
	private DataRegion16 dataRegion16;
	private FileAllocationTable16 fileAllocationTable16;
	
	//Other
	private long totalSlackFileSizeInBytes = 0;
	
	private Container container;
	private Container infoContainer = new Container();
	private Container fatContainer = new Container();
	private Container dataContainer = new Container();
	
	//public MainView(String title, BootBlock bootBlock, FatDirectory fatDirectory, DataRegion dataRegion, FileAllocationTable fileAllocationTable) throws IOException, NotEnoughBytesReadException
	public MainView(String title, FileSystemFat16 fileSystemFAT16) throws IOException, NotEnoughBytesReadException
	{
		//Set window title
		super(title);
		
		this.fileSystemFAT16 = fileSystemFAT16;
		
		this.bootBlock = fileSystemFAT16.getBootBlock();
		this.rootDirectory = fileSystemFAT16.getFatDirectory();
		this.dataRegion = fileSystemFAT16.getDataRegion();
		this.fileAllocationTable = fileSystemFAT16.getFileAllocationTable();
		
		this.bootBlock16 = (BootBlock16)bootBlock;
		this.fat16Directory = (Fat16Directory)rootDirectory;
		dataRegion16 = (DataRegion16)dataRegion;
		fileAllocationTable16 = (FileAllocationTable16)fileAllocationTable;
		
		setSize(800, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		container = getContentPane();
		container.setLayout(new GridLayout(3, 1));
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData("CF","","","","","","","","","",true));
		
		//ArrayList<FatEntry> filesInFolder = fat16Directory.directory();
		ArrayList<FatEntry> filesInFolder = fileSystemFAT16.ls();
		
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
        infoContainer.setLayout(new GridLayout(10, 1));
        JLabel label1 = new JLabel("Total file slack size in bytes: " + OutputFormater.formatOutput(totalSlackFileSizeInBytes));
        infoContainer.add(label1);
        
        JLabel label2 = new JLabel("FAT type: " + bootBlock16.getType());
        infoContainer.add(label2);
        
        JLabel label3 = new JLabel("Bytes per sector: " + OutputFormater.formatOutput((long)bootBlock16.getBPB_BytsPerSec()));
        infoContainer.add(label3);
        
        JLabel label4 = new JLabel("Number of sectors per allocation unit (cluster): " + (long)bootBlock16.getBPB_SecPerClus());
        infoContainer.add(label4);
        
        //Cluster <=> AllocationUnit
        long bytesPerAllocationUnit = (long)bootBlock16.getBPB_BytsPerSec() * (long)bootBlock16.getBPB_SecPerClus();
        JLabel label5 = new JLabel("Number of Bytes per allocation unit (cluster): " + OutputFormater.formatOutput(bytesPerAllocationUnit));
        infoContainer.add(label5);
        
        JLabel label6 = new JLabel("Number of reserved blocks: " + (long)bootBlock16.getBPB_RsvdSecCnt());
        infoContainer.add(label6);
        
        JLabel label7 = new JLabel("Number of FAT data structures on the volume: " + (long)bootBlock16.getBPB_NumFATs());
        infoContainer.add(label7);
        
        JLabel label8 = new JLabel("Number of sectors occupied by ONE FAT: " + (long)bootBlock16.getFATSz());
        infoContainer.add(label8);
        
        JLabel label9 = new JLabel("Number of sectors in data region: " + (long)bootBlock16.getNumberOfDataSectors());
        infoContainer.add(label9);
        
        JLabel label10 = new JLabel("Number of clusters in data region: " + (long)bootBlock16.getCountofClustersInDataRegion());
        infoContainer.add(label10);
        
        container.add(infoContainer);
        
        //---------------------------------------------------------
        FatTableComponent fatTableComponent = new FatTableComponent(fatContainer, fileSystemFAT16);
        fatTableComponent.fillModel();
        
        container.add(fatContainer);
        
        DataTableComponent dataTableComponent = new DataTableComponent(dataContainer, fileSystemFAT16);
        dataTableComponent.fillModel();
        
        container.add(dataContainer);
        //---------------------------------------------------------
	}
	
	private void scanFileSystem(ArrayList<FatEntry> files, DefaultMutableTreeNode treeNode) throws IOException, NotEnoughBytesReadException
	{
		//For all files (some may be directories)
		for (int i = 0; i < files.size(); i++)
		{
			Fat16Entry file = (Fat16Entry)files.get(i);
			
			String filename = file.getFilename();
			String filenameExtension = file.getFilenameExtension();
			String longFilename = file.getLongFileName();
			char startingClusterNumber = file.getStartingClusterNumber();
			long filesizeInBytes = file.getFilesizeInBytes();
			String md5OfData = file.calculateMd5OfData();
			String md5OfFileSlack = file.calculateMd5OfFileSlack();
			long totalClustersNeededForData = file.getTotalClustersNeededForData();
			long totalAllocatedSizeInBytes = totalClustersNeededForData * dataRegion16.getBytesPerCluster();
			long fileSlackSizeInBytes = file.getFileSlackSizeInBytes();
			
			totalSlackFileSizeInBytes += fileSlackSizeInBytes;
			
			String sStartingClusterNumber = Long.toString((long)startingClusterNumber);
			String sFilesizeInBytes = OutputFormater.formatOutput(filesizeInBytes);
			String sTotalClustersNeededForData = Long.toString(totalClustersNeededForData);
			String sTotalAllocatedSizeInBytes = OutputFormater.formatOutput(totalAllocatedSizeInBytes);
			String sFileSlackSizeInBytes = OutputFormater.formatOutput(fileSlackSizeInBytes);
			
			//If file
			if (!file.isSubdirectoryEntry())
			{
				treeNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,longFilename,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5OfData,md5OfFileSlack,false)));
			}
			//If directory
			else
			{
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,longFilename,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5OfData,md5OfFileSlack,true));
						
				//Ignore . and .. in search
				if (filename.compareToIgnoreCase(".") != 0 && filename.compareToIgnoreCase("..") != 0)
				{
					fileSystemFAT16.cd(file);
					ArrayList<FatEntry> filesInSubdir = fileSystemFAT16.ls();
					
					scanFileSystem(filesInSubdir, subNode);
				}
				
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