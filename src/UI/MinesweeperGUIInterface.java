package UI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javafx.stage.Stage;

/**
 * Interface for the minesweeper game, contains all the most important 
 * methods, nesessary for running the game
 * @author Jan Prokorát
 */
public interface MinesweeperGUIInterface {
    
   void gameOverWindow(Stage stage2) throws IOException;
   void writeToFile() throws UnsupportedEncodingException ;
   void loadFile() throws FileNotFoundException, IOException;
   void countTime();
}
