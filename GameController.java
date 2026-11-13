import java.util.ArrayList;
import java.util.List;


public class GameController {
    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

    private final Settings settings;
    private final WaitingQueue queue = new WaitingQueue();
    private final List<Table> tables = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();

    private int currentTurn = 0;
    private int revenueCents = 0;
    private int servedCount = 0;
    private int unhappyDepartures = 0;
    private int paidCount = 0;
    private int satisfactionSum = 0;
    private int nextCustomerId = 1;
    private boolean turnInProgress = false;

    public GameController(Settings settings) {
        this.settings = settings;
        for (int i = 1; i <= settings.getTableCount(); i++) tables.add(new Table(i));
    }

    
    public Settings getSettings() { return settings; }
    public int getCurrentTurn() { return currentTurn; }
    public int getMaxTurns() { return settings.getMaxTurns(); }
    public List<Table> getTables() { return tables; }
    public List<Customer> getCustomers() { return customers; }
    public boolean isGameOver() { return currentTurn >= settings.getMaxTurns() && !turnInProgress; }

    public List<Customer> getWaitingCustomers() {
        List<Customer> waiting = new ArrayList<>();
        for (Customer c : customers) if (c.getStatus() == CustomerStatus.WAITING) waiting.add(c);
        return waiting;
    }

    public Result getResult() {
        GameResult outcome = isGameOver() ? checkVictory() : null;
        return new Result(servedCount, unhappyDepartures, revenueCents, paidCount, satisfactionSum, outcome);
    }

    public boolean paidTargetMet() { return paidCount >= settings.getMinPaidCustomers(); }
    public boolean revenueTargetMet() { return revenueCents >= settings.getMinRevenueCents(); }
    public boolean satisfactionTargetMet() {
        return paidCount > 0 && satisfactionSum >= settings.getMinAverageSatisfaction() * paidCount;
    }

    private GameResult checkVictory() {
        return paidTargetMet() && revenueTargetMet() && satisfactionTargetMet() ? GameResult.VICTORY : GameResult.DEFEAT;
    }


    public void startTurn() {
        if (isGameOver() || turnInProgress) throw new IllegalStateException("Cannot start a turn now.");
        currentTurn++;
        turnInProgress = true;
        waiter.resetTurn();
        chef.resetTurn();
        cashier.resetTurn();

        for (int t : ARRIVAL_TURNS) {
            if (t == currentTurn) {
                Customer c = new Customer(nextCustomerId++, currentTurn);
                customers.add(c);
                queue.add(c);
            }
        }
    }

   
    public String waiterAct(WaiterAction action, Integer argument) {
        requireTurn();
        if (!waiter.canAct()) throw new ActionRejectedException("The Waiter has already acted this turn.");
        try {
            String message;
            switch (action) {
                case SEAT: message = seat(argument); break;
                case TAKE_ORDER: message = takeOrder(requireId(argument)); break;
                case SERVE: message = serve(requireId(argument)); break;
                default:
                    waiter.waitTurn();
                    message = "Waiter waits this turn.";
            }
            return message;
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ActionRejectedException(e.getMessage());
        }
    }

    private String seat(Integer tableNumber) {
        if (getWaitingCustomers().isEmpty()) throw new ActionRejectedException("Nobody is waiting for a table.");
        if (tableNumber == null) throw new ActionRejectedException("Choose a table number.");
        Table t = findTable(tableNumber);
        if (t.isOccupied()) throw new ActionRejectedException("Table " + t.getNumber() + " is occupied.");
        Customer c = nextWaitingFromQueue();
        waiter.seat(c, t);
        return "Waiter seats " + label(c) + " at Table " + t.getNumber() + ".";
    }

    private String takeOrder(int customerId) {
        Customer c = findCustomer(customerId);
        if (c.getStatus() != CustomerStatus.SEATED) {
            throw new ActionRejectedException("Customer #" + customerId + " is not seated and ready to order (" + c.getStatus() + ").");
        }
        Order o = waiter.takeOrder(c);
        return "Waiter takes the order of " + label(c) + ": " + o.getMenuItem().getName()
                + " (" + money(cents(o)) + ", " + o.getMenuItem().getPrepUnit() + " prep unit(s)).";
    }

