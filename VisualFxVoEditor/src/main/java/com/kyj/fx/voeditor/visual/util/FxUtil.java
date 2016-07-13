/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleException;
import com.kyj.fx.voeditor.visual.framework.InstanceTypes;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.momory.FxMemory;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
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
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 * Javafx UI 관련 유틸리티 클래스
 *
 * @author KYJ
 *
 */
public class FxUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FxUtil.class);

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
	 * @throws GagoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <T> T load(Class<?> controllerClass) throws GagoyleException, NullPointerException, IOException {
		return load(controllerClass, null, null, null);
	}

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * FXMLController에 정의된 내용을 기준으로 FXML을 로드한다.
	 *
	 * @param controllerClass
	 * @param option
	 *            FXML을 로드한후 후처리할 내용을 기입한다.
	 * @return
	 * @throws GagoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <T, C> T load(Class<C> controllerClass, Consumer<T> option) throws GagoyleException, NullPointerException, IOException {
		return load(controllerClass, null, option, null);
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
	 * @throws GagoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 */
	public static <N, C> N loadAndControllerAction(Class<C> controllerClass, Consumer<C> controllerAction)
			throws GagoyleException, NullPointerException, IOException {
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
	 * @throws GagoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <T, C> T loadRoot(Class<C> controllerClass, Object instance) throws GagoyleException, NullPointerException, IOException {
		return load(controllerClass, instance, null, null);
	}

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * FXMLController에 정의된 내용을 기준으로 FXML을 로드한다.
	 *
	 * @param controllerClass
	 * @return
	 * @throws GagoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 ********************************/
	public static <T, C> T loadRoot(Class<C> controllerClass)
			throws GagoyleException, NullPointerException, IOException, InstantiationException, IllegalAccessException {
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
	public static <T, C> T loadRoot(Class<C> controllerClass, Consumer<Exception> errorCallback) {
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
	 * @throws GagoyleException
	 * @throws NullPointerException
	 * @throws IOException
	 ********************************/
	public static <N, C> N load(Class<C> controllerClass, Object rootInstance, Consumer<N> option, Consumer<C> controllerAction)
			throws GagoyleException, NullPointerException, IOException {
		if (controllerClass == null)
			throw new NullPointerException("controller is null.");

		String fullClassName = controllerClass.getCanonicalName();

		FXMLController controller = controllerClass.getAnnotation(FXMLController.class);
		if (controller == null) {
			throw new GagoyleException("this is not FXMLController. check @FXMLController");
		}
		String fxml = controller.value();

		if (ValueUtil.isEmpty(fxml))
			throw new IllegalArgumentException("value is empty..");

		InstanceTypes type = controller.instanceType();
		N newInstance = null;
		switch (type) {
		case RequireNew:
			newInstance = newInstance(controllerClass, rootInstance, option, controller, fxml, controllerAction);
			break;
		case Singleton:
			Node node = FxMemory.get(fullClassName);
			if (node == null) {
				newInstance = newInstance(controllerClass, rootInstance, option, controller, fxml, controllerAction);
				FxMemory.put(fullClassName, (Node) newInstance);
			} else {
				newInstance = (N) node;
			}

			break;
		}

		return newInstance;
	}

	private static <T, C> T newInstance(Class<?> controllerClass, Object rootInstance, Consumer<T> option, FXMLController controller,
			String fxml, Consumer<C> controllerAction) throws GagoyleException, IOException {
		URL resource = controllerClass.getResource(fxml);

		FXMLLoader loader = new FXMLLoader(resource);

		if (controller.isSelfController() && rootInstance != null) {
			try {
				loader.setRoot(rootInstance);
				loader.setController(rootInstance);
			} catch (Exception e) {
				throw new GagoyleException(e);
			}
		}

		T load = loader.load();
		C instanceController = loader.getController();

		// show warning...
		if (load == null) {
			LOGGER.warn("load result is empty.. controller class : {} ", controllerClass);
		}

		Method[] declaredMethods = controllerClass.getDeclaredMethods();

		// findfirst로 수정. @FxPostInitialize가 여러건있는경우를 잘못된 로직 유도를 방지.
		Stream.of(declaredMethods).filter(m -> m.getParameterCount() == 0 && m.getAnnotation(FxPostInitialize.class) != null).findFirst()
				.ifPresent((m -> {
					if (m.getModifiers() == Modifier.PUBLIC) {
						try {
							m.invoke(instanceController);
						} catch (Exception e) {
							LOGGER.error(ValueUtil.toString(e));
						}
					}
				}));

		if (option != null) {
			option.accept(load);
		}

		if (controllerAction != null)
			controllerAction.accept(instanceController);

		return load;
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
		createStageAndShow(new Scene(new BorderPane(parent)), false);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param isModal
	 */
	public static void createStageAndShow(Node parent, boolean isModal) {
		createStageAndShow(new Scene(new BorderPane(parent)), isModal);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param option
	 */
	public static void createStageAndShow(Node parent, Consumer<Stage> option) {
		createStageAndShow(new Scene(new BorderPane(parent)), option);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param option
	 */
	public static void createStageAndShow(Parent parent, Consumer<Stage> option) {
		createStageAndShow(new Scene(parent), option);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param parent
	 * @param isModal
	 */
	public static void createStageAndShow(Parent parent, boolean isModal) {
		createStageAndShow(new Scene(parent), isModal);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param scene
	 * @param isModal
	 */
	public static void createStageAndShow(Scene scene, boolean isModal) {
		Consumer<Stage> option = null;
		if (isModal) {
			option = stage -> {
				stage.setAlwaysOnTop(true);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(stage);
				stage.show();
			};
		} else {
			option = stage -> {
				stage.show();
			};
		}

		createStageAndShow(scene, option);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 */
	public static void createStageAndShow(Scene scene, Consumer<Stage> option) {
		Stage stage = new Stage();
		stage.setScene(scene);

		if (option != null)
			option.accept(stage);

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

		try {
			FileOutputStream out = new FileOutputStream(saveFile);
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

	public static void snapShot(Node target, OutputStream out, int requestWidth, int requestHeight, Consumer<Exception> errorCallback) {

		if (target == null)
			throw new NullPointerException("target Node is empty.");

		if (out == null)
			throw new NullPointerException("target Stream is empty.");

		SnapshotParameters params = new SnapshotParameters();
		params.setDepthBuffer(true);
		//		params.setFill(Color.CORNSILK);

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
		//		if (out != null)
		//			out.close();
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
		//		PrinterAttributes printerAttributes = printer.getPrinterAttributes();
		//		
		Paper a4 = Paper.A4;

		//		Paper a4 = PrintHelper.createPaper("Rotate A4", Paper.A4.getHeight(), Paper.A4.getWidth(), Units.MM);
		PageLayout pageLayout = printer.createPageLayout(a4, PageOrientation.REVERSE_PORTRAIT, MarginType.DEFAULT);

		PrinterJob printerJob = PrinterJob.createPrinterJob();

		//		JobSettings jobSettings = printerJob.getJobSettings();
		//		jobSettings.setPrintSides(PrintSides.TUMBLE);
		ImageView imageView = new ImageView();
		//화면 사이즈에 맞게 크기 조절.
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

}
