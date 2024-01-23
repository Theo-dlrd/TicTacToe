import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;

public class Server {
    public Server(){
        try{    
            System.out.println("Server : construction...");
            Grille grille = new Grille();
            LocateRegistry.createRegistry(1099);
            System.out.println("Objet Grille lié dans le RMIregistry.");
            Naming.rebind("rmi:/"+getIPAdress()+":1099/Grille", grille);
            System.out.println("Attente des invocations des clients...");
        }
        catch(Exception e){
            System.out.println("Erreur de laison objet Grille !");
            System.out.println(e.toString());
        }
    }

    private static String getIPAdress(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                // Filtrer les interfaces qui ne sont pas actives
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        System.out.println("Adresse IP Server : " + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
