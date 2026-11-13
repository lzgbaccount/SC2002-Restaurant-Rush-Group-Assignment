public class Waiter extends Staff {
    
    public Waiter() {
        super("Waiter");
    }

    public void seat(Customer customer, Table table) {
        requireCanAct();
        customer.seatAt(table);
        markActed();
    }

    public Order takeOrder(Customer customer, HappyHourPowerUp powerUp) {
        requireCanAct();
        MenuItem item = customer.choosefood();
        Order order = customer.placeOrder(item, powerUp);
        markActed();
        return order;
    }

    public void serve(Order order, int currentTurn) {
        requireCanAct();
        if(!order.isServable(currentTurn)){
            throw new IllegalStateException("Order is not ready to be served this turn");
        }
        order.getCustomer().markAsServed(currentTurn);
        markActed();
    }

}
