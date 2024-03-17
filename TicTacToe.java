
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.BoxLayout;


/**
 * Classe permettant d'implémenter le menu du TicTacToe.
 */
public class TicTacToe {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    /**
     * Méthode d'instanciation de la fenêtre du tictactoe avec le menu d'hébergement du serveur et le menu d'arrivée sur un serveur existant par le client.
     */
    public TicTacToe(){
        //Paramètres de la fenêtre
        JFrame frame = new JFrame("TicTacToe");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

    /**
     * Méthode permettant d'attribuer au bouton passé en paramètre des caractéristiques particulière.
     * @param button [JButton] Le bouton à personnaliser.
     */
    public void setButtonAppearence(JButton button){
        button.setBackground(Color.lightGray);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 30));
    }

    /**
     * Méthode d'implementation du menu ainsi que les comportements des différents boutons présents sur la fenêtre.
     * @param frame [JFrame] La frame(fenêtre) du jeu.
     */
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

        JTextField nomText = new JTextField();
        nomText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        nomText.setPreferredSize(new Dimension(200, 30));

        JLabel text = new JLabel("Saisir le numéro de la partie");
        text.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        text.setPreferredSize(new Dimension(200, 30));

        panelBoutons.add(Box.createVerticalStrut(20));  //Ajouter un petit espace vertical
        panelBoutons.add(hostButton);
        panelBoutons.add(Box.createVerticalStrut(20));
        panelBoutons.add(joinButton);
        panelBoutons.add(Box.createVerticalStrut(20));
        panelBoutons.add(quitButton);

        frame.add(panelImg, constraintsImg);
        frame.add(panelBoutons, constraintsBut);


        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                System.exit(0);
            }
        });

        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                panelBoutons.removeAll();
                panelBoutons.repaint();
                panelImg.removeAll();
                panelImg.repaint();

                Thread server = new Thread(() -> new Server());
                server.start();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Thread client = new Thread(() -> {
                    new Client(frame, null);
                });
                client.start();
            }
        });

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                panelBoutons.removeAll();
                panelBoutons.add(text);
                panelBoutons.add(nomText);
                panelBoutons.add(validButton);
                panelBoutons.add(Box.createVerticalStrut(20));
                panelBoutons.add(quitButton);
                panelBoutons.revalidate();
                panelBoutons.repaint();
            }
        });

        validButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                if(nomText.getText().length()!=0){
                    System.out.println(nomText.getText());
                    
                    panelBoutons.removeAll();
                    panelBoutons.add(Box.createVerticalStrut(20));
                    text.setText("Recherche de la partie. Veuillez patienter...");
                    text.setPreferredSize(new Dimension(300, 80));
                    panelBoutons.add(text);

                    panelBoutons.revalidate();
                    panelBoutons.repaint();

                    Thread client = new Thread(() -> new Client(frame, nomText.getText()));
                    client.start();
                }
            }
        });
    }


    /**
     * Méthode statique principale du jeu.
     * @param args Argument du main par défaut.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TicTacToe();
        }); 
    }
}