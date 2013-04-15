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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
import javax.swing.JTextArea;
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
import filesystem.hash.Hash;
import filesystem.io.DataTransfer;
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
	
	private Container dataContainer = new Container();
	private DataTableComponent dataTableComponent;
	
	private JXTreeTable binTree = null;
	private JScrollPane binTreeScrollPane = null;
	private JPanel binTreePanel = new JPanel(new GridLayout(1, 1));
	
	//Menu
	private JMenuBar menuBar;
	private JMenu menu, submenu;
	private JMenuItem menuItemExit, menuItemOpenData, menuItemInfo;
	
	//Data loading
	File fileToLoadData = null;
	private String fileToLoadDataMd5 = "";
	private long fileToLoadDataLength = 0;
	private JTable tableLoadedData;
	private DefaultTableModel modelLoadedData = new DefaultTableModel();
	
	private JTable tableSelectedFiles;
	private DefaultTableModel modelSelectedFiles = new DefaultTableModel();
	
	private JPanel panelButtons = new JPanel();
	private JButton buttonAdd = new JButton("Add");
	private JButton buttonClear = new JButton("Clear");
	private JButton buttonWriteToFileSlack = new JButton("Write to file slack");
	private JLabel labelSelectedSlackSize = new JLabel();
	private JButton buttonReadFromFileSlack = new JButton("Read from file slack");
	private JButton buttonRefresh = new JButton("Refresh file system");
	
	//Output
	private String textAreaString = "";
	private JTextAreaWithScroll textAreaWithScroll = new JTextAreaWithScroll (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
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
		modelLoadedData.addColumn("Md5 of data file");
		
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
	    modelSelectedFiles.addColumn("Directory Path");
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
	    
	    createMenu();
		
		container = getContentPane();
		container.setLayout(new GridLayout(3, 1));
		
		
		
		//-------------------
		refreshBinTree();
		container.add(binTreePanel);
        //-------------------
		
        //container.add(new JScrollPane(tableSelectedFiles));
        
        
        
        //---------------------------------------------------------
        //JPanel
        panelButtons.setLayout(new GridLayout(3, 2));
        buttonAdd.addActionListener(this);
        panelButtons.add(buttonAdd);
        
        buttonClear.addActionListener(this);
        panelButtons.add(buttonClear);
        
        buttonWriteToFileSlack.addActionListener(this);
        panelButtons.add(buttonWriteToFileSlack);
        
        buttonReadFromFileSlack.addActionListener(this);
        panelButtons.add(buttonReadFromFileSlack);
        
        buttonRefresh.addActionListener(this);
        panelButtons.add(buttonRefresh);
        
        updateLabelSelectedSlackSize();
        panelButtons.add(labelSelectedSlackSize);
        //---------------------------------------------------------
        
        //Data loading
        //container.add(new JScrollPane(tableLoadedData));
        
        
        //container.add(textAreaWithScroll.getScrollPane());
        
        
        
        //---------------------------------------------------------
        dataTableComponent = new DataTableComponent(dataContainer, fileSystemFAT16);
        
        
        //---------------------------------------------------------
        
        JPanel panelButtons1 = new JPanel(new GridLayout(4, 1));
        panelButtons1.add(new JScrollPane(tableSelectedFiles));
        panelButtons1.add(new JScrollPane(tableLoadedData));
        panelButtons1.add(textAreaWithScroll.getScrollPane());
        panelButtons1.add(panelButtons);
        //container.add(panelButtons);
        container.add(panelButtons1);
        
        
        container.add(dataContainer);
	}
	
	private void createMenu()
	{
		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("File menu");
		menuBar.add(menu);

		menuItemInfo = new JMenuItem("Show info", KeyEvent.VK_I);
		menuItemInfo.addActionListener(this);
		menu.add(menuItemInfo);

		menuItemOpenData = new JMenuItem("Load data file", KeyEvent.VK_L);
		menuItemOpenData.addActionListener(this);
		menu.add(menuItemOpenData);

		menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItemExit.addActionListener(this);
		menu.add(menuItemExit);

		this.setJMenuBar(menuBar);
	}
	
	private void scanFileSystem(ArrayList<FatEntry> files, DefaultMutableTreeNode treeNode) throws IOException, NotEnoughBytesReadException
	{
		//Search
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
			String directoryPath = file.getDirectoryPath();
			
			String time = file.getTimeCreatedOrLastUpdated();
			String date = file.getDateCreatedOrLastUpdated();
			
			//If file
			if (!file.isSubdirectoryEntry())
			{
				treeNode.add(new DefaultMutableTreeNode(new TableRowData(file, filename,filenameExtension,longFilename,directoryPath,time,date,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5OfData,md5OfFileSlack,false)));
			}
			//If directory
			else
			{
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(file, filename,filenameExtension,longFilename,directoryPath,time,date,sStartingClusterNumber,sFilesizeInBytes,sTotalClustersNeededForData,sTotalAllocatedSizeInBytes,sFileSlackSizeInBytes,md5OfData,md5OfFileSlack,true));
						
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
		
		//Return to previous directory through ..
		//For all files (some may be directories)
		for (int i = 0; i < files.size(); i++)
		{
			Fat16Entry file = (Fat16Entry)files.get(i);
			String filename = file.getFilename();
			
			//If directory
			if (file.isSubdirectoryEntry())
			{
				//If directory is ..
				if (filename.compareToIgnoreCase("..") == 0)
					fileSystemFAT16.cd(file);
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
			actionMenuItemExit();
		}
		else if (e.getSource() == menuItemOpenData)
		{
			actionMenuItemOpenData();
		}
		else if (e.getSource() == menuItemInfo)
		{
			actionMenuItemInfo();
		}
		else if (e.getSource() == buttonAdd)
		{
			actionButtonAdd();
		}
		else if (e.getSource() == buttonClear)
		{
			actionButtonClear();
		}
		else if (e.getSource() == buttonWriteToFileSlack)
		{
			actionButtonWriteToFileSlack();
		}
		else if (e.getSource() == buttonReadFromFileSlack)
		{
			actionButtonReadFromFileSlack();
		}
		else if (e.getSource() == buttonRefresh)
		{
			actionButtonRefresh();
		}
    }
	
	private void actionMenuItemExit()
	{
		this.setVisible(false);
		this.dispose(); //Destroy the JFrame object
	}
	
	private void actionMenuItemOpenData()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to load");
		
		int userSelection = fileChooser.showOpenDialog(this);
		
		//JFileChooser.CANCEL_OPTION : the user cancels file selection.
		//JFileChooser.APPROVE_OPTION: the user accepts file selection.
		//JFileChooser.ERROR_OPTION: if there’s an error or the user closes the dialog by clicking on X button.
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.getSelectedFile().length() <= 0)
		    {
		    	errorBox("File is empty! No data loaded.", "Empty file");return;
		    }
			
			fileToLoadData = fileChooser.getSelectedFile();
		    System.out.println("Open as file: " + fileToLoadData.getAbsolutePath());
		    System.out.println("length in bytes: " + fileToLoadData.length());
		    
		    //Empty data table
            for( int i = modelLoadedData.getRowCount() - 1; i >= 0; i-- ) {
            	modelLoadedData.removeRow(i);
            }
            
            //Calculate hash of file
            String md5 = Hash.getMd5FromFile(fileToLoadData);
            System.out.println("md5: " + md5);
            fileToLoadDataMd5 = md5;
            
		    long bytesPerAllocationUnit = (long)bootBlock16.getBPB_BytsPerSec() * (long)bootBlock16.getBPB_SecPerClus();
		    modelLoadedData.addRow(new Object[]{fileToLoadData.getAbsolutePath(), OutputFormater.formatOutput(fileToLoadData.length()), (long)Math.ceil((double)fileToLoadData.length() / (double)bytesPerAllocationUnit), md5});
		    
		    fileToLoadDataLength = fileToLoadData.length();
		    updateLabelSelectedSlackSize();
		}
	}
	
	private void actionMenuItemInfo()
	{
		InfoView infoView = new InfoView(bootBlock16, totalSlackFileSizeInBytes);
		infoView.setVisible(true);
	}
	
	private void actionButtonAdd()
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
		        		Object[] row = new Object[8];
		        		row[0] = file;
		        		row[1] = file.getLongFileName();
		        		row[2] = file.getDirectoryPath();
		        		row[3] = Long.toString((long)file.getStartingClusterNumber());
		        		row[4] = OutputFormater.formatOutput(file.getFilesizeInBytes());
		        		long totalClustersNeededForData = file.getTotalClustersNeededForData();
		        		row[5] = Long.toString(totalClustersNeededForData);
		        		long totalAllocatedSizeInBytes = totalClustersNeededForData * dataRegion16.getBytesPerCluster();
		        		row[6] = OutputFormater.formatOutput(totalAllocatedSizeInBytes);
		        		row[7] = OutputFormater.formatOutput(file.getFileSlackSizeInBytes());
		        		
		        		modelSelectedFiles.addRow(row);
		        		
		        		//Update label
		        		selectedSlackFileSizeInBytes += file.getFileSlackSizeInBytes();
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
	
	private void actionButtonClear()
	{
		//Update label
		selectedSlackFileSizeInBytes = 0;
		updateLabelSelectedSlackSize();
		
		//Empty tableSelectedFiles table
        for( int i = modelSelectedFiles.getRowCount() - 1; i >= 0; i-- ) {
        	modelSelectedFiles.removeRow(i);
        }
        
        //Clear text log output
		textAreaString = "";
		textAreaWithScroll.setText(textAreaString);
	}
	
	private void actionButtonWriteToFileSlack()
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
		
		//Clear text log output
		textAreaString = "";
		textAreaWithScroll.setText(textAreaString);
		
		try
		{
			FileInputStream fileInputStream = new FileInputStream(fileToLoadData);
			
			//Text log output
			textAreaString += "Input file:" + fileToLoadData.getName() + "/" + fileToLoadData.length() + "/" + fileToLoadDataMd5 + "\n";
			textAreaString += "Hidden in:\n";
			textAreaWithScroll.setText(textAreaString);
			
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
				
				//Text log output
				textAreaString += numberOfBytesToRead + "/" + fat16Entry.getFileSlackSizeInBytes() + "/" + fat16Entry.readFromFileSlack().getMd5() + fat16Entry.getDirectoryPath() + fat16Entry.getLongFileName() + "\n";
				textAreaWithScroll.setText(textAreaString);
				
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
	
	private void actionButtonRefresh()
	{
		System.out.println("buttonRefresh");
		
		try
		{
			refreshBinTree();
			dataTableComponent.refresh();
		}
		catch (Exception exc)
		{
			System.out.println(exc);
		}
	}
	
	private void actionButtonReadFromFileSlack()
	{
		//Clear text log output
		textAreaString = textAreaWithScroll.getText();
		
		//Is textArea empty?
		if (textAreaString == null || textAreaString.length() <= 0)
		{
			errorBox("No description of hidden files!", "No description");
			return;
		}
		
		//PARSE log
		StringTokenizer rows = new StringTokenizer(textAreaString, "\n");
		
		//Log should include at least three rows
		if (rows.countTokens() < 3)
		{errorBox("Parse error!", "Parse error");return;}
		
		File outputFile = null;
		FileOutputStream fileOutputStream = null;
		
		int rowNum = 0;
		String originalFileName = null;
		String originalFileHash = null;
		long originalFileSize = -1;
		while (rows.hasMoreTokens())
		{
			String row = rows.nextToken();
			
			//Get input file
			if (rowNum == 0)
			{
				StringTokenizer firstRow = new StringTokenizer(row, ":");
				
				//First row has two parts separated with :
				if (firstRow.countTokens() != 2)
				{errorBox("Parse error!", "Parse error");return;}
				
				System.out.println("countTokens(): " + firstRow.countTokens());
				
				firstRow.nextToken(); //Ignore info text
				String temp = firstRow.nextToken(); //Original file name, it's size in bytes and it's hash
				
				StringTokenizer originalFileTokenizer = new StringTokenizer(temp,"/");
				
				//FileName, file size in bytes and file hash are separated with /
				if (originalFileTokenizer.countTokens() != 3)
				{errorBox("Parse error!", "Parse error");return;}
				
				originalFileName = originalFileTokenizer.nextToken();
				
				try {
					outputFile = new File(originalFileName);
					fileOutputStream = new FileOutputStream(outputFile);
					
					originalFileSize = Long.parseLong(originalFileTokenizer.nextToken());
					
					if (originalFileSize <= 0)
					{errorBox("Parse error!", "Parse error");return;}
					
					originalFileHash = originalFileTokenizer.nextToken();
					
					System.out.println("originalFileHash: " + originalFileHash);
				}
				catch (FileNotFoundException fileNotFoundException)
				{
					errorBox("Parse error (FileNotFoundException)!", "Parse error");return;
				}
				catch (NumberFormatException numberFormatException)	
				{
					errorBox("Parse error (NumberFormatException)!", "Parse error");return;
				}
				
				//originalFileName = firstRow.nextToken();
				
				System.out.println("originalFileName: " + originalFileName);
				System.out.println("originalFileSize: " + originalFileSize);
			}
			else if (rowNum == 1)
				; //Ignore info text
			else
			{
				StringTokenizer rowTokenizer = new StringTokenizer(row, "/");
				
				//Row should consist of at least 4 tokens
				if (rowTokenizer.countTokens() < 4)
				{errorBox("Parse error!", "Parse error");return;}
				
				try {
					int numberOfTokens = rowTokenizer.countTokens();
					
					long slackSpaceUsedInBytes = Long.parseLong(rowTokenizer.nextToken());
					long slackSpaceTotalInBytes = Long.parseLong(rowTokenizer.nextToken());
					
					System.out.println("slackSpaceUsedInBytes: " + slackSpaceUsedInBytes);
					System.out.println("slackSpaceTotalInBytes: " + slackSpaceTotalInBytes);
					
					if (slackSpaceUsedInBytes > slackSpaceTotalInBytes)
					{errorBox("Parse error!", "Parse error");return;}
					
					String fileSlackSpaceMd5 = rowTokenizer.nextToken();
					System.out.println("fileSlackSpaceMd5: " + fileSlackSpaceMd5);
					
					String filePath = "";
					//-4 as we called nextToken() three times and want to ignore the filename
					for (int i = 0; i < numberOfTokens - 4; i++)
					{
						filePath += "/" + rowTokenizer.nextToken();
					}
					filePath += "/";
					
					String fileName = rowTokenizer.nextToken();
					
					System.out.println("filePath: " + filePath);
					System.out.println("fileName: " + fileName);
					
					//MOVE TO FILE PATH
					//String pwd = fileSystemFAT16.getCurrentDirectoryPath();
					//System.out.println("pwd: " + pwd);
					
					try {
						if (!fileSystemFAT16.cd(filePath))
						{errorBox("Cannot open path: " + filePath, "Parse error");return;}
					}
					catch (IOException exp)
					{
						errorBox("Parse error (IOException)!", "Parse error");return;
					}
					catch (NotEnoughBytesReadException exp)
					{
						errorBox("Parse error (NotEnoughBytesReadException)!", "Parse error");return;
					}
					
					//pwd = fileSystemFAT16.getCurrentDirectoryPath();
					//System.out.println("pwd: " + pwd);
					
					//Get content of current folder
					ArrayList<FatEntry> dirContent = fileSystemFAT16.ls();
					
					//Go trough all entries in current directory
					//We are searching for file with name equal to var. fileName
					Fat16Entry fileEntry = null;
					for (int i = 0; i < dirContent.size(); i++)
					{
						Fat16Entry fat16Entry = (Fat16Entry)dirContent.get(i);
						
						//If entry is the file we seek => store reference and break from loop
						if (!fat16Entry.isSubdirectoryEntry() && fat16Entry.getLongFileName().compareToIgnoreCase(fileName) == 0)
						{
							fileEntry = fat16Entry;
							break;
						}
					}
					
					//File found
					if (fileEntry != null)
					{
						DataTransfer dataTransfer = fileEntry.readFromFileSlack();
						byte[] payload = dataTransfer.getPayload();
						
						
						fileOutputStream.write(payload, 0, (int)slackSpaceUsedInBytes);
					}
					else
					{errorBox("File does not exist: " + filePath + fileName, "Parse error");return;}
				}
				catch (NumberFormatException numberFormatException)	
				{
					errorBox("Parse error (NumberFormatException)!", "Parse error");return;
				}
				catch (NotEnoughBytesReadException notEnoughBytesReadException)
				{
					errorBox("Parse error (NotEnoughBytesReadException)!", "Parse error");return;
				}
				catch (IOException ioException)
				{
					errorBox("Parse error (IOException)!", "Parse error");return;
				}
				
				System.out.println("rowTokenizer.countTokens(): " + rowTokenizer.countTokens());
			}
			
			System.out.println(row);
			
			rowNum++;
		}
		
		try {
			fileOutputStream.close();
			
			//Calculate hash of newly created file
			String outputFileHash = Hash.getMd5FromFile(outputFile);
			
			System.out.println("outputFileHash: " + outputFileHash);
			System.out.println("originalFileHash: " + originalFileHash);
			
			//Hash of restored file IS EQUAL to hash of original file
			if (outputFileHash.compareToIgnoreCase(originalFileHash) == 0)
				infoBox("Success! Restored file hash == original file hash.\nFile: " + outputFile.getAbsolutePath() + " was created.", "Success");
			else
				errorBox("Restored file hash != original file hash", "Parse error");return;
			
		}
		catch (IOException ioException)
		{
			errorBox("Parse error (IOException)!", "Parse error");return;
		}
	}
	
	private void refreshBinTree() throws IOException, NotEnoughBytesReadException
	{
		//Move to root on filesystem
		fileSystemFAT16.cd("/");
		
		//Remove treetable from jpanel
		if (binTreeScrollPane != null)
			binTreePanel.remove(binTreeScrollPane);
		
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData(null,"CF","","","","","","","","","","","","",true));
        
        ArrayList<FatEntry> filesInFolder = fileSystemFAT16.ls();
        
        //Recursively
      	totalSlackFileSizeInBytes = 0;
      	scanFileSystem(filesInFolder, rootNode);
      	
        binTree = new JXTreeTable(new MyTreeModel(rootNode));
        
        Highlighter highligher = HighlighterFactory.createSimpleStriping(HighlighterFactory.BEIGE);
    	binTree.setHighlighters(highligher);
        binTree.setShowGrid(false);
        binTree.setShowsRootHandles(true);
        configureCommonTableProperties(binTree);
        binTree.setTreeCellRenderer(new TreeTableCellRenderer());
        binTree.addMouseListener(this);
        
        binTreeScrollPane = new JScrollPane(binTree);
        
        //Add newly created treetable to jpanel
        binTreePanel.add(binTreeScrollPane);
        
        //Same as JFrame resize
        this.validate();
	}
	
	public void updateLabelSelectedSlackSize()
	{
		//labelSelectedSlackSize.setText("Selected file slack size in bytes: " + OutputFormater.formatOutput(selectedSlackFileSizeInBytes) + " / " + OutputFormater.formatOutput(fileToLoadDataLength));
		labelSelectedSlackSize.setText("Loaded data size in bytes: " + OutputFormater.formatOutput(fileToLoadDataLength) + " / " + "Selected file slack size in bytes: " + OutputFormater.formatOutput(selectedSlackFileSizeInBytes));
	}
	
	public static void errorBox(String content, String title)
    {
        JOptionPane.showMessageDialog(null, content, title, JOptionPane.ERROR_MESSAGE);
    }
	
	public static void infoBox(String content, String title)
    {
        JOptionPane.showMessageDialog(null, content, title, JOptionPane.INFORMATION_MESSAGE);
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

class JTextAreaWithScroll extends JTextArea
{
    private JScrollPane scrollPane;

    public JTextAreaWithScroll (int vsbPolicy, int hsbPolicy)
    {
        scrollPane = new JScrollPane (this, vsbPolicy, hsbPolicy);
    }

    public JScrollPane getScrollPane ()
    {
        return scrollPane;
    }
}