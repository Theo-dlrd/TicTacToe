import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Grille extends UnicastRemoteObject implements GrilleInterface{

    private int grille [][];

    Grille() throws RemoteException{
        super();
        this.grille = new int[3][3];
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                this.grille[i][j] = 0;
            }
        }
    }

    public int placeCroix(int x, int y){
        this.grille[x][y] = 1;
        if(win(x, y, 1)){
            return 1;
        }
        else if(isDraw()){
            return 10;
        }
        return 0;
    }

    public int placeRond(int x, int y){
        this.grille[x][y] = -1;
        if(win(x, y, -1)){
            return -1;
        }
        else if(isDraw()){
            return 10;
        }
        return 0;
    }

    public int[][] getGrille(){
        return grille;
    }

    public void clear(){
        this.grille = new int[3][3];
    }

    private boolean isDraw(){
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if(grille[i][j]!=0){
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
