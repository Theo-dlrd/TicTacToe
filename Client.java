import java.awt.Dimension;
import java.rmi.*;
import java.util.Enumeration;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.Serializable;
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
import java.awt.Component;


public class Client {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    public int forme;
    public int id_joueur;
    private int[][] ecouteGrille;
    private boolean monTour;
    

    public Client(JFrame frame, String ip){
        try{
            GrilleInterface ri;
            if(ip==null){
                ri = (GrilleInterface) Naming.lookup("rmi:/"+this.getIPAdress()+":1099/Grille");
            }
            else{
                ri = (GrilleInterface) Naming.lookup("rmi:/"+ip+":1099/Grille");
            }
            frame.setLayout(new GridLayout(3,3));
            frame.setBounds(WINDOW_WIDTH/2-250, WINDOW_HEIGHT/2-250, 500, 500);

            Random random = new Random();
            int retourJoin;
            do{
                this.id_joueur = random.nextInt(100)+1;
            }while((retourJoin = ri.rejoindrePartie(this.id_joueur))==-1);

            if(retourJoin==0){
                this.forme = ri.getForme(this.id_joueur);
                System.out.println("Début communication avec le serveur. Forme = "+this.forme+"!\n");
                if(this.forme==1){
                    this.monTour=true;
                }
                else{
                    this.monTour=false;
                }
            }
            else if(retourJoin==-1){
                JOptionPane.showMessageDialog(frame, "Un autre utilisateur utilise ce pseudo !");
                System.exit(retourJoin);
            }
            else{
                JOptionPane.showMessageDialog(frame, "Le nombre maximal de personnes pour cette partie est atteint !");
                System.exit(retourJoin);
            }

            while (ri.getNbJoueurs()<2) {
                ri.sendStatus(this.id_joueur, GrilleInterface.Status.WAITING);
                JOptionPane.showMessageDialog(frame, "Code partie : "+ this.getIPAdress()+"\nEn attente d'un autre joueur. Veuillez patienter...");
            }

            ri.sendStatus(this.id_joueur, GrilleInterface.Status.READY);

            frame.setVisible(true);
            frame.repaint();

            while(!ri.allStatusReady());
           
            /*
            for (int i = 0; i < 9; i++) {
                JButton button = new JButton();
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                            // Remplacer le bouton par une image
                            Container container = button.getParent();
                            int index = container.getComponentZOrder(button);
                            System.out.println(index);
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
                                ri.passerTour();
                            } 
                            catch (Exception ex) {
                                System.out.println(ex.toString());
                            }
                        
                    }
                });
                frame.add(button);
            }
           */


            boolean play = true;
            this.monTour = false;
            try{
                ri.passerTour();
                while(play){
                    ecouteGrille = ri.getGrille();
                    // Vérifier le tour du joueur
                    if (ri.getTour() == this.id_joueur) {

                        frame.removeAll();
                        for (int i = 0; i < ecouteGrille.length; i++) {
                            for (int j = 0; j < ecouteGrille[i].length; j++) {
                                if(ecouteGrille[i][j]==1){
                                    ImageIcon imgCroix = new ImageIcon("croix.png");
                                    JLabel imgLabel = new JLabel(imgCroix);
                                    frame.add(imgLabel);
                                }
                                else if(ecouteGrille[i][j]==-1){
                                    ImageIcon imgCroix = new ImageIcon("rond.png");
                                    JLabel imgLabel = new JLabel(imgCroix);
                                    frame.add(imgLabel);
                                }
                                else{
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
                                            } 
                                            catch (Exception ex) {
                                                System.out.println(ex.toString());
                                            } 
                                        }
                                    });
                                }
                            }
                        }
                        frame.setVisible(true);

                        for (Component component : frame.getContentPane().getComponents()){
                            if(component instanceof JButton){
                                component.setEnabled(true);
                            }
                        }
                        
                        JOptionPane.showMessageDialog(frame,"A vous de jouer...");

                        while(sameGrille(ecouteGrille, ri.getGrille()));
                        ri.passerTour();
                        
                            
                    } 
                    else {
                        for (Component component : frame.getContentPane().getComponents()){
                            if(component instanceof JButton){
                                component.setEnabled(false);
                            }
                        }
                        
                        JOptionPane.showMessageDialog(frame,"Au tour de votre adversaire. Veuillez patienter...");
                       
                        while (sameGrille(ecouteGrille, ri.getGrille()));
                        ri.passerTour();
                    }
                }
            } 
            catch (Exception ex) {
                System.out.println(ex.toString());
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
                        System.out.println("Adresse IP Client : " + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean sameGrille(int[][] grille1, int[][] grille2){
        for (int i = 0; i < grille1.length; i++) {
            for (int j = 0; j < grille1[i].length; j++) {
                if(grille1[i][j]!=grille2[i][j]){
                    return false;
                }
            }
        }
        return true;
    }


    /*
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

        new Client(frame);

        frame.setVisible(true);
        frame.pack();
    }
    */
}


