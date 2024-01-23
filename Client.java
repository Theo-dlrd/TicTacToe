import java.rmi.*;
import java.util.ArrayList;
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
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Component;


public class Client {
    public int forme;
    public int id_joueur;
    private int[][] ecouteGrille;
    

    public Client(JFrame frame, String ip){
        frame.setLayout(new GridLayout(3, 3));

        for(Component component: frame.getContentPane().getComponents()){
            frame.remove(component);
        }
        frame.revalidate();
        frame.repaint();

        

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

            /* 
            ecouteGrille = ri.getGrille();
            for (int i = 0; i < ecouteGrille.length; i++) {
                for (int j = 0; j < ecouteGrille[i].length; j++) {
                    JButton button = new JButton();
                    button.setVisible(true);
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae){
                            Container container = button.getParent();
                            System.out.println(container);
                            int index = container.getComponentZOrder(button);
                            System.out.println("Index = "+index);
                            
                            frame.remove(button);

                            ImageIcon img;
                            if(forme==1){
                                img = new ImageIcon("croix.png");
                            }
                            else{
                                img = new ImageIcon("rond.png");
                            }
                            JLabel imgLb = new JLabel(img);
                            frame.add(imgLb, index);
                            
                            int x = index / 3;
                            int y = index % 3;
                            System.out.println(x+" - "+y);
                            try {
                                ri.placerForme(x, y, forme);
                            } 
                            catch (Exception e) {
                                System.out.println(e);
                            }
                            frame.revalidate();
                            frame.repaint();
                        }
                    });
                    frame.add(button);
                }
            }

            frame.revalidate();
            frame.repaint();

            */
            boolean play = true;
            try{
                //Sélection aléatoire du joueur qui commence
                for(int i=0; i<random.nextInt(10)+1; i++){
                    ri.passerTour();
                }

                wait(3000);

                if(ri.getTour() == this.id_joueur){
                    JOptionPane.showMessageDialog(frame,"A vous de commencer...");
                }
                else{
                    JOptionPane.showMessageDialog(frame,"Votre adversaire commence...");
                }
                
                while(play){
                    ecouteGrille = ri.getGrille();
                    // Vérifier le tour du joueur
                    if (ri.getTour() == this.id_joueur){
                        //Lorsque l'on jouer pour la première fois
                        /* 
                        ArrayList<JButton> listButton = new ArrayList<JButton>();

                        for (Component component : frame.getContentPane().getComponents()){
                            if(component instanceof JButton){
                                component.setEnabled(true);
                                listButton.add((JButton)component);
                            }
                        }
                        System.out.println("Taille list boutons = "+listButton.size());
                        */

                        frame.removeAll();
                        frame.repaint();
                        for (int i = 0; i < ecouteGrille.length; i++) {
                            for (int j = 0; j < ecouteGrille[i].length; j++) {
                                switch (ecouteGrille[i][j]) {
                                    case 1:
                                        ImageIcon imgC = new ImageIcon("croix.png");
                                        JLabel imgCLb = new JLabel(imgC);
                                        frame.add(imgCLb);
                                        break;
                                    case -1:
                                        ImageIcon imgR = new ImageIcon("rond.png");
                                        JLabel imgRLb = new JLabel(imgR);
                                        frame.add(imgRLb);
                                        break;
                                    case 0:
                                        JButton button = new JButton();
                                        button.setVisible(true);
                                        button.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent ae){
                                                Container container = button.getParent();
                                                System.out.println(container);
                                                int index = container.getComponentZOrder(button);
                                                System.out.println("Index = "+index);
                                                
                                                frame.remove(button);
                    
                                                ImageIcon img;
                                                if(forme==1){
                                                    img = new ImageIcon("croix.png");
                                                }
                                                else{
                                                    img = new ImageIcon("rond.png");
                                                }
                                                JLabel imgLb = new JLabel(img);
                                                frame.add(imgLb, index);
                                                
                                                int x = index / 3;
                                                int y = index % 3;
                                                System.out.println(x+" - "+y);
                                                try {
                                                    ri.placerForme(x, y, forme);
                                                } 
                                                catch (Exception e) {
                                                    System.out.println(e);
                                                }
                                                frame.revalidate();
                                                frame.repaint();
                                            }
                                        });
                                        button.setEnabled(true);
                                        frame.add(button);
                                        break;
                                }
                            }
                        }
                        frame.revalidate();
                        frame.repaint();
                        
                        //JOptionPane.showMessageDialog(frame,"A vous de jouer...");
                        System.out.println("avant boucle verif idem");
                        while(sameGrille(ecouteGrille, ri.getGrille()));
                        System.out.println("apres boucle verif idem");

                        ri.passerTour();
                        
                    } 
                    else {
                        /*
                        for (Component component : frame.getContentPane().getComponents()){
                            if(component instanceof JButton){
                                component.setEnabled(false);
                            }
                        }
                        */
                        frame.repaint();
                        
                        //JOptionPane.showMessageDialog(frame,"Au tour de votre adversaire. Veuillez patienter...");
                        System.out.println("avant boucle verif idem NMT");
                        while (sameGrille(ecouteGrille, ri.getGrille()));
                        System.out.println("avant boucle verif idem NMT");
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
}


