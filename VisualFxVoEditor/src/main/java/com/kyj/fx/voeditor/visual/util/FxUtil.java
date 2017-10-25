/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.controlsfx.control.PopOver;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.bar.GargoyleLoadBar;
import com.kyj.fx.voeditor.visual.component.bar.GargoyleSynchLoadBar;
import com.kyj.fx.voeditor.visual.component.console.WebViewConsole;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockNode;
import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.IOptions;
import com.kyj.fx.voeditor.visual.component.notifycation.GargoyleNotification;
import com.kyj.fx.voeditor.visual.component.popup.JavaTextView;
import com.kyj.fx.voeditor.visual.component.scm.FxSVNHistoryDataSupplier;
import com.kyj.fx.voeditor.visual.component.scm.ScmCommitComposite;
import com.kyj.fx.voeditor.visual.component.scm.SvnChagnedCodeComposite;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.component.text.SimpleTextView;
import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleException;
import com.kyj.fx.voeditor.visual.framework.InstanceTypes;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.framework.builder.GargoyleBuilderFactory;
import com.kyj.fx.voeditor.visual.framework.builder.GargoyleButtonBuilder;
import com.kyj.fx.voeditor.visual.framework.contextmenu.FxContextManager;
import com.kyj.fx.voeditor.visual.functions.ToExcelFileFunction;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.momory.FxMemory;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.Printer.MarginType;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.SnapshotResult;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;
import jfxtras.scene.layout.HBox;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

/**
 * Javafx UI 관련 유틸리티 클래스
 *
 * @author KYJ
 *
 */
/***************************
 *
 * @author KYJ
 *
 ***************************/
public class FxUtil {

	/**
	 * @최초생성일 2016. 10. 6.
	 */
	public static final String DEFAULT_FONT_SIMPLE = "NANUMBARUNGOTHIC";
	public static final String DEFAULT_FONT = DEFAULT_FONT_SIMPLE + ".TTF";
	public static final String FONTS_NANUMBARUNGOTHIC_TTF = "fonts/" + DEFAULT_FONT;

	private static final Logger LOGGER = LoggerFactory.getLogger(FxUtil.class);

