package dev.friday;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;

public class PicToConsole {
    private final int consoleResolution;
    private final BufferedImage bufferedImage;
    private String imageString;

    private static final int LOWER_BOUND = 10;
    private static final int UPPER_BOUND = 1000;
    private static final int DEFAULT_VALUE = 50;

    PicToConsole(int consoleResolution, String url) throws IOException {
        this.consoleResolution = validateResolution(consoleResolution);
        this.bufferedImage = ImageIO.read(URI.create(url).toURL());
        validateImageBounds();
        this.convertImage();
    }

    PicToConsole(int consoleResolution, File path) throws IOException {
        if (!path.exists() || !path.isFile())
            throw new NoSuchFileException(path.toString());

        this.consoleResolution = validateResolution(consoleResolution);
        this.bufferedImage = ImageIO.read(path);
        validateImageBounds();
        this.convertImage();
    }

    private int validateResolution(int resolution) {
        if (resolution >= LOWER_BOUND && resolution <= UPPER_BOUND) {
            return resolution;
        }
        return DEFAULT_VALUE;
    }

    private void validateImageBounds() {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Can't load image!");
        }
        else if (consoleResolution > bufferedImage.getWidth()
                || consoleResolution > bufferedImage.getHeight()) {
            throw new IllegalArgumentException("Resolution is too big!");
        }
    }

    private void convertImage() {
        int widthOfOriginalImage = bufferedImage.getWidth();
        int heightOfOriginalImage = bufferedImage.getHeight();

        int widthOfConsolePixel = widthOfOriginalImage / consoleResolution;
        int heightOfConsolePixel = heightOfOriginalImage / (consoleResolution / 2);  // Because height of "█" == 2*width

        StringBuilder imageStringBuilder = new StringBuilder();

        long r, g, b;
        int pixelCount;

        for (int consoleY = 0; consoleY < consoleResolution /2; consoleY++) {
            for (int consoleX = 0; consoleX < consoleResolution; consoleX++) {
                r = 0; g = 0; b = 0;
                pixelCount = 0;

                for (int pixelY = 0; pixelY < heightOfConsolePixel; pixelY++) {
                    for (int pixelX = 0; pixelX < widthOfConsolePixel; pixelX++) {
                        Color tempColor = new Color(bufferedImage.getRGB(consoleX*widthOfConsolePixel+pixelX, consoleY*heightOfConsolePixel+pixelY));

                        r += tempColor.getRed(); g += tempColor.getGreen(); b += tempColor.getBlue();

                        pixelCount++;
                    }
                }

                r /= pixelCount; g /= pixelCount; b /= pixelCount;

                imageStringBuilder.append(String.format("\u001b[38;2;%d;%d;%dm", r, g, b)).append("█");
            }
            imageStringBuilder.append("\n");
        }
        imageStringBuilder.append("\u001b[0m"); // Returns the default color to console

        this.imageString = imageStringBuilder.toString();
    }

    public String getImageString() {
        return this.imageString;
    }
}
