package org.andresoviedo.util.serialization.api2.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.andresoviedo.util.serialization.api2.StringField;

/**
 * This class represents a confirmation receipt from service for a previously sent request.
 * 
 * @author aoviedo
 *
 */
public final class EntityExample2 implements Serializable {

	private static final long serialVersionUID = -7961850400529285101L;

	@StringField(offset = 0, format = "%04d")
	private static final int registryType = 8081;

	@StringField(offset = 4, format = "yyMMdd")
	private Date fileCreationDate;

	@StringField(offset = 10, format = "HHmm")
	private Date fileCreationTime;

	@StringField(offset = 14, format = "%06d")
	private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

	@StringField(offset = 20, format = "%12s")
	private static final String reservedField1 = "";

	@StringField(offset = 32, format = "%40d")
	private String issuerEntityCode;

	@StringField(offset = 72, format = "%4d")
	private int entityNBRE;

	@StringField(offset = 76, format = "%4s")
	private static final String reservedField2 = "";

	@StringField(offset = 80, format = "%10d")
	private int queueSize;

	@StringField(offset = 90, format = "%017d")
	private static final int reservedField3 = 0;

	@StringField(offset = 107, format = "%10d")
	private int numberOfRequests;

	@StringField(offset = 117, format = "%017d")
	private static final int reservedField4 = 0;

	@StringField(offset = 134, format = "%02d")
	private int responseCode;

	@StringField(offset = 136, format = "%02d")
	private static final int reservedField5 = 0;

	@StringField(offset = 138, format = "%2d")
	private static final int reservedField6 = 80;

	@StringField(offset = 140, format = "%2d")
	private static final int reservedField7 = 1;

	@StringField(offset = 142, format = "%6s")
	private String freeString1;

	@StringField(offset = 148, format = "%8s")
	private static final String emptyField = "";

	public EntityExample2 setIssuerEntityCode(String issuerEntityCode) {
		this.issuerEntityCode = issuerEntityCode;
		return this;
	}

	public EntityExample2 setEntityNRBE(int entityNBRE) {
		this.entityNBRE = entityNBRE;
		return this;
	}

	public EntityExample2 setQueueSize(int queueSize) {
		this.queueSize = queueSize;
		return this;
	}

	public EntityExample2 setNumberOfRequests(int numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
		return this;
	}

	public EntityExample2 setResponseCode(int responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	public EntityExample2 setFreeString1(String freeString1) {
		this.freeString1 = freeString1;
		return this;
	}

	public EntityExample2 setFileCreationDate(Date fileCreationDate) {
		this.fileCreationDate = fileCreationDate;
		return this;
	}

	public EntityExample2 setFileCreationTime(Date fileCreationTime) {
		this.fileCreationTime = fileCreationTime;
		return this;
	}

	/**
	 * @return the sum of the date and time.
	 */
	private Date getRealDate() {
		if (fileCreationDate == null || fileCreationTime == null) {
			throw new RuntimeException("Either date of time is null");
		}
		Calendar time = Calendar.getInstance();
		time.setTime(fileCreationTime);

		Calendar date = Calendar.getInstance();
		date.setTime(fileCreationDate);
		date.set(Calendar.HOUR, time.get(Calendar.HOUR));
		date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		return date.getTime();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entityNBRE;
		result = prime * result + ((fileCreationDate == null) ? 0 : fileCreationDate.hashCode());
		result = prime * result + ((fileCreationTime == null) ? 0 : fileCreationTime.hashCode());
		result = prime * result + ((freeString1 == null) ? 0 : freeString1.hashCode());
		result = prime * result + ((issuerEntityCode == null) ? 0 : issuerEntityCode.hashCode());
		result = prime * result + numberOfRequests;
		result = prime * result + queueSize;
		result = prime * result + responseCode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityExample2 other = (EntityExample2) obj;
		if (entityNBRE != other.entityNBRE)
			return false;
		if (fileCreationDate == null) {
			if (other.fileCreationDate != null)
				return false;
		} else if (!fileCreationDate.equals(other.fileCreationDate))
			return false;
		if (fileCreationTime == null) {
			if (other.fileCreationTime != null)
				return false;
		} else if (!fileCreationTime.equals(other.fileCreationTime))
			return false;
		if (freeString1 == null) {
			if (other.freeString1 != null)
				return false;
		} else if (!freeString1.equals(other.freeString1))
			return false;
		if (issuerEntityCode == null) {
			if (other.issuerEntityCode != null)
				return false;
		} else if (!issuerEntityCode.equals(other.issuerEntityCode))
			return false;
		if (numberOfRequests != other.numberOfRequests)
			return false;
		if (queueSize != other.queueSize)
			return false;
		if (responseCode != other.responseCode)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EntityExample2 [fileDate=" + getRealDate() + ", issuerEntityCode=" + issuerEntityCode
				+ ", entityNBRE=" + entityNBRE + ", queueSize=" + queueSize + ", numberOfRequests=" + numberOfRequests
				+ ", responseCode=" + responseCode + ", freeString1=" + freeString1 + "]";
	}

}
