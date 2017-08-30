/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 7. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import javafx.scene.input.KeyCode;

/**
 * @author KYJ
 *
 */
public class DrawItemBehavior extends BehaviorBase<DrawItem> {

	static final List<KeyBinding> DRAW_ITEM_BINDINGS = new ArrayList<KeyBinding>();

	static {
		DRAW_ITEM_BINDINGS.add(new KeyBinding(KeyCode.DOWN, "down").shortcut());
		DRAW_ITEM_BINDINGS.add(new KeyBinding(KeyCode.UP, "up").shortcut());
		DRAW_ITEM_BINDINGS.add(new KeyBinding(KeyCode.LEFT, "left").shortcut());
		DRAW_ITEM_BINDINGS.add(new KeyBinding(KeyCode.RIGHT, "right").shortcut());
	}

	// @SuppressWarnings("restriction")
	// public DrawItemBehavior(DrawItem control, List<KeyBinding> keyBindings) {
	// super(control, keyBindings);
	// // TODO Auto-generated constructor stub
	// }
	//
	// public DrawItemBehavior(DrawItem control, List<KeyBinding> keyBindings) {
	// super(control, keyBindings);
	// }

	public DrawItemBehavior(DrawItem control) {
		super(control, DRAW_ITEM_BINDINGS);
	}

	@Override
	protected void callAction(String name) {
		switch (name) {
		case "down": {
			DrawItem editor = getControl();
			editor.setLayoutY(editor.getLayoutY() + 1);
		}
			break;
		case "up": {
			DrawItem editor = getControl();
			editor.setLayoutY(editor.getLayoutY() - 1);
		}
			break;
		case "left": {
			DrawItem editor = getControl();
			editor.setLayoutX(editor.getLayoutX() - 1);
		}
			break;
		case "right": {
			DrawItem editor = getControl();
			editor.setLayoutX(editor.getLayoutX() + 1);
		}
			break;

		default:
			super.callAction(name);
		}

	}
}
