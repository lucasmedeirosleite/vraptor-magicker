package br.com.caelum.vraptor.magicker;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

public class MagickerTest {

	private Magicker magicker;
	private UploadedFile file;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp(){
		magicker = new DefaultMagicker();
		file = Mockito.mock(UploadedFile.class);
	}
	
	@Test
	public void should_not_accept_null_input_stream(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Stream should not be null");
		this.magicker.takeImageStream(null);
	}
	
	@Test
	public void should_return_magick_image() throws MagickException{
		MagickImage image = this.magicker.takeImageStream(this.getClass().getResourceAsStream("caelum.png")).getImage();
		assertNotNull(image);
		assertThat(image.getImageFormat(), is(equalTo("PNG")));
	}
	
	@Test
	public void should_warn_me_when_could_not_get_image(){
		this.thrown.expect(MagickerException.class);
		this.magicker.takeImageStream(this.getClass().getResourceAsStream("blah.png")).getImage();
	}
	
	@Test
	public void should_resize_image() throws MagickException{
		
		int width = 300;
		int height = 350;
		
		MagickImage image = this.magicker.takeImageStream(this.getClass().getResourceAsStream("caelum.png")).resizeTo(width, height).getImage();
		
		assertThat(image.getDimension().getWidth(), is(equalTo(new Double(width))));
		assertThat(image.getDimension().getHeight(), is(equalTo(new Double(height))));
		
		
	}
	
	@Test
	public void should_not_accept_null_byte_array(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Bytes should not be null");
		this.magicker.takeImageBytes(null);
	}
	
	@Test
	public void should_not_accept_empty_byte_array(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Bytes should not be empty");
		this.magicker.takeImageBytes(new byte[0]);
	}
	
	@Test
	public void should_return_magick_image_when_pass_byte_array() throws IOException, MagickException{
		byte[] bytes = IOUtils.toByteArray(this.getClass().getResourceAsStream("caelum.png"));
		MagickImage image = this.magicker.takeImageBytes(bytes).getImage();
		assertNotNull(image);
		assertThat(image.getImageFormat(), is(equalTo("PNG")));
	}
	
	@Test
	public void should_not_accept_null_uploaded_file(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("UploadedFile should not be null");
		this.magicker.takeImageUploaded(null);
	}
	
	@Test
	public void should_return_magick_image_when_pass_uploaded_file() throws IOException, MagickException{
		Mockito.when(file.getFile()).thenReturn(this.getClass().getResourceAsStream("caelum.png"));
		MagickImage image = this.magicker.takeImageUploaded(file).getImage();
		assertNotNull(image);
		assertThat(image.getImageFormat(), is(equalTo("PNG")));
	}
	
	@Test
	public void should_not_accept_null_path(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Path should not be null or empty");
		this.magicker.takeImagePath(null);
	}
	
	@Test
	public void should_not_accept_empty_path(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Path should not be null or empty");
		this.magicker.takeImagePath("");
	}
	
	@Test
	public void should_return_magick_image_when_pass_image_path() throws IOException, MagickException{
		MagickImage image = this.magicker.takeImagePath(this.getClass().getResource("caelum.png").getPath()).getImage();
		assertNotNull(image);
		assertThat(image.getImageFormat(), is(equalTo("PNG")));
	}
	
	
}
