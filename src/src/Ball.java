package src;

import sun.jvm.hotspot.memory.PlaceholderEntry;

import javax.tools.Tool;
import java.awt.*;

import static src.Paddle.paddleType.HORIZONTAL;
import static src.constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Ball {
    private int xpos;
    private int ypos;
    private int xspeed;
    private int yspeed;
    private int radius=RADIUS;
    private int widthBound = SCREEN_WIDTH;
    private int heightBound;
    private PingPong runningapp;

    public Ball(PingPong app){
        this.runningapp = app;
        xpos = XCENTER;
        ypos = YCENTER;
        xspeed = XSPEED;
        yspeed = YSPEED;
        // put ball in center
        getWorkingScreenSize();
    }

    public void updateLocation(){
        switch (paddleHit()) {
            case 0:
                yspeed = -yspeed;
                // Player0 update
                break;
            case 1:
                xspeed = -xspeed;
                // Player1 update
                break;
            case 2:
                yspeed = -yspeed;
                // Player2 update
                break;
            case 3:
                xspeed = -xspeed;
                // Player3 update
                break;
            default:
                break;
        }
        switch (wallHit()) {
            case 0:
                yspeed = -yspeed;
                // Player0 update
                break;
            case 1:
                xspeed = -xspeed;
                // Player1 update
                break;
            case 2:
                yspeed = -yspeed;
                // Player2 update
                break;
            case 3:
                xspeed = -xspeed;
                // Player3 update
                break;
            default:
                break;
        }
        xpos += xspeed;
        ypos += yspeed;
    }

    public void updateSpeed(){

    }
//    Player0 = new Paddle(app,200,5, HORIZONTAL, HUMAN);
//    Player1 = new Paddle(app,390,182, VERTICAL, AI);
//    Player2 = new Paddle(app,200,370, HORIZONTAL, AI);
//    Player3 = new Paddle(app,5,182, VERTICAL, AI);

    public int paddleHit(){
        Paddle[] Players = runningapp.getBoard().getPlayers();
        for(int i=0; i< Players.length; ++i) {
            switch (i){
                case 0:
                    if(Players[i].getxCollisionBounds(1).left<= xpos && xpos <= Players[i].getxCollisionBounds(1).right && ypos==Players[i].getypos()){
                        System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
                case 1:
                    //System.out.println(String.format("up %d down %d ball %d",Players[i].getyCollisionBounds().left,Players[i].getyCollisionBounds().right,ypos));
                    if(Players[i].getyCollisionBounds(1).left<= ypos && ypos <= Players[i].getyCollisionBounds(1).right && xpos+2*radius-LEN==Players[i].getxpos()){
                        System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
                case 2:
                    if(Players[i].getxCollisionBounds(-1).left<= xpos && xpos <= Players[i].getxCollisionBounds(-1).right && ypos+2*radius-LEN==Players[i].getypos()){
                        System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
                case 3:
                    if(Players[i].getyCollisionBounds(-1).left<= ypos && ypos <= Players[i].getyCollisionBounds(-1).right && xpos==Players[i].getxpos()){
                        System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
            }
        }
        return -1;
    }

    public int wallHit(){
        if(ypos < 0 ) {
            System.out.println(String.format("case = %d, xpos = %d, ypos = %d",0,xpos,ypos));
            return 0;
        }
        if(xpos + 2*radius > SCREEN_WIDTH){
            System.out.println(String.format("case = %d, xpos = %d, ypos = %d",1,xpos,ypos));
            return 1;
        }
        if(ypos + 2*radius > heightBound) {
            System.out.println(String.format("case = %d, xpos = %d, ypos = %d",2,xpos,ypos));
            return 2;
        }
        if(xpos < 0){
            System.out.println(String.format("case = %d, xpos = %d, ypos = %d",3,xpos,ypos));
            return 3;
        }
        return -1;
    }

    public void draw(Graphics g){
        g.setColor(Color.ORANGE);
        g.fillOval(xpos,ypos,2*radius,2*radius);
    }

    public void getWorkingScreenSize() {
        heightBound = SCREEN_HEIGHT - 21;
    }

    public int getxpos() {
        return xpos;
    }

    public int getypos() {
        return ypos;
    }
}