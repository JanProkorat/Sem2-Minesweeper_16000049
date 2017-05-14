
package App;



/**
 * Class creating new object in type player for Minesweeper game
 * @author Jan Prokorát
 */
public class Player implements Comparable<Player>{
    private String name;
    private final String time;


    public Player(String name, String time){
        this.name = name;
        this.time = time;
    }
    public String getName(){
        return name;
    }
    public String getTime(){
        return time;
    }
    public void setName(String s){
        name = s;
    }
    @Override
    public String toString() {
        return "Player{" + "name=" + name + ", time=" + time + '}';
    }

    /**
     * Compares and sorts players in observable list by time 
     * @param p player to compare
     * @return a negative integer, zero, or a positive integer as this player is 
     * less than, equal to, or greater than the specified player.
     */
    @Override
    public int compareTo(Player p) {
        String[] tmp = this.time.split(":");
        String number = tmp[0] + tmp[1];
        String[] tmp2 = p.time.split(":");
        String number2 = tmp2[0] + tmp2[1];
        return Integer.parseInt(number) < Integer.parseInt(number2) ? -1 : 
                Integer.parseInt(number) > Integer.parseInt(number2) ? 1 : 0;
    }
    
    /**
     * Compares and sorts players in observable list by name
     * @param p player to compare
     * @return a negative integer, zero, or a positive integer as this player is 
     * less than, equal to, or greater than the specified player.
     */
    public int compareTo2(Player p) {
        return Integer.parseInt(this.name) < Integer.parseInt(p.name) ? -1 : 
                Integer.parseInt(this.name) > Integer.parseInt(p.name) ? 1 : 0;
    }
    
}
