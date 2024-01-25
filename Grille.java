import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;


/**
 * Classe permettant d'implémenter les méthodes qu'utilisera le client pour communiquer avec le serveur
 */
public class Grille extends UnicastRemoteObject implements GrilleInterface{

    private int grille [][];
    private HashMap<Integer,Integer> joueurs;
    private HashMap<Integer,Status> joueursStatus;
    private int tour;
    private int id_gagnant;

    /**
     * Méthode d'instanciation de la grille de TicTacToe présente sur le serveur.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    Grille() throws RemoteException{
        super();
        this.grille = new int[3][3];
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                this.grille[i][j] = 0;
            }
        }
        this.joueurs = new HashMap<>();
        this.joueursStatus = new HashMap<>();
        this.tour=0;
        this.id_gagnant=0;
    }

    /**
     * Méthode permettant de connaitre la forme associée au joueur (1=croix, -1=rond)
     * @param id [Integer] Numéro d'identification du joueur.
     * @return [Integer] Numéro caractérisant la forme du joueur (1=croix, -1=rond)
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public int getForme(int id) throws RemoteException{
        System.out.println("Recherche de : "+id);
        return joueurs.get(id);
    }

    /**
     * Méthode permettant de récupérer la forme de l'adversaire.
     * @param id [Integer] Le numero d'identifiant du joueur courant.
     * @return [Integer] L'entier qui caractérise la forme de l'adversaire.
     */
    @Override
    public int getOpponentForm(int id) throws RemoteException{
        for (HashMap.Entry<Integer, Integer> entry : joueurs.entrySet()) {
            if (entry.getKey().equals(id)==false) {
                return entry.getValue();
            }
        }
        return 0;
    }

    /**
     * Méthode permettant de connaitre le nombre de joueur dans la partie.
     * @return [Integer] Nombre de joueur connecté à la partie.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public int getNbJoueurs() throws RemoteException{
        return joueurs.size();
    }

    /**
     * Méthode de récupération du tour actuel, c'est-à-dire de l'identifiant du joueur qui doit jouer.
     * @return [Integer] Numero d'id du joueur qui doit jouer.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public int getTour() throws RemoteException {
        return tour;
    }

    /**
     * Méthode permettant de passer le tour du joueur. C'est au joueur suivant de jouer.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public void passerTour() throws RemoteException {
        if(this.tour == 0){
            this.tour = joueurs.keySet().iterator().next();  // Si c'est le premier tour, définir le premier joueur
        } 
        else{
            // Changer de joueur
            for(int id : joueurs.keySet()) {
                if(id != this.tour){
                    this.tour = id;
                    break;
                }
            }
        }
        System.out.println("Tour du joueur id : " + this.tour);
    }


    /**
     * Méthode permettant à un joueur de rejoindre une partie nouvellement commencée.
     * @param idJoueur [Integer] Le numero d'identifiant du joueur.
     * @return [Integer] une valeur caractérisant la connexion (0=réussite, 1=trop de monde, 2=id joueur déjà présent)
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public int rejoindrePartie(int idJoueur) throws RemoteException {
        if (joueurs.size() < 2 && joueurs.get(idJoueur)==null) {
            int symbole = (joueurs.size() == 0) ? 1 : -1;
            joueurs.put(idJoueur, symbole);
            joueursStatus.put(idJoueur, Status.CREATED);
            System.out.println(idJoueur+" nails the competition !");
            System.out.println(idJoueur+" : "+joueurs.get(idJoueur));
            return 0;
        } 
        else if(joueurs.get(idJoueur)!=null){
            return -1;
        }
        else {
            System.out.println(idJoueur+" go fuck yourself !");
            return 1;
        }
    }

    /**
     * Méthode permettant au joueur de quitter une partie.
     * @param id [Integer] Le numero d'identifiant du joueur.
     * @return [Boolean] Un booléen qui informe si la déconnexion a bien eu lieu ou pas.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public boolean quitterPartie(int id) throws RemoteException{
        joueurs.remove(id);
        joueursStatus.remove(id);
        if(joueurs.get(id)!=null || joueursStatus.get(id)!=null){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Méthode verifiant que tous les joueurs ont le statut PRET (READY) avant de débuter une partie.
     * @return [Boolean] Un booléen informant que tous les joueurs sont prets.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public boolean allStatusReady() throws RemoteException{
        for (Integer key : joueursStatus.keySet()) {
           if(joueursStatus.get(key)!=Status.READY){
            return false;
           }
        }
        return true;
    }

    /**
     * Méthode permettant de récupérer l'état d'un joueur.
     * @param id [Integer] Le numero d'identifiant du joueur.
     * @return [Status] Le statut du joueur (CREATED,WAINTING,READY).
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public Status getStatus(int id) throws RemoteException{
        return joueursStatus.get(id);
    }

    /**
     * Méthode permettant d'assigner à un joueur un statut.
     * @param id [Integer] Le numero d'identifiant d'un joueur.
     * @param st [Status] Le statut à assigner au joueur.
     */
    @Override
    public void sendStatus(int id, Status st) throws RemoteException{
        joueursStatus.put(id, st);
    }

