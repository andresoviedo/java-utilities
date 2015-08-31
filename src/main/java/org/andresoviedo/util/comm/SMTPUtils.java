package org.andresoviedo.util.comm;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * SMTP utils is an optional delegated class that is capable of getting a file or a message and send it like an e-mail
 * <p>
 * Requires the following parameters:
 * <li>A file (optional).
 * <li>A subject (optional).
 * <li>A message (optional).
 * <li>An array of recipient addresses (at least one).
 * <li>An originator address.
 * <li>The SMTP server address.
 * <li>A user for SMTP authentication.
 * <li>A password for SMTP authentication.
 * 
 */

public class SMTPUtils {

	public File file = null;

	public String subject = "SMTPMessenger subject";

	public String message = "";

	public InternetAddress[] recipients = null; // Contains InternetAddress
	// instances

	public String from = null; // Originator address

	public String SMTPHost = null; // SMTP Host address

	public String SMTPUser = null; // User for authentication

	public String SMTPPwd = null; // Password for authentication

	public SMTPUtils() {
	}

	public void setHighlight(String highlight) {
		subject = highlight;
	}

	public void setRecipients(InternetAddress[] addresses) {
		recipients = addresses;
	}

	public void clearRecipients() {
		recipients = new InternetAddress[0];
	}

	public boolean dispatch(File f) {
		file = f;
		return dispatch();
	}

	public boolean dispatch() {
		System.out.println("dispatching message");
		long start = System.currentTimeMillis();
		if ((from != null) && (SMTPHost != null) && (SMTPUser != null) && (SMTPPwd != null) && (recipients != null)) {
			// Set properties
			Properties p = new Properties();
			p.put("mail.smtp.host", SMTPHost);
			p.put("mail.user", SMTPUser);
			p.put("mail.smtp.auth", "true");
			p.put("mail.from", from);
			// Setup authenticator
			Authenticator auth = new SMTPAuthenticator(SMTPUser, SMTPPwd);
			// Get session
			Session session = Session.getDefaultInstance(p, auth);
			try {
				MimeMessage mm = new MimeMessage(session);
				mm.setFrom();
				mm.setSubject(subject);
				mm.setRecipients(Message.RecipientType.TO, recipients);
				mm.setSentDate(new Date());
				// Fill contents
				MimeBodyPart mbp1 = new MimeBodyPart();
				mbp1.setText(message);
				MimeBodyPart mbp2 = new MimeBodyPart();
				if (file != null) {
					FileDataSource fds = new FileDataSource(file);
					mbp2.setDataHandler(new DataHandler(fds));
					mbp2.setFileName(file.getName());
					mbp2.setHeader("Content-Transfer Encoding", "BASE64");
				} else {
					mbp2.setText("No attachments");
				}

				Multipart mp = new MimeMultipart();
				mp.addBodyPart(mbp1);
				mp.addBodyPart(mbp2);
				mm.setContent(mp);

				// Get info
				Transport.send(mm);
				long seconds = System.currentTimeMillis() - start;
				System.out.println("message already dispatched. Spent time (miliseconds) =" + seconds);
				return true;
			} catch (MessagingException ex) {
				System.out.println("Exception while dispatching email: " + ex.getMessage());
				// SystemLog.logException(ex);
				Exception nex = null;
				if ((nex = ex.getNextException()) != null) {
					System.out.println("Secondary exception: " + nex.getMessage());
				}
			} catch (Exception e) {
				System.out.println("Exception while dispatching email: " + e.getMessage());
			}
		}
		return false;
	}
}

class SMTPAuthenticator extends Authenticator {
	private String _user = "";

	private String _pwd = "";

	public SMTPAuthenticator(String user, String pwd) {
		_user = user;
		_pwd = pwd;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(_user, _pwd);
	}
}
