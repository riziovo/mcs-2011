package client;

import stream.Stream;
import stream.User;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.Timer;
import javax.swing.text.html.*;
import java.io.*;


public class ChatWindow extends JFrame implements ActionListener
{

    JFileChooser choose;
    JMenu file;
    JMenuItem Fsend,about;
    JMenuBar menubar;
    private Client frame;
    private ChatWindow thisframe;
    private Container container;
    private JEditorPane dArea;
    private JTextArea wArea;
    private JButton send;
    private User user;
    private int hwnd;
    private Timer timer=null;
    boolean isFocused = false;


    private DataInputStream dis;
    private DataOutputStream dos;


    public ChatWindow(Client frame,User user,DataInputStream dis,DataOutputStream dos)
    {
        this.frame = frame;
        this.user = user;
        launchWindow();
        this.dis=dis;
        this.dos=dos;
    }

    public void launchWindow()
    {
        thisframe = this;
        container= this.getContentPane();
        container.setLayout(null);

        dArea = new JEditorPane();
        dArea.setEditorKit(new HTMLEditorKit());

        dArea.setEditable(false);

        JScrollPane dPane= new JScrollPane(dArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dPane.setBounds(10,10,400,100);

        wArea = new JTextArea();
        wArea.setFont(new Font("Arial",Font.PLAIN,14));
        wArea.setLineWrap(true);

        JScrollPane wPane= new JScrollPane(wArea,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wPane.setBounds(10,120,400,50);


        send = new JButton("Send");
        send.setBounds(415,120,65,50);
        send.addActionListener(this);

        container.add(dPane);
        container.add(wPane);
        container.add(send);

        wArea.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent ke)
            {
                if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    setVisible(false);
                    frame.removeFrame(user);
                }
                else if(ke.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if(wArea.getText().length() == 0) return;
                    wDisplay(frame.getUserFrame().toString(),wArea.getText(),false);
                    Stream stream = new Stream(10,wArea.getText());
                    stream.destination = user.toString();

                    try
                    {
                        System.out.println("Stream Destination="+user.toString()+"\nStream="+stream.message);
                        frame.sendMessageToServer(stream);
                    }
                    catch(Exception e)
                    {
                        JOptionPane.showMessageDialog(container,"Error sending stream! Please try again","Error",JOptionPane.ERROR_MESSAGE);
                    }
                    wArea.setText("");
                }
            }
        });


        wArea.addMouseListener( new MouseInputAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                isFocused=true;
                wArea.setEnabled(true);
                wArea.requestFocus();
                if(timer!=null)timer.stop();
            }
        });





        send.addMouseListener( new MouseInputAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if(wArea.getText().length()==0)
                    return;
                else
                {
                    wDisplay(frame.getUserFrame().toString(),wArea.getText(),false);
                    Stream stream=new Stream(10,wArea.getText());
                    stream.destination=user.toString();
                    stream.user=frame.getUserFrame();
                    try
                    {
                        frame.sendMessageToServer(stream);
                    }
                    catch(Exception exc)
                    {
                        JOptionPane.showMessageDialog(container,"Error sending message! Please try again","Error",JOptionPane.ERROR_MESSAGE);
                    }
                    wArea.setText("");
                }
            }
        });


        send.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent ke)
            {
                if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    setVisible(false);
                    frame.removeFrame(user);
                }
            }
        });

        dArea.addMouseListener(new MouseInputAdapter()
        {
            public void mouseClicked(MouseEvent me)
            {
                isFocused = true;
                if(timer != null)timer.stop();
            }
        });

        this.setResizable(false);
        this.setSize(600,250);
        this.setTitle(user+" - Stream");
        this.setLocation(300,300);

        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                if(timer != null) timer.stop();
                frame.removeFrame(user);
            }

            public void windowActivated(WindowEvent ae)
            {
                isFocused = true;
                if(timer != null) timer.stop();
            }

            public void windowDeactivated(WindowEvent ae)
            {
                isFocused = false;
            }
            public void windowOpened( WindowEvent e )
            {
                wArea.requestFocus();
            }
        });

        menubar=new JMenuBar();
        file=new JMenu("File");
        Fsend=new JMenuItem("Send");
        Fsend.addActionListener(this);
        about=new JMenuItem("About");
        about.addActionListener(this);
        file.add(Fsend);
        menubar.add(file);
        menubar.add(about);
        setJMenuBar(menubar);
        this.setVisible(true);
        wArea.requestFocus();

        timer = new Timer(500,new FlashwindowListener(thisframe));
        isFocused = false;
    }


    public void startFlashing()
    {
        isFocused = false;
        timer.start();
    }

    public void Update(Stream stream)
    {
        //Stream stream=(Stream)O;

        if(!stream.username.equals(user.toString()))
            return;

        if(stream.header==2)
        {
            wDisplay(null,user.toString()+" logged off at "+new Date(),true);
            wArea.setEnabled(false);
            send.setEnabled(false);
        }
        if(stream.header==10)
        {
            wDisplay(user.toString(),stream.getMessage(),true);
        }
    }


    public String toString()
    {
        return user.toString();
    }

    public void wDisplay(String user,String str,boolean received)
    {
        if(user != null)
        {
            if(received)
            {
                str ="<FONT COLOR='red' STYLE='font-size:14pt;font-family:Arial'>"+user+": </FONT><FONT STYLE='font-size:12pt;font-family:Arial'>"+str;
            }
            else
            {
                str ="<FONT COLOR='blue' STYLE='font-size:14pt;font-family:Arial'>"+user+": </FONT><FONT STYLE='font-size:12pt;font-family:Arial'>"+str;
            }
        }
        else
        {
            str ="<FONT COLOR='red' STYLE='font-size:12pt;font-family:Arial'><B>"+str;
        }

        str+="</FONT>";

        try
        {
            ((HTMLEditorKit)dArea.getEditorKit()).read(new java.io.StringReader(str),dArea.getDocument(), dArea.getDocument().getLength());
            dArea.setCaretPosition(dArea.getDocument().getLength());
        }
        catch(Exception e){}
    }


    public void actionPerformed(ActionEvent event)
    {
        if(event.getSource()==about)
        {
            ImageIcon icon =new ImageIcon(" images/middle.gif ");
            JOptionPane.showMessageDialog(frame,"Developed By Rushi Brahmbhatt \n TYBCA \n Navrachana University","About the chat application",JOptionPane.INFORMATION_MESSAGE,icon);
        }

        if(event.getSource()==Fsend)
        {
            Vector myvec=new Vector();

            BufferedReader br;
            String line;
            choose=new JFileChooser();
            choose.setCurrentDirectory(new File("."));
            int result=choose.showOpenDialog(this);
            if(result==JFileChooser.APPROVE_OPTION)
            {
                String name=choose.getSelectedFile().getPath();
                String nam=choose.getSelectedFile().getName();
                System.out.println("File is-->"+nam);
                Stream stream=new Stream(15);
                stream._filecontent=new Vector();

                try
                {
                    br=new BufferedReader(new FileReader(name));

                    while((line=br.readLine())!=null)
                    {
                        stream._filecontent.add(line);

                    }
                    br.close();

                    stream.destination=user.toString();
                    stream.filename=nam;

                    try
                    {
                        System.out.println("Stream Destination="+user.toString()+"\nStream="+stream.message);
                        frame.sendMessageToServer(stream);
                        wDisplay(null,"<FONT COLOR='green' STYLE='font-size:10pt;font-family:Courier New'><b>"+user.toString()+" Send File "+name+"<b></font>",false);
                    }
                    catch(Exception e)
                    {
                        JOptionPane.showMessageDialog(container,"Error sending stream! Please try again","Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
                catch(Exception er)
                {
                    System.out.println(er);
                }
            }
        }

        if((event.getSource() == wArea)||(event.getSource() == send))
        {
            if(wArea.getText().length() == 0)
                return;

            wDisplay(frame.getUserFrame().toString(),wArea.getText(),false);
            Stream stream = new Stream(10,wArea.getText());
            stream.destination = user.toString();
            try
            {
                frame.sendMessageToServer(stream);
            }
            catch(Exception e)
            {
                System.out.println("Error sending stream");
            }
            wArea.setText("");
        }
    }
}

class FlashwindowListener implements ActionListener
{
    private Window chatwindow;
    private final native void flashWindow(Window chatwindow);

    public FlashwindowListener(Window window)
    {
        this.chatwindow = window;
    }

    public void actionPerformed(ActionEvent ae)
    {
        flashWindow(chatwindow);
    }
}