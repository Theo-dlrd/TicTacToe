import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Grille extends UnicastRemoteObject implements GrilleInterface{
    private int grille [][];

    Grille() throws RemoteException{
        super();
        this.grille = new int[3][3];
    }

    public void clear(){
        this.grille = new int[3][3];
    }

    public void placeCroix(int x, int y){
        this.grille[x][y] = 1;
    }

    public void placeRond(int x, int y){
        this.grille[x][y] = -1;
    }

    public int[][] getGrille(){
        return grille;
    }
}
