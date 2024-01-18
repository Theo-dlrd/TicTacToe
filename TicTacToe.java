import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.BoxLayout;

public class TicTacToe extends JFrame{
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;

    public TicTacToe(){
        //Paramètres de la fenêtre
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
        
        //Paramètres du panel
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.menu(panel);
        this.add(panel);

        //Affichage de la fenêtre
        this.pack();
        this.setVisible(true);
    }

    public void setButtonAppearence(JButton button){
        button.setBackground(Color.cyan);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 30));
    }

    private void menu(JPanel panel){
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ImageIcon imageTictactoe = new ImageIcon("image1.png");
        JLabel imgLabel = new JLabel(imageTictactoe);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(WINDOW_WIDTH, 600));
        

        JButton joinButton = new JButton("Rejoindre une partie");
        JButton quitButton = new JButton("Quitter");
        JButton hostButton = new JButton("Héberger une partie");

        setButtonAppearence(quitButton);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        
        setButtonAppearence(joinButton);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                int playPressed = 0;
                System.out.println("Demande de rejoindre une partie");
                panel.remove(joinButton);
                panel.remove(hostButton);

                JLabel txtCode = new JLabel("Code partie");
                JTextField codeField = new JTextField(10);

                JButton playButton = new JButton("Jouer");
                setButtonAppearence(playButton);
                playButton.setVisible(false);
                

                codeField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        if(codeField.getText().length()>0 && playButton.isVisible()==false){
                            playButton.setVisible(true);
                            panel.repaint();
                        }
                        else if(codeField.getText().length()==0 && playButton.isVisible()==true){
                            playButton.setVisible(false);
                            panel.repaint();
                        }
                    }
                });

                playButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        System.out.println("Lancement du jeu...\n");
                    }
                });

                panel.add(txtCode);
                panel.add(codeField);
                panel.add(playButton);
                panel.repaint();
            }
        });

        setButtonAppearence(hostButton);
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Demande de créer une partie");
            }
        });

        panel.add(Box.createVerticalStrut(30));
        panel.add(imgLabel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(hostButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(joinButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(quitButton);
    }
    
    public static void main(String[] args) {
        new TicTacToe();
    }
}