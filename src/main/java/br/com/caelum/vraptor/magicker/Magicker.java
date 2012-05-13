package br.com.caelum.vraptor.magicker;

import java.io.InputStream;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

import magick.MagickImage;

public interface Magicker {

	MagickImage getImage();

	Magicker resizeTo(int width, int height);

	Magicker takeImageBytes(byte[] bytes);

	Magicker takeImagePath(String path);

	Magicker takeImageStream(InputStream imageStream);

	Magicker takeImageUploaded(UploadedFile uploadedFile);

	Magicker withTitle(String title);

	void save();

	Magicker withPath(String path);

}
