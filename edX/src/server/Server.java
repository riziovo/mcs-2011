package server;

import stream.Stream;
import stream.User;


import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{

    ServerSocket server;

    Socket socket;

    byte[]data;

    private static Hashtable userSocketList = new Hashtable();

    private static Hashtable userObjList = new Hashtable();

    private static List allUserSockets;

    String hostname;

    Handler handler;

    public Server()
    {
        try
        {
            allUserSockets=new ArrayList(10);
            server=new ServerSocket(4321);
            while(true)
            {
                socket=server.accept();
                if(socket!=null)
                {
                    synchronized(allUserSockets)
                    {
                        allUserSockets.add(socket);
                    }

                }
                DataInputStream dis=new DataInputStream(socket.getInputStream());
                data = new byte[8192];
                System.out.println("Before");
                dis.read(data);
                System.out.println("After");
                Stream stream = ((Stream)TProtocol.BytesToObj(data));
                hostname=stream.host;
                System.out.println("Joined client "+stream.username+" at "+stream.host+"...");
                synchronized(userSocketList)
                {
                    userSocketList.put(stream.username,socket);
                }
                synchronized(userObjList)
                {
                    userObjList.put(stream.user.toString(),stream.user);
                }


                SendAllUserList(stream);
                WriteToAllClients(stream);
                handler = new Handler(socket,hostname,stream.user);
            }
        }
        catch(Exception ee)
        {
            System.out.println(ee);
        }
    }


    public void SendAllUserList(Stream stream)
    {
        int header;
        String destination;
        header=stream.header;
        stream.header=6;
        stream.userlist=new Vector(userObjList.values());
        destination=stream.destination;
        stream.destination=stream.username;
        WriteToClientStream(stream);

        stream.header=header;
        stream.destination=destination;
    }

    public static void WriteToClientStream(Stream stream)
    {
        Socket socket;
        byte[] data;
        DataOutputStream dos;
        synchronized(userSocketList)
        {
            try
            {
                socket = (Socket)userSocketList.get(stream.destination);
                dos=new DataOutputStream(socket.getOutputStream());
                data=TProtocol.ObjToBytes(stream);
                dos.write(data,0,data.length);
                System.out.println("Stream="+stream.message+"\n Stream dEStination="+stream.destination+"\nStream User="+stream.username);

            }
            catch(Exception e)
            {
                System.out.println("SEND EXCEPTION"+e);
            }
        }
    }

    public static synchronized void WriteToAllClients(Stream stream)
    {
        byte[] data;
        DataOutputStream dos;
        int count;
        for(count=0;count<allUserSockets.size();count++)
        {
            try {
                dos=new DataOutputStream(((Socket)allUserSockets.get(count)).getOutputStream());
                data=TProtocol.ObjToBytes(stream);
                dos.write(data,0,data.length);
            }
            catch(Exception e) {
                System.out.println("Output exception");
            }
        }
        System.out.println("Total no of Clients that received message so far="+count);

    }


    public static void main(String args[])
    {
        Server life=new Server();
    }


    public static synchronized void processClientStream(Stream stream)
    {
        switch(stream.header)
        {
            case 7:
                UpdateUserList(stream.user);
                WriteToAllClients(stream);
                break;

            case 2:

                RemoveUser(stream.user);
                WriteToAllClients(stream);
                break;

            default:
                WriteToClientStream(stream);

        }
    }


    public static void UpdateUserList(User user)
    {
        synchronized(userObjList)
        {
            userObjList.put(user.toString(),user);
        }
    }


    public static synchronized void RemoveUser(User user)
    {
        try
        {
            Socket socket = (Socket)userSocketList.get(user.toString());
            allUserSockets.remove(socket);
            userObjList.remove(user.toString());
            userSocketList.remove(user.toString());
        }
        catch(Exception e)
        {
            System.out.println("ERROR REMOVING SOCKET "+e);
        }
    }
}


class Handler implements Runnable
{

    private DataInputStream dis;
    private Socket socket;
    private boolean done=false;
    private Thread thread;
    private String hostname;
    private User user;

    public Handler(Socket _socket,String _hostname,User user)
    {
        try	{
            this.socket = _socket;
            this.hostname=_hostname;
            this.user = user;
            dis=new DataInputStream(socket.getInputStream());
            thread=new Thread(this,"SERVICE");
            thread.start();
        }
        catch(Exception e)
        {
            System.out.println("handler constructor"+e);
        }
    }

    public void run()
    {
        byte[] data;
        while(!done)
        {
            try
            {
                data = new byte[8192];
                dis.read(data);
                Stream stream = ((Stream)TProtocol.BytesToObj(data));
                Server.processClientStream(stream);
            }
            catch(Exception e)
            {
                done = true;
                Server.RemoveUser(user);
                Stream stream = new Stream(2);
                user.isOnline = 2;
                stream.user = user;
                Server.WriteToAllClients(stream);
                System.out.println("Server Here"+e);
                try
                {
                    socket.close();
                }
                catch(Exception se)
                {
                    System.out.println("ERROR CLOSING SOCKET "+se);
                }

            }
        }
    }
}