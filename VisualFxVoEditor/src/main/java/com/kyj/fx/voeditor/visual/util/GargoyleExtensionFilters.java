/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 8. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public interface GargoyleExtensionFilters {

	static final String EXTENSION_COMMONS = "*.";

	public static final String ALL_NAME = "All files";
	public static final String ALL = "*.*";


	public static final String FX_CSS_NAME = "CSS files (*.css)";
	public static final String FX_CSS_EXTENSION = "css";
	public static final String FX_CSS = EXTENSION_COMMONS + FX_CSS_EXTENSION;

	public static final String EXE_NAME = "exe files (*.exe)";
	public static final String EXE_EXTENSION = "exe";
	public static final String EXE = EXTENSION_COMMONS + EXE_EXTENSION;


	public static final String SQL_NAME = "SQL files (*.sql)";
	public static final String SQL_EXTENSION = "sql";
	public static final String SQL = EXTENSION_COMMONS + SQL_EXTENSION;

	public static final String XLS_NAME = "Excel files (*.xls)";
	public static final String XLS_EXTENSION = "xls";
	public static final String XLS = EXTENSION_COMMONS + XLS_EXTENSION;


	public static final String XLSX_NAME = "Excel files (*.xlsx)";
	public static final String XLSX_EXTENSION = EXTENSION_COMMONS + "xlsx";
	public static final String XLSX = EXTENSION_COMMONS + XLSX_EXTENSION;

	public static final String DOCX_NAME = "Doc files (*.docx)";
	public static final String DOCX_EXTENSION = "docx";
	public static final String DOCX = EXTENSION_COMMONS + DOCX_EXTENSION;

	public static final String DOC_NAME = "Doc files (*.doc)";
	public static final String DOC_EXTENSION = "*.doc";
	public static final String DOC = EXTENSION_COMMONS + DOC_EXTENSION;

	public static final String PROPERTIES_NAME = "Property files (*.properties)";
	public static final String PROPERTIES_EXTENSION = "properties";
	public static final String PROPERTIES = EXTENSION_COMMONS+ PROPERTIES_EXTENSION;

	public static final String FONT_NAME = "Font files (*.ttf)";
	public static final String FONT_EXTENSION = "ttf";
	public static final String FONT = EXTENSION_COMMONS + FONT_EXTENSION;

}
