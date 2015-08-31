package org.andresoviedo.util.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
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

import org.andresoviedo.util.bean.MailInfoBean;

/**
 * EMailSender can send an e-mail with file attachments to many recipients through a host needed authentication.
 * 
 * @author andresoviedo
 * @version 1.0
 */

public class EMailSender {

	public static void send(MailInfoBean mailInfo) throws MessagingException {

		assert (mailInfo.getHost() != null && mailInfo.getUser() != null && mailInfo.getPass() != null);
		assert (mailInfo.getFrom() != null && mailInfo.getTo() != null && mailInfo.getSubject() != null && mailInfo.getMessage() != null);

		// parse mail address
		String[] to = mailInfo.getTo().split(";");
		Address[] addrTo = new InternetAddress[to.length];
		for (int i = 0; i < addrTo.length; i++)
			addrTo[i] = new InternetAddress(to[i]);

		// prepare carbon copy addresses
		Address[] addrCC = null;
		if (mailInfo.getCC() != null) {
			String[] cc = mailInfo.getCC().split(";");
			addrCC = new InternetAddress[cc.length];
			for (int i = 0; i < cc.length; i++)
				addrCC[i] = new InternetAddress(cc[i]);
		}

		// prepare blind carbon copy addresses
		Address[] addrBCC = null;
		if (mailInfo.getBCC() != null) {
			String[] bcc = mailInfo.getBCC().split(";");
			addrBCC = new InternetAddress[bcc.length];
			for (int i = 0; i < bcc.length; i++)
				addrBCC[i] = new InternetAddress(bcc[i]);
		}

		// Set mail properties
		Properties p = new Properties();
		p.put("mail.smtp.host", mailInfo.getHost());
		p.put("mail.smtp.port", String.valueOf(mailInfo.getPort()));
		if (mailInfo.isUseTLS()) {
			p.put("mail.smtp.starttls.enable", "true");
			p.put("mail.smtp.socketFactory.port", String.valueOf(mailInfo.getPort()));
			p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			p.put("mail.smtp.socketFactory.fallback", "false");
		}

		// Setup authenticator
		Authenticator auth = null;
		if (mailInfo.getUser() != null && mailInfo.getPass() != null) {
			// p.put("mail.user", mailInfo.getUser());
			p.put("mail.smtp.auth", "true");
			auth = new MailAuthenticator(mailInfo.getUser(), mailInfo.getPass());
		}

		// Get session
		Session session = Session.getInstance(p, auth);

		MimeMessage mm = new MimeMessage(session);
		mm.setFrom(new InternetAddress(mailInfo.getFrom()));
		mm.setSubject(mailInfo.getSubject());
		mm.setRecipients(Message.RecipientType.TO, addrTo);
		if (addrCC != null) {
			mm.setRecipients(Message.RecipientType.CC, addrCC);
		}
		if (addrBCC != null) {
			mm.setRecipients(Message.RecipientType.BCC, addrBCC);
		}
		mm.setSentDate(new Date());

		// Prepare contents
		Multipart mp = new MimeMultipart();

		// Message part
		MimeBodyPart textPart = new MimeBodyPart();
		if (mailInfo.getMessageEncoding() == null) {
			textPart.setText(mailInfo.getMessage());
		} else {
			textPart.setText(mailInfo.getMessage(), mailInfo.getMessageEncoding());
		}
		mp.addBodyPart(textPart);
		if (mailInfo.isHTMLMessage()) {
			textPart.setHeader("Content-Type", "text/html");
		}

		// Attachments part
		if (mailInfo.getFiles() != null) {
			int i = 0;
			for (File file : mailInfo.getFiles()) {
				MimeBodyPart filePart = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(file);
				filePart.setDataHandler(new DataHandler(fds));
				filePart.setFileName(file.getName());
				filePart.setHeader("Content-Transfer Encoding", "BASE64");
				filePart.setHeader("Content-ID", "file" + i++);
				mp.addBodyPart(filePart);
			}
		}

		// Set mail content
		mm.setContent(mp);

		// Send mail
		Transport.send(mm);
	}

	public static void main(String[] args) {
		// MailInfoBean mailInfo = new MailInfoBean("192.168.0.10", "user",
		// "@user", "user@domain.com", "user@domain.com",
		// "Hola test", "Hola mensaje");
		// File[] files = new File[2];
		// files[0] = new
		// File("C:\\Documents and Settings\\aoviedo\\Escritorio\\GUI.jpg");
		// files[1] = new
		// File("C:\\Documents and Settings\\aoviedo\\Escritorio\\ChatServlet.xml");
		// mailInfo.setFiles(files);
		// try {
		// EMailSender.send(mailInfo);
		// } catch (MessagingException e) {
		// e.printStackTrace();
		// }
		/*
		 * MailInfoBean mailInfo = new MailInfoBean("smtp.gmail.com", 465, "user@gmail.com", "pass", "\"source\" <user@gmail.com>",
		 * "\"Andres Oviedo\" <aoviedo@gmail.com>", "Hola test", //
		 * "<html><h1>Que tal amigo?</h1><ul><li>punto 1</li><li>punto 2</li></ul>jajaja <b>negrita</b></p><img src=\"cid:file0\"><img src=\"cid:file1\"><p>texto después de la imagen</p></html>"
		 * // ); "<html><body><h3>Exceptions encountered while loading business logs</h3></body></html>" );
		 */

		MailInfoBean mailInfo = new MailInfoBean("smtp.google.com", 25, "user@gmail.com", "pass", "\"Andres Oviedo\" <aoviedo@gmail.com>",
				"\"Andres Oviedo\" <aoviedo@gmail.com>", "Hola test",
				// "<html><h1>Que tal amigo?</h1><ul><li>punto 1</li><li>punto 2</li></ul>jajaja <b>negrita</b></p><img src=\"cid:file0\"><img src=\"cid:file1\"><p>texto después de la imagen</p></html>"
				// );
				"<html><body><h3>Exceptions encountered while loading business logs (169)</h3></body></html>");

		mailInfo.setHTMLMessage(true);
		// mailInfo.setUseTLS(true);
		// mailInfo.setFiles(new File[] { new File("c:\\hand1.gif"), new
		// File("c:\\dibujo.gif") });
		try {
			System.out.println("Sending mail...");
			EMailSender.send(mailInfo);
			System.out.println("Mail sent succesfully");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}

class MailAuthenticator extends Authenticator {
	private String _user = "";
	private String _pwd = "";

	public MailAuthenticator(String user, String pwd) {
		_user = user;
		_pwd = pwd;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(_user, _pwd);
	}
}
