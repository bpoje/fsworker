package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

public class MainView extends JFrame implements ActionListener, MouseListener {
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
	private long selectedSlackFileSizeInBytes = 0;
	
	private Container container;
	private Container infoContainer = new Container();
	private Container dataContainer = new Container();
	
	private JXTreeTable binTree;
	
	//Menu
	private JMenuBar menuBar;
	private JMenu menu, submenu;
	private JMenuItem menuItemExit, menuItemOpenData;
	
	//Data loading
	File fileToLoadData = null;
	private long fileToLoadDataLength = 0;
	private JTable tableLoadedData;
	private DefaultTableModel modelLoadedData = new DefaultTableModel();
	
	private JTable tableSelectedFiles;
	private DefaultTableModel modelSelectedFiles = new DefaultTableModel();
	
	private JPanel panelButtons = new JPanel();
	private JButton buttonAdd = new JButton("Add");
	private JButton buttonClear = new JButton("Clear");
	private JButton buttonWriteToFileSlack = new JButton("Write to file slack");
	private JLabel labelSelectedSlackSize = new JLabel("Selected file slack size in bytes: 0 B / 0 B");
	
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
		
		setSize(1200, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Data loading
		modelLoadedData.addColumn("Filename");
		modelLoadedData.addColumn("Size");
		modelLoadedData.addColumn("Clusters required");
		
	  	tableLoadedData = new JTable(modelLoadedData){
	        private static final long serialVersionUID = 1L;
	        
	        //Disable editing
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
	    
	    //
	    modelSelectedFiles.addColumn("File");
	    modelSelectedFiles.addColumn("Long Filename (VFAT)");
		modelSelectedFiles.addColumn("Starting Cluster Number");
		modelSelectedFiles.addColumn("File size in Bytes");
		modelSelectedFiles.addColumn("Total clusters needed for data");
		modelSelectedFiles.addColumn("Total allocated size in Bytes");
		modelSelectedFiles.addColumn("File slack size in Bytes");
		
	  	tableSelectedFiles = new JTable(modelSelectedFiles){
	        private static final long serialVersionUID = 1L;
	        
	        //Disable editing
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
	    
		//Create the menu bar.
		menuBar = new JMenuBar();
		
		//Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
		        "File menu");
		menuBar.add(menu);
		
		menuItemOpenData = new JMenuItem("Load data file", KeyEvent.VK_I);
		menuItemOpenData.addActionListener(this);
		menu.add(menuItemOpenData);
		
		menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItemExit.addActionListener(this);
		menu.add(menuItemExit);
		
		this.setJMenuBar(menuBar);
		
		container = getContentPane();
		container.setLayout(new GridLayout(6, 1));
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData(null,"CF","","","","","","","","","",true));
		
		//ArrayList<FatEntry> filesInFolder = fat16Directory.directory();
		ArrayList<FatEntry> filesInFolder = fileSystemFAT16.ls();
		
		//Recursively
		totalSlackFileSizeInBytes = 0;
		scanFileSystem(filesInFolder, rootNode);
		
		//rootNode.add(incomeNode);
		
		binTree = new JXTreeTable(new MyTreeModel(rootNode));
    	
    	Highlighter highligher = HighlighterFactory.createSimpleStriping(HighlighterFactory.BEIGE);
    	binTree.setHighlighters(highligher);
        binTree.setShowGrid(false);
        binTree.setShowsRootHandles(true);
        configureCommonTableProperties(binTree);
        binTree.setTreeCellRenderer(new TreeTableCellRenderer());
        
        //---------
        binTree.addMouseListener(this);
        
        //--------
        
        
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
        
        //
        container.add(new JScrollPane(tableSelectedFiles));
        
        //Data loading
        container.add(new JScrollPane(tableLoadedData));
        
      //---------------------------------------------------------
        buttonAdd.addActionListener(this);
        panelButtons.add(buttonAdd);
        
        buttonClear.addActionListener(this);
        panelButtons.add(buttonClear);
        
        buttonWriteToFileSlack.addActionListener(this);
        panelButtons.add(buttonWriteToFileSlack);
        
        panelButtons.add(labelSelectedSlackSize);
        
        container.add(panelButtons);
        
        //---------------------------------------------------------
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
				treeNode.add(new DefaultMutableTreeNode(new TableRowData(file, filename,filenameExtension,longFilename,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5OfData,md5OfFileSlack,false)));
			}
			//If directory
			else
			{
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(file, filename,filenameExtension,longFilename,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5OfData,md5OfFileSlack,true));
						
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
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItemExit)
		{
			this.setVisible(false);
			this.dispose(); //Destroy the JFrame object
		}
		else if (e.getSource() == menuItemOpenData)
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to load");
			
			int userSelection = fileChooser.showOpenDialog(this);
			
			//JFileChooser.CANCEL_OPTION : the user cancels file selection.
			//JFileChooser.APPROVE_OPTION: the user accepts file selection.
			//JFileChooser.ERROR_OPTION: if there’s an error or the user closes the dialog by clicking on X button.
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				fileToLoadData = fileChooser.getSelectedFile();
			    System.out.println("Open as file: " + fileToLoadData.getAbsolutePath());
			    System.out.println("length in bytes: " + fileToLoadData.length());
			    
			    //Empty data table
	            for( int i = modelLoadedData.getRowCount() - 1; i >= 0; i-- ) {
	            	modelLoadedData.removeRow(i);
	            }
	            
			    long bytesPerAllocationUnit = (long)bootBlock16.getBPB_BytsPerSec() * (long)bootBlock16.getBPB_SecPerClus();
			    modelLoadedData.addRow(new Object[]{fileToLoadData.getAbsolutePath(), OutputFormater.formatOutput(fileToLoadData.length()), (long)Math.ceil((double)fileToLoadData.length() / (double)bytesPerAllocationUnit)});
			    
			    fileToLoadDataLength = fileToLoadData.length();
			    updateLabelSelectedSlackSize();
			}
		}
		else if (e.getSource() == buttonAdd)
		{
			//if (fileToLoadData == null)
			//	return;
			
			int [] selectedRows = binTree.getSelectedRows();
	        
	        System.out.print("selectedRows: ");
	        for (int i = 0; i < selectedRows.length; i++)
	        	System.out.print(selectedRows[i] + " ");
	        System.out.println();
	        
	        if (selectedRows.length > 0)
	        {
	        	//System.out.println(binTree.getValueAt(viewRow, viewColumn));
	        	
	        	for (int i = 0; i < selectedRows.length; i++)
	        	{
	        		Fat16Entry file = (Fat16Entry)binTree.getValueAt(selectedRows[i], 0);
	        		
	        		//Add only files (no folders)
	        		if (!file.isSubdirectoryEntry())
	        		{
		        		//System.out.println(binTree.getValueAt(selectedRows[i], 0));
		        		
	        			boolean unique = true;
		        		for (int j = 0; j < tableSelectedFiles.getRowCount(); j++)
		        		{
		        			//System.out.println(tableSelectedFiles.getValueAt(j, 0));
		        			Fat16Entry alreadyIncludedFile = (Fat16Entry)tableSelectedFiles.getValueAt(j, 0);
		        			
		        			if (alreadyIncludedFile == file)
		        			{
		        				unique = false;
		        				break;
		        			}
		        		}
		        		
		        		if (unique)
		        		{
			        		Object[] row = new Object[7];
			        		row[0] = file;
			        		row[1] = file.getLongFileName();
			        		row[2] = Long.toString((long)file.getStartingClusterNumber());
			        		row[3] = OutputFormater.formatOutput(file.getFilesizeInBytes());
			        		long totalClustersNeededForData = file.getTotalClustersNeededForData();
			        		row[4] = Long.toString(totalClustersNeededForData);
			        		long totalAllocatedSizeInBytes = totalClustersNeededForData * dataRegion16.getBytesPerCluster();
			        		row[5] = OutputFormater.formatOutput(totalAllocatedSizeInBytes);
			        		row[6] = OutputFormater.formatOutput(file.getFileSlackSizeInBytes());
			        		
			        		modelSelectedFiles.addRow(row);
			        		
			        		//Update label
			        		selectedSlackFileSizeInBytes += file.getFileSlackSizeInBytes();
			        		//labelSelectedSlackSize.setText("Selected file slack size in bytes: " + OutputFormater.formatOutput(selectedSlackFileSizeInBytes));
			        		updateLabelSelectedSlackSize();
		        		}
	        		}
	        	}
	        	System.out.println();
	        	
	        	/*
	        	long bytesPerAllocationUnit = (long)bootBlock16.getBPB_BytsPerSec() * (long)bootBlock16.getBPB_SecPerClus();
	        	Object[] object = new Object[] {
	        			fileToLoadData.getAbsolutePath(), 
	        			OutputFormater.formatOutput(fileToLoadData.length()), 
	        			(long)Math.ceil((double)fileToLoadData.length() / (double)bytesPerAllocationUnit)
	        			};
	        	
			    modelSelectedFiles.addRow(object);
			    */
	        }
			
		}
		else if (e.getSource() == buttonClear)
		{
			//Update label
			selectedSlackFileSizeInBytes = 0;
			//labelSelectedSlackSize.setText("Selected file slack size in bytes: " + OutputFormater.formatOutput(selectedSlackFileSizeInBytes));
			updateLabelSelectedSlackSize();
			
			//Empty tableSelectedFiles table
            for( int i = modelSelectedFiles.getRowCount() - 1; i >= 0; i-- ) {
            	modelSelectedFiles.removeRow(i);
            }
		}
		else if (e.getSource() == buttonWriteToFileSlack)
		{
			if (fileToLoadData == null)
			{
				errorBox("No data file loaded!", "Load data file");
				return;
			}
			
			long dataFileLengthInBytes = fileToLoadData.length();
			
			if (dataFileLengthInBytes > selectedSlackFileSizeInBytes)
			{
				errorBox("Not enough slack file space!", "Not enough space");
				return;
			}
			
			try
			{
				FileInputStream fileInputStream = new FileInputStream(fileToLoadData);
				
				long bytesRemaining = fileToLoadData.length();
				for (int i = 0; i < modelSelectedFiles.getRowCount(); i++)
				{
					System.out.println(modelSelectedFiles.getValueAt(i, 0));
					
					Fat16Entry fat16Entry = (Fat16Entry)modelSelectedFiles.getValueAt(i, 0);
					
					long fileSlackSizeInBytes = fat16Entry.getFileSlackSizeInBytes();
					System.out.println("fileSlackSizeInBytes: " + fileSlackSizeInBytes);
					
					long numberOfBytesToRead;
					if (bytesRemaining >= fileSlackSizeInBytes)
					{
						System.out.println("a");
						numberOfBytesToRead = fileSlackSizeInBytes;
					}
					else
					{
						System.out.println("b");
						numberOfBytesToRead = bytesRemaining;
					}
					
					System.out.println("numberOfBytesToRead: " + numberOfBytesToRead);
					
					byte[] writeBuffer = new byte[(int)fileSlackSizeInBytes];
					
					int numBytesRead = fileInputStream.read(writeBuffer, 0, (int)numberOfBytesToRead);
					
					System.out.println("writeBuffer.length: " + writeBuffer.length);
					
					if (numBytesRead != (int)numberOfBytesToRead)
					{
						errorBox("Error reading data file!", "Error reading");
						return;
					}
					
					fat16Entry.writeToFileSlack(writeBuffer);
					
					bytesRemaining -= numberOfBytesToRead;
					
					if (bytesRemaining == 0)
						break;
				}
				
				System.out.println("bytesRemaining: " + bytesRemaining);
				
				fileInputStream.close();
			}
			catch (Exception exc)
			{
				System.out.println(exc);
			}
		}
    }
	
	public void updateLabelSelectedSlackSize()
	{
		labelSelectedSlackSize.setText("Selected file slack size in bytes: " + OutputFormater.formatOutput(selectedSlackFileSizeInBytes) + " / " + OutputFormater.formatOutput(fileToLoadDataLength));
	}
	
	public static void errorBox(String content, String title)
    {
        JOptionPane.showMessageDialog(null, content, title, JOptionPane.ERROR_MESSAGE);
    }
	
	public static void infoBox(String infoMessage, String location)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + location, JOptionPane.INFORMATION_MESSAGE);
    }
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource() == binTree)
		{	
			//int viewRow = binTree.getSelectedRow();
	        //int viewColumn = binTree.getSelectedColumn();
	        
	        
	        
			
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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