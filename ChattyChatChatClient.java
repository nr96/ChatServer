import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

public class ChattyChatChatClient 
{
    public static void main(String args[]) throws UnknownHostException, IOException 
    { 
        Scanner scanner = new Scanner(System.in); //open terminal scanner
        Socket socket = new Socket(args[0], Integer.parseInt(args[1])); //connect to server socket
        
        //obtaining input and out data streams 
        DataInputStream input = new DataInputStream(socket.getInputStream()); 
        DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 

        SendMessage sendMessage = new SendMessage(output, scanner);
        ReadMessage readMessage = new ReadMessage(input);
 
        Thread sendMessageThread = new Thread(sendMessage); //create sendMessage thread
        Thread readMessageThread = new Thread(readMessage); //create readMessage thread 

        sendMessageThread.start(); //start sendMessageThread
        readMessageThread.start(); //start readMessageThread
    }//END main() 

    public void stopIO(DataInputStream input, DataOutputStream output, Socket socket) //close resources 
    {
        try
        { 
            input.close(); //close input data stream 
            output.close(); //close output data stream 
            socket.close(); //close socket connection
        }//END try
        catch(IOException e)
        {  e.printStackTrace(); }    
    }//END stopIO()
}//END ChattyChatChatClient class 

class ReadMessage implements Runnable //read the message sent to this client
{
    final DataInputStream input;

    public ReadMessage(DataInputStream input)
    { this.input = input; }//END ReadMessage()

    @Override
    public void run()
    {
        try
        {
            String msg;
            while (true) 
            { 
                msg = input.readUTF(); //read input message 
                System.out.println(msg); //display message
            }//END while 
        }//END try
        catch (IOException e) 
        { System.exit(0); }//END catch 
    }//END run()
} //END ReadMessage class

class SendMessage implements Runnable //send the message from client to server
{
    final DataOutputStream output;
    final Scanner scanner; 

    public SendMessage(DataOutputStream output, Scanner scanner)
    {
        this.output = output;
        this.scanner = scanner;
    }//END SendMessage()

    @Override
    public void run() 
    { 
        while (true) 
        { 
            String msg = scanner.nextLine();  //read the message to deliver.     
            try 
            { 
                output.writeUTF(msg); //write on the output stream 
            }//END try 
            catch (IOException e) 
            { 
                e.printStackTrace();
                System.exit(0); 
            }//END catch 
        }//END while
    }//END run()    
}//END SendMessage class 
