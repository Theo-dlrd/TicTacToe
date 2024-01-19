import java.awt.Dimension;
import java.rmi.*;
import java.util.ArrayList;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;


public class Client {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    private String nom;
    public int forme;

    public Client(JFrame frame, String nom){
        this.nom = nom;
        try{
            GrilleInterface ri = (GrilleInterface) Naming.lookup("rmi://192.168.1.37:1099/Grille");
            if(ri.rejoindrePartie(nom)){
                this.forme = ri.getForme(nom);
                System.out.println("Début communication avec le serveur. Forme = "+this.forme+"!\n");
            }
            else{
                System.exit(-1);
            }
            ri.clear();


            frame.setLayout(new GridLayout(3,3));
            frame.setBounds(WINDOW_WIDTH/2-250, WINDOW_HEIGHT/2-250, 500, 500);
            for (int i = 0; i < 9; i++) {
                JButton button = new JButton();
                button.addActionListener(new ButtonClickListener(button, ri));
                frame.add(button);
            }
        }
        catch(Exception e){
            System.out.println("Erreur : Connexion avec objet Grille non établie !");
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    private class ButtonClickListener implements ActionListener {
        private JButton button;
        private GrilleInterface ri;

        public ButtonClickListener(JButton button, GrilleInterface ri) {
            this.button = button;
            this.ri = ri;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Remplacer le bouton par une image
            Container container = button.getParent();
            int index = container.getComponentZOrder(button);
            container.remove(button);

            // Charger l'image
            ImageIcon imageIcon = new ImageIcon("croix.png");
            JLabel imageLabel = new JLabel(imageIcon);

            // Ajouter l'image à la même position que le bouton précédent
            container.add(imageLabel, index);

            // Rafraîchir l'interface graphique
            container.revalidate();
            container.repaint();

            int x = index / 3;
            int y = index % 3;

            try {
                ri.placerForme(x, y, forme);
                /*
                int[][] grille = ri.getGrille();
                for (int i = 0; i < grille.length; i++) {
                    for (int j = 0; j < grille[i].length; j++) {
                        System.out.print(" "+grille[i][j]+" ");
                    }
                    System.out.print("\n");
                } 
                */
            } 
            catch (Exception ex) {
                System.out.println("Erreur : Connexion avec le serveur de la grille interrompu !");
                System.out.println(ex.toString());
                try {
                    int[][] grille = ri.getGrille();
                    for (int i = 0; i < grille.length; i++) {
                        for (int j = 0; j < grille[i].length; j++) {
                            System.out.print(" "+grille[i][j]+" ");
                        }
                        System.out.print("\n");
                    } 
                } catch (Exception exx) {
                    System.out.println("Erreur : Connexion avec le serveur de la grille interrompu !");
                    System.out.println(ex.toString());
                }
            }
            
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("TicTacToe");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()-WINDOW_WIDTH)/2);
        int y = (int) ((dimension.getHeight()-WINDOW_HEIGHT)/2);
        frame.setBackground(Color.WHITE);
        frame.setLocation(x, y);
        frame.setResizable(false);
        frame.setVisible(true);

        Client cl1 = new Client(frame, "Théo");


        frame.pack();
    }
}


