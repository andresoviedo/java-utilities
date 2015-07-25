package org.andresoviedo.util.http;

import java.util.ArrayList;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Clase de utilidades para la gestión de cookies.
 * 
 * @author andresoviedo
 * 
 *         TODO Las constantes de esta classe deberia moverse a un sitio más apropiado (otra clase y teniendo en cuenta
 *         si son constantes multicanal o específicas de canal oficina) ya que esta clase deberia ser genérica.
 */

public class CookieUtils {
	/**
	 * Método para la recuperación de una cookie.
	 * 
	 * @param cookieName
	 *            Nombre de la cookie.
	 * @param request
	 *            HTTP request en curs.
	 * @return La cookie cuyo name corresponde al pámetro cookieName, null si la cookie no existe.
	 */
	public static Cookie getCookie(String cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(cookieName)) {
					return cookies[i];
				}
			}
		}

		return null;
	}

	/**
	 * Obtiene un valor de cookie a partir del request, asignándole un valor por defecto si no la encuentra.
	 * 
	 * @param request
	 *            HTTP request en curso
	 * @param identifier
	 *            nombre de la cookie a buscar
	 * @param defaultValue
	 *            valor por defecto
	 * 
	 * @return valor de la cookie si se encuentra la cookie indicada, defaultValue si no se encuentra la cookie
	 */
	public static String getCookieValue(HttpServletRequest request, String identifier, String defaultValue) {

		if (request == null) {
			return defaultValue;
		}

		Cookie userCookie = getCookie(identifier, request);
		if (userCookie != null) {
			return userCookie.getValue();
		} else {
			// no encontrado, le asignamos su valor por defecto
			return defaultValue;
		}
	}

	/**
	 * Método para el establecimiento de una cookie con un nombre y un valor determinado. Para los otros atributos de la
	 * cookie se establecen los siguientes valores: Expiración: -1. No persistencia. Path: default path. Dominio: valor
	 * por defecto, dominio actual.
	 * 
	 * @param cookieName
	 *            Nombre de la cookie.
	 * @param cookieValue
	 *            Valor a establecer en la cookie.
	 * @param response
	 *            HTPP response en curso.
	 */
	public static void setCookie(String cookieName, String cookieValue, ServletResponse response) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		((HttpServletResponse) response).addCookie(cookie);
	}

	/**
	 * Método para el establecimiento de una cookie con un nombre, valor y dominio determinado. Deberia usarse para
	 * aquellas cookies cross entre diferentes dominios.
	 * 
	 * Para los otros atributos de la cookie se establecen los siguientes valores: Expiración: -1. No persistencia.
	 * Path: default path.
	 * 
	 * @param cookieName
	 *            Nombre de la cookie.
	 * @param value
	 *            Valor de la cookie.
	 * @param domain
	 *            Dominio asociado a la cookie en formato HDN (Host domain name).
	 * @param response
	 *            HTTP response en curso.
	 */
	public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue,
			String cookieDomain) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(-1);
		cookie.setDomain(cookieDomain);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * Método para el borrado de una cookie de un dominio concreto. Sólo se borrar la cookie si esta existe. Para los
	 * otros atributos de la cookie se establecen los siguientes valores: Expiración: 0. Eliminación Path: default path.
	 * 
	 * @param cookieName
	 *            Nombre de la cookie.
	 * @param domain
	 *            dominio de la cookie a borrar.
	 * @param request
	 *            HTTP request en curso.
	 * @param response
	 *            HTTP response en curso.
	 */
	public static void removeDomainCookie(String cookieName, String domain, HttpServletRequest request,
			HttpServletResponse response) {
		Cookie cookie = getCookie(cookieName, request);
		if (cookie != null) {
			cookie.setMaxAge(0);
			cookie.setDomain(domain);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	/**
	 * Método para el borrado de una cookie en el dominio de la petición Para los otros atributos de la cookie se
	 * establecen los siguientes valores: Expiración: 0. Eliminación Path: default path.
	 * 
	 * @param cookieName
	 *            Nombre de la cookie.
	 * @param request
	 *            HTTP request en curso.
	 * @param response
	 *            HTTP response en curso.
	 */
	public static void removeCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = getCookie(cookieName, request);
		if (cookie != null) {
			cookie.setMaxAge(0);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	// EV-4166 INICIO Eliminación cookies de sesion
	/**
	 * Metodo para buscar los nombres de cookies que comiencen por JSESSIONID
	 */
	public static ArrayList<String> listaCookiesSession(HttpServletRequest request, HttpServletResponse response) {

		ArrayList<String> listado = new ArrayList<String>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().startsWith("JSESSIONID")) {
					listado.add(cookies[i].getName());
				}
			}
		}

		return listado;
	}
	// EV-4166 FINAL Eliminación cookies de sesion

}
