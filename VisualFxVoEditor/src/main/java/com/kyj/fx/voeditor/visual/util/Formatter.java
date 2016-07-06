package com.kyj.fx.voeditor.visual.util;

/**
 * Formatter contract
 *
 * @author Steve Ebersole
 */
public interface Formatter {
	/**
	 * Format the source SQL string.
	 *
	 * @param source
	 *            The original SQL string
	 *
	 * @return The formatted version
	 */
	public String format(String source);

	public String toUpperCase(String source);

	public String toLowerCase(String source);

	public String split(String source, int position);

}
