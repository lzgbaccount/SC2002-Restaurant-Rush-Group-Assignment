import java.util.List;
import java.util.Scanner;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class ConsoleUI {

    private final GameController game;
    private final Scanner in;
    private final boolean echoInput;   

    public ConsoleUI(GameController game, Scanner in, boolean echoInput) {
        this.game = game;
        this.in = in;
        this.echoInput = echoInput;
    }

    public void run() {
        printWelcome();
        try {
            while (!game.isGameOver()) {
                game.startTurn();
                printTurnHeader();
                printArrivals();
                printState();

                waiterPhase();
                chefPhase();

                printEvents(game.endTurn());
            }
            printFinalResult();
        } catch (StopGame stop) {
            System.out.println(stop.getMessage() + " Game stopped at turn " + game.getCurrentTurn() + ".");
        }
    }

    private void waiterPhase() {
        runMenu("WAITER", "Waiter choice: ", new String[] {
                "Seat the next customer in the queue",
                "Take an order",
                "Serve a READY dish",
                "Wait (do nothing this turn)" },
                choice -> {
                    switch (choice) {
                        case 1: return seatMenu();
                        case 2: return takeOrderMenu();
                        case 3: return serveMenu();
                        default: return attempt(() -> game.waiterAct(WaiterAction.WAIT, null));
                    }
                });
    }

    private String seatMenu() {
        List<Customer> waiting = game.getWaitingCustomers();
        if (waiting.isEmpty()) {
            System.out.println("  Nobody is in the waiting queue.");
            return null;
        }
        Customer next = waiting.get(0);
        System.out.println("  Seat " + GameController.label(next) + " (front of queue) at which table?");
        Integer table = chooseTable(t -> describeTableForSeating(t));
        if (table == null) return null;
        return attempt(() -> game.waiterAct(WaiterAction.SEAT, table));
    }

    private String takeOrderMenu() {
        System.out.println("  Take the order at which table?");
        return actOnCustomerAtTable(t -> describeTableForOrdering(t),
                c -> game.waiterAct(WaiterAction.TAKE_ORDER, c.getId()));
    }

    private String serveMenu() {
        System.out.println("  Serve the dish to which table?");
        return actOnCustomerAtTable(t -> describeTableForServing(t),
                c -> game.waiterAct(WaiterAction.SERVE, c.getId()));
    }

   
    private void chefPhase() {
        runMenu("CHEF", "Chef choice: ", new String[] {
                "Cook one preparation unit of an order",
                "Wait (do nothing this turn)" },
                choice -> {
                    if (choice == 1) {
                        System.out.println("  Cook for which table?");
                        return actOnCustomerAtTable(t -> describeTableForCooking(t), c -> game.chefCook(c.getId()));
                    }
                    return attempt(() -> game.chefWait());
                });
    }

    private void runMenu(String staff, String prompt, String[] options, IntFunction<String> handleChoice) {
        while (true) {
            System.out.println();
            System.out.println(staff + " - choose one task:");
            for (int i = 0; i < options.length; i++) System.out.println("  " + (i + 1) + ". " + options[i]);
            int choice = readNumber(prompt);
            if (choice < 1 || choice > options.length) {
                System.out.println("  Please enter a number from 1 to " + options.length + ".");
                continue;
            }
            String result = handleChoice.apply(choice);
            if (result == null) continue;
            System.out.println("  >> " + result);
            return;
        }
    }

    private static class StopGame extends RuntimeException {
        StopGame(String message) { super(message); }
    }

    private interface TableDescriber { String describe(Table t); }
    private interface CustomerAction { String apply(Customer c); }
    private interface Action { String run(); }

    private Integer chooseTable(TableDescriber describer) {
        List<Table> tables = game.getTables();
        for (Table t : tables) {
            System.out.println("    " + t.getNumber() + ". Table " + t.getNumber() + " - " + describer.describe(t));
        }
        System.out.println("    0. Back");
        while (true) {
            int n = readNumber("  Table number: ");
            if (n == 0) return null;
            for (Table t : tables) if (t.getNumber() == n) return n;
            System.out.println("  There is no table " + n + ". Enter a number from the list.");
        }
    }

    private String actOnCustomerAtTable(TableDescriber describer, CustomerAction action) {
        while (true) {
            Integer number = chooseTable(describer);
            if (number == null) return null;
            Customer c = tableByNumber(number).getCustomer();
            if (c == null) {
                System.out.println("  Table " + number + " is empty. Choose another table, or 0 to go back.");
                continue;
            }
            return attempt(() -> action.apply(c));
        }
    }

    private String attempt(Action action) {
        try {
            return action.run();
        } catch (ActionRejectedException e) {
            System.out.println("  Not allowed: " + e.getMessage());
            System.out.println("  The task was not used up - choose again.");
            return null;
        }
    }

    private Table tableByNumber(int number) {
        for (Table t : game.getTables()) if (t.getNumber() == number) return t;
        return null;
    }


    private String describeTableForSeating(Table t) {
        Customer c = t.getCustomer();
        return c == null ? "free" : "occupied by " + GameController.label(c);
    }

    private String describeTableForOrdering(Table t) {
        Customer c = t.getCustomer();
        if (c == null) return "empty";
        if (c.getStatus() == CustomerStatus.SEATED) return GameController.label(c) + " is ready to order";
        return GameController.label(c) + " - " + describeOrder(c.getOrder());
    }

    private String describeTableForServing(Table t) {
        return describeOrderAtTable(t, c -> c.getStatus() == CustomerStatus.ORDERED && c.getOrder().isServable(game.getCurrentTurn()), "  <- can be served");
    }

    private String describeTableForCooking(Table t) {
        return describeOrderAtTable(t, c -> c.getOrder().canBeCooked(), "  <- needs cooking");
    }

    private String describeOrderAtTable(Table t, Predicate<Customer> showHint, String hint) {
        Customer c = t.getCustomer();
        if (c == null) return "empty";
        if (c.getOrder() == null) return GameController.label(c) + " has not ordered yet";
        String s = GameController.label(c) + " - " + describeOrder(c.getOrder());
        return showHint.test(c) ? s + hint : s;
    }

    private int readNumber(String label) {
        while (true) {
            System.out.print(label);
            if (!in.hasNextLine()) {
                System.out.println();
                throw new StopGame("Input ended.");
            }
            String line = in.nextLine().trim();
            if (echoInput) System.out.println(line);
            if (line.isEmpty()) continue;
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  '" + line + "' is not a number. Please enter one of the numbers shown.");
            }
        }
    }



    private void printWelcome() {
        Settings s = game.getSettings();
        System.out.println("=== RESTAURANT RUSH ===");
        System.out.println("You are the manager. " + s.getMaxTurns() + " turns, " + s.getTableCount() + " tables, 1 Waiter, 1 Chef, 1 Cashier.");
        System.out.println("Goal after turn " + s.getMaxTurns() + ": at least " + s.getMinPaidCustomers() + " paid customers, revenue at least "
                + GameController.money(s.getMinRevenueCents()) + ", average satisfaction of paid customers at least "
                + s.getMinAverageSatisfaction() + ".");
        System.out.println("Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units). Customers choose their own food.");
        System.out.println("Each turn the Waiter acts first, then the Chef. Answer every question with a number.");
    }

    private void printTurnHeader() {
        System.out.println();
        System.out.println("==================== TURN " + game.getCurrentTurn() + " / " + game.getMaxTurns() + " ====================");
    }

  
    private void printState() {
        Result r = game.getResult();
        System.out.println("Revenue: " + r.getRevenueText() + " | Avg satisfaction (paid): " + r.getAverageSatisfactionText()
                + " | Paid: " + r.getPaidCount() + " | Left unhappy: " + r.getUnhappyDepartures());

        List<Customer> waiting = game.getWaitingCustomers();
        StringBuilder q = new StringBuilder("Waiting queue: ");
        if (waiting.isEmpty()) q.append("(empty)");
        for (int i = 0; i < waiting.size(); i++) {
            Customer c = waiting.get(i);
            q.append(i == 0 ? "" : ", ").append(GameController.label(c));
        }
        System.out.println(q);

        System.out.println("Tables:");
        for (Table t : game.getTables()) {
            Customer c = t.getCustomer();
            if (c == null) {
                System.out.println("  Table " + t.getNumber() + ": free");
            } else {
                System.out.println("  Table " + t.getNumber() + ": " + GameController.label(c) + " | satisfaction " + c.getSatisfaction()
                        + " | " + c.getStatus() + " | order: " + describeOrder(c.getOrder()));
            }
        }
        printAvailableTasks();
    }

    private String describeOrder(Order o) {
        if (o == null) return "not taken yet";
        String s = o.getMenuItem().getName() + " " + o.getStatus();
        if (o.getStatus() == OrderStatus.PREPARING) s += " (" + o.getRemainingUnits() + " unit(s) left)";
        return s;
    }

    private void printAvailableTasks() {
        int turn = game.getCurrentTurn();
        StringBuilder free = new StringBuilder(), order = new StringBuilder(), serve = new StringBuilder(), cook = new StringBuilder();
        for (Table t : game.getTables()) {
            Customer c = t.getCustomer();
            if (c == null) { free.append(" ").append(t.getNumber()); continue; }
            Order o = c.getOrder();
            if (c.getStatus() == CustomerStatus.SEATED) order.append(" ").append(t.getNumber());
            if (c.getStatus() == CustomerStatus.ORDERED && o.isServable(turn)) serve.append(" ").append(t.getNumber());
            if (o != null && o.canBeCooked()) cook.append(" ").append(t.getNumber());
        }
        boolean canSeat = free.length() > 0 && !game.getWaitingCustomers().isEmpty();

        System.out.println("Staff tasks available:");
        System.out.println("  Waiter: seat " + (canSeat ? "(free table" + free + ")" : "(no)")
                + " | take order " + (order.length() == 0 ? "(none)" : "(table" + order + ")")
                + " | serve " + (serve.length() == 0 ? "(none)" : "(table" + serve + ")")
                + " | wait");
        System.out.println("  Chef:   cook " + (cook.length() == 0 ? "(none)" : "(table" + cook + ")") + " | wait");
        System.out.println("  Cashier: automatic (one READY_TO_PAY customer per turn, oldest first)");
    }

    private void printArrivals() {
        for (Customer c : game.getCustomers()) {
            if (c.getArrivalTurn() == game.getCurrentTurn()) {
                System.out.println("** NEW CUSTOMER AT QUEUE ** " + GameController.label(c));
            }
        }
    }

    private void printEvents(List<String> events) {
        for (String e : events) System.out.println("  * " + e);
    }
    
    private void printFinalResult() {
        Result r = game.getResult();
        Settings s = game.getSettings();
        System.out.println();
        System.out.println("==================== GAME OVER ====================");
        System.out.println("Paid customers:   " + r.getPaidCount() + "  (target >= " + s.getMinPaidCustomers() + ") " + tick(game.paidTargetMet()));
        System.out.println("Revenue:          " + r.getRevenueText() + "  (target >= " + GameController.money(s.getMinRevenueCents()) + ") " + tick(game.revenueTargetMet()));
        System.out.println("Avg satisfaction: " + r.getAverageSatisfactionText() + "  (target >= " + s.getMinAverageSatisfaction() + ") " + tick(game.satisfactionTargetMet()));
        System.out.println("Served: " + r.getServedCount() + " | Unhappy departures: " + r.getUnhappyDepartures());
        System.out.println();
        System.out.println(r.getOutcome() == GameResult.VICTORY ? "*** VICTORY! ***" : "*** DEFEAT ***");
    }

    private static String tick(boolean ok) {
        return ok ? "[met]" : "[NOT met]";
    }
}
