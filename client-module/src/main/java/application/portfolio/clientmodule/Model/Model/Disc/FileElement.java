package application.portfolio.clientmodule.Model.Model.Disc;

import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.image.Image;


public class FileElement extends DiscElement {

    private static final String[] FILE_KEYS = {"extension", "hasThumbnail", "thumbnailString"};

    private boolean hasThumbnail;
    private String thumbnailString;
    private Image fullImage;
    private String fullContent;
    private byte[] sourceData;

    public boolean isHasThumbnail() {
        return hasThumbnail;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public String getThumbnailString() {
        return thumbnailString;
    }

    public void setThumbnailString(String thumbnailString) {
        this.thumbnailString = thumbnailString;
    }

    public Image getFullImage() {
        return fullImage;
    }

    public void setFullImage(Image fullImage) {
        this.fullImage = fullImage;
    }

    public String getFullContent() {
        return fullContent;
    }

    public void setFullContent(String fullContent) {
        this.fullContent = fullContent;
    }

    public byte[] getSourceData() {
        return sourceData;
    }

    public void setSourceData(byte[] sourceData) {
        this.sourceData = sourceData;
    }

    public static FileElement completeElement(JsonNode node) {
        if (DataParser.validateElements(node, FILE_KEYS)) {
            FileElement element = new FileElement();
            boolean thumbnail = node.get(FILE_KEYS[1]).asBoolean();
            element.setHasThumbnail(thumbnail);
            if (thumbnail) {
                String thumbnailString = node.get(FILE_KEYS[2]).asText();
                element.setThumbnailString(thumbnailString);
            }
            return element;
        }
        return null;
    }
}
