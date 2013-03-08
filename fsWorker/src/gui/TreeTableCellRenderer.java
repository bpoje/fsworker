package gui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeTableCellRenderer extends DefaultTreeCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5593629042737938947L;

	public TreeTableCellRenderer() {

    	//setOpenIcon(new ImageIcon("icons/minus.gif"));
    	//setClosedIcon(new ImageIcon("icons/plus.gif"));
		
		//Icons: http://en-human-begin.blogspot.com/2007/11/javas-icons-by-default.html
		
		setOpenIcon(UIManager.getIcon("Tree.expandedIcon"));
    	setClosedIcon(UIManager.getIcon("Tree.collapsedIcon"));
    	
    	//setOpenIcon(new ImageIcon("images/cup.gif"));
    	//setClosedIcon(new ImageIcon("images/cup.gif"));
    }

    @Override
	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        
        if(node != null && node.getUserObject() != null && (node.getUserObject() instanceof TableRowData))
        {
        	TableRowData item = (TableRowData)(node.getUserObject());
        	setText(item.getFilename());
        	if(item.isRoot())
        	{
        		//setOpenIcon(new ImageIcon("icons/minus.gif"));
            	//setClosedIcon(new ImageIcon("icons/plus.gif"));
        		
        		setOpenIcon(UIManager.getIcon("Tree.expandedIcon"));
            	setClosedIcon(UIManager.getIcon("Tree.collapsedIcon"));
        		
        	}
        	else
        	{
        		setIcon(null);
        	}
        }
        else
        {
        	setIcon(null);
        }
        return this;

    }

}
