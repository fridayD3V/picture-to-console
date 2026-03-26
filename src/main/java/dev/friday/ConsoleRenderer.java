package dev.friday;

import java.awt.image.BufferedImage;

public class ConsoleRenderer {
    private final int consoleWidth;

    private static final int LOWER_BOUND = 10;
    private static final int UPPER_BOUND = 1000;
    private static final int DEFAULT_VALUE = 50;

    ConsoleRenderer() {
        this.consoleWidth = DEFAULT_VALUE;
    }

    ConsoleRenderer(int consoleWidth) {
        if (consoleWidth >= LOWER_BOUND && consoleWidth <= UPPER_BOUND)
            this.consoleWidth = consoleWidth;
        else this.consoleWidth = DEFAULT_VALUE;
    }

    private void validateImageBounds(BufferedImage bufferedImage) {
        if (bufferedImage == null)
            throw new IllegalArgumentException("Can't load image!");
        else if (this.consoleWidth > bufferedImage.getWidth()
                || this.consoleWidth > bufferedImage.getHeight()) {
            throw new IllegalArgumentException("Resolution is too big!");
        }
    }

    public String render(BufferedImage bufferedImage) {
        validateImageBounds(bufferedImage);

        StringBuilder sb = new StringBuilder();

        int widthOfOriginalImage = bufferedImage.getWidth();
        int heightOfOriginalImage = bufferedImage.getHeight();

        int widthOfConsolePixel = widthOfOriginalImage / this.consoleWidth;
        int heightOfConsolePixel = heightOfOriginalImage / (this.consoleWidth / 2);

        for (int consoleY = 0; consoleY < this.consoleWidth /2; consoleY++) {
            for (int consoleX = 0; consoleX < this.consoleWidth; consoleX++) {
                String pixelStr = getAverageBlockColor(consoleX, consoleY,
                        widthOfConsolePixel,
                        heightOfConsolePixel,
                        bufferedImage);
                sb.append(pixelStr);
            }
            sb.append("\n");
        }

        sb.append("\u001b[0m"); // Returns the default color to console

       return sb.toString();
    }

    private String getAverageBlockColor(int consoleX, int consoleY,
                                        int widthOfConsolePixel,
                                        int heightOfConsolePixel,
                                        BufferedImage bufferedImage) {

        long r = 0L, g = 0L, b = 0L;
        int pixelCount = widthOfConsolePixel * heightOfConsolePixel;

        for (int pixelY = 0; pixelY < heightOfConsolePixel; pixelY++) {
            for (int pixelX = 0; pixelX < widthOfConsolePixel; pixelX++) {
                int currentX = (consoleX * widthOfConsolePixel) + pixelX;
                int currentY = (consoleY * heightOfConsolePixel) + pixelY;

                int rgb = bufferedImage.getRGB(currentX, currentY);

                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += rgb & 0xFF;
            }
        }

        r /= pixelCount; g /= pixelCount; b /= pixelCount;
        return String.format("\u001b[38;2;%d;%d;%dm█", r, g, b);
    }
}