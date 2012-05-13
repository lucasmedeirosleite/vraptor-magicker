package br.com.caelum.vraptor.magicker;

import java.io.InputStream;

import magick.MagickImage;

public interface Magicker {

	Magicker takeImageStream(InputStream imageStream);

	MagickImage getImage();

}
