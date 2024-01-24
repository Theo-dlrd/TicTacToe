import java.rmi.*;
import java.util.Enumeration;
import java.util.Random;
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
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Container;


public class Client {
    public int forme;
    public int id_joueur;
    private int[][] ecouteGrille;
    

    public Client(JFrame frame, String ip){
        for(Component component: frame.getContentPane().getComponents()){
            frame.remove(component);
        }
        frame.revalidate();
        frame.repaint();

        frame.setLayout(new GridLayout(3, 3));
        
        try{
            GrilleInterface ri;
            if(ip==null){
                ri = (GrilleInterface) Naming.lookup("rmi:/"+this.getIPAdress()+":1099/Grille");
            }
            else{
                ri = (GrilleInterface) Naming.lookup("rmi:/"+ip+":1099/Grille");
            }


            Random random = new Random();
            int retourJoin;
            do{
                this.id_joueur = random.nextInt(100)+1;
                System.out.println("mon id joueur : "+this.id_joueur);
            }while((retourJoin = ri.rejoindrePartie(this.id_joueur))==-1);

            if(retourJoin==0){
                this.forme = ri.getForme(this.id_joueur);
                System.out.println("Début communication avec le serveur. Forme = "+this.forme+"!\n");
            }
            else if(retourJoin==-1){
                JOptionPane.showMessageDialog(frame, "Un autre utilisateur utilise ce pseudo !");
                System.exit(retourJoin);
            }
            else{
                JOptionPane.showMessageDialog(frame, "Le nombre maximal de personnes pour cette partie est atteint !");
                System.exit(retourJoin);
            }

            if(ip==null){
                ri.sendStatus(this.id_joueur, GrilleInterface.Status.WAITING);
            }
            else{
                ri.sendStatus(this.id_joueur, GrilleInterface.Status.READY);
            }

            if(ri.getStatus(this.id_joueur)==GrilleInterface.Status.WAITING){
                JOptionPane.showMessageDialog(frame, "Code partie : "+ this.getIPAdress()+"\nEn attente d'un autre joueur. Veuillez patienter...");
                ri.sendStatus(this.id_joueur, GrilleInterface.Status.READY);
            }

            while (ri.getNbJoueurs()<2);

            frame.setVisible(true);

            while(!ri.allStatusReady());

            boolean play = true;
            try{
                //Sélection aléatoire du joueur qui commence
                for(int i=0; i<random.nextInt(10)+1; i++){
                    ri.passerTour();
                }

                Thread.sleep(1000);

                if(ri.getTour() == this.id_joueur){
                    JOptionPane.showMessageDialog(frame,"A vous de commencer...");
                }
                else{
                    JOptionPane.showMessageDialog(frame,"Votre adversaire commence...");
                }
                
                while(play){
                    ecouteGrille = ri.getGrille();

                    // Vérifier le tour du joueur
                    for (Component composant : frame.getContentPane().getComponents()) {
                        frame.remove(composant);
                    }

                    if (ri.getTour() == this.id_joueur){
                        /*
                        for (int i = 0; i < ecouteGrille.length; i++) {
                            for (int j = 0; j < ecouteGrille[i].length; j++) {
                                if(ecouteGrille[i][j]==1){
                                    ImageIcon imgRond = new ImageIcon("rond.png");
                                    JLabel rondLb = new JLabel(imgRond);
                                    frame.add(rondLb);
                                }
                                else if(ecouteGrille[i][j]==-1){
                                    ImageIcon imgCroix = new ImageIcon("croix.png");
                                    JLabel croixLb = new JLabel(imgCroix);
                                    frame.add(croixLb);
                                }
                                else{
                                    JButton button = new JButton();
                                    button.setVisible(true);
                                    button.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent ae){
                                            Container container = button.getParent();
                                            System.out.println(container);
                                            int index = container.getComponentZOrder(button);
                                            System.out.println("index = "+index);
                                            
                                            // Vérifier si l'emplacement est vide dans la grille
                                            int x = index / 3;
                                            int y = index % 3;

                                            System.out.println("x : "+x+"; y : "+y);
                                            if(ecouteGrille[x][y] == 0){
                                                // Placer la forme sur la grille
                                                try {
                                                    ri.placerForme(x, y, forme);
                                                } catch (Exception e) {
                                                    System.out.println(e);
                                                }
                    
                                                // Mettre à jour l'interface graphique
                                                frame.remove(button);
                    
                                                ImageIcon img;
                                                if(forme==1){
                                                    img = new ImageIcon("croix.png");
                                                } 
                                                else {
                                                    img = new ImageIcon("rond.png");
                                                }
                                                JLabel imgLb = new JLabel(img);
                                                frame.getContentPane().add(imgLb);
                                            } 
                                            else {
                                                // Informer que l'emplacement est déjà occupé
                                                JOptionPane.showMessageDialog(frame, "Emplacement déjà rempli. Choisissez-en un autre !");
                                            }
                                        }
                                    });
                                    frame.add(button);
                                }
                            }
                        }
                        frame.revalidate();
                        frame.repaint();
                        */

                        majEcran(frame, ecouteGrille, ri);

                        while(sameGrille(ecouteGrille, ri.getGrille()));

                        ri.passerTour();
                    }
                    else {
                        // Attendre que ce soit le tour du joueur actuel
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } 
            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        catch(ConnectException ce){
            JOptionPane.showMessageDialog(frame, "Aucune partie trouvée !\nAu revoir !");
            System.out.println(ce.toString());
            System.exit(1);

        }
        catch(Exception e){
            System.out.println(e.toString());
        }        
    }

    private boolean grilleVide(int[][] grille){
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if(grille[i][j]!=0){
                    return false;
                }
            }
        }
        return true;
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

    private void majEcran(JFrame frame, int[][] ecouteGrille, GrilleInterface ri){
        for (Component composant : frame.getContentPane().getComponents()) {
            frame.remove(composant);
        }

        for (int i = 0; i < ecouteGrille.length; i++) {
            for (int j = 0; j < ecouteGrille.length; j++) {
                if(ecouteGrille[i][j]==1){
                    ImageIcon imgCroix = new ImageIcon("croix.png");
                    JLabel croixLb = new JLabel(imgCroix);
                    frame.add(croixLb);
                }
                else if(ecouteGrille[i][j]==-1){
                    ImageIcon imgRond = new ImageIcon("rond.png");
                    JLabel rondLb = new JLabel(imgRond);
                    frame.add(rondLb);
                }
                else{
                    JButton button = new JButton();
                    button.setVisible(true);
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae){
                            Container container = button.getParent();
                            System.out.println(container);
                            int index = container.getComponentZOrder(button);
                            System.out.println("index = "+index);
                            
                            // Vérifier si l'emplacement est vide dans la grille
                            int x = index / 3;
                            int y = index % 3;

                            System.out.println("x : "+x+"; y : "+y);
                            if(ecouteGrille[x][y] == 0){
                                // Placer la forme sur la grille
                                // Mettre à jour l'interface graphique
                                frame.remove(button);

                                int[][] grille = new int[3][3];
                                try {
                                    ri.placerForme(x, y, forme);
                                    grille=ri.getGrille();
                                } 
                                catch(Exception e) {
                                    System.out.println(e);
                                }

                                
                                majEcran(frame, grille, ri);
                            } 
                            else {
                                // Informer que l'emplacement est déjà occupé
                                JOptionPane.showMessageDialog(frame, "Emplacement déjà rempli. Choisissez-en un autre !");
                            }
                        }
                    });
                    frame.add(button);
                }
            }
        }
        frame.revalidate();
        frame.repaint();
    }
}