	static {

		try {

			/*
			 * 2017-04-24 Font가 jar파일안에 압축되어있는경우 Temp 폴더에 임시 파일이 계속 쌓임. 관련된
			 * 버그수정을 위해 Font를 임시디렉토리로 복사한후 읽어옴.
			 */
			File parentFile = new File(FileUtil.getTempGagoyle(), "font");
			if (!parentFile.exists())
				parentFile.mkdirs();

			File fontFile = new File(parentFile, DEFAULT_FONT);

			// 복사.
			try (InputStream is = ClassLoader.getSystemResource(FONTS_NANUMBARUNGOTHIC_TTF).openStream()) {
				FileUtil.copy(is, fontFile);
			}

			Font.loadFont(new FileInputStream(fontFile), 12);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Font getBoldFont() {
		return getBoldFont(12d);
	}

	public static Font getBoldFont(double fontSize) {
		return Font.font(DEFAULT_FONT_SIMPLE, FontWeight.BOLD, FontPosture.ITALIC, fontSize);
	}

	/**
	 * 에러 콜백.
	 *
	 * @최초생성일 2016. 6. 29.
	 */
	private static final Consumer<Exception> DEFAULT_ERROR_CALLBACK = error -> {
		LOGGER.error(ValueUtil.toString(error));
	};

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * FXMLController에 정의된 내용을 기준으로 FXML을 로드한다.
	 *
	 *
	 * @param controllerClass
	 * @return
	 * @throws GargoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <T> T load(Class<?> controllerClass) throws Exception {
		return load(controllerClass, null, null, null);
	}

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ <br/>
	 * FXMLController에 정의된 내용을 기준으로 FXML을 로드한다. <br/>
	 * 
	 * @param controllerClass
	 * @param N
	 *            - Node option <br/>
	 *            FXML을 로드한후 후처리할 내용을 기입한다. <br/>
	 * @return
	 * @throws GargoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <N, C> N load(Class<C> controllerClass, Consumer<N> option) throws Exception {
		return load(controllerClass, null, option, null);
	}

	public static <N, C> N load(Class<C> controllerClass, Consumer<N> option, Consumer<Exception> errHandler) {
		N n = null;
		try {
			n = load(controllerClass, null, option, null);
		} catch (Exception e) {
			if (errHandler != null)
				e.printStackTrace();
			else
				LOGGER.error(ValueUtil.toString(e));
		}
		return n;
	}

	/**
	 * XMLController에 정의된 내용을 기준으로 FXML을 로드한다.
	 *
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param controllerClass
	 * @param controllerAction
	 * @return
	 * @throws GargoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 */
	public static <N, C> N loadAndControllerAction(Class<C> controllerClass, Consumer<C> controllerAction) throws Exception {
		return load(controllerClass, null, null, controllerAction);
	}

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * FXMLController에 정의된 내용을 기준으로 FXML을 로드한다.
	 *
	 * @param controllerClass
	 * @param instance
	 * @return
	 * @throws GargoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <T, C> T loadRoot(Class<C> controllerClass, Object instance) throws Exception {
		return load(controllerClass, instance, null, null);
	}

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * FXMLController에 정의된 내용을 기준으로 FXML을 로드한다.
	 *
	 * @param controllerClass
	 * @return
	 * @throws GargoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 ********************************/
	public static <T, C> T loadRoot(Class<C> controllerClass) throws Exception {
		return load(controllerClass, controllerClass.newInstance(), null, null);
	}

	/********************************
	 * 작성일 : 2016. 5. 28. 작성자 : KYJ
	 *
	 * ref loadRoot() method.
	 *
	 * 에러를 뱉지않고 핸들링할 수 있는 파라미터를 받음.
	 *
	 * @param controllerClass
	 * @param errorCallback
	 * @return
	 ********************************/
	private static <T, C> T loadRoot(Class<C> controllerClass, Consumer<Exception> errorCallback) {
		try {
			return load(controllerClass, controllerClass.newInstance(), null, null);
		} catch (Exception e) {
			errorCallback.accept(e);
		}
		return null;
	}

	/********************************
	 * 작성일 : 2016. 5. 29. 작성자 : KYJ
	 *
	 * ref loadRoot() method.
	 *
	 * 에러를 뱉지않고 핸들링할 수 있는 파라미터를 받음.
	 *
	 * @param controllerClass
	 * @param instance
	 * @param errorCallback
	 * @return
	 ********************************/
	public static <T, C> T loadRoot(Class<C> controllerClass, Object instance, Consumer<Exception> errorCallback) {
		try {
			return load(controllerClass, instance, null, null);
		} catch (Exception e) {
			errorCallback.accept(e);
		}
		return null;
	}

	private static <C> FXMLController getFxmlController(Class<C> controllerClass) throws GargoyleException {
		return controllerClass.getAnnotation(FXMLController.class);
	}

	private static String getFxml(FXMLController controller) {
		if (controller == null) {
			return null;
		}
		return controller.value();
	}

	private static String getCss(Class<?> res, FXMLController controller) {
		if (res != null) {
			try {
				String css = getCss(controller);

				if (ValueUtil.isEmpty(css))
					return null;

				URL resource = res.getResource(css);

				if (resource == null)
					return null;

				return resource.toExternalForm();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getCss(FXMLController controller) {
		if (controller == null) {
			return null;
		}
		return controller.css();
	}

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * FXMLController 어노테이션 에 정의된 내용을 기준으로 FXML을 로드한다. </br>
	 * 아래메소드를 활용하는 경우
	 *
	 * PostInitialize 어노테이션을 활용하여 initialize() 수행후 후처리를 지정할 수 있음.
	 *
	 * @param controllerClass
	 * @param option
	 * @return
	 * @throws GargoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <N, C> N load(Class<C> controllerClass, Object rootInstance, Consumer<N> option, Consumer<C> controllerAction)
			throws Exception {
		if (controllerClass == null)
			throw new NullPointerException("controller is null.");

		String fullClassName = controllerClass.getCanonicalName();
		FXMLController controller = getFxmlController(controllerClass);
		if (controller == null) {
			throw new GargoyleException("this is not FXMLController. check @FXMLController");
		}

		String fxml = getFxml(controller);// controller.value();

		String css = getCss(controllerClass, controller);// controller.value();

		if (ValueUtil.isEmpty(fxml))
			throw new IllegalArgumentException("value is empty..");

		InstanceTypes type = controller.instanceType();
		N newInstance = null;
		switch (type) {

		case Singleton:

			Node node = FxMemory.get(fullClassName);
			if (node == null) {
				newInstance = newInstance(controllerClass, rootInstance, controller.isSelfController(), fxml, css, option,
						controllerAction);
				FxMemory.put(fullClassName, (Node) newInstance);
			} else {
				newInstance = (N) node;
			}

			break;

		case RequireNew:
			newInstance = newInstance(controllerClass, rootInstance, controller.isSelfController(), fxml, css, option, controllerAction);
			break;
		}

		return newInstance;
	}

	public static <T, C> T load(Class<?> controllerClass, Object rootInstance, boolean isSelfController, String fxml, String css)
			throws Exception {
		return newInstance(controllerClass, rootInstance, isSelfController, fxml, css, null, null);
	}

	public static <T, C> T load(Class<?> controllerClass, Object rootInstance, boolean isSelfController, String fxml, Consumer<T> option,
			Consumer<C> controllerAction) throws Exception {
		return newInstance(controllerClass, rootInstance, isSelfController, fxml, null, option, controllerAction);
	}

	public static <T, C> T load(Class<?> controllerClass, Object rootInstance, boolean isSelfController, String fxml, String css,
			Consumer<T> option, Consumer<C> controllerAction) throws Exception {
		return newInstance(controllerClass, rootInstance, isSelfController, fxml, css, option, controllerAction);
	}

	private static <T, C> T newInstance(Class<?> controllerClass, Object rootInstance, boolean isSelfController, String _fxml, String _css,
			Consumer<T> option, Consumer<C> controllerAction) throws Exception {

		String fxml = _fxml;
		if (fxml == null) {
			FXMLController controller = getFxmlController(controllerClass);
			if (controller == null) {
				throw new GargoyleException("this is not FXMLController. check @FXMLController");
			}
			fxml = getFxml(controller);// controller.value();
		}

		URL resource = controllerClass.getResource(fxml);

		FXMLLoader loader = createNewFxmlLoader();
		loader.setLocation(resource);

		if (isSelfController && rootInstance != null) {
			try {
				loader.setRoot(rootInstance);
				loader.setController(rootInstance);

				if (rootInstance instanceof Parent && ValueUtil.isNotEmpty(_css)) {
					((Parent) rootInstance).getStylesheets().add(_css);
				}

			} catch (Exception e) {
				throw new GargoyleException(e);
			}
		}
		// T _load = null;
		// fix load error.
		// if (resource != null) {
		T load = loader.load();
		// } else {
		// _load = loader.load(controllerClass.getResourceAsStream(fxml));
		// }
		//
		// T load = _load;
		C instanceController = loader.getController();

		// show warning...
		if (load == null) {
			LOGGER.warn("load result is empty.. controller class : {} ", controllerClass);
		}
		Method[] declaredMethods = controllerClass.getDeclaredMethods();

		// 2017-02-07 findfirst에서 어노테이션으로 선언된 다건의 함수를 호출하게 다시 유도.
		// findfirst로 수정. @FxPostInitialize가 여러건있는경우를 잘못된 로직 유도를 방지.
		Stream.of(declaredMethods).filter(m -> m.getParameterCount() == 0 && m.getAnnotation(FxPostInitialize.class) != null).forEach(m -> {
			// .ifPresent((m -> {
			if (((m.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC)) {
				try {
					if (instanceController != null) {
						// Lazy Run.
						Platform.runLater(() -> {
							try {

								m.setAccessible(true);
								m.invoke(instanceController);

							} catch (Exception e) {
								LOGGER.error(ValueUtil.toString(e));
							}
						});
					}
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}
		});

		if (option != null) {
			option.accept(load);
		}

		if (controllerAction != null)
			controllerAction.accept(instanceController);

		Platform.runLater(() -> {
			Parent parent = (Parent) load;
			List<Node> findAllByNodes = FxUtil.findAllByNodes(parent, v -> v instanceof Button);
			findAllByNodes.forEach(v -> {
				GargoyleButtonBuilder.applyStyleClass((Button) v, SkinManager.BUTTON_STYLE_CLASS_NAME);
				// LOGGER.debug("Button : {}", v);
			});
		});

		return load;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 28.
	 * @return
	 */
	public static FXMLLoader createNewFxmlLoader() {

		FXMLLoader loader = new FXMLLoader();
		loader.setBuilderFactory(GargoyleBuilderFactory.getInstance());
		return loader;
	}

	/**
	 * 픽포인트의 노드 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 20.
	 * @param node
	 * @param sceneX
	 * @param sceneY
	 * @return
	 */
	public static Node pick(Node node, double sceneX, double sceneY) {
		Point2D p = node.sceneToLocal(sceneX, sceneY, true /* rootScene */);

		// check if the given node has the point inside it, or else we drop out
		if (!node.contains(p))
			return null;

		// at this point we know that _at least_ the given node is a valid
		// answer to the given point, so we will return that if we don't find
		// a better child option
		if (node instanceof Parent) {
			// we iterate through all children in reverse order, and stop when
			// we find a match.
			// We do this as we know the el ements at the end of the list have a
			// higher
			// z-order, and are therefore the better match, compared to children
			// that
			// might also intersect (but that would be underneath the element).
			Node bestMatchingChild = null;
			List<Node> children = ((Parent) node).getChildrenUnmodifiable();
			for (int i = children.size() - 1; i >= 0; i--) {
				Node child = children.get(i);
				p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */);
				if (child.isVisible() && !child.isMouseTransparent() && child.contains(p)) {
					bestMatchingChild = child;
					break;
				}
			}

			if (bestMatchingChild != null) {
				return pick(bestMatchingChild, sceneX, sceneY);
			}
		}

		return node;
	}

	/**
	 * X축 절대좌표를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param longRegion
	 *            사이즈가 늘어나는 Region객체
	 * @param target
	 *            절대좌표를 구할 target객체
	 * @return
	 */
	public static double getAbsolteX(Region longRegion, Parent target) {
		double result = target.getLayoutX();
		Parent parent2 = target.getParent();
		if (longRegion == parent2) {
			return result;
		}
		return result += getAbsolteX(longRegion, parent2);
	}

	/**
	 * Y축 절대좌표를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param longRegion
	 *            사이즈가 늘어나는 Region객체
	 * @param target
	 *            절대좌표를 구할 target객체
	 * @return
	 */
	public static double getAbsolteY(Region longRegion, Parent target) {
		double result = target.getLayoutY();
		Parent parent2 = target.getParent();
		if (longRegion == parent2) {
			return result;
		}
		return result += getAbsolteY(longRegion, parent2);
	}

	/**
	 * X, Y축 절대좌표를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param longRegion
	 *            사이즈가 늘어나는 Region객체
	 * @param target
	 *            절대좌표를 구할 target객체
	 * @return
	 */
	public static Point2D getAbsolte2D(Region longRegion, Parent target) {

		Point2D point2d = new Point2D(target.getLayoutX(), target.getLayoutY());
		// Point2D point2d = new Point2D(target.getBoundsInLocal().getMaxX(),
		// target.getBoundsInLocal().getMaxY());
		// Point2D point2d = new Point2D(target.getScene().getX(),
		// target.getScene().getY());
		Parent parent2 = target.getParent();
		if (longRegion == parent2) {
			return point2d;
		}
		return point2d.add(getAbsolte2D(longRegion, parent2));
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 11.
	 * @param root
	 * @param object
	 */
	public static <T> TreeItem<T> search(TreeItem<T> root, Predicate<T> isMatch) {

		TreeItem<T> result = null;
		if (isMatch.test(root.getValue())) {
			return root;
		}

		ObservableList<TreeItem<T>> children = root.getChildren();
		Optional<TreeItem<T>> findFirst = children.stream().map(tree -> {

			T value = tree.getValue();
			if (value == null)
				return null;

			if (isMatch.test(value)) {
				return tree;
			}
			return null;

		}).filter(v -> v != null).findFirst();

		if (findFirst.isPresent()) {
			return findFirst.get();
		}

		Iterator<TreeItem<T>> iterator = children.parallelStream().iterator();
		while (iterator.hasNext()) {
			TreeItem<T> next = iterator.next();
			LOGGER.debug("search :: {}", next.getValue());
			TreeItem<T> search = search(next, isMatch);
			if (search != null) {
				result = search;
				break;
			}
		}

		return result;
	}

	/**************************************************************************************************************/
	// Stage 생성로직.
	/**************************************************************************************************************/

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 */
	public static void createStageAndShow(Node parent) {
		createStageAndShow(parent.getClass().getSimpleName(), new Scene(new BorderPane(parent)), false);
	}

	public static void createStageAndShow(String title, Node parent) {
		createStageAndShow(title, new Scene(new BorderPane(parent)), false);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param isModal
	 */
	public static <N extends Node> void createStageAndShow(String title, N parent, boolean isModal) {
		createStageAndShow(title, new Scene(new BorderPane(parent)), isModal);
	}

	public static <N extends Node> void createStageAndShow(String title, N parent, Consumer<Stage> option) {
		createStageAndShow(title, new Scene(new BorderPane(parent)), option);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param isModal
	 */
	public static void createStageAndShow(String title, Parent parent, boolean isModal) {
		createStageAndShow(title, new Scene(parent), isModal);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param option
	 */
	// public static void createStageAndShow(String title, Node parent,
	// Consumer<Stage> option) {
	// createStageAndShow(title, new Scene(new BorderPane(parent)), option);
	// }

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param option
	 */
	public static <P extends Parent> void createStageAndShow(P parent, Consumer<Stage> option) {
		Scene scene = new Scene(parent);
		createStageAndShow(scene, option);
	}

	public static void createStageAndShow(CloseableParent<? extends Parent> cloableParent, Consumer<Stage> option) {

		Stage stage = craeteStage(cloableParent.getParent(), option);
		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, ev -> {
			try {
				cloableParent.close();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		});

		// remove setOnCloseRequest and then, addEventHandler
		// stage.setOnCloseRequest(ev -> {
		//
		// });
		stage.show();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param scene
	 * @param isModal
	 */
	public static void createStageAndShow(String title, Scene scene, Consumer<Stage> option) {
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(title);
		option.accept(stage);
		stage.show();
	}

	public static void createStageAndShow(String title, Scene scene, boolean isModal) {
		Consumer<Stage> option = null;
		if (isModal) {
			option = stage -> {
				stage.setTitle(title);
				stage.setAlwaysOnTop(true);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(stage);
			};
		} else {
			option = stage -> {
				stage.setTitle(title);
				// stage.setAlwaysOnTop(true);
				// stage.initModality(Modality.APPLICATION_MODAL);
				// stage.initOwner(stage);
			};
		}

		createStageAndShow(scene, option);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 */
	public static void createStageAndShow(Scene scene, Consumer<Stage> option) {
		Stage stage = craeteStage(scene, option);
		stage.show();
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 *
	 * @param scene
	 * @return
	 ********************************/
	public static Stage craeteStage(Scene scene) {
		Stage stage = new Stage();
		stage.setScene(scene);
		return stage;
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 *
	 * @param scene
	 * @param option
	 * @return
	 ********************************/
	public static Stage craeteStage(Scene scene, Consumer<Stage> option) {
		Stage stage = new Stage();
		stage.setScene(scene);

		scene.getStylesheets().add(SkinManager.getInstance().getSkin());
		scene.getStylesheets().add(SkinManager.getInstance().getButtonSkin());

		if (option != null)
			option.accept(stage);
		return stage;
	}

	public static Stage craeteStage(Parent parent, Consumer<Stage> option) {
		return craeteStage(new Scene(parent), option);
	}

	/********************************
	 * 작성일 : 2016. 6. 29. 작성자 : KYJ
	 *
	 * 캡쳐
	 *
	 * @param target
	 * @param saveFile
	 ********************************/
	public static void snapShot(Node target, File saveFile) {
		snapShot(target, saveFile, DEFAULT_ERROR_CALLBACK);
	}

	/********************************
	 * 작성일 : 2016. 6. 29. 작성자 : KYJ
	 *
	 * 캡쳐
	 *
	 * @param target
	 * @param saveFile
	 * @param errorCallback
	 ********************************/
	public static void snapShot(Node target, File saveFile, Consumer<Exception> errorCallback) {

		try (FileOutputStream out = new FileOutputStream(saveFile)) {
			snapShot(target, out, errorCallback);
		} catch (IOException e) {
			errorCallback.accept(e);
		}
	}

	/********************************
	 * 작성일 : 2016. 6. 29. 작성자 : KYJ
	 *
	 * 캡쳐
	 *
	 * out처리가 완료되면 stream은 자동 close처리됨.
	 *
	 * @param target
	 * @param out
	 * @param errorCallback
	 ********************************/
	public static void snapShot(Node target, OutputStream out, Consumer<Exception> errorCallback) {
		snapShot(target, out, -1, -1, errorCallback);
	}

	public static void snapShot(Scene target, OutputStream out, Consumer<Exception> errorCallback) {
		snapShot(target, out, -1, -1, errorCallback);
	}

	public static void snapShot(Scene target, OutputStream out, int requestWidth, int requestHeight, Consumer<Exception> errorCallback) {
		if (target == null)
			throw new NullPointerException("target Node is empty.");

		if (out == null)
			throw new NullPointerException("target Stream is empty.");

		SnapshotParameters params = new SnapshotParameters();
		params.setDepthBuffer(true);

		// params.setFill(Color.TRANSPARENT);

		WritableImage wi = null;
		if (requestWidth >= 0 || requestHeight >= 0) {
			wi = new WritableImage(requestWidth, requestHeight);
		}

		WritableImage snapshot = target.snapshot(wi);
		try {
			boolean isSuccess = snapShot(out, snapshot);
			LOGGER.debug("Write Image result {}", isSuccess);
		} catch (IOException e) {
			errorCallback.accept(e);
		}
	}

	public static void snapShot(Node target, OutputStream out, int requestWidth, int requestHeight, Consumer<Exception> errorCallback) {

		if (target == null)
			throw new NullPointerException("target Node is empty.");

		if (out == null)
			throw new NullPointerException("target Stream is empty.");

		SnapshotParameters params = new SnapshotParameters();
		params.setDepthBuffer(true);

		// params.setFill(Color.TRANSPARENT);

		WritableImage wi = null;
		if (requestWidth >= 0 || requestHeight >= 0) {
			wi = new WritableImage(requestWidth, requestHeight);
		}

		WritableImage snapshot = target.snapshot(params, wi);
		try {
			boolean isSuccess = snapShot(out, snapshot);
			LOGGER.debug("Write Image result {}", isSuccess);
		} catch (IOException e) {
			errorCallback.accept(e);
		}
	}

	/********************************
	 * 작성일 : 2016. 6. 29. 작성자 : KYJ
	 *
	 * 캡쳐 후 stream close처리.
	 *
	 * @param out
	 * @param image
	 * @return
	 * @throws IOException
	 ********************************/
	private static boolean snapShot(OutputStream out, WritableImage image) throws IOException {
		return ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
		// if (out != null)
		// out.close();
	}

	/********************************
	 * 작성일 : 2016. 6. 29. 작성자 : KYJ
	 *
	 * print 처리.
	 *
	 * @param window
	 * @param target
	 ********************************/
	public static void printJob(Window window, Node target) {
		Printer printer = Printer.getDefaultPrinter();
		// PrinterAttributes printerAttributes = printer.getPrinterAttributes();
		//
		Paper a4 = Paper.A4;

		// Paper a4 = PrintHelper.createPaper("Rotate A4", Paper.A4.getHeight(),
		// Paper.A4.getWidth(), Units.MM);
		PageLayout pageLayout = printer.createPageLayout(a4, PageOrientation.REVERSE_PORTRAIT, MarginType.DEFAULT);

		PrinterJob printerJob = PrinterJob.createPrinterJob();

		// JobSettings jobSettings = printerJob.getJobSettings();
		// jobSettings.setPrintSides(PrintSides.TUMBLE);
		ImageView imageView = new ImageView();
		// 화면 사이즈에 맞게 크기 조절.
		Callback<SnapshotResult, Void> callback = param -> {
			final WritableImage image = param.getImage();
			imageView.setImage(image);

			final double scaleX = pageLayout.getPrintableWidth() / imageView.getBoundsInParent().getWidth();
			final double scaleY = pageLayout.getPrintableHeight() / imageView.getBoundsInParent().getHeight();
			imageView.getTransforms().add(new Scale(scaleX, scaleY));

			return null;
		};

		target.snapshot(callback, null, null);

		if (printerJob.showPrintDialog(window) && printerJob.printPage(pageLayout, imageView))
			printerJob.endJob();
	}

	public static void printDefefaultJob(Window window, Node target) {
		printJob(window, target, (param, imageView) -> {

			// ImageView imageView = new ImageView();
			final WritableImage image = param.getImage();
			imageView.setImage(image);

			// final double scaleX = pageLayout.getPrintableWidth() /
			// imageView.getBoundsInParent().getWidth();
			// final double scaleY = pageLayout.getPrintableHeight() /
			// imageView.getBoundsInParent().getHeight();
			imageView.getTransforms().add(new Scale(1, 1));

		});
	}

	public static void printJob(Window window, Node target, BiConsumer<SnapshotResult, ImageView> draw) {
		Printer printer = Printer.getDefaultPrinter();
		// PrinterAttributes printerAttributes = printer.getPrinterAttributes();
		//
		Paper a4 = Paper.A4;

		// Paper a4 = PrintHelper.createPaper("Rotate A4", Paper.A4.getHeight(),
		// Paper.A4.getWidth(), Units.MM);
		PageLayout pageLayout = printer.createPageLayout(a4, PageOrientation.REVERSE_PORTRAIT, MarginType.DEFAULT);

		PrinterJob printerJob = PrinterJob.createPrinterJob();

		// JobSettings jobSettings = printerJob.getJobSettings();
		// jobSettings.setPrintSides(PrintSides.TUMBLE);
		ImageView imageView = new ImageView();

		Callback<SnapshotResult, Void> callback = param -> {

			draw.accept(param, imageView);

			// final WritableImage image = param.getImage();
			// imageView.setImage(image);

			// final double scaleX = pageLayout.getPrintableWidth() /
			// imageView.getBoundsInParent().getWidth();
			// final double scaleY = pageLayout.getPrintableHeight() /
			// imageView.getBoundsInParent().getHeight();
			// imageView.getTransforms().add(new Scale(scaleX, scaleY));

			return null;
		};

		target.snapshot(callback, null, null);

		if (printerJob.showPrintDialog(window) && printerJob.printPage(pageLayout, imageView))
			printerJob.endJob();
	}

	/**
	 * 파일로부터 이미지를 그리기 위한 뷰를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param file
	 * @return
	 * @User KYJ
	 */
	public static ImageView createImageIconView(File file) {
		Image fxImage = null;
		if (file.exists()) {
			FileSystemView fileSystemView = FileSystemView.getFileSystemView();
			Icon icon = fileSystemView.getSystemIcon(file);

			BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
			fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
		} else {
			return new ImageView();
		}

		return new ImageView(fxImage);
	}

	/**
	 * 파일로부터 이미지를 그리기 위한 뷰를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param file
	 * @return
	 * @User KYJ
	 */
	public static ImageView createImageView(InputStream inputStream) {
		Image fxImage = null;
		if (inputStream != null) {
			fxImage = new Image(inputStream);
		} else {
			return new ImageView();
		}
		return new ImageView(fxImage);
	}

	/**
	 * WEB 색상 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param color
	 * @return
	 */
	public static String toWebString(Color color) {
		if (color == null)
			return "BLACK";

		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
	}

	/**
	 * RGB Color String
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 1.
	 * @param color
	 * @return
	 */
	public static String toRgbString(Color color) {
		if (color == null)
			return "BLACK";
		return String.format("rgba(%d, %d, %d, 1)", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	/**
	 * HSB Color String
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 1.
	 * @param color
	 * @return
	 */
	public static String toHsbString(Color color) {
		if (color == null)
			return "BLACK";
		return String.format("hsl(%d, %d%%, %d%%)", (int) (color.getHue()), (int) (color.getSaturation() * 100),
				(int) (color.getBrightness() * 100));
	}

	/********************************
	 * 작성일 : 2016. 7. 19. 작성자 : KYJ
	 *
	 * Show PopOver
	 *
	 * @param root
	 * @param showingNode
	 ********************************/
	public static void showPopOver(Node root, Node showingNode) {
		showPopOver(root, showingNode, null);
	}

	public static void showPopOver(Node root, Node showingNode, Function<PopOver, PopOver> callback) {
		if (root == showingNode)
			return;

		PopOver popOver = null;
		if (callback != null) {
			popOver = callback.apply(new PopOver(showingNode));
		} else
			popOver = new PopOver(showingNode);

		popOver.show(root);
	}

	/**
	 * Show PopOver
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 28.
	 * @param root
	 * @param showingNode
	 * @param locationX
	 * @param locationY
	 */
	public static void showPopOver(Node root, Node showingNode, double locationX, double locationY) {
		if (root == showingNode)
			return;

		showPopOver(root, showingNode, pop -> {
			pop.setX(locationX);
			pop.setY(locationY);
			return pop;
		});
	}

	/**
	 * SVNGraph 노드생성후 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static TabPane createSVNGraph(String url) throws Exception {
		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, url);
		return createSVNGraph(properties);
	}

	/**
	 * SVNGraph 노드생성후 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	public static TabPane createSVNGraph(Properties properties) throws Exception {
		return createSVNGraph(new JavaSVNManager(properties));
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @param manager
	 * @throws Exception
	 */
	public static TabPane createSVNGraph(JavaSVNManager manager) throws Exception {
		FxSVNHistoryDataSupplier svnDataSupplier = new FxSVNHistoryDataSupplier(manager);
		SvnChagnedCodeComposite svnChagnedCodeComposite = new SvnChagnedCodeComposite(svnDataSupplier);
		ScmCommitComposite scmCommitComposite = new ScmCommitComposite(svnDataSupplier);
		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(new Tab("Chagned Codes.", svnChagnedCodeComposite), new Tab("Commit Hist.", scmCommitComposite));
		return tabPane;

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 22.
	 * @param content
	 */
	public static JavaTextArea createJavaTextArea(String title, String content, double width, double height) {
		JavaTextArea javaTextArea = new JavaTextArea();
		javaTextArea.setPrefSize(width, height);
		javaTextArea.setContent(content);
		return javaTextArea;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 22.
	 * @param content
	 */
	public static JavaTextArea createJavaTextArea(String content, double width, double height) {
		JavaTextArea javaTextArea = new JavaTextArea();
		javaTextArea.setPrefSize(width, height);
		javaTextArea.setContent(content);
		return javaTextArea;
	}

	public static JavaTextArea createJavaTextArea(String content) {
		return createJavaTextArea(content, 1200, 800);
	}

	public static void createSimpleTextAreaAndShow(String content, Consumer<Stage> option) {
		createStageAndShow(new TextArea(content), option);
	}

	public static void createCodeAreaAndShow(String content) {
		SimpleTextView parent = new SimpleTextView(content);

		createStageAndShow(parent, stage -> {
			stage.setWidth(1200d);
			stage.setHeight(800d);
		});
	}

	public static void createCodeAreaAndShow(String content, Consumer<Stage> option) {
		createCodeAreaAndShow(content, c -> {

			c.setParagraphGraphicFactory(LineNumberFactory.get(c));

		}, option);
	}

	public static void createCodeAreaAndShow(String content, Consumer<CodeArea> add, Consumer<Stage> option) {
		CodeArea parent = new CodeArea(content);

		if (add != null)
			add.accept(parent);

		createStageAndShow(parent, option);
	}

	/********************************
	 * 작성일 : 2016. 8. 23. 작성자 : KYJ
	 *
	 * Node의 Window 객체를 리턴함.
	 *
	 * @param node
	 * @return
	 ********************************/
	public static Window getWindow(Node node) {
		return getWindow(node, () -> null);
	}

	public static Window getWindow(Node node, Supplier<Window> emptyThan) {
		if (node != null) {
			Scene scene = node.getScene();
			if (scene != null) {
				return scene.getWindow();
			}
		}

		if (emptyThan != null)
			return emptyThan.get();

		return null;
	}

	/********************************
	 * 작성일 : 2016. 9. 3. 작성자 : KYJ
	 *
	 * TableView 키이벤트를 등록
	 *
	 * @param tb
	 ********************************/
	public static void installClipboardKeyEvent(TableView<?> tb) {
		// FxTableViewUtil.installCopyPasteHandler(tb);
		FxTableViewUtil.installCopyHandler(tb);
		// 2017.05.26 사용안함.
		// ClipboardKeyEventInstaller.install(tb);
	}

	/**
	 * 컨텐스트 메뉴를 추가하기 위한 유틸리티성 클래스
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 10.
	 * @param contextManager
	 */
	public static void installContextMenu(FxContextManager contextManager) {
		contextManager.install();
		LOGGER.debug(" contextmenu install success. ");
	}

	/**
	 * 편의성 처리를 위해 완성코드 리턴.
	 *
	 * @author KYJ
	 *
	 */
	public static class EasyFxUtils {

		public static void showApplicationCode(String sql, Function<String, String> convert) {

			String[] split = sql.split("\n");
			StringBuilder sb = new StringBuilder();
			sb.append("StringBuffer sb = new StringBuffer();\n");
			for (String str : split) {
				sb.append("sb.append(\"").append(convert.apply(str)).append("\\n").append("\");\n");
			}
			sb.append("sb.toString();");

			LOGGER.debug(sb.toString());

			try {
				new JavaTextView(sb.toString()).show(800, 500);
			} catch (IOException e) {
				LOGGER.error(ValueUtil.toString(e));
			}

		}

		/**
		 * 본문에 double dot(")로 인해 발생되는 이슈를 해결함.
		 *
		 * @최초생성일 2016. 11. 10.
		 */
		private static final Function<String, String> smartDoubleDotConvert = str -> {

			if (str.indexOf("\"") != -1) {
				List<Integer> doubleDots = new ArrayList<>();
				int idx = 0;

				while (idx != -1) {

					int nextIdx = str.indexOf("\"", idx);

					if (nextIdx != -1) {

						/*
						 * ignore index check. character.
						 *
						 * 실제 텍스트 \ 기호가 포함되는 경우는 변환처리대상에서 제외.
						 *
						 * 텍스트에 \" 없이 단순히 "(double dot) 만 포함되는경우는
						 *
						 * \를 포함시킴.
						 */
						if (str.charAt(nextIdx - 1) != '\\') {
							doubleDots.add(nextIdx);
						}
					}

					if (nextIdx == -1)
						break;

					idx = nextIdx + 1;

				}

				if (!doubleDots.isEmpty()) {
					// 찾아낸 문자열들을 다시 재조합한후 리턴.
					StringBuffer sb = new StringBuffer();
					int arrayIdx = doubleDots.size();
					int stringIdx = 0;

					for (int i = 0; i < arrayIdx; i++) {
						int splitIndex = doubleDots.get(i).intValue();
						sb.append(str.substring(stringIdx, splitIndex)).append("\\\"");
						stringIdx = splitIndex + 1;
					}
					sb.append(str.substring(stringIdx));

					str = sb.toString();
				}

			}

			return str;
		};

		/**
		 * 어플리케이션 코드를 만들어주는 팝업을 보여준다.
		 *
		 * @작성자 : KYJ
		 * @작성일 : 2016. 9. 23.
		 * @param sql
		 * @throws IOException
		 */
		public static void showApplicationCode(String sql) {

			showApplicationCode(sql, smartDoubleDotConvert);
			// String[] split = sql.split("\n");
			// StringBuilder sb = new StringBuilder();
			// sb.append("StringBuffer sb = new StringBuffer();\n");
			// for (String str : split) {
			// sb.append("sb.append(\"").append(str).append("\\n").append("\");\n");
			// }
			// sb.append("sb.toString();");
			//
			// LOGGER.debug(sb.toString());
			//
			// try {
			// new JavaTextView(sb.toString()).show(800, 500);
			// } catch (IOException e) {
			// LOGGER.error(ValueUtil.toString(e));
			// }
		}

	}

	/**
	 * 툴팁 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param v
	 * @param string
	 */
	public static void installTooltip(Node node, String string) {
		Tooltip.install(node, new Tooltip(string));
	}

	/**
	 * 테이블뷰의 컬럼모델을 쉽게 생성하기 위한 api.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 17.
	 * @param baseModel
	 * @param view
	 * @param option
	 */
	public static <T extends AbstractDVO> void installCommonsTableView(Class<T> baseModel, TableView<T> view, IOptions option) {
		FxTableViewUtil.installCommonsTableView(baseModel, view, option);
	}

	/**
	 * TextField에 텍스트Auto Binding 설치
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 21.
	 * @param textField
	 * @param suggestions
	 */
	public static void installAutoTextFieldBinding(TextField textField, Supplier<Collection<String>> suggestions) {
		if (suggestions == null || suggestions.get() == null)
			return;

		AutoCompletionTextFieldBinding<String> autoCompletionTextFieldBinding = new AutoCompletionTextFieldBinding<>(textField, param -> {
			String userText = param.getUserText();
			return suggestions.get().stream().filter(v -> v.startsWith(userText)).collect(Collectors.toList());
		});
		autoCompletionTextFieldBinding.setVisibleRowCount(10);

	}

	/**
	 * Parent에서 filter의 조건에 맞는 노드들을 찾은후 리턴.
	 *
	 * visible true인 대상만 찾음.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 20.
	 * @param p
	 * @param filter
	 * @return
	 */

	public static List<Node> findAllByNodes(Parent p, Predicate<Node> filter) {
		return findAllByNodes(p, true, filter);
	}

	/**
	 * Parent에서 filter의 조건에 맞는 노드들을 찾은후 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 11.
	 * @param p
	 * @param onlyVisible
	 *            visible 속성에 맞는 노드들 리턴
	 * @param filter
	 * @return
	 */
	public static List<Node> findAllByNodes(Parent p, boolean onlyVisible, Predicate<Node> filter) {
		return p.getChildrenUnmodifiable().stream().flatMap(v -> {

			// 화면에 visible true인 대상만.
			if (onlyVisible) {

				if (v.isVisible()) {

					if (filter.test(v)) {
						return Stream.of(v);
					}

					else if (v instanceof TableView) {

						Set<Node> lookupAll = ((TableView) v).lookupAll(".table-cell");
						return lookupAll.stream().map(n -> (TableCell) n).map(cell -> {
							return cell.getGraphic();
						}).filter(n -> filter.test(n));
						// return lookupAll.stream();
						// return Stream.empty();
						// return visibleLeafColumns.stream();
						// return findAllByNodes((Parent) v, onlyVisible,
						// filter).stream();
					} else if (v instanceof TitledPane) {

						Stream<Node> stream = findAllByNodes((Parent) v, onlyVisible, filter).stream();
						Node content = ((TitledPane) v).getContent();
						Stream<Node> stream2 = findAllByNodes((Parent) content, onlyVisible, filter).stream();
						return Stream.concat(stream, stream2);

					} else if (v instanceof Parent) {
						return findAllByNodes((Parent) v, onlyVisible, filter).stream();
					}

				}

			}
			// visible 상관없이 모두 찾음.
			else {

				if (filter.test(v)) {
					return Stream.of(v);
				}

				else if (v instanceof Parent) {
					return findAllByNodes((Parent) v, onlyVisible, filter).stream();
				}
			}

			return Stream.empty();
		}).collect(Collectors.toList());
	}

	/**
	 * 도킹기능이 제공되는 팝업을 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 * @param owner
	 * @param dockNode
	 *            메인화면이 되는 노드.
	 */
	public static void createDockStageAndShow(Window owner, DockNode dockNode) {
		createDockStageAndShow(owner, dockNode, null, true);
	}

	/**
	 * 도킹기능이 제공되는 팝업을 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 * @param owner
	 * @param dockNode
	 *            메인화면이 되는 노드.
	 * @param center
	 */
	public static void createDockStageAndShow(Window owner, DockNode dockNode, boolean center) {
		createDockStageAndShow(owner, dockNode, null, center);
	}

	/**
	 * 도킹기능이 제공되는 팝업을 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 * @param dockNode
	 */
	public static void createDockStageAndShow(Window owner, DockNode dockNode, Point2D initLocation) {
		createDockStageAndShow(owner, dockNode, initLocation, false);
	}

	/**
	 * 도킹기능이 제공되는 팝업을 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 * @param dockNode
	 *            메인화면이 되는 노드.
	 */
	public static void createDockStageAndShow(Window owner, DockNode dockNode, Point2D initLocation, boolean center) {
		Platform.runLater(() -> {

			Parent p = (Parent) dockNode.getContents();
			p.getStylesheets().add(SkinManager.getInstance().getSkin());
			p.getStylesheets().add(SkinManager.getInstance().getButtonSkin());
			dockNode.setOwner(owner);
			dockNode.setFloating(true, initLocation);
			if (center)
				dockNode.getStage().centerOnScreen();

		});

	}

	// public static void createDockStageAndShow(CloseableParent<DockNode> node)
	// {
	// createDockStageAndShow(null, node, null, true);
	// }

	// public static void createDockStageAndShow(Window owner,
	// CloseableParent<DockNode> node) {
	// createDockStageAndShow(owner, node, null, true);
	// }
	//
	// public static void createDockStageAndShow(Window owner,
	// CloseableParent<DockNode> node, Point2D initLocation, boolean center) {
	//
	// // DockNode p = node.getParent();
	// // createDockStageAndShow(owner, p, initLocation, center);
	//
	// DockNode dockNode = node.getParent();
	// Parent p = node.getParent();
	// p.getStylesheets().add(SkinManager.getInstance().getSkin());
	// p.getStylesheets().add(SkinManager.getInstance().getButtonSkin());
	//
	// dockNode.setOwner(owner);
	//
	// dockNode.setFloating(true, initLocation);
	// if (center)
	// dockNode.getStage().centerOnScreen();
	//
	// Platform.runLater(() -> {
	// // Window window = p.getScene().getWindow();
	//
	// owner.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, ev -> {
	// try {
	// node.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// });
	// });
	//
	// }

	/**
	 * 로딩바가 뜨면서 액션 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 31.
	 * @param action
	 *            사용자 처리 작업에 대한 코드 로직이 입력됨
	 * @return
	 */
	public static <K> K showLoading(Task<K> action) {
		return showLoading(SharedMemory.getPrimaryStage(), action);
	}

	/**
	 * 로딩바가 뜨면서 액션 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 31.
	 * @param owner
	 *            primaryStage에 대한 데이터가 입력되야함. (사이즈 및 너비, 높이 조절에 대한 메타데이터를 참조함)
	 * @param action
	 *            사용자 처리 작업에 대한 코드 로직이 입력됨
	 * @return
	 */
	public static <K> K showLoading(Window owner, Task<K> action) {

		// 비동기 로딩바
		GargoyleLoadBar<K> gargoyleSynchProgessPopup = new GargoyleSynchLoadBar<>(owner, action);
		// 비동기 로딩바
		// gargoyleSynchProgessPopup = new GargoyleASynchLoadBar<>(stage, task);

		gargoyleSynchProgessPopup.setExecutor(GargoyleSynchLoadBar.newSingleThreadExecutor);
		gargoyleSynchProgessPopup.start();

		return gargoyleSynchProgessPopup.getValue();
	}

	private static final double BROWSER_WIDTH = 1400d;
	private static final double BROWSER_HEIGHT = 900d;

	/**
	 * 브라우저 창 오픈
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 18.
	 * @param content
	 * @return
	 */
	public static WebView openBrowser(Node parent, String content) {
		return openBrowser(parent, content, true);
	}

	public static WebView openBrowser(Node parent, String content, boolean isLink) {

		WebView view = new WebView();
		view.setContextMenuEnabled(false);
		WebEngine engine = view.getEngine();

		engine.setOnError(err -> {
			String message = err.getMessage();
			DialogUtil.showMessageDialog(FxUtil.getWindow(parent), message);
		});

		view.setOnKeyPressed(key -> {

			switch (key.getCode()) {
			case F5:
				if (key.isConsumed())
					return;
				view.getEngine().reload();
				break;

			case F12:
				if (key.isConsumed())
					return;

				FxUtil.createStageAndShow("Simple Web Console", new WebViewConsole(view));
				key.consume();
				break;

			default:
				break;
			}

		});

		view.setOnContextMenuRequested(ev -> {

			MenuItem miReload = new MenuItem("Reload");
			miReload.setOnAction(e -> {
				view.getEngine().reload();
			});

			MenuItem miSource = new MenuItem("Source");
			miSource.setOnAction(e -> {
				Object executeScript = view.getEngine().executeScript("document.documentElement.innerHTML");
				String htmlCoded = executeScript.toString();

				XMLEditor fxmlTextArea = new XMLEditor();
				fxmlTextArea.setContent(htmlCoded);
				FxUtil.createStageAndShow(fxmlTextArea, stage -> {
					stage.setWidth(1200d);
					stage.setHeight(800d);
				});
				// FxUtil.createStageAndShow("Source", new
				// WebViewConsole(view));

			});

			ContextMenu contextMenu = new ContextMenu(miReload, miSource);

			contextMenu.show(FxUtil.getWindow(view), ev.getScreenX(), ev.getScreenY());

		});

		engine.setJavaScriptEnabled(true);
		engine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {

			@Override
			public WebEngine call(PopupFeatures p) {

				// Stage stage = new Stage();
				// WebView wv2 = new WebView();
				//
				// wv2.getEngine().setJavaScriptEnabled(true);
				//
				// stage.setScene(new Scene(wv2, BROWSER_WIDTH,
				// BROWSER_HEIGHT));
				// stage.initOwner(parent == null ? (Window) null :
				// parent.getScene().getWindow());
				// stage.show();

				WebView openBrowser = openBrowser(view, "", false);

				return openBrowser.getEngine();
			}
		});

		engine.setOnAlert(ev -> {
			DialogUtil.showMessageDialog(ev.getData());
		});

		engine.setConfirmHandler(new Callback<String, Boolean>() {

			@Override
			public Boolean call(String param) {
				Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("Confirm.", param);
				if (showYesOrNoDialog.isPresent()) {
					Pair<String, String> pair = showYesOrNoDialog.get();
					if (pair == null)
						return false;
					return "Y".equals(pair.getValue());
				}
				return false;
			}
		});

		engine.setOnAlert((WebEvent<String> wEvent) -> {
			LOGGER.debug("Alert Event  -  Message: {}  ", wEvent.getData());
		});
		if (isLink)
			engine.load(content);
		else
			engine.loadContent(content);

		BorderPane root = new BorderPane(view);

		HBox linkGroup = new HBox(5);
		linkGroup.setAlignment(Pos.CENTER_LEFT);
		linkGroup.setPadding(new Insets(5));
		TextField txtLink = new TextField(content);
		HBox.setHgrow(txtLink, Priority.ALWAYS);
		txtLink.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
			if (KeyCode.ENTER == ev.getCode())
				engine.load(txtLink.getText());
		});

		linkGroup.getChildren().add(new Label("URL : "));
		linkGroup.getChildren().add(txtLink);

		root.setTop(linkGroup);

		engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				// if (newValue == State.SUCCEEDED) {
				String location = engine.getLocation();
				txtLink.setText(location);
				// }
			}
		});

		FxUtil.createStageAndShow(new Scene(root, BROWSER_WIDTH, BROWSER_HEIGHT), stage -> {

			stage.initOwner(parent == null ? (Window) null : parent.getScene() == null ? null : parent.getScene().getWindow());
			stage.setOnCloseRequest(ev -> {

				// 메모리 릭 방지.
				engine.load("about:blank");
			});
		});

		return view;
	}

	public enum POPUP_STYLE {
		POP_OVER, POPUP
	}

	/**
	 * 테이블뷰에 더블클릭하면 팝업이 열리는 기능을 install 처리한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 28.
	 * @param tbMetadata
	 */
	@SuppressWarnings("rawtypes")
	public static <T> void installDoubleClickPopup(POPUP_STYLE style, TableView<T> tbMetadata) {
		installDoubleClickPopup(null, style, tbMetadata);
	}

	public static <T> void installDoubleClickPopup(Window owner, POPUP_STYLE style, TableView<T> tbMetadata) {
		tbMetadata.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {

			if (MouseButton.PRIMARY == ev.getButton() && ev.getClickCount() == 2) {
				if (ev.isConsumed())
					return;

				ObservableList<TablePosition> selectedCells = tbMetadata.getSelectionModel().getSelectedCells();
				if (selectedCells == null || selectedCells.isEmpty())
					return;

				TablePosition tablePosition = selectedCells.get(0);
				int column = tablePosition.getColumn();
				int row = tablePosition.getRow();

				if (column == -1)
					return;
				if (row == -1)
					return;

				// tablePosition.getTableColumn().
				Object valueByConverter = FxTableViewUtil.getValue(tbMetadata, column, row);
				String value = "";
				if (ValueUtil.isNotEmpty(valueByConverter)) {
					value = valueByConverter.toString();
				}

				/**
				 * @최초생성일 2016. 11. 28.
				 */
				final double WIDTH = 500d;
				final double HEIGHT = 400d;

				JavaTextArea createJavaTextArea = createJavaTextArea(value);
				createJavaTextArea.setPrefSize(WIDTH, HEIGHT);

				switch (style) {
				case POP_OVER:
					FxUtil.showPopOver(tbMetadata, createJavaTextArea);
					break;
				case POPUP:

					createSimpleTextAreaAndShow(value, stage -> {
						stage.setTitle("Show Values");
						stage.setWidth(WIDTH);
						stage.setHeight(HEIGHT);
						stage.initStyle(StageStyle.UTILITY);
						stage.initOwner(owner);
						stage.focusedProperty().addListener((oba, o, n) -> {
							if (!n)
								stage.close();
						});
						stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
							if (KeyCode.ESCAPE == event.getCode()) {
								if (!event.isConsumed()) {
									stage.close();
								}
							}
						});
					});
					break;
				}

			}

		});
	}

	public static class FONTUtil {

		/**
		 * 디폴트 폰트 리턴
		 *
		 * @작성자 : KYJ
		 * @작성일 : 2016. 12. 2.
		 * @return
		 */
		public static Font getDefaultFont() {
			return Font.getDefault();
		}

		/**
		 * 폰트명들을 리턴
		 *
		 * @return
		 * @작성자 : KYJ
		 * @작성일 : 2016. 12. 2.
		 */
		public static List<String> getAvaliableFontNames() {
			return Font.getFontNames();
		}

		public static List<String> getAvaliableFontFamilis() {
			return Font.getFamilies();
		}

		/**
		 * Font Styles 리턴
		 *
		 * @작성자 : KYJ
		 * @작성일 : 2016. 12. 2.
		 * @return
		 */
		public static List<String> getFontStyles() {
			return Stream.of(FontPosture.values()).map(s -> s.name()).collect(Collectors.toList());
		}

		/**
		 * Font Weight 정보 리턴
		 *
		 * @작성자 : KYJ
		 * @작성일 : 2016. 12. 2.
		 * @return
		 */
		public static List<String> getFontWeights() {
			return Stream.of(FontWeight.values()).map(s -> s.name()).collect(Collectors.toList());
		}

	}

	/**
	 * 다른이름으로 저장처리를 하기위한 형식을 정의한 클래스
	 * 
	 * @author KYJ
	 *
	 */
	public static interface SaveAsModel {
		public default Charset getEncoding() {
			return Charset.forName("UTF-8");
		}

		public abstract String getContent();

		public default Consumer<FileChooser> getFileChooserOption() {
			// FileChooset에 대한 정의처리.
			return chooser -> {

			};
		}

		public default void onSuccess(File f) {

		}

		/**
		 * 파일 쓰기 처리중 에러가 발생한경우
		 * 
		 * @작성자 : KYJ
		 * @작성일 : 2017. 1. 13.
		 * @return
		 */
		public default Consumer<Exception> onError() {
			return err -> LOGGER.error(ValueUtil.toString(err));
		}
	}

	/**
	 * 다른이름으로 저장 처리에 대한 공통 API
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 13.  <br/>
	 * 				  2017. 10. 20  리턴값 추가. 
	 * @param owner
	 * @param model
	 * @return 
	 *
	 */
	public static <T> File saveAsFx(Window owner, SaveAsModel model) {
		if (model == null) {
			throw new NullPointerException("SaveAsModel is null");
		}

		File saveAs = DialogUtil.showFileSaveCheckDialog(owner, model.getFileChooserOption());
		if (saveAs == null)
			return null;

		if (!saveAs.exists()) {
			try {
				saveAs.createNewFile();
			} catch (IOException e) {
				LOGGER.error(ValueUtil.toString(e));
				Consumer<Exception> onError = model.onError();
				if (onError != null)
					model.onError().accept(e);
				else
					throw new RuntimeException(e);
			}
		}

		if (saveAs.exists()) {
			FileUtil.writeFile(saveAs, model.getContent(), model.getEncoding(), model.onError());
			model.onSuccess(saveAs);
		}
		
		return saveAs;

	}

	/**
	 * tableView의 item을 엑셀파일로 전환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 31.
	 * @param saveFile
	 * @param tableView
	 */
	public static void exportExcelFile(File saveFile, TableView<?> tableView) {

		List<Map<String, Object>> items = tableView.getItems().stream().map(v -> {
			if (v instanceof Map) {
				return (Map<String, Object>) v;
			}
			return (Map<String, Object>) ObjectUtil.toMap(v);
		}).collect(Collectors.toList());

		// ObservableList<Map<String, Object>> items = this.tbResult.getItems();
		ToExcelFileFunction toExcelFileFunction = new ToExcelFileFunction();
		List<String> columns = tableView.getColumns().stream().map(col -> col.getText()).collect(Collectors.toList());
		toExcelFileFunction.generate0(saveFile, columns, items);
		DialogUtil.showMessageDialog("complete...");
	}

	/**
	 * 테이블컬럼에서 화면에 보여주는 텍스트를 리턴한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 31.
	 * @param tc
	 * @param row
	 * @return
	 */
	public static String getDisplayText(TableColumn<?, ?> tc, int row) {
		return FxTableViewUtil.getDisplayText(tc, row).toString();
	}

	public static Object getValue(TableView<?> table, TableColumn<?, ?> column, int rowIndex) {
		return FxTableViewUtil.getValue(table, column, rowIndex);
	}

	public static Consumer<Exception> DEFAULT_LOGGER = err -> {
		LOGGER.error(ValueUtil.toString(err));
	};

	private static final float CENTER_ON_SCREEN_X_FRACTION = 1.0f / 2;
	private static final float CENTER_ON_SCREEN_Y_FRACTION = 1.0f / 3;

	public Point2D getCenter(Parent parent) {
		Stage window = (Stage) parent.getScene().getWindow();
		return getCenter(window);
	}

	public Point2D getCenter(Stage window) {

		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		double centerX = bounds.getMinX() + (bounds.getWidth() - window.getWidth()) * CENTER_ON_SCREEN_X_FRACTION;
		double centerY = bounds.getMinY() + (bounds.getHeight() - window.getHeight()) * CENTER_ON_SCREEN_Y_FRACTION;

		return new Point2D(centerX, centerY);
	}

	public static class Animation {

		public static Transition createTransition(Node node, AnimationType type) {
			return AnimationUtils.createTransition(node, type);
		}

		public static Transition createFadeInTransition(Node node) {
			return createTransition(node, AnimationType.FADE_IN);
		}

		public static Transition createBounceInTransition(Node node) {
			return createTransition(node, AnimationType.BOUNCE_IN);
		}
	}

	/**
	 * [start notifycation] 2017-06-08 kyj notifycation
	 */
	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 8.
	 * @param cont
	 */
	public static void showNotification(final String cont) {
		showNotification("", cont);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 8.
	 * @param title
	 * @param cont
	 */
	public static void showNotification(final String title, final String cont) {
		showNotification(null, title, cont, Pos.BOTTOM_RIGHT);
	}

	public static void showNotification(final String title, final String cont, Pos pos) {
		showNotification(null, title, cont, pos);
	}

	public static void showNotification(Node graphics, final String title, final String cont, Pos pos) {
		showNotification(() -> {
			return GargoyleNotification.create().darkStyle();
		}, n -> {

			if (graphics != null)
				n.graphic(graphics);

			n.text(cont);
			n.title(title);
			n.position(pos);
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 8.
	 * @param suppl
	 * @param action
	 */
	public static void showNotification(Supplier<GargoyleNotification> suppl, Consumer<GargoyleNotification> action) {
		GargoyleNotification create = suppl.get();
		action.accept(create);
		create.show();
	}

	/**
	 * excel Export 기능이 있는 메뉴 아이템을 리턴.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 18.
	 * @param target
	 * @return
	 */
	public static <T> MenuItem createMenuItemExcelExport(TableView<T> target) {
		return FxTableViewUtil.EasyMenuItem.createExcelExportMenuItem(target);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 1.
	 * @param owner
	 * @return
	 */
	public static File showFileOpenDialog(Window owner) {
		return DialogUtil.showFileDialog(owner);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 1.
	 * @param owner
	 * @param option
	 * @return
	 */
	public static File showFileOpenDialog(Window owner, Consumer<FileChooser> option) {
		return DialogUtil.showFileDialog(owner, option);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 18.
	 * @return
	 */
	public static MouseEvent mouseEventForDummy() {
		// NamedArg("eventType") EventType<? extends MouseEvent> eventType,
		// @NamedArg("x") double x, @NamedArg("y") double y,
		// @NamedArg("screenX") double screenX, @NamedArg("screenY") double
		// screenY,
		// @NamedArg("button") MouseButton button,
		// @NamedArg("clickCount") int clickCount,
		// @NamedArg("shiftDown") boolean shiftDown,
		// @NamedArg("controlDown") boolean controlDown,
		// @NamedArg("altDown") boolean altDown,
		// @NamedArg("metaDown") boolean metaDown,
		// @NamedArg("primaryButtonDown") boolean primaryButtonDown,
		// @NamedArg("middleButtonDown") boolean middleButtonDown,
		// @NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
		// @NamedArg("synthesized") boolean synthesized,
		// @NamedArg("popupTrigger") boolean popupTrigger,
		// @NamedArg("stillSincePress") boolean stillSincePress,
		// @NamedArg("pickResult") PickResult pickResult

		return new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false,
				false, false, false, null);
	}
}
