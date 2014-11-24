package com.capstone.potlatch.util;

import com.capstone.potlatch.models.Gift;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GiftImageFileManager {

	public static GiftImageFileManager get() throws IOException {
		return new GiftImageFileManager();
	}
	
	private Path targetDir = Paths.get("gifts");

	private GiftImageFileManager() throws IOException{
		if(!Files.exists(targetDir)){
			Files.createDirectories(targetDir);
		}
	}
	
	// Private helper method for resolving video file paths
	public Path getImagePath(Gift gift, String size) throws IOException {
		assert(gift != null);

        String dirPathString = "id_" + gift.getId();
        Path dirPath = targetDir.resolve(dirPathString);
        if(!Files.exists(dirPath)){
            Files.createDirectories(dirPath);
        }

		return dirPath.resolve("image_" + size + ".jpg");
	}

//	public void copyData(Gift gift, String size, OutputStream out) throws IOException {
//		Path source = getImagePath(gift, size);
//		if(!Files.exists(source)){
//			throw new FileNotFoundException("Unable to find the referenced image file for gift id:"+gift.getId());
//		}
//		Files.copy(source, out);
//	}

	public void saveImage(Gift gift, String size, InputStream giftImage) throws IOException{
		assert(giftImage != null);
		
		Path target = getImagePath(gift, size);
		Files.copy(giftImage, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
}
