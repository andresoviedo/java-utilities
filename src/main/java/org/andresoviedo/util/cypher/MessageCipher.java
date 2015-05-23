package org.andresoviedo.util.cypher;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import org.andresoviedo.util.encoding.Base64Coder;

public class MessageCipher {

	static Cipher cipher;
	static Cipher uncipher;

	static {
		try {
			// creamos el cipher AES
			cipher = Cipher.getInstance("AES");
			// inicializamos el cipher en modo encriptación con la clave simétrica
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("privatekey______".getBytes("UTF-8"), "AES"));

			// creamos el cipher AES
			uncipher = Cipher.getInstance("AES");
			// inicializamos el cipher en modo encriptación con la clave simétrica
			uncipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("privatekey______".getBytes("UTF-8"), "AES"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String cypherToBase64(String message) {
		String ret = null;
		try {
			// escriptamos el String
			byte[] encBytes = cipher.doFinal(message.getBytes("UTF-8"));
			// lo convertimos a base64 para que pueda ser representado en ASCII
			ret = String.valueOf(Base64Coder.encode(encBytes));
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	public static String uncypherFromBase64(String base64Message) {
		String ret = null;
		try {
			// convert base64 message to bytes
			byte[] encMessageBytes = Base64Coder.decode(base64Message);
			ret = new String(uncipher.doFinal(encMessageBytes), "UTF-8");
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	public static void main(String[] args) {
		// Referencia (String) a encriptar y desencriptar
		String message = "9";
		System.out.println(message);
		String cipheredMessage = MessageCipher.cypherToBase64(message);
		System.out.println(cipheredMessage);
		System.out.println(MessageCipher.uncypherFromBase64(cipheredMessage));

		message = "hola";
		System.out.println(message);
		cipheredMessage = MessageCipher.cypherToBase64(message);
		System.out.println(cipheredMessage);
		System.out.println(MessageCipher.uncypherFromBase64(cipheredMessage));

		message = "hola. esto es un mensaje corto";
		System.out.println(message);
		cipheredMessage = MessageCipher.cypherToBase64(message);
		System.out.println(cipheredMessage);
		System.out.println(MessageCipher.uncypherFromBase64(cipheredMessage));

		message = "hola. esto es un mensaje largooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";
		System.out.println(message);
		cipheredMessage = MessageCipher.cypherToBase64(message);
		System.out.println(cipheredMessage);
		System.out.println(MessageCipher.uncypherFromBase64(cipheredMessage));
	}
}
