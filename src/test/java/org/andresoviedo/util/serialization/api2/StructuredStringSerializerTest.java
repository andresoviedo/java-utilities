package org.andresoviedo.util.serialization.api2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.andresoviedo.util.serialization.api2.StructuredStringSerializer;
import org.andresoviedo.util.serialization.api2.model.EntityExample1;
import org.andresoviedo.util.serialization.api2.model.EntityExample2;
import org.andresoviedo.util.serialization.api2.model.EntityExample3;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This tests the model is well defined, by checking serialization against (src/main/resources) examples.
 * 
 * @author aoviedo
 *
 */
public class StructuredStringSerializerTest {

	String CONSTANT_DATE_STRING = "2012/06/19";

	String CONSTANT_TIME_STRING = "09:52";

	String CONSTANT_TIME2_STRING = "00:20";

	Date constantDate;

	Date constantTime;

	Date constantTime2;

	StructuredStringSerializer<EntityExample1> requestSerializer;

	StructuredStringSerializer<EntityExample2> confirmationSerializer;

	StructuredStringSerializer<EntityExample3> responseSerializer;

	@Before
	public void init() throws ParseException {
		requestSerializer = new StructuredStringSerializer<EntityExample1>(EntityExample1.class);
		confirmationSerializer = new StructuredStringSerializer<EntityExample2>(EntityExample2.class);
		responseSerializer = new StructuredStringSerializer<EntityExample3>(EntityExample3.class);
		constantDate = new SimpleDateFormat("yyyy/MM/dd").parse(CONSTANT_DATE_STRING);
		constantTime = new SimpleDateFormat("HH:mm").parse(CONSTANT_TIME_STRING);
		constantTime2 = new SimpleDateFormat("HH:mm").parse(CONSTANT_TIME2_STRING);
	}

	@After
	public void destroy() {
		constantDate = null;
		requestSerializer = null;
	}

	@Test
	public void testRequestSerialization() {
		EntityExample1 vr = new EntityExample1();
		vr.getTransmissionHeader().setFileCreationDate(constantDate).setFileCreationTime(constantTime)
				.setRequestNrForDay(1);
		vr.getReportingEntity().setFileCreationDate(constantDate).setRequestNrForDay(1)
				.setRequestSendingDate(constantDate);
		vr.getRequestingEntity().setFileCreationDate(constantDate).setRequestNrForDay(1)
				.setRequestSendingDate(constantDate);
		vr.addRequestRecord().setCustomerNif("5423731S").setCustomerSurname1("CANTARINA").setCustomerSurname2("LIMA")
				.setCustomerName("LUISA").setFinancialRequestId("0000000001");
		vr.addRequestRecord().setCustomerNif("50987213P").setCustomerSurname1("NIETA").setCustomerSurname2("MUROS")
				.setCustomerName("MARTA").setFinancialRequestId("0000000002");
		vr.addRequestRecord().setCustomerNif("53517259T").setCustomerSurname1("CIRRO").setCustomerSurname2("ALVAREZ")
				.setCustomerName("JUAN").setFinancialRequestId("0000000003");
		vr.getRequestingEntityTotal().setRequestSendingDate(constantDate).setRequestNrForDay(1)
				.setFileCreationDate(constantDate).setNumberOfRecords(vr.getRequestsSize() + 2)
				.setNumberOfRequests(vr.getRequestsSize());
		vr.getReportingEntityTotal().setRequestSendingDate(constantDate).setRequestNrForDay(1)
				.setFileCreationDate(constantDate).setNumberOfRecords(vr.getRequestsSize() + 4)
				.setNumberOfRequests(vr.getRequestsSize());

		Assert.assertEquals(
				new Scanner(this.getClass().getResourceAsStream("E_EEEEAAAAMMDDHHMM.txt")).useDelimiter("\\Z").next(),
				requestSerializer.serialize(vr));
	}

	@Test
	public void testConfirmationDeserialization() {
		EntityExample2 vc = new EntityExample2();
		vc.setFileCreationDate(constantDate).setFileCreationTime(constantTime)
				.setIssuerEntityCode("ENTIDAD FINANCIERA DE PRUEBA,S.A        ").setEntityNRBE(1139).setQueueSize(5)
				.setNumberOfRequests(5).setResponseCode(0).setFreeString1("      ");

		Assert.assertEquals(
				vc,
				confirmationSerializer.deserialize(new Scanner(new BOMInputStream(this.getClass().getResourceAsStream(
						"C_EEEEAAAAMMDDHHMM.txt")), "ISO-8859-1").useDelimiter("\\Z").next()));
	}

