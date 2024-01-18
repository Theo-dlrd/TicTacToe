import java.awt.Dimension;
import java.rmi.*;
import java.util.ArrayList;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.GridLayout;


public class Client {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    private static void lancerTicTacToe(JFrame frame){
        frame.setLayout(new GridLayout(3,3));
        frame.setBounds(WINDOW_WIDTH/2-250, WINDOW_HEIGHT/2-250, 500, 500);
        ArrayList<JButton> buttons = new ArrayList<JButton>(9);
        for (int i = 0; i < 9; i++) {
            JButton button = new JButton();
            button.addActionListener(new ActionListener(){
                @Override
                    public void actionPerformed(ActionEvent a){
                        int x = buttons.indexOf(button)/3;
                        int y = buttons.indexOf(button)%3;
                        frame.remove(button);
                        try{
                            GrilleInterface ri = (GrilleInterface) Naming.lookup("rmi://localhost:1099/Grille");
                            System.out.println("Début communication avec le serveur !\n");
                            ri.placeCroix(x, y);
                            int[][] nouvGrille = ri.getGrille();
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < 3; j++) {
                                    System.out.print(" "+nouvGrille[i][j]+" ");
                                }
                                System.out.print("\n");
                            }
                        }
                        catch(Exception e){
                            System.out.println("Erreur de laison objet Grille !");
                            System.out.println(e.toString());
                        }
                    }
            });
            buttons.add(button);
            frame.add(button);
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("TicTacToe");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()-WINDOW_WIDTH)/2);
        int y = (int) ((dimension.getHeight()-WINDOW_HEIGHT)/2);
        frame.setBackground(Color.WHITE);
        frame.setLocation(x, y);
        frame.setResizable(false);
        frame.setVisible(true);

        lancerTicTacToe(frame);

        frame.pack();
    }
}
