import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.*;



class Screen extends JPanel{
    //setup emulated screen
    private Graphics g;
    private int scale = 3; //10 pixels for each emulated-system pixel.
    private int width = 224 * scale;
    private int height = 256 * scale;
    //Random random = new Random();
    //long z = 0;
    //setup audio variables
    //AudioFormat audioFormat;
    //AudioInputStream audioInputStream;


    //used to check if screen was set up
    public Screen() {
        System.out.println("Screen initialized");
    }
    //set screen size/resolution
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }


    /**
     * Paints a emulated-system pixel.
     */
    //checks if it should be a different color based on location
    //(the game is black and while, but the arcade version had a colored overlay)
    public void paintPixel(boolean white, int x, int y) {
        if (white) {
            if (y>32&y<=64){g.setColor(Color.RED);}
            else if(y > 184 && y <= 240 && x >= 0 && x <= 223){g.setColor(Color.GREEN);}
            else if (y > 238 & y <= 256 & x >= 16 && x < 132){g.setColor(Color.GREEN);}
            else{g.setColor(Color.WHITE);}
        } else {
            g.setColor(Color.BLACK);
        }

        g.fillRect(x * scale, y * scale, scale, scale);

    }

    /**
     * Paints full screen from screen memory.
     */
    private void paintFullScreen() {

        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z<8;z++){
                    boolean value = (Main.i8080.memory[(0x2400+32*y+x)]>>z)%2==1;
                    paintPixel(value,y , 256-(x*8+z));
                }
            }
        }
    }


    /**
     * Paints full screen from screen memory. Public.
     */
    public void paintScreen() {
        repaint();
    }


    /**
     * Paints the component. It has to be called through paintScreen().
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;

        // Draw background
        g.setColor(Color.GRAY);

        g.fillRect(0, 0, width, height);

        paintFullScreen();


    }
    //audio player
    //synchronized as to not freeze screen while playing audio
    public static synchronized void playAudio(final String url) {
        new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments
            public void run() {
                try {
                    //gets audio file and starts playing
                    BufferedInputStream myStream = new BufferedInputStream(getClass().getResourceAsStream(url));
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audio2 = AudioSystem.getAudioInputStream(myStream);
                    clip.open(audio2);
                    clip.start();
                    clip.addLineListener( new LineListener() {
                        public void update(LineEvent evt) {
                            if (evt.getType() == LineEvent.Type.STOP) {
                                evt.getLine().close();
                            }
                        }
                    });

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }

}
