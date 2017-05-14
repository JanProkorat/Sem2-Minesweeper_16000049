package App;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class, that creats object of type field, which contains informations about 
 * every small cell in gamefield
 * @author Jan Prokorát
 */
public class Field {

    protected boolean opened;
    protected boolean mine;
    protected int ID;
    protected Button button;
    protected boolean image;
    
    
    public Field() {
        opened = false;
        mine = false;
        ID = 0;
        image = false;
    }
    public boolean getMine(){
        return mine;
    }
    public void setMine(boolean b) {
        mine = b;
    }
    public void setID(int i) {
        ID = i;
    }
    public boolean getOpened(){
        return opened;
    }
    public void setOpened(boolean z) {
        opened = z;
        if (mine == true) {
            Image mina = new Image(getClass().getResourceAsStream("mina.jpg"));
            button.setGraphic(new ImageView(mina));
        }
        if (mine != true && ID != 0) {
            button.setText(String.valueOf(ID));
        }
        button.setDisable(true);
    }
    public void setButton(Button b) {
        button = b;
    }
}
