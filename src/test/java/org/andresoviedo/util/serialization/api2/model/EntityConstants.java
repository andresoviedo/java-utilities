package org.andresoviedo.util.serialization.api2.model;

import java.util.regex.Pattern;

/**
 * Interface for implementations of entities.
 * 
 * @author andres
 *
 */
public interface EntityConstants {

	/**
	 * Bank entity number
	 */
	int BANK_ENTITY_CODE = 1139;
	/**
	 * Bank entity number
	 */
	int BANK_OFFICE_CODE = 1;
	/**
	 * Bank folder
	 */
	String BANK_FOLDER = "/" + BANK_ENTITY_CODE;
	/**
	 * Request folder name
	 */
	String REQUEST_FOLDER = "Peticiones";
	/**
	 * Folder to put requests to service
	 */
	String ABSOLUTE_REQUEST_FOLDER = BANK_FOLDER + "/" + REQUEST_FOLDER;
	/**
	 * Confirmation folder name
	 */
	String CONFIRMATION_FOLDER = "Confirmaciones";
	/**
	 * Folder to get request receipt confirmation from service
	 */
	String ABSOLUTE_CONFIRMATION_FOLDER = BANK_FOLDER + "/" + CONFIRMATION_FOLDER;
	/**
	 * Response folder name
	 */
	String RESPONSE_FOLDER = "Respuestas";
	/**
	 * Folder to get responses from service
	 */
	String ABSOLUTE_RESPONSE_FOLDER = BANK_FOLDER + "/" + RESPONSE_FOLDER;
	/**
	 * The format is like "E_EEEEAAAAMMDDHHMM.txt": "{@code <file_kind>_<entity_code><date>.txt}"
	 */
	String FILE_FORMAT = "%1$1c_" + BANK_ENTITY_CODE + "%2$tY%2$tm%2$td%2$tH%2$tM.txt";
	/**
	 * This would be the path for new request
	 */
	String NEW_REQUEST_FILENAME_FORMAT = ABSOLUTE_REQUEST_FOLDER + "/" + FILE_FORMAT;
	/**
	 * Pattern of confirmation filename
	 */
	Pattern CONFIRMATION_FILE_PATTERN = Pattern.compile("C_\\d{16}\\.txt");
	/**
	 * Pattern to get confirmation files
	 */
	Pattern ABSOLUTE_CONFIRMATION_FILES_PATTERN = Pattern.compile(ABSOLUTE_CONFIRMATION_FOLDER + "/"
			+ CONFIRMATION_FILE_PATTERN.pattern());
	/**
	 * Pattern of response filename
	 */
	Pattern RESPONSE_FILE_PATTERN = Pattern.compile("R_\\d{16}\\.txt");
	/**
	 * Pattern to get response files
	 */
	Pattern ABSOLUTE_RESPONSE_FILES_PATTERN = Pattern.compile(ABSOLUTE_RESPONSE_FOLDER + "/"
			+ RESPONSE_FILE_PATTERN.pattern());
	/**
	 * File encoding for requests, confirmation and response
	 */
	String FILE_ENCODING = "ISO-8859-1";

	/**
	 * Type of file
	 * 
	 * @author andres
	 *
	 */
	enum FileType {
		REQUEST('E'), CONFIRMATION('C'), RESPONSE('R');
		public char code;

		FileType(char code) {
			this.code = code;
		}
	};

	void sendRequest(EntityExample1 request);
}
