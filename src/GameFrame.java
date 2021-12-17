import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class GameFrame extends JFrame {
    CheckerBoard checkerboard;

    boolean isBlack;
    List<chess> chess_list = new LinkedList<>();
    Stack<Log> logs = new Stack<>();
    BufferedImage buffer_black, buffer_white;

    Image black, white;
    Cursor cr_black, cr_white;
    mouse[][] click = new mouse[8][8];
    String[][] chess_map = new String[8][8];
    square[][] squares = new square[8][8];
    JMenuBar menuBar;
    JMenu game;
    JMenuItem restart,regret,output_logs;
    int up, down, right, left, left_up, right_down, left_down, right_up;

    public GameFrame() {
        super("黑白棋");
        setSize(650, 660);

        checkerboard = new CheckerBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new square(i,j);
                click[i][j] = new mouse(squares[i][j]);
                chess_map[i][j] = "n";
                checkerboard.add(squares[i][j]);
            }
        }
        add(checkerboard);

        menuBar = new JMenuBar();
        game = new JMenu("Option");
        output_logs = new JMenuItem("輸出棋步");
        output_logs.addActionListener(e -> {
            Output_Logs(logs);
        });
        regret = new JMenuItem("悔棋");
        regret.addActionListener(e -> {
            Regret();
        });
        restart = new JMenuItem("重新開始");
        restart.addActionListener(e -> {
            this.dispose();
            new GameFrame();
        });

        game.add(restart);
        game.add(regret);
        game.add(output_logs);
        menuBar.add(game);
        menuBar.setVisible(true);
        setJMenuBar(menuBar);

        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        try {
            buffer_black = ImageIO.read(new File("black.png"));
            buffer_white = ImageIO.read(new File("white.png"));
            black = buffer_black.getScaledInstance(75, -1, Image.SCALE_AREA_AVERAGING);
            white = buffer_white.getScaledInstance(75, -1, Image.SCALE_AREA_AVERAGING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
        
        cr_black = Toolkit.getDefaultToolkit().createCustomCursor(black, new Point(0, 0), "Black");
        cr_white = Toolkit.getDefaultToolkit().createCustomCursor(white, new Point(0, 0), "White");
        set_cursor();
    }

    public void init() {
        isBlack = true;
        chess_list.clear();
        logs.clear();
        chess_map[3][4] = "B";
        squares[3][4].setImg(black);
        chess_list.add(new chess(3, 4, Color.BLACK));
        chess_map[3][3] = "W";
        squares[3][3].setImg(white);
        chess_list.add(new chess(3, 3, Color.WHITE));
        chess_map[4][3] = "B";
        squares[4][3].setImg(black);
        chess_list.add(new chess(4, 3, Color.BLACK));
        chess_map[4][4] = "W";
        squares[4][4].setImg(white);
        chess_list.add(new chess(4, 4, Color.WHITE));

    }

    class mouse extends MouseAdapter {
        square square;
        public mouse(square s) {
            square = s;
            s.addMouseListener(this);
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isLegal(isBlack, square.x, square.y)) {
                chang_color(isBlack,square.x, square.y);
                switch (check_next()) {
                    case "both":
                        isBlack = !isBlack;
                        break;
                    case "B":
                        isBlack = true;
                        break;
                    case "W":
                        isBlack = false;
                        break;
                    case "No":
                        get_result();
                        break;
                }
                set_cursor();
            }
        }
    }

    public void get_result() {
        int B = 0, W = 0;
        for (chess c : chess_list) {
            if (c.color == Color.BLACK) {
                B += 1;
            } else if (c.color == Color.WHITE) {
                W += 1;
            }
        }

        String message = new String();
        if (B == W) {
            message = ("黑棋:" + B + "\n" + "白棋:" + W + "\n" + "雙方平手");
        } else if (B > W) {
            message = ("黑棋:" + B + "\n" + "白棋:" + W + "\n" + "黑棋勝");
        } else if (W > B) {
            message = ("黑棋:" + B + "\n" + "白棋:" + W + "\n" + "白棋勝");
        }

        JOptionPane.showMessageDialog(this,message,"Result",JOptionPane.INFORMATION_MESSAGE);
    }

    public void chang_color(Boolean is_black,int x, int y) {
        Image self, opponent;
        Color c_self, c_opponent;
        self = is_black ? black : white;
        opponent = is_black ? white : black;
        c_self = is_black ? Color.BLACK : Color.WHITE;
        c_opponent = is_black ? Color.WHITE : Color.BLACK;
        List<chess> will_changed = new LinkedList<>();
        chess_list.add(new chess(x, y, c_self));

        int ns = x + y > 7 ? y - Math.abs(8 - x - y - 1) : y;

/*
        System.out.println("up : "+(x-up-1));         //up多1
        System.out.println("down : "+(down-x-1));     //down多1
        System.out.println("left : "+(y-left-1));     //left多1
        System.out.println("right : "+(right-y-1));   //right多1
        System.out.println("left_up : "+(Math.min(x, y) - left_up-1));   //left_up多1
        System.out.println("right_down : "+(right_down - Math.min(x, y)-1)); //right_down多1
        System.out.println("left_down : "+(ns - left_down-1));   //right_down多1
        System.out.println("right_up : "+(right_up - ns-1));     //right_down多1
        System.out.println("=====================================================================");
 */

        if ((x-up-1)>0){
            for (int i = 1; i < (x - up); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x - i) && (c.y == y)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+"become" + self);
                    }
                }
            }
        }

        if ((down-x-1)>0){
            for (int i = 1; i < (down - x); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x + i) && (c.y == y)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+" become" + self);
                    }
                }
            }
        }

        if ((y-left-1)>0){
            for (int i = 1; i < (y - left); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x) && (c.y == y - i)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+"become" + self);
                    }
                }
            }
        }

        if ((right-y-1)>0){
            for (int i = 1; i < (right - y); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x) && (c.y == y + i)) {
                        c.color = c_self;
                        //System.out.println(c.x+","+c.y+"become" + self);
                        will_changed.add(c);
                    }
                }
            }
        }

        if ((Math.min(x, y) - left_up-1)>0){
            for (int i = 1; i < (Math.min(x, y) - left_up); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x - i) && (c.y == y - i)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+"become" + self);
                    }
                }
            }
        }

        if ((right_down - Math.min(x, y)-1)>0) {
            for (int i = 1; i < (right_down - Math.min(x, y)); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x + i) && (c.y == y + i)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+"become" + self);
                    }
                }
            }
        }

        if ((ns - left_down-1)>0){
            for (int i = 1; i < (ns - left_down); i++) {
                for (chess c : chess_list) {
                    if ((c.x == x + i) && (c.y == y - i)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+"become" + self);
                    }
                }
            }
        }

        if ((right_up - ns-1)>0){
            for (int i = 1; i < (right_up - ns); i++) {
                System.out.println((x - i) + "," + (y + i));
                for (chess c : chess_list) {
                    if ((c.x == x - i) && (c.y == y + i)) {
                        c.color = c_self;
                        will_changed.add(c);
                        //System.out.println(c.x+","+c.y+"become" + self);
                    }
                }
            }
        }

        Log log = new Log(is_black,new chess(x, y, c_self),will_changed);
        //String str = (log.toString());
        logs.push(log);

        for (chess c : chess_list) {
            //squares[c.x][c.y].setBackground(c.color);
            if (c.color == Color.BLACK) {
                chess_map[c.x][c.y] = "B";
                squares[c.x][c.y].setImg(black);
            } else if (c.color == Color.WHITE) {
                chess_map[c.x][c.y] = "W";
                squares[c.x][c.y].setImg(white);
            }
        }

