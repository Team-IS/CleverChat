package com.example.cleverchat;

import threads.Receiver;
import threads.Sender;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * @author Mentonka Isampel Anna-Maria ==> 3100109
 * @author Pitsios Stamatios ==> 3100153
 * 
 * The main activity of the application.
 */
public class MainActivity extends Activity
{
	/**
	 * Object receiver that represents the server
	 */
	private Receiver receiver = null;
	
	/**
	 * Object sender that represents the client
	 */
	private Sender sender = null;
	
	/**
	 * The name of user of the chat
	 */
	private String myName;
	
	/**
	 * The ip on which the client of this device will connect.
	 */
	private String IP;
	
	/**
	 * The message to be sent to the other device.
	 */
	private String message;
	
	
	
	
	/**
	 *  Method called when the activity is starting.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			
		//TextView that holds all the messages sent and received.
		TextView textView = (TextView)findViewById(R.id.textChat);
		
		//Start the receiver.
		receiver = new Receiver(textView , this);
		receiver.start();
	}
	
	
	
	
	/**
	 * Method that initializes the contents of the Activity's standard options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	
	/**
	 *  Method called when activity is destroyed.
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	
	
	
	
	/**
	 *  Method called when the activity has detected the user's press of the back key.
	 */
	@Override
	public void onBackPressed()
	{
	    this.exitApp();
	}
	
	
	
	
	/**
	 * Method called when the Button "Start Chat" is pressed.
	 * It opens a dialog window in order to insert the information necessary to connect with the co-chatter.
	 * @param view
	 */
	public void startChat(View view)
	{	
		LayoutInflater inflater = LayoutInflater.from(this);
		
		final View dialogLayout = inflater.inflate(R.layout.connect_to_chat_layout, null);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		dialog.setView(dialogLayout);
		
		// The title of the dialog window
		dialog.setTitle("Connect to chat");
		
		// Set positive option of the dialog window
		dialog.setPositiveButton("Connect", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// The ip address of the co-chatter
				EditText editIP = ((EditText)dialogLayout.findViewById(R.id.hostIpInput));
				
				// The name of the current user of the chat
				EditText editName = ((EditText)dialogLayout.findViewById(R.id.userNameInput));
				
				IP = 	 editIP.getText().toString();
				myName = editName.getText().toString();
				message = "!###TEST_CONNECTION_MESS@GE###!";
				
				// Start the client
				startClient();
			}
		});
		
		// Set negative option of the dialog window
		dialog.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				return;
			}
		});
			
		dialog.show();
	}
	
	
	
	
	/**
	 * Method called in order to start the sender (client)
	 */
	public void startClient()
	{
		//Initialize the sender.
		sender = new Sender(IP , this , (Button)findViewById(R.id.start_chat) , (Button)findViewById(R.id.sendMessage) , (EditText)findViewById(R.id.chatMessage) , (TextView)findViewById(R.id.textChat));
		
		//Set the message to be sent.
		sender.setMessage(this.message);
		
		// Start the sender
		sender.start();				
	}
	
	
	
	
	/**
	 * Method called when the Button "Quit" is pressed.
	 * @param view
	 */
	public void Quit(View view)
	{
		this.exitApp();
    }
	
	
	
	
	/**
	 * Method that performs the necessary actions before the activity is destroyed.
	 */
	private void exitApp()
	{
		//Send a message to the other device , to inform it that the chat will now terminate.
		this.message = "###!TERMIN@TE_CH@T!###";
		this.startClient();
		
		//If the receiver is not null, stop the receiver.
		if(!(receiver == null)) this.receiver.Stop();
			
		try
		{	
			//Wait for 2 seconds to be sure that the "###!TERMIN@TE_CH@T!###" message will be sent before the application is closed.
			Thread.sleep(2000);
			
			//Exit the app.
			this.onDestroy();
			finish();
			System.exit(0);
		}
		
		catch(Exception e)
		{
			
		}	
	}
	
	
	
		
	/**
	 * Method called when the Button "Send Message" is pressed.
	 * The Method gets the message written from the user and sends it to the co-chatter.
	 * It also appends the message to the textView that holds all the messages sent and received.
	 * @param view
	 */
	public void sendMessage(View view)
	{
		// The message written by the current user .
		EditText myMessage = (EditText) findViewById(R.id.chatMessage);
		String text = myMessage.getText().toString();
		
		// The messages sent and received
		TextView textView = (TextView)findViewById(R.id.textChat);
		
		// The message to send in the format : " User's name : Message to send " .
		String newText = this.myName + " wrote : "+ text;
		
		// Append the message the current user wrote to the textView and color it dark green.
		textView.append(Html.fromHtml("<font color='#003324'><b>"+newText + "</b></font>"));
		textView.append("\n\n");
		
		//Set the message to the input given and send it.
		this.message = newText;
		this.startClient();
		
		// EditText emptied
		myMessage.setText("");
	}
}