package org.andresoviedo.util.serialization.api2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.andresoviedo.util.serialization.api2.StringField;

/**
 * This class represents a response from service
 * 
 * @author aoviedo
 *
 */
public final class EntityExample3 implements Serializable {

	private static final long serialVersionUID = -505342993354759418L;

	@StringField(order = 0)
	private final TransmissionHeader transmissionHeader = new TransmissionHeader();

	@StringField(order = 1)
	private final ReportingEntity reportingEntity = new ReportingEntity();

	@StringField(order = 2)
	private final RequestingEntity requestingEntity = new RequestingEntity();

	@StringField
	private final List<InformationResponseRecord> responseRecords = new ArrayList<InformationResponseRecord>();

	@StringField
	private final List<ComplementaryInformationResponseRecord> complementaryResponseRecords = new ArrayList<ComplementaryInformationResponseRecord>();

	@StringField(order = 5)
	private final RequestingEntityTotal requestingEntityTotal = new RequestingEntityTotal();

	@StringField(order = 6)
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

	public List<InformationResponseRecord> getResponseRecords() {
		return Collections.unmodifiableList(responseRecords);
	}

	public List<ComplementaryInformationResponseRecord> getComplementaryResponseRecords() {
		return Collections.unmodifiableList(complementaryResponseRecords);
	}

	public ReportingEntityTotal getReportingEntityTotal() {
		return reportingEntityTotal;
	}

	public RequestingEntityTotal getRequestingEntityTotal() {
		return requestingEntityTotal;
	}

	public InformationResponseRecord addResponseRecord() {
		InformationResponseRecord ret = new InformationResponseRecord();
		responseRecords.add(ret);
		return ret;
	}

	public ComplementaryInformationResponseRecord addComplementaryResponseRecord() {
		ComplementaryInformationResponseRecord ret = new ComplementaryInformationResponseRecord();
		complementaryResponseRecords.add(ret);
		return ret;
	}

	@Override
	public String toString() {
		return "EntityExample3 [transmissionHeader=" + transmissionHeader + ", reportingEntity=" + reportingEntity
				+ ", requestingEntity=" + requestingEntity + ", responseRecords=" + responseRecords
				+ ", complementaryResponseRecords=" + complementaryResponseRecords + ", requestingEntityTotal="
				+ requestingEntityTotal + ", reportingEntityTotal=" + reportingEntityTotal + "]";
	}

	/**
	 * Registro de cabecera de transmisión de respuesta.
	 * 
	 * @author aoviedo
	 *
	 */
	public static final class TransmissionHeader implements Serializable {

		private static final long serialVersionUID = 3898570706041318082L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 1;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%02d")
		private static final int unknown1 = 1;

		@StringField(offset = 6, format = "%02d")
		private static final int responseType = 81;

