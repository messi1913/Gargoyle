/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2017. 6. 15.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.AnnotationOptions;
import com.kyj.fx.voeditor.visual.component.grid.CommonsBaseGridView;
import com.kyj.fx.voeditor.visual.component.grid.CrudBaseGridView;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public class ZipFileViewerComposite extends BorderPane implements Runnable {

	private CrudBaseGridView<ZipEntryDVO> crudBaseGridView;
	private File zipFile;

	public ZipFileViewerComposite() {
		this(null);
	}

	/**
	 * @READ_SIZE_LIMIT 값에 따라 읽을 대상 파일의 내용 제한을 둔다.
	 * @최초생성일 2017. 6. 15.
	 */
	private static final long READ_SIZE_LIMIT = 1024 * 1024 * 30;

	/**
	 * Zip 파일내에서 바로 읽을 파일 확장자 정의
	 * @최초생성일 2017. 6. 15.
	 */
	private String[] readFileFilter = { ".html", ".txt", ".java", ".long" };

	/**
	 * 실행파일 리스트
	 * @최초생성일 2017. 6. 16.
	 */
	private String[] excuteFileFilter = { ".mp3" };

	public ZipFileViewerComposite(File zipFile) {
		this.zipFile = zipFile;

		crudBaseGridView = new CrudBaseGridView<ZipEntryDVO>(ZipEntryDVO.class, new AnnotationOptions<ZipEntryDVO>(ZipEntryDVO.class) {

			@Override
			public int useButtons() {
				return 0;
			}

			@Override
			public String style(String columnName) {
				if ("compressedSize".equals(columnName) || "size".equals(columnName)) {
					return "-fx-alignment : center-right; ";
				} else if ("crc".equals(columnName)) {
					return "-fx-alignment : center; ";
				}
				return super.style(columnName);
			}

			@Override
			public int columnSize(String columnName) {
				if ("name".equals(columnName))
					return 600;
				return super.columnSize(columnName);
			}

			@Override
			public StringConverter<Object> stringConverter(String columnName) {

				if ("compressedSize".equals(columnName) || "size".equals(columnName)) {
					return new StringConverter<Object>() {

						@Override
						public String toString(Object object) {
							if (object instanceof Long) {

								return String.format("%,d", object);
								//								return String.valueOf(object);

							}
							return object.toString();
						}

						@Override
						public Long fromString(String string) {

							return null;
						}
					};
				} else if ("crc".equals(columnName)) {
					return new StringConverter<Object>() {

						@Override
						public String toString(Object object) {
							if (object instanceof Long) {
								return Long.toHexString((long) object);
							}
							return object.toString();
						}

						@Override
						public Long fromString(String string) {

							return null;
						}
					};
				}

				return super.stringConverter(columnName);
			}

			@Override
			public boolean visible(String columnName) {
				if ("time".equals(columnName))
					return false;
				return super.visible(columnName);
			}

		});

		CommonsBaseGridView<ZipEntryDVO> realGrid = crudBaseGridView.getRealGrid();
		FxUtil.installClipboardKeyEvent(realGrid);

		realGrid.setOnMouseClicked(ev -> {
			if (ev.getClickCount() == 2) {

				ZipEntryDVO selectedItem = crudBaseGridView.getSelectionModel().getSelectedItem();

				Service<String> service = new Service<String>() {

					@Override
					protected Task<String> createTask() {
						// TODO Auto-generated method stub
						return new Task<String>() {

							@Override
							protected String call() throws Exception {
								try (ZipFile z = new ZipFile(getZipFile(), Charset.forName("EUC-KR"))) {

									String name = selectedItem.getName();
									ZipEntry entry = z.getEntry(name);
									if (entry == null)
										return null;
									//용량이 너무 크다면 읽지않는다.
									if (entry.getSize() > READ_SIZE_LIMIT) {
										return null;
									}

									Optional<String> readFileAny = Stream.of(readFileFilter).filter(ext -> {
										return name.endsWith(ext);
									}).findAny();

									if (readFileAny.isPresent()) {

										String data = null;
										try (InputStream inputStream = z.getInputStream(entry)) {
											data = ValueUtil.toString(inputStream);
											updateMessage(name);
										}
										return data;

									}

									return null;

								} catch (Exception e) {
									e.printStackTrace();
								}
								return null;
							}
						};
					}

				};

				service.setOnSucceeded(h -> {
					Object value = h.getSource().getValue();
					if (value != null) {
						FxUtil.createCodeAreaAndShow(value.toString());
					}

				});

				service.setExecutor(ExecutorDemons.getGargoyleSystemExecutorSerivce());
				service.start();
			}
		});

		this.setStyle("-fx-padding : 5px;");
		Button btnFullUnZip = new Button("전체 압축 풀기");
		btnFullUnZip.setOnAction(this::btnFullUnZipOnAction);
		Button btnCheckedUnZip = new Button("선택 압축 풀기");
		btnCheckedUnZip.setOnAction(this::btnCheckedUnZipOnAction);
		setTop(new HBox(5, btnFullUnZip, btnCheckedUnZip));
		setCenter(crudBaseGridView);

		if (this.zipFile != null && this.zipFile.exists())
			read();

	}

	/**
	 * 전체 압축 풀기
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15. 
	 * @param e
	 */
	public void btnFullUnZipOnAction(ActionEvent e) {
		ObservableList<ZipEntryDVO> items = crudBaseGridView.getItems();
		System.out.println(items);
	}

	/**
	 * 선택 압축 풀기
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15. 
	 * @param e
	 */
	public void btnCheckedUnZipOnAction(ActionEvent e) {
		List<ZipEntryDVO> items = crudBaseGridView.getItems().stream().filter(v -> v.getClicked()).collect(Collectors.toList());
		System.out.println(items);
	}

	public void read() {
		new Thread(this, "read-zip-thread").start();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15. 
	 * @param ext
	 */
	public void addReadFileFilter(String ext) {
		int newLength = readFileFilter.length + 1;
		String[] newarr = new String[newLength];
		System.arraycopy(readFileFilter, 0, newarr, 0, readFileFilter.length);
		newarr[newLength - 1] = ext;
		readFileFilter = newarr;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15. 
	 * @return
	 */
	public String[] getReadFileFilter() {
		return readFileFilter;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15. 
	 * @param readFileFilter
	 */
	public void setReadFileFilter(String[] readFileFilter) {
		this.readFileFilter = readFileFilter;
	}

	@Override
	public void run() {
		FileInputStream fis = null;
		ZipInputStream zis = null;
		ZipEntry zentry = null;
		List<ZipEntryDVO> arrayList = new ArrayList<ZipEntryDVO>();
		try {
			new BufferedInputStream(fis = new FileInputStream(zipFile)); // FileInputStream
			zis = new ZipInputStream(fis, Charset.forName("EUC-KR")); // ZipInputStream

			while ((zentry = zis.getNextEntry()) != null) {
				String name = zentry.getName();
				if (!zentry.isDirectory()) {
					ZipEntryDVO zipEntryDVO = new ZipEntryDVO();

					String comment = zentry.getComment();
					long compressedSize = zentry.getCompressedSize();
					long crc = zentry.getCrc();
					FileTime creationTime = zentry.getCreationTime();
					FileTime lastAccessTime = zentry.getLastAccessTime();
					long size = zentry.getSize();
					long time = zentry.getTime();

					zipEntryDVO.setName(name);
					zipEntryDVO.setComment(comment);
					zipEntryDVO.setCompressedSize(compressedSize);
					zipEntryDVO.setCrc(crc);
					zipEntryDVO.setCreationTime(creationTime);
					zipEntryDVO.setLastAccessTime(lastAccessTime);
					zipEntryDVO.setSize(size);
					zipEntryDVO.setTime(time);

					arrayList.add(zipEntryDVO);

				}
			}

			Platform.runLater(() -> {
				crudBaseGridView.getItems().addAll(arrayList);
			});

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			close(fis, zis);
		}
	}

	private void close(FileInputStream fis, ZipInputStream zis) {
		if (zis != null) {
			try {
				zis.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

	public static class ZipEntryDVO extends AbstractDVO {
		String name;
		String comment;
		long compressedSize;
		long crc;
		FileTime creationTime;
		FileTime lastAccessTime;
		long size;
		long time;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public long getCompressedSize() {
			return compressedSize;
		}

		public void setCompressedSize(long compressedSize) {
			this.compressedSize = compressedSize;
		}

		public long getCrc() {
			return crc;
		}

		public void setCrc(long crc) {
			this.crc = crc;
		}

		public FileTime getCreationTime() {
			return creationTime;
		}

		public void setCreationTime(FileTime creationTime) {
			this.creationTime = creationTime;
		}

		public FileTime getLastAccessTime() {
			return lastAccessTime;
		}

		public void setLastAccessTime(FileTime lastAccessTime) {
			this.lastAccessTime = lastAccessTime;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

	}

}
