/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

/**
 * @author Hong
 *
 */
public class Filter3Lower implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kyj.fx.voeditor.visual.example.Filter#doFilter(int)
	 */
	@Override
	public int doFilter(int i) {
		if (i < 3) {
			System.out.println(i);
			return i;
		}
		return -1;
	}

}
