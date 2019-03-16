//http://www.classiccmp.org/dunfield/altair/d/88opman.pdf useful link
import java.util.Arrays;
import java.util.HashMap;
public class cpu {
    //set up cpu registers memory and flags as variables
    public int[] memory = new int[0x8000];
    private boolean debug_mode=false;
    public boolean cpm_mode=false;

    private int[]par_table=new int []{1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1};
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int h;
    private int l;
    private int sp=0x2400;
    private boolean carry;
    private boolean aux_carry;
    private boolean zero;
    private boolean parity;
    private boolean sign;
    private int[] state;
    //public int breakpoint = 9565000;
    public int breakpoint = 1990000;

    public boolean breakpoint_enabled=false;
    public boolean interrupt_enabled=false;
    public int pc;
    public long tc=0;
    private int bc;
    private int de;
    private int hl;
    private int z;
    public int cycles=0;
    public String files;
    //variable for which keys are pressed
    public HashMap<String, Integer> key = new HashMap<>();
    //initialize game specif serial ports setup
    public ports ports;
    //private int[]cycle_table = new int []{4,  10, 7,  5,  5,  5,  7,  4,  4 , 10, 7,  5,  5,  5,  7,  4, 4,  10, 7,  5,  5,  5,  7,  4,  4,  10, 7,  5,  5,  5,  7,  4, 4,  10, 16, 5,  5,  5,  7,  4,  4,  10, 16, 5,  5,  5,  7,  4, 4,  10, 13, 5,  10, 10, 10, 4,  4,  10, 13, 5,  5,  5,  7,  4, 5,  5,  5,  5,  5,  5,  7,  5,  5,  5,  5,  5,  5,  5,  7,  5, 5,  5,  5,  5,  5,  5,  7,  5,  5,  5,  5,  5,  5,  5,  7,  5, 5,  5,  5,  5,  5,  5,  7,  5,  5,  5,  5,  5,  5,  5,  7,  5, 7,  7,  7,  7,  7,  7,  7,  7,  5,  5,  5,  5,  5,  5,  7,  5, 4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4, 4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4, 4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4, 4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4, 11, 10, 10, 10, 17, 11, 7,  11, 11, 10, 10, 10, 10, 17, 7,  11, 11, 10, 10, 10, 17, 11, 7,  11, 11, 10, 10, 10, 10, 17, 7,  11, 11, 10, 10, 18, 17, 11, 7,  11, 11, 5,  10, 5,  17, 17, 7,  11, 11, 10, 10, 4,  17, 11, 7,  11, 11, 5,  10, 4,  17, 17, 7,  11};
    //table for how many cycles each instruction takes
    public int[]cycle_table = new int []{4,10,7,6,5,5,7,4,0,11,7,6,5,5,7,4      ,4,10,7,6,5,5,7,4,4,11,7,6,5,5,7,4      ,4,10,16,6,5,5,7,4,4,11,16,6,5,5,7,4        ,4,10,13,6,10,10,10,4,4,11,13,6,5,5,7,4     ,5,5,5,5,5,5,7,5,5,5,5,5,5,5,7,5        ,5,5,5,5,5,5,7,5,5,5,5,5,5,5,7,5    ,5,5,5,5,5,5,7,5,5,5,5,5,5,5,7,5    ,7,7,7,7,7,7,7,7,5,5,5,5,5,5,7,5    ,4,4,4,4,4,4,7,4,4,4,4,4,4,4,7,4    ,4,4,4,4,4,4,7,4,4,4,4,4,4,4,7,4    ,4,4,4,4,4,4,7,4,4,4,4,4,4,4,7,4    ,4,4,4,4,4,4,7,4,4,4,4,4,4,4,7,4    ,11,10,15,10,18,11,7,11,11,10,15,00,18,17,7,11      ,11,10,15,10,18,11,7,11,11,4,15,10,18,4,7,11    ,11,10,15,4,18,11,7,11,11,4,10,4,18,11,7,11     ,11,10,15,4,18,11,7,11,11,6,15,4,18,4,7,11};
    //run 1 emulated cycle
    public void setup(){
        ports= new game_config(files);
        if (cpm_mode){
            pc=0x100;
            memory[5]=0xc9;
        }
    }
    public cpu(){

    }
    public void cycle(){
        //load next memory as instrctions
        int opcode=memory[pc];
        int d8=memory[pc+1];
        int op3=memory[pc+2];
        //debuging info
        if (debug_mode){
            System.out.println("True counter:"+tc);
            System.out.println("cycles:"+cycles);
            System.out.println("    Program counter:"+hex(pc));
            System.out.print("    "+hex(a)+hex(get_f())+" "+hex(b)+hex(c)+" "+hex(d)+hex(e)+" "+hex(h)+hex(l)+" "+hex(sp)+" ");
            if (zero){System.out.print("z");}
            if (sign){System.out.print("s");}
            if (parity){System.out.print("p");}
            if (carry){System.out.print("c");}
            System.out.println(" ");
            System.out.println("    Opcode:"+hex(opcode)+"   "+hex(d8)+" "+hex(op3));
            System.out.print("    ");
            try {
                System.out.println(hex(memory[(h<<8)+l]));
            } catch (Exception e) {
                System.out.println("null");
            }
        }
        //run the current instrucion
        if (cpm_mode){
            if(pc==5){
                if(c==9){
                    int ofset = (d<<8)+e;
                    int z=0;
                    while((char)memory[ofset+3+z]!='$'){
                        System.out.print((char)memory[ofset+3+z]);
                        z++;
                    }
                    System.out.println(pc);

                }
                else if (c==2){
                    System.out.print((char)e);
                    //System.out.println(tc);

                }
            }
            else if (pc==0){
                System.out.println("\nExiting(cp/m warm boot attempted)");
                System.exit(0);
            }
        }
        run_op(opcode,d8,op3);
        //increase counters
        pc++;
        tc++;
        //count cycles passed
        cycles+=cycle_table[opcode];
        //exit programm after set number of cyles(debuging)
        if(breakpoint_enabled) {
            if (tc == breakpoint) {
                System.out.println(memory[0x92]);

                sleep(5000);
                Main.screen.paintScreen();
                System.exit(0);
            }
            //turn on debug mode 500 cycles before progam exits
            if (tc + 5000 >= breakpoint) {
                debug_mode = true;
            }
        }

    }
    public void run_interrpt(int opcode) {
        run_op(opcode,0,0);
        pc++;
    }

