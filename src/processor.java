import java.util.HashMap;
abstract class processor{
    abstract void setup(String filename);
    abstract void cycle();
    abstract void run_interrupt(int opcode);
    abstract void save_state();
    abstract void load_state();
    abstract void set_breakpoint(long brk);
    abstract int read_memory(int adr);
    abstract int get_cycles();
    abstract void set_cycles(int c);

    public int[] memory=new int[0x1000];
    public ports ports;
}
