import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameController game = new GameController(Settings.base());
        boolean piped = System.console() == null;   
        new ConsoleUI(game, new Scanner(System.in), piped).run();
    }
}
