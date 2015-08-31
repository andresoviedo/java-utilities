package org.andresoviedo.util.serialization.api2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.andresoviedo.util.serialization.api2.StringField;

/**
 * This class represents a request for service
 * 
 * @author andresoviedo
 *
 */
public final class EntityExample1 implements Serializable {

	private static final long serialVersionUID = 9176718585676139163L;

	@StringField(order = 0)
	private final TransmissionHeader transmissionHeader = new TransmissionHeader();

	@StringField(order = 1)
	private final ReportingEntity reportingEntity = new ReportingEntity();

	@StringField(order = 2)
	private final RequestingEntity requestingEntity = new RequestingEntity();

	@StringField(order = 3)
	private final List<InformationRequestRecord> requestRecords = new ArrayList<InformationRequestRecord>();

	@StringField(order = 4)
	private final RequestingEntityTotal requestingEntityTotal = new RequestingEntityTotal();

	@StringField(order = 5)
	private final ReportingEntityTotal reportingEntityTotal = new ReportingEntityTotal();

	public TransmissionHeader getTransmissionHeader() {
		return transmissionHeader;
	}

	public ReportingEntity getReportingEntity() {
		return reportingEntity;
	}

	public RequestingEntity getRequestingEntity() {
		return requestingEntity;
	}

	public List<InformationRequestRecord> getRequestRecords() {
		return requestRecords;
	}

	public InformationRequestRecord addRequestRecord() {
		InformationRequestRecord ret = new InformationRequestRecord();
		requestRecords.add(ret);
		return ret;
	}

	public int getRequestsSize() {
		return requestRecords.size();
	}

	public ReportingEntityTotal getReportingEntityTotal() {
		return reportingEntityTotal;
	}

	public RequestingEntityTotal getRequestingEntityTotal() {
		return requestingEntityTotal;
	}

	/**
	 * Registro de cabecera de transmisión de petición.
	 * 
	 * @author andresoviedo
	 *
	 */
	public static final class TransmissionHeader implements Serializable {

		private static final long serialVersionUID = 2762043797610237362L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 1;

		@StringField(offset = 2, format = "%02d")
		private static final int fileType = 0;

		@StringField(offset = 4, format = "%02d")
		private static final int unknown1 = 1;

		@StringField(offset = 6, format = "%02d")
		private static final int requestType = 80;

		@StringField(offset = 8, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 10, format = "yyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 16, format = "HHmm")
		private Date fileCreationTime;

		@StringField(offset = 20, format = "%06d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 26, format = "%6s")
		private static final String reservedField1 = "";

		@StringField(offset = 32, format = "%268s")
		private static final String reservedField2 = "";

		public int getRequestNrForDay() {
			return requestNrForDay;
		}

		public TransmissionHeader setRequestNrForDay(int requestNrForDay) {
			this.requestNrForDay = requestNrForDay;
			return this;
		}

		public TransmissionHeader setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}

		public TransmissionHeader setFileCreationTime(Date fileCreationTime) {
			this.fileCreationTime = fileCreationTime;
			return this;
		}
	}

	/**
	 * This class represents the financial entity querying for report
	 * 
	 * @author andresoviedo
	 *
	 */
	public static final class ReportingEntity implements Serializable {

		private static final long serialVersionUID = 1L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 1;

		@StringField(offset = 2)
		private static final String fileType = "VF";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date requestSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%274s")
		private static final String emptyField = "";

		public ReportingEntity setRequestSendingDate(Date requestSendingDate) {
			this.requestSendingDate = requestSendingDate;
			return this;
		}

		public ReportingEntity setRequestNrForDay(int requestNrForDay) {
			this.requestNrForDay = requestNrForDay;
			return this;
		}

		public ReportingEntity setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}
	}

	/**
	 * This class represents the financial entity requesting for report
	 * 
	 * @author andresoviedo
	 *
	 */
	public static final class RequestingEntity implements Serializable {

		private static final long serialVersionUID = -4394881873352757619L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 3;

		@StringField(offset = 2)
		private static final String fileType = "VF";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date requestSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%274s")
		private static final String emptyField = "";

		public RequestingEntity setRequestSendingDate(Date requestSendingDate) {
			this.requestSendingDate = requestSendingDate;
			return this;
		}

