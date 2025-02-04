package application.portfolio.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ImageMethods {

    private static final String THUMBNAIL_EXT = "jpg";

    public static String encodeToBase64(Path path) {

        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data);
    }

    public static boolean createImage(Path outputPath, InputStream inputStream, boolean isThumbnail) {

        try {
            Files.createDirectories(outputPath.getParent());
        } catch (IOException e) {
            return false;
        }

        BufferedImage image, imageToSave;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            return false;
        }

        if (image.getColorModel().hasAlpha()) {
            BufferedImage newImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = newImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = newImage;
        }

        if (isThumbnail) {
            imageToSave = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imageToSave.createGraphics();
            Image scaledImage = image.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            g.drawImage(scaledImage, 0, 0, 64, 64, null);
            g.dispose();
        } else {
            imageToSave = image;
        }

        try {
            ImageIO.write(imageToSave, THUMBNAIL_EXT, outputPath.toFile());
            return Files.exists(outputPath);
        } catch (IOException e) {
            return false;
        }
    }
}
