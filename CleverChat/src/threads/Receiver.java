package threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Html;
import android.widget.TextView;


/**
 * @author Mentonka Isampel Anna-Maria ==> 3100109
 * @author Pitsios Stamatios ==> 3100153
 * 
 * An instance of this class is the server that will be responsible for accepting the messages that were delivered from the other device.
 */
public class Receiver extends Thread 
{
	/**
	 * textview that holds the messages
	 */
	private TextView textView = null;
	
	/**
	 * Object Context
	 */
	private Context context = null;
	
	/**
	 * port number used to create the server socket
	 */
	private final int portNumber = 5000;
	
	/**
	 * Object ServerSocket that holds the socket of our server
	 */
	private ServerSocket serverSocket = null;
	
	/**
	 * Object ClientSocket that holds the socket of our client
	 */
	private Socket clientSocket = null;
	
	/**
	 * Object BufferedReader
	 */
	private BufferedReader reader = null;
	
	/**
	 * flag used in order to determine whether the co-chatter has exited or not
	 * co-chatter still there if true
	 */
	private boolean flag;
	
	
	
	
	/**constructor 
	 * 
	 * @param textView
	 * @param context
	 */
	public Receiver(TextView textView , Context context)
	{
		this.textView = textView;
		this.context = context;
		this.flag = true;
	}
	
	
	
	
	/**
	 * Set the flag to false , so that the server terminates.
	 */
	public void Stop()
	{
		this.flag = false;
	}
	
	
	
	
	/**
	 * method that creates the server socket using a specific port number
	 * initially the server is started
	 * messages are received 
	 * when there are no other messages or the co-chatter has exited it closes the server and client sockets
	 */
	@Override
	public void run()
	{			
		try
		{
			serverSocket = new ServerSocket(portNumber);
			String inputLine;
			
			//Keep the server alive , until we close the application or our co-chater left.
			while(flag)
			{
				//Wait for a client to connect.
				clientSocket = serverSocket.accept();
				
				//Open the stream so that we can receive messages from the client.
				reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				//wait for a message.
				inputLine = reader.readLine();
				
				final String messageReceived = inputLine;
				
				//If the message is "###!TERMIN@TE_CH@T!###" , which means that our co-chater left , terminate the server.
				if(messageReceived.equals("###!TERMIN@TE_CH@T!###"))
				{
					break;
				}
				
				//Else , if the message is not an empty string , display it.
				else if(!(messageReceived.equals("")))
				{
					((Activity)context).runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							textView.append(Html.fromHtml("<font color='#00F63'><b>"+messageReceived+"</b></font>"));	
							textView.append("\n\n");
						}
					});
				}
				
				//Close the socket and the input stream.
				clientSocket.close();
				reader.close();
			}
			
			//Close all the open connections.
			serverSocket.close();
			clientSocket.close();
			reader.close();
			
			//If the server ended because our co-chater left , inform the user about it.
			if(flag) showGoodbyeMessage();		
		}
		
		catch(Exception e)
		{
			final Exception ex = e;

			((Activity)context).runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			        dialog.setMessage(ex.getMessage());
			        dialog.setTitle("Exception Occured");
			        dialog.setPositiveButton("OK", new OnClickListener() {
						
			        	   @Override
			        	   public void onClick(DialogInterface dialog, int which) 
			        	   {
			        		   return;
			        	   }
			           });
			           
			           dialog.show();
				}
			});
		}
	}
	
	
	
	
	/**
	 * A method that displays a message in case when the co-chatter exits the chat
	 */
	private void showGoodbyeMessage()
	{	
		((Activity)context).runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		        dialog.setMessage("Your co-chater left.\nApplication will now close.");
		        dialog.setTitle("Chat is over.");
		        dialog.setPositiveButton("OK", new OnClickListener() {
						
		        	   @Override
		        	   public void onClick(DialogInterface dialog, int which) 
		        	   {
		        		   ((Activity)context).finish();
		        		   System.exit(0);
		        	   }
		           });
			           
		       dialog.show();
			}
		});	
	}
}