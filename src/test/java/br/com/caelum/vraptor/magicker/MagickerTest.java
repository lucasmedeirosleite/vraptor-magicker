package br.com.caelum.vraptor.magicker;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import magick.MagickException;
import magick.MagickImage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MagickerTest {

	private Magicker magicker;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp(){
		magicker = new DefaultMagicker();
	}
	
	@Test
	public void should_not_accept_null_input_stream(){
		this.thrown.expect(MagickerException.class);
		this.thrown.expectMessage("Stream should not be null");
		this.magicker.takeImageStream(null);
	}
	
	@Test
	public void should_return_magick_image(){
		MagickImage image = this.magicker.takeImageStream(this.getClass().getResourceAsStream("caelum.png")).getImage();
		assertNotNull(image);
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
	
	
}