	@Test
	public void testDeserializationWithScanner() {
		EntityExample2 vc = new EntityExample2();
		vc.setFileCreationDate(constantDate).setFileCreationTime(constantTime)
				.setIssuerEntityCode("ENTIDAD FINANCIERA DE PRUEBA,S.A        ").setEntityNRBE(1139).setQueueSize(5)
				.setNumberOfRequests(5).setResponseCode(0).setFreeString1("      ");

		Assert.assertEquals(
				vc,
				confirmationSerializer.deserialize(new Scanner(new BOMInputStream(this.getClass().getResourceAsStream(
						"C_EEEEAAAAMMDDHHMM.txt")))));

		Assert.assertEquals(
				vc,
				confirmationSerializer.deserialize(new Scanner(new BOMInputStream(this.getClass().getResourceAsStream(
						"C_EEEEAAAAMMDDHHMM.txt")))));
	}

	// 09RV1139201206190120120619000008000003000000000001

	@Test
	public void testResponseDeserialization() {
		EntityExample3 vr = new EntityExample3();
		vr.getTransmissionHeader().setFileCreationDate(constantDate).setFileCreationTime(constantTime2)
				.setRequestNrForDay(1).setReservedField("      ");
		vr.getReportingEntity().setResponseSendingDate(constantDate).setResponseNrForDay(1)
				.setFileCreationDate(constantDate);
		vr.getRequestingEntity().setResponseSendingDate(constantDate).setResponseNrForDay(1)
				.setFileCreationDate(constantDate);
		vr.addResponseRecord().setCustomerNif("005423731S").setCustomerSurname1("CANTARINA           ")
				.setCustomerSurname2("LIMA                ").setCustomerName("LUISA               ")
				.setResponseId("0000000001      ").setCustomerStatus("01").setContractType("CA").setCustomerJobType(0)
				.setCustomerContributionGroup(2).setEmployerCIF("0B78019343")
				.setEmployerSurname1("                    ").setEmployerSurname2("                    ")
				.setEmployerName("                    ")
				.setCompanyName("COLEGIO GENERAL MANUEL MIRAS, S.L.                     ").setCompanyJobType(8531)
				.setInfoExtractionDate(constantDate);
		vr.addResponseRecord().setCustomerNif("050987213P").setCustomerSurname1("NIETA               ")
				.setCustomerSurname2("MUROS               ").setCustomerName("MARTA               ")
				.setResponseId("0000000002      ").setCustomerStatus("01").setContractType("CP")
				.setCustomerJobType(6190).setCustomerContributionGroup(0).setEmployerCIF("          ")
				.setEmployerSurname1("                    ").setEmployerSurname2("                    ")
				.setEmployerName("                    ")
				.setCompanyName("                                                       ").setCompanyJobType(0)
				.setInfoExtractionDate(constantDate);
		vr.addComplementaryResponseRecord().setCustomerNif("050987213P").setCustomerSurname1("NIETA               ")
				.setCustomerSurname2("MUROS               ").setCustomerName("MARTA               ")
				.setResponseId("0000000002      ").setCustomerStatus("01").setContractType("CA").setCustomerJobType(0)
				.setCustomerContributionGroup(1).setEmployerCIF("0B84302258")
				.setEmployerSurname1("                    ").setEmployerSurname2("                    ")
				.setEmployerName("                    ")
				.setCompanyName("AGROPECUARIA VALLECRIO NUEVO, S.L.                     ").setCompanyJobType(111)
				.setInfoExtractionDate(constantDate);
		vr.addResponseRecord().setCustomerNif("053517259T").setCustomerSurname1("CIRRO               ")
				.setCustomerSurname2("ALVAREZ             ").setCustomerName("JUAN                ")
				.setResponseId("0000000003      ").setCustomerStatus("01").setContractType("CA").setCustomerJobType(0)
				.setCustomerContributionGroup(6).setEmployerCIF("0H34098344")
				.setEmployerSurname1("                    ").setEmployerSurname2("                    ")
				.setEmployerName("                    ")
				.setCompanyName("COM.PROP.ALCALDE CERVER SINDE 6                        ").setCompanyJobType(9700)
				.setInfoExtractionDate(constantDate);
		vr.getRequestingEntityTotal().setRequestSendingDate(constantDate).setRequestNrForDay(1)
				.setFileCreationDate(constantDate).setNumberOfRecords(6).setNumberOfComplementaryRequests(1)
				.setNumberOfAcceptedRequests(3).setNumberOfRejectedRequests(0);
		vr.getReportingEntityTotal().setResponseSendingDate(constantDate).setResponseNrForDay(1)
				.setFileCreationDate(constantDate).setTotalNumberOfRecords(8).setNumberOfComplementaryRecords(1)
				.setNumberOfAcceptedRequests(3).setNumberOfRejectedRequests(0);

		Assert.assertEquals(
				vr.toString(),
				responseSerializer.deserialize(
						new Scanner(new BOMInputStream(this.getClass().getResourceAsStream("R_EEEEAAAAMMDDHHMM.txt")))
								.useDelimiter("\\Z").next()).toString());
	}
}
