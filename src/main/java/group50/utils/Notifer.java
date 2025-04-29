package group50.utils;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.AWTException;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Notifer {

    public static void main(String[] args) throws AWTException, IOException {
        if (SystemTray.isSupported()) {
            Notifer td = new Notifer();
        } else {
            System.err.println("System tray not supported!");
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("nux");
    }

    public void displayTray(String message) throws AWTException, IOException {
        if(isWindows()){
            Image image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/sideOn/plane.png")));

            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(image, "App Tooltip");

            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage("Runway Tool", message, TrayIcon.MessageType.INFO);

        }
        if(isMac()){
            String script = String.format("display notification \"%s\" with title \"%s\"",message, "Runway Tool");
            try {
                Runtime.getRuntime().exec(new String[]{"osascript", "-e", script});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Obtain only one instance of the SystemTray object

    }
}