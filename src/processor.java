import java.util.HashMap;
abstract class processor{
    abstract void setup(String filename);
    abstract void cycle();
    abstract void run_interrupt(int opcode);
    abstract void save_state();
    abstract void load_state();
    abstract void set_breakpoint(long brk);
    abstract void write_rom(int adr, int value);
    abstract int read_memory(int adr);
    abstract void key_pressed(String key);
    abstract void key_released(String key);
    abstract int get_key(String key);
    abstract int get_cycles();
    abstract void set_cycles(int c);

    public int[] memory=new int[0x1000];
    public HashMap<String, Integer> key;
    boolean interrupt_enabled;
    long tc;
    int[]cycle_table;
    int cycles=1;

}
