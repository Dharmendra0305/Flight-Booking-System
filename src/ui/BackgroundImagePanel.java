package ui;

import javax.swing.*;
import java.awt.*;

public class BackgroundImagePanel extends JPanel
{
    private final Image bgImage;
    public BackgroundImagePanel(String imagePath)
    {
        bgImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        setLayout(new BorderLayout());
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
