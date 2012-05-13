package br.com.caelum.vraptor.magicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Strings;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class DefaultMagicker implements Magicker {
	
	private MagickImage original;
	private String title;
	private String path;
	private List<ImageHolder> children;
	private final Environment environment;
	
	public DefaultMagicker(Environment environment) {
		this.environment = environment;
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

	@Override
	public MagickImage getImage() {
		return this.original;
	}

	@PostConstruct
	public void load(){
		System.setProperty("jmagick.systemclassloader","false");
		children = new ArrayList<ImageHolder>();
	}
	
	@Override
	public Magicker resizeTo(int width, int height) {
		try {
			this.original = this.original.scaleImage(width, height);
			return this;
		} catch (MagickException e) {
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
		
		this.original = createImageUsingBytes(bytes);
		
		return this;
		
	}
	
	@Override
	public Magicker takeImagePath(String path) {
		if(Strings.isNullOrEmpty(path)){
			throw new MagickerException("Path should not be null or empty");
		}
		
		try {
			this.original = new MagickImage(new ImageInfo(path));
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
		
		return this;
	}

	@Override
	public Magicker takeImageStream(InputStream imageStream) {
		if(imageStream == null){
			throw new MagickerException("Stream should not be null");
		}
		this.original = createImage(imageStream);
		return this;
	}

	@Override
	public Magicker takeImageUploaded(UploadedFile uploadedFile) {
		if(uploadedFile == null){
			throw new MagickerException("UploadedFile should not be null");
		}
		this.original = createImage(uploadedFile.getFile());
		return this;
	}

	@Override
	public Magicker withTitle(String title) {
		
		if(Strings.isNullOrEmpty(title)){
			throw new MagickerException("Title should not be null or empty");
		}
		
		this.title = title;
		return this;
	}

	@Override
	public void save() {
		
		if(Strings.isNullOrEmpty(path)){
			this.path = this.environment.get("magicker.images_path");
		}
		
		if(Strings.isNullOrEmpty(path)){
			throw new MagickerException("No path defined (check your environment key path (magicker.images_path) or tell the path using method withPath)");
		}
		
		try {
			ImageInfo info = new ImageInfo();
			original.setFileName( this.path + "/" + title);
			original.writeImage(info);
			
			for (ImageHolder holder : children) {
				
				if(ImageType.THUMBNAIL == holder.getType()){
					info = new ImageInfo();
					holder.getImage().setFileName(this.path + "/thumb/" + title);
					holder.getImage().writeImage(info);
				}
				
			}
			
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException("Image could not be saved");
		}
		
	}

	@Override
	public Magicker withPath(String path) {
		this.path = path;
		return this;
	}

	@Override
	public Magicker addThumb() {
		
		int width = Integer.valueOf(this.environment.get("magicker.images.thumb.width"));
		int height = Integer.valueOf(this.environment.get("magicker.images.thumb.height"));
		
		try {
			children.add(new ImageHolder(original.scaleImage(width, height), ImageType.THUMBNAIL));
			return this;
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
		
	}
	

}
