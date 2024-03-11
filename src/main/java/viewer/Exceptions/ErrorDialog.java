package viewer.Exceptions;

import javax.swing.*;

public class ErrorDialog {
    public static void show(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
