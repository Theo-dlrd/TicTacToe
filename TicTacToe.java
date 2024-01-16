import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.net.URL;

public class TicTacToe extends JFrame{
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 1000;

    public TicTacToe(){
        super("TicTacToe");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()-WINDOW_WIDTH)/2);
        int y = (int) ((dimension.getHeight()-WINDOW_HEIGHT)/2);
        this.setLocation(x, y);
        this.setResizable(false);
        this.setLayout(null);

        this.menu();

        this.pack();
        this.setVisible(true);
    }

    public void setAppearence(JButton button){
        button.setBackground(Color.cyan);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void menu(){
        JButton quitButton = new JButton("Quitter");
        quitButton.setBounds(WINDOW_WIDTH/2-100, WINDOW_HEIGHT-200, 200, 30);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        JButton joinButton = new JButton("Rejoindre une partie");
        joinButton.setBounds(WINDOW_WIDTH/2-100, WINDOW_HEIGHT-250, 200, 30);
        this.setAppearence(joinButton);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Demande de rejoindre une partie");
            }
        });

        JButton hostButton = new JButton("Héberger une partie");
        hostButton.setBounds(WINDOW_WIDTH/2-100, WINDOW_HEIGHT-300, 200, 30);
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Demande de créer une partie");
            }
        });

        this.add(quitButton);
        this.add(hostButton);
        this.add(joinButton);
    }

    
    public static void main(String[] args) {
        new TicTacToe();
    }
}