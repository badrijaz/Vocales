package app.badrs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface extends JFrame {

    private JButton connectButton = new JButton("Connect");
    private JLabel connectionIndicator = new JLabel("Idle");

    public UserInterface(Thread serverThread) {
        super("Vocales");

        GridLayout gridLayout = new GridLayout(0, 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);

        getContentPane().setLayout(gridLayout);

        /* Adding components to the layout */
        getContentPane().add(connectionIndicator);
        getContentPane().add(connectButton);

        /* Start / restart connection on button clcik */
        connectButton.addActionListener(actionEvent -> {
            if (serverThread.isAlive()) {
                Main.disconnectFromServer();
                connectionIndicator.setText("Idle");
            } else {
                Main.connectToServer();
                connectionIndicator.setText("Connected");
            }
        });
    }

}
