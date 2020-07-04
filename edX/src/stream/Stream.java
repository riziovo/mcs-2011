package stream;

import java.io.*;
import java.util.*;

public class Stream implements Serializable
{
    public int header;
    public String username;
    public String destination;
    public String message;
    public String host;
    public User user;
    public List userlist;
    public String data;
    public List _filecontent;
    public String filename;

    public Stream()
    {}

    public Stream(int header)
    {
        this.header = header;
    }

    public Stream(int header,String message)
    {
        this.header = header;
        this.message=message;
    }

    public String getMessage()
    {
        return this.message;
    }
}