import javax.swing.*;

public class App extends JFrame {

    public App() {
        super("Snake Game");
        add (new Board());
        pack();

        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
    }
          
    public static void main(String[] args) throws Exception {
        new App().setVisible(true);
    }
}
