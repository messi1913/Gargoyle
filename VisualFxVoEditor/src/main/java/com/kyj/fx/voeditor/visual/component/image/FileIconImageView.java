/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.image
 *	작성일   : 2017. 10. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.image;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

/**
 * @author KYJ
 *
 */
public class FileIconImageView extends ImageView {
	private File file;

	public FileIconImageView(File file) {
		this.file = file;
		setImage(this.file, 25, 25);
	}

	public FileIconImageView(javafx.scene.image.Image img) {
		super.setImage(img);
	}

	private Icon getIcon(File file) {
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		Icon icon = fileSystemView.getSystemIcon(file);
		return icon;
	}

	private void setImage(File file, int width, int height) {

		if (file == null || !file.exists())
			return;

		Icon icon = getIcon(file);

		if (icon == null) {
			setImage((BufferedImage) null);
			return;
		}
		java.awt.Image image = ((ImageIcon) icon).getImage();
		java.awt.Image scaledInstance = image.getScaledInstance(width, width, 4);

		BufferedImage bufferedImage = new BufferedImage(width, height, 2);
		icon = new ImageIcon(scaledInstance);
		icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		setImage(bufferedImage);
	}

	private void setImage(File file) {
		Icon icon = getIcon(file);
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), 2);
		icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		setImage(bufferedImage);
	}

	private void setImage(BufferedImage bufferedImage) {
		javafx.scene.image.Image fxImage = null;
		if (bufferedImage != null) {
			fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
		}
		if (fxImage != null) {
			setImage(fxImage);
		}
	}
}
