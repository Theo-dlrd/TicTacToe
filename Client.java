import java.awt.Dimension;
import java.rmi.*;
import java.util.Enumeration;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;


public class Client {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    private String nom;
    public int forme;
    private int[][] ecouteGrille;

    public Client(JFrame frame, String nom){
        try{
            this.nom = nom;

            GrilleInterface ri = (GrilleInterface) Naming.lookup("rmi:/"+this.getIPAdress()+":1099/Grille");
            frame.setLayout(new GridLayout(3,3));
            frame.setBounds(WINDOW_WIDTH/2-250, WINDOW_HEIGHT/2-250, 500, 500);

            if(ri.rejoindrePartie(this.nom)){
                this.forme = ri.getForme(this.nom);
                System.out.println("Début communication avec le serveur. Forme = "+this.forme+"!\n");
            }
            else{
                JOptionPane.showMessageDialog(frame, "Le nombre maximal de personnes pour cette partie est atteint !");
                System.exit(-1);
            }

            while (ri.getNbJoueurs()<2) {
                JOptionPane.showMessageDialog(frame, "En attente d'un autre joueur. Veuillez patienter...");
            }

            
            for (int i = 0; i < 9; i++) {
                JButton button = new JButton();
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Remplacer le bouton par une image
                        Container container = button.getParent();
                        int index = container.getComponentZOrder(button);
                        container.remove(button);

                        // Charger l'image
                        ImageIcon imageIcon;
                        if(forme == 1){
                            imageIcon= new ImageIcon("croix.png");
                        }
                        else{
                            imageIcon= new ImageIcon("rond.png");
                        }
                        JLabel imageLabel = new JLabel(imageIcon);

                        // Ajouter l'image à la même position que le bouton précédent
                        container.add(imageLabel, index);

                        // Rafraîchir l'interface graphique
                        container.revalidate();
                        container.repaint();

                        int x = index / 3;
                        int y = index - x*3;

                        try {
                            ri.placerForme(x, y, forme);
                            int[][] grille = ri.getGrille();
                            ecouteGrille = ri.getGrille();

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
                });
                frame.add(button);
                frame.repaint();
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }        
    }


    private String getIPAdress(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                // Filtrer les interfaces qui ne sont pas actives
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        System.out.println("Adresse IP sur le réseau : " + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
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

        new Client(frame, "Théo");

        frame.setVisible(true);
        //frame.pack();
    }
}


