/**
 * package : com.kyj.fx.voeditor.visual.util
 *	fileName : FxClipboardUtil.java
 *	date      : 2015. 11. 10.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * 자바 FX 클립보드 관련 유틸리티 클래스
 *
 * @author KYJ
 *
 */
public class FxClipboardUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(FxClipboardUtil.class);

	private FxClipboardUtil() {
	}

	/**
	 * 문자열을 클립보드에 복사
	 *
	 * @param clipTarget
	 */
	public static void putString(String clipTarget) {
		Clipboard.getSystemClipboard().clear();
		ClipboardContent clipboardContent = new ClipboardContent();

		clipboardContent.putString(clipTarget);
		boolean result = Clipboard.getSystemClipboard().setContent(clipboardContent);
		LOGGER.debug(String.format("clipboard set content result :%b", result));

	}

	/**
	 * 문자열을 클립보드에 복사
	 *
	 * @param clipTarget
	 */
	public static String pastString() {
		Clipboard systemClipboard = Clipboard.getSystemClipboard();
		return systemClipboard.getString();
	}

	/**
	 * 문자열을 클립보드에 복사
	 *
	 * @param clipTarget
	 */
	public static Image pastImage() {
		Clipboard systemClipboard = Clipboard.getSystemClipboard();
		return systemClipboard.getImage();
	}

	public static final int EMPTY = 0x000;
	public static final int FILE = 0x001;
	public static final int HTML = 0x02;
	public static final int IMAGE = 0x004;
	public static final int RTF = 0x010;
	public static final int URL = 0x020;
	public static final int STRING = 0x040;

	/**
	 * 클릭보드에 저장된 타입을 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 16.
	 * @return
	 */
	public static int getCipboardContentTypes() {

		int type = EMPTY;
		boolean hasFiles = Clipboard.getSystemClipboard().hasFiles();
		boolean hasHtml = Clipboard.getSystemClipboard().hasHtml();
		boolean hasImage = Clipboard.getSystemClipboard().hasImage();
		boolean hasRtf = Clipboard.getSystemClipboard().hasRtf();
		boolean hasUrl = Clipboard.getSystemClipboard().hasUrl();
		boolean hasString = Clipboard.getSystemClipboard().hasString();

		if (hasFiles) {
			type |= FILE;
		} else if (hasUrl) {
			type |= URL;
		}

		if (hasHtml) {
			type |= HTML;
		}
		if (hasImage) {
			type |= IMAGE;
		}
		if (hasRtf) {
			type |= RTF;
		}

		if (hasString) {
			type |= STRING;
		}

		return type;
	}

	public static List<File> pasteFiles() {
		return Clipboard.getSystemClipboard().getFiles();
	}

	public static String pasteUrl() {
		return Clipboard.getSystemClipboard().getUrl();
	}

	/********************************
	 * 작성일 : 2016. 5. 12. 작성자 : KYJ
	 *
	 * 테이블 셀 copy & paste 기능을 추가한다.
	 *
	 * @param table
	 ********************************/
	public static void installCopyPasteHandler(TableView<?> table) {
		FxTableClipboardUtil.installCopyPasteHandler(table);
	}
}
