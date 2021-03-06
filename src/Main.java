//importing needed utilities
import javax.swing.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.io.File;
import java.util.HashMap;
public class Main {
    private static long clock_speed=2000000;
    private static int cycles_per_frame= (int)((clock_speed+60)/60);
    public static HashMap<String, Integer> game_config = new HashMap<>();
    public static HashMap<Integer, Integer> key;
    private static int cpu_frames_completed;
    public static int frames_completed;


    private static String filename="games/invaders.zip";

    //setting up variables
    private static long[] last_frame = new long[60];
    public static long last_fps_update;
    public static long next_frame;
    public static Screen screen;
    private static keylistener keyboard =new keylistener();
    public static JFrame f = new JFrame("Java 8080 emulator Stackunderfl0w");
    public static double max_fps=60;
    public static processor cpu;
    public static String[] messages= {"","","","","","","","","",""};
    public static int[] time_left= new int[10];
    public static game_config config;
    public static int[] interrupts= new int[0];
    public static int interrupt;
    public static boolean screen_enabled=true;
    public static boolean sound_enabled;
    public static void main(String[] args) {
        key = new HashMap<>();
        cpu = new i8080();
        try {
            System.out.println( args[0]);
            filename = args[0];
        }
        catch(Exception f) {
            //f.printStackTrace(System.out);
        }
        config=new game_config(filename);
        //setting up jframe for graphics
        if (screen_enabled) {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            screen = new Screen();
            f.add(screen);
            f.addKeyListener(keyboard);

            f.pack();
            f.setFocusable(true);
            f.setVisible(true);
            f.setFocusable(true);
            f.requestFocusInWindow();
        }
        cpu.setup(filename);
        System.out.println("Config loaded: " + filename.substring(filename.lastIndexOf('/')+1));
        System.out.println(game_config);
        load_rom(filename);
        cpu.setup(filename);

        System.out.println("0x"+hex(cpu.memory.length)+" Bytes ready");
        //System.out.println("0x"+hex(cpu.cycle_table.length)+ " opcodes loaded");
        //System.out.println(Arrays.toString(cpu.memory));
        System.out.println("Cpu speed "+clock_speed+"hz");
        System.out.println("Cycles per frame " + cycles_per_frame+"hz");
        System.out.println("\nBoot Success!");
        if(filename.toLowerCase().contains(".com")){
            System.out.println("System booted in cp/m mode\n");
        }

        start();//this is where the fun begins
    }
    private static void start(){
        send_message("Game loaded("+filename+")",180);
        while(true){
            //makes sure the game runs at the correct speed
            if ( System.nanoTime() >next_frame){
                //fps counter
                double fps =round(1000000000.0/(System.nanoTime()-last_frame[59])*60,2);
                //add fps to title of window
                if (System.nanoTime()-last_fps_update>1000000000/2) {
                    last_fps_update=System.nanoTime();
                    f.setTitle("Java 8080 emulator Stackunderfl0w (fps, " + fps + ", " + round(100 * fps / 60, 1) + "%)"+"gpu fps("+frames_completed*2+")");
                    frames_completed=0;
                }//reset time since last frame
                System.arraycopy(last_frame, 0, last_frame, 1, 59);
                last_frame[0]=System.nanoTime();

                interrupt =0;
                //run exact amount of instructions till next interupt
                while (interrupt<interrupts.length){
                    while (cpu.get_cycles()<cycles_per_frame/interrupts.length){
                        cpu.cycle();
                    }
                    cpu.run_interrupt(interrupts[interrupt]);
                    interrupt++;
                }
                cpu.set_cycles(cpu.get_cycles()-cycles_per_frame);
                //updates screen
                f.repaint();
                //System.out.println(cpu.tc);
                //System.out.println("Number of active threads from the given thread: " + Thread.activeCount());
                next_frame=last_frame[0]+1000000000/(int)max_fps+1;
                cpu_frames_completed++;
                System.out.println(cpu_frames_completed+" "+frames_completed);
            }
            //sleep if there is time
            if(next_frame-System.nanoTime()>2000000){
                sleep(1);
            }
            //System.out.println(System.nanoTime()-last_frame[0]);
            sleep(0);
        }
    }
    //load game to memory
    private static void load_game(String game){
        load_game(game,0);
    }
    //load game to memory starting at location adr
    private static void load_game(String game, int adr){
        int d=0;
        for (int o = 0; o < (game.length()+1)/3; o++){
            cpu.memory[o+adr] = Integer.parseInt(game.substring(o*3,o*3+2),16);d++;
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
                        //System.out.print(hex(i)+" ");
                        try {
                            if (i != 0) {
                                cpu.memory[o + game_config.get(entry.toString())] = i;
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
                    //System.out.print(hex(i)+" ");
                    try {
                        if (i != 0) {
                            cpu.memory[o+0x100] = i;
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
        }
        System.out.println("\nRom loaded");
    }
    //round number to x decimal places
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public static void send_message(String string,int time){
        for (int i=9;i>0;i--){
            time_left[i]=time_left[i-1];
            messages[i]=messages[i-1];
        }
        time_left[0]=time;
        messages[0]=string;

        //System.out.println(Arrays.toString(messages));
        //System.out.println(Arrays.toString(time_left));
    }
    private static String hex(int x){
        String r=(String.format("%x",x));
        if (r.length()==1){r="0"+r;}
        return r;
    }
    private static void sleep(long x){
        try {
            Thread.sleep(x);
        }
        catch(Exception e) {
            // this part is executed when an exception (in this example InterruptedException) occurs
        }
    }
}
