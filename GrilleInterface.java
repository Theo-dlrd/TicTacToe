import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface permettant l'utilisation de méthode à la modification et vérification de la grille ainsi que des joueurs.
 */
public interface GrilleInterface extends Remote{

    /**
     * Enumaration de type Status pouvant prendre comme valeur
     * 
     */
    public static enum Status {
        /**
         * Etat du joueur quand il est pret à jouer.
         */
        READY,
        
        /**
         * Etat du joueur quand il attend une action du serveur ou de son adversaire.
         */
        WAITING,
        
        /**
         * Etat du joueur lorsqu'il vient d'être créé.
         */
        CREATED};

    /**
     * Méthode verifiant que tous les joueurs ont le statut PRET (READY) avant de débuter une partie.
     * @return [Boolean] Un booléen informant que tous les joueurs sont prets.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public boolean allStatusReady() throws RemoteException; 

    /**
     * Méthode permettant d'assigner à un joueur un statut.
     * @param id [Integer] Le numero d'identifiant d'un joueur.
     * @param st [Status] Le statut à assigner au joueur.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public void setStatus(int id, Status st) throws RemoteException;

    /**
     * Méthode permettant de récupérer l'état d'un joueur.
     * @param id [Integer] Le numero d'identifiant du joueur.
     * @return [Status] Le statut du joueur (CREATED,WAINTING,READY).
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public Status getStatus(int id) throws RemoteException;

    /**
     * Méthode de récupération du tour actuel, c'est-à-dire de l'identifiant du joueur qui doit jouer.
     * @return [Integer] Numero d'id du joueur qui doit jouer.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int getTour() throws RemoteException;

    /**
     * Méthode permettant de passer le tour du joueur. C'est au joueur suivant de jouer.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public void passerTour() throws RemoteException;

    /**
     * Méthode permettant de connaitre le nombre de joueur dans la partie.
     * @return [Integer] Nombre de joueur connecté à la partie.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int getNbJoueurs() throws RemoteException;

    /**
     * Méthode permettant à un joueur de rejoindre une partie nouvellement commencée.
     * @param id [Integer] Le numero d'identifiant du joueur.
     * @return [Integer] une valeur caractérisant la connexion (0=réussite, 1=trop de monde, 2=id joueur déjà présent)
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int rejoindrePartie(int id) throws RemoteException;

    /**
     * Méthode permettant de connaitre la forme associée au joueur (1=croix, -1=rond)
     * @param id [Integer] Numéro d'identification du joueur.
     * @return [Integer] Numéro caractérisant la forme du joueur (1=croix, -1=rond)
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int getForme(int id) throws Exception;

    /**
     * Méthode de remise à zéro de la grille.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public void clear() throws RemoteException;

    /**
     * Méthode permettant de placer la forme du joueur (Integer) dans la grille.
     * @param x [Integer] La ligne sur laquelle le forme est placée.
     * @param y [Integer] La colonne sur laquelle la forme est placée.
     * @param forme [Integer] La valeur de la forme à placer.
     * @return [Boolean] Un booléen caractérisant le placement de la forme (true=succès, false=echec).
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public boolean placerForme(int x, int y, int forme) throws RemoteException;

    /**
     * Méthode de récupération de la grille.
     * @return [Integer[][]] Un tableau 3x3 représentant la grille du jeu.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int[][] getGrille() throws RemoteException;

    /**
     * Méthode permettant au joueur de quitter une partie.
     * @param id [Integer] Le numero d'identifiant du joueur.
     * @return [Boolean] Un booléen qui informe si la déconnexion a bien eu lieu ou pas.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public boolean quitterPartie(int id) throws RemoteException;

    /**
     * Méthode retournant le numero d'identifiant du joueur gagnant
     * @return [Integer] Le numero d'identifiant du gagnant
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int getGagnant() throws RemoteException;

    /**
     * Méthode vérifiant que la dernière forme placée permet d'obtenir une configuration gagnante pouyr l'un des deux joueurs.
     * @param forme [Integer] Forme du joueur dernièrement placée.
     * @return [Boolean] Booléen indiquant si on est dans une configuration gagnante ou pas.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public boolean victoire(int forme) throws RemoteException;

    /**
     * Méthode verifiant que la grille est complétée. Permet d'obteniur le cas où on arrive sur une égalité.
     * @return [Boolean] Un booléen qui dis si les joueurs sont dans une configuration d'égalité.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public boolean nul() throws RemoteException;

    /**
     * Méthode permettant de récupérer la forme de l'adversaire.
     * @param id [Integer] Le numero d'identifiant du joueur courant.
     * @return [Integer] L'entier qui caractérise la forme de l'adversaire.
     * @throws RemoteException Si une erreur survient lors de la connection au serveur ou lors de l'établissement du réseau par le serveur.
     */
    public int getOpponentForm(int id) throws RemoteException;
}
