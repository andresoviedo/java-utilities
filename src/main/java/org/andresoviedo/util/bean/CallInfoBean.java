package org.andresoviedo.util.bean;

public class CallInfoBean {
	String portName;
	String telephoneNr;
	int waitSecs;

	public CallInfoBean() {
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getTelephoneNr() {
		return telephoneNr;
	}

	public void setTelephoneNr(String telephoneNr) {
		this.telephoneNr = telephoneNr;
	}

	public int getWaitSecs() {
		return waitSecs;
	}

	public void setWaitSecs(int waitSecs) {
		this.waitSecs = waitSecs;
	}

}
