package gui;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.treetable.*;
import org.jdesktop.swingx.decorator.*;

import fat.DOSFilename;
import fat.RootDirectory;
import fat.RootDirectoryEntry;

import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainView extends JFrame {
	public MainView(String title, RootDirectory rootDir)
	{
		super(title);
		
		setSize(800, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		
		
		
		ArrayList<RootDirectoryEntry> files = rootDir.directory();
		
		System.out.println("files.size(): " + files.size());
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TableRowData("CF","","","",true));
		
		for (int i = 0; i < files.size(); i++)
		{
			DOSFilename file = (DOSFilename)files.get(i);
			
			String filename = file.getFilename();
			String filenameExtension = file.getFilenameExtension();
			char startingClusterNumber = file.getStartingClusterNumber();
			long filesizeInBytes = file.getFilesizeInBytes();
			String sStartingClusterNumber = Long.toString((long)startingClusterNumber);
			String sFilesizeInBytes = Long.toString(filesizeInBytes);
			
			if (!file.isSubdirectoryEntry())
			{
				rootNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,false)));
			}
			else
			{
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,false));
				subNode.add(new DefaultMutableTreeNode(new TableRowData(filename,filenameExtension,sStartingClusterNumber,sFilesizeInBytes,false)));
				rootNode.add(subNode);
			}
		}
		
		//rootNode.add(incomeNode);
		
		JXTreeTable binTree = new JXTreeTable(new MyTreeModel(rootNode));
    	
    	Highlighter highligher = HighlighterFactory.createSimpleStriping(HighlighterFactory.BEIGE);
    	binTree.setHighlighters(highligher);
        binTree.setShowGrid(false);
        binTree.setShowsRootHandles(true);
        configureCommonTableProperties(binTree);
        binTree.setTreeCellRenderer(new TreeTableCellRenderer());
        
        this.getContentPane().add(new JScrollPane(binTree));
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
