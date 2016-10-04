/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.pmd
 *	작성일   : 2016. 9. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jfxtras.scene.layout.HBox;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;
import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;
import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.util.StringUtil;

public class DesignerFx implements ClipboardOwner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesignerFx.class);

	/**
	 * @최초생성일 2016. 9. 30.
	 */
	private static final Font FONT = javafx.scene.text.Font.font("Verdana", FontWeight.NORMAL, 16);

	static {
		Initializer.initialize();
	}

	//	private static final int DEFAULT_LANGUAGE_VERSION_SELECTION_INDEX = getDefaultLanguageVersionSelectionIndex();

	private int getDefaultLanguageVersionSelectionIndex() {
		return Arrays.asList(getSupportedLanguageVersions()).indexOf(LanguageRegistry.getLanguage("Java").getDefaultVersion());
	}

	private final CodeEditorTextPaneFx codeEditorPane = new CodeEditorTextPaneFx();
	//	private final TextArea codeEditorPane = new TextArea();

	private final TreeWidget astTreeWidget = new TreeWidget(new Object[0]);

	private DefaultListModel xpathResults = new DefaultListModel();
	private final JList xpathResultList = new JList(xpathResults);

	private final TextArea xpathQueryArea = new TextArea();
	//	private final ButtonGroup xpathVersionButtonGroup = new ButtonGroup();
	private final ToggleGroup xpathVersionButtonGroup = new ToggleGroup();

	private final TreeWidget symbolTableTreeWidget = new TreeWidget(new Object[0]);
	//	private final JFrame frame = new JFrame("PMD Rule Designer (v " + PMD.VERSION + ')');
	private final DFAPanelFx dfaPanel; //= new DFAPanel();
	//	private final SwingNode dfaPanelFx;

	private final RadioMenuItem[] languageVersionMenuItems = new RadioMenuItem[getSupportedLanguageVersions().length];

	private SplitPane containerSplitPane;
	private JMenuBar menuBar;
	private MenuBar createFxMenuBar;

	public DesignerFx(String[] args) {

		xpathQueryArea.setFont(FONT);

		SplitPane controlSplitPane = new SplitPane(createCodeEditorPanel(), createXPathQueryPanel());
		controlSplitPane.setOrientation(Orientation.HORIZONTAL);
		SplitPane astAndSymbolTablePane = new SplitPane(createASTPanel(), createSymbolTableResultPanel());
		astAndSymbolTablePane.setOrientation(Orientation.VERTICAL);
		astAndSymbolTablePane.setDividerPositions(1, 1);

		SplitPane resultsSplitPane = new SplitPane(astAndSymbolTablePane, createXPathResultPanel());
		resultsSplitPane.setOrientation(Orientation.HORIZONTAL);
		TabPane tabbed = new TabPane();

		dfaPanel = new DFAPanelFx();
		//		dfaPanelFx = new SwingNode();
		//		createSwingContent(dfaPanelFx, dfaPanel);

		tabbed.getTabs().add(new Tab("Abstract Syntax Tree / XPath / Symbol Table", resultsSplitPane));
		tabbed.getTabs().add(new Tab("Data Flow Analysis", dfaPanel));

		containerSplitPane = new SplitPane(controlSplitPane, tabbed);
		containerSplitPane.setOrientation(Orientation.VERTICAL);
		createFxMenuBar = createFxMenuBar();

		//		controlSplitPane.setDividerPosition(0, 0.5);
		//		containerSplitPane.setDividerPosition(0, 0.5);
		//		astAndSymbolTablePane.setDividerPosition(0, 0.5);
		//		astAndSymbolTablePane.setDividerPositions(0, 0.5);
		//		resultsSplitPane.setDividerPositions(0, 0.5);

		loadSettings();
	}

	private Node getCompilationUnit() {
		LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler();
		return getCompilationUnit(languageVersionHandler);
	}

	static Node getCompilationUnit(LanguageVersionHandler languageVersionHandler, String code) {
		Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
		Node node = parser.parse(null, new StringReader(code));
		languageVersionHandler.getSymbolFacade().start(node);
		languageVersionHandler.getTypeResolutionFacade(DesignerFx.class.getClassLoader()).start(node);
		return node;
	}

	private Node getCompilationUnit(LanguageVersionHandler languageVersionHandler) {
		return getCompilationUnit(languageVersionHandler, codeEditorPane.getText());
	}

	private static LanguageVersion[] getSupportedLanguageVersions() {
		List<LanguageVersion> languageVersions = new ArrayList<>();
		for (LanguageVersion languageVersion : LanguageRegistry.findAllVersions()) {
			LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();

			if (languageVersionHandler != null) {
				Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
				if (parser != null && parser.canParse()) {
					languageVersions.add(languageVersion);
					LOGGER.debug("support parser: {}", parser.toString());
				} else {
					LOGGER.debug("not support parser: {}", parser.toString());
				}
			} else {
				LOGGER.debug("not support parser (handler is null): {}", languageVersion.toString());
			}
		}
		return languageVersions.toArray(new LanguageVersion[languageVersions.size()]);
	}

	private LanguageVersion getLanguageVersion() {
		return getSupportedLanguageVersions()[selectedLanguageVersionIndex()];
	}

	private void setLanguageVersion(LanguageVersion languageVersion) {
		if (languageVersion != null) {
			LanguageVersion[] versions = getSupportedLanguageVersions();
			for (int i = 0; i < versions.length; i++) {
				LanguageVersion version = versions[i];
				if (languageVersion.equals(version)) {
					languageVersionMenuItems[i].setSelected(true);
					break;
				}
			}
		}
	}

	private int selectedLanguageVersionIndex() {
		for (int i = 0; i < languageVersionMenuItems.length; i++) {
			if (languageVersionMenuItems[i].isSelected()) {
				return i;
			}
		}
		throw new RuntimeException("Initial default language version not specified");
	}

	private LanguageVersionHandler getLanguageVersionHandler() {
		LanguageVersion languageVersion = getLanguageVersion();
		return languageVersion.getLanguageVersionHandler();
	}

	private class ExceptionNode implements TreeNode {

		private Object item;
		private ExceptionNode[] kids;

		public ExceptionNode(Object theItem) {
			item = theItem;

			if (item instanceof ParseException) {
				createKids();
			}
		}

		// each line in the error message becomes a separate tree node
		private void createKids() {

			String message = ((ParseException) item).getMessage();
			String[] lines = StringUtil.substringsOf(message, PMD.EOL);

			kids = new ExceptionNode[lines.length];
			for (int i = 0; i < lines.length; i++) {
				kids[i] = new ExceptionNode(lines[i]);
			}
		}

		public int getChildCount() {
			return kids == null ? 0 : kids.length;
		}

		public boolean getAllowsChildren() {
			return false;
		}

		public boolean isLeaf() {
			return kids == null;
		}

		public TreeNode getParent() {
			return null;
		}

		public TreeNode getChildAt(int childIndex) {
			return kids[childIndex];
		}

		public String label() {
			return item.toString();
		}

		public Enumeration<TreeNode> children() {
			Enumeration<TreeNode> e = new Enumeration<TreeNode>() {
				int i = 0;

				public boolean hasMoreElements() {
					return kids != null && i < kids.length;
				}

				public ExceptionNode nextElement() {
					return kids[i++];
				}
			};
			return e;
		}

		public int getIndex(TreeNode node) {
			for (int i = 0; i < kids.length; i++) {
				if (kids[i] == node) {
					return i;
				}
			}
			return -1;
		}
	}

	// Tree node that wraps the AST node for the tree widget and
	// any possible children they may have.
	private class ASTTreeNode implements TreeNode {

		private Node node;
		private ASTTreeNode parent;
		private ASTTreeNode[] kids;

		public ASTTreeNode(Node theNode) {
			node = theNode;

			Node parent = node.jjtGetParent();
			if (parent != null) {
				this.parent = new ASTTreeNode(parent);
			}
		}

		private ASTTreeNode(ASTTreeNode parent, Node theNode) {
			node = theNode;
			this.parent = parent;
		}

		public int getChildCount() {
			return node.jjtGetNumChildren();
		}

		public boolean getAllowsChildren() {
			return false;
		}

		public boolean isLeaf() {
			return node.jjtGetNumChildren() == 0;
		}

		public TreeNode getParent() {
			return parent;
		}

		public Scope getScope() {
			if (node instanceof ScopedNode) {
				return ((ScopedNode) node).getScope();
			}
			return null;
		}

		public Enumeration<TreeNode> children() {

			if (getChildCount() > 0) {
				getChildAt(0); // force it to build kids
			}

			Enumeration<TreeNode> e = new Enumeration<TreeNode>() {
				int i = 0;

				public boolean hasMoreElements() {
					return kids != null && i < kids.length;
				}

				public ASTTreeNode nextElement() {
					return kids[i++];
				}
			};
			return e;
		}

		public TreeNode getChildAt(int childIndex) {

			if (kids == null) {
				kids = new ASTTreeNode[node.jjtGetNumChildren()];
				for (int i = 0; i < kids.length; i++) {
					kids[i] = new ASTTreeNode(this.parent, node.jjtGetChild(i));
				}
			}
			return kids[childIndex];
		}

		public int getIndex(TreeNode node) {

			for (int i = 0; i < kids.length; i++) {
				if (kids[i] == node) {
					return i;
				}
			}
			return -1;
		}

		public String label() {
			LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler();
			StringWriter writer = new StringWriter();
			languageVersionHandler.getDumpFacade(writer, "", false).start(node);
			return writer.toString();
		}

		public String getToolTipText() {
			String tooltip = "Line: " + node.getBeginLine() + " Column: " + node.getBeginColumn();
			tooltip += " " + label();
			return tooltip;
		}

		public List<String> getAttributes() {
			List<String> result = new LinkedList<String>();
			AttributeAxisIterator attributeAxisIterator = new AttributeAxisIterator(node);
			while (attributeAxisIterator.hasNext()) {
				Attribute attribute = attributeAxisIterator.next();
				result.add(attribute.getName() + "=" + attribute.getStringValue());
			}
			return result;
		}
	}

	private TreeCellRenderer createNoImageTreeCellRenderer() {
		DefaultTreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer();
		treeCellRenderer.setLeafIcon(null);
		treeCellRenderer.setOpenIcon(null);
		treeCellRenderer.setClosedIcon(null);
		return treeCellRenderer;
	}

	// Special tree variant that knows how to retrieve node labels and
	// provides the ability to expand all nodes at once.
	private class TreeWidget extends JTree {

		private static final long serialVersionUID = 1L;

		public TreeWidget(Object[] items) {
			super(items);
			setToolTipText("");
		}

		@Override
		public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			if (value == null) {
				return "";
			}
			if (value instanceof ASTTreeNode) {
				return ((ASTTreeNode) value).label();
			}
			if (value instanceof ExceptionNode) {
				return ((ExceptionNode) value).label();
			}
			return value.toString();
		}

		@Override
		public String getToolTipText(MouseEvent e) {
			if (getRowForLocation(e.getX(), e.getY()) == -1) {
				return null;
			}
			TreePath curPath = getPathForLocation(e.getX(), e.getY());
			if (curPath.getLastPathComponent() instanceof ASTTreeNode) {
				return ((ASTTreeNode) curPath.getLastPathComponent()).getToolTipText();
			} else {
				return super.getToolTipText(e);
			}
		}

		public void expandAll(boolean expand) {
			TreeNode root = (TreeNode) getModel().getRoot();
			expandAll(new TreePath(root), expand);
		}

		private void expandAll(TreePath parent, boolean expand) {
			// Traverse children
			TreeNode node = (TreeNode) parent.getLastPathComponent();
			if (node.getChildCount() >= 0) {
				for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
					TreeNode n = e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					expandAll(path, expand);
				}
			}

			if (expand) {
				expandPath(parent);
			} else {
				collapsePath(parent);
			}
		}
	}

	private void loadASTTreeData(TreeNode rootNode) {
		astTreeWidget.setModel(new DefaultTreeModel(rootNode));
		astTreeWidget.setRootVisible(true);
		astTreeWidget.expandAll(true);
	}

	private void loadSymbolTableTreeData(TreeNode rootNode) {
		if (rootNode != null) {
			symbolTableTreeWidget.setModel(new DefaultTreeModel(rootNode));
			symbolTableTreeWidget.expandAll(true);
		} else {
			symbolTableTreeWidget.setModel(null);
		}
	}

	private class ShowListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			TreeNode tn;
			try {
				Node lastCompilationUnit = getCompilationUnit();
				tn = new ASTTreeNode(lastCompilationUnit);
			} catch (ParseException pe) {
				tn = new ExceptionNode(pe);
			}

			loadASTTreeData(tn);
			loadSymbolTableTreeData(null);
		}
	}

	private class DFAListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {

			LanguageVersion languageVersion = getLanguageVersion();
			DFAGraphRule dfaGraphRule = languageVersion.getLanguageVersionHandler().getDFAGraphRule();
			RuleSet rs = new RuleSet();
			if (dfaGraphRule != null) {
				rs.addRule(dfaGraphRule);
			}
			RuleContext ctx = new RuleContext();
			ctx.setSourceCodeFilename("[no filename]." + languageVersion.getLanguage().getExtensions().get(0));
			StringReader reader = new StringReader(codeEditorPane.getText());
			PMDConfiguration config = new PMDConfiguration();
			config.setDefaultLanguageVersion(languageVersion);

			try {
				new SourceCodeProcessor(config).processSourceCode(reader, new RuleSets(rs), ctx);
				//	    } catch (PMDException pmde) {
				//		loadTreeData(new ExceptionNode(pmde));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (dfaGraphRule != null) {
				List<DFAGraphMethod> methods = dfaGraphRule.getMethods();
				if (methods != null && !methods.isEmpty()) {
					dfaPanel.resetTo(methods, codeEditorPane);
					dfaPanel.repaint();
				}
			}
		}
	}

	//	private class XPathListener implements ActionListener {
	//		public void actionPerformed(ActionEvent ae) {
	//			xpathResults.clear();
	//			if (StringUtil.isEmpty(xpathQueryArea.getText())) {
	//				xpathResults.addElement("XPath query field is empty.");
	//				xpathResultList.repaint();
	//				codeEditorPane.requestFocus();
	//				return;
	//			}
	//			Node c = getCompilationUnit();
	//			try {
	//				XPathRule xpathRule = new XPathRule() {
	//					@Override
	//					public void addViolation(Object data, Node node, String arg) {
	//						xpathResults.addElement(node);
	//					}
	//				};
	//				xpathRule.setMessage("");
	//				xpathRule.setLanguage(getLanguageVersion().getLanguage());
	//				xpathRule.setXPath(xpathQueryArea.getText());
	//				Object userData = xpathVersionButtonGroup.getSelectedToggle().getUserData();
	//				xpathRule.setVersion(userData.toString());
	//				//				xpathRule.setVersion(xpathVersionButtonGroup.getSelection().getActionCommand());
	//
	//				RuleSet ruleSet = new RuleSet();
	//				ruleSet.addRule(xpathRule);
	//
	//				RuleSets ruleSets = new RuleSets(ruleSet);
	//
	//				RuleContext ruleContext = new RuleContext();
	//				ruleContext.setLanguageVersion(getLanguageVersion());
	//
	//				List<Node> nodes = new ArrayList<Node>();
	//				nodes.add(c);
	//				ruleSets.apply(nodes, ruleContext, xpathRule.getLanguage());
	//
	//				if (xpathResults.isEmpty()) {
	//					xpathResults.addElement("No matching nodes " + System.currentTimeMillis());
	//				}
	//			} catch (ParseException pe) {
	//				xpathResults.addElement(pe.fillInStackTrace().getMessage());
	//			}
	//			xpathResultList.repaint();
	//			//			xpathQueryArea.requestFocus();
	//		}
	//	}
	private class XPathListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			xpathResults.clear();
			if (StringUtil.isEmpty(xpathQueryArea.getText())) {
				xpathResults.addElement("XPath query field is empty.");
				xpathResultList.repaint();
				codeEditorPane.requestFocus();
				return;
			}
			Node c = getCompilationUnit();
			try {
				XPathRule xpathRule = new XPathRule() {
					@Override
					public void addViolation(Object data, Node node, String arg) {
						xpathResults.addElement(node);
					}
				};
				xpathRule.setMessage("");
				xpathRule.setLanguage(getLanguageVersion().getLanguage());
				xpathRule.setXPath(xpathQueryArea.getText());
				//				xpathRule.setVersion(xpathVersionButtonGroup.getSelection().getActionCommand());
				Object userData = xpathVersionButtonGroup.getSelectedToggle().getUserData();
				xpathRule.setVersion(userData.toString());

				RuleSet ruleSet = new RuleSet();
				ruleSet.addRule(xpathRule);

				RuleSets ruleSets = new RuleSets(ruleSet);

				RuleContext ruleContext = new RuleContext();
				ruleContext.setLanguageVersion(getLanguageVersion());

				List<Node> nodes = new ArrayList<>();
				nodes.add(c);
				ruleSets.apply(nodes, ruleContext, xpathRule.getLanguage());

				if (xpathResults.isEmpty()) {
					xpathResults.addElement("No matching nodes " + System.currentTimeMillis());
				}
			} catch (ParseException pe) {
				xpathResults.addElement(pe.fillInStackTrace().getMessage());
			}
			xpathResultList.repaint();
			xpathQueryArea.requestFocus();
		}
	}

	private class SymbolTableListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			if (e.getNewLeadSelectionPath() != null) {
				ASTTreeNode astTreeNode = (ASTTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

				DefaultMutableTreeNode symbolTableTreeNode = new DefaultMutableTreeNode();
				DefaultMutableTreeNode selectedAstTreeNode = new DefaultMutableTreeNode("AST Node: " + astTreeNode.label());
				symbolTableTreeNode.add(selectedAstTreeNode);

				List<Scope> scopes = new ArrayList<Scope>();
				Scope scope = astTreeNode.getScope();
				while (scope != null) {
					scopes.add(scope);
					scope = scope.getParent();
				}
				Collections.reverse(scopes);
				for (int i = 0; i < scopes.size(); i++) {
					scope = scopes.get(i);
					DefaultMutableTreeNode scopeTreeNode = new DefaultMutableTreeNode("Scope: " + scope.getClass().getSimpleName());
					selectedAstTreeNode.add(scopeTreeNode);
					for (Map.Entry<NameDeclaration, List<NameOccurrence>> entry : scope.getDeclarations().entrySet()) {
						DefaultMutableTreeNode nameDeclarationTreeNode = new DefaultMutableTreeNode(
								entry.getKey().getClass().getSimpleName() + ": " + entry.getKey());
						scopeTreeNode.add(nameDeclarationTreeNode);
						for (NameOccurrence nameOccurrence : entry.getValue()) {
							DefaultMutableTreeNode nameOccurranceTreeNode = new DefaultMutableTreeNode(
									"Name occurrence: " + nameOccurrence);
							nameDeclarationTreeNode.add(nameOccurranceTreeNode);
						}
					}
				}

				List<String> attributes = astTreeNode.getAttributes();
				DefaultMutableTreeNode attributesNode = new DefaultMutableTreeNode("Attributes (accessible via XPath):");
				selectedAstTreeNode.add(attributesNode);
				for (String attribute : attributes) {
					attributesNode.add(new DefaultMutableTreeNode(attribute));
				}

				loadSymbolTableTreeData(symbolTableTreeNode);
			}
		}
	}

	private class CodeHighlightListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			if (e.getNewLeadSelectionPath() != null) {
				ASTTreeNode selected = (ASTTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
				if (selected != null) {
					codeEditorPane.select(selected.node);
				}
			}
		}
	}

	private class ASTListCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			String text;
			if (value instanceof Node) {
				Node node = (Node) value;
				StringBuffer sb = new StringBuffer();
				String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
				if (Proxy.isProxyClass(value.getClass())) {
					name = value.toString();
				}
				sb.append(name).append(" at line ").append(node.getBeginLine()).append(" column ").append(node.getBeginColumn())
						.append(PMD.EOL);
				text = sb.toString();
			} else {
				text = value.toString();
			}
			setText(text);
			return this;
		}
	}

	private class ASTSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!lsm.isSelectionEmpty()) {
				Object o = xpathResults.get(lsm.getMinSelectionIndex());
				if (o instanceof Node) {
					codeEditorPane.select((Node) o);
				}
			}
		}
	}

	public SplitPane getJSplitPane() {
		return containerSplitPane;
	}

	public JMenuBar getJMenuBar() {
		return menuBar;
	}

	public MenuBar getFxMenuBar() {
		return createFxMenuBar;
	}

	//	private JMenuBar createMenuBar() {
	//		JMenuBar menuBar = new JMenuBar();
	//		JMenu menu = new JMenu("Language");
	//		ButtonGroup group = new ButtonGroup();
	//
	//		LanguageVersion[] languageVersions = getSupportedLanguageVersions();
	//		for (int i = 0; i < languageVersions.length; i++) {
	//			LanguageVersion languageVersion = languageVersions[i];
	//			MenuItem button = new MenuItem(languageVersion.getShortName());
	//			languageVersionMenuItems[i] = button;
	//			group.add(button);
	//			menu.add(button);
	//		}
	//		languageVersionMenuItems[DEFAULT_LANGUAGE_VERSION_SELECTION_INDEX].setSelected(true);
	//		menuBar.add(menu);
	//
	//		JMenu actionsMenu = new JMenu("Actions");
	//		JMenuItem copyXMLItem = new JMenuItem("Copy xml to clipboard");
	//		copyXMLItem.addActionListener(new ActionListener() {
	//			public void actionPerformed(ActionEvent e) {
	//				copyXmlToClipboard();
	//			}
	//		});
	//		actionsMenu.add(copyXMLItem);
	//		JMenuItem createRuleXMLItem = new JMenuItem("Create rule XML");
	//		createRuleXMLItem.addActionListener(new ActionListener() {
	//			public void actionPerformed(ActionEvent e) {
	//				createRuleXML();
	//			}
	//		});
	//		actionsMenu.add(createRuleXMLItem);
	//		menuBar.add(actionsMenu);
	//
	//		return menuBar;
	//	}

	private MenuBar createFxMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Language");

		ToggleGroup group = new ToggleGroup();

		LanguageVersion[] languageVersions = getSupportedLanguageVersions();
		for (int i = 0; i < languageVersions.length; i++) {
			LanguageVersion languageVersion = languageVersions[i];

			//			JRadioButtonMenuItem button = new JRadioButtonMenuItem(languageVersion.getShortName());
			RadioMenuItem button = new RadioMenuItem(languageVersion.getShortName());
			button.setToggleGroup(group);
			languageVersionMenuItems[i] = button;
			//			group.add(button);
			menu.getItems().add(button);
		}
		//		languageVersionMenuItems[DEFAULT_LANGUAGE_VERSION_SELECTION_INDEX].setSelected(true);
		languageVersionMenuItems[getDefaultLanguageVersionSelectionIndex()].setSelected(true);

		menuBar.getMenus().add(menu);

		Menu actionsMenu = new Menu("Actions");
		MenuItem copyXMLItem = new MenuItem("Copy xml to clipboard");
		copyXMLItem.setOnAction(e -> copyXmlToClipboard());

		actionsMenu.getItems().add(copyXMLItem);
		MenuItem createRuleXMLItem = new MenuItem("Create rule XML");
		createRuleXMLItem.setOnAction(e -> createRuleXML());
		actionsMenu.getItems().add(createRuleXMLItem);
		menuBar.getMenus().add(actionsMenu);

		return menuBar;

	}

	private void createRuleXML() {

		CreateXMLRulePanelFx rulePanel = new CreateXMLRulePanelFx(xpathQueryArea, codeEditorPane);
		JFrame xmlframe = new JFrame("Create XML Rule");
		xmlframe.setContentPane(rulePanel);
		xmlframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		xmlframe.setSize(new Dimension(600, 700));
		xmlframe.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				JFrame tmp = (JFrame) e.getSource();
				if (tmp.getWidth() < 600 || tmp.getHeight() < 700) {
					tmp.setSize(600, 700);
				}
			}
		});
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		xmlframe.pack();
		xmlframe.setLocation((screenWidth - xmlframe.getWidth()) / 2, (screenHeight - xmlframe.getHeight()) / 2);
		xmlframe.setVisible(true);
	}

	private BorderPane createCodeEditorPanel() {

		BorderPane p = new BorderPane();
		//		JPanel p = new JPanel();
		//		p.setLayout(new BorderLayout());

		//		codeEditorPane.setBorder(BorderFactory.createLineBorder(Color.black));
		//		makeTextComponentUndoable(codeEditorPane);

		p.setTop(new Label("Source code:"));
		//		SwingNode fxScrollPane = new SwingNode();
		//		createSwingContent(fxScrollPane, new JScrollPane(codeEditorPane));
		p.setCenter(codeEditorPane);
		//		p.add(new JLabel("Source code:"), BorderLayout.NORTH);
		//		p.add(new JScrollPane(codeEditorPane), BorderLayout.CENTER);

		//		SwingNode swingNode = new SwingNode();
		//		createSwingContent(swingNode, p);
		return p;
	}

	private javafx.scene.Node createASTPanel() {
		astTreeWidget.setCellRenderer(createNoImageTreeCellRenderer());
		TreeSelectionModel model = astTreeWidget.getSelectionModel();
		model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		model.addTreeSelectionListener(new SymbolTableListener());
		model.addTreeSelectionListener(new CodeHighlightListener());
		SwingNode swingNode = new SwingNode();
		createSwingContent(swingNode, new JScrollPane(astTreeWidget));
		return swingNode;
	}

	private javafx.scene.Node createXPathResultPanel() {
		xpathResults.addElement("No XPath results yet, run an XPath Query first.");
		xpathResultList.setBorder(BorderFactory.createLineBorder(Color.black));
		xpathResultList.setFixedCellWidth(300);
		xpathResultList.setCellRenderer(new ASTListCellRenderer());
		xpathResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		xpathResultList.getSelectionModel().addListSelectionListener(new ASTSelectionListener());
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(xpathResultList);

		SwingNode swingNode = new SwingNode();
		createSwingContent(swingNode, scrollPane);
		return swingNode;
	}

	private BorderPane createXPathQueryPanel() {

		BorderPane borderPane = new BorderPane();
		//		JPanel p = new JPanel();
		//		p.setLayout(new BorderLayout());
		//		xpathQueryArea.setBorder(BorderFactory.createLineBorder(Color.black));
		//		makeTextComponentUndoable(xpathQueryArea);

		//		ScrollPane scrollPane = new ScrollPane(xpathQueryArea);
		//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		final Button b = createGoButton();
		BorderPane topBorderPane = new BorderPane();
		//		JPanel topPanel = new JPanel();
		//		topPanel.setLayout(new BorderLayout());
		Label value = new Label("XPath Query (if any):");

		//		value.setPrefSize(Label.USE_COMPUTED_SIZE, Double.MAX_VALUE);
		value.setAlignment(Pos.CENTER_LEFT);
		HBox value2 = new HBox(5, value);
		value2.setPadding(new Insets(5, 5, 5, 5));
		topBorderPane.setLeft(value2);
		//		topPanel.add(new JLabel("XPath Query (if any):"), BorderLayout.WEST);
		//		topPanel.add(createXPathVersionPanel(), BorderLayout.EAST);
		topBorderPane.setRight(createXPathVersionPanel());

		borderPane.setBottom(b);
		//		SwingNode scrollPaneFx = new SwingNode();
		//		createSwingContent(scrollPaneFx, scrollPane);
		borderPane.setCenter(xpathQueryArea);
		//		p.add(topPanel, BorderLayout.NORTH);
		//		p.add(scrollPane, BorderLayout.CENTER);
		borderPane.setTop(topBorderPane);
		//		p.add(b, BorderLayout.SOUTH);

		//		xpathQueryArea.heightProperty().bind(value.heightProperty());
		//		xpathQueryArea.prefHeightProperty().addListener((ob, o, n) -> value.setPrefHeight((double) n));

		//		SwingNode swingNode = new SwingNode();
		//		createSwingContent(swingNode, p);
		return borderPane;
	}

	private void createSwingContent(final SwingNode swingNode, JComponent com) {
		//		SwingUtilities.invokeLater(() -> {
		swingNode.setContent(com);
		//		});
	}

	private javafx.scene.Node createSymbolTableResultPanel() {
		symbolTableTreeWidget.setCellRenderer(createNoImageTreeCellRenderer());

		SwingNode swingNode = new SwingNode();
		JScrollPane jScrollPane = new JScrollPane(symbolTableTreeWidget);
		createSwingContent(swingNode, jScrollPane);
		return swingNode;
	}

	private javafx.scene.Node createXPathVersionPanel() {

		HBox p = new HBox(5);
		p.setPadding(new Insets(5, 5, 5, 5));
		//		JPanel p = new JPanel();
		Label e = new Label("XPath Version:");
		e.setAlignment(Pos.CENTER_LEFT);
		p.add(e);
		for (Object[] values : XPathRule.VERSION_DESCRIPTOR.choices()) {
			//			JRadioButton b = new JRadioButton();
			RadioButton b = new RadioButton((String) values[0]);

			//			b.setText((String) values[0]);
			b.setUserData(b.getText());
			//			b.setActionCommand(b.getText());
			if (values[0].equals(XPathRule.VERSION_DESCRIPTOR.defaultValue())) {
				b.setSelected(true);
			}
			b.setToggleGroup(xpathVersionButtonGroup);
			//			xpathVersionButtonGroup.add(b);
			p.add(b);
		}

		//		SwingNode swingNode = new SwingNode();
		//		createSwingContent(swingNode, p);
		return p;
	}

	private Button createGoButton() {
		Button b = new Button("Go");
		b.setPrefSize(Double.MAX_VALUE, Button.USE_COMPUTED_SIZE);
		//		JButton b = new JButton("Go");

		//		b.pressedProperty().addListener((ob) ->{
		//			System.out.println("zz");
		//		});
		//		b.setMnemonic('g');
		b.addEventHandler(javafx.event.ActionEvent.ACTION, e -> {
			new ShowListener().actionPerformed(null);
			new XPathListener().actionPerformed(null);
			new DFAListener().actionPerformed(null);
			saveSettings();
		});

		//		b.addActionListener(new ShowListener());
		//		b.addActionListener(new XPathListener());
		//		b.addActionListener(new DFAListener());
		//		b.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				saveSettings();
		//			}
		//		});
		return b;
	}

	//	private static void makeTextComponentUndoable(JTextComponent textConponent) {
	//		final UndoManager undoManager = new UndoManager();
	//		textConponent.getDocument().addUndoableEditListener(new UndoableEditListener() {
	//			public void undoableEditHappened(UndoableEditEvent evt) {
	//				undoManager.addEdit(evt.getEdit());
	//			}
	//		});
	//		ActionMap actionMap = textConponent.getActionMap();
	//		InputMap inputMap = textConponent.getInputMap();
	//		actionMap.put("Undo", new AbstractAction("Undo") {
	//			public void actionPerformed(ActionEvent evt) {
	//				try {
	//					if (undoManager.canUndo()) {
	//						undoManager.undo();
	//					}
	//				} catch (CannotUndoException e) {
	//				}
	//			}
	//		});
	//		inputMap.put(KeyStroke.getKeyStroke("control Z"), "Undo");
	//
	//		actionMap.put("Redo", new AbstractAction("Redo") {
	//			public void actionPerformed(ActionEvent evt) {
	//				try {
	//					if (undoManager.canRedo()) {
	//						undoManager.redo();
	//					}
	//				} catch (CannotRedoException e) {
	//				}
	//			}
	//		});
	//		inputMap.put(KeyStroke.getKeyStroke("control Y"), "Redo");
	//	}

	//	public static void main(String[] args) {
	//		new Designer(args);
	//	}

	final void setCodeEditPaneText(String text) {
		codeEditorPane.setText(text);
	}

	private final String getXmlTreeCode() {
		if (codeEditorPane.getText() != null && codeEditorPane.getText().trim().length() > 0) {
			Node cu = getCompilationUnit();
			return getXmlTreeCode(cu);
		}
		return null;
	}

	static final String getXmlTreeCode(Node cu) {
		String xml = null;
		if (cu != null) {
			try {
				xml = getXmlString(cu);
			} catch (TransformerException e) {
				e.printStackTrace();
				xml = "Error trying to construct XML representation";
			}
		}
		return xml;
	}

	private final void copyXmlToClipboard() {
		String xml = getXmlTreeCode();
		if (xml != null) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(xml), this);
		}
	}

	/**
	 * Returns an unformatted xml string (without the declaration)
	 *
	 * @throws TransformerException if the XML cannot be converted to a string
	 */
	private static String getXmlString(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();

		Source source = new DOMSource(node.getAsDocument());
		Result result = new StreamResult(writer);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer xformer = transformerFactory.newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.transform(source, result);

		return writer.toString();
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	private static final String SETTINGS_FILE_NAME = System.getProperty("user.home") + System.getProperty("file.separator")
			+ ".pmd_designer.xml";

	private void loadSettings() {
		try {
			File file = new File(SETTINGS_FILE_NAME);
			if (file.exists()) {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = builder.parse(new FileInputStream(file));
				Element settingsElement = document.getDocumentElement();
				Element codeElement = (Element) settingsElement.getElementsByTagName("code").item(0);
				Element xpathElement = (Element) settingsElement.getElementsByTagName("xpath").item(0);

				String code = getTextContext(codeElement);
				String languageVersion = codeElement.getAttribute("language-version");
				String xpath = getTextContext(xpathElement);
				String xpathVersion = xpathElement.getAttribute("version");

				codeEditorPane.setText(code);
				setLanguageVersion(LanguageRegistry.findLanguageVersionByTerseName(languageVersion));
				xpathQueryArea.setText(xpath);

				//				for (Enumeration<AbstractButton> e = xpathVersionButtonGroup.getElements(); e.hasMoreElements();) {
				//					AbstractButton button = e.nextElement();
				//					if (xpathVersion.equals(button.getActionCommand())) {
				//						button.setSelected(true);
				//						break;
				//					}
				//				}
				ObservableList<Toggle> toggles = xpathVersionButtonGroup.getToggles();
				for (Toggle e : toggles) {
					if (xpathVersion.equals(e.getUserData())) {
						e.setSelected(true);
						break;
					}
				}

			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private void saveSettings() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element settingsElement = document.createElement("settings");
			document.appendChild(settingsElement);

			Element codeElement = document.createElement("code");
			settingsElement.appendChild(codeElement);
			codeElement.setAttribute("language-version", getLanguageVersion().getTerseName());
			codeElement.appendChild(document.createCDATASection(codeEditorPane.getText()));

			Element xpathElement = document.createElement("xpath");
			settingsElement.appendChild(xpathElement);
			Object userData = xpathVersionButtonGroup.getSelectedToggle().getUserData();
			xpathElement.setAttribute("version", userData.toString());
			//			xpathElement.setAttribute("version", xpathVersionButtonGroup.getSelection().getActionCommand());

			xpathElement.appendChild(document.createCDATASection(xpathQueryArea.getText()));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			// This is as close to pretty printing as we'll get using standard Java APIs.
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			Source source = new DOMSource(document);
			Result result = new StreamResult(new FileWriter(new File(SETTINGS_FILE_NAME)));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private String getTextContext(Element element) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			org.w3c.dom.Node child = element.getChildNodes().item(i);
			if (child instanceof Text) {
				buf.append(((Text) child).getData());
			}
		}
		return buf.toString();
	}
}
