package client;


import stream.Stream;
import stream.User;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Client extends JFrame implements ActionListener,ItemListener,Runnable
{
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private Dimension screensize = toolkit.getScreenSize();


    private JLabel label;

    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem l_item,e_item,about;

    private String _username,_password,_server,_address;

    private static Client frame;
    Container container;
    private JComboBox combo;
    private UserPanel panel;
    Thread thread;
    Socket socket;
    private boolean connected = false;
    private boolean done=false;
    User user;
    private DataInputStream dis;
    private DataOutputStream dos;
    public static List _userlist = new ArrayList();
    ChatWindow dialog ;
    public static Hashtable frameTable = new Hashtable();
    PrintWriter pr;


    private void sendLogout()
    {
        try {
            Stream stream = new Stream(2);
            user.isOnline = 2;
            stream.user = user;
            sendMessageToServer(stream);
        }
        catch(Exception e)
        {
            System.exit(0);
        }
    }


    private void sendStatus(int status)
    {
        try {
            switch(status)
            {
                case 0:
                    status = 1;
                    break;
                case 1:
                    status = 3;
                    break;
                case 2:
                    status = 2;
                    break;
                default:
                    status = 4;
            }

            Stream stream = new Stream(7);
            user.isOnline = status;
            stream.user = user;
            sendMessageToServer(stream);
        }
        catch(Exception e)
        {
            System.exit(0);
        }
    }

    public void removeFrame(User user)
    {
        synchronized(frameTable)
        {
            frameTable.remove(user.toString());
        }
    }

    public void createFrame(User user,DataInputStream diss,DataOutputStream doss)
    {
        dialog = (ChatWindow) frameTable.get(user.toString());
        if(dialog == null)
        {
            dialog = new ChatWindow(this,user,dis,dos);
            dialog.setLocation(500, 500);
        }
        frameTable.put(user.toString(),dialog);
    }

    public User getUserFrame()
    {
        return user;
    }


    public Client()throws UnknownHostException
    {
        _address=InetAddress.getLocalHost().toString();

        frame = this;
        container = this.getContentPane();
        container.setLayout(new FlowLayout());



        menubar= new JMenuBar();
        menu = new JMenu("Login");
        l_item=new JMenuItem("Login");
        e_item=new JMenuItem("Exit");
        about=new JMenuItem("About");

        menu.add(l_item);
        menu.add(e_item);
        menubar.add(menu);
        menubar.add(about);
        this.setJMenuBar(menubar);


        about.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(e.getSource()==about)
                {
                    ImageIcon icon =new ImageIcon(" images/middle.gif ");
                    JOptionPane.showMessageDialog(frame,"Developed By RIZIOVO \n 2011 ","About the chat application",JOptionPane.INFORMATION_MESSAGE,icon);
                }
            }
        });

        l_item.addActionListener(this);
        e_item.addActionListener(this);
        label = new JLabel("Status");
        combo = new JComboBox();
        combo.addItem("I'm available");
        combo.addItem("Busy");
        combo.addItem("Invisible");
        combo.addItem("Away");
        combo.addItemListener(new ItemListener()
                              {
                                  public void itemStateChanged(ItemEvent event)
                                  {
                                      if(event.getStateChange() == ItemEvent.SELECTED)
                                      {
                                          sendStatus(((JComboBox)event.getSource()).getSelectedIndex());
                                      }
                                  }
                              }
        );

        this.addWindowListener(new WindowAdapter() {

            public void windowIconified(WindowEvent e)
            {

            }

            public void windowClosing(WindowEvent e)
            {
                if(JOptionPane.showConfirmDialog(container,"Are you sure you want to quit?","Quit ",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null) == JOptionPane.YES_OPTION)
                {
                    sendLogout();
                    System.exit(0);
                }
            }
        });

        this.setSize(250, 500);
        this.setLocation(400,100);
        this.setVisible(true);
        this.setResizable(true);
        this.setTitle("Chat Client");
        processLogin();


    }

    public  void processLogin()
    {
        LoginD dialog = new LoginD(this);
        _username= dialog.getUserName();
        _password= dialog.getPassword();
        _server = dialog.getServerHost();
        int _p=dialog.getPort();


        System.out.println("User Name="+_username);
        System.out.println("Password="+_password);
        System.out.println("Host="+_server);

        if(_username == null || _password==null)
            return;

        try
        {
            if((_username.length()!=0) || (_password.length() != 0))
            {
                user = (new User(LoginD.getUserName(),InetAddress.getLocalHost().toString(),1));

                Stream stream = new Stream(1);
                stream.message = _password;
                stream.user = user;
                socket = new Socket(_server,_p);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                sendMessageToServer(stream);
                connected = true;
                thread = new Thread(this,"USER THREAD");
                thread.start();
                l_item.setEnabled(false);
            }

        }
        catch(Exception ee)
        {
            System.out.println(ee);
        }

    }

    public synchronized void sendMessageToServer(Stream stream) throws Exception
    {
        stream.user = user;
        byte[] data;

        stream.username = _username;
        stream.host = _address;
        data = TProtocol.ObjToBytes(stream);
        dos.write(data,0,data.length);
        dos.flush();

        System.out.println("Stream Sent......"+stream.message);

    }



    public void run()
    {

        while(connected && !done)
        {
            try {

                byte[] data;
                data = new byte[8192];
                System.out.println("Before Reading");
                dis.read(data);
                System.out.println("After Reading");
                Stream stream = (Stream)TProtocol.BytesToObj(data);
                System.out.println("Stream Header="+stream.header);
                String str;

                switch(stream.header)
                {

                    case 15:
                        try
                        {

                            System.out.println("Before PrintWriter...");
                            pr=new PrintWriter(new FileWriter("C:///"+stream.filename),true);
                            System.out.println("No Of Lines in a File="+stream._filecontent.size());

                            Iterator enum1=stream._filecontent.iterator();
                            while (enum1.hasNext())
                            {
                                str=(String)enum1.next();
                                pr.println(str);
                            }
                            pr.close();
                            dialog.wDisplay(stream.user.toString(),"File Named  "+stream.filename+"received successfully from "+stream.user.toString()+"and is stored in C: drive",true);

                        }
                        catch(Exception er)
                        {
                            System.out.println(er);
                        }

                        break;

                    case 6:

                        for(int cnt=0;cnt<stream.userlist.size();cnt++)
                            _userlist.add(stream.userlist.get(cnt).toString());

                        System.out.println("After For Loop");
                        panel = new UserPanel(this,stream.userlist,dis,dos,user);
                        System.out.println("After Panel");
                        panel.setPreferredSize(new Dimension(220,300));
                        container.add(panel);
                        container.add(label);
                        container.add(combo);
                        container.validate();
                        break;

                    case 1:

                        if(!stream.username.equals(_username))
                            _userlist.add(stream.user.toString());

                        panel.updateUser(stream.user);
                        System.out.println("Login Stream USer--"+stream.user.toString());

                        break;

                    case 7:

                        panel.updateUser(stream.user);
                        break;

                    case 2:

                        if(_userlist.contains(stream.user.toString()))
                        {
                            _userlist.remove(stream.user.toString());
                            panel.removeUser(stream.user);
                            dialog.Update(stream);

                        }

                    case 10:

                        createFrame(stream.user,dis,dos);
                        //dialog = (ChatWindow) frameTable.get(user.toString());
                        System.out.println(stream.user.toString()+"  Stream-->"+stream.message);
                        dialog.wDisplay(stream.user.toString(),stream.message,true);

                        break;
                    default:
                }
            }
            catch(Exception se)
            {
                JOptionPane.showMessageDialog(frame,"Server connection reset!! Please login again","Talk2Me: Error",JOptionPane.ERROR_MESSAGE);
                done = true;
                System.out.println("Error:::"+se);
            }

        }

    }

    public static void main(String args[])throws Exception
    {
        Client object=new Client();
    }

    public void actionPerformed(ActionEvent arg0)
    {
    }

    public void itemStateChanged(ItemEvent arg0)
    {
    }

}


