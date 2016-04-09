import javax.swing.*;
import java.awt.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PingPong extends JFrame{
    private pongBoard Board;

    public PingPong(){
        renderDisplay();
    }

    private void renderDisplay(){
        setSize(400,400);
        setResizable(true);
        setTitle("Ping Pong");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setBackground(Color.BLACK);
        Container c = this.getContentPane();
        c.setBackground(Color.BLACK);
        // center window on screen
        setLocationRelativeTo(null);
        Board = new pongBoard(this);
        add(Board);
    }

    public static void main(String args[]){
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                PingPong app = new PingPong();
                app.setVisible(true);
            }
        });
    }
}