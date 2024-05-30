package com.chess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class test {
    public static void main( String[] args){
        String url="art/holywarriors/Black.JPG";
        try {
            // Read the image file and create a BufferedImage
            BufferedImage bufferedImage = ImageIO.read(new File(url));

            // Check if the image was successfully loaded
            if (bufferedImage != null) {
                System.out.println("Image successfully loaded.");
                // You can now work with the BufferedImage object
            } else {
                System.out.println("Failed to load image.");
            }
        } catch (IOException e) {
            // Handle the exception if the file is not found or an error occurs during reading
            System.err.println("Error reading the image file: " + e.getMessage());
        }
    }
}
