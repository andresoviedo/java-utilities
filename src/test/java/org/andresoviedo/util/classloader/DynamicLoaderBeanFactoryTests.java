package org.andresoviedo.util.classloader;

import java.util.HashMap;
import java.util.Map;

import org.andresoviedo.util.encoding.Base64;
import org.andresoviedo.util.spring.core.DynamicLoaderBeanFactory;
import org.junit.Assert;
import org.junit.Test;

public class DynamicLoaderBeanFactoryTests {

	// static class SomeRunnable implements Runnable, Serializable {
	//
	// private static final long serialVersionUID = -302055673749076128L;
	//
	// @Override
	// public void run() {
	// System.out.println("Hola. Soy la versión original de la clase!");
	// }
	//
	// }

	// static class SomeRunnable2 implements Runnable, Serializable {
	//
	// public String attrib1 = "hola";
	//
	// public String attrib2 = "soy clase desconocida";
	//
	// @Override
	// public void run() {
	// System.out.println(attrib1 + ". " + attrib2 + "!");
	// }
	//
	// }
	//
	// public static void main(String[] args) throws IOException {
	// final ByteArrayOutputStream out = new ByteArrayOutputStream();
	// final ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
	// objectOutputStream.writeObject(SomeRunnable.class);
	// objectOutputStream.close();
	// System.out.println(Base64.encodeToString(out.toByteArray(), false));
	//
	// final String string =
	// "C:\\Users\\Andres Oviedo\\Desktop\\TEMP\\github.com\\java-utilities\\target\\classes\\org\\andresoviedo\\util\\classloader\\SomeRunnable.class";
	// final File file = new File(string);
	// byte[] fileBytes = new byte[(int) file.length()];
	// IOUtils.readFully(new FileInputStream(file), fileBytes);
	//
	// System.out.println(Base64.encodeToString(fileBytes, false));
	// }

	final String claseBinariaOriginal = "yv66vgAAADIAKAoABgAYCQAZABoIABsKABwAHQcAHgcAHwcAIAcAIQEAEHNlcmlhbFZlcnNpb25VSUQBAAFKAQANQ29uc3RhbnRWYWx1ZQUAAAAAAAAAAQEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAwTG9yZy9hbmRyZXNvdmllZG8vdXRpbC9jbGFzc2xvYWRlci9Tb21lUnVubmFibGU7AQADcnVuAQAKU291cmNlRmlsZQEAEVNvbWVSdW5uYWJsZS5qYXZhDAAOAA8HACIMACMAJAEAHEhvbGEuIFNveSBjbGFzZSBkZXNjb25vY2lkYSEHACUMACYAJwEALm9yZy9hbmRyZXNvdmllZG8vdXRpbC9jbGFzc2xvYWRlci9Tb21lUnVubmFibGUBABBqYXZhL2xhbmcvT2JqZWN0AQASamF2YS9sYW5nL1J1bm5hYmxlAQAUamF2YS9pby9TZXJpYWxpemFibGUBABBqYXZhL2xhbmcvU3lzdGVtAQADb3V0AQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYAIAAFAAYAAgAHAAgAAQAaAAkACgABAAsAAAACAAwAAgAAAA4ADwABABAAAAAvAAEAAQAAAAUqtwABsQAAAAIAEQAAAAYAAQAAAAUAEgAAAAwAAQAAAAUAEwAUAAAAAQAVAA8AAQAQAAAANwACAAEAAAAJsgACEgO2AASxAAAAAgARAAAACgACAAAADgAIAA8AEgAAAAwAAQAAAAkAEwAUAAAAAQAWAAAAAgAX";
	final String miClaseBinaria = "yv66vgAAADIAKAcAAgEALm9yZy9hbmRyZXNvdmllZG8vdXRpbC9jbGFzc2xvYWRlci9Tb21lUnVubmFibGUHAAQBABBqYXZhL2xhbmcvT2JqZWN0BwAGAQASamF2YS9sYW5nL1J1bm5hYmxlBwAIAQAUamF2YS9pby9TZXJpYWxpemFibGUBABBzZXJpYWxWZXJzaW9uVUlEAQABSgEADUNvbnN0YW50VmFsdWUFAAAAAAAAAAEBAAY8aW5pdD4BAAMoKVYBAARDb2RlCgADABIMAA4ADwEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBADBMb3JnL2FuZHJlc292aWVkby91dGlsL2NsYXNzbG9hZGVyL1NvbWVSdW5uYWJsZTsBAANydW4JABkAGwcAGgEAEGphdmEvbGFuZy9TeXN0ZW0MABwAHQEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwgAHwEAHkhvbGEuIFNveSBjbGFzZSBkZXNjb25vY2lkYSAyIQoAIQAjBwAiAQATamF2YS9pby9QcmludFN0cmVhbQwAJAAlAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAClNvdXJjZUZpbGUBABFTb21lUnVubmFibGUuamF2YQAgAAEAAwACAAUABwABABoACQAKAAEACwAAAAIADAACAAAADgAPAAEAEAAAAC8AAQABAAAABSq3ABGxAAAAAgATAAAABgABAAAABQAUAAAADAABAAAABQAVABYAAAABABcADwABABAAAAA3AAIAAQAAAAmyABgSHrYAILEAAAACABMAAAAKAAIAAAAOAAgADwAUAAAADAABAAAACQAVABYAAAABACYAAAACACc=";
	final String miClaseBinaria2 = "yv66vgAAADIAKAcAAgEALm9yZy9hbmRyZXNvdmllZG8vdXRpbC9jbGFzc2xvYWRlci9Tb21lUnVubmFibGUHAAQBABBqYXZhL2xhbmcvT2JqZWN0BwAGAQASamF2YS9sYW5nL1J1bm5hYmxlBwAIAQAUamF2YS9pby9TZXJpYWxpemFibGUBABBzZXJpYWxWZXJzaW9uVUlEAQABSgEADUNvbnN0YW50VmFsdWUFAAAAAAAAAAEBAAY8aW5pdD4BAAMoKVYBAARDb2RlCgADABIMAA4ADwEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBADBMb3JnL2FuZHJlc292aWVkby91dGlsL2NsYXNzbG9hZGVyL1NvbWVSdW5uYWJsZTsBAANydW4JABkAGwcAGgEAEGphdmEvbGFuZy9TeXN0ZW0MABwAHQEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwgAHwEAJEhvbGEuIFNveSBjbGFzZSBkZXNjb25vY2lkYSA9PT0+IDMgIQoAIQAjBwAiAQATamF2YS9pby9QcmludFN0cmVhbQwAJAAlAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAClNvdXJjZUZpbGUBABFTb21lUnVubmFibGUuamF2YQAgAAEAAwACAAUABwABABoACQAKAAEACwAAAAIADAACAAAADgAPAAEAEAAAAC8AAQABAAAABSq3ABGxAAAAAgATAAAABgABAAAABQAUAAAADAABAAAABQAVABYAAAABABcADwABABAAAAA3AAIAAQAAAAmyABgSHrYAILEAAAACABMAAAAKAAIAAAAOAAgADwAUAAAADAABAAAACQAVABYAAAABACYAAAACACc=";

	/**
	 * Valida que nuestra BeanFactory custom hace el unmarshalling de las clases binarias.
	 */
	@Test
	public void testDynamicLoad() {
		DynamicLoaderBeanFactory dlfb = new DynamicLoaderBeanFactory();
		Map<String, byte[]> source = new HashMap<String, byte[]>();
		source.put("org.andresoviedo.util.classloader.SomeRunnable", Base64.decodeFast(claseBinariaOriginal));
		dlfb.setSource(source);
		dlfb.getClassNames().put("org.andresoviedo.util.classloader.SomeRunnable", "org.andresoviedo.util.classloader.SomeRunnable");
		Runnable bean = (Runnable) dlfb.getBean("org.andresoviedo.util.classloader.SomeRunnable");
		bean.run();
		Assert.assertNotNull(bean);

		source.put("org.andresoviedo.util.classloader.SomeRunnable", Base64.decodeFast(miClaseBinaria));
		dlfb.setSource(source);
		bean = (Runnable) dlfb.getBean("org.andresoviedo.util.classloader.SomeRunnable");
		bean.run();
		Assert.assertNotNull(bean);

		source.put("org.andresoviedo.util.classloader.SomeRunnable", Base64.decodeFast(miClaseBinaria2));
		dlfb.setSource(source);
		bean = (Runnable) dlfb.getBean("org.andresoviedo.util.classloader.SomeRunnable");
		bean.run();
		Assert.assertNotNull(bean);
	}
}