		@StringField(offset = 8, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 10, format = "yyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 16, format = "HHmm")
		private Date fileCreationTime;

		@StringField(offset = 20, format = "%06d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 26, format = "%6s")
		private String reservedField;

		@StringField(offset = 32, format = "%268s")
		private static final String reservedField2 = "";

		public int getRequestNrForDay() {
			return requestNrForDay;
		}

		public Date getFileCreationDate() {
			return (Date) fileCreationDate.clone();
		}

		public Date getFileCreationTime() {
			return (Date) fileCreationTime.clone();
		}

		public int getFinancialEntityCode() {
			return financialEntityCode;
		}

		public String getReservedField() {
			return reservedField;
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

		public TransmissionHeader setReservedField(String reservedField) {
			this.reservedField = reservedField;
			return this;
		}

		@Override
		public String toString() {
			return "TransmissionHeader [requestNrForDay=" + requestNrForDay + ", fileCreationDate=" + fileCreationDate
					+ ", fileCreationTime=" + fileCreationTime + ", financialEntityCode=" + financialEntityCode
					+ ", reservedField=" + reservedField + "]";
		}

	}

	/**
	 * This class represents the financial entity querying for report
	 * 
	 * @author aoviedo
	 *
	 */
	public static final class ReportingEntity implements Serializable {

		private static final long serialVersionUID = 1363227327362866908L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 1;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date responseSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int responseNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%274s")
		private static final String emptyField = "";

		public int getFinancialEntityCode() {
			return financialEntityCode;
		}

		public Date getRequestSendingDate() {
			return responseSendingDate;
		}

		public int getRequestNrForDay() {
			return responseNrForDay;
		}

		public Date getFileCreationDate() {
			return fileCreationDate;
		}

		public ReportingEntity setResponseSendingDate(Date responseSendingDate) {
			this.responseSendingDate = responseSendingDate;
			return this;
		}

		public ReportingEntity setResponseNrForDay(int responseNrForDay) {
			this.responseNrForDay = responseNrForDay;
			return this;
		}

		public ReportingEntity setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}

		@Override
		public String toString() {
			return "ReportingEntity [financialEntityCode=" + financialEntityCode + ", responseSendingDate="
					+ responseSendingDate + ", responseNrForDay=" + responseNrForDay + ", fileCreationDate="
					+ fileCreationDate + "]";
		}

	}

	/**
	 * This class represents the financial entity requesting for report
	 * 
	 * @author aoviedo
	 *
	 */
	public static final class RequestingEntity implements Serializable {

		private static final long serialVersionUID = -3059631451321331538L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 3;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%04d")
		private static final int financialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date responsetSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int responseNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%04d")
		private static final int reportingEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 34, format = "%270s")
		private static final String emptyField = "";

		public int getReportingEntityCode() {
			return reportingEntityCode;
		}

		public int getFinancialEntityCode() {
			return financialEntityCode;
		}

		public Date getRequestSendingDate() {
			return responsetSendingDate;
		}

		public int getRequestNrForDay() {
			return responseNrForDay;
		}

		public Date getFileCreationDate() {
			return fileCreationDate;
		}

		public RequestingEntity setResponseSendingDate(Date responseSendingDate) {
			this.responsetSendingDate = responseSendingDate;
			return this;
		}

		public RequestingEntity setResponseNrForDay(int responseNrForDay) {
			this.responseNrForDay = responseNrForDay;
			return this;
		}

		public RequestingEntity setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}

		@Override
		public String toString() {
			return "RequestingEntity [financialEntityCode=" + financialEntityCode + ", responsetSendingDate="
					+ responsetSendingDate + ", responseNrForDay=" + responseNrForDay + ", fileCreationDate="
					+ fileCreationDate + ", reportingEntityCode=" + reportingEntityCode + "]";
		}

	}

	/**
	 * This class represents a single request, that is a request for 1 customer
	 * 
	 * @author aoviedo
	 *
	 */
	@StringField(id = "05")
	public static final class InformationResponseRecord implements Serializable {

		private static final long serialVersionUID = -5730643367170159864L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 5;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%04d")
		private static final int requestingFinancialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "%04d")
		private static final int requestingFinancialOfficeCode = EntityConstants.BANK_OFFICE_CODE;

		@StringField(offset = 12, format = "%'0'10s")
		private String customerNif;

		@StringField(offset = 22, format = "%-20s")
		private String customerSurname1;

		@StringField(offset = 42, format = "%-20s")
		private String customerSurname2;

		@StringField(offset = 62, format = "%-20s")
		private String customerName;

		@StringField(offset = 82, format = "%-16s")
		private String responseId;

		@StringField(offset = 98, format = "%2s")
		private String customerStatus;

		@StringField(offset = 100, format = "%2s")
		private String contractType;

		@StringField(offset = 102, format = "%05d")
		private int customerJobType;

		@StringField(offset = 107, format = "%2d")
		private int customerContributionGroup;

		@StringField(offset = 109, format = "%10s")
		private String employerCIF;

		@StringField(offset = 119, format = "%20s")
		private String employerSurname1;

		@StringField(offset = 139, format = "%20s")
		private String employerSurname2;

		@StringField(offset = 159, format = "%20s")
		private String employerName;

		@StringField(offset = 179, format = "%55s")
		private String companyName;

