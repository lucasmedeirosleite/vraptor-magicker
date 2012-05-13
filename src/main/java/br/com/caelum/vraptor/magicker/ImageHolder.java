package br.com.caelum.vraptor.magicker;

import magick.MagickImage;

class ImageHolder {
	
	MagickImage image;
	ImageType type;
	
	public ImageHolder(MagickImage image, ImageType type){
		this.image = image;
		this.type = type;
	}

	public MagickImage getImage() {
		return image;
	}

	public ImageType getType() {
		return type;
	}
	

}
