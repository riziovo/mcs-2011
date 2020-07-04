package server;

import java.io.*;

public class DbUser implements Serializable
{
    private int ID;
    private String username;
    private String passwd;
    private String secQ;
    private String Answer;
    private String tickr;

    public DbUser() {}

    public DbUser(String name,String pass,String q,String ans,String tickr)
    {
        this.username=name;
        this.passwd=pass;
        this.secQ=q;
        this.Answer=ans;
        this.tickr=tickr;
    }

    public int getID()
    {
        return this.ID;
    }

    public void setUsername(String name)
    {
        this.username=name;
    }
    public String getUsername()
    {
        return this.username;
    }

    public void setPasswd(String pass)
    {
        this.passwd=pass;
    }
    public String getPasswd()
    {
        return this.passwd;
    }

    public void setSecQ(String q)
    {
        this.secQ=q;
    }
    public String getSecQ()
    {
        return this.secQ;
    }

    public void setAnswer(String a)
    {
        this.Answer=a;
    }
    public String getAnswer()
    {
        return this.Answer;
    }

    public void setTickr(String tickr)
    {
        this.tickr=tickr;
    }
    public String getTickr()
    {
        return this.tickr;
    }
}