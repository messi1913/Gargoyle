/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PropertyDescriptor;

/**
 * This class handles the creation of Renderers.
 * 
 * @see Renderer
 */
public class RendererFactory {

	private static final Logger LOG = Logger.getLogger(RendererFactory.class.getName());

	//2016-10-01 final 키워드 삭제.
	public static final Map<String, Class<? extends Renderer>> REPORT_FORMAT_TO_RENDERER = new TreeMap<>();
	static {

//		2016-10-01 추가 by kyj.
		REPORT_FORMAT_TO_RENDERER.put(DatabaseXmlRenderer.NAME, DatabaseXmlRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(CodeClimateRenderer.NAME, CodeClimateRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(XMLRenderer.NAME, XMLRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(IDEAJRenderer.NAME, IDEAJRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(TextColorRenderer.NAME, TextColorRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(TextRenderer.NAME, TextRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(TextPadRenderer.NAME, TextPadRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(EmacsRenderer.NAME, EmacsRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(CSVRenderer.NAME, CSVRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(HTMLRenderer.NAME, HTMLRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(XSLTRenderer.NAME, XSLTRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(YAHTMLRenderer.NAME, YAHTMLRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(SummaryHTMLRenderer.NAME, SummaryHTMLRenderer.class);
		REPORT_FORMAT_TO_RENDERER.put(VBHTMLRenderer.NAME, VBHTMLRenderer.class);
		
		//2016-10-01 수정불가능하므로 삭제.
//		REPORT_FORMAT_TO_RENDERER = Collections.unmodifiableMap(map);
	}

	/********************************
	 * 작성일 :  2016. 10. 1. 작성자 : KYJ
	 *
	 *
	 * @param name
	 * @param rendererClass
	 ********************************/
	public static synchronized void putNewReportRenderer(String name, Class<? extends Renderer> rendererClass){
		REPORT_FORMAT_TO_RENDERER.put(name, rendererClass);
	}
	
	
	/**
	 * Construct an instance of a Renderer based on report format name.
	 * 
	 * @param reportFormat
	 *            The report format name.
	 * @param properties
	 *            Initialization properties for the corresponding Renderer.
	 * @return A Renderer instance.
	 */
	public static Renderer createRenderer(String reportFormat, Properties properties) {
		Class<? extends Renderer> rendererClass = getRendererClass(reportFormat);
		Constructor<? extends Renderer> constructor = getRendererConstructor(rendererClass);

		Renderer renderer;
		try {
			if (constructor.getParameterTypes().length > 0) {
				LOG.warning(
						"The renderer uses a deprecated mechanism to use the properties. Please define the needed properties with this.definePropertyDescriptor(..).");
				renderer = constructor.newInstance(properties);
			} else {
				renderer = constructor.newInstance();

				for (PropertyDescriptor<?> prop : renderer.getPropertyDescriptors()) {
					String value = properties.getProperty(prop.name());
					if (value != null) {
						@SuppressWarnings("unchecked")
						PropertyDescriptor<Object> prop2 = (PropertyDescriptor<Object>) prop;
						Object valueFrom = prop2.valueFrom(value);
						renderer.setProperty(prop2, valueFrom);
					}
				}
			}
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Unable to construct report renderer class: " + e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to construct report renderer class: " + e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(
					"Unable to construct report renderer class: " + e.getTargetException().getLocalizedMessage());
		}
		// Warn about legacy report format usages
		if (REPORT_FORMAT_TO_RENDERER.containsKey(reportFormat) && !reportFormat.equals(renderer.getName())) {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning("Report format '" + reportFormat + "' is deprecated, and has been replaced with '" + renderer.getName()
						+ "'. Future versions of PMD will remove support for this deprecated Report format usage.");
			}
		}
		return renderer;
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Renderer> getRendererClass(String reportFormat) {
		Class<? extends Renderer> rendererClass = REPORT_FORMAT_TO_RENDERER.get(reportFormat);

		// Look up a custom renderer class
		if (rendererClass == null && !"".equals(reportFormat)) {
			try {
				Class<?> clazz = Class.forName(reportFormat);
				if (!Renderer.class.isAssignableFrom(clazz)) {
					throw new IllegalArgumentException(
							"Custom report renderer class does not implement the " + Renderer.class.getName() + " interface.");
				} else {
					rendererClass = (Class<? extends Renderer>) clazz;
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Can't find the custom format " + reportFormat + ": " + e);
			}
		}
		return rendererClass;
	}

	private static Constructor<? extends Renderer> getRendererConstructor(Class<? extends Renderer> rendererClass) {
		Constructor<? extends Renderer> constructor = null;

		// 1) Properties constructor? - deprecated
		try {
			constructor = rendererClass.getConstructor(Properties.class);
			if (!Modifier.isPublic(constructor.getModifiers())) {
				constructor = null;
			}
		} catch (NoSuchMethodException e) {
			// Ok
		}

		// 2) No-arg constructor?
		try {
			constructor = rendererClass.getConstructor();
			if (!Modifier.isPublic(constructor.getModifiers())) {
				constructor = null;
			}
		} catch (NoSuchMethodException e2) {
			// Ok
		}

		if (constructor == null) {
			throw new IllegalArgumentException(
					"Unable to find either a public java.util.Properties or no-arg constructors for Renderer class: "
							+ rendererClass.getName());
		}
		return constructor;
	}
}