		@StringField(offset = 234, format = "%05d")
		private int companyJobType;

		@StringField(offset = 239, format = "yyyyMMdd")
		private Date infoExtractionDate;

		@StringField(offset = 247, format = "%53s")
		private static final String emptyField = "";

		public String getCustomerStatus() {
			return customerStatus;
		}

		public String getContractType() {
			return contractType;
		}

		public int getCustomerJobType() {
			return customerJobType;
		}

		public int getCustomerContributionGroup() {
			return customerContributionGroup;
		}

		public String getEmployerCIF() {
			return employerCIF;
		}

		public String getEmployerSurname1() {
			return employerSurname1;
		}

		public String getEmployerSurname2() {
			return employerSurname2;
		}

		public String getEmployerName() {
			return employerName;
		}

		public String getCompanyName() {
			return companyName;
		}

		public int getCompanyJobType() {
			return companyJobType;
		}

		public Date getInfoExtractionDate() {
			return infoExtractionDate;
		}

		public int getFinancialEntityCode() {
			return requestingFinancialEntityCode;
		}

		public int getFinancialOfficeCode() {
			return requestingFinancialOfficeCode;
		}

		public String getCustomerNif() {
			return customerNif;
		}

		public String getCustomerSurname1() {
			return customerSurname1;
		}

		public String getCustomerSurname2() {
			return customerSurname2;
		}

		public String getCustomerName() {
			return customerName;
		}

		public String getFinancialRequestId() {
			return responseId;
		}

		public InformationResponseRecord setCustomerNif(String customerNif) {
			this.customerNif = customerNif;
			return this;
		}

		public InformationResponseRecord setCustomerSurname1(String surname1) {
			this.customerSurname1 = surname1;
			return this;
		}

		public InformationResponseRecord setCustomerSurname2(String surname2) {
			this.customerSurname2 = surname2;
			return this;
		}

		public InformationResponseRecord setCustomerName(String customerName) {
			this.customerName = customerName;
			return this;
		}

		public InformationResponseRecord setResponseId(String responseId) {
			this.responseId = responseId;
			return this;
		}

		public InformationResponseRecord setCustomerStatus(String customerStatus) {
			this.customerStatus = customerStatus;
			return this;
		}

		public InformationResponseRecord setContractType(String contractType) {
			this.contractType = contractType;
			return this;
		}

		public InformationResponseRecord setCustomerJobType(int customerJobType) {
			this.customerJobType = customerJobType;
			return this;
		}

		public InformationResponseRecord setCustomerContributionGroup(int customerContributionGroup) {
			this.customerContributionGroup = customerContributionGroup;
			return this;
		}

		public InformationResponseRecord setEmployerCIF(String employerCIF) {
			this.employerCIF = employerCIF;
			return this;
		}

		public InformationResponseRecord setEmployerSurname1(String employerSurname1) {
			this.employerSurname1 = employerSurname1;
			return this;
		}

		public InformationResponseRecord setEmployerSurname2(String employerSurname2) {
			this.employerSurname2 = employerSurname2;
			return this;
		}

		public InformationResponseRecord setEmployerName(String employerName) {
			this.employerName = employerName;
			return this;
		}

		public InformationResponseRecord setCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		public InformationResponseRecord setCompanyJobType(int companyJobType) {
			this.companyJobType = companyJobType;
			return this;
		}

		public InformationResponseRecord setInfoExtractionDate(Date infoExtractionDate) {
			this.infoExtractionDate = infoExtractionDate;
			return this;
		}

