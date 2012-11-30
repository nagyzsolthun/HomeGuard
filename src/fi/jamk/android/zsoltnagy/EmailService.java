package fi.jamk.android.zsoltnagy;

import java.util.Properties;

/*import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;*/

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class EmailService extends GuardService {

	TextView delayTextView;
	SeekBar seekBar;
	
	
	String from = "nagy.zsolt.hun.public@gmail.com";
    String to = "nagy.zsolt.hun@gmail.com";
    String subject = "Testing Subject";
    String bodyText = "This is a important message with attachment TODO";
    
    Properties props;
    
    EmailService(MainActivity context) {
    	super(context);
    	setLayoutElements();

    	props = new Properties();
    	setAvailable(false);	//not implemented..
    }
	public void run() {
		if(! isActive()) return;
		Log.d("HomeGuard email", "email sending started");
		/*props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		try {
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("nagy.zsolt.hun.public","");
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(bodyText);
			Transport.send(message);
			Log.d("HomeGuard email", "email sent..");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		Log.d("EmailSender","email should be sent");
	}
	
	private void setLayoutElements() {
    	delayTextView = (TextView) context.findViewById(R.id.emailDelayTextView);
    	seekBar = (SeekBar) context.findViewById(R.id.emailSeekBar);
    	
    	//not working implementation
    	delayTextView.setTextColor(context.getResources().getColor(R.color.inactiveTextColor));
    	seekBar.setEnabled(false);
    	
    	delayTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				context.startActivity(new Intent(context, fi.jamk.android.zsoltnagy.EmailSettingsActivity.class));
			}
		});
    	
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(context, R.string.detailed_email_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("emailDelaySecs", progress);
				sharedPreferencesEditor.commit();
				delayTextView.setText(context.getString(R.string.brief_email_delay)+": " + progress + " sec");
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("emailDelaySecs", 10));
	}
	
	private void changeColors() {
		if(isActive()) {
			delayTextView.setTextColor(context.getResources().getColor(R.color.activeTextColor));
			seekBar.setEnabled(true);
			return;
		}
		seekBar.setEnabled(false);
		if(! isAvailable()) delayTextView.setTextColor(context.getResources().getColor(R.color.unavailableTextColor));
		else delayTextView.setTextColor(context.getResources().getColor(R.color.inactiveTextColor));
	}
	
	@Override
	public void setAvailable(boolean available) {
		super.setAvailable(available);
		changeColors();
	}

}
