package org.andresoviedo.util.bean;


import java.io.Serializable;

public class ImporteDivisaDeTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6163795193447295359L;
	
	private Number importe;
	private String divisa;
	
	public ImporteDivisaDeTest() {}
	
	public ImporteDivisaDeTest(Number importe, String divisa) {
		this.importe = importe;
		this.divisa = divisa;
	}

	public Number getImporte() {
		return this.importe;
	}
	
	public String getDivisa() {
		return this.divisa;
	}
	
	public void setImporte(Number importe) {
		this.importe = importe;
	}
	
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
}
