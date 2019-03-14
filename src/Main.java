//importing needed utilities
import javax.swing.*;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.nio.file.*;
import java.io.File;
import java.util.HashMap;
public class Main {
    private static long clock_speed=2000000;
    private static int cycles_per_frame= (int)((clock_speed+60)/60);
    public static HashMap<String, Integer> game_config = new HashMap<>();

    public static String filename="games/invaders.zip";

    //setting up variables
    private static long[] last_frame = new long[60];
    public static Screen screen;
    public static keylistener keyboard =new keylistener();
    public static JFrame f = new JFrame("Java 8080 emulator Patrick Iacob");
    private static double fps;
    public static double max_fps=60;
    public static cpu i8080= new cpu();
    public static void main(String[] args) {
        try {
            System.out.println( args[0]);
            filename = args[0];
        }
        catch(Exception f) { }
        i8080.files=filename;

        //setting up jframe for graphics
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen = new Screen();
        f.add(screen);
        f.addKeyListener(keyboard);

        f.pack();
        f.setFocusable(true);
        f.setVisible(true);
        f.setFocusable(true);
        f.requestFocusInWindow();
        i8080.setup();
        System.out.println("Config loaded: " + filename);
        System.out.println(game_config);
        load_rom(filename);
        i8080.setup();

        System.out.println("0x"+i8080.hex(i8080.memory.length)+" Bytes ready");
        System.out.println("0x"+i8080.hex(i8080.cycle_table.length)+ " opcodes loaded");
        //System.out.println(Arrays.toString(i8080.memory));
        System.out.println("Cpu speed "+clock_speed+"hz");
        System.out.println("Cycles per frame " + cycles_per_frame+"hz");
        System.out.println("\nBoot Success!");
        if(i8080.cpm_mode){
            System.out.println("System booted in cp/m mode\n");
        }

        start();//this is where the fun begins
    }
    private static void start(){
        int frames=0;
        while(true){
            //makes sure the game runs at the correct speed
            if ( System.nanoTime() - last_frame[0] > 1000000000/max_fps){
                //fps counter
                fps=round(1000000000.0/(System.nanoTime()-last_frame[59])*60,2);
                //add fps to title of window
                frames++;
                if (frames>=max_fps/6) {
                    f.setTitle("Java 8080 emulator Patrick Iacob (fps, " + fps + ", " + round(100 * fps / 60, 1) + "%)");
                    frames=0;
                }//reset time since last frame
                for (int i=59;i>0;i--){
                    last_frame[i]=last_frame[i-1];
                }
                last_frame[0]=System.nanoTime();

                //run exact amount of instructions till next interupt
                while (i8080.cycles<cycles_per_frame/2){
                    i8080.cycle();
                }
                //if the cpu has enabled interrupts they will be run
                if (i8080.interrupt_enabled){ i8080.run_interrpt(0xcf);}
                while (i8080.cycles<cycles_per_frame){
                    i8080.cycle();
                }
                i8080.cycles-=cycles_per_frame;
                if (i8080.interrupt_enabled){ i8080.run_interrpt(0xd7);}
                //System.out.println(i8080.tc)
                //updates screen
                screen.paintScreen();
                //System.out.println(i8080.tc);
                //System.out.println("Number of active threads from the given thread: " + Thread.activeCount());
            }
            i8080.sleep(0);
        }
    }
    //load game to memory
    private static void load_game(String game){
        int d=0;
        for (int o = 0; o < (game.length()+1)/3; o++){
            i8080.memory[o] = Integer.parseInt(game.substring(o*3,o*3+2),16);d++;
        }
        System.out.println(d);
    }
    private static void load_rom(String filename) {
        if (filename.toLowerCase().contains(".zip")) {
            try {
                int i;
                File file = new File(filename);
                filename = (file.getAbsolutePath());
                ZipFile zipFile = new ZipFile(filename);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    InputStream stream = zipFile.getInputStream(entry);
                    System.out.print(entry);
                    int o = 0;
                    while ((i = stream.read()) != -1) {
                        // prints character
                        //System.out.print(i8080.hex(i)+" ");
                        try {
                            if (i != 0) {
                                i8080.memory[o + game_config.get(entry.toString())] = i;
                            }
                        } catch (Exception f) {
                            f.printStackTrace(System.out);
                            System.out.println(entry);
                            System.out.println(game_config);
                        }
                        o++;
                    }
                }
            } catch (Exception f) {
                f.printStackTrace(System.out);
                System.exit(6);
            }
        }
        else if (filename.toLowerCase().contains(".com")){
            try {
                File file = new File(filename);
                filename = (file.getAbsolutePath());
                InputStream stream = new FileInputStream(filename);
                int i;
                int o=0;
                while ((i = stream.read()) != -1) {

                    // prints character
                    //System.out.print(i8080.hex(i)+" ");
                    try {
                        if (i != 0) {
                            i8080.memory[o+0x100] = i;
                        }
                    } catch (Exception f) {
                        f.printStackTrace(System.out);
                        System.out.println(filename);
                    }
                    o++;
                }
            }
            catch (Exception f) {
                f.printStackTrace(System.out);
                System.exit(5);
            }
            i8080.cpm_mode=true;
        }
        System.out.println("\nRom loaded");
    }
    //load game to memory starting at location adr
    private static void load_game(String game, int adr){
        int d=0;
        for (int o = 0; o < (game.length()+1)/3; o++){
            i8080.memory[o+adr] = Integer.parseInt(game.substring(o*3,o*3+2),16);d++;
        }
        System.out.println(d);
    }
    //round number to x decimal places
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