        //run cpu instruction
    public void run_op(int opcode,int d8,int op3){
        int d16=(op3<<8)+d8;
        //switch statement for each of the 256 instructions
        switch(opcode){
            //load registers from next memory locations
            case 0x01:if (debug_mode){System.out.println("LXI B, D16");}
                b=op3;
                c=d8;
                pc+=2;
                break;
            case 0x11:if (debug_mode){System.out.println("LXI D, D16");}
                d=op3;
                e=d8;
                pc+=2;
                break;
            case 0x21:if (debug_mode){System.out.println("LXI H, D16");}
                h=op3;
                l=d8;
                pc+=2;
                break;
            case 0x31:if (debug_mode){System.out.println("LXI SP, D16");}
                sp=d16;
                pc+=2;
                break;
            //Direct addressing: STA, LDA, SHLD, LHLD
            //access memory location(general)
            case 0x22:if (debug_mode){System.out.println("Shld adr");}
                memory[d16+1]=h;
                memory[d16]=l;
                pc+=2;
                break;
            case 0x2a:if (debug_mode){System.out.println("Lhld adr");}
                h=(memory[d16+1]+256)%256;
                l=(memory[d16]+256)%256;
                pc+=2;
                break;
            case 0x32:if (debug_mode){System.out.println("Sta adr");}
                memory[d16]=a;
                pc+=2;
                break;
            case 0x3a:if (debug_mode){System.out.println("Lda adr");}
                a=memory[d16];
                pc+=2;
                break;
            //STAX, LDAX
            //set a to value at memory location or oposite
            case 0x02:if (debug_mode){System.out.println("Stax B");}
                memory[(b<<8)+c]=a;
                break;
            case 0x0a:if (debug_mode){System.out.println("LDAX B");}
                a=(memory[(b<<8)+c]+256)%256;
                break;
            case 0x12:if (debug_mode){System.out.println("Stax D");}
                memory[(d<<8)+e]=a;
                break;
            case 0x1a:if (debug_mode){System.out.println("LDAX D");}
                a=(memory[(d<<8)+e]+256)%256;
                break;
            //MVI(dest, d8)
            //set register to next byte
            case 0x06:if (debug_mode){System.out.println("Mvi b,d8");}
                b=d8;
                pc++;
                break;
            case 0x0e:if (debug_mode){System.out.println("Mvi c,d8");}
                c=d8;
                pc++;
                break;
            case 0x16:if (debug_mode){System.out.println("Mvi d,d8");}
                d=d8;
                pc++;
                break;
            case 0x1e:if (debug_mode){System.out.println("Mvi e,d8");}
                e=d8;
                pc++;
                break;
            case 0x26:if (debug_mode){System.out.println("Mvi h,d8");}
                h=d8;
                pc++;
                break;
            case 0x2e:if (debug_mode){System.out.println("Mvi l,d8");}
                l=d8;
                pc++;
                break;
            case 0x36:if (debug_mode){System.out.println("Mvi m,d8");}
                memory[(h<<8)+l]=d8;
                pc++;
                break;
            case 0x3e:if (debug_mode){System.out.println("Mvi a,d8");}
                a=d8;
                pc++;
                break;
            //MOV(dest, src)
            //copy value from one register to another
            case 0x40:if (debug_mode){System.out.println("Mov b,b");}
                //b=b;
                break;
            case 0x41:if (debug_mode){System.out.println("Mov b,c");}
                b=c;
                break;
            case 0x42:if (debug_mode){System.out.println("Mov b,d");}
                b=d;
                break;
            case 0x43:if (debug_mode){System.out.println("Mov b,e");}
                b=e;
                break;
            case 0x44:if (debug_mode){System.out.println("Mov b,h");}
                b=h;
                break;
            case 0x45:if (debug_mode){System.out.println("Mov b,l");}
                b=l;
                break;
            case 0x46:if (debug_mode){System.out.println("Mov b,m");}
                b=memory[(h<<8)+l];
                break;
            case 0x47:if (debug_mode){System.out.println("Mov b,a");}
                b=a;
                break;

            case 0x48:if (debug_mode){System.out.println("Mov c,b");}
                c=b;
                break;
            case 0x49:if (debug_mode){System.out.println("Mov c,c");}
                //c=c;
                break;
            case 0x4a:if (debug_mode){System.out.println("Mov c,d");}
                c=d;
                break;
            case 0x4b:if (debug_mode){System.out.println("Mov c,e");}
                c=e;
                break;
            case 0x4c:if (debug_mode){System.out.println("Mov c,h");}
                c=h;
                break;
            case 0x4d:if (debug_mode){System.out.println("Mov c,l");}
                c=l;
                break;
            case 0x4e:if (debug_mode){System.out.println("Mov c,m");}
                c=memory[(h<<8)+l];
                break;
            case 0x4f:if (debug_mode){System.out.println("Mov c,a");}
                c=a;
                break;

            case 0x50:if (debug_mode){System.out.println("Mov d,b");}
                d=b;
                break;
            case 0x51:if (debug_mode){System.out.println("Mov d,c");}
                d=c;
                break;
            case 0x52:if (debug_mode){System.out.println("Mov d,d");}
                //d=d;
                break;
            case 0x53:if (debug_mode){System.out.println("Mov d,e");}
                d=e;
                break;
            case 0x54:if (debug_mode){System.out.println("Mov d,h");}
                d=h;
                break;
            case 0x55:if (debug_mode){System.out.println("Mov d,l");}
                d=l;
                break;
            case 0x56:if (debug_mode){System.out.println("Mov d,m");}
                d=memory[(h<<8)+l];
                break;
            case 0x57:if (debug_mode){System.out.println("Mov d,a");}
                d=a;
                break;

            case 0x58:if (debug_mode){System.out.println("Mov e,b");}
                e=b;
                break;
            case 0x59:if (debug_mode){System.out.println("Mov e,c");}
                e=c;
                break;
            case 0x5a:if (debug_mode){System.out.println("Mov e,d");}
                e=d;
                break;
            case 0x5b:if (debug_mode){System.out.println("Mov e,e");}
                //e=e;
                break;
            case 0x5c:if (debug_mode){System.out.println("Mov e,h");}
                e=h;
                break;
            case 0x5d:if (debug_mode){System.out.println("Mov e,l");}
                e=l;
                break;
            case 0x5e:if (debug_mode){System.out.println("Mov e,m");}
                e=memory[(h<<8)+l];
                break;
            case 0x5f:if (debug_mode){System.out.println("Mov e,a");}
                e=a;
                break;

            case 0x60:if (debug_mode){System.out.println("Mov h,b");}
                h=b;
                break;
            case 0x61:if (debug_mode){System.out.println("Mov h,c");}
                h=c;
                break;
            case 0x62:if (debug_mode){System.out.println("Mov h,d");}
                h=d;
                break;
            case 0x63:if (debug_mode){System.out.println("Mov h,e");}
                h=e;
                break;
            case 0x64:if (debug_mode){System.out.println("Mov h,h");}
                //h=h;
                break;
            case 0x65:if (debug_mode){System.out.println("Mov h,l");}
                h=l;
                break;
            case 0x66:if (debug_mode){System.out.println("Mov h,m");}
                h=memory[(h<<8)+l];
                break;
            case 0x67:if (debug_mode){System.out.println("Mov h,a");}
                h=a;
                break;

            case 0x68:if (debug_mode){System.out.println("Mov l,b");}
                l=b;
                break;
            case 0x69:if (debug_mode){System.out.println("Mov l,c");}
                l=c;
                break;
            case 0x6a:if (debug_mode){System.out.println("Mov l,d");}
                l=d;
                break;
            case 0x6b:if (debug_mode){System.out.println("Mov l,e");}
                l=e;
                break;
            case 0x6c:if (debug_mode){System.out.println("Mov l,h");}
                l=h;
                break;
            case 0x6d:if (debug_mode){System.out.println("Mov l,l");}
                //l=l;
                break;
            case 0x6e:if (debug_mode){System.out.println("Mov l,m");}
                l=memory[(h<<8)+l];
                break;
            case 0x6f:if (debug_mode){System.out.println("Mov l,a");}
                l=a;
                break;

            case 0x70:if (debug_mode){System.out.println("Mov m,b");}
                write_memory((h<<8)+l,b);
                break;
            case 0x71:if (debug_mode){System.out.println("Mov m,c");}
                write_memory((h<<8)+l,c);
                break;
            case 0x72:if (debug_mode){System.out.println("Mov m,d");}
                write_memory((h<<8)+l,d);
                break;
            case 0x73:if (debug_mode){System.out.println("Mov m,e");}
                write_memory((h<<8)+l,e);
                break;
            case 0x74:if (debug_mode){System.out.println("Mov m,h");}
                write_memory((h<<8)+l,h);
                break;
            case 0x75:if (debug_mode){System.out.println("Mov m,l");}
                write_memory((h<<8)+l,l);
                break;
            case 0x77:if (debug_mode){System.out.println("Mov m,a");}
                write_memory((h<<8)+l,a);
                break;

            case 0x78:if (debug_mode){System.out.println("Mov a,b");}
                a=b;
                break;
            case 0x79:if (debug_mode){System.out.println("Mov a,c");}
                a=c;
                break;
            case 0x7a:if (debug_mode){System.out.println("Mov a,d");}
                a=d;
                break;
            case 0x7b:if (debug_mode){System.out.println("Mov a,e");}
                a=e;
                break;
            case 0x7c:if (debug_mode){System.out.println("Mov a,h");}
                a=h;
                break;
            case 0x7d:if (debug_mode){System.out.println("Mov a,l");}
                a=l;
                break;
            case 0x7e:if (debug_mode){System.out.println("Mov a,m");}
                a=memory[(h<<8)+l];
                break;
            case 0x7f:if (debug_mode){System.out.println("Mov a,a");}
                break;
            //Increment/decrement
            //increase or decrease value of a register by 1
            case 0x04:if (debug_mode){System.out.println("Inr b");}
                b++;
                if (b>=256){b-=256;}
                check_zero(b);
                check_sign(b);
                check_parity(b);
                aux_carry = (b & 0xf) == 0;
                break;
            case 0x05:if (debug_mode){System.out.println("Dcr b");}
                b--;
                if (b<0){b+=256;}
                check_zero(b);
                check_sign(b);
                check_parity(b);
                aux_carry = (b & 0xf) == 0;
                break;
            case 0x0c:if (debug_mode){System.out.println("Inr c");}
                c++;
                check_carry(c);
                if (c>=256){c-=256;}
                check_zero(c);
                check_sign(c);
                check_parity(c);
                aux_carry = (c & 0xf) == 0;
                break;
            case 0x0d:if (debug_mode){System.out.println("Dcr c");}
                c--;
                if (c<0){c+=256;}
                check_zero(c);
                check_sign(c);
                check_parity(c);
                aux_carry = (c & 0xf) == 0;
                break;
            case 0x14:if (debug_mode){System.out.println("Inr d");}
                d++;
                if (d>=256){d-=256;}
                check_zero(d);
                check_sign(d);
                check_parity(d);
                aux_carry = (d & 0xf) == 0;
                break;
            case 0x15:if (debug_mode){System.out.println("Dcr d");}
                d--;
                if (d<0){d+=256;}
                check_zero(d);
                check_sign(d);
                check_parity(d);
                aux_carry = (d & 0xf) == 0;
                break;
            case 0x1c:if (debug_mode){System.out.println("Inr e");}
                e++;
                if (e>=256){e-=256;}
                check_zero(e);
                check_sign(e);
                check_parity(e);
                aux_carry = (e & 0xf) == 0;
                break;
            case 0x1d:if (debug_mode){System.out.println("Dcr e");}
                e--;
                if (e<0){e+=256;}
                check_zero(e);
                check_sign(e);
                check_parity(e);
                aux_carry = (e & 0xf) == 0;
                break;
            case 0x24:if (debug_mode){System.out.println("Inr h");}
                h++;
                if (h>=256){h-=256;}
                check_zero(h);
                check_sign(h);
                check_parity(h);
                aux_carry = (h & 0xf) == 0;
                break;
            case 0x25:if (debug_mode){System.out.println("Dcr h");}
                h--;
                if (h<0){h+=256;}
                check_zero(h);
                check_sign(h);
                check_parity(h);
                aux_carry = (h & 0xf) == 0;
                break;
            case 0x2c:if (debug_mode){System.out.println("Inr l");}
                l++;
                check_carry(l);
                if (c>=256){l-=256;}
                check_zero(l);
                check_sign(l);
                check_parity(l);
                aux_carry = (l & 0xf) == 0;
                break;
            case 0x2d:if (debug_mode){System.out.println("Dcr l");}
                l--;
                if (l<0){l+=256;}
                check_zero(l);
                check_sign(l);
                check_parity(l);
                aux_carry = (l & 0xf) == 0;
                break;
            case 0x34:if (debug_mode){System.out.println("Inr m");}
                memory[(h<<8)+l]++;
                if (memory[(h<<8)+l]>=256){memory[(h<<8)+l]-=256;}
                check_zero(memory[(h<<8)+l]);
                check_sign(memory[(h<<8)+l]);
                check_parity(memory[(h<<8)+l]);
                aux_carry = ((memory[(h<<8)+l]) & 0xf) == 0;
                break;
            case 0x35:if (debug_mode){System.out.println("Dcr m");}
                memory[(h<<8)+l]--;
                if (memory[(h<<8)+l]<0){memory[(h<<8)+l]+=256;}
                check_zero(memory[(h<<8)+l]);
                check_sign(memory[(h<<8)+l]);
                check_parity(memory[(h<<8)+l]);
                aux_carry = ((memory[(h<<8)+l]) & 0xf) == 0;
                break;
            case 0x3c:if (debug_mode){System.out.println("Inr a");}
                a++;
                if (a>=256){a-=256;}
                check_zero(a);
                check_sign(a);
                check_parity(a);
                aux_carry = (a & 0xf) == 0;
                break;
            case 0x3d:if (debug_mode){System.out.println("Dcr a");}
                a--;
                if (a<0){a+=256;}
                check_zero(a);
                check_sign(a);
                check_parity(a);
                aux_carry = (a & 0xf) == 0;
                break;
            //Rotate
            //binarry shift accumulator instructions
            case 0x07:if (debug_mode){System.out.println("Rlc");}//might work
                carry=a>>7==1;
                a=(a<<1)%256;
                if(carry){a++;}
                break;
            case 0x17:if (debug_mode){System.out.println("Ral");}//might work
                z=a>>7;
                a=(a<<1)%256;
                if(carry){a++;}
                carry=z==1;
                break;
            case 0x0f:if (debug_mode){System.out.println("Rrc");}
                carry=a%2==1;
                a=a>>1;
                if(carry){a+=0b10000000;}
                break;
            case 0x1f:if (debug_mode){System.out.println("Rar");}//might work
                z=a%2;
                a=a>>1;
                if(carry){a+=0b10000000;}
                carry=z==1;
                break;
            case 0x2f:if (debug_mode){System.out.println("cma");}//might work
                a^=0xff;
                break;
            case 0x37:if (debug_mode){System.out.println("Stc");}
                carry=true;
                break;
            case 0x3f:if (debug_mode){System.out.println("Cmc");}
                carry=!carry;
                break;
            //16-bit inc/dec
            //increase 16 bit registers by 1
            case 0x03:if (debug_mode){System.out.println("Inx B");}
                bc= (b<<8)+c;
                bc++;
                b=bc>>8%256;
                c=bc%256;
                break;
            case 0x0b:if (debug_mode){System.out.println("Dcx B");}
                bc= (b<<8)+c;
                bc--;
                b=bc>>8%256;
                c=bc%256;
                break;
            case 0x13:if (debug_mode){System.out.println("Inx D");}
                de= (d<<8)+e;
                de++;
                d=de>>8%256;
                e=de%256;
                break;
            case 0x1b:if (debug_mode){System.out.println("Dcx D");}
                de= (d<<8)+e;
                de--;
                d=de>>8%256;
                e=de%256;
                break;
            case 0x23:if (debug_mode){System.out.println("Inx H");}
                hl= (h<<8)+l;
                hl++;
                h=hl>>8%256;
                l=hl%256;
                break;
            case 0x2b:if (debug_mode){System.out.println("Dcx H");}
                hl= (h<<8)+l;
                hl--;
                h=hl>>8%256;
                l=hl%256;
                break;
            case 0x33:if (debug_mode){System.out.println("Inx Sp");}
                sp++;
                break;
            case 0x3b:if (debug_mode){System.out.println("Dcx Sp");}
                sp--;
                break;
            //XTHL, XCHG, SPHL
            //exchange or set values of 16 bit registers
            case 0xe3:if (debug_mode){System.out.println("Xthl");}//asdwda
                z=l;
                l=memory[sp];
                memory[sp]=z;
                z=h;
                h=memory[sp+1];
                memory[sp+1]=z;
                break;
            case 0xe9:if (debug_mode){System.out.println("Pchl");}//awdasda
                hl= (h<<8)+l;
                pc=hl-1;
                break;
            case 0xeb:if (debug_mode){System.out.println("Xchg");}
                z=h;
                h=d;
                d=z;
                z=l;
                l=e;
                e=z;
                break;
            case 0xf9:if (debug_mode){System.out.println("Sphl");}
                hl= (h<<8)+l;
                sp=hl;
                break;
            //Arith/logic
            //addition subtraction /other arithmetic functions
            //add values of registers
            case 0x80:if (debug_mode){System.out.println("Add b");}
                add(b);
                break;
            case 0x81:if (debug_mode){System.out.println("Add c");}
                add(c);
                break;
            case 0x82:if (debug_mode){System.out.println("Add d");}
                add(d);
                break;
            case 0x83:if (debug_mode){System.out.println("Add e");}
                add(e);
                break;
            case 0x84:if (debug_mode){System.out.println("Add h");}
                add(h);
                break;
            case 0x85:if (debug_mode){System.out.println("Add l");}
                add(l);
                break;
            case 0x86:if (debug_mode){System.out.println("Add m");}
                add(memory[(h<<8)+l]);
                break;
            case 0x87:if (debug_mode){System.out.println("Add a");}
                add(a);
                break;
                //subtract values of registers from accumulator
            case 0x90:if (debug_mode){System.out.println("sub b");}
                sub(b);
                break;
            case 0x91:if (debug_mode){System.out.println("sub c");}
                sub(c);
                break;
            case 0x92:if (debug_mode){System.out.println("sub d");}
                sub(d);
                break;
            case 0x93:if (debug_mode){System.out.println("sub e");}
                sub(e);
                break;
            case 0x94:if (debug_mode){System.out.println("sub h");}
                sub(h);
                break;
            case 0x95:if (debug_mode){System.out.println("sub l");}
                sub(l);
                break;
            case 0x96:if (debug_mode){System.out.println("sub m");}
                sub(memory[(h<<8)+l]);
                break;
            case 0x97:if (debug_mode){System.out.println("sub a");}
                sub(a);
                break;
                //subtract registers from accumulator and carry
            case 0x98:if (debug_mode){System.out.println("Sbb b");}
                sbb(b);
                break;
            case 0x99:if (debug_mode){System.out.println("Sbb c");}
                sbb(c);
                break;
            case 0x9a:if (debug_mode){System.out.println("Sbb d");}
                sbb(d);
                break;
            case 0x9b:if (debug_mode){System.out.println("Sbb e");}
                sbb(e);
                break;
            case 0x9c:if (debug_mode){System.out.println("Sbb h");}
                sbb(h);
                break;
            case 0x9d:if (debug_mode){System.out.println("Sbb l");}
                sbb(l);
                break;
            case 0x9e:if (debug_mode){System.out.println("Sbb m");}
                sbb(memory[(h<<8)+l]);
                break;
            case 0x9f:if (debug_mode){System.out.println("Sbb a");}
                sbb(a);
                break;
                //add values of register and accumulator(bitwise)
            case 0xa0:if (debug_mode){System.out.println("Ana b");}
                ana(b);
                break;
            case 0xa1:if (debug_mode){System.out.println("Ana c");}
                ana(c);
                break;
            case 0xa2:if (debug_mode){System.out.println("Ana d");}
                ana(d);
                break;
            case 0xa3:if (debug_mode){System.out.println("Ana e");}
                ana(e);
                break;
            case 0xa4:if (debug_mode){System.out.println("Ana h");}
                ana(h);
                break;
            case 0xa5:if (debug_mode){System.out.println("Ana l");}
                ana(l);
                break;
            case 0xa6:if (debug_mode){System.out.println("Ana m");}
                ana(memory[(h<<8)+l]);
                break;
            case 0xa7:if (debug_mode){System.out.println("Ana a");}
                ana(a);
                break;
                //not or value of accumulators and register
            case 0xa8:if (debug_mode){System.out.println("Xra b");}
                xra(b);
                break;
            case 0xa9:if (debug_mode){System.out.println("Xra c");}
                xra(c);
                break;
            case 0xaa:if (debug_mode){System.out.println("Xra d");}
                xra(d);
                break;
            case 0xab:if (debug_mode){System.out.println("Xra e");}
                xra(e);
                break;
            case 0xac:if (debug_mode){System.out.println("Xra h");}
                xra(h);
                break;
            case 0xad:if (debug_mode){System.out.println("Xra l");}
                xra(l);
                break;
            case 0xae:if (debug_mode){System.out.println("Xra m");}
                xra(memory[(h<<8)+l]);
                break;
            case 0xaf:if (debug_mode){System.out.println("Xra a");}
                xra(a);
                break;
                //or values of accumulator and register
            case 0xb0:if (debug_mode){System.out.println("Ora b");}
                ora(b);
                break;
            case 0xb1:if (debug_mode){System.out.println("Ora c");}
                ora(c);
                break;
            case 0xb2:if (debug_mode){System.out.println("Ora d");}
                ora(d);
                break;
            case 0xb3:if (debug_mode){System.out.println("Ora e");}
                ora(e);
                break;
            case 0xb4:if (debug_mode){System.out.println("Ora h");}
                ora(h);
                break;
            case 0xb5:if (debug_mode){System.out.println("Ora l");}
                ora(l);
                break;
            case 0xb6:if (debug_mode){System.out.println("Ora m");}
                ora(memory[(h<<8)+l]);
                break;
            case 0xb7:if (debug_mode){System.out.println("Ora a");}
                ora(a);
                break;
                //compare values of register and accumulator
            case 0xb8:if (debug_mode){System.out.println("cmp b");}
                cmp(b);
                break;
            case 0xb9:if (debug_mode){System.out.println("cmp c");}
                cmp(c);
                break;
            case 0xba:if (debug_mode){System.out.println("cmp d");}
                cmp(d);
                break;
            case 0xbb:if (debug_mode){System.out.println("cmp e");}
                cmp(e);
                break;
            case 0xbc:if (debug_mode){System.out.println("cmp h");}
                cmp(h);
                break;
            case 0xbd:if (debug_mode){System.out.println("cmp l");}
                cmp(l);
                break;
            case 0xbe:if (debug_mode){System.out.println("cmp m");}
                cmp(memory[(h<<8)+l]);
                break;
            case 0xbf:if (debug_mode){System.out.println("cmp a");}
                cmp(a);
                break;
            case 0xf6:if (debug_mode){System.out.println("Ori d8");}
                ora(d8);
                pc++;
                break;
            //ADI, ADC, SUI, SBI, ANI, XRI, ORI, CPI
            //arithmetic instructions with byte instruction
            case 0x88:if (debug_mode){System.out.println("Adc b");}
                adc(b);
                break;
            case 0x89:if (debug_mode){System.out.println("Adc c");}
                adc(c);
                break;
            case 0x8a:if (debug_mode){System.out.println("Adc d");}
                adc(d);
                break;
            case 0x8b:if (debug_mode){System.out.println("Adc e");}
                adc(e);
                break;
            case 0x8c:if (debug_mode){System.out.println("Adc h");}
                adc(h);
                break;
            case 0x8d:if (debug_mode){System.out.println("Adc l");}
                adc(l);
                break;
            case 0x8e:if (debug_mode){System.out.println("Adc m");}
                adc(memory[(h<<8)+l]);
                break;
            case 0x8f:if (debug_mode){System.out.println("Adc a");}
                adc(a);
                break;
            case 0xc6:if (debug_mode){System.out.println("Adi d8");}
                add(d8);
                pc++;
                break;
            case 0xce:if (debug_mode){System.out.println("Aci d8");}
                adc(d8);
                pc++;
                break;
            case 0xd6:if (debug_mode){System.out.println("Sui d8");}
                sub(d8);
                pc++;
                break;
            case 0xde:if (debug_mode){System.out.println("Sbi d8");}
                sbb(d8);
                pc++;
                break;
            case 0xe6:if (debug_mode){System.out.println("Ani d8");}
                ana(d8);
                pc++;
                break;
            case 0xee:if (debug_mode){System.out.println("Xri d8");}
                xra(d8);
                pc++;
                break;
            case 0xfe:if (debug_mode){System.out.println("Cpi d8");}
                cmp(d8);
                pc++;
                break;
            //16bit A/L
            //add values of 16 bit registers
            case 0x09:if (debug_mode){System.out.println("Dad B");}
                hl= (h<<8)+l;
                bc= (b<<8)+c;
                hl+=bc;
                h=(hl>>8)&0xff;
                l=hl&0xff;
                carry= (hl & 0x10000) != 0;
                break;
            case 0x19:if (debug_mode){System.out.println("Dad D");}
                hl= (h<<8)+l;
                de= (d<<8)+e;
                hl+=de;
                h=(hl>>8)&0xff;
                l=hl&0xff;
                carry= (hl & 0x10000) != 0;
                break;
            case 0x29:if (debug_mode){System.out.println("Dad H");}
                hl= (h<<8)+l;
                hl*=2;
                h=(hl>>8)&0xff;
                l=hl%256;
                carry= (hl & 0x10000) != 0;
                break;
            case 0x39:if (debug_mode){System.out.println("Dad Sp");}
                hl= (h<<8)+l;
                hl+=sp;
                h=(hl>>8)&0xff;
                l=hl%256;
                carry= (hl & 0x10000) != 0;
                break;
            //Push/pop using stack pointer
            //push pop 16 bit registers to/from stack
            case 0xc1:if (debug_mode){System.out.println("Pop B");}
                c=memory[sp];
                b=memory[sp+1];
                sp+=2;
                break;
            case 0xc5:if (debug_mode){System.out.println("Push B");}
                memory[sp-1]=b;
                memory[sp-2]=c;
                sp-=2;
                break;
            case 0xd1:if (debug_mode){System.out.println("Pop D");}
                e=memory[sp];
                d=memory[sp+1];
                sp+=2;
                break;
            case 0xd5:if (debug_mode){System.out.println("Push D");}
                memory[sp-1]=d;
                memory[sp-2]=e;
                sp-=2;
                break;
            case 0xe1:if (debug_mode){System.out.println("Pop H");}
                l=memory[sp];
                h=memory[sp+1];
                sp+=2;
                break;
            case 0xe5:if (debug_mode){System.out.println("Push H");}
                memory[sp-1]=h;
                memory[sp-2]=l;
                sp-=2;
                break;
                //save flags to stack
            case 0xf1:if (debug_mode){System.out.println("Pop Psw");}
                z=memory[sp];
                sign=(z>>7)==1;
                zero=(z>>6)%2==1;
                aux_carry=(z>>4)%2==1;
                parity=(z>>2)%2==1;
                carry=z%2==1;
                a=memory[sp+1];
                sp+=2;
                break;
                //load flags from stack
            case 0xf5:if (debug_mode){System.out.println("Push Psw");}
                memory[sp-1]=a;
                memory[sp-2]=get_f();
                sp-=2;
                break;
            //Jumps
            //jump to memory location(sometimes conditional)
            case 0xc2:if (debug_mode){System.out.println("Jnz $"+hex(d16));}
                if (!zero){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xc3:if (debug_mode){System.out.println("Jmp $"+hex(d16));}
                pc=d16-1;
                break;
            case 0xca:if (debug_mode){System.out.println("Jz $"+hex(d16));}
                if (zero){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xd2:if (debug_mode){System.out.println("Jnc $"+hex(d16));}
                if (!carry){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xda:if (debug_mode){System.out.println("Jc $"+hex(d16));}
                if (carry){pc=d16-1;break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xe2:if (debug_mode){System.out.println("Jpo $"+hex(d16));}
                if (!parity){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xea:if (debug_mode){System.out.println("Jpe $"+hex(d16));}
                if (parity){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xf2:if (debug_mode){System.out.println("Jp $"+hex(d16));}//nani
                if (!sign){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            case 0xfa:if (debug_mode){System.out.println("Jm $"+hex(d16));}
                if (sign){jump(d16);break;}
                pc+=2;
                cycles-=5;
                break;
            //Calls
            //push current location to stack and jump to new location(sometimes conditional)
            case 0xc4:if (debug_mode){System.out.println("Cnz $"+hex(d16));}
                pc+=2;
                if (!zero){call(d16);break;}
                cycles-=7;
                break;
            case 0xcc:if (debug_mode){System.out.println("Cnz $"+hex(d16));}
                pc+=2;
                if (zero){call(d16);break;}
                cycles-=7;
                break;
            case 0xcd:if (debug_mode){System.out.println("call $"+hex(d16));}
                pc+=2;
                call(d16);
                break;
            case 0xd4:if (debug_mode){System.out.println("Cnc $"+hex(d16));}
                pc+=2;
                if (!carry){call(d16);break;}
                cycles-=7;
                break;
            case 0xdc:if (debug_mode){System.out.println("Cc $"+hex(d16));}
                pc+=2;
                if (carry){call(d16);break;}
                cycles-=7;
                break;
            case 0xe4:if (debug_mode){System.out.println("Cpo $"+hex(d16));}
                pc+=2;
                if (!parity){call(d16);break;}
                cycles-=7;
                break;
            case 0xec:if (debug_mode){System.out.println("Cpe $"+hex(d16));}
                pc+=2;
                if (parity){call(d16);break;}
                cycles-=7;
                break;
            case 0xf4:if (debug_mode){System.out.println("Cp $"+hex(d16));}
                pc+=2;
                if (!sign){call(d16);break;}
                cycles-=7;
                break;
            case 0xfc:if (debug_mode){System.out.println("Cm $"+hex(d16));}
                pc+=2;
                if (sign){call(d16);break;}
                cycles-=7;
                break;
            //Returns
            //return to location stored in stack from call(sometimes conditional)
            case 0xc0:if (debug_mode){System.out.println("Rnz");}
                if(!zero){ret();break;}
                cycles-=6;
                break;
            case 0xc8:if (debug_mode){System.out.println("Rz");}
                if(zero){ret();break;}
                cycles-=6;
                break;
            case 0xc9:if (debug_mode){System.out.println("Ret");}
                ret();
                break;
            case 0xe0:if (debug_mode){System.out.println("Rpo");}
                if(!parity){ret();break;}
                cycles-=6;
                break;
            case 0xe8:if (debug_mode){System.out.println("Rpe");}
                if(parity){ret();break;}
                cycles-=6;
                break;
            case 0xd0:if (debug_mode){System.out.println("Rnc");}
                if(!carry){ret();break;}
                cycles-=6;
                break;
            case 0xd8:if (debug_mode){System.out.println("Rc");}
                if(carry){ret();break;}
                cycles-=6;
                break;
            case 0xf0:if (debug_mode){System.out.println("Rp");}
                if(!sign){ret();break;}
                cycles-=6;
                break;
            case 0xf8:if (debug_mode){System.out.println("Rm");}
                if(sign){ret();break;}
                cycles-=6;
                break;

                //Restarts
            //hard-wired call functions to set memory locations(used as interrupts)
            case 0xc7:
                if (debug_mode) { System.out.println("Rst 0"); }
                pc--;
                call(0);
                break;
            case 0xcf:
                if (debug_mode) { System.out.println("Rst 8"); }
                pc--;
                call(8);
                break;
            case 0xd7:
                if (debug_mode) { System.out.println("Rst 10"); }
                pc--;
                call(0x10);
                break;
            case 0xdf:
                if (debug_mode) { System.out.println("Rst 18"); }
                pc--;
                call(0x18);
                break;
            case 0xe7:
                if (debug_mode) { System.out.println("Rst 20"); }
                pc--;
                call(0x20);
                break;
            case 0xef:
                if (debug_mode) { System.out.println("Rst 28"); }
                pc--;
                call(0x28);
                break;
            case 0xf7:
                if (debug_mode) { System.out.println("Rst 30"); }
                pc--;
                call(0x30);
                break;
            case 0xff:
                if (debug_mode) { System.out.println("Rst 38"); }
                pc--;
                call(0x38);
                break;

            //NOP
            //does nothing or reserved for later cpus
            case 0x0:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x08:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x10:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x18:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x20:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x28:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x30:if (debug_mode){System.out.println("Nop");}
                break;
            case 0x38:if (debug_mode){System.out.println("Nop");}
                break;
            case 0xcb:if (debug_mode){System.out.println("Nop");}
                break;
            case 0xd9:if (debug_mode){System.out.println("Nop");}
                break;
            case 0xdd:if (debug_mode){System.out.println("Nop");}
                break;
            case 0xed:if (debug_mode){System.out.println("Nop");}
                break;
            case 0xfd:if (debug_mode){System.out.println("Nop");}
                break;
            //IO and Special Group
            //special(complicaded and unique) instructions
            //use 1 8-bit register to represent 2 4-bit numbers
            case 0x27:if (debug_mode){System.out.println("Daa (skipped)");}//Here be dragons
                a&=0xff;
                if(aux_carry|(a&0xf)>9){a+=6;aux_carry=true;}else{aux_carry=false;}
                if(carry|a>>4>9){a+=6<<4;carry=true;}else{carry=false;}
                a&=0xff;
                check_parity(a);
                check_zero(a);
                check_sign(a);
                break;
            case 0x76:if (true){System.out.println("Hlt (skipped)");}
                break;
            //interface with external hardware
            case 0xd3:if (debug_mode){System.out.println("Out,("+d8+")"+a+" (skipped)");}
                ports.out(a,d8);
                pc++;
                break;
            case 0xdb:if (debug_mode){System.out.println("In,("+d8+") (unfinished)");}
                a=ports.in(d8);
                pc++;
                break;
                //enable/ disable interrupts
            case 0xf3:if (debug_mode){System.out.println("Di");}
                interrupt_enabled=false;
            case 0xfb:if (debug_mode){System.out.println("El");}
                interrupt_enabled=true;
                break;
                //if not found
            default:not_implemented(opcode,d8,op3);
        }
    }
    public void save_state(){
        state=Arrays.copyOf(memory,memory.length+16);
        state[memory.length+1]=a;
        state[memory.length+2]=b;
        state[memory.length+3]=c;
        state[memory.length+4]=d;
        state[memory.length+5]=e;
        state[memory.length+6]=h;
        state[memory.length+7]=l;
        state[memory.length+8]=sp;
        if(carry){state[memory.length+9]=1;}
        if(aux_carry){state[memory.length+10]=1;}
        if(zero){state[memory.length+11]=1;}
        if(parity){state[memory.length+12]=1;}
        if(sign){state[memory.length+13]=1;}
        if(interrupt_enabled){state[memory.length+14]=1;}
        state[memory.length+15]=pc;
        state[memory.length+16]=cycles;
    }
    public void load_state(){
        memory=(Arrays.copyOf(state,memory.length));
        a=state[memory.length+1];
        b=state[memory.length+2];
        c=state[memory.length+3];
        d=state[memory.length+4];
        e=state[memory.length+5];
        h=state[memory.length+6];
        l=state[memory.length+7];
        sp=state[memory.length+8];
        carry=state[memory.length+9]==1;
        aux_carry=state[memory.length+10]==1;
        zero=state[memory.length+11]==1;
        parity=state[memory.length+12]==1;
        sign=state[memory.length+13]==1;
        interrupt_enabled=state[memory.length+14]==1;
        pc=state[memory.length+15];
        cycles=state[memory.length+16];
    }
    //pause program
    public void sleep(long x){
        try {
            Thread.sleep(x);
        }
        catch(Exception e) {
            // this part is executed when an exception (in this example InterruptedException) occurs
        }
    }
    //return int formated as hexidecimal number
    public String hex(int x){
        String r=(String.format("%x",x));
        if (r.length()==1){r="0"+r;}
        return r;
    }
    //debug info for unimplemented opccode
    private void not_implemented(int opcode, int d8,int op3){
        System.out.println("Opcode not implemented "+ hex(opcode)+" at "+tc);
        System.out.println("True counter:"+tc);
        System.out.println("    Program counter:"+hex(pc));
        //System.out.print("    "+hex(a)+hex(get_f())+" "+hex(b)+hex(c)+" "+hex(d)+hex(e)+" "+hex(h)+hex(l)+" "+hex(sp)+" ");
        if (zero){System.out.print("z");}
        if (sign){System.out.print("s");}
        if (parity){System.out.print("p");}
        if (carry){System.out.print("c");}
        System.out.println(" ");
        System.out.println("    Opcode:"+hex(opcode)+"   "+hex(d8)+" "+hex(op3));
        System.out.print("    ");
        sleep(5000);
        System.exit(0);
    }
    //adds register to accumulator and checks flags
    private void add(int x){
        aux_carry = (((a+x) ^ x ^ a) & 0x10) != 0;
        a+=x;
        check_carry(a);
        if (a>=256){a-=256;}
        check_zero(a);
        check_sign(a);
        check_parity(a);
    }
    //subtracts register from accumulator and checks flags
    private void sub(int x){
        aux_carry = (((a-x) ^ x ^ a) & 0x10) != 0;
        a-=x;
        check_carry(a);
        if(a<0){a+=256;}
        check_zero(a);
        check_sign(a);
        check_parity(a);
    }
    //subtracts register from accumulator with carry flag and checks flags
    private void sbb(int x){
        z=a;
        a-=x;
        if (carry){a--;}
        aux_carry = ((a ^ x ^ z) & 0x10) != 0;

        check_carry(a);
        if(a<0){a+=256;}
        check_zero(a);
        check_sign(a);
        check_parity(a);

    }
    //bitwise ands reister to accumulator and checks flags
    private void ana(int x){
        a&=x;
        check_zero(a);
        check_sign(a);
        check_parity(a);
        check_carry(a);
    }
    //bitwise not ors register to accumulator and checks flags
    private void xra(int x){
        a^=x;
        check_zero(a);
        check_sign(a);
        check_parity(a);
        check_carry(a);
    }
    //bitwise ors register to accumulator and checks flags
    private void ora(int x){
        a|=x;
        check_zero(a);
        check_sign(a);
        check_parity(a);
        check_carry(a);
    }
    //compares register to accumulator and checks flags
    private void cmp(int x){
        z=a-x;
        aux_carry = ((z ^ x ^ a) & 0x10) != 0;
        check_carry(z);
        if(z<0){z+=256;}
        check_zero(z);
        check_sign(z);
        check_parity(z);
    }
    //add register to accumulator with carry and checks flags
    private void adc(int x){
        z=a;
        a+=x;
        aux_carry = ((a ^ x ^ z) & 0x10) != 0;
        if (carry){a++;}
        check_carry(a);
        if(a>255){a&=0xff;}
        check_zero(a);
        check_sign(a);
        check_parity(a);
    }
    //jumps to memory location
    private void jump(int x){
        pc=x-1;
    }
    //calls memory location
    private void call(int x){
        pc++;
        memory[(sp-1)]=pc>>8;
        memory[(sp-2)]=(pc)&0xff;
        pc=x-1;
        sp-=2;
    }
    //returns from call function
    private void ret(){
        pc=((memory[sp+1]<<8)+memory[sp]-1);
        sp+=2;
    }
    //shortcuts to check flags
    private void check_zero(int x){
        zero = (x & 0xff)==0;
    }
    private void check_sign(int x){
        sign = (x & 0x80)!=0;
    }
    private void check_parity(int x){
        parity = par_table[x]==1;
    }
    private void check_carry(int x){
        carry =(x & 0b100000000) != 0;
    }
    //returns flags as 8 bit number
    private int get_f(){
        int x=0;
        if(sign){x+=0b10000000;}
        if(zero){x+=0b1000000;}
        if(aux_carry){x+=0b10000;}
        if(parity){x+=0b100;}
        if(carry){x+=0b1;}
        return x;
    }
    private void write_memory(int adr, int value){
        if((h<<8)+l>=0x2000|cpm_mode) {
            memory[adr] = value;
        }

    }
}
