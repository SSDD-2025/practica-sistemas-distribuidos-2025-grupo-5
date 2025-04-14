package es.codeurjc.practica1.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageUtils {

    public Blob remoteImageToBlob(String imageUrl){
        try {
            Resource image = new UrlResource(imageUrl);
		    return BlobProxy.generateProxy(image.getInputStream(), image.contentLength());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	}

	public Blob localImageToBlob(String localFilePath){

		File imageFile = new File(localFilePath);
		
		if (imageFile.exists()) {

			try {
				System.out.println("LA IMAGEN EXISTE");
				return BlobProxy.generateProxy(imageFile.toURI().toURL().openStream(), imageFile.length());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR at processing the IMAGE");
			}
		}
		System.out.println("LA IMAGEN NO EXISTE");

		return null;
	}

    public Blob multiPartFileImageToBlob(MultipartFile image){
		if (image!=null && !image.isEmpty()) {
			try {
				return BlobProxy.generateProxy(image.getInputStream(), image.getSize());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
			}
		}
		System.out.println("path image: images/" + image);
        return null;
	}

    public Blob createBlob(InputStream inputStream) throws SQLException {
        try {
            byte[] imageBytes = inputStream.readAllBytes();
            return new SerialBlob(imageBytes);
        } catch (Exception e) {
            throw new SQLException("Error al convertir la imagen a Blob", e);
        }
    }
    
}
