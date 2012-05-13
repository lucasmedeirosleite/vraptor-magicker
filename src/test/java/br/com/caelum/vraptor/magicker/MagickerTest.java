package br.com.caelum.vraptor.magicker;

import static org.junit.Assert.assertNotNull;
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
	
	
}
