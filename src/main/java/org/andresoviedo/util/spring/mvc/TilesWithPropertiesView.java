package org.andresoviedo.util.spring.mvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.tiles.Attribute;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.servlet.context.ServletTilesApplicationContext;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextAware;

/**
 * Tiles extension for exposing the tiles attributes to the spring view model
 * (static attributes). Useful for decorating the view with dynamic properties
 * based on the those static attributes.
 * 
 * <p>
 * In the following example, there is a static tiles attribute
 * <code>ID_PAGE</code> that we are exposing in the <code>views.xml</code>. So
 * the <code>ID_PAGE</code> with its value <code>PAGE_001</code> will be exposed
 * as static attributes in the view model:
 * 
 * <pre>
 *{@code
 *	<tiles-definitions>
 *		<definition name="myapp.homepage" template="/layouts/classic.jsp">
 *			<put-attribute name="title" value="Tiles tutorial homepage" />
 *			<put-attribute name="header" value="/tiles/banner.jsp" />
 *			<put-attribute name="menu" value="/tiles/common_menu.jsp" />
 *			<put-attribute name="body" value="/tiles/home_body.jsp" />
 *			<put-attribute name="footer" value="/tiles/credits.jsp" />
 *			<put-attribute name="ID_PAGE" value="PAGE_001" />
 *		</definition>
 *	</tiles-definitions>
 *}
 * </pre>
 * </p>
 * 
 * <p>
 * Just configure the static properties to expose in the
 * {@link #tilesPropertiesToExpose} variable. In our example we want to expose
 * "title" and "ID_PAGE".
 * </p>
 * 
 * @author andresoviedo
 */
public class TilesWithPropertiesView extends org.springframework.web.servlet.view.tiles2.TilesView
		implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(TilesWithPropertiesView.class);

	/**
	 * mandatory
	 */
	private final Set<String> tilesPropertiesToExpose = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[] { "title", "ID_PAGE" })));

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		if (tilesPropertiesToExpose == null || tilesPropertiesToExpose.isEmpty()) {
			throw new IllegalArgumentException("No tiles properties to expose configured");
		}

		super.afterPropertiesSet();

		final TilesContainer container = ServletUtil.getContainer(getServletContext());
		if (!(container instanceof BasicTilesContainer)) {
			// Cannot check properly - let's assume it's there.
			return;
		}
		final BasicTilesContainer basicContainer = (BasicTilesContainer) container;
		final TilesApplicationContext appContext = new ServletTilesApplicationContext(getServletContext());
		final TilesRequestContext requestContext = new ServletTilesRequestContext(appContext, null, null) {
			@Override
			public Locale getRequestLocale() {
				return Locale.getDefault();
			}
		};
		if (basicContainer.getDefinitionsFactory().getDefinition(getUrl(), requestContext) != null) {
			for (final String tileProperty : tilesPropertiesToExpose) {
				final Attribute attr = basicContainer.getDefinitionsFactory().getDefinition(getUrl(), requestContext)
						.getAttribute(tileProperty);
				if (attr != null && attr.getValue() != null) {
					super.addStaticAttribute(tileProperty, attr.getValue().toString());
				}

			}
			if (!super.getStaticAttributes().isEmpty()) {
				LOGGER.debug("View properties '{}' ==> '{}'", getUrl(), super.getStaticAttributes());
			}
		}
	}

}
