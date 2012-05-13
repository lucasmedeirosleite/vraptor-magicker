package br.com.caelum.vraptor.magicker;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class DefaultMagicker implements Magicker {
	
	private InputStream stream;

	@PostConstruct
	public void load(){
		System.setProperty("jmagick.systemclassloader","false");
	}
	
	@Override
	public Magicker takeImageStream(InputStream imageStream) {
		if(imageStream == null){
			throw new MagickerException("Stream should not be null");
		}
		this.stream = imageStream;
		return this;
	}

	@Override
	public MagickImage getImage() {
		
		try {
			byte[] imageBytes = IOUtils.toByteArray(this.stream);
			ImageInfo info = new ImageInfo();
			MagickImage image = new MagickImage();
			image.allocateImage(info);
			image.blobToImage(info, imageBytes);
			return image; 
		} catch (IOException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
	}

}
