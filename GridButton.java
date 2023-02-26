import java.awt.*;
import javax.swing.*;

public class GridButton extends AbstractButton {
    private Boolean hit = null;
    private boolean sunk;//enabled may not be needed
    //private MouseEventMod mouseEvent;

    public void setHitStatus(boolean data) { hit = data; }    
    public Boolean getHitStatus() { return hit; }
    public void setSunkStatus(boolean data) { sunk = data; }
    public boolean getSunkStatus() { return sunk; }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (hit == null) return;
        else if (hit) 
        {
            g2.setColor(Color.red);
            g2.drawOval((((Board)getParent()).getWidth()/40)+1, (((Board)getParent()).getHeight()/40)+1, ((Board)getParent()).getWidth()/20, ((Board)getParent()).getHeight()/20);//diving by ten then 4, 2 etc
        } // draw red hit
        else if (!hit) 
        {
            g2.setColor(Color.white);
            g2.drawOval((((Board)getParent()).getWidth()/40)+1, (((Board)getParent()).getHeight()/40)+1, ((Board)getParent()).getWidth()/20, ((Board)getParent()).getHeight()/20);//diving by ten then 4, 2 etc
        } // miss, draw white miss
       
        if (sunk) 
        {
            g2.setColor(Color.red);
            g2.fillOval((((Board)getParent()).getWidth()/40)+1, (((Board)getParent()).getHeight()/40)+1, ((Board)getParent()).getWidth()/20, ((Board)getParent()).getHeight()/20);//diving by ten then 4, 2 etc
        } // add something to show its sunk
        else return;     
    }    
}