public class Order {
    private final Customer customer;
    private final MenuItem menuItem;
    private int remainingUnits;
    private int readyonturn;
    private OrderStatus status;

    public Order(Customer customer, MenuItem menuItem) {
        if (customer == null || menuItem == null) {
            throw new IllegalArgumentException("Customer and MenuItem cannot be null");
        }
        this.customer = customer;
        this.menuItem = menuItem;
        this.remainingUnits = menuItem.getPrepUnit();
        this.readyonturn = -1;
        this.status = OrderStatus.PREPARING;
    }


    
    public Customer getCustomer() {
        return customer;
    }
    public MenuItem getMenuItem() {
        return menuItem;
    }
    public int getRemainingUnits() {
        return remainingUnits;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public float getBill() {
        return menuItem.getPrice();
    }

  
    public boolean canBeCooked(){
        return status == OrderStatus.PREPARING && remainingUnits > 0;
    }

    public boolean isServable(int currentTurn){
        return status == OrderStatus.READY && readyonturn < currentTurn;
    }

    public void advanceTurn(int currentTurn){
        if (!canBeCooked()) {
            throw new IllegalStateException("Order cannot be cooked");
        }
        remainingUnits--;
        if (remainingUnits == 0) {
            status = OrderStatus.READY;
            readyonturn = currentTurn;
        }
    }

    public void markAsServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Order is not ready to be served");
        }
        status = OrderStatus.SERVED;
    }

    public void markAsPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Order is not served yet");
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID || status == OrderStatus.SERVED) {
            throw new IllegalStateException("Cannot cancel a paid order");
        }
        status = OrderStatus.CANCELLED;
    }


}
