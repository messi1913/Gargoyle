package com.kyj.fx.voeditor.visual.component.velocity;

import javafx.scene.layout.BorderPane;

public class ServiceVelocityBinderComposite extends BorderPane {

	private VelocityBinderComposite composite;

	public ServiceVelocityBinderComposite(VelocityBinderComposite composite) {
		super();
		this.composite = composite;
		this.setCenter(this.composite);
	}

}
