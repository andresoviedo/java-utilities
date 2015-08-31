package org.andresoviedo.util.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase que encapsula el objeto de negocio Contrato:
 * 
 * formato Formato de visualitzación del contrato: interbancari, 4-3-6-2, 4-2-6-2, 4-2-7-2 entidad Codigo entidad para el formato
 * interbancario dc Digitos de control para el formato interbancario area Area alfabetica para formatos 4-3-6-2, 4-2-6-2, 4-2-7-2 oficina
 * Oficina (aplica a todos los formatos) modalidad Modalidad (aplica a todos los formatos) contrato Contrato (aplica a todos los formatos) y
 * puede contener el dcInterno dcInterno Digito de control interno para uso de formatos 4-3-6-2, 4-2-6-2, 4-2-7-2
 * 
 * @author andresoviedo
 * 
 */
public class ContratoDeTest implements Serializable {

	private static final long serialVersionUID = -2192196459956385982L;

	public static final String INTERBANK_FORMAT = "interbank";
	public static final String INTERNAL_4362_FORMAT = "internal4-3-6-2";
	public static final String INTERNAL_4262_FORMAT = "internal4-2-6-2";
	public static final String INTERNAL_4272_FORMAT = "internal4-2-7-2";

	private String entidad;
	private String dc;
	private String area;
	private String oficina;
	private String modalidad;
	private String contrato;
	private String dcInterno;

	/**
	 * Default constructor
	 */
	public ContratoDeTest() {
	}

	/**
	 * Construcción de un contrato interbancario
	 * 
	 * @param entidad
	 *            Codigo entidad
	 * @param oficina
	 *            oficina
	 * @param dc
	 *            Dígito de control
	 * @param contrato
	 *            contrato
	 */
	public ContratoDeTest(String entidad, final String oficina, final String dc, final String contrato) {
		this.entidad = entidad;
		this.oficina = oficina;
		this.dc = dc;
		this.contrato = contrato;
	}

	/**
	 * Construcción de un contrato interno (4-3-6-2, 4-2-6-2, 4-2-7-2)
	 * 
	 * @param oficina
	 *            oficina del contrato
	 * @param modalidad
	 *            modalidad del contrato
	 * @param contrato
	 *            contrato
	 * 
	 */
	public ContratoDeTest(String oficina, final String modalidad, final String contrato) {
		this(null, oficina, modalidad, contrato, null);
	}

	/**
	 * Construción de un contrato interno (4-3-6-2, 4-2-6-2, 4-2-7-2)
	 * 
	 * @param area
	 *            area alfabetica del contrato
	 * @param oficina
	 *            oficina del contrato
	 * @param modalidad
	 *            modalidad del contrato
	 * @param contrato
	 *            contrato
	 */
	public ContratoDeTest(String area, final String oficina, final String modalidad, final String contrato, final String dcInterno) {
		this.area = area;
		this.oficina = oficina;
		this.modalidad = modalidad;
		this.contrato = contrato;
		this.dcInterno = dcInterno;
	}

	/**
	 * El mètodo {@link ContratoDeTest.getFormato} calcula el formato en función de los datos actuales, actualmente soporta la detección de
	 * interbancario, interno (4-3-6-2, 4-2-6-2, 4-2-7-2) por lo que no se recomienda el uso de este método a no ser que se necesite un tipo
	 * no soportado por el este objeto.
	 * 
	 * @param formato
	 *            formato del contrato
	 */
	public void setFormato(String formato) {
	}

	public String getFormato() {
		return whoami();
	}

