import java.util.HashMap;

//used to make adding support for more games later easyer
abstract class ports {
    abstract int in(int x);
    abstract void out(int x, int y);
    public HashMap<String, Integer> key;
    ports(){

    }
}
