package net.rickiekarp.core.util;

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

public class TextToImage {

    private int imageWidth = 500;
    private int imageHeight = 500;

    public void SaveToImage(String text) {
        {
            Image image = textToImage(text);
            save(image, "/home/rickie/test.png");
        }
    }

    private void save(Image image, String outFile) {
        File outputFile = new File(outFile);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Image textToImage (String text) {

        Font font = Font.loadFont(this.getClass().getClassLoader().getResource("fonts/masonic.ttf").toExternalForm(), 50);

        Label label = new Label(text);
        label.setFont(font);
        label.setMinSize(imageWidth, imageHeight);
        label.setMaxSize(imageWidth, imageHeight);
        label.setPrefSize(imageWidth, imageHeight);
        label.setStyle("-fx-background-color:#191919; -fx-text-fill:white;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new Group(label));
        WritableImage img = new WritableImage(imageWidth, imageHeight);
        scene.snapshot(img);
        return img;
    }

}