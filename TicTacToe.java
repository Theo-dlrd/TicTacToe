import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.BoxLayout;

public class TicTacToe {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    public TicTacToe(){
        //Paramètres de la fenêtre
        JFrame frame = new JFrame("TicTacToe");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()-WINDOW_WIDTH)/2);
        int y = (int) ((dimension.getHeight()-WINDOW_HEIGHT)/2);
        frame.setBackground(Color.WHITE);
        frame.setLocation(x, y);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        
        //Paramètres du panel
        this.menu(frame);

        //Affichage de la fenêtre
        frame.pack();
        frame.setVisible(true);
    }

    public void setButtonAppearence(JButton button){
        button.setBackground(Color.lightGray);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 30));
    }

    private void menu(JFrame frame){
        JPanel panelImg = new JPanel();
        GridBagConstraints constraintsImg = new GridBagConstraints();
        constraintsImg.gridx = 0; // Position en colonne 0
        constraintsImg.gridy = 0; // Position en ligne 0
        constraintsImg.gridwidth = 1; // Largeur de la cellule : 1
        constraintsImg.gridheight = 1; // Hauteur de la cellule : 1

        JPanel panelBoutons = new JPanel();
        panelBoutons.setLayout(new BoxLayout(panelBoutons, BoxLayout.Y_AXIS));
        GridBagConstraints constraintsBut = new GridBagConstraints();
        constraintsBut.gridx = 0; // Position en colonne 0
        constraintsBut.gridy = 1; // Position en ligne 0
        constraintsBut.gridwidth = 1; // Largeur de la cellule : 1
        constraintsBut.gridheight = 1; // Hauteur de la cellule : 1

        ImageIcon imageTictactoe = new ImageIcon("image1.png");
        JLabel jlImg = new JLabel(imageTictactoe);
        panelImg.add(jlImg);        

        JButton joinButton = new JButton("Rejoindre une partie");
        setButtonAppearence(joinButton);
        JButton quitButton = new JButton("Quitter");
        setButtonAppearence(quitButton);
        JButton hostButton = new JButton("Héberger une partie");
        setButtonAppearence(hostButton);
        JButton validButton = new JButton("Valider");
        setButtonAppearence(validButton);
        JTextField nomText = new JTextField("Saisir le numéro de la partie");



        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                System.exit(0);
            }
        });

        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                frame.removeAll();

                Thread server = new Thread(() -> new Server());
                server.start();

                try{
                    wait(1000);
                }
                catch(InterruptedException e){
                    System.out.println(e.toString());
                } 

                Thread client = new Thread(() -> new Client(frame, null));
                client.start();
            }
        });

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                frame.remove(hostButton);
                frame.remove(joinButton);
                frame.add(nomText);
                frame.add(validButton);
            }
        });

        validButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                frame.removeAll();

                if(nomText.getText().length()!=0){
                    Thread client = new Thread(() -> new Client(frame, nomText.getText()));
                    client.start();
                }
            }
        });


        panelBoutons.add(Box.createVerticalStrut(20));
        panelBoutons.add(hostButton);
        panelBoutons.add(Box.createVerticalStrut(20));
        panelBoutons.add(joinButton);
        panelBoutons.add(Box.createVerticalStrut(20));
        panelBoutons.add(quitButton);

        frame.add(panelImg, constraintsImg);
        frame.add(panelBoutons, constraintsBut);
    }
    
    public static void main(String[] args) {
        new TicTacToe();
    }
}