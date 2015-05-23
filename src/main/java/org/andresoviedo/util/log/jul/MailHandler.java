package org.andresoviedo.util.log.jul;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.mail.MessagingException;

import org.andresoviedo.util.bean.MailInfoBean;
import org.andresoviedo.util.data.CyclicBuffer;
import org.andresoviedo.util.email.EMailSender;

public class MailHandler extends Handler {

	private Formatter formatter;
	private Level level;
	private MailInfoBean mailInfo;
	private int maxRecords;

	private boolean mustSend = false;
	private CyclicBuffer buffer;

	public MailHandler(MailInfoBean mailInfo, int maxRecords, Level level, Formatter formatter) {
		this.mailInfo = mailInfo;
		this.level = level;
		this.formatter = formatter;
		this.maxRecords = maxRecords;
		buffer = new CyclicBuffer(maxRecords);
	}

	public int getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}

	public void close() throws SecurityException {
		flush();
	}

	public void flush() {
		if (buffer.size() > 0 && mustSend) {
			sendMail();
		}
	}

	public void publish(LogRecord record) {
		buffer.add(record);
		if (record.getLevel().intValue() >= level.intValue()) {
			mustSend = true;
		}
		// System.out.println("maxRecords[" + maxRecords + "], buffer size[" + buffer.size() + "], mustSend[" + mustSend + "]");
		if (buffer.size() == maxRecords && mustSend) {
			sendMail();
			buffer = new CyclicBuffer(maxRecords);
			mustSend = false;
		}
	}

	private void sendMail() {
		System.out.println("sending mail...");
		StringBuffer message = new StringBuffer();
		for (Object record : buffer.get()) {
			if (record != null) {
				message.append(formatter.format((LogRecord) record));
			}
		}
		mailInfo.setMessage(message.toString());
		try {
			EMailSender.send(mailInfo);
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}
}