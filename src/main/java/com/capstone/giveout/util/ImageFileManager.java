package com.capstone.giveout.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageFileManager {

    private Path targetDir;

	public static ImageFileManager get(String path) throws IOException {
		return new ImageFileManager(path);
	}


	private ImageFileManager(String path) throws IOException{
        targetDir = Paths.get(path);
		if(!Files.exists(targetDir)){
			Files.createDirectories(targetDir);
		}
	}

	public Path getImagePath(long objectId, String size) throws IOException {
        String dirPathString = "id_" + objectId;
        Path dirPath = targetDir.resolve(dirPathString);
        if(!Files.exists(dirPath)){
            Files.createDirectories(dirPath);
        }

		return dirPath.resolve("image_" + size + ".jpg");
	}

	public void copyImage(long objectId, String size, OutputStream out) throws IOException {
		Path source = getImagePath(objectId, size);
		if(!Files.exists(source)){
			throw new FileNotFoundException("Unable to find the referenced image file for path '" + targetDir + "and id:" + objectId);
		}
		Files.copy(source, out);
	}

	public void saveImage(long objectId, String size, BufferedImage image) throws IOException{
		assert(image != null);
		
		Path target = getImagePath(objectId, size);
        ImageIO.write(image, "jpeg", target.toFile());
	}
	
}
