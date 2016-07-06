/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

/**
 * @author Hong
 *
 */
public class JJacSu implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kyj.fx.voeditor.visual.example.Filter#doFilter(int)
	 */
	@Override
	public int doFilter(int i) {

		if ((i % 2 == 0)) {
			return i;
		}

		// 잘못된 값인경우 -1
		return -1;
	}
}
