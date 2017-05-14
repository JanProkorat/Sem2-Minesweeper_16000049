package App;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Main logic class for minesweeper game, creats object in type game and big 
 * gamefield, which contains all fields in game
 * @author Jan Prokorát
 */
public class Game {

    public Field[][] f;
    private boolean win;
    private int numberofcells;
    private final int NUMOFMINES;
    
    /**
     * Constructor of the type game
     * @param w width of the gamefield
     * @param h height of the gamefield
     * @param m number of mines in minesweeper game
     */
    public Game(int w, int h, int m) {
        f = new Field[w][h];
        int i;
        int j;
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                f[i][j] = new Field();
            }
        }
        for (i = 0; i < m; i++) {
            int x = (int) (Math.random() * w);
            int y = (int) (Math.random() * h);
            addMine(x, y);
        }
        win = false;
        numberofcells = w * h;
        NUMOFMINES = m;
    }
    
    /**
     * Adds mines to game 
     * @param x coordinate in gamefield
     * @param y coordinate in gamefield
     */
    private final void addMine(int x, int y) {
        f[x][y].setMine(true);
        if (f[x][y].mine == true) {
            f[x][y].setID(-1);
        }
        if (x - 1 >= 0 && y - 1 >= 0) {
            f[x - 1][y - 1].ID++;                   // levý horní roh
        }
        if (y - 1 >= 0) {
            f[x][y - 1].ID++;			// levý prostřední
        }
        if (x + 1 < f[0].length && y - 1 >= 0) {
            f[x + 1][y - 1].ID++;                   // levý dolní roh
        }
        if (x - 1 >= 0) {
            f[x - 1][y].ID++;			// horní prost�ední	
        }
        if (x - 1 >= 0 && y + 1 < f[0].length) {
            f[x - 1][y + 1].ID++;                   // pravý horní roh
        }
        if (x + 1 < f[0].length) {
            f[x + 1][y].ID++; 			// pravý prostřední
        }
        if (x + 1 < f[0].length && y + 1 < f[0].length) {
            f[x + 1][y + 1].ID++;			// dolní pravý roh
        }
        if (y + 1 < f[0].length) {
            f[x][y + 1].ID++;			// dolní prostřední
        }

    }
    
    /**
     * Method for displaying gamefield in the console, if it's needed
     * @param w width of the gamefield
     * @param h height of the gamefield
     */
    public void display(int w, int h) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (f[i][j].mine) {
                    System.out.print("M");
                } else {
                    System.out.print(f[i][j].ID);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Adds a button to every cell in game
     * @param b button
     * @param x coordinate in gamefield
     * @param y coordinate in gamefield
     */
    public void addButton(Button b, int x, int y) {
        f[x][y].setButton(b);
    }
    
    /**
     * Method to check,if the clicked button contains a flag, if so, 
     * method deletes that image and runs the clickedMouseLeft method, if not,
     * clickedMouseLeft method runs directly 
     * @param x coordinate in gamefield
     * @param y coordinate in gamefield
     */
    public void checkImage(int x, int y) {
        if (f[x][y].image == false) {
            clickMouseLeft(x, y);
        } else {
            f[x][y].button.setGraphic(null);
            clickMouseLeft(x, y);
        }
    }
    
    /**
     * Method to change opened value in field with this x and y coordinates
     * and shows, if field contains mine, if not, shows ID 
     * @param x coordinate in gamefield
     * @param y coordinate in gamefield
     */
    public void clickMouseLeft(int x, int y) {
        if (x < 0 || x >= f.length) {
            return;
        }
        if (y < 0 || y >= f[0].length) {
            return;
        }
        if (f[x][y].opened == false) {
            f[x][y].setOpened(true);
            numberofcells--;
            if (f[x][y].ID == 0 && f[x][y].mine == false) {
                clickMouseLeft(x - 1, y - 1);
                clickMouseLeft(x, y - 1);
                clickMouseLeft(x + 1, y - 1);
                clickMouseLeft(x - 1, y);
                clickMouseLeft(x - 1, y + 1);
                clickMouseLeft(x + 1, y);
                clickMouseLeft(x + 1, y + 1);
                clickMouseLeft(x, y + 1);
            }
        }
        if (f[x][y].mine == true) {
            f[x][y].button.setStyle("-fx-base:red");
            endGame();
        }
        if(numberofcells == NUMOFMINES && f[x][y].mine == false){
            win = true;
            endGame();
        }
        
    }
    
    /**
     * Method to mark field with flag, when the player has suspicion, that that 
     * specific field contains a mine
     * @param x coordinate in gamefield
     * @param y coordinate in gamefield
     */
    public void clickMouseRight(int x, int y) {
        Image vlajka = new Image(getClass().getResourceAsStream("vlajka.jpg"));
        f[x][y].button.setGraphic(new ImageView(vlajka));
        f[x][y].image = true;
    }
    
    
    /**
     * Disables all fields in game, if the player clickes on the mine.
     * Also reveals, in which cells the mines were.
     */
    public void endGame() {
        for (Field[] f1 : f) {
            for (int j = 0; j < f.length; j++) {
                f1[j].setOpened(true);
            }
        }

    }
    public boolean getWin(){
        return win;
    }


}
