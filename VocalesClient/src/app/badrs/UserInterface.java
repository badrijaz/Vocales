package app.badrs;

import javax.swing.*;
import java.awt.*;

public class UserInterface extends JFrame {

    public static JTextArea addressTextArea = new JTextArea();
    private JLabel connectionIndicator = new JLabel("Idle", SwingConstants.CENTER);
    private JButton connectButton = new JButton("Connect");

    public UserInterface() {
        super("Vocales");

        GridLayout gridLayout = new GridLayout(0, 1);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);

        getContentPane().setLayout(gridLayout);

        /* addressTextArea */
        addressTextArea.setRows(1);
        addressTextArea.setText("192.168.10.");
        /* Adding components to the layout */
        getContentPane().add(addressTextArea);
        getContentPane().add(connectionIndicator);
        getContentPane().add(connectButton);

        /* Start / restart connection on button click */
        connectButton.addActionListener(actionEvent -> {
            if (Main.isThreadRunning) {
                Main.isThreadRunning = false;
                Main.disconnectFromServer();
                connectButton.setText("Connect");
                connectionIndicator.setText("Idle");
            } else {
                Main.isThreadRunning = true;
                Main.connectToServer();
                connectButton.setText("Disconnect");
                connectionIndicator.setText("Connected");
            }
        });
    }

}
