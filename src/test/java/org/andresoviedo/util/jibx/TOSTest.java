package org.andresoviedo.util.jibx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.andresoviedo.util.bean.BeanUtils;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public final class TOSTest {

	public interface Contrato {

		void setArea(String string);

		void setDcInterno(String string);

		void setModalidad(String string);

	}

	public class ContratoImpl implements Contrato {

		public ContratoImpl(String string, String string2, String string3, String string4) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void setArea(String string) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setDcInterno(String string) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setModalidad(String string) {
			// TODO Auto-generated method stub

		}

	}

	private static final String CONTRATO_XML_SAMPLE = "<contrato><area>area1</area><contrato>0100743842</contrato><dc>13</dc><dcInterno>dcInterno1</dcInterno>"
			+ "<entidad>2100</entidad><modalidad>mod1</modalidad><oficina>0630</oficina></contrato>";

	private Contrato contrato;

	@Before
	public void setUp() {
		contrato = new ContratoImpl("2100", "0630", "13", "0100743842");
		contrato.setArea("area1");
		contrato.setDcInterno("dcInterno1");
		contrato.setModalidad("mod1");
	}

	@After
	public void tearDown() {
		contrato = null;
	}

	@Test
	public void testContratoSerialization() throws Exception {
		OMElement omEl = marshallJibxObject(contrato);
		StringWriter stringWriter = new StringWriter();
		omEl.serialize(stringWriter);
		Assert.assertEquals(CONTRATO_XML_SAMPLE, stringWriter.getBuffer().toString());
	}

	@Test
	public void testImporteDivisaSerialization() throws Exception {
		Contrato unmarshaledContract = (Contrato) unmarshallJibxObject(CONTRATO_XML_SAMPLE, ContratoImpl.class);
		Assert.assertEquals(BeanUtils.reflectionToString(contrato), BeanUtils.reflectionToString(unmarshaledContract));
	}

	/**
	 * Genera un nodo XML a partir de la serialización mediante JIBX del objeto especificado.
	 * 
	 * @param obj
	 *            el objeto a serializar
	 * @return el nodo XML generado
	 * @throws JiBXException
	 * @throws XMLStreamException
	 */
	OMElement marshallJibxObject(Object obj) throws Exception {
		IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
		IMarshallingContext mctx = bfact.createMarshallingContext();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mctx.marshalDocument(obj, null, null, baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		StAXOMBuilder stAXOMBuilder = new StAXOMBuilder(bais);
		return stAXOMBuilder.getDocumentElement();
	}

	/**
	 * Deserializa mediante JIBX el documento XML especificado, el cual crea un objeto de la clase especificada.
	 * 
	 * @param xmlResponse
	 *            xml con el contenido a deserializar
	 * @param clazz
	 *            la clase del objeto que se esta deserializando
	 * @return el objeto deserializado a partir del XML
	 * @throws JiBXException
	 */
	Object unmarshallJibxObject(String xmlResponse, Class<?> clazz) throws Exception {
		StringReader reader = new StringReader(xmlResponse);
		IBindingFactory bfact = BindingDirectory.getFactory(clazz);
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
		return uctx.unmarshalDocument(reader);
	}

}
