import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Arrays;


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
        //send_message("hello");
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
        int newx=(int)(x*width/224.0);
        int newy=(int)(y*height/256.0);
        int pixelwidth=(int)(((x+1)*width/224.0)-newx);
        int pixelheight=(int)(((y+1)*height/256.0)-newy);
        g.fillRect(newx, newy, pixelwidth, pixelheight);

    }

    /**
     * Paints full screen from screen memory.
     */
    private void paintFullScreen() {
        width=getWidth();
        height=getHeight();
        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z<8;z++){
                    boolean value = (Main.cpu.read_memory(0x2400+32*y+x)>>z)%2==1;
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
        //long starttime=System.nanoTime();

        paintFullScreen();
        //System.out.println(System.nanoTime()-starttime);

        print_messages();
        Main.frames_completed++;
    }
    //audio player
    //synchronized as to not freeze screen while playing audio
    public synchronized void playAudio(final String url) {
        if (Main.sound_enabled) {
            new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments
                public void run() {
                    try {
                        //gets audio file and starts playing
                        BufferedInputStream myStream = new BufferedInputStream(getClass().getResourceAsStream(url));
                        Clip clip = AudioSystem.getClip();
                        AudioInputStream audio2 = AudioSystem.getAudioInputStream(myStream);
                        clip.open(audio2);
                        clip.start();
                        clip.addLineListener(new LineListener() {
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
    private void paint_string(String s, int x, int y, int size, Color colour){
        //create new font with new size
        Font font = new Font("Arial", Font.BOLD, size);
        g.setFont(font);
        final FontMetrics fm = getFontMetrics(font);
        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];
        g.setColor(colour);
        g.drawString(s,x-w/2,y+h/2);
        //System.out.println("string" +s+" at " +x+","+y);
    }
    private void print_messages(){
        if(Main.time_left[0]>0){
            for (int i=8;i>=0;i--) {
                if(Main.time_left[i]>0) {
                    Main.time_left[i]--;
                }
                if(Main.time_left[i]==0){
                    Main.time_left[i]=Main.time_left[i+1];
                    Main.messages[i]=Main.messages[i+1];
                }
            }
            //System.out.println(Main.messages[0]);
            Font font = new Font("Arial", Font.BOLD, 36);
            g.setFont(font);
            g.setColor(Color.yellow);
            for(int i=0;i<8;i++) {
                g.drawString(Main.messages[i], 0, height-50*i);
            }
        }
    }

}
