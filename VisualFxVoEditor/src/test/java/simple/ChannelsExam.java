package simple;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.junit.Test;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2016. 7. 30.
 *	작성자   : KYJ
 *******************************/

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class ChannelsExam {

	@Test
	public void catConsumer() throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (FileInputStream fis = new FileInputStream("pom.xml")) {
			byte[] b = new byte[1024];
			while (fis.read(b) != -1) {
				byteArrayOutputStream.write(b);
			}
		}

		System.out.println(byteArrayOutputStream.toString("UTF-8"));

		byteArrayOutputStream.close();
	}

	public void catConsumer(InputStream src, WritableByteChannel dest) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
		ReadableByteChannel newChannel = Channels.newChannel(src);

		while (newChannel.read(buffer) != -1) {
			buffer.flip();
			dest.write(buffer);
			buffer.compact();
		}

		buffer.flip();

		while (buffer.hasRemaining()) {
			dest.write(buffer);
		}
	}

	public static void copyStreamContent(InputStream is, OutputStream os) throws IOException {
		ReadableByteChannel inChannel = Channels.newChannel(is);
		WritableByteChannel outChannel = Channels.newChannel(os);

		// TODO make this configurable
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int read;

		while ((read = inChannel.read(buffer)) > 0) {
			buffer.rewind();
			buffer.limit(read);

			while (read > 0) {
				read -= outChannel.write(buffer);
			}

			buffer.clear();
		}
	}

}
