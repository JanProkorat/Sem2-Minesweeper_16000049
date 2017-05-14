package UI;

import App.Player;
import App.Game;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.NANOS;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * Main class and GUI for the Minesweeper game
 *
 * @author Jan Prokorát
 * @version 1.0 13.05.2017
 */
public class MinesweeperGUI extends Application implements MinesweeperGUIInterface{

    private Game g;
    private BorderPane bp, bp2, bp3;
    private GridPane game;
    private Button b, newgame;
    private HBox h, h2, h3, h4;
    private VBox difficulty, v;
    private TextField tf, name;
    private static PrintWriter vysledky = null;
    private static File score = null;
    private static final ObservableList<Player> statistics = FXCollections.observableArrayList();
    private static ListView playersList;

    private final DateFormat TIMEFORMAT = new SimpleDateFormat("hh:mm:ss");
    private final LocalTime STARTTIME = LocalTime.parse(TIMEFORMAT.format(System.currentTimeMillis()));
    private LocalTime finishTime = null;
    private final DateTimeFormatter DTFFINISHTIME = DateTimeFormatter.ofPattern("mm:ss");
    private final DateTimeFormatter DTFSTARTTIME = DateTimeFormatter.ofPattern("mm:ss");
    private Timeline timeline;

    /**
     * Main window of the game
     * @param stage gamefield
     */
    @Override
    public void start(Stage stage) {
        g = new Game(10, 10, 10);
        stage.setTitle("Minesweeper");
        bp = new BorderPane();
        Scene scene = new Scene(bp);
        // adding horizontal box for upper new game button with emoji
        h = new HBox();
        h.setAlignment(Pos.TOP_CENTER);
        newgame = new Button();
        newgame.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("smajlik.jpg"))));
        //action for the new game button
        newgame.setOnAction((ActionEvent t) -> {
            stage.close();
            timeline.stop();
            Platform.runLater(() -> start(new Stage()));
        });
        countTime();
        h.getChildren().addAll(newgame);
        bp.setTop(h);
        //grid with the game in the center of the scene
        game = new GridPane();
        game.setGridLinesVisible(true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                //adding buttons to the gamefield
                b = new Button();
                g.addButton(b, i, j);
                b.setPrefHeight(47);
                b.setPrefWidth(47);
                b.setStyle("-fx-font: 16 arial; -fx-base: #b6e7c9;");
                b.setAlignment(Pos.CENTER);
                game.add(b, i, j);
                final int ii = i;
                final int jj = j;
                //adding action after click on the button
                b.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t) -> {
                    if (t.getButton() == MouseButton.PRIMARY) {
                        g.checkImage(ii, jj);
                        if (g.f[ii][jj].getOpened() == true && g.f[ii][jj].getMine() == true) {
                            playAudio();
                            newgame.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("smajliksmutny.jpg"))));
                            try {
                                timeline.pause();
                                gameOverWindow(stage);
                            } catch (IOException ex) {
                                Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (g.getWin() == true) {
                            playAudio();
                            try {
                                timeline.pause();
                                gameOverWindow(stage);
                            } catch (IOException ex) {
                                Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    if (t.getButton() == MouseButton.SECONDARY) {
                        g.clickMouseRight(ii, jj);
                    }
                });
            }
        }
        bp.setCenter(game);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Second windows generated, when the game is over, show statistics and
     * win/loose label
     * @param stage2 stage for the game over window 
     * @throws IOException if stream to File cannot be written to or closed
     */
    @Override
    public void gameOverWindow(Stage stage2) throws IOException {
        statistics.remove(0, statistics.size());
        Stage stage = new Stage();
        stage.setTitle("Game Over");
        bp2 = new BorderPane();
        Scene scene = new Scene(bp2);
        /*adding Box with win/loose label and possibly TextField for the player's
          name, if he wins*/
        h2 = new HBox();
        Label GameOver;
        if (g.getWin() == true) {
            GameOver = new Label("You Win!"); 
            h3 = new HBox();
            Label l = new Label("Enter your name: ");
            name = new TextField();
            writeToFile();
            h3.getChildren().addAll(l, name);
            bp2.setCenter(h3);
        }else{
            GameOver = new Label("You Lose!");
        }
        h2.getChildren().addAll(GameOver);
        h2.setAlignment(Pos.TOP_CENTER);
        GameOver.setStyle("-fx-font: 20 verdana;");
        //adding vertical box with the listview and newgame/quit button
        v = new VBox();
        Label best = new Label("Best Results");
        best.setStyle("-fx-font: 20 verdana;");
        playersList = new ListView(statistics);
        loadFile();
        formatTextInListView();
        h4 = new HBox();
        //adding action to the newgame button
        Button nw = new Button("New Game");
        nw.setOnAction((ActionEvent t) -> {
            stage.close();
            stage2.close();
            timeline.stop();
            Platform.runLater(() -> start(new Stage()));
        });
        //adding action to the quit button
        Button quit = new Button("Quit");
        quit.setOnAction((ActionEvent t) ->{
            Platform.exit();
        });
        h4.setAlignment(Pos.TOP_CENTER);
        h4.getChildren().addAll(nw,quit);
        v.setAlignment(Pos.TOP_CENTER);
        v.getChildren().addAll(best, playersList,h4);
        bp2.setTop(h2);
        bp2.setBottom(v);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method, which creats new File and save statisics about the Player into
     * .TXT file. Method saves name and gaming time.
     * @throws UnsupportedEncodingException 
     */
    @Override
    public void writeToFile() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(System.out, true, "utf-8"));
        name.setOnAction((ActionEvent e) -> {
            score = new File("Score.txt");
            try {
                if(!name.getText().matches("[A-Z][a-z]+")){
                    name.setText("Nevalidní jméno");
                }else{
                    Player p = new Player(name.getText(), getGamingTime());
                    statistics.add(p);
                    vysledky = new PrintWriter(new BufferedWriter(new FileWriter(score, true)));
                    vysledky.println(p.getName() + "            " + p.getTime());
                }
            } catch (IOException ex) {
                Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (vysledky != null) {
                    vysledky.close();
                }
            }
            playersList.setItems(FXCollections.observableArrayList(statistics));
        });
    }

    /**
     * Method to load statistics from .TXT file to listview.
     * @throws FileNotFoundException if File does not exist.
     * @throws IOException if stream to File cannot be written to or closed
     */
    @Override
    public void loadFile() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("Score.txt"));
        String line = br.readLine();
        while (line != null) {
            statistics.add(new Player(line.substring(0, 8), line.substring(line.length() - 5, line.length())));
            Collections.sort(statistics);
            line = br.readLine();
        }
        playersList.setItems(FXCollections.observableArrayList(statistics));

    }
    /**
     * counts time of playing
     */
    @Override
    public void countTime() {
        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            long gametime = System.currentTimeMillis();
            finishTime = LocalTime.parse(TIMEFORMAT.format(gametime));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    /**
     * Method to calculate gaming time of the player
     *
     * @return gaming time
     */
    private LocalTime gamingTime() {
        if (finishTime != null) {
            return LocalTime.ofNanoOfDay(STARTTIME.until(finishTime, NANOS));
        }
        return null;
    }

    /**
     * Method to convert gaming time to string
     *
     * @return
     */
    private String getGamingTime() {
        if (gamingTime() != null) {
            return gamingTime().format(DTFFINISHTIME);
        }
        return ".";
    }

    /**
     * Method to formate text in listview as string
     */
    private void formatTextInListView() {
        playersList.setCellFactory(new Callback<ListView<Player>, ListCell<Player>>() {
            @Override
            public ListCell<Player> call(ListView<Player> param) {
                TextFieldListCell<Player> cell = new TextFieldListCell<>();
                StringConverter<Player> converter = new StringConverter<Player>() {
                    @Override
                    public String toString(Player p) {
                        return p.getName() + "                                  " + p.getTime();
                    }

                    @Override
                    public Player fromString(String s) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                };
                cell.setConverter(converter);
                return cell;
            }
        });
    }
    
    /**
     * Plays song at the and of the game
     */
    private void playAudio(){
        Media m;
        MediaPlayer mp;
        File f;
        if(g.getWin() == true){
            f = new File("applause.mp3");
            m = new Media(f.toURI().toString());
            mp = new MediaPlayer(m);
            mp.play();
        }else{
            f = new File("laugh.wav");
            m = new Media(f.toURI().toString());
            mp = new MediaPlayer(m);
            mp.play();
        }
    }
    
    /**
     * Main method for the minesweeper game
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

}
