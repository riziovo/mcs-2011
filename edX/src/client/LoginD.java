package client;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EventObject;
import java.util.Vector;

class LoginD extends JDialog implements ActionListener
{
    private static String _username=null,_password=null,_server=null;
    private static int _port=4321;
    private JLabel label1,label2,label3,label4;
    private JTextField user,server,port;
    private JPasswordField password;
    private JButton ok,cancel;
    private Container container;
    final String SERVER_HOST="localhost";
    final int SERVER_PORT = 2979;

    public LoginD(JFrame frame)
    {
        super(frame,"Login",true);
        initDialogBox(frame);
    }

    private void initDialogBox(JFrame frame)
    {
        container = this.getContentPane();
        container.setLayout(null);
        label1= new JLabel("Login name :");
        label1.setBounds(10,10,80,20);
        label2= new JLabel("  Password :");
        label2.setBounds(10,40,80,20);
        user= new JTextField();
        user.setBounds(100,10,100,20);
        password=new JPasswordField();
        password.setBounds(100,40,100,20);

        label3= new JLabel("    Server :");
        label3.setBounds(10,70,80,20);
        label4= new JLabel("      Port :");
        label4.setBounds(10,100,80,20);
        server= new JTextField(SERVER_HOST);
        server.setBounds(100,70,100,20);
        port=new JTextField((new Integer(4321)).toString());
        port.setBounds(100,100,100,20);
        //port.setEditable(false);
        ok=new JButton("Login");
        ok.setBounds(30,130,70,20);
        cancel= new JButton("Cancel");
        cancel.setBounds(110,130,80,20);

        container.add(label1);
        container.add(user);
        container.add(label2);
        container.add(password);
        container.add(label3);
        container.add(server);
        container.add(label4);
        container.add(port);
        container.add(ok);
        container.add(cancel);

        user.addActionListener(this);
        password.addActionListener(this);
        server.addActionListener(this);
        port.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);

        this.setSize(220,190);
        this.setResizable(false);
        this.setLocationRelativeTo(frame);
        this.setLocation(450,220);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
        if((event.getSource() == ok) || (event.getSource() == password)||(event.getSource() == user) || (event.getSource() == server) )
        {
            _username = user.getText();
            _password = new String(password.getPassword());
            _server = server.getText();
            _port=Integer.parseInt(port.getText());

            if((_username.length()==0) || (_password.length() == 0))
            {
                JOptionPane.showMessageDialog(this,"Please enter a valid username and password","Error",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(_server.length()== 0)
            {
                JOptionPane.showMessageDialog(this,"Invalid server host","Error",JOptionPane.WARNING_MESSAGE);
                return;
            }

        }
        else if(event.getSource() == cancel)
        {
        }
        this.setVisible(false);

    }

    protected static String getUserName()
    {
        return _username;
    }
    protected static String getPassword()
    {
        return _password;
    }
    protected static String getServerHost()
    {
        return _server;
    }
    protected static int getPort()
    {
        return _port;
    }
}