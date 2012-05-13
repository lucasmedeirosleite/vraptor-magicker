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
	
	private MagickImage image;

	@PostConstruct
	public void load(){
		System.setProperty("jmagick.systemclassloader","false");
	}
	
	@Override
	public Magicker takeImageStream(InputStream imageStream) {
		if(imageStream == null){
			throw new MagickerException("Stream should not be null");
		}
		this.image = createImage(imageStream);
		return this;
	}

	@Override
	public MagickImage getImage() {
		return this.image;
	}

	@Override
	public Magicker resizeTo(int width, int height) {
		try {
			this.image = this.image.scaleImage(width, height);
			return this;
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
	}
	
	private MagickImage createImage(InputStream stream){
		try {
			byte[] imageBytes = IOUtils.toByteArray(stream);
			return createImageUsingBytes(imageBytes);
		} catch (IOException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
	}

	@Override
	public Magicker takeImageBytes(byte[] bytes) {
		
		if(bytes == null){
			throw new MagickerException("Bytes should not be null");
		}
		
		if(bytes.length == 0){
			throw new MagickerException("Bytes should not be empty");
		}
		
		this.image = createImageUsingBytes(bytes);
		
		return this;
		
	}
	
	private MagickImage createImageUsingBytes(byte[] bytes){
		
		try {
			ImageInfo info = new ImageInfo();
			MagickImage image = new MagickImage();
			image.allocateImage(info);
			image.blobToImage(info, bytes);
			return image;
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
	}

}
