import java.rmi.*;

public interface Grille extends Remote{
    public static void clear() throws RemoteException;
    public static void placeCroix(int x, int y) throws RemoteException;
    public static void placeCroix(int x, int y) throws RemoteException;
}
