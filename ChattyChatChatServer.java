import java.io.*; 
import java.util.*; 
import java.net.*;
  
public class ChattyChatChatServer  //ChattyChatChatServer class handles server duties i.e. listening for clients
{ 
    static Vector<ClientHandler> clientList = new Vector<>(); //vector to store active clients 
      
    public static void main(String[] args) throws IOException  
    { 
        ServerSocket serversock = new ServerSocket(Integer.parseInt(args[0])); //begin listening on port # passed in
        //System.out.println("Server now Listening");  
         
        Socket socket; //create socket for clients to connect to  
 
        while (true) //continue running server infinitely so clients can connect 
        { 
            socket = serversock.accept(); //accept the incoming request 
  
            //System.out.println("New client request received : " + socket); 
              
            //obtain input and output streams 
            DataInputStream input = new DataInputStream(socket.getInputStream()); 
            DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 
              
            //System.out.println("Creating a new handler for this client..."); 
 
            ClientHandler client = new ClientHandler(socket, input, output); //create a new ClientHandler Obj for current client
            Thread thread = new Thread(client); //create a new thread for client           
            clientList.add(client); //add client to active clients list  
            thread.start(); //start the thread

        }//END while
    }//END main
}//END ChattyChatChatServer class

class ClientHandler implements Runnable  //ClientHandler class to handle client input
{ 
    String name = "unknown"; //set client nickname to unkown until they manually set one
    final DataInputStream input; 
    final DataOutputStream output; 
    Socket socket; //create socket for clients to connect to
      
    public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output) //constructor to initialize IO
    { 
        this.input = input; 
        this.output = output; 
        this.socket = socket; 
    }// ClientHandler() 
  
    @Override
    public void run() //main thread to handle client input
    { 
        String line; 
        while (true) //always listen for client input
        { 
            try
            { 
                line = input.readUTF(); // read client input 
                System.out.println(name + ": " + line); 
                  
                if(line.equals("/quit"))
                { 
                    stopIO(); //close client resources
                    break; 
                }//END if 

                if (line.indexOf("/nick")>=0) //check for /nick command
                    this.setName(parseMessage(line)); //set client nickname
                else if (line.indexOf("/dm")>=0) //check for /dm command
                    this.directMessage(parseMessage(line)); //dm requested client
                else
                    sendToAll(line); //no special command detected, sent input to all clients;
            }//END try
            catch (IOException e) 
            {  e.printStackTrace(); }//END catch   
        }//END while 
    }//END run()

    public String[] parseMessage(String line) //method to parse input from a singular string to string[]
    {
       String[] parsedLine = line.split(" "); //split line and disregard spaces
       return parsedLine;//return String[] 
    }//END parseMessage()

    public void setName(String[] parsedLine)//method to set client nickname
    {
        this.name = parsedLine[1];  
    }//END setName()

    public void sendToAll(String line) //method to send msg to all clients in clientList  
    {
        try
        {
            for (ClientHandler client : ChattyChatChatServer.clientList)  //iterate through clients in clientList
                client.output.writeUTF(this.name + ": " + line); //display message to client   
        }//END try
        catch(IOException e)
        {  e.printStackTrace(); } 
    }//END sendToAll()

    public void directMessage(String[] parsedLine) //method to send a DM to specified client 
    {
        try
        {
            for (ClientHandler client : ChattyChatChatServer.clientList) //search for client in clientList
                sendDM(client, parsedLine); 
        }//END try
        catch(IOException e)
        {  e.printStackTrace(); }     
    }//END directMessage()

    public void sendDM(ClientHandler client, String[] parsedLine) throws IOException
    {
        if (client.name.equals(parsedLine[1])) //if the recipient is found, send message
        {
            parsedLine = copyStringArray(parsedLine); //create new String[] without /dm & name
            String message = String.join(" ", parsedLine); //combine String[] to singular String
            client.output.writeUTF(this.name + ": " + message); //finally display message to client
        }//END if 
    }//END sendDM()

    public void stopIO() //method to close client IO resources
    {
        try
        { 
            this.input.close(); 
            this.output.close();
            this.socket.close();  
        }//END try
        catch(IOException e)
        {  e.printStackTrace(); }    
    }//END stopIO()
  
    public String[] copyStringArray(String[] array) //copying one array to another
    {
        String[] newArray = new String[array.length-2]; //create new String[]

        for(int i = 0; i < array.length-2; i++) //iterate through original String[]
            newArray[i]=array[i+2]; //copy to new String[]
            
        return newArray; //return new String[]
    }//END CopyStringArray()

}//END class ClientHandler
