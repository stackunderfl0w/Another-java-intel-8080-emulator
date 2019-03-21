import java.util.HashMap;

public class game_config extends ports{
    //variables for if buttons are pressed and internal registers
    private int player_one_fire;
    private int player_one_left;
    private int player_one_right;
    private int one_player_button;
    private int coin;
    private int player_two_fire;
    private int player_two_left;
    private int player_two_right;
    private int two_player_button;
    private final int extra_ship_location=0;//0=1500 1=1000 points
    private final int display_coin_in_demo=0;//0=ON
    private final int extra_ships=0;//extra starting ships 0-3(3-6 total)
    private int port_2;
    private int port_4lo;
    private int port_4hi;
    private String[] sounds=new String[]{"sound/ufo_highpitch.wav","sound/shoot.wav","sound/explosion.wav","sound/invaderkilled.wav","","sound/fastinvader1.wav","sound/fastinvader2.wav","sound/fastinvader3.wav","sound/fastinvader4.wav","sound/ufo_lowpitch.wav"};
    private boolean[] sounds_playing=new boolean[10];
    private int x;
    public HashMap<String, Integer> game_config = new HashMap<>();
    game_config(String game){
        game=game.substring(game.lastIndexOf('/')+1);
        switch(game.toLowerCase()) {
            case "invaders.zip":
                game_config.put("invaders.h", 0);
                game_config.put("invaders.g", 0x800);
                game_config.put("invaders.f", 0x1000);
                game_config.put("invaders.e", 0x1800);
                Main.game_config = game_config;
                break;
            case "lrescue.zip":
                game_config.put("lrescue.1",0);
                game_config.put("lrescue.2",0x800);
                game_config.put("lrescue.3",0x1000);
                game_config.put("lrescue.4",0x1800);
                game_config.put("lrescue.5",0x4000);
                game_config.put("lrescue.6",0x4800);
                break;
            case "ballbomb.zip":
                game_config.put("tn01",0);
                game_config.put("tn02",0x800);
                game_config.put("tn03",0x1000);
                game_config.put("tn04",0x1800);
                game_config.put("tn05",0x4000);
                game_config.put("tn05-1",0x4000);
                break;
            default:
                System.out.println("No config found for\""+game+"\", Exiting");
                System.exit(404);
                break;
        }
        //constructor
        //makes sure keys needed are present in hashmap
        System.out.println("Input/output ports Initialized");
        Main.cpu.key.put("Space",0);
        Main.cpu.key.put("A",0);
        Main.cpu.key.put("D",0);
        Main.cpu.key.put("O",0);
        Main.cpu.key.put("C",0);
        Main.cpu.key.put("␣",0);

        Main.cpu.key.put("Left",0);
        Main.cpu.key.put("Right",0);
        Main.cpu.key.put("Insert",0);
        Main.cpu.key.put("P",0);

    }
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
    public void get_key(){
        //check what keys are pressed
        //just in case keys are not in hashmap
        try
        {
            player_one_fire = Main.cpu.get_key("Space")|Main.cpu.get_key("␣");
            player_one_left = Main.cpu.get_key("A");
            player_one_right = Main.cpu.get_key("D");
            one_player_button = Main.cpu.get_key("O");
            coin = Main.cpu.get_key("C");
            player_two_fire = Main.cpu.get_key("Insert");
            player_two_left = Main.cpu.get_key("Left");
            player_two_right = Main.cpu.get_key("Right");
            two_player_button = Main.cpu.get_key("P");
        }
        catch(Exception f)
        {
            f.printStackTrace(System.out);
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