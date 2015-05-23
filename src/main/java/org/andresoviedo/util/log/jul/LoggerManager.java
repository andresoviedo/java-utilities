package org.andresoviedo.util.log.jul;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.andresoviedo.util.bean.LoggerInfoBean;
import org.andresoviedo.util.bean.MailInfoBean;

public abstract class LoggerManager {

	public static Logger initLogger(LoggerInfoBean desc) throws IOException {
		assert (desc.getName() != null && desc.getLogFileName() != null && desc.getLevelName() != null && desc.getNumFiles() > 0 && desc
				.getFileSize() > 0);
		Logger logger = LogManager.getLogManager().getLogger(desc.getName());
		if (logger == null) {
			// Prepare logger
			logger = Logger.getLogger(desc.getName());
			logger.setLevel(Level.parse(desc.getLevelName()));
			logger.setUseParentHandlers(false);

			// File handler
			File temp = new File(new File(desc.getLogFileName()).getAbsolutePath());
			if (temp.getParent() != null && !temp.getParentFile().exists()) {
				temp.getParentFile().mkdirs();
			}
			FileHandler fileHandler = new FileHandler(temp.getAbsolutePath(), desc.getFileSize(), desc.getNumFiles(), true);
			DefaultFileFormat mf = new DefaultFileFormat();
			fileHandler.setFormatter(mf);
			fileHandler.setEncoding("UTF-8");
			logger.addHandler(fileHandler);

			// Mail handler
			if (desc.isUseMailHandler()) {
				MailHandler mailHandler = new MailHandler(desc.getMailInfo(), 100, Level.SEVERE, mf);
				logger.addHandler(mailHandler);
			}
		}
		return logger;
	}

	public static void closeLogger(LoggerInfoBean desc) {
		Logger logger = LogManager.getLogManager().getLogger(desc.getName());
		for (Handler h : logger.getHandlers()) {
			h.close();
			logger.removeHandler(h);
		}
	}

	/*
	 * private static SMTPHandler createSMTPHandler(LoggerInfoBean desc) { SMTPHandler ret = null; try { assert (desc.getMailLevelName() !=
	 * null); assert (desc.getMailHost() != null && desc.getMailUser() != null && desc.getMailPass() != null); assert (desc.getMailFrom() !=
	 * null && desc.getMailTo() != null && desc.getMailSubject() != null); assert (desc.getMailSize() > 0); SMTPHandler smtpHandler = new
	 * SMTPHandler(); smtpHandler.setLevel(Level.parse(desc.getLevelName())); smtpHandler.setSmtpHost(desc.getMailHost());
	 * smtpHandler.setSmtpUsername(desc.getMailUser()); smtpHandler.setSmtpPassword(desc.getMailPass());
	 * smtpHandler.setFrom(desc.getMailFrom()); smtpHandler.setTo(desc.getMailTo()); smtpHandler.setSubject(desc.getMailSubject());
	 * smtpHandler.setBufferSize(desc.getMailSize()); ret = smtpHandler; } catch (AssertionError err) { err.printStackTrace(); } catch
	 * (Exception ex) { ex.printStackTrace(); } return ret; }
	 */

	public static void main(String[] args) {
		// TODO: el smtp handler tiene un problema y es que envía un mail cada vez que se genera un log de error.
		try {
			LoggerInfoBean bean = new LoggerInfoBean();
			bean.setName("org.andresoviedo.log");
			bean.setLogFileName("c:\\temp\\2008-01-29\\test_%g.log");
			bean.setFileSize(10240);
			bean.setLevelName("FINE");
			bean.setNumFiles(2);

			bean.setUseMailHandler(true);
			MailInfoBean mailInfo = new MailInfoBean();
			mailInfo.setFrom("aoviedo@gmail.com");
			mailInfo.setTo("aoviedo@gmail.com");
			mailInfo.setHost("192.168.0.10");
			mailInfo.setUser("user");
			mailInfo.setPass("pass");
			mailInfo.setSubject("test");
			bean.setMailInfo(mailInfo);

			LoggerManager.initLogger(bean);

			Logger logger = Logger.getLogger("org.andresoviedo.log");
			int i = 0;
			for (; i < 98; i++) {
				logger.logp(Level.INFO, "LoggerManager", "main", "iteration  [" + i + "]");
				logger.logp(Level.FINEST, "LoggerManager", "main", "iteration  [" + i + "]");
			}

			for (; i < 104; i++) {
				logger.logp(Level.SEVERE, "LoggerManager", "main", "severe iteration  [" + i + "]");
			}

			for (; i < 150; i++) {
				logger.logp(Level.INFO, "LoggerManager", "main", "second iteration  [" + i + "]");
				logger.logp(Level.FINEST, "LoggerManager", "main", "second iteration  [" + i + "]");
			}

			LoggerManager.closeLogger(bean);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
