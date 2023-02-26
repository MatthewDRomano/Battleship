import javax.swing.*;
import java.awt.*;

public class Ship extends AbstractButton
{
    private Type shipType;
    private int length, height = 1; //1 is temp (until it is changable via rotate) prob gonna need to add ;ength/heigh setters
    private int posX, posY;
    private int scaleFactor; 
    private boolean rotated = false;
    private ImageIcon imageIcon, imageIconR;
    private JLabel imgLabel;

    public Ship(Type type, int _length, int x, int y, int _scaleFactor)
    {
        scaleFactor = _scaleFactor;
        shipType = type;
        length = _length;
        posX = x; posY = y;  
        setBounds(posX, posY, (scaleFactor*length), scaleFactor * height);
        setImage();    
         
    }
    public Type getType() { return shipType; }
    public int getLength() { return length; }  
    public int retrieveHeight() { return height; }  
    public boolean isRotated() { return rotated; }
    private void setImage()
    {
        imgLabel = new JLabel();
        imageIcon = null;       
        
        switch (shipType)
        {
            case Carrier:
            imageIcon = new ImageIcon("normandy.png");
            imageIconR = new ImageIcon("1normandy.png");
            break;

            case Battleship:
            imageIcon = new ImageIcon("reaper.png");
            imageIconR = new ImageIcon("1reaper.png");
            break;

            case Submarine:
            imageIcon = new ImageIcon("Crucible.png");
            imageIconR = new ImageIcon("1crucible.png");
            break;

            case PatrolBoat:
            imageIcon = new ImageIcon("me3gunship.png");
            imageIconR = new ImageIcon("1me3gunship.png");
            break;   
        }
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(scaleFactor*length, scaleFactor*height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);  // transform it back
        imgLabel.setIcon(imageIcon); 
        add(imgLabel);
    }
    public void rotate()//use paint to draw/rotate
    {
        ImageIcon newIcon = null;
        if (!rotated) newIcon = imageIconR;
        else if (rotated) newIcon = imageIcon;
        Image image = (newIcon.getImage()).getScaledInstance(scaleFactor*height, scaleFactor*length,  java.awt.Image.SCALE_SMOOTH);
        newIcon = new ImageIcon(image);
        imgLabel.setIcon(newIcon);
        setBounds(getX(), getY(), (scaleFactor*height), scaleFactor * length);
        int temp = length;
        length = height;
        height = temp;
        rotated = !rotated;
    }
    public void scaleImage(int scaleFactor)
    {            
        ImageIcon imageIcon1 = null;
        if (!rotated) imageIcon1 = new ImageIcon(imageIcon.getImage().getScaledInstance(scaleFactor*length, scaleFactor*height, java.awt.Image.SCALE_SMOOTH)); //100, 100 add your own size
        else if (rotated) imageIcon1 = new ImageIcon(imageIconR.getImage().getScaledInstance(scaleFactor*length, scaleFactor*height, java.awt.Image.SCALE_SMOOTH)); //100, 100 add your own size
        imgLabel.setIcon(imageIcon1);
    }
    enum Type
    {
        Carrier,
        Battleship,
        Submarine,
        PatrolBoat
    }
}