		public RequestingEntity setRequestNrForDay(int requestNrForDay) {
			this.requestNrForDay = requestNrForDay;
			return this;
		}

		public RequestingEntity setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}
	}

	/**
	 * This class represents a single request, that is a request for 1 customer
	 * 
	 * @author andresoviedo
	 *
	 */
	public static final class InformationRequestRecord implements Serializable {

		private static final long serialVersionUID = -6307505232171816573L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 5;

		@StringField(offset = 2)
		private static final String fileType = "VF";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "%04d")
		private static final int financialOfficeCode = EntityConstants.BANK_OFFICE_CODE;

		@StringField(offset = 12, format = "%'0'10s")
		private String customerNif;

		@StringField(offset = 22, format = "%-20s")
		private String customerSurname1;

		@StringField(offset = 42, format = "%-20s")
		private String customerSurname2;

		@StringField(offset = 62, format = "%-20s")
		private String customerName;

		@StringField(offset = 82, format = "%-16s")
		private String financialRequestId;

		@StringField(offset = 98, format = "%202s")
		private static final String emptyField = "";

		public InformationRequestRecord setCustomerNif(String customerNif) {
			this.customerNif = customerNif;
			return this;
		}

		public InformationRequestRecord setCustomerSurname1(String surname1) {
			this.customerSurname1 = surname1;
			return this;
		}

		public InformationRequestRecord setCustomerSurname2(String surname2) {
			this.customerSurname2 = surname2;
			return this;
		}

		public InformationRequestRecord setCustomerName(String customerName) {
			this.customerName = customerName;
			return this;
		}

		public InformationRequestRecord setFinancialRequestId(String financialRequestId) {
			this.financialRequestId = financialRequestId;
			return this;
		}

	}

	/**
	 * This class represents one of the summary records of the request with respect to the requesting entity.
	 * 
	 * @author andresoviedo
	 *
	 */
	public static final class RequestingEntityTotal implements Serializable {

		private static final long serialVersionUID = 9020731921070185750L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 7;

		@StringField(offset = 2)
		private static final String fileType = "VF";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date requestSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%06d")
		private int numberOfRecords;

		@StringField(offset = 32, format = "%06d")
		private int numberOfRequests;

		@StringField(offset = 38, format = "%262s")
		private static final String emptyField = "";

		public RequestingEntityTotal setRequestSendingDate(Date requestSendingDate) {
			this.requestSendingDate = requestSendingDate;
			return this;
		}

		public RequestingEntityTotal setRequestNrForDay(int requestNrForDay) {
			this.requestNrForDay = requestNrForDay;
			return this;
		}

		public RequestingEntityTotal setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}

		public RequestingEntityTotal setNumberOfRecords(int numberOfRecords) {
			this.numberOfRecords = numberOfRecords;
			return this;
		}

		public RequestingEntityTotal setNumberOfRequests(int numberOfRequests) {
			this.numberOfRequests = numberOfRequests;
			return this;
		}
	}

	/**
	 * This class represents one of the summary records of the request with respect to the reporting entity,
	 * 
	 * @author andresoviedo
	 *
	 */
	public static final class ReportingEntityTotal implements Serializable {

		private static final long serialVersionUID = 9075159741597662963L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 9;

		@StringField(offset = 2)
		private static final String fileType = "VF";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date requestSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%06d")
		private int numberOfRecords;

		@StringField(offset = 32, format = "%06d")
		private int numberOfRequests;

		@StringField(offset = 38, format = "%262s")
		private static final String emptyField = "";

		public ReportingEntityTotal setRequestSendingDate(Date requestSendingDate) {
			this.requestSendingDate = requestSendingDate;
			return this;
		}

		public ReportingEntityTotal setRequestNrForDay(int requestNrForDay) {
			this.requestNrForDay = requestNrForDay;
			return this;
		}

		public ReportingEntityTotal setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}

		public ReportingEntityTotal setNumberOfRecords(int numberOfRecords) {
			this.numberOfRecords = numberOfRecords;
			return this;
		}

		public ReportingEntityTotal setNumberOfRequests(int numberOfRequests) {
			this.numberOfRequests = numberOfRequests;
			return this;
		}
	}

}
