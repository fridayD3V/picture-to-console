package dev.friday;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;

public class ImageLoader {
    public BufferedImage loadFromFile(File file) throws IOException {
        if (file.exists() && file.isFile()) return ImageIO.read(file);
        else throw new NoSuchFileException(file.toString());
    }

    public BufferedImage loadFromUrl(String url) throws IOException {
        return ImageIO.read(URI.create(url).toURL());
    }
}
