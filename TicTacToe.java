import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class TicTacToe extends JFrame{
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    public TicTacToe(){
        super("TicTacToe");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()-WINDOW_WIDTH)/2);
        int y = (int) ((dimension.getHeight()-WINDOW_HEIGHT)/2);
        this.setBackground(Color.WHITE);
        this.setLocation(x, y);
        this.setResizable(false);
        this.setLayout(null);

        this.menu();

        this.pack();
        this.setVisible(true);
    }

    public void setButtonAppearence(JButton button, int x, int y, int width, int height){
        button.setBackground(Color.cyan);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBounds(x,y,width,height);
    }

    private void menu(){
        ImageIcon imageTictactoe = new ImageIcon("image1.png");
        JLabel imgLabel = new JLabel(imageTictactoe);
        imgLabel.setBounds(WINDOW_WIDTH/2-325, 0, 650, 600);

        JButton quitButton = new JButton("Quitter");
        this.setButtonAppearence(quitButton, WINDOW_WIDTH/2-100, WINDOW_HEIGHT-150, 200, 30);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        JButton joinButton = new JButton("Rejoindre une partie");
        this.setButtonAppearence(joinButton, WINDOW_WIDTH/2-100, WINDOW_HEIGHT-200, 200, 30);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Demande de rejoindre une partie");
            }
        });

        JButton hostButton = new JButton("Héberger une partie");
        this.setButtonAppearence(hostButton, WINDOW_WIDTH/2-100, WINDOW_HEIGHT-250, 200, 30);
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Demande de créer une partie");
            }
        });

        this.add(quitButton);
        this.add(hostButton);
        this.add(joinButton);
        this.add(imgLabel);
    }

    
    public static void main(String[] args) {
        new TicTacToe();
    }
}