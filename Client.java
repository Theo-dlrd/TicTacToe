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
import javax.swing.Timer;

import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Container;


/**
 * Classe implémentant les actions et affichages des clients sur la fenêtre.
 */
public class Client {
    public int forme;
    public int id_joueur;
    private int[][] ecouteGrille;

    /**
     * Méthode d'instanciation du client.
     * @param frame [JFrame] La page sur laquelle les éléments visueles seront affichés.
     * @param ip    [String] L'adresse ip interne du serveur (null pour le client qui créer la partie).
     */
    public Client(JFrame frame, String ip){
        for(Component component: frame.getContentPane().getComponents()){
            frame.remove(component);
        }
        frame.revalidate();
        frame.repaint();


        frame.setLayout(new GridLayout(3, 3));
        

        try{
            //Gestion de la partie serveur
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

            //Vérifier que l'on a bien 2 joueurs
            while (ri.getNbJoueurs()<2);

            frame.setVisible(true);

            //Vérifier que les deux joueurs sont prets
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

                        majEcran(frame, ecouteGrille, ri);

                        while(sameGrille(ecouteGrille, ri.getGrille()));

                        if(ri.victoire(this.forme) || ri.nul()){
                            play=false;
                            ecouteGrille = ri.getGrille();
                            majEcran(frame, ecouteGrille, ri);
                        }
                        else{
                            ri.passerTour();
                        }
                    }
                    else {
                        // Attendre que ce soit le tour du joueur actuel
                        try {
                            Thread.sleep(1000);
                        } 
                        catch(InterruptedException e){
                            e.printStackTrace();
                        }

                        if(ri.victoire(ri.getOpponentForm(this.id_joueur)) || ri.nul()){
                            play=false;
                            ecouteGrille = ri.getGrille();
                            majEcran(frame, ecouteGrille, ri);
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } 
                catch(InterruptedException e){
                    e.printStackTrace();
                }

                //Déterminer l'issue de la partie
                int id_gagnant = ri.getGagnant();
                if(id_gagnant!=0){
                    if(id_gagnant==this.id_joueur){
                        JOptionPane.showMessageDialog(frame, "Félicitations ! Vous avez gagné !");
                        System.exit(0);
                    }
                    else{
                        JOptionPane.showMessageDialog(frame, "Dommage ! Tu as perdu !");
                        System.exit(0);
                    }
                }
                else if(ri.nul()){
                    JOptionPane.showMessageDialog(frame, "Oufff ! Egalité !");
                    System.exit(0);
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Erreur : finalité non trouvée !");
                    System.exit(-1);
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

    /**
     * Méthode permettant de récupérer l'adresse ip locale de la machine sur laquelle le client est lancé.
     * @return [String] L'adresse ip locale du client.
     */
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


    /**
     * Méthode de vérification d'égalité entre deux matrice. Permet le passage de tour.
     * @param grille1 [int[][]] Première grille à vérifier.
     * @param grille2 [int[][]] Deuxième grille à vérifier.
     * @return [Boolean] Un booléen indiquant si les deux matrices sont égales ou pas.
     */
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


    /**
     * Méthode de mise à jour de l'écran des joueurs après que l'un des deux ait joué.
     * @param frame [JFrame] Affichage dans la fenêtre à modifier.
     * @param ecouteGrille  [int[][]] Matrice de la grille qui va servir de base pour mettre à jour l'interface.
     * @param ri [GrilleInterface] Instanciation de la grille qui permet d'appeler les méthodes liées à celle-ci.
     */
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

                                try {
                                    ri.placerForme(x, y, forme);
                                } 
                                catch(Exception e) {
                                    System.out.println(e);
                                }

                                for (Component composant : frame.getContentPane().getComponents()) {
                                    frame.remove(composant);
                                }
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
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.revalidate();
                frame.repaint();
            }
        });

        timer.start();
    }
}


