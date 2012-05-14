package br.com.caelum.vraptor.magicker;

import java.io.InputStream;
import java.util.List;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

import magick.MagickImage;

public interface Magicker {

	Magicker addCustom(int width, int height);

	Magicker addMedium();

	Magicker addThumb();

	List<MagickImage> getChildren();

	MagickImage getImage();

	Magicker resizeTo(int width, int height);

	void save();

	Magicker takeImageBytes(byte[] bytes);

	Magicker takeImagePath(String path);

	Magicker takeImageStream(InputStream imageStream);
	
	Magicker takeImageUploaded(UploadedFile uploadedFile);

	Magicker withPath(String path);

	Magicker withTitle(String title);
	
	String getFullPathOf(ImageType type);

}
