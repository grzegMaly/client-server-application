package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import application.portfolio.clientmodule.Model.Model.Disc.DiscElement;
import application.portfolio.clientmodule.Model.Model.Disc.FileElement;
import application.portfolio.clientmodule.Model.Request.Disc.DiscRequestViewModel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DiscFilesHandler {

    private static final Path downloadDir = Paths.get(System.getProperty("user.home"), "Downloads");
    private static final long MAX_SIZE = 2L * (long) Math.pow(1024, 3);

    protected static void displayImage(FileElement fileElement, DiscRequestViewModel viewModel) {

        Image image = fileElement.getFullImage();
        if (image == null) {
            InputStream is = viewModel.downloadResource(fileElement.getPath());
            try (is) {
                byte[] data = is.readAllBytes();
                InputStream is2 = new ByteArrayInputStream(data);
                image = new Image(is2);
                fileElement.setSourceData(data);
            } catch (IOException e) {
                return;
            }
            fileElement.setFullImage(image);
        }

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(800);
        imageView.setFitWidth(800);

        StackPane root = new StackPane(imageView);
        root.getStyleClass().add("stackPaneImage");

        Stage imageStage = new Stage();
        imageStage.setScene(new Scene(root, 800, 800));
        imageStage.setTitle(fileElement.getFileName());
        imageStage.show();
    }

    protected static void displayTextFile(FileElement fileElement, DiscRequestViewModel viewModel) {

        String fullContent = fileElement.getFullContent();
        if (fullContent == null) {
            InputStream is = viewModel.downloadResource(fileElement.getPath());
            try (is) {
                byte[] data = is.readAllBytes();
                if (fileElement.getFileName().endsWith(".pdf")) {
                    try (PDDocument document = Loader.loadPDF(data)) {
                        PDFTextStripper pdfStripper = new PDFTextStripper();
                        fullContent = pdfStripper.getText(document);
                    } catch (IOException e) {
                        return;
                    }
                } else {
                    fullContent = new String(data);
                }
                fileElement.setSourceData(data);
                fileElement.setFullContent(fullContent);
            } catch (IOException e) {
                return;
            }
            fileElement.setFullContent(fullContent);
        }

        TextArea textArea = new TextArea(fullContent);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        StackPane stackPane = new StackPane(textArea);

        Stage textStage = new Stage();
        textStage.setScene(new Scene(stackPane, 800, 800));
        textStage.setTitle(fileElement.getFileName());
        textStage.show();
    }

    public static void download(Node node, DiscRequestViewModel viewModel) {

        InputStream is;
        String fileName;
        if (node instanceof FileElement element) {
            fileName = element.getFileName();
            if (element.getSourceData() != null) {
                is = new ByteArrayInputStream(element.getSourceData());
            } else {
                Path path = element.getPath();
                is = viewModel.downloadResource(path);
            }
        } else {
            DiscElement dElement = (DiscElement) node;
            fileName = dElement.getFileName() + ".zip";
            Path path = dElement.getPath();
            is = viewModel.downloadResource(path);
        }

        if (is == null) {
            return;
        }

        if (!Files.exists(downloadDir)) {
            try {
                Files.createDirectories(downloadDir);
            } catch (IOException e) {
                return;
            }
        }

        Path destinationPath = downloadDir.resolve(fileName);
        try (OutputStream os = Files.newOutputStream(destinationPath, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING); is) {
            is.transferTo(os);
        } catch (IOException e) {
            //Ignore
        }
    }

    public static boolean delete(Node node, DiscRequestViewModel viewModel) {
        if (node instanceof DiscElement element) {
            Path path = element.getPath();
            return viewModel.deleteResource(path);
        }
        return false;
    }

    public static void upload(File file, DiscController discController) {

        long size;
        Path path = Path.of(file.getPath());
        try {
            size = file.isFile() ? Files.size(path) : calcDirSize(path);
        } catch (IOException e) {
            return;
        }

        if (size > MAX_SIZE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File is to large");
            alert.showAndWait();
            return;
        }

        String fileName = file.getName();

        DiscRequestViewModel viewModel = discController.getViewModel();
        try (InputStream is = file.isFile() ? Files.newInputStream(path) : getZipStream(path)) {
            path = discController.getCurrentPath().resolve(fileName);
            DiscElement element = viewModel.uploadResource(path, file.isFile(), is);

            if (element != null) {
                discController.addResourceToView(element);
            } else {
                viewModel.deleteResource(path);
            }
        } catch (IOException e) {
            viewModel.deleteResource(path);
        }
    }

    private static long calcDirSize(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            return stream.filter(Files::isRegularFile)
                    .mapToLong(f -> {
                        try {
                            return Files.size(f);
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).sum();
        }
    }

    private static InputStream getZipStream(Path resourcePath) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);
             Stream<Path> stream = Files.walk(resourcePath)) {

            stream.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry entry = new ZipEntry(resourcePath.relativize(path).toString());
                        try {
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException e) {
            return null;
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
