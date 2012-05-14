package br.com.caelum.vraptor.magicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.Component;

import com.google.common.base.Strings;

@Component
public class DefaultMagicker implements Magicker {
	
	private List<ImageHolder> children;
	private final Environment environment;
	private MagickImage original;
	private String path;
	private String title;
	
	public DefaultMagicker(Environment environment) {
		this.environment = environment;
	}
	
	@PostConstruct
	public void load(){
		System.setProperty("jmagick.systemclassloader","false");
		children = new ArrayList<ImageHolder>();
	}

	@Override
	public Magicker addCustom(int width, int height) {
		try {
			children.add(new ImageHolder(original.scaleImage(width, height), ImageType.CUSTOM));
			return this;
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
	}
	
	@Override
	public Magicker addMedium() {
		
		int width = Integer.valueOf(this.environment.get("magicker.images.medium.width"));
		int height = Integer.valueOf(this.environment.get("magicker.images.medium.height"));
		
		try {
			children.add(new ImageHolder(original.scaleImage(width, height), ImageType.MEDIUM));
			return this;
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
		
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
	public List<MagickImage> getChildren() {
		List<MagickImage> images = new ArrayList<MagickImage>();
		for (ImageHolder holder : children) {
			images.add(holder.getImage());
		}
		return Collections.unmodifiableList(images);
	}
	
	@Override
	public MagickImage getImage() {
		return this.original;
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
				
				if(ImageType.MEDIUM == holder.getType()){
					info = new ImageInfo();
					holder.getImage().setFileName(this.path + "/medium/" + title);
					holder.getImage().writeImage(info);
				}
				
				if(ImageType.CUSTOM == holder.getType()){
					info = new ImageInfo();
					holder.getImage().setFileName(this.path + "/custom/" + title);
					holder.getImage().writeImage(info);
				}
				
			}
			
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException("Image could not be saved");
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
	public Magicker withPath(String path) {
		this.path = path;
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
	public String getFullPathOf(ImageType type) {
		
		try {
			
			if(ImageType.ORIGINAL == type){
				return this.original.getFileName();
			}
			
			for (ImageHolder holder : children) {
				
				if(holder.getType() == type){
					return holder.getImage().getFileName();
				}
				
			}
			
		} catch (MagickException e) {
			e.printStackTrace();
			throw new MagickerException(e.getMessage());
		}
		
		throw new MagickerException("Image not found");
		
	}
	
	

}
