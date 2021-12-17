import java.util.LinkedList;
import java.util.List;

public class Log {
    chess chess;
    Boolean black;
    List<chess> changed = new LinkedList<>();
    String[] y = {"a","b","c","d","e","f","g","h"};
    public Log(Boolean b,chess c,List<chess> be_changed){
        this.chess = c;
        this.black = b;
        this.changed = be_changed;
    }
    public String toString(){
        String color = black ? "B" : "W";
        return color + " : (" + (chess.x+1) + "," + y[chess.y] + ")";
    }
}
