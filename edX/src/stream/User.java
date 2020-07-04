package stream;

public class User implements java.io.Serializable
{
    public String userName;
    public String hostname;
    public int isOnline;
    public boolean isConference=false;

    public User(String userName,String hostname,int isOnline)
    {
        this.hostname=hostname;
        this.userName=userName;
        this.isOnline = isOnline;
    }

    public String toString()
    {
        return userName;
    }
}