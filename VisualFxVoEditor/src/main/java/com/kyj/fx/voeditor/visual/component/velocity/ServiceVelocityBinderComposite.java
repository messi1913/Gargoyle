package com.kyj.fx.voeditor.visual.component.velocity;

import javafx.scene.layout.BorderPane;

public class ServiceVelocityBinderComposite extends BorderPane {

	private AbstractVelocityBinderComposite composite;

	public ServiceVelocityBinderComposite(AbstractVelocityBinderComposite composite) {
		super();
		this.composite = composite;
		this.setCenter(this.composite);
	}

}
