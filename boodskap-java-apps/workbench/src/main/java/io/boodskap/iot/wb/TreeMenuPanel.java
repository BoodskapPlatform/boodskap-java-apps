package io.boodskap.iot.wb;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

class TreeMenuPanel extends JPanel {
	
	private static final long serialVersionUID = 3231321626151233198L;
	
	protected DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;
	protected JTree tree;
	
	private Map<TreeMenu, EditorComponent> views = new HashMap<>();
	
	public TreeMenuPanel(final EditorTabbedPanel tabs, String label) {

		super(new GridLayout(1, 0));
		
		rootNode = new DefaultMutableTreeNode(new TreeMenu(TreeMenuType.ROOT_MENU, label));
		treeModel = new DefaultTreeModel(rootNode);

		tree = new JTree(treeModel);
		//tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new TreeMenuRenderer());
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getClickCount() > 1) {
					TreePath currentSelection = tree.getSelectionPath();
					if (currentSelection != null) {
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
						MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
						if (parent != null) {
							
							System.out.println(currentNode.getUserObject());
							TreeMenu menu = (TreeMenu) currentNode.getUserObject();
							EditorComponent view = views.get(menu);
							
							if(null != view) {
								tabs.setSelectedComponent(view);
								return;
							}
							
							switch(menu.getType()) {
							case DOMAIN_RULE:
								view = new EditorComponent(TreeMenuPanel.this, menu);
								tabs.addTab("Domain Rule", TreeMenuRenderer.getIcon(menu.getType()), view);
								break;
							case MESSAGE_RULE:
								view = new EditorComponent(TreeMenuPanel.this, menu);
								tabs.addTab("Message Rule", TreeMenuRenderer.getIcon(menu.getType()), view);
								break;
							case NAMED_RULE:
								view = new EditorComponent(TreeMenuPanel.this, menu);
								tabs.addTab("Domain Rule", TreeMenuRenderer.getIcon(menu.getType()), view);
								break;
							case SCHEDULED_RULE:
								view = new EditorComponent(TreeMenuPanel.this, menu);
								tabs.addTab("Domain Rule", TreeMenuRenderer.getIcon(menu.getType()), view);
								break;
							case GROOVY_CLASS:
								view = new EditorComponent(TreeMenuPanel.this, menu);
								break;
							default:
								return;
							}
							
							if(null != view) {
								views.put(menu, view);
								tabs.setSelectedComponent(view);
								view.load();
							}
							
						}
					}
				}else if(e.getClickCount() == 1) {
					
					TreePath currentSelection = tree.getSelectionPath();
					if (currentSelection != null) {
						
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
						MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
						if (parent != null) {
							TreeMenu menu = (TreeMenu) currentNode.getUserObject();
							JComponent view = views.get(menu);
							if(null != view) {
								tabs.setSelectedComponent(view);
							}
							
						}
					}
				}
			}
			
		});

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
		
		setMinimumSize(new Dimension(220, 80));
	}
	
	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	/** Remove all nodes except the root node. */
	public void clear() {
		rootNode.removeAllChildren();
		treeModel.reload();
	}
	
	public EditorComponent removeView(TreeMenu menu) {
		return views.remove(menu);
	}

	public DefaultMutableTreeNode addMenu(TreeMenu child) {
		DefaultMutableTreeNode parentNode = null;
		TreePath parentPath = tree.getSelectionPath();

		if (parentPath == null) {
			parentNode = rootNode;
		} else {
			parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
		}

		return addMenu(parentNode, child, true);
	}

	public DefaultMutableTreeNode addMenu(DefaultMutableTreeNode parent, TreeMenu child) {
		return addMenu(parent, child, false);
	}

	public DefaultMutableTreeNode addMenu(DefaultMutableTreeNode parent, TreeMenu child, boolean shouldBeVisible) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

		if (parent == null) {
			parent = rootNode;
		}

		// It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

		// Make sure the user can see the lovely new node.
		if (shouldBeVisible) {
			tree.scrollPathToVisible(new TreePath(childNode.getPath()));
		}
		return childNode;
	}

}