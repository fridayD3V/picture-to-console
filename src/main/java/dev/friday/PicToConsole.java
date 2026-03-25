package dev.friday;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.net.URI;

public class PicToConsole {
    private final int consoleWidth;
    private final BufferedImage bufferedImage;
    private final String imageString;

    private static final int LOWER_BOUND = 10;
    private static final int UPPER_BOUND = 1000;
    private static final int DEFAULT_VALUE = 50;

    PicToConsole(int consoleWidth, String url) throws IOException {
        this.consoleWidth = validateResolution(consoleWidth);
        this.bufferedImage = ImageIO.read(URI.create(url).toURL());
        validateImageBounds();
        this.imageString = this.renderImageToString();
    }

    PicToConsole(int consoleWidth, File path) throws IOException {
        if (!path.exists() || !path.isFile())
            throw new NoSuchFileException(path.toString());

        this.consoleWidth = validateResolution(consoleWidth);
        this.bufferedImage = ImageIO.read(path);
        validateImageBounds();
        this.imageString = this.renderImageToString();
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
        else if (consoleWidth > bufferedImage.getWidth()
                || consoleWidth > bufferedImage.getHeight()) {
            throw new IllegalArgumentException("Resolution is too big!");
        }
    }

    private String renderImageToString() {
        StringBuilder imageStringBuilder = new StringBuilder();

        int widthOfOriginalImage = this.bufferedImage.getWidth();
        int heightOfOriginalImage = this.bufferedImage.getHeight();

        int widthOfConsolePixel = widthOfOriginalImage / this.consoleWidth;
        // Because height of "█" == 2*width
        int heightOfConsolePixel = heightOfOriginalImage / (this.consoleWidth / 2);

        for (int consoleY = 0; consoleY < this.consoleWidth /2; consoleY++) {
            for (int consoleX = 0; consoleX < this.consoleWidth; consoleX++) {

                long[] avgColor = getAverageBlockColor(consoleX, consoleY, widthOfConsolePixel, heightOfConsolePixel);

                imageStringBuilder.append(String.format("\u001b[38;2;%d;%d;%dm", avgColor[0], avgColor[1], avgColor[2])).append("█");
            }
            imageStringBuilder.append("\n");
        }
        imageStringBuilder.append("\u001b[0m"); // Returns the default color to console

       return imageStringBuilder.toString();
    }

    private long[] getAverageBlockColor(int consoleX, int consoleY, int widthOfConsolePixel, int heightOfConsolePixel) {
        long r = 0L, g = 0L, b = 0L;
        int pixelCount = 0;

        for (int pixelY = 0; pixelY < heightOfConsolePixel; pixelY++) {
            for (int pixelX = 0; pixelX < widthOfConsolePixel; pixelX++) {
                int currentX = consoleX*widthOfConsolePixel+pixelX;
                int currentY = consoleY*heightOfConsolePixel+pixelY;

                int rgb = this.bufferedImage.getRGB(currentX, currentY);

                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += rgb & 0xFF;

                pixelCount++;
            }
        }

        r /= pixelCount; g /= pixelCount; b /= pixelCount;

        return new long[]{r, g, b};
    }

    public String getImageString() {
        return this.imageString;
    }
}