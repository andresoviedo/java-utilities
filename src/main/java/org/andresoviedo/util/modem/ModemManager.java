package org.andresoviedo.util.modem;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.andresoviedo.util.bean.CallInfoBean;

public class ModemManager implements Serializable {

	private static final long serialVersionUID = 8494357761572317271L;

	protected static void send(PrintStream pis, DataInputStream din, String s) throws IOException, InterruptedException {
		System.out.println(">>> " + s);
		pis.print(s + "\r\n");

		// Expect the modem to echo the command.
		if (!expect_impl(din, s, 30)) {
			System.out.println("WARNING: Modem did not echo command.");
		}

		// The modem sends an extra blank line by way of a prompt.
		// Here we read and discard it.
		@SuppressWarnings("deprecation")
		String junk = din.readLine();
		if (junk != null && junk.length() != 0) {
			System.out.print("Warning unexpected response: ");
			System.out.println(junk);
		}
	}

	protected static boolean expect(DataInputStream din, String exp, int attempts) throws InterruptedException, IOException {
		System.out.println("Expecting " + exp + "...");
		return expect_impl(din, exp, attempts);
	}

	@SuppressWarnings("deprecation")
	protected static boolean expect_impl(DataInputStream din, String exp, int attempts) throws InterruptedException, IOException {
		String response = din.readLine();
		if (response == null && attempts > 0) {
			Thread.sleep(1000);
			return expect_impl(din, exp, attempts - 1);
		} else {
			System.out.println("<<< " + response);
			return response != null && response.indexOf(exp) >= 0;
		}
	}

	/**
	 * Read a line, saving it in "response".
	 * 
	 * @return true if the expected String is contained in the response, false if not.
	 * @throws InterruptedException
	 */
	public static void call(CallInfoBean callInfo) {

		Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();

		@SuppressWarnings("unused")
		DataInputStream uin = new DataInputStream(System.in);

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(callInfo.getPortName())) {
					SerialPort serialPort = null;
					try {
						serialPort = (SerialPort) portId.open(ModemManager.class.getName(), 3000);
					} catch (PortInUseException e) {
						e.printStackTrace();
					}
					PrintStream pis = null;
					DataInputStream din = null;
					try {
						OutputStream outputStream = serialPort.getOutputStream();
						pis = new PrintStream(outputStream);
						InputStream in = serialPort.getInputStream();
						din = new DataInputStream(in);
					} catch (IOException e) {
						System.out.println(e);
					}
					try {
						serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					} catch (UnsupportedCommOperationException e) {
						e.printStackTrace();
						serialPort.close();
					}

					try {
						serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
					} catch (UnsupportedCommOperationException e) {
						e.printStackTrace();
					}

					try {
						send(pis, din, "ATE1");// ECHO

						expect(din, "OK", 5);

						send(pis, din, "ATZ");

						expect(din, "OK", 5);

						Thread t = new Thread(new HangTimeout(pis, din, callInfo.getWaitSecs() * 1000));
						t.setDaemon(true);
						t.setName("espera");
						t.start();

						send(pis, din, "ATDT" + callInfo.getTelephoneNr());

						send(pis, din, "ATZ");

						expect(din, "OK", 5);

					} catch (Exception exce) {
						System.out.println(exce);
					} finally {
						if (serialPort != null) {
							serialPort.close();
						}
						if (din != null) {
							try {
								din.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
						if (pis != null) {
							pis.close();
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		CallInfoBean callInfo = new CallInfoBean();
		callInfo.setPortName("COM3");
		callInfo.setTelephoneNr("0617111110");
		callInfo.setWaitSecs(10);
		ModemManager.call(callInfo);
		System.out.println("bye");
	}
}

class HangTimeout implements Runnable {

	PrintStream pis = null;
	DataInputStream din = null;
	long timeout;

	HangTimeout(PrintStream pis, DataInputStream din, long timeout) {
		this.pis = pis;
		this.din = din;
		this.timeout = timeout;
	}

	public void run() {
		try {
			Thread.sleep(timeout);
			ModemManager.send(pis, din, "ATH0");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}