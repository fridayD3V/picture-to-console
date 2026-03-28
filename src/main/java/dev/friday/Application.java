package dev.friday;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class Application {
    static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Picture-To-Console ===");
        System.out.println("1 - Download picture (URL)");
        System.out.println("2 - Choose file from system");
        System.out.print("Your choice: ");

        while (true) {
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                processImage(new UrlImageChooser());
                return;
            } else if (choice.equals("2")) {
                processImage(new FileImageChooser());
                return;
            }
            System.out.print("Wrong input!\nYour choice: ");
        }
    }

    private static void processImage(ImageChooser imageChooser) {
        BufferedImage bufferedImage = imageChooser.getImage();

        if (bufferedImage == null) {
            System.out.println("Failed to load the image.");
            return;
        }

        try {
            System.out.print("Image console width: ");
            int width = Integer.parseInt(scanner.nextLine());

            ConsoleRenderer consoleRenderer = new ConsoleRenderer(width);
            String image = consoleRenderer.render(bufferedImage);
            System.out.println(image);
        } catch (NumberFormatException e) {
            System.out.println("Entered value is not a number!");
        }
    }
}

interface ImageChooser {
    BufferedImage getImage();
}

class FileImageChooser implements ImageChooser {
    @Override
    public BufferedImage getImage() {
        // Set native OS look for the dialog
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        // Invisible frame to bring dialog to the front
        JFrame parentFrame = new JFrame();
        parentFrame.setAlwaysOnTop(true);
        parentFrame.setLocationRelativeTo(null);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose image");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files (JPG, PNG)", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showOpenDialog(parentFrame);

        // Dispose the hidden frame after selection
        parentFrame.dispose();

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try {
                return ImageIO.read(selectedFile);
            } catch (IOException e) {
                System.out.println("File read error: " + e.getMessage());
            }
        } else {
            System.out.println("Selection cancelled.");
        }

        return null;
    }
}

class UrlImageChooser implements ImageChooser {
    @Override
    public BufferedImage getImage() {
        System.out.print("Enter image URL: ");
        String url = Application.scanner.nextLine();

        try {
            return ImageIO.read(URI.create(url).toURL());
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("URL read error: " + e.getMessage());
            return null;
        }
    }
}