/*
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(chess_map[i][j]);
            }
            System.out.println("");
        }

 */
    }

    public int get_Array_up(String[] array, int c) {
        for (int i = 1; i < array.length - c; i++) {
            if (array[c + i].equals("")) {
                break;
            } else if (array[c + i].equals("s")) {
                c = c + i;
                break;
            } else if (array[c + i].equals("o")) {
                continue;
            }
        }
        return c;
    }

    public int get_Array_down(String[] array, int c) {
        for (int i = 1; i < c + 1; i++) {
            if (array[c - i].equals("")) {
                break;
            } else if (array[c - i].equals("s")) {
                c = c - i;
                break;
            } else if (array[c - i].equals("o")) {
                continue;
            }
        }
        return c;
    }
    //垂直
    public boolean can_vertical(Boolean is_black, int x, int y) {
        String[] v = {"", "", "", "", "", "", "", ""};
        Color self;
        self = is_black ? Color.BLACK : Color.WHITE;
        for (chess c : chess_list) {
            if (c.y == y) {
                v[c.x] = c.color == self ? "s" : "o";
            }
        }
        v[x] = "me";
        down = get_Array_up(v, x);
        up = get_Array_down(v, x);
        return (x - up >= 2) || (down - x >= 2) ? true : false;

    }
    //水平
    public boolean can_horizontal(Boolean is_black, int x, int y) {
        String[] h = {"", "", "", "", "", "", "", ""};
        Color self, opponent;

        self = is_black ? Color.BLACK : Color.WHITE;

        for (chess c : chess_list) {
            if (c.x == x) {
                h[c.y] = c.color == self ? "s" : "o";
            }
        }


        right = get_Array_up(h, y);

        left = get_Array_down(h, y);

        return (y - left >= 2) || (right - y >= 2) ? true : false;
    }
    //斜線
    public boolean can_slash(Boolean is_black, int x, int y) {
        String[] p_s, n_s;
        int p_s_size, n_s_size;

        p_s_size = 8 - Math.abs(x - y);
        n_s_size = x + y > 7 ? 15 - x - y : x + y + 1;
        /*
        n_s_size = x + y + 1;
        if (n_s_size > 8) {
            n_s_size = 8 + (8 - n_s_size);
        }

         */

        //System.out.println("斜率為1的欄位 : "+p_s_size);
        //System.out.println("斜率為-1的欄位 : "+n_s_size);
        p_s = new String[p_s_size];
        n_s = new String[n_s_size];

        for (int i = 0; i < p_s.length; i++) {
            p_s[i] = "n";
        }
        for (int i = 0; i < n_s.length; i++) {
            n_s[i] = "n";
        }

        Color self, opponent;
        self = is_black ? Color.BLACK : Color.WHITE;
        /*
        if (is_black) {
            self = Color.BLACK;
            opponent = Color.WHITE;
        } else {
            self = Color.WHITE;
            opponent = Color.BLACK;
        }

         */

        p_s[Math.min(x, y)] = "m";
        int me;
        me = x + y >= 8 ? y - Math.abs(8 - n_s_size) : y;
        /*
        if (x + y >= 8) {
            me = y - Math.abs(8 - n_s_size);
        } else {
            me = y;
        }

         */
        n_s[me] = "m";

        for (chess c : chess_list) {
            if (Math.abs(x - c.x) == Math.abs(y - c.y)) {
                //System.out.println(c.toString()+"在對角線");
                //System.out.print(c.x+","+c.y+" : ");
                switch ((x - c.x) / (y - c.y)) {
                    case 1:
                        int location1;
                        location1 = Math.min(c.x, c.y);
                        if (c.color == self) {
                            p_s[location1] = "s";
                        } else {
                            p_s[location1] = "o";
                        }
                        //System.out.println(p_s[location1]);
                        break;
                    case -1:
                        int location2;
                        if (c.x + c.y >= 8) {
                            location2 = c.y - Math.abs(8 - n_s_size);
                        } else {
                            location2 = c.y;
                        }

                        if (c.color == self) {
                            n_s[location2] = "s";
                        } else {
                            n_s[location2] = "o";
                        }
                        //System.out.println(n_s[location2]);
                        break;
                }
            }
        }

        String ps = "", ns = "";
        for (int i = 0; i < p_s.length; i++) {
            ps += p_s[i];
        }
        for (int i = 0; i < n_s.length; i++) {
            ns += n_s[i];
        }

        //System.out.println("正斜線 : " + ps);


        left_up = Math.min(x, y);
        for (int i = 1; i < left_up + 1; i++) {


            if (p_s[left_up - i].equals("n")) {

                break;
            } else if (p_s[left_up - i].equals("s")) {
                left_up = left_up - i;
                break;
            } else if (p_s[left_up - i].equals("o")) {
                continue;
            }
        }

        right_down = Math.min(x, y);
        for (int i = 1; i < p_s.length - right_down; i++) {
            if (p_s[right_down + i].equals("n")) {
                break;
            } else if (p_s[right_down + i].equals("s")) {
                right_down = right_down + i;
                break;
            } else if (p_s[right_down + i].equals("o")) {
                continue;
            }
        }

        if (x + y >= 8) {
            left_down = y - Math.abs(8 - n_s_size);
            right_up = y - Math.abs(8 - n_s_size);
        }
        else {
            left_down = y;
            right_up = y;
        }

        for (int i = 1; i < left_down + 1; i++) {
            if (n_s[left_down - i].equals("n")) {
                break;
            } else if (n_s[left_down - i].equals("s")) {
                left_down = left_down - i;
                break;
            } else if (n_s[left_down - i].equals("o")) {
                continue;
            }
        }
        for (int i = 1; i < n_s.length - right_up; i++) {
            if (n_s[right_up + i].equals("n")) {
                break;
            } else if (n_s[right_up + i].equals("s")) {
                right_up = right_up + i;
                break;
            } else if (n_s[right_up + i].equals("o")) {
                continue;
            }
        }

        //System.out.println("負斜線 : " + ns);

        if ((Math.min(x, y) - left_up >= 2) || (right_down - Math.min(x, y) >= 2) || (me - left_down >= 2) || (right_up - me >= 2)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isLegal(Boolean is_black, int x, int y) {
        /*
        List<chess> black = new LinkedList<>();
        List<chess> white = new LinkedList<>();
        for (chess c : chesses){
            if (c.color == Color.BLACK){
                black.add(c);
            }
            else{
                white.add(c);
            }
        }
         */
        for (chess c : chess_list) {
            if (c.x == x && c.y == y) {
                return false;
            } else {
                continue;
            }
        }

        boolean v = can_vertical(is_black, x, y);
        //System.out.println("can_vertical : "+v);
        boolean h = can_horizontal(is_black, x, y);
        //System.out.println("can_horizontal : "+h);
        boolean s = can_slash(is_black, x, y);
        //System.out.println("can_slash : "+s);
        return (s || h || v);

        //return (can_slash(x,y)||can_vertical(x,y)||can_horizontal(x,y));      有問題
    }

    public void set_cursor() {
        Cursor cr;
        String color;
        cr = isBlack ? cr_black : cr_white;
        color = isBlack ? "黑棋下" : "白棋下";
        setCursor(cr);
        setTitle("黑白棋-"+color);
    }

    public String check_next() {
        boolean black, white;
        black = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chess_map[i][j] == "n" && isLegal(true, i, j)) {
                    black = true;
                    break;
                } else {
                    continue;
                }
            }
        }
        //System.out.println("Black:" + black);
        white = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chess_map[i][j] == "n" && isLegal(false, i, j)) {
                    white = true;
                    break;
                } else {
                    continue;
                }
            }
        }
        //System.out.println("White:" + white);

        if (black && white) {
            return "both";
        } else if (black) {
            return "B";
        } else if (white) {
            return "W";
        } else {
            return "No";
        }
    }

    public void Regret(){
        if (logs.empty()){
            JOptionPane.showMessageDialog(this,"沒東西給你悔啦~~","Regret_Warning",JOptionPane.WARNING_MESSAGE);
        }
        else {
            Log  log = logs.pop();
            Boolean b = log.black;
            chess chess = log.chess;
            List<chess> changed = log.changed;

            Color c_oppo = b ? Color.WHITE : Color.BLACK;
            Image oppo = b ? white : black;

            for (chess c : chess_list){
                if ((c.x == chess.x)&&(c.y==chess.y)){
                    chess_list.remove(c);
                    squares[c.x][c.y].setImg(null);
                    break;
                }
            }

            for (chess change : changed){
                for (chess c : chess_list){
                    if ((c.x == change.x)&&(c.y==change.y)){
                        c.color = c_oppo;
                        squares[c.x][c.y].setImg(oppo);
                        continue;
                    }
                }
            }
            isBlack = b;
            set_cursor();
        }
    }

    public void Output_Logs(Stack<Log> Ls){
        Stack<Log> Logs =  Ls;
        Stack<Log> print_logs = new Stack<>();

        try {

            File output_file = new File("LastGame.txt");
            if (!output_file.exists()) {
                output_file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(output_file));

            while (!Logs.empty()){
                Log l = Logs.pop();
                print_logs.push(l);
            }
            int i = 0;
            while (!print_logs.empty()){
                Log l = print_logs.pop();
                i += 1;
                //out.write("第"+i+"步 : "+l.toString()+"\r\n");
                System.out.println("第"+i+"步 : "+l.toString());
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
