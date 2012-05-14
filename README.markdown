## vraptor-magicker

	A vraptor plugin to work with the native library ImageMagick (using jmagick)

# installation

 	Clone the project and run mvn install.
	In your pom.xml:

		<dependency>
			<groupId>br.com.caelum.vraptor</groupId>
			<artifactId>vraptor-magicker</artifactId>
			<version>1.0.0</version>
			<scope>compile</scope>
		</dependency>
		
	- Because jmagick call the native ImageMagick  library on your machine, you'll need to install it
	(on mac you can install it running the homebrew command: brew install imagemagick)
	
	-After that download the latest jmagick source from svn running: svn co https://jmagick.svn.sourceforge.net/svnroot/jmagick jmagick
	-Then run the following commands:
		./configure --with-java-home=/System/Library/Frameworks/JavaVM.framework/Versions/Current --with-magick-home=/usr/local/jmagick
		make all & make install
		ln -s /usr/local/lib/libJMagick-6.5.7.so /Library/Java/Extensions/libJMagick.jnilib
		
	 -After that you should provide your jar to maven, in my case I used:
		
		<dependency>
			<groupId>jmagick</groupId>
			<artifactId>jmagick</artifactId>
			<version>6.5.7</version>
			<scope>provided</scope>
		</dependency>

		
# configuration

	vraptor-magicker uses vraptor-environment to provide some configuration:
	
	magicker.images_path -> path where you want to save the images
	magicker.images.thumb.width ->  thumbnail width
	magicker.images.thumb.height -> thumbnail height
	magicker.images.medium.width -> medium width
	magicker.images.medium.height -> medium height
	
	
# getting image


	- Supose you'll need to convert your image object to MagickImage object, to do that, you have four options:
	
		1.1 Using InputStream:
		
			InputStream stream = this.getClass().getResourceAsStream("caelum.png");
			MagickImage image = this.magicker.takeImageStream(stream).getImage();
			
		1.2 Using a byte array:
		
			byte[] bytes = IOUtils.toByteArray(this.getClass().getResourceAsStream("caelum.png"));
			MagickImage image = this.magicker.takeImageBytes(bytes).getImage();
			
		1.3 Using path:
		
			String path = this.getClass().getResource("caelum.png").getPath();
			MagickImage image = this.magicker.takeImagePath(path).getImage();
			
		1.4 Using vraptor UploadedFile
		
			UploadedFile file = aFile;
			this.magicker.takeImageUploaded(file).getImage();

	 - Resize an image:
	
			MagickImage image = this.magicker.takeImageStream(this.getClass().getResourceAsStream("caelum.png")).resizeTo(width, height).getImage();
			
	 - Save original image and a thumbnail image:
	
			this.magicker.takeImageStream(stream).withTitle(title).addThumb().save();
			
	 - Save original image and a medium sized image:
	
			this.magicker.takeImageStream(stream).withTitle(title).addMedium().save();
			
	 - Save original and a custom sized image:
	
			this.magicker.takeImageStream(stream).withTitle(title).addCustom(500, 600).save();
			
	 - Or you can save all:
			
			this.magicker.takeImageStream(stream).withTitle(title).addThumb().addMedium().addCustom(500, 600).save();
			
	 - If you want to define other path
			
			this.magicker.takeImageStream(stream).withPath(path).withTitle(title).addThumb().addMedium().addCustom(500, 600).save();
			
	 - Receiving the Magicker object:
	
				@Resource
			    public class MyController {

			        private Magicker magicker;

			        public MeuController(Magicker magicker) {
			            this.magicker = magicker;
			        }

			        public void saveImage(UploadedFile file) {
			            this.magicker.takeImageUploaded(file).addThumb().addMedium().addCustom(500, 600).save();
			        }

			    }
			
	IMPORTANT:
	
			Thumbnails will be saved in a sub-directory called thumb
			Medium images will be saved in a sub-directory called medium
			Custom sized images will be saved in a sub-directory called custom
			And you need to create them all, the library will not create them for you.
			
		
		