    private String serve(int customerId) {
        Customer c = findCustomer(customerId);
        Order o = c.getOrder();
        if (c.getStatus() != CustomerStatus.ORDERED || o == null) {
            throw new ActionRejectedException("Customer #" + customerId + " has no order waiting to be served (" + c.getStatus() + ").");
        }
        if (!o.isServable(currentTurn)) {
            throw new ActionRejectedException("The order of Customer #" + customerId + " is not servable yet (status "
                    + o.getStatus() + "). A dish finished this turn can only be served next turn.");
        }
        waiter.serve(o, currentTurn);
        servedCount++;
        return "Waiter serves " + o.getMenuItem().getName() + " to " + label(c) + ". The customer starts eating.";
    }

  
    public String chefCook(int customerId) {
        requireTurn();
        requireWaiterFirst();
        if (!chef.canAct()) throw new ActionRejectedException("The Chef has already acted this turn.");
        Customer c = findCustomer(customerId);
        Order o = c.getOrder();
        if (o == null) throw new ActionRejectedException("Customer #" + customerId + " has not ordered yet.");
        if (!o.canBeCooked()) {
            throw new ActionRejectedException("The order of Customer #" + customerId + " cannot be cooked (status " + o.getStatus() + ").");
        }
        try {
            chef.cook(o, currentTurn);
        } catch (IllegalStateException e) {
            throw new ActionRejectedException(e.getMessage());
        }
        String msg = "Chef cooks one unit of " + label(c) + "'s " + o.getMenuItem().getName()
                + " (" + o.getRemainingUnits() + " unit(s) left).";
        if (o.getStatus() == OrderStatus.READY) msg += " The dish is READY; it can be served next turn at the earliest.";
        return msg;
    }

    public String chefWait() {
        requireTurn();
        requireWaiterFirst();
        if (!chef.canAct()) throw new ActionRejectedException("The Chef has already acted this turn.");
        chef.waitTurn();
        return "Chef waits this turn.";
    }

    
    public List<String> endTurn() {
        requireTurn();
        if (waiter.canAct() || chef.canAct()) {
            throw new ActionRejectedException("The Waiter and the Chef must each act or wait before the turn can end.");
        }
        List<String> events = new ArrayList<>();

    
        Customer paid = cashier.payOldestReadyToPay(customers, currentTurn);
        if (paid != null) {
            int bill = cents(paid.getOrder());
            revenueCents += bill;
            paidCount++;
            satisfactionSum += paid.getSatisfaction();  
            events.add("Cashier collects " + money(bill) + " from " + label(paid)
                    + " (satisfaction " + paid.getSatisfaction() + "). The table is free.");
        }
   
        for (Customer c : customers) {
            CustomerStatus before = c.getStatus();
            c.updateEating(currentTurn);
            if (before == CustomerStatus.SERVED && c.getStatus() == CustomerStatus.ATE) {
                events.add(label(c) + " finishes eating and is ready to pay.");
            }
        }
       
        for (Customer c : customers) {
            if (c.isLosingSatisfaction()) {
                c.applyWaitingPenalty();
                if (c.isLeaving()) {
                    c.leave();
                    unhappyDepartures++;
                    events.add(label(c) + " loses patience and leaves without paying. Any order is cancelled.");
                }
            }
        }
        turnInProgress = false;
        return events;
    }

   
    private void requireTurn() {
        if (!turnInProgress) throw new IllegalStateException("No turn is in progress.");
    }

    private void requireWaiterFirst() {
        if (waiter.canAct()) throw new ActionRejectedException("The Waiter acts before the Chef: choose the Waiter's task (or wait) first.");
    }

    private int requireId(Integer id) {
        if (id == null) throw new ActionRejectedException("This command needs a customer number.");
        return id;
    }

    private Customer findCustomer(int id) {
        for (Customer c : customers) if (c.getId() == id) return c;
        throw new ActionRejectedException("There is no customer #" + id + ".");
    }

    private Table findTable(int number) {
        for (Table t : tables) if (t.getNumber() == number) return t;
        throw new ActionRejectedException("There is no table " + number + ".");
    }


    private Customer nextWaitingFromQueue() {
        Customer c;
        do {
            c = queue.removeNext();
        } while (c != null && c.getStatus() != CustomerStatus.WAITING);
        return c;
    }

    private static int cents(Order o) {
        return Math.round(o.getBill() * 100);
    }

    static String label(Customer c) {
        return "Customer #" + c.getId();
    }

    static String money(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}
