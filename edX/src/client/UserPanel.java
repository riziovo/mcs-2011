package client;

import stream.User;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.List;


public class UserPanel extends JPanel
{
    private List _userlist ;
    private JTree usrtree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private Client frame;
    private Hashtable nodeTable = new Hashtable();
    private UserPanel panel;
    private static final String libName = "client";
    private User user;

    private DataInputStream dis;
    private DataOutputStream dos;

    public UserPanel(Client frame,List vector,DataInputStream dis,DataOutputStream dos,User user) throws Exception
    {
        this.frame = frame;
        this._userlist = vector;
        this.dis=dis;
        this.dos=dos;
        this.user=user;

        launchPanel();
    }

    private void launchPanel() throws Exception
    {
        this.setLayout(new FlowLayout());

        rootNode = new DefaultMutableTreeNode("Joined User");
        createNodes(rootNode);

        treeModel = new DefaultTreeModel(rootNode);
        usrtree = new JTree(treeModel);
        usrtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        ToolTipManager.sharedInstance().registerComponent(usrtree);

        usrtree.setCellRenderer(new MyRenderer());
        usrtree.addMouseListener(new MyMouseAdapter(frame,usrtree,dis,dos,user));

        JScrollPane scrollpane;
        scrollpane=new JScrollPane(usrtree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollpane.setPreferredSize(new Dimension(200,330));

        this.add(scrollpane);
    }

    private void createNodes(DefaultMutableTreeNode top)
    {
        DefaultMutableTreeNode user = null;

        for(int count = 0; count<_userlist.size();count++)
        {
            user = new DefaultMutableTreeNode((User)_userlist.get(count));
            nodeTable.put(((User)_userlist.get(count)).toString(),user);
            top.add(user);
        }
    }

    public void addUser(Object child)
    {
        DefaultMutableTreeNode childNode =new DefaultMutableTreeNode(child);
        treeModel.insertNodeInto(childNode,rootNode,rootNode.getChildCount());
        nodeTable.put(((User)child).toString(),childNode);
    }

    public void removeUser(User user)
    {
        MutableTreeNode node = (MutableTreeNode)nodeTable.get(user.toString());
        node.setUserObject(user);
        treeModel.reload(node);
    }

    public void updateUser(User user)
    {
        MutableTreeNode node;
        node = (MutableTreeNode)nodeTable.get(user.toString());
        if(node == null)
        {
            addUser(user);
            return;
        }
        node.setUserObject(user);
        treeModel.reload(node);
        nodeTable.put(user.toString(),node);
    }
}

class MyRenderer extends DefaultTreeCellRenderer
{
    final ClassLoader loader = ClassLoader.getSystemClassLoader();

    final ImageIcon rootIcon = new ImageIcon(Objects.requireNonNull(loader.getResource("root.gif")));
    final ImageIcon onlineIcon = new ImageIcon(Objects.requireNonNull(loader.getResource("online.gif")));
    final ImageIcon offlineIcon = new ImageIcon(Objects.requireNonNull(loader.getResource("offline.gif")));
    final ImageIcon busyIcon = new ImageIcon(Objects.requireNonNull(loader.getResource("busy.gif")));
    final ImageIcon idleIcon = new ImageIcon(Objects.requireNonNull(loader.getResource("idle.gif")));

    public MyRenderer()
    {

    }
    public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus)
    {

        super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);

        if (leaf)
        {
            User user=getUser(value);
            switch(user.isOnline)
            {
                case 1 :
                    setIcon(onlineIcon);
                    setToolTipText("I am online @"+user.hostname);
                    break;
                case 2:
                    setIcon(offlineIcon);
                    setToolTipText("Offline");
                    break;
                case 3 :
                    setIcon(busyIcon);
                    setToolTipText("I am busy");
                    break;
                case 4 :
                    setIcon(idleIcon);
                    setToolTipText("Away from computer");
                    break;
                default:
                    setIcon(offlineIcon);
                    setToolTipText("Offline");
            }
        }
        else
        {
            setIcon(rootIcon);
            setToolTipText(null);
        }

        return this;
    }

    private User getUser(Object value)
    {
        DefaultMutableTreeNode node =(DefaultMutableTreeNode)value;
        User nodeInfo =(User)(node.getUserObject());
        return nodeInfo;
    }

}

class MyMouseAdapter extends MouseAdapter
{
    private Client frame;
    private JTree tree;
    DataInputStream dis;
    DataOutputStream dos;
    User user;
    public MyMouseAdapter(Client frame,JTree tree,DataInputStream dis,DataOutputStream dos,User user)
    {
        this.frame = frame;
        this.tree = tree;
        this.dis=dis;
        this.dos=dos;
        this.user=user;
    }

    public void mouseClicked(MouseEvent e)
    {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

        DefaultMutableTreeNode node;
        if(selRow > 0 )
        {

            if(e.getClickCount() == 2)
            {
                node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
                User user = (User)(node.getUserObject());
                if(user.isOnline != 2)
                {
                    System.out.println("UserName="+user.userName);
                    System.out.println("HostName="+user.hostname);


                    frame.createFrame(user,dis,dos);

                }
                else
                {
                    JOptionPane.showMessageDialog(frame,"Click on an online user to send a message",	"Error",JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
    }
}