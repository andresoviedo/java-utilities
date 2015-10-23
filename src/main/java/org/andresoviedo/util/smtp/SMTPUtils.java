package org.andresoviedo.util.smtp;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorCode;

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

public final class SMTPUtils {

	private static final Logger LOG = Logger.getLogger(SMTPUtils.class);

	private SMTPUtils() {

	}

	public static boolean dispatch(String from, String to, String SMTPHost, Integer SMTPPort, String SMTPUser, String SMTPPwd,
			boolean isTLS, String subject, String message, String contenType, File attachment) {
		long start = System.currentTimeMillis();
		// Set properties
		Properties p = new Properties();
		if (SMTPHost != null) {
			p.put("mail.smtp.host", SMTPHost);
		}
		if (SMTPPort != null) {
			p.put("mail.smtp.port", SMTPPort);
		}
		if (isTLS) {
			p.put("mail.smtp.starttls.enable", "true");
		}
		// Setup authenticator
		Authenticator auth = null;
		if (SMTPUser != null && SMTPPwd != null) {
			p.put("mail.user", SMTPUser);
			p.put("mail.smtp.auth", "true");
			auth = new SMTPAuthenticator(SMTPUser, SMTPPwd);
		}

		// Get session
		Session session = Session.getDefaultInstance(p, auth);
		try {
			MimeMessage mm = new MimeMessage(session);
			if (from != null) {
				mm.setFrom(getAddress(from));
			} else {
				mm.setFrom();
			}
			if (to != null && to.length() > 0) {
				mm.setRecipients(Message.RecipientType.TO, parseAddress(to));
			}
			if (subject != null) {
				try {
					mm.setSubject(MimeUtility.encodeText(subject, "UTF-8", null));
				} catch (UnsupportedEncodingException ex) {
					LOG.error("Unable to encode SMTP subject", ex);
				}
			}

			if (attachment != null) {
				// Fill contents
				MimeBodyPart mbp1 = new MimeBodyPart();
				mbp1.setText(message);
				MimeBodyPart mbp2 = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(attachment);
				mbp2.setDataHandler(new DataHandler(fds));
				mbp2.setFileName(attachment.getName());
				mbp2.setHeader("Content-Transfer Encoding", "BASE64");

				Multipart mp = new MimeMultipart();
				mp.addBodyPart(mbp1);
				mp.addBodyPart(mbp2);
				mm.setContent(mp);
			} else {
				mm.setContent(message, contenType);
			}

			// Get info
			mm.setSentDate(new Date());

			Transport.send(mm);

			long seconds = System.currentTimeMillis() - start;
			LOG.info("Message sent. Spent time (miliseconds) =" + seconds);

			return true;
		} catch (MessagingException ex) {
			LOG.error("Exception while sending email: " + ex.getMessage(), ex);
			// SystemLog.logException(ex);
			Exception nex = null;
			if ((nex = ex.getNextException()) != null) {
				LOG.error("Secondary exception: " + nex.getMessage());
			}
		} catch (Exception e) {
			LOG.error("Unexpected exception: " + e.getMessage(), e);
		}
		return false;
	}

	private static InternetAddress getAddress(String addressStr) {
		try {
			return new InternetAddress(addressStr);
		} catch (AddressException e) {
			LOG.error("Could not parse address [" + addressStr + "]:" + ErrorCode.ADDRESS_PARSE_FAILURE, e);
			return null;
		}
	}

	private static InternetAddress[] parseAddress(String addressStr) {
		try {
			return InternetAddress.parse(addressStr, true);
		} catch (AddressException e) {
			LOG.error("Could not parse address [" + addressStr + "]: " + ErrorCode.ADDRESS_PARSE_FAILURE, e);
			return null;
		}
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
