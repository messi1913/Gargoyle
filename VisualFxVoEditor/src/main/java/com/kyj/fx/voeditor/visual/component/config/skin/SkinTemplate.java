/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.skin
 *	작성일   : 2016. 12. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

/**
 *
 * Custom Skin을 생성하기 위한 템플릿.
 * @author KYJ
 *
 */
final public class SkinTemplate {

	public static String skinTemplate;

	public static String KEY_TAB = "tab-gargoyle";
	public static String KEY_HBOX = "hbox-gargoyle";
	public static String MENU_BAR = "menubar-gargoyle";
	public static String MENU_TAB_SELECTED = "tab-selected-gargoyle";
	public static String MENU_TAB_UNSELECTED = "tab-unselected-gargoyle";

	public static final String getSkinTemplate() {
		return skinTemplate;
	}

	static {
		StringBuffer sb = new StringBuffer();
		sb.append("/* JavaFX CSS - Generated Gargoyle Skin Template */\n");
		sb.append("\n");
		sb.append(".table-row-cell:empty {\n");
		sb.append("    -fx-background-color: #FFFFFF;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".hbox-gargoyle\n");
		sb.append("{\n");
		sb.append("	-fx-padding: 5.0px;\n");
		sb.append("	-fx-background-color: $hbox-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".hbox-gargoyle Label\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill:#FFFFFF;\n");
		sb.append("	-fx-fill:#FFFFFF;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".hbox-gargoyle CheckBox\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill:#FFFFFF;\n");
		sb.append("	-fx-fill:#FFFFFF;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".hbox-gargoyle > .radio-button > .text\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill:#FFFFFF;\n");
		sb.append("	-fx-fill:#FFFFFF;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".menu-bar{\n");
		sb.append("    -fx-background-color: $menubar-gargoyle;\n");
		sb.append("}\n");
		sb.append(".menu-bar HBox{\n");
		sb.append("    -fx-background-color: $menubar-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".context-menu {\n");
		sb.append("    -fx-background-color: $menubar-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".menu-bar .label {\n");
		sb.append("    -fx-text-fill: black;\n");
		sb.append("    -fx-opacity: 1.0;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("#tabPanWorkspace BorderPane\n");
		sb.append("{\n");
		sb.append("	-fx-background-color: $menubar-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("#tabPanWorkspace TableView\n");
		sb.append("{\n");
		sb.append("	-fx-background-color: $menubar-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("#borConfigMain BorderPane\n");
		sb.append("{\n");
		sb.append("	-fx-background-color: #F4F4F4;\n");
		sb.append("}\n");
		sb.append("BorderPane\n");
		sb.append("{\n");
		sb.append("\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("DockTitleBar\n");
		sb.append("{\n");
		sb.append("	-fx-background-color: $menubar-gargoyle;\n");
		sb.append("	-fx-opacity: 1.0;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("DockTitleBar Label\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill:black ;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".console-skin\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill: white ;\n");
		sb.append("	-fx-highlight-text-fill:black  ;\n");
		sb.append("	-fx-prompt-text-fill: yellow;\n");
		sb.append("	-fx-highlight-fill: $menubar-gargoyle;\n");
		sb.append("	-fx-font-size:15.0px;\n");
		sb.append("	-fx-wrap-text: true;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".console-skin .content\n");
		sb.append("{\n");
		sb.append("	-fx-background-color: $hbox-gargoyle;\n");
		sb.append("	-fx-text-fill:white ;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("\n");
		sb.append(".tab{\n");
		sb.append("	-fx-background-color: $tab-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".tab:top .tab-container .tab-label{\n");
		sb.append("	-fx-opacity: 0.5;\n");
		sb.append("	-fx-text-fill: $tab-unselected-gargoyle ;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".tab:selected:top .tab-container .tab-label{\n");
		sb.append("	-fx-opacity: 1.0;\n");
		sb.append("	-fx-text-fill: $tab-selected-gargoyle;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append(".column-header .label\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill:black;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".fiile-tree-item{\n");
		sb.append("	-fx-background-color: transparent ;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append(".fiile-tree-item Label\n");
		sb.append("{\n");
		sb.append("	-fx-text-fill:black;\n");
		sb.append("}\n");

		skinTemplate = sb.toString();
	}

}
