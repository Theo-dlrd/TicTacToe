import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GrilleInterface extends Remote{
    public boolean rejoindrePartie(String nom) throws RemoteException;
    public void clear() throws RemoteException;
    public int placerForme(int x, int y, int forme) throws RemoteException;
    public int[][] getGrille() throws RemoteException;
}
