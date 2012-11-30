package fi.jamk.android.zsoltnagy;

import java.util.Properties;

/*import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;*/

import android.util.Log;

public class EmailSender implements Runnable {

	String from = "nagy.zsolt.hun.public@gmail.com";
    String to = "nagy.zsolt.hun@gmail.com";
    String subject = "Testing Subject";
    String bodyText = "This is a important message with attachment TODO";
    
    Properties props;
    
    
    EmailSender() {
    	props = new Properties();
    }
	public void run() {
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

}
