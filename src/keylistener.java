import java.awt.event.*;
public class keylistener implements KeyListener{
    keylistener(){

    }
    //do whenever key is pressed
    public void keyPressed(KeyEvent e) {
        try
        {
            //adds key that is pressed to hashmap of pressed keys
            Main.i8080.key.put(KeyEvent.getKeyText(e.getKeyCode()), 1);
            //System.out.println(Main.i8080.key);
            //press f to pay respects
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("F")){System.exit(0); }
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("7")){
                Main.i8080.breakpoint_enabled=true;
                Main.i8080.breakpoint= (int)Main.i8080.tc+500;
                //System.exit(0);
            }
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("Q")){Main.max_fps=300;}
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("U")){Main.max_fps=30000;}
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("P")){
                Main.send_message("savestate",120);
                Main.i8080.save_state();
            }
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("L")){
                Main.send_message("loadstate",120);
                Main.i8080.load_state();
            }
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("M")){Main.f.repaint();}


        }
        catch(Exception f)
        {
            //error handling code
        }

    }
    //do whenever key is released
    public void keyReleased(KeyEvent e) {
        try
        {
            Main.i8080.key.put(KeyEvent.getKeyText(e.getKeyCode()), 0);
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("Q")){Main.max_fps=60;}
            if(KeyEvent.getKeyText(e.getKeyCode()).equals("U")){Main.max_fps=60;}

        }
        catch(Exception f)
        {
            //error handling code
        }

    }
    //do whenever key is typed
    public void keyTyped(KeyEvent e) {
        //l.setText("Key Typed");
    }
}
