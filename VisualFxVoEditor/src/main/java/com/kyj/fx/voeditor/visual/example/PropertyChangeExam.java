package com.kyj.fx.voeditor.visual.example;

import java.lang.reflect.Method;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;

import com.sun.javafx.binding.ExpressionHelper;

public class PropertyChangeExam extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	public void propertyCopy2(Node from, Node to) {
		ObservableList<Item> properties = BeanPropertyUtils.getProperties(from);
		ObservableList<Item> properties2 = BeanPropertyUtils.getProperties(from);

		properties.forEach(item -> {
			// System.out.println(item.getCategory() + "/ name :  " +
			// item.getName() + " value : " + item.getValue());

				setProperty(to, item.getName(), item.getValue());
			});
		properties2.addAll(properties);

	}

	public void setProperty(Node node, String name, Object value) {
		try {
			if (value == null)
				return;
			Method method = node.getClass().getMethod(name + "Property");
			if (method == null)
				return;

			method.setAccessible(true);

			Object fromPropertValue = method.invoke(node);
			if (Property.class.isAssignableFrom(fromPropertValue.getClass())) {
				if (WritableValue.class.isAssignableFrom(fromPropertValue.getClass())) {

					Method setMethod = null;
					if (fromPropertValue instanceof BooleanProperty || fromPropertValue instanceof DoubleProperty) {
						setMethod = fromPropertValue.getClass().getMethod("setValue", Object.class);
					} else {
						setMethod = fromPropertValue.getClass().getMethod("set", Object.class);
					}

					if (setMethod != null) {
						setMethod.setAccessible(true);
						if (value.getClass().isPrimitive()) {
							setMethod.invoke(fromPropertValue, value.getClass());
						} else {
							setMethod.invoke(fromPropertValue, value);
						}

					}
				}
				else
				{
					
				}
			}
			else
			{
				System.out.println(fromPropertValue);
			}
		} catch (Exception e) {
			System.out.println("error prop name : " + name + " value : " + value + " value type : " + value.getClass());
			// e.printStackTrace();
		}

	}
	public void propertyCopy(Node from, Node to) throws Exception {
		Method[] fromMethods = from.getClass().getMethods();

		Class<? extends Node> class1 = to.getClass();
		for (Method fromMethod : fromMethods) {
			String methodName = fromMethod.getName();
			if (methodName.endsWith("Property")) {

				Method targetMethod = class1.getMethod(methodName);
				targetMethod.setAccessible(true);
				if (targetMethod != null) {
					Object fromPropertValue = fromMethod.invoke(from);
					Object toPropertValue = targetMethod.invoke(from);

					if (Property.class.isAssignableFrom(fromPropertValue.getClass())) {
						if (WritableValue.class.isAssignableFrom(fromPropertValue.getClass())) {

							// System.out.println("Writable : " +
							// fromPropertValue);
							// if
							// (StyleConverter.class.isAssignableFrom(fromPropertValue.getClass()))
							// {

							Property fromProp = (Property) fromPropertValue;
							Property toProp = (Property) toPropertValue;
							Object value = fromProp.getValue();
							if (value == null)
								continue;
							System.out.println("set value : " + fromProp.getValue());
							toProp.setValue(fromProp.getValue());
						}

						// }
					}
				}

			}
		}

	}

	private ExpressionHelper<String> helper = null;
	@Override
	public void start(Stage primaryStage) throws Exception {

		TextArea textArea = new TextArea("");
		Button button = new Button("ssss");
		button.setStyle("-fx-base: red;");
		button.setFont(new Font(55));
		Button button2 = new Button("ssss2");

		// button2.textProperty().bind(button.textProperty());
		// button2.styleProperty().bind(button.styleProperty());
		// button2.getStyleClass().addAll(button.getStyleClass());

		propertyCopy2(button, button2);
		//
		// button.setVisible(false);
		// propertyCopy(button, button2);

		// List<CssMetaData<? extends Styleable, ?>> cssMetaData =
		// button.getCssMetaData();
		// cssMetaData.forEach(cssMeta -> {
		// String property = cssMeta.getProperty();
		// System.out.print(property);
		// try {
		// String ss = String.valueOf(cssMeta.getInitialValue(null));
		//
		// System.out.println(" : " + ss);
		// } catch (NullPointerException e) {
		//
		// }
		//
		// });
		// button.setStyle(button.getStyle());

		// button2.styleProperty().set(button.getStyle());
		// button2.setSkin(button.getSkin());

		// button2.getCssMetaData().clear();
		// button2.getCssMetaData().addAll(button.getControlCssMetaData());
		// button2.fire();
		//
		// ExpressionHelper.fireValueChangedEvent(helper);
		// button2.getCssMetaData().addAll(button.getControlCssMetaData());
		// button2.textProperty().bind(button.textProperty());
		// button2.styleProperty().bind(button.styleProperty());
		// button2.visibleProperty().bind(button.visibleProperty());
		// button2.disableProperty().bind(button.disableProperty());
		// button2.fontProperty().bind(button.fontProperty());

		textArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> oba, String oldval, String newval) {
				button.setStyle(newval);
				button2.setStyle(newval);
			}
		});

		AnchorPane root = new AnchorPane(new HBox(textArea, button, button2));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
