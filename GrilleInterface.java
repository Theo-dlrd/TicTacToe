import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GrilleInterface extends Remote{
    public void clear() throws RemoteException;
    public void placeCroix(int x, int y) throws RemoteException;
    public void placeRond(int x, int y) throws RemoteException;
    public int[][] getGrille() throws RemoteException;
}