		@Override
		public String toString() {
			return "InformationResponseRecord [requestingFinancialEntityCode=" + requestingFinancialEntityCode
					+ ", requestingFinancialOfficeCode=" + requestingFinancialOfficeCode + ", customerNif="
					+ customerNif + ", customerSurname1=" + customerSurname1 + ", customerSurname2=" + customerSurname2
					+ ", customerName=" + customerName + ", responseId=" + responseId + ", customerStatus="
					+ customerStatus + ", contractType=" + contractType + ", customerJobType=" + customerJobType
					+ ", customerContributionGroup=" + customerContributionGroup + ", employerCIF=" + employerCIF
					+ ", employerSurname1=" + employerSurname1 + ", employerSurname2=" + employerSurname2
					+ ", employerName=" + employerName + ", companyName=" + companyName + ", companyJobType="
					+ companyJobType + ", infoExtractionDate=" + infoExtractionDate + "]";
		}

	}

	/**
	 * This class represents a single request, that is a request for 1 customer
	 * 
	 * @author aoviedo
	 *
	 */
	@StringField(id = "06")
	public static final class ComplementaryInformationResponseRecord implements Serializable {

		private static final long serialVersionUID = -4645438451523015279L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 6;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%04d")
		private static final int requestingFinancialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "%04d")
		private static final int requestingFinancialOfficeCode = EntityConstants.BANK_OFFICE_CODE;

		@StringField(offset = 12, format = "%'0'10s")
		private String customerNif;

		@StringField(offset = 22, format = "%-20s")
		private String customerSurname1;

		@StringField(offset = 42, format = "%-20s")
		private String customerSurname2;

		@StringField(offset = 62, format = "%-20s")
		private String customerName;

		@StringField(offset = 82, format = "%-16s")
		private String responseId;

		@StringField(offset = 98, format = "%2s")
		private String customerStatus;

		@StringField(offset = 100, format = "%2s")
		private String contractType;

		@StringField(offset = 102, format = "%05d")
		private int customerJobType;

		@StringField(offset = 107, format = "%2d")
		private int customerContributionGroup;

		@StringField(offset = 109, format = "%10s")
		private String employerCIF;

		@StringField(offset = 119, format = "%20s")
		private String employerSurname1;

		@StringField(offset = 139, format = "%20s")
		private String employerSurname2;

		@StringField(offset = 159, format = "%20s")
		private String employerName;

		@StringField(offset = 179, format = "%55s")
		private String companyName;

		@StringField(offset = 234, format = "%05d")
		private int companyJobType;

		@StringField(offset = 239, format = "yyyyMMdd")
		private Date infoExtractionDate;

		@StringField(offset = 247, format = "%53s")
		private static final String emptyField = "";

		public String getCustomerStatus() {
			return customerStatus;
		}

		public String getContractType() {
			return contractType;
		}

		public int getCustomerJobType() {
			return customerJobType;
		}

		public int getCustomerContributionGroup() {
			return customerContributionGroup;
		}

		public String getEmployerCIF() {
			return employerCIF;
		}

		public String getEmployerSurname1() {
			return employerSurname1;
		}

		public String getEmployerSurname2() {
			return employerSurname2;
		}

		public String getEmployerName() {
			return employerName;
		}

		public String getCompanyName() {
			return companyName;
		}

		public int getCompanyJobType() {
			return companyJobType;
		}

		public Date getInfoExtractionDate() {
			return infoExtractionDate;
		}

		public String getCustomerNif() {
			return customerNif;
		}

		public String getCustomerSurname1() {
			return customerSurname1;
		}

		public String getCustomerSurname2() {
			return customerSurname2;
		}

		public String getCustomerName() {
			return customerName;
		}

		public String getFinancialRequestId() {
			return responseId;
		}

		public ComplementaryInformationResponseRecord setCustomerNif(String customerNif) {
			this.customerNif = customerNif;
			return this;
		}

		public ComplementaryInformationResponseRecord setCustomerSurname1(String surname1) {
			this.customerSurname1 = surname1;
			return this;
		}

		public ComplementaryInformationResponseRecord setCustomerSurname2(String surname2) {
			this.customerSurname2 = surname2;
			return this;
		}

		public ComplementaryInformationResponseRecord setCustomerName(String customerName) {
			this.customerName = customerName;
			return this;
		}

		public ComplementaryInformationResponseRecord setResponseId(String responseId) {
			this.responseId = responseId;
			return this;
		}

		public ComplementaryInformationResponseRecord setCustomerStatus(String customerStatus) {
			this.customerStatus = customerStatus;
			return this;
		}

		public ComplementaryInformationResponseRecord setContractType(String contractType) {
			this.contractType = contractType;
			return this;
		}

		public ComplementaryInformationResponseRecord setCustomerJobType(int customerJobType) {
			this.customerJobType = customerJobType;
			return this;
		}

		public ComplementaryInformationResponseRecord setCustomerContributionGroup(int customerContributionGroup) {
			this.customerContributionGroup = customerContributionGroup;
			return this;
		}

		public ComplementaryInformationResponseRecord setEmployerCIF(String employerCIF) {
			this.employerCIF = employerCIF;
			return this;
		}

		public ComplementaryInformationResponseRecord setEmployerSurname1(String employerSurname1) {
			this.employerSurname1 = employerSurname1;
			return this;
		}

		public ComplementaryInformationResponseRecord setEmployerSurname2(String employerSurname2) {
			this.employerSurname2 = employerSurname2;
			return this;
		}

		public ComplementaryInformationResponseRecord setEmployerName(String employerName) {
			this.employerName = employerName;
			return this;
		}

		public ComplementaryInformationResponseRecord setCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		public ComplementaryInformationResponseRecord setCompanyJobType(int companyJobType) {
			this.companyJobType = companyJobType;
			return this;
		}

		public ComplementaryInformationResponseRecord setInfoExtractionDate(Date infoExtractionDate) {
			this.infoExtractionDate = infoExtractionDate;
			return this;
		}

		@Override
		public String toString() {
			return "ComplementaryInformationResponseRecord [requestingFinancialEntityCode="
					+ requestingFinancialEntityCode + ", requestingFinancialOfficeCode="
					+ requestingFinancialOfficeCode + ", customerNif=" + customerNif + ", customerSurname1="
					+ customerSurname1 + ", customerSurname2=" + customerSurname2 + ", customerName=" + customerName
					+ ", responseId=" + responseId + ", customerStatus=" + customerStatus + ", contractType="
					+ contractType + ", customerJobType=" + customerJobType + ", customerContributionGroup="
					+ customerContributionGroup + ", employerCIF=" + employerCIF + ", employerSurname1="
					+ employerSurname1 + ", employerSurname2=" + employerSurname2 + ", employerName=" + employerName
					+ ", companyName=" + companyName + ", companyJobType=" + companyJobType + ", infoExtractionDate="
					+ infoExtractionDate + "]";
		}

	}

	public static final class RequestingEntityTotal implements Serializable {

		private static final long serialVersionUID = -7311501726807772330L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 7;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%04d")
		private static final int requestingFinancialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date requestSendingDate;

		@StringField(offset = 16, format = "%02d")
		private int requestNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%06d")
		private int numberOfRecords;

		@StringField(offset = 32, format = "%04d")
		private static final int reportingFinancialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 36, format = "%06d")
		private int numberOfAcceptedRequests;

		@StringField(offset = 42, format = "%06d")
		private int numberOfRejectedRequests;

		@StringField(offset = 48, format = "%06d")
		private int numberOfComplementaryRequests;

		@StringField(offset = 54, format = "%246s")
		private static final String emptyField = "";

		public Date getRequestSendingDate() {
			return requestSendingDate;
		}

		public int getRequestNrForDay() {
			return requestNrForDay;
		}

		public Date getFileCreationDate() {
			return fileCreationDate;
		}

		public int getNumberOfRecords() {
			return numberOfRecords;
		}

		public int getReportingFinancialEntityCode() {
			return reportingFinancialEntityCode;
		}

		public int getNumberOfAcceptedRequests() {
			return numberOfAcceptedRequests;
		}

		public int getNumberOfRejectedRequests() {
			return numberOfRejectedRequests;
		}

		public int getNumberOfComplementaryRequests() {
			return numberOfComplementaryRequests;
		}

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

		public RequestingEntityTotal setNumberOfAcceptedRequests(int numberOfAcceptedRequests) {
			this.numberOfAcceptedRequests = numberOfAcceptedRequests;
			return this;
		}

		public RequestingEntityTotal setNumberOfRejectedRequests(int numberOfRejectedRequests) {
			this.numberOfRejectedRequests = numberOfRejectedRequests;
			return this;
		}

		public RequestingEntityTotal setNumberOfComplementaryRequests(int numberOfComplementaryRequests) {
			this.numberOfComplementaryRequests = numberOfComplementaryRequests;
			return this;
		}

		@Override
		public String toString() {
			return "RequestingEntityTotal [requestingFinancialEntityCode=" + requestingFinancialEntityCode
					+ ", requestSendingDate=" + requestSendingDate + ", requestNrForDay=" + requestNrForDay
					+ ", fileCreationDate=" + fileCreationDate + ", numberOfRecords=" + numberOfRecords
					+ ", reportingFinancialEntityCode=" + reportingFinancialEntityCode + ", numberOfAcceptedRequests="
					+ numberOfAcceptedRequests + ", numberOfRejectedRequests=" + numberOfRejectedRequests
					+ ", numberOfComplementaryRequests=" + numberOfComplementaryRequests + "]";
		}

	}

	public static final class ReportingEntityTotal implements Serializable {

		private static final long serialVersionUID = 5087182638053166914L;

		@StringField(offset = 0, format = "%02d")
		private static final int registryType = 9;

		@StringField(offset = 2)
		private static final String fileType = "RV";

		@StringField(offset = 4, format = "%04d")
		private static final int requestingFinancialEntityCode = EntityConstants.BANK_ENTITY_CODE;

		@StringField(offset = 8, format = "yyyyMMdd")
		private Date responseSendDate;

		@StringField(offset = 16, format = "%02d")
		private int responseNrForDay;

		@StringField(offset = 18, format = "yyyyMMdd")
		private Date fileCreationDate;

		@StringField(offset = 26, format = "%06d")
		private int totalNumberOfRecords;

		@StringField(offset = 32, format = "%06d")
		private int numberOfAcceptedRequests;

		@StringField(offset = 38, format = "%06d")
		private int numberOfRejectedRequests;

		@StringField(offset = 44, format = "%06d")
		private int numberOfComplementaryRequests;

		public ReportingEntityTotal setResponseSendingDate(Date responseSendDate) {
			this.responseSendDate = responseSendDate;
			return this;
		}

		public ReportingEntityTotal setResponseNrForDay(int responseNrForDay) {
			this.responseNrForDay = responseNrForDay;
			return this;
		}

		public ReportingEntityTotal setFileCreationDate(Date fileCreationDate) {
			this.fileCreationDate = fileCreationDate;
			return this;
		}

		public ReportingEntityTotal setTotalNumberOfRecords(int totalNumberOfRecords) {
			this.totalNumberOfRecords = totalNumberOfRecords;
			return this;
		}

		public ReportingEntityTotal setNumberOfAcceptedRequests(int numberOfAcceptedRequests) {
			this.numberOfAcceptedRequests = numberOfAcceptedRequests;
			return this;
		}

		public ReportingEntityTotal setNumberOfRejectedRequests(int numberOfRejectedRequests) {
			this.numberOfRejectedRequests = numberOfRejectedRequests;
			return this;
		}

		public ReportingEntityTotal setNumberOfComplementaryRecords(int numberOfComplementaryRequests) {
			this.numberOfComplementaryRequests = numberOfComplementaryRequests;
			return this;
		}

		@Override
		public String toString() {
			return "ReportingEntityTotal [requestingFinancialEntityCode=" + requestingFinancialEntityCode
					+ ", responseSendDate=" + responseSendDate + ", responseNrForDay=" + responseNrForDay
					+ ", fileCreationDate=" + fileCreationDate + ", totalNumberOfRecords=" + totalNumberOfRecords
					+ ", numberOfAcceptedRequests=" + numberOfAcceptedRequests + ", numberOfRejectedRequests="
					+ numberOfRejectedRequests + ", numberOfComplementaryRequests=" + numberOfComplementaryRequests
					+ "]";
		}

	}

}
