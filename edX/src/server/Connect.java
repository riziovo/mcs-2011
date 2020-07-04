package server;

import java.io.*;
import java.sql.*;

class Connect
{
    private static int connected=0;
    Connection Con;

    public Connect()
    {
        try
        {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            connected=1;
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Driver Class Not Found In Classpath..");
            connected=0;
        }
        try
        {
            Con=DriverManager.getConnection("jdbc:odbc:ServerDB");
            connected = 1;
        }
        catch(SQLException e)
        {
            System.out.println("Server Can Not Connect To The Database..");
            connected = 0;
        }
    }

    public int isConnected()
    {
        return this.connected;
    }

    public void InsertNewDbUser(DbUser u)
    {
        try
        {

            Statement st=Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

            ResultSet Table=st.executeQuery("select * from ServerDB;");

            Table.last();
            int id=(Table.getInt(1)+1);

            Table.moveToInsertRow();

            Table.updateInt(1,id);
            Table.updateString(2,u.getUsername());
            Table.updateString(3,u.getPasswd());
            Table.updateString(4,u.getSecQ());
            Table.updateString(5,u.getAnswer());
            Table.updateString(6,u.getTickr());

            Table.insertRow();
            Table.moveToCurrentRow();
            Table.updateRow();

            st.close();
            Table.close();
        }
        catch(SQLException e)
        {
            System.out.println("Server Could Not Perform The Query..");
            connected = 0;
        }
    }

    public void UpdateDbUser(DbUser u)
    {
        try
        {
            Statement st=Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

            ResultSet Table=st.executeQuery("select * from ServerDB;");

            while(Table.next())
            {
                if(Table.getInt(1)==u.getID())
                {
                    Table.updateString(2,u.getUsername());
                    Table.updateString(3,u.getPasswd());
                    Table.updateString(4,u.getSecQ());
                    Table.updateString(5,u.getAnswer());
                    Table.updateString(6,u.getTickr());

                    break;
                }
            }

            st.close();
            Table.close();
        }
        catch(SQLException e)
        {
            System.out.println("Server Could Not Perform The Query..");
            connected = 0;
        }
    }

    public void DeleteDbUser(DbUser u)
    {
        try
        {

            Statement st=Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

            ResultSet Table=st.executeQuery("select * from ServerDB;");

            while(Table.next())
            {
                if(Table.getInt(1)==u.getID())
                {
                    Table.deleteRow();
                    break;
                }
            }

            st.close();
            Table.close();
        }
        catch(SQLException e)
        {
            System.out.println("Server Could Not Perform The Query..");
            connected = 0;
        }
    }

    /*public int uniqueID()
    {
        Statement st=Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

        ResultSet Table=st.executeQuery("select * from ServerDB;");

        Table.last();
        int id=(Table.getInt(1)+1);

        st.close();
        Table.close();

        return id;
    }*/
    public void CloseConn()
    {
        try
        {
            Con.close();
            connected=0;
        }
        catch(SQLException e)
        {
            System.out.println("Server Could Not Perform The Query..");
            connected = 0;
        }
    }
}