package org.andresoviedo.util.file;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class LazyFileOutputStreamTest {

	@Test
	public void testFileNotCreated() throws IOException {
		LazyFileOutputStream lfos = new LazyFileOutputStream(new File(this.getClass().getName() + ".1"), false);
		lfos.flush();
		lfos.close();
		Assert.assertFalse(lfos.getFile().exists());
	}

	@Test
	public void testFileInitialized() throws IOException {
		File f = new File(this.getClass().getName() + ".2");
		LazyFileOutputStream lfos = new LazyFileOutputStream(f, false);
		Assert.assertFalse(f.exists());

		lfos.write(1);
		lfos.write(2);
		lfos.flush();
		lfos.close();
		Assert.assertTrue(lfos.getFile().exists());

		// Clean workspace
		lfos.getFile().delete();
	}

}
