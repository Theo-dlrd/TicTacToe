import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GrilleInterface extends Remote{

    public static enum Status {READY,WAITING,CREATED};

    public boolean allStatusReady() throws RemoteException; 
    public void sendStatus(int id, Status st) throws RemoteException;
    public Status getStatus(int id) throws RemoteException;
    public int getTour() throws RemoteException;
    public void passerTour() throws RemoteException;
    public int getNbJoueurs() throws RemoteException;
    public int rejoindrePartie(int id) throws RemoteException;
    public int getForme(int id) throws Exception;
    public void clear() throws RemoteException;
    public int placerForme(int x, int y, int forme) throws RemoteException;
    public int[][] getGrille() throws RemoteException;
}
