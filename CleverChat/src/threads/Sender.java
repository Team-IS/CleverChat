package threads;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * @author Mentonka Isampel Anna-Maria ==> 3100109
 * @author Pitsios Stamatios ==> 3100153
 * 
 * An instance of this class is the client that will be responsible for sending the messages to the other device.
 */
public class Sender extends Thread
{
	/**
	 * The IP of the host device.
	 */
	private String hostIP;
	
	/**
	 * The message to be sent to the other device.
	 */
	private String messageToSend;
	
	/**
	 * The context - Activity on which this client is running.
	 */
	private Context context;
	
	/**
	 * The "connect to chat button" of the interface.
	 */
	private Button connectButton;
	
	/**
	 * The "send message" button of the interface.
	 */
	private Button sendButton;
	
	/**
	 * The edit text of interface.
	 */
	private EditText editText;
	
	/**
	 * The text view of the interface.
	 */
	private TextView textView;
	
	
	
	
	public Sender(String ip , Context context , Button btn1 , Button btn2 , EditText edit , TextView textView)
	{
		this.hostIP = ip;
		this.messageToSend = "";
		this.context = context;
		this.connectButton = btn1;
		this.sendButton = btn2;
		this.editText = edit;
		this.textView = textView;
	}
	
	
	
	
	/**
	 * Sets the message to be send to the other device , to the value given as parameter.
	 * @param msg The message to be send.
	 */
	public void setMessage(String msg)
	{
		this.messageToSend = msg;
	}
	
	
	
	
	@Override
	public void run()
	{
		Socket senderSocket = null;
		PrintWriter writer = null;
		
		try
		{
			//Create an address on which the client socket should connect.
			SocketAddress address = new InetSocketAddress(hostIP, 5000);
			
			//Try to connect to the specified address in time less than 7s. Otherwise exception will be thrown.
			senderSocket = new Socket();
			senderSocket.connect(address, 7000);
			
			//Open the writer.
			writer = new PrintWriter(senderSocket.getOutputStream(), true);
			
			/*
			 * If the message that we have to send is "!###TEST_CONNECTION_MESS@GE###!" , 
			 * this means that we want to test if the host is listening.
			 * If yes , then we will show a connection successful message.
			 * Otherwise an exception will be thrown.
			 */
			if(messageToSend.equals("!###TEST_CONNECTION_MESS@GE###!"))
			{
				/*
				 * Run the following code on UIThread , in order to be able to handle the interface components.
				 */
				((Activity)context).runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						/*
						 * Create and show a message to the current device , indicating that the connection to the host was successful.
						 */
						AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				        dialog.setMessage("Connection established successfully!");
				        dialog.setPositiveButton("OK", new OnClickListener() {
							
				        	   //Set some of te interface widgets visible.
				        	   @Override
				        	   public void onClick(DialogInterface dialog, int which) 
				        	   {
				        		   connectButton.setEnabled(false);
				        		   sendButton.setVisibility(View.VISIBLE);
				        		   editText.setVisibility(View.VISIBLE);
				        		   textView.setVisibility(View.VISIBLE);
				        	   }
				           });
				        dialog.show();
					}
				});
				
				writer.println("");
			}
			
			else
			{
				writer.println(messageToSend);				
			}
			
			//Wait for 5 seconds in order to be sure that the message was delivered before attempting to close the connections.
			sleep(5000);
			writer.close();
			senderSocket.close();	
		}
		
		catch (UnknownHostException e) 
		{
			
			((Activity)context).runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			        dialog.setMessage("Can not connect to " + hostIP +".\nServer is down or does not exist.\nPlease try later.");
			        dialog.setTitle("Error Occurred");
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
		
		catch (IOException e) 
		{
			((Activity)context).runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			        dialog.setMessage("An IOException occured.\nPlease try later.");
			        dialog.setTitle("IOException occurred");
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
			        dialog.setTitle("Exception occurred.");
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
		
	}//run method end.
}