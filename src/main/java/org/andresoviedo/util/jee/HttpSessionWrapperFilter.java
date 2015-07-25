package org.andresoviedo.util.jee;



import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.WebUtils;

/**
 * HttpSessionWrapper que wrappea la request. Se implementa debido a un "bug?" de la implementación de la HttpSession
 * del Weblogic.
 * <p>
 * Requiere (idealmente) que el listener de Spring HttpSessionMutexListener este registrado en la aplicación para
 * garantizar que se puede sincronizar la sesión de usuario.
 * </p>
 * 
 * @author andresoviedo
 */
public class HttpSessionWrapperFilter implements Filter {

	private Log logger = LogFactory.getLog(HttpSessionWrapperFilter.class);

	public void init(FilterConfig arg0) throws ServletException {
		logger.debug("HttpSessionWrapperFilter initialized");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		filterChain.doFilter(new HttpRequestWrapper((HttpServletRequest) request), response);
	}

	public void destroy() {
	}

	/**
	 * Wrapper de la request que a su vez wrappea la sessión por una sesión que esta sincronizada.
	 * <p>
	 * EV-4671: MCA > Implementar parche AppContext por problema con implementación HttpSession del Weblogic
	 * </p>
	 * 
	 * @author andresoviedo
	 */
	private final class HttpRequestWrapper extends HttpServletRequestWrapper {

		public HttpRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public HttpSession getSession() {
			return new SynchronizedHttpSessionWrapper(super.getSession());
		}

		@Override
		public HttpSession getSession(boolean create) {
			HttpSession session = super.getSession(create);
			return session != null ? new SynchronizedHttpSessionWrapper(session) : null;
		}
	}

	/**
	 * Wrapper de la HttpSession que sincroniza los accesos de setAttribute, removeAttribute y getAttribute.
	 * 
	 * @author andresoviedo
	 */
	private final class SynchronizedHttpSessionWrapper implements HttpSession {

		private final HttpSession session;

		private SynchronizedHttpSessionWrapper(HttpSession session) {
			this.session = session;
		}

		public Object getAttribute(String arg0) {
			synchronized (WebUtils.getSessionMutex(session)) {
				return session.getAttribute(arg0);
			}
		}

		public Enumeration getAttributeNames() {
			return new FilteringEnumeration(session.getAttributeNames());
		}

		public long getCreationTime() {
			return session.getCreationTime();
		}

		public String getId() {
			return session.getId();
		}

		public long getLastAccessedTime() {
			return session.getLastAccessedTime();
		}

		public int getMaxInactiveInterval() {
			return session.getMaxInactiveInterval();
		}

		public ServletContext getServletContext() {
			return session.getServletContext();
		}

		public HttpSessionContext getSessionContext() {
			return session.getSessionContext();
		}

		public Object getValue(String arg0) {
			return session.getValue(arg0);
		}

		public String[] getValueNames() {
			return session.getValueNames();
		}

		public void invalidate() {
			session.invalidate();
		}

		public boolean isNew() {
			return session.isNew();
		}

		public void putValue(String arg0, Object arg1) {
			session.putValue(arg0, arg1);
		}

		public void removeAttribute(String arg0) {
			if (!WebUtils.SESSION_MUTEX_ATTRIBUTE.equals(arg0)) {
				synchronized (WebUtils.getSessionMutex(session)) {
					session.removeAttribute(arg0);
				}
			}
		}

		public void removeValue(String arg0) {
			session.removeValue(arg0);
		}

		public void setAttribute(String arg0, Object arg1) {
			if (!WebUtils.SESSION_MUTEX_ATTRIBUTE.equals(arg0)) {
				synchronized (WebUtils.getSessionMutex(session)) {
					session.setAttribute(arg0, arg1);
				}
			}
		}

		public void setMaxInactiveInterval(int arg0) {
			session.setMaxInactiveInterval(arg0);
		}

	}

	/**
	 * <pre>
	 * The utillib library.
	 * More information is available at http://www.jinchess.com/.
	 * Copyright (C) 2003 Alexander Maryanovsky.
	 * All rights reserved.
	 * 
	 * The utillib library is free software; you can redistribute
	 * it and/or modify it under the terms of the GNU Lesser General Public License
	 * as published by the Free Software Foundation; either version 2 of the
	 * License, or (at your option) any later version.
	 * 
	 * The utillib library is distributed in the hope that it will
	 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
	 * General Public License for more details.
	 * 
	 * You should have received a copy of the GNU Lesser General Public License
	 * along with utillib library; if not, write to the Free Software
	 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	 * 
	 * An implementation of the <code>Enumeration</code> interface which delegates to another <code>Enumeration</code>,
	 * but only returns elements which pass the {@link #accept(Object)} method.
	 * </pre>
	 */
	private final class FilteringEnumeration implements Enumeration {

		/**
		 * The delegate enumeration.
		 */

		private final Enumeration delegate;

		/**
		 * The next element we'll return. This is set by the <code>findNext</code> method.
		 */

		private Object next = null;

		/**
		 * Creates a new <code>FilteringEnumeration</code> object with the specified delegate.
		 */

		public FilteringEnumeration(Enumeration delegate) {
			this.delegate = delegate;
		}

		/**
		 * Finds the next element in the delegate enumeration which passes <code>accept</code> and puts it in
		 * <code>next</code>.
		 */

		private void findNext() {
			if (next != null)
				return;

			while (delegate.hasMoreElements()) {
				Object element = delegate.nextElement();
				if (accept(element)) {
					next = element;
					break;
				}
			}
		}

		/**
		 * Returns whether there are more elements in this <code>Enumeration</code>.
		 */

		public boolean hasMoreElements() {
			findNext();

			return next != null;
		}

		/**
		 * Returns the next element in the delegate enumeration which passes the <code>accept</code> method.
		 */

		public Object nextElement() throws NoSuchElementException {
			findNext();

			if (next == null)
				throw new NoSuchElementException();

			Object result = next;
			next = null;
			return result;
		}

		/**
		 * Returns whether the specified object passes the filter.
		 */

		public boolean accept(Object element) {
			return !WebUtils.SESSION_MUTEX_ATTRIBUTE.equals(element);
		}
	}
}
