import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GrilleRemote extends UnicastRemoteObject implements Grille{
    private Forme grille [][];

    GrilleRemote() throws RemoteException{
        super();
        this.grille = new Forme[3][3];
    }

    public void clear(){
        this.grille = new Forme[3][3];
    }

    public void placeCroix(int x, int y){
        this.grille[x][y] = new Croix();
    }

    public void placeRond(int x, int y){
        this.grille[x][y] = new Rond();
    }
}
