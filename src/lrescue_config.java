import java.util.HashMap;

public class lrescue_config extends ports{
    //variables for if buttons are pressed and internal registers
    private static int player_one_fire;
    private static int player_one_left;
    private static int player_one_right;
    private static int one_player_button;
    private static int coin;
    private static int player_two_fire;
    private static int player_two_left;
    private static int player_two_right;
    private static int two_player_button;
    private static final int extra_ship_location=0;//0=1500 1=1000 points
    private static final int display_coin_in_demo=0;//0=ON
    private static final int extra_ships=3;//extra starting ships 0-3(3-6 total)
    private static int port_2;
    private static int port_4lo;
    private static int port_4hi;
    private static String[] sounds=new String[]{"sound/ufo_highpitch.wav","sound/shoot.wav","sound/explosion.wav","sound/invaderkilled.wav","","sound/fastinvader1.wav","sound/fastinvader2.wav","sound/fastinvader3.wav","sound/fastinvader4.wav","sound/ufo_lowpitch.wav"};
    private static boolean[] sounds_playing=new boolean[10];
    private int x;
    public HashMap<String, Integer> game_config = new HashMap<>();

    lrescue_config(){
        game_config.put("lrescue.1",0);
        game_config.put("lrescue.2",0x800);
        game_config.put("lrescue.3",0x1000);
        game_config.put("lrescue.4",0x1800);
        game_config.put("lrescue.5",0x4000);
        game_config.put("lrescue.6",0x4800);
        Main.game_config=game_config;
        //constructor
        //makes sure keys needed are present in hashmap
        System.out.println("Input/output ports Initialized");
        Main.i8080.key.put("Space",0);
        Main.i8080.key.put("A",0);
        Main.i8080.key.put("D",0);
        Main.i8080.key.put("O",0);
        Main.i8080.key.put("C",0);
        Main.i8080.key.put("␣",0);

        Main.i8080.key.put("Left",0);
        Main.i8080.key.put("Right",0);
        Main.i8080.key.put("Insert",0);
        Main.i8080.key.put("P",0);


    }
    //return values of registers(used for button input and bitshift registers)
    public int in(int port){
        switch(port){
            case 1:
                get_key();
                return coin+(two_player_button<<1)+(one_player_button<<2)+(1<<3)+(player_one_fire<<4) +(player_one_left<<5)+(player_one_right<<6);
            case 2:
                return extra_ships+(extra_ship_location<<3)+(player_two_fire<<4)+(player_two_left<<5)+(player_two_right<<6)+(display_coin_in_demo<<7);

            case 3:
                return ((((port_4hi << 8) | (port_4lo)) << port_2) >> 8) & 0xFF;
        }
        return 0;
    }
    //takes input from cpu (used for bitshift register input and sound);
    public void out(int a,int port){
        switch(port){
            case 2:
                port_2=a;
                break;
            case 3://play sound
                //since the original sound hardware is analogue we need to check if the sound is already suppost to be
                // playing or else it will run multiple times stack and slow down the entire program
                //special case, ufo sfx needs to loop
                if (a%2==1){if(!sounds_playing[0]|x>65){ Main.screen.playAudio(sounds[0]);sounds_playing[0]=true;x=0;} }
                else{sounds_playing[0]=false;}
                x++;
                if ((a>>1)%2==1){if(!sounds_playing[1]){ Main.screen.playAudio(sounds[1]);sounds_playing[1]=true;} }
                else{sounds_playing[1]=false;}
                if ((a>>2)%2==1){if(!sounds_playing[2]){ Main.screen.playAudio(sounds[2]);sounds_playing[2]=true;} }
                else{sounds_playing[2]=false;}
                if ((a>>3)%2==1){if(!sounds_playing[3]){ Main.screen.playAudio(sounds[3]);sounds_playing[3]=true;} }
                else{sounds_playing[3]=false;}
                //if ((a>>4)%2==1){if(!sounds_playing[4]){ Main.screen.playAudio(sounds[4]);sounds_playing[4]=true;} }
                //else{sounds_playing[4]=false;}
            case 4://input for bitshift register
                port_4lo=port_4hi;
                port_4hi=a;
                break;
            case 5://play sound 2(secord set of sounds)
                if (a%2==1){if(!sounds_playing[5]){ Main.screen.playAudio(sounds[5]);sounds_playing[5]=true;} }
                else{sounds_playing[5]=false;}
                if ((a>>1)%2==1){if(!sounds_playing[6]){ Main.screen.playAudio(sounds[6]);sounds_playing[6]=true;} }
                else{sounds_playing[6]=false;}
                if ((a>>2)%2==1){if(!sounds_playing[7]){ Main.screen.playAudio(sounds[7]);sounds_playing[7]=true;} }
                else{sounds_playing[7]=false;}
                if ((a>>3)%2==1){if(!sounds_playing[8]){ Main.screen.playAudio(sounds[8]);sounds_playing[8]=true;} }
                else{sounds_playing[8]=false;}
                if ((a>>4)%2==1){if(!sounds_playing[9]){ Main.screen.playAudio(sounds[9]);sounds_playing[9]=true;} }
                else{sounds_playing[9]=false;}
        }
    }
    public static void get_key(){
        //check what keys are pressed
        //just in case keys are not in hashmap
        try
        {
            player_one_fire = Main.i8080.key.get("Space")|Main.i8080.key.get("␣");
            player_one_left = Main.i8080.key.get("A");
            player_one_right = Main.i8080.key.get("D");
            one_player_button = Main.i8080.key.get("O");
            coin = Main.i8080.key.get("C");
            player_two_fire = Main.i8080.key.get("Insert");
            player_two_left = Main.i8080.key.get("Left");
            player_two_right = Main.i8080.key.get("Right");
            two_player_button = Main.i8080.key.get("P");
        }
        catch(Exception f)
        {
            //error handling code
        }
    }
}
//ports layout
/*
Read (in)
   00        INPUTS (Mapped in hardware but never used by the code)
   01        INPUTS
   02        INPUTS
   03        bit shift register read
  Write (out)
   02        shift amount (3 bits)
   03        sound bits
   04        shift data
   05        sound bits
   06        watch-dog
 */
// Port 1 maps the keys for space invaders
// Bit 0 = coin slot
// Bit 1 = two players button
// Bit 2 = one player button
// Bit 4 = player one fire
// Bit 5 = player one left
// Bit 6 = player one right
// Port 2 maps player 2 controls and dip switches
// Bit 0,1 = number of ships
// Bit 2   = mode (1=easy, 0=hard)
// Bit 4   = player two fire
// Bit 5   = player two left
// Bit 6   = player two right
// Bit 7   = show or hide coin info

//3
// Connected to the sound hardware
// Bit 1 = spaceship sound (looped)
// Bit 2 = Shot
// Bit 3 = Your ship hit
// Bit 4 = Invader hit
// Bit 5 = Extended play sound

//5
// Plays sounds
// Bit 0 = invaders sound 1
// Bit 1 = invaders sound 2
// Bit 2 = invaders sound 3
// Bit 3 = invaders sound 4
// Bit 4 = spaceship hit
// Bit 5 = amplifier enabled/disabled