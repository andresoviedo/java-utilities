package org.andresoviedo.util.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

/**
 * FTP client to put files and receive files based on a regexp.
 * 
 * @author andresoviedo
 *
 */
public class EasyFTPsClient {

	private static final Logger LOG = Logger.getLogger(EasyFTPsClient.class);

	private final String user;
	private final String pass;
	private final String host;
	private final int port;

	public EasyFTPsClient(String user, String pass, String host, int port) {
		super();
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.port = port;
	}

	public void put(String filePath, InputStream is) {
		LOG.info("[PUT] Connecting to ftp server: " + host + "[" + user + "/" + pass + "] ...");
		FTPSClient ftpClient = new FTPSClient(false);
		try {
			ftpClient.connect(host, port);
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new RuntimeException("[PUT] Cannot connect to ftp server");
			}

			if (!ftpClient.login(user, pass)) {
				throw new RuntimeException("[PUT] Cannot login in ftp server");
			}

			LOG.info("[PUT] Connected to ftp server.");
			ftpClient.execPBSZ(0);
			ftpClient.execPROT("P");
			ftpClient.enterLocalPassiveMode();

			if (!ftpClient.storeFile(filePath, is)) {
				throw new RuntimeException("[PUT] Problem putting file to ftp server");
			}

			LOG.info("[PUT] File succesfully uploaded.");

		} catch (Exception ex) {
			LOG.error("[PUT] Error: " + ex.getMessage(), ex);
			throw new RuntimeException("[PUT] Problem putting file to ftp server", ex);
		} finally {
			try {
				ftpClient.logout();
				LOG.info("[PUT] Disconnected.");
			} catch (IOException e) {
				throw new RuntimeException("[PUT] Problem disconnecting from ftp server");
			}
		}
	}

	public File[] get(String filePathRegex) {
		try {
			File tempFile = File.createTempFile("java_ftps_client", "");
			tempFile.delete();
			tempFile.mkdir();
			return get(tempFile, filePathRegex);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Get all the files from servers and removes them once we got it.
	 * 
	 * @param outputFolder
	 *            folder where to download files
	 * @param filePathAndNameRegex
	 *            file path and name can be regex. Ex.: /dir1/dir2/^my_pattern$.txt
	 * @return all the files downloaded from server
	 */
	public File[] get(File outputFolder, String filePathAndNameRegex) {

		List<File> ret = new ArrayList<File>();

		String dir = ".";
		String filenameRegex = filePathAndNameRegex;
		int lastIndexOfSlash = filePathAndNameRegex.lastIndexOf('/');
		if (lastIndexOfSlash != -1) {
			dir = filePathAndNameRegex.substring(0, lastIndexOfSlash);
			filenameRegex = filePathAndNameRegex.substring(lastIndexOfSlash + 1);
		}

		LOG.info("[GET] Connecting to ftp server: " + host + "[" + user + "/" + pass + "]... dir [" + dir + "] filename [" + filenameRegex
				+ "]");
		FTPSClient ftpClient = new FTPSClient(false);

		try {
			ftpClient.connect(host, port);

			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new RuntimeException("[GET] Cannot connect to ftp server");
			}

			if (!ftpClient.login(user, pass)) {
				throw new RuntimeException("[GET] Cannot login in ftp server");
			}

			LOG.info("[GET] Connected to ftp server");
			ftpClient.execPBSZ(0);
			ftpClient.execPROT("P");
			ftpClient.enterLocalPassiveMode();

			FTPFile[] listNames = ftpClient.listFiles(dir);
			if (listNames == null || listNames.length <= 0) {
				LOG.info("[GET] No file found on server. Will check again later");
				return new File[0];
			}

			ftpClient.cwd(dir);

			LOG.info("[GET] Files found on server: '" + Arrays.toString(listNames) + "'");
			for (FTPFile file : listNames) {
				if (!file.isFile() || !file.getName().matches(filenameRegex)) {
					LOG.info("[GET] Found non-matching file on server[" + file + "]");
					continue;
				}
				LOG.info("[GET] Found file on server[" + file + "]. Downloading it...");

				File download = new File(outputFolder, file.getName());
				FileOutputStream fileOutputStream = new FileOutputStream(download);
				ftpClient.retrieveFile(file.getName(), fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
				LOG.info("[GET] File on server [" + file + "] successfully downloaded to [" + download + "]");
				// ftpClient.deleteFile(fileName);
				LOG.info("[GET] Removing file from server[" + file + "] successful");
				ret.add(download);
			}
			Collections.sort(ret);
			return ret.toArray(new File[ret.size()]);
		} catch (Exception ex) {
			LOG.error("[GET] Error: " + ex.getMessage(), ex);
			throw new RuntimeException("[GET] Problem putting file to ftp server", ex);
		} finally {
			try {
				ftpClient.logout();
				LOG.info("[GET] Disconnected");
			} catch (IOException e) {
				throw new RuntimeException("[GET] Problem disconnecting from ftp server");
			}
		}
	}
}
