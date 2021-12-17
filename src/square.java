import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class square extends JPanel {
    Color color;
    int x,y;
    Container parent;
    Image img;

    public square(int x,int y){
        parent = getParent();
        Border blackline = BorderFactory.createLineBorder(Color.black);
        setBorder(blackline);
        setBackground(Color.GREEN);
        this.x =x ;
        this.y=y;
    }

    public void setImg(Image img) {
        this.img = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img,0,0,Color.GREEN,this);
    }
}
