import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args){
        try{
            System.out.println("Server : construction...");
            Grille grille = new Grille();
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Objet Grille lié dans le RMIregistry.");
            Naming.rebind("rmi:/192.168.1.39:1099/Grille", grille);
            System.out.println("Attente des invocations des clients...");
        }
        catch(Exception e){
            System.out.println("Erreur de laison objet Grille !");
            System.out.println(e.toString());
        }
    }
}
