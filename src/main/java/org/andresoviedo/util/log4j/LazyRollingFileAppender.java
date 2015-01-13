package org.andresoviedo.util.log4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.andresoviedo.util.file.LazyFileOutputStream;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;

public class LazyRollingFileAppender extends RollingFileAppender {

	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
		LogLog.debug("setFile called: " + fileName + ", " + append);

		// It does not make sense to have immediate flush and bufferedIO.
		if (bufferedIO) {
			setImmediateFlush(false);
		}

		reset();
		OutputStream ostream = null;

		//
		// attempt to create file
		//
		ostream = new LazyFileOutputStream(fileName, append);

		Writer fw = createWriter(ostream);
		if (bufferedIO) {
			fw = new BufferedWriter(fw, bufferSize);
		}
		this.setQWForFiles(fw);
		this.fileName = fileName;
		this.fileAppend = append;
		this.bufferedIO = bufferedIO;
		this.bufferSize = bufferSize;
		writeHeader();
		LogLog.debug("setFile ended");

		if (append) {
			File f = new File(fileName);
			((CountingQuietWriter) qw).setCount(f.length());
		}
	}
}
