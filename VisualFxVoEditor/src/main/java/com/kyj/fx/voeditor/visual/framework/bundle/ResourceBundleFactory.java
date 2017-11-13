/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.util
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 다국어 정보를 처리할 수 있는 번들을 리턴한다.
 *
 * @author KYJ
 *
 */
public class ResourceBundleFactory {

	/**
	 * 번들을 생성하는 코어 로직.
	 * 
	 * @최초생성일 2016. 8. 12.
	 */
	private static DefaultLanguageInitializer loader = new DefaultLanguageInitializer();

	private static ResourceBundle bundle;

	public static ResourceBundle getBundle() throws IOException, Exception {

		if (bundle != null)
			return bundle;

		bundle = new DefaultPropertyResourceBundle(loader.load());
		return bundle;
	}

	/**
	 *
	 * Copied PropertyResourceBundle.java
	 *
	 * @author KYJ
	 *
	 */
	static class DefaultPropertyResourceBundle extends ResourceBundle {

		/**
		 * Creates a property resource bundle from an {@link java.io.InputStream
		 * InputStream}. The property file read with this constructor must be
		 * encoded in ISO-8859-1.
		 *
		 * @param stream
		 *            an InputStream that represents a property file to read
		 *            from.
		 * @throws IOException
		 *             if an I/O error occurs
		 * @throws NullPointerException
		 *             if <code>stream</code> is null
		 * @throws IllegalArgumentException
		 *             if {@code stream} contains a malformed Unicode escape
		 *             sequence.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public DefaultPropertyResourceBundle(Properties properties) throws IOException {
			lookup = new HashMap(properties);
		}

		/*
		 * @inheritDoc
		 */
		@Override
		public boolean containsKey(String key) {
			return true;
		}

		/*
		 * @inheritDoc
		 */
		@Override
		public Object handleGetObject(String key) {
			if (key == null) {
				return "";
			}

			Object result = null;

			try {
				result = lookup.get(key);
			} catch (Exception e) {
				;
			}

			return (result == null) ? key : result;
		}

		/**
		 * Returns an <code>Enumeration</code> of the keys contained in
		 * this <code>ResourceBundle</code> and its parent bundles.
		 *
		 * @return an <code>Enumeration</code> of the keys contained in
		 *         this <code>ResourceBundle</code> and its parent bundles.
		 * @see #keySet()
		 */
		public Enumeration<String> getKeys() {
			ResourceBundle parent = this.parent;
			return new GargoyeResourceBundleEnumeration(lookup.keySet(), (parent != null) ? parent.getKeys() : null);
		}

		/**
		 *  기존 사용하던 API 내용 구현 ResourceBundleEnumeration
		 *  
		 * @author KYJ
		 *
		 */
		static class GargoyeResourceBundleEnumeration implements Enumeration<String> {
			Set<String> set;
			Iterator<String> iterator;
			Enumeration<String> enumeration; // may remain null

			/**
		     * Constructs a resource bundle enumeration.
		     * @param set an set providing some elements of the enumeration
		     * @param enumeration an enumeration providing more elements of the enumeration.
		     *        enumeration may be null.
		     */
		    public GargoyeResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
		        this.set = set;
		        this.iterator = set.iterator();
		        this.enumeration = enumeration;
		    }

			String next = null;

			public boolean hasMoreElements() {
				if (next == null) {
					if (iterator.hasNext()) {
						next = iterator.next();
					} else if (enumeration != null) {
						while (next == null && enumeration.hasMoreElements()) {
							next = enumeration.nextElement();
							if (set.contains(next)) {
								next = null;
							}
						}
					}
				}
				return next != null;
			}

			public String nextElement() {
				if (hasMoreElements()) {
					String result = next;
					next = null;
					return result;
				} else {
					throw new NoSuchElementException();
				}
			}
		}

		/**
		 * Returns a <code>Set</code> of the keys contained <em>only</em> in
		 * this <code>ResourceBundle</code>.
		 *
		 * @return a <code>Set</code> of the keys contained only in this
		 *         <code>ResourceBundle</code>
		 * @since 1.6
		 * @see #keySet()
		 */
		protected Set<String> handleKeySet() {
			return lookup.keySet();
		}

		// ==================privates====================

		private Map<String, Object> lookup;
	}

}