	public String getEntidad() {
		return this.entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getDc() {
		return this.dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getOficina() {
		return this.oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public String getModalidad() {
		return this.modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}

	public String getContrato() {
		return this.contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public String getDcInterno() {
		return this.dcInterno;
	}

	public void setDcInterno(String dcInterno) {
		this.dcInterno = dcInterno;
	}

	/**
	 * Calcula el formato en función de los datos del contrato
	 * 
	 * @return el tipo de formato valido asociado al contrato, null si no se puede calcular
	 */
	private String whoami() {

		// Detecció i validació del Interbancari >> EEEE OOOO DD MMNNNNNNDD
		if (StringUtils.isNotBlank(this.entidad) && StringUtils.isNotBlank(this.oficina) && StringUtils.isNotBlank(this.dc)
				&& StringUtils.isNotEmpty(this.contrato)) {
			return INTERBANK_FORMAT;
		}

		// Tipus interns. Es cobreix dos variacions:
		// 1) El número de compte SI porta concatenat els dígits de control
		// 2) El número de compte NO porta concatenat els dígits de control
		if (StringUtils.isNotEmpty(this.oficina) && StringUtils.isNotEmpty(this.modalidad) && StringUtils.isNotEmpty(this.contrato)) {
			// Validació oficina
			if (this.oficina.length() > 4) {
				return null;
			}

			int dcLength = this.dcInterno != null ? this.dcInterno.length() : 0;

			// Detecció i validació del format internal-4362 >> OOOO MMM NNNNNNDD
			if (this.modalidad.length() == 3 && (this.contrato.length() == 8 || this.contrato.length() + dcLength == 8)) {
				return INTERNAL_4362_FORMAT;
			}

			// Detecció i validació del format internal-4272 >> OOOO MM NNNNNNNDD
			if (this.modalidad.length() == 2 && (this.contrato.length() == 9 || this.contrato.length() + dcLength == 9)) {
				return INTERNAL_4272_FORMAT;
			}

			// Detecció i validació del format internal-4262 >> OOOO MM NNNNNNDD
			if (this.modalidad.length() == 2 && (this.contrato.length() == 8 || this.contrato.length() + dcLength == 8)) {
				return INTERNAL_4262_FORMAT;
			}
		}

		return null;
	}

	/**
	 * Extracción del contrato para formatos internal
	 * 
	 * @return el contrato sin digitos de control, null si no es formato internal
	 */
	public String extraerContratoInterno() {
		String contracte = this.contrato + (this.dcInterno != null ? this.dcInterno : "");
		if (isInternal4362()) {
			return contracte.substring(0, 6);
		} else if (isInteral4272()) {
			return contracte.substring(0, 7);
		} else if (isInternal4262()) {
			return contracte.substring(0, 6);
		} else {
			return null;
		}
	}

	/**
	 * Extracción de los digitos de control internos para formatos internal
	 * 
	 * @return digitos de control, null is no es formato internal
	 */
	public String extraerDigitoInterno() {
		String contracte = this.contrato + (this.dcInterno != null ? this.dcInterno : "");
		if (isInternal4362()) {
			return contracte.substring(6, 8);
		} else if (isInteral4272()) {
			return contracte.substring(7, 9);
		} else if (isInternal4262()) {
			return contracte.substring(6, 8);
		} else {
			return null;
		}
	}

	/**
	 * Indica si es formato interbancario
	 * 
	 * @return true si es formato interbancario, false en otro caso
	 */
	public boolean isInterBank() {
		return INTERBANK_FORMAT.equals(whoami());
	}

	/**
	 * Indica si es formato internal 4362
	 * 
	 * @return true si es formato internal 4362, false en otro caso
	 */
	public boolean isInternal4362() {
		return INTERNAL_4362_FORMAT.equals(whoami());
	}

	/**
	 * Indica si es formato internal 4262
	 * 
	 * @return true si es formato internal 4262, false en otro caso
	 */
	public boolean isInternal4262() {
		return INTERNAL_4262_FORMAT.equals(whoami());
	}

	/**
	 * Indica si es formato internal 4272
	 * 
	 * @return true si es formato internal 4272, false en otro caso
	 */
	public boolean isInteral4272() {
		return INTERNAL_4272_FORMAT.equals(whoami());
	}
}
