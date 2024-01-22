import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Grille extends UnicastRemoteObject implements GrilleInterface{

    private int grille [][];
    private HashMap<Integer,Integer> joueurs;
    private HashMap<Integer,Status> joueursStatus;
    private int tour;


    Grille() throws RemoteException{
        super();
        this.grille = new int[3][3];
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                this.grille[i][j] = 0;
            }
        }
        joueurs = new HashMap<>();
        joueursStatus = new HashMap<>();
        tour=0;
    }

    public int getForme(int id) throws Exception{
        System.out.println("Recherche de : "+id);
        return joueurs.get(id);
    }

    public int getNbJoueurs(){
        return joueurs.size();
    }

    @Override
    public int getTour() throws RemoteException {
        return tour;
    }


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

    public boolean allStatusReady(){
        for (Integer key : joueursStatus.keySet()) {
           if(joueursStatus.get(key)!=Status.READY){
            return false;
           }
        }
        return true;
    }


    public void sendStatus(int id, Status st){
        joueursStatus.put(id, st);
    }


    @Override
    public int placerForme(int x, int y, int forme) throws RemoteException{
        this.grille[x][y] = forme;
        if(win(x, y, forme)){
            return 1;
        }
        else if(isDraw()){
            return 10;
        }
        return 0;
    }

    @Override
    public int[][] getGrille(){
        return grille;
    }

    @Override
    public void clear(){
        this.grille = new int[3][3];
    }

    private boolean isDraw(){
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if(grille[i][j]!= 0){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean win(int x, int y, int forme){
        return (verifLine(x, y, forme) || verifDiag(x, y, forme) || verifColumn(x, y, forme));
    }

    private boolean verifLine(int x, int y, int forme){
        switch(y) {
            case 0:
                return (grille[x][y]==forme && grille[x+1][y]==forme && grille[x+2][y]==forme);
            
            case 1:
                return (grille[x][y]==forme && grille[x+1][y]==forme && grille[x-1][y]==forme); 

            case 2:
                return (grille[x][y]==forme && grille[x-1][y]==forme && grille[x-2][y]==forme); 
        
            default:
                return false;
        }
    }

    private boolean verifColumn(int x, int y, int forme){
        switch(y) {
            case 0:
                return (grille[x][y]==forme && grille[x][y+1]==forme && grille[x][y+2]==forme);
            
            case 1:
                return (grille[x][y]==forme && grille[x][y+1]==forme && grille[x][y-1]==forme); 

            case 2:
                return (grille[x][y]==forme && grille[x][y-1]==forme && grille[x][y-2]==forme); 
        
            default:
                return false;
        }
    }

    private boolean verifDiag(int x, int y, int forme){
        if((x!=0 && y!=1) && (x!=1 && y!=0) && (x!=1 && y!=2) && (x!=2 && y!=1)){
            if((x==0 && y==0) || (x==1 && y==1) || (x==2 && y==2)){
                return (grille[0][0]==forme && grille[1][1]==forme && grille[2][2]==forme);
            }
            if((x==0 && y==2) || (x==1 && y==1) || (x==2 && y==0)){
                return (grille[0][2]==forme && grille[1][1]==forme && grille[2][0]==forme);
            }
        }
        return false;
    }
}
