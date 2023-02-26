import javax.swing.*;
import java.awt.*;

public class Board extends JPanel//is this class needed?
{//maybe make a layered panel class that holds this and ships? or just put all in client
    int[][] boardValues = new int[10][10];
    Boolean[][] moveHistory = new Boolean[10][10];
    private int length, height; 
    private int xPos, yPos;

    public Board(int _xPos, int _yPos, int _length, int _height)
    {
        length = _length; height = _height;
        xPos = _xPos; yPos = _yPos;
        setSize(new Dimension(length, height));
        setLocation(xPos, yPos);
        setOpaque(false);
    }

    public void setBoard(int[][] vals) { boardValues = vals; }
    public int[][] getBoard() { return boardValues; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);//
        Graphics2D g2 = (Graphics2D)(g);
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.black);//not dynamic OG color: new Color(3,18,118)
        for (int i = 0; i <= 10; i++)
        {
            g2.drawLine((i*(getBounds().width/10)), 0, (i*(getBounds().width/10)), getBounds().height);
            g2.drawLine(0, (i*(getBounds().height/10)), getBounds().width, (i*(getBounds().height/10)));
        }
    }
}