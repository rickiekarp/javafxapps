package net.rickiekarp.core.util.image;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage ;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TextToImage {

    private int imageWidth = 512;
    private int imageHeight = 512;
    private String outFormat = "png";

    public void SaveToImage(String text, int fontSize, String outFile) {
        {
            Image image = textToImage(text, fontSize);
            saveToFile(image, outFile);
        }
    }

    private void saveToFile(Image image, String outPath) {
        File outputFile = new File(outPath);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, outFormat, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Image textToImage(String text, int fontSize) {
        Label label = new Label(text);
        label.setMinSize(imageWidth, imageHeight);
        label.setMaxSize(imageWidth, imageHeight);
        label.setPrefSize(imageWidth, imageHeight);
        label.setStyle("-fx-background-color:#191919; -fx-text-fill:white;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        setCustomFont(label, fontSize);

        Scene scene = new Scene(new Group(label));
        WritableImage img = new WritableImage(imageWidth, imageHeight);
        scene.snapshot(img);
        return img;
    }

    private void setCustomFont(Label label, int fontSize) {
        URL url = this.getClass().getClassLoader().getResource("fonts/masonic.ttf");
        if (url == null) {
            return;
        }

        Font font = Font.loadFont(url.toExternalForm(), fontSize);
        label.setFont(font);
    }
}