    /**
     * Méthode permettant de placer la forme du joueur (Integer) dans la grille.
     * @param x [Integer] La ligne sur laquelle le forme est placée.
     * @param y [Integer] La colonne sur laquelle la forme est placée.
     * @param forme [Integer] La valeur de la forme à placer.
     * @return [Boolean] Un booléen caractérisant le placement de la forme (true=succès, false=echec).
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public boolean placerForme(int x, int y, int forme) throws RemoteException{
        if(this.grille[x][y]==0){
            this.grille[x][y] = forme;
            for (int i = 0; i < grille.length; i++) {
                for (int j = 0; j < grille[i].length; j++) {
                    System.out.print(grille[i][j]);
                }
                System.out.print("\n");
            }
            return true;
        }
        else{
            System.out.println("Emplacement déjà rempli. Choisissez-en un autre !");
            return false;
        }
    }

    /**
     * Méthode de récupération de la grille.
     * @return [Integer[][]] Un tableau 3x3 représentant la grille du jeu.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public int[][] getGrille() throws RemoteException{
        return grille;
    }

    /**
     * Méthode de remise à zéro de la grille.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    @Override
    public void clear() throws RemoteException{
        this.grille = new int[3][3];
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                this.grille[i][j]=0;
            }
        }
    }

    /**
     * Méthode verifiant que la grille est complétée. Permet d'obteniur le cas où on arrive sur une égalité.
     * @return [Boolean] Un booléen qui dis si les joueurs sont dans une configuration d'égalité.
     */
    public boolean nul(){
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if(grille[i][j]== 0){
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Méthode vérifiant que la dernière forme placée permet d'obtenir une configuration gagnante pouyr l'un des deux joueurs.
     * @param x [Integer] Ligne sur laquelle la dernière forme a été placé.
     * @param y [Integer] Colonne sur laquelle la dernière forme a été placée.
     * @param forme [Integer] Forme du joueur dernièrement placée.
     * @return [Boolean] Booléen indiquant si on est dans une configuration gagnante ou pas.
     */
    public boolean victoire(int forme){
        if(verifLine(forme) || verifDiag(forme) || verifColumn(forme)){
            for (HashMap.Entry<Integer, Integer> entry : joueurs.entrySet()) {
                if (entry.getValue().equals(forme)) {
                    this.id_gagnant = entry.getKey();
                    break;
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    public int getGagnant() throws RemoteException{
        return this.id_gagnant;
    }



    /**
     * Méthode permettant de vérifier que le dernier point placé permet une victoire verticalement.
     * @param x [Integer] Ligne sur laquelle la dernière forme a été placé.
     * @param y [Integer] Colonne sur laquelle la dernière forme a été placée.
     * @param forme [Integer] Forme du joueur dernièrement placée.
     * @return [Boolean] Booléen indiquant si on est dans une configuration verticale gagnante ou pas.
     */
    private boolean verifColumn(int forme){
        for (int y = 0; y < grille.length; y++) {
            if(grille[0][y]==forme && grille[1][y]==forme && grille[2][y]==forme){
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode permettant de vérifier que le dernier point placé permet une victoire horizontalement.
     * @param x [Integer] Ligne sur laquelle la dernière forme a été placé.
     * @param y [Integer] Colonne sur laquelle la dernière forme a été placée.
     * @param forme [Integer] Forme du joueur dernièrement placée.
     * @return [Boolean] Booléen indiquant si on est dans une configuration horizontale gagnante ou pas.
     */
    private boolean verifLine(int forme){
        for (int x = 0; x < grille.length; x++) {
            if(grille[x][0]==forme && grille[x][1]==forme && grille[x][2]==forme){
                return true;
            }
        }
        return false;
    }


    /**
     * Méthode permettant de vérifier que le dernier point placé permet une victoire en diagonale.
     * @param x [Integer] Ligne sur laquelle la dernière forme a été placé.
     * @param y [Integer] Colonne sur laquelle la dernière forme a été placée.
     * @param forme [Integer] Forme du joueur dernièrement placée.
     * @return [Boolean] Booléen indiquant si on est dans une configuration horizontale gagnante ou pas.
     */
    private boolean verifDiag(int forme){
        if(grille[0][0]==forme && grille[1][1]==forme && grille[2][2]==forme){
            return true; 
        }
        if(grille[0][2]==forme && grille[1][1]==forme && grille[2][0]==forme){
            return true; 
        }
        return false;
    }
}
