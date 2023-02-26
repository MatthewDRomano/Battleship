import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.MouseInputAdapter;

public class Client //work on UI and make ship rotation ---> then work on enemy
{
    public static DragListener drag = null;  
    public static boolean homeView = true;
    public static JFrame gui;
    public static JLayeredPane layeredPane;
    public static Board friendlyBoard, enemyBoard;
    public static Ship[] ships = new Ship[4];
    public static Ship[] enemyShips = new Ship[4]; 
    public static JButton startButton, fireButton, switchViewButton, enemyPanelButton;
    public static GridButton[][] fBoardSpot = new GridButton[10][10], eBoardSpot = new GridButton[10][10];
    public static MouseEventMod mouseEvent;
    private static JTextArea display;
    public static int[][] placeholder = new int[10][10];//if i dont use this my code breaks don't ask me why I don't know

    //public static JPanel menuSquare;
    static class MouseEventMod implements MouseListener {
        public boolean allowed = true;// make getters setters

        public void mousePressed(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
        public void allowHit() 
        {
            allowed = true;
        } // call this when its 'your' turn

        public void mouseClicked(MouseEvent e) {
            //if (!turn) return;     
            Component component = e.getComponent();
            if (((GridButton)component).getName() == "yes" || ((Board)(component.getParent())).getWidth() < 500) return; // only can shoot if enemy panel is main screen
            if (((GridButton)component).getHitStatus() != null) return;

            int[][] boardVals = enemyBoard.getBoard();
            if (boardVals[(component.getY()/50)][(component.getX()/50)] > 1) {
                ((GridButton)component).setHitStatus(true);
                //allowed = false;               
            }
            else  {
                ((GridButton)component).setHitStatus(false);
            }
            ((GridButton)component).repaint();

            shipHitDetecter(eBoardSpot, enemyBoard, boardVals[(component.getY()/50)][(component.getX()/50)]);

            if (determineWin(1)) { display.setText(display.getText() + "\nYou Win!"); JOptionPane.showMessageDialog(gui, "You Win!"); endGame(); }
            makeEnemyMove(); 

            if (determineWin(0)) { display.setText(display.getText() + "\nYou Lost!"); JOptionPane.showMessageDialog(gui, "You Lost!"); endGame(); }
        }
    }
    public static void main(String args[]) //https://onlinepngtools.com/change-png-opacity
    {//check gpt history for inspiration/help
        gui = new JFrame()
        {
            {
                setSize(1000,737);
                //setBackground(Color.blue);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setResizable(false);
                setLayout(null);
                setTitle("Battle Ship Game");          
            }
        };              

        layeredPane = new JLayeredPane();//
        layeredPane.setBounds(gui.getBounds());        
        gui.add(layeredPane);

        friendlyBoard = new Board(50,50,500,500);
        layeredPane.add(friendlyBoard, 1); 

        //#region Set Frame Background
        JLabel bgLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("bg4.png");
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(1000, 737,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);  // transform it back
        bgLabel.setIcon(imageIcon); 
        bgLabel.setBounds(0,0,1000,737);
        layeredPane.add(bgLabel, 1);
        //#endregion 

        enemyBoard = new Board(615,350,300,300);
        layeredPane.add(enemyBoard, 1);

     
        createBorders();
        createButtonsMenu();
        createChat();
        createShips();
        createEnemyShips();

        GridLayout friendlyBLayout = new GridLayout(10,10);
        GridLayout enemyBLayout = new GridLayout(10,10);
        friendlyBoard.setLayout(friendlyBLayout);
        enemyBoard.setLayout(enemyBLayout);
        
        mouseEvent = new MouseEventMod();
        for (int i = 0; i < fBoardSpot.length; i++)//add these to a panel on top layer, put that layer bounds to the fboard and eboard bounds
        {
            for (int q = 0; q < fBoardSpot[i].length; q++)
            {
                fBoardSpot[i][q] = new GridButton();
                eBoardSpot[i][q] = new GridButton();
                
                friendlyBoard.add(fBoardSpot[i][q]);
                enemyBoard.setName("yes");
                enemyBoard.add(eBoardSpot[i][q]);

                eBoardSpot[i][q].addMouseListener(mouseEvent);
            }
        }
        gui.setVisible(true);
    }
    public static void shipHitDetecter(GridButton[][] arr, Board board, int shiplength)
    {
        if (shiplength == 0) return;
        int counter = 0;

        for (int i = 0; i < arr.length; i++)
        {
            for (int q = 0; q < arr[i].length; q++)
            {
                if (arr[i][q].getHitStatus() != null && (board.getBoard())[i][q] == shiplength && arr[i][q].getHitStatus()) counter++;
                //if (counter == 1) { }
            }
        }

        String name = "";
        switch (shiplength)
        {
            case 2:
                name = "Patrol Boat";
                break;
            case 3:
                name = "Submarine";
                break;
            case 4:
                name = "Battleship";
                break;
            case 5:
                name = "Carrier";
                break;
        }

        if (arr == eBoardSpot && counter == shiplength) display.setText(display.getText() + "\nYou sunk their " + name + "!");
        else if (arr == fBoardSpot && counter == shiplength) display.setText(display.getText() + "\nYour " + name + " was sunk!");
    }
    public static void endGame()
    {
        friendlyBoard.setEnabled(false);
        enemyBoard.setEnabled(false);
        switchViewButton.setEnabled(false);
        fireButton.setEnabled(false);

        for (int i = 0; i < eBoardSpot.length; i++)
            for (int q = 0; q < eBoardSpot[i].length; q++)
                eBoardSpot[i][q].removeMouseListener(mouseEvent);
    }
    public static boolean determineWin(int decider)
    {
        int counter = 0;
        if (decider == 0) 
        {
            for (int i = 0; i < fBoardSpot.length; i++)        
            {
                for (int q = 0; q < fBoardSpot[i].length; q++)    
                {
                    if (fBoardSpot[i][q].getHitStatus() == null) continue; //dodges errors
                    if (fBoardSpot[i][q].getHitStatus()) counter++;
                }
            }
        }    
        else if (decider == 1)
        {           
            for (int i = 0; i < eBoardSpot.length; i++)        
            {
                for (int q = 0; q < eBoardSpot[i].length; q++)    
                {
                    if (eBoardSpot[i][q].getHitStatus() == null) continue; //dodges errors
                    if (eBoardSpot[i][q].getHitStatus()) counter++;
                }
            }
            
        }
        if (counter == 14) return true;
        else return false;    
    }
    public static void makeEnemyMove()
    {
        int[][] boardVals = friendlyBoard.getBoard();
        int x = (int)(Math.random()*10), y = (int)(Math.random()*10);

        while (fBoardSpot[y][x].getHitStatus() != null)
        {
            x = (int)(Math.random()*10); 
            y = (int)(Math.random()*10);
        }
        if (boardVals[y][x] > 1) {
            fBoardSpot[y][x].setHitStatus(true);            
        }
        else  {
            fBoardSpot[y][x].setHitStatus(false);
        }
        fBoardSpot[y][x].repaint();
        shipHitDetecter(fBoardSpot, friendlyBoard, boardVals[y][x]);
    }
    public static void switchView()//maybe i dont need to switch panels
    {
        if (homeView)
        {
            for (Ship ship : ships) 
            {
                ship.setBounds(((ship.getX()-50)/50)*30+615, ((ship.getY()-50)/50)*30+350, ship.getLength()*30, ship.retrieveHeight()*30);
                ship.scaleImage(30);
                homeView = false;
            }
        }
        else if (!homeView)
        {
            for (Ship ship : ships) 
            {
                ship.setBounds(((ship.getX()-615)/30)*50+50, ((ship.getY()-350)/30)*50+50, ship.getLength()*50, ship.retrieveHeight()*50);
                ship.scaleImage(50);   
                homeView = true;
            }
        }       
        int x = friendlyBoard.getX(), y = friendlyBoard.getY(), lgth = friendlyBoard.getWidth(), hght = friendlyBoard.getHeight();
        friendlyBoard.setBounds(enemyBoard.getBounds());
        enemyBoard.setBounds(x, y, lgth, hght);
    }
    public static void startGame()
    {    
        int[][] boardVals = new int[10][10];

        if (!isLegalSetup(boardVals, ships)) { JOptionPane.showMessageDialog(gui, "Invalid Ship Positioning\nTry Again"); return; }

        for (int i = 0; i < ships.length; i++)
        {
            ships[i].removeMouseMotionListener(drag);
            ships[i].removeMouseListener(drag);
        }
        friendlyBoard.setBoard(boardVals);
        startButton.setEnabled(false);
        switchViewButton.setEnabled(true);
    }
    public static boolean isLegalSetup(int[][] vals, Ship[] shipArr)//add vertial detection
    {
        for (Ship ship : shipArr)//50 can be replaced by board.length/10 (vice versa)
        {           
            for (int i = 0; i < ship.getLength(); i++) if (!ship.isRotated())
                vals[(ship.getY()/(friendlyBoard.getHeight()/10))-1][((ship.getX()+(i*50))/(friendlyBoard.getWidth()/10))-1] = ship.getLength();  
            
            for (int q = 0; q < ship.retrieveHeight(); q++) if (ship.isRotated())
                vals[((ship.getY()+(q*50))/(friendlyBoard.getHeight()/10))-1][(ship.getX()/(friendlyBoard.getWidth()/10))-1] = ship.retrieveHeight();                                  
        }
                                   
        //sets array of ship valuse ^^^
        //BELOW confirms legality of setup
        int checker = 0;
        for (int i = 0; i < vals.length; i++)
            for (int q = 0; q < vals[i].length; q++)
                if (vals[i][q] > 0) checker++;

                if (shipArr.equals(enemyShips))
                {
                    System.out.println("\f");
                    for (int i = 0; i < vals.length; i++)
                    {
                        for (int q = 0; q < vals[i].length; q++)
                        {
                            placeholder[i][q] = vals[i][q];
                            System.out.print(vals[i][q] + " ");
                        }
                        System.out.println();
                    }
                }
        

        if (checker != 14) return false;
        else return true;
    }
    public static void createEnemyShips()//not dynamic//make it not take array and just set instance variables
    {       
        enemyShips[0] = new Ship(Ship.Type.Carrier, 5, 50, 50, friendlyBoard.getWidth()/10); layeredPane.add(enemyShips[0], 0);
        enemyShips[1] = new Ship(Ship.Type.Battleship, 4, 50, 50, friendlyBoard.getWidth()/10); layeredPane.add(enemyShips[1], 0);        
        enemyShips[2] = new Ship(Ship.Type.Submarine, 3, 50, 50, friendlyBoard.getWidth()/10); layeredPane.add(enemyShips[2], 0);
        enemyShips[3] = new Ship(Ship.Type.PatrolBoat, 2, 50, 50, friendlyBoard.getWidth()/10); layeredPane.add(enemyShips[3], 0);

        int[][] enemyOceanValues = new int[10][10]; 
            for (int i = 0; i < enemyShips.length; i++)
            {
                enemyShips[i].setBounds(((int)((Math.random()*((10-enemyShips[i].getLength()) + 1)))*50)+50,((int)((Math.random()*10)+1))*50,(50*(enemyShips[i].getLength())),50);  
                enemyShips[i].setVisible(false); 
            }
        if (!isLegalSetup(enemyOceanValues, enemyShips))   
        {
            for (int i = 0; i < enemyShips.length; i++)
            {
                layeredPane.remove(enemyShips[i]);
                enemyShips[i] = null;
            }
                
            createEnemyShips();
        }
        enemyBoard.setBoard(placeholder);         
    }
    public static void createShips() // make this work for friendly and enemy array. call this in createEnemyShips()
    {
        ships[0] = new Ship(Ship.Type.Carrier, 5, 50, 100, friendlyBoard.getWidth()/10); layeredPane.add(ships[0], 0);        
        ships[1] = new Ship(Ship.Type.Battleship, 4, 250, 200, friendlyBoard.getWidth()/10); layeredPane.add(ships[1], 0);         
        ships[2] = new Ship(Ship.Type.Submarine, 3, 50, 400, friendlyBoard.getWidth()/10); layeredPane.add(ships[2], 0);  
        ships[3] = new Ship(Ship.Type.PatrolBoat, 2, 300, 500, friendlyBoard.getWidth()/10); layeredPane.add(ships[3], 0);    
        
        drag = new DragListener();
        for (int i = 0; i < ships.length; i++)
        {
            ships[i].addMouseListener(drag);
            ships[i].addMouseMotionListener(drag); 
        }
    }
    public static void createButtonsMenu()
    {
        JPanel menuSquare = new JPanel()
        {
            {
                setBackground(Color.black);
                setBounds(50,550,500,100);
                setVisible(true);             
            }
        };
        layeredPane.add(menuSquare, 5);

        startButton = new JButton("Start")
        {
            {
                setFocusPainted(false);
                setBounds(75,575,100,50);
                //setBackground(new Color(36,141,246));   
                setBackground(new Color(126,73,249));           
            }
        }; 
        //startButton.addActionListener(e -> startGame());//only works at home
        startButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                startGame();
            } 
          } );
        fireButton = new JButton("Fire")
        {
            {
                setBounds(425,575,100,50);
                //setBackground(new Color(36,141,246)); 
                setBackground(new Color(126,73,249));    
                setEnabled(false);   
                setFocusPainted(false);
            }
        };
        switchViewButton = new JButton("Switch View")
        {
            {
                setBounds(213,575,175,50);
                //setBackground(new Color(36,141,200));  
                setFocusPainted(false);
                setBackground(new Color(126,73,249));    
                setEnabled(false);
            }
        };

        switchViewButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                switchView();
            } 
          } );        
        layeredPane.add(startButton,5);
        layeredPane.add(fireButton,5);
        layeredPane.add(switchViewButton,5);
    }
    public static void createBorders()
    {
        JPanel[] borders = new JPanel[13];
        for (int i = 0; i < borders.length; i++)
        {
            borders[i] = new JPanel();
            borders[i].setBackground(new Color(126,73,249));
            layeredPane.add(borders[i],1);
        }
        borders[0].setBounds(42,42,8,616);
        borders[1].setBounds(42,42,516,8);
        borders[2].setBounds(550,42,8,616);
        borders[3].setBounds(42,650,516,8);
        //^^^Main Grid
        borders[4].setBounds(607,42,8,292);
        borders[5].setBounds(607,42,316,8);
        borders[6].setBounds(915,42,8,292);
        borders[7].setBounds(607,326,316,8);
        //^^^Chat Panel 
        borders[8].setBounds(607,342,8,316);
        borders[9].setBounds(607,342,316,8);
        borders[10].setBounds(915,342,8,316);
        borders[11].setBounds(607,650,316,8);
        //^^^preview Grid 
        borders[12].setBounds(607,278,316,8);
        //^^^ diving line between chat display and input
    }
    public static void createChat()
    {
        display = new JTextArea(14,26);
        display.setEditable(false); // set textArea non-editable
        JScrollPane scrollBox = new JScrollPane(display);
        scrollBox.setBounds(615, friendlyBoard.getY(), 300,228);
        scrollBox.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JTextArea input = new JTextArea(14,26);
        input.setBounds(615,friendlyBoard.getY()+scrollBox.getHeight()+8,300,40);

        layeredPane.add(scrollBox,1);
        layeredPane.add(input,1);
    }

    static class DragListener extends MouseInputAdapter  {
        Point location;
        MouseEvent pressed, released;

        public void mousePressed(MouseEvent me)
        {
            Component component = me.getComponent();
            location = component.getLocation(location);
            pressed = me;
            if (SwingUtilities.isRightMouseButton(me))
            {
                component = me.getComponent();
                for (int i = 0; i < ships.length; i++)
                {
                    if (component.equals(ships[i])) ships[i].rotate();                
                }          
            location = component.getLocation(location);
            int x = location.x - pressed.getX() + me.getX();
            int y = (location.y - pressed.getY() + me.getY());
            snapShip(x, y, component);
            }      
        }
        public void mouseReleased(MouseEvent me)
        {        
            if (!SwingUtilities.isLeftMouseButton(me)) return;
            Component component = me.getComponent();
            int x = location.x - pressed.getX() + me.getX();
            int y = (location.y - pressed.getY() + me.getY());
            snapShip(x, y, component);
        }
        public void snapShip(int x, int y, Component component)
        {
            location = component.getLocation(location);//comment-out-able i think
            x = 50 * ((int)(x / 50));
            y = 50 * ((int)(y / 50));
            if (x < 50) x = 50;
            else if (x + component.getWidth() > 550) x = 550 - component.getWidth();//not dynamic
            if (y < 50) y = 50;
            else if (y + component.getHeight()> 550) y = 550 - component.getHeight();//not dynamic
            component.setLocation(x, y);
        }
        public void mouseDragged(MouseEvent me)
        {
            if (!SwingUtilities.isLeftMouseButton(me)) return; 
            Component component = me.getComponent();
            (component.getParent()).setComponentZOrder(component,0);
            location = component.getLocation(location);
            int x = location.x - pressed.getX() + me.getX();
            int y = (location.y - pressed.getY() + me.getY());       
            component.setLocation(x, y);
        }
    }
}