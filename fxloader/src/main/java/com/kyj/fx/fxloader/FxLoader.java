/********************************
 *	프로젝트 : fxloader
 *	패키지   : com.kyj.fx.fxloader
 *	작성일   : 2017. 11. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;

/**
 * @author KYJ
 *
 */
public class FxLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(FxLoader.class);

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
			throw new NullPointerException("this is not FXMLController. check @FXMLController");
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

	private static <C> FXMLController getFxmlController(Class<C> controllerClass) throws Exception {
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
				throw new NullPointerException("this is not FXMLController. check @FXMLController");
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
				throw e;
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
			List<Node> findAllByNodes = findAllByNodes(parent, v -> v instanceof Button);
			findAllByNodes.forEach(v -> {
				GargoyleButtonBuilder.applyStyleClass((Button) v, FxSkinManager.BUTTON_STYLE_CLASS_NAME);
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
}
