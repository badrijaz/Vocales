package app.badrs;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class UserInterface extends JFrame {

    public static JTextArea addressTextArea = new JTextArea();
    private JLabel connectionIndicator = new JLabel("Idle");
    private JButton connectButton = new JButton("Connect");

    public UserInterface() {
        super("Vocales");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);

        getContentPane().setLayout(new BorderLayout());

        /* addressTextArea */
        addressTextArea.setRows(1);
        addressTextArea.setText("192.168.10.5");

        /* Adding components to the layout */
        getContentPane().add(addressTextArea, BorderLayout.NORTH);

        /* Button */
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(connectButton);
        connectButton.setPreferredSize(new Dimension(getWidth(), 30));
        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        /* Connection Indicator */
        JPanel statusPanel = new JPanel();
        connectionIndicator.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(connectionIndicator);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 30));
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        /* Start / restart connection on button click */
        connectButton.addActionListener(actionEvent -> {
            if (Main.isThreadRunning) {
                Main.isThreadRunning = false;
                Main.disconnectFromServer();
            } else {
                Main.isThreadRunning = true;
                Main.connectToServer();
            }

            java.awt.EventQueue.invokeLater(() -> {
                connectionIndicator.setText(Main.isThreadRunning ? "Connected" : "Idle");
                connectButton.setText(Main.isThreadRunning ? "Disconnect" : "Connect");
            });
        });

        setVisible(true);
    }

}
