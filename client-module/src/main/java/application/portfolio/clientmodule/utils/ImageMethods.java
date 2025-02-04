package application.portfolio.clientmodule.utils;

import application.portfolio.clientmodule.Model.Model.Disc.DiscElement;
import application.portfolio.clientmodule.Model.Model.Disc.FileElement;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ImageMethods {

    private static final String[] FILES = {"fileIcon.png", "dirIcon.png"};
    private static final String ICONS = "/View/Icons/";

    private static final ImageView FILE_ICON = loadIcon(FILES[0]);
    private static final ImageView DIR_ICON = loadIcon(FILES[1]);

    private static ImageView loadIcon(String fileName) {
        try (InputStream is = ImageMethods.class.getResourceAsStream(ICONS.concat(fileName))) {
            if (is != null) {
                Image image = new Image(is);
                return new ImageView(image);
            }
        } catch (IOException e) {
            System.out.println("Dupa, coś poszło nie tak");
        }
        return new ImageView();
    }

    public static ImageView getFileIcon() {
        return new ImageView(FILE_ICON.getImage());
    }

    public static ImageView getDirIcon() {
        return new ImageView(DIR_ICON.getImage());
    }


    public static ImageView getGraphic(DiscElement element) {

        ImageView icon;
        if (element instanceof FileElement e) {
            if (e.isHasThumbnail()) {
                icon = decodeFromBase64(e.getThumbnailString());
            } else {
                icon = getFileIcon();
            }
        } else {
            icon = getDirIcon();
        }

        if (icon == null) {
            icon = getFileIcon();
        }

        icon.setFitWidth(64);
        icon.setFitHeight(64);
        icon.setPreserveRatio(true);
        icon.setMouseTransparent(true);
        return icon;
    }

    public static ImageView decodeFromBase64(String thumbnailString) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(thumbnailString);
            Image image = new Image(new ByteArrayInputStream(decodedBytes));
            return new ImageView(image);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
