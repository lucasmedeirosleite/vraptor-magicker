package br.com.caelum.vraptor.magicker;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

public class MagickerTest {

	private Environment environment;
	private DefaultMagicker magicker;
	private UploadedFile file;
	private String path;
	
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp(){
		path = this.getClass().getResource("caelum.png").getPath().replace("/caelum.png", "");
		environment = Mockito.mock(Environment.class);
		magicker = new DefaultMagicker(environment);
		file = Mockito.mock(UploadedFile.class);
		magicker.load();
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
	
	@Test
	public void should_not_accept_null_title(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Title should not be null or empty");
		InputStream imageStream = this.getClass().getResourceAsStream("caelum.png");
		this.magicker.takeImageStream(imageStream).withTitle(null);
		
	}
	
	@Test
	public void should_not_accept_empty_title(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Title should not be null or empty");
		InputStream imageStream = this.getClass().getResourceAsStream("caelum.png");
		this.magicker.takeImageStream(imageStream).withTitle(null);
	}
	
	@Test
	public void should_save_image_on_disk_using_path_defined_on_environment(){
		Mockito.when(environment.get("magicker.images_path")).thenReturn(path);
		
		String title = "caelum2.png";
		InputStream stream = this.getClass().getResourceAsStream("caelum.png");
		this.magicker.takeImageStream(stream).withTitle(title).save();
		assertNotNull(this.magicker.takeImagePath(path + "/" + title).getImage());
		
	}
	
	@Test
	public void should_not_save_image_on_disk_when_not_provided_a_path_by_developer_and_environment(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("No path defined (check your environment key path (magicker.images_path) or tell the path using method withPath)");
		Mockito.when(environment.get("magicker.images_path")).thenReturn(null);
		
		String title = "caelum2.png";
		InputStream stream = this.getClass().getResourceAsStream("caelum.png");
		this.magicker.takeImageStream(stream).withTitle(title).save();
	}
	
	@Test
	public void should_save_image_on_disk_using_path_defined_by_developer(){
		String title = "caelum2.png";
		InputStream stream = this.getClass().getResourceAsStream("caelum.png");
		this.magicker.takeImageStream(stream).withPath(path).withTitle(title).save();
		assertNotNull(this.magicker.takeImagePath(path + "/" + title).getImage());
	}
	
	@Test
	public void should_save_and_its_thumbnail_with_width_and_height_defined_on_environment() throws MagickException{
	
		Mockito.when(environment.get("magicker.images_path")).thenReturn("/Users/lucasmedeiros/Desenvolvimento/images");
		int width = 30;
		int height = 30;
		Mockito.when(environment.get("magicker.images.thumb.width")).thenReturn(String.valueOf(width));
		Mockito.when(environment.get("magicker.images.thumb.height")).thenReturn(String.valueOf(height));
		
		String title = "caelum2.png";
		InputStream stream = this.getClass().getResourceAsStream("caelum.png");
		this.magicker.takeImageStream(stream).withTitle(title).addThumb().save();
		
		MagickImage thumbImage = this.magicker.takeImagePath("/Users/lucasmedeiros/Desenvolvimento/images" + "/thumb/" + title).getImage();
		assertThat(thumbImage.getDimension().getWidth(), is(equalTo(new Double(width))));
		assertThat(thumbImage.getDimension().getHeight(), is(equalTo(new Double(height))));
		
	}
	
	@After
	public void tearDown(){
		File file = new File(path + "/" + "caelum2.png");
		if(file.exists()){
			file.delete();
		}
	}
	
	
}
