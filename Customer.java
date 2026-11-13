public class Customer {
    private int id;
    private static final int START_SATISFACTION = 100;
    private static final int LOSS_SATISFACTION=8;
    private static final String[] MENU= {"Salad","Burger","Pasta"};    
    private int satisfaction;
    private Table table;
    private int arrivalTurn;
    private CustomerStatus status;
    private Order order;
    private int servedonturn;
    private int readytopay;



    public Customer(int id, int arrivalTurn) {
        this.id = id;
        this.arrivalTurn = arrivalTurn;
        this.satisfaction = START_SATISFACTION;
        this.status = CustomerStatus.WAITING;
        this.servedonturn = -1;
        this.readytopay = -1;
    }

    public int getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public Order getOrder() {
        return order;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    private void requireStatus(CustomerStatus expectedStatus, String action) {
        if (status != expectedStatus) {
            throw new IllegalStateException("Customer must be " + expectedStatus + " to " + action);
        }
    }

    public void seatAt(Table table) {
        requireStatus(CustomerStatus.WAITING,"be seated");
        table.occupy(this);
        this.table = table;
        this.status = CustomerStatus.SEATED;
        
    }

    public MenuItem choosefood() {
       return new MenuItem(MENU[(id-1)%MENU.length]);
    }

    public Order placeOrder(MenuItem item){
        requireStatus(CustomerStatus.SEATED,"place an order");
        this.order = new Order(this, item);
        this.status = CustomerStatus.ORDERED;
        return order;
    }

    public void markAsServed(int currentTurn) {
        requireStatus(CustomerStatus.ORDERED,"be served");
        order.markAsServed();
        this.status = CustomerStatus.SERVED;
        this.servedonturn = currentTurn;
    }

    public void updateEating(int currentTurn) {
        if (status == CustomerStatus.SERVED && servedonturn < currentTurn){
            this.status = CustomerStatus.ATE;
            readytopay = currentTurn;
        }
    }

    public boolean canPay(int currentTurn) {
        return status == CustomerStatus.ATE && readytopay < currentTurn;
    }

    public void pay() {
        requireStatus(CustomerStatus.ATE,"pay");
        order.markAsPaid();
        this.status = CustomerStatus.PAID;
        if (table != null) {
            table.vacate();
            table = null;
        }
    }

    public boolean isLosingSatisfaction(){
        return status==CustomerStatus.WAITING || status==CustomerStatus.SEATED || status==CustomerStatus.ORDERED;
    }

    public void applyWaitingPenalty() {
        if (isLosingSatisfaction()) {
            satisfaction -= LOSS_SATISFACTION;
            if (satisfaction < 0) {
                satisfaction = 0;
            }
        }
    }

    public boolean isLeaving() {
        return isLosingSatisfaction() && satisfaction == 0;
    }

    public void leave() {
        if (!isLeaving()) {
            throw new IllegalStateException("Customer is not leaving yet");
        }
        if (order != null) {
            order.cancel();
        }
        if (table != null) {
            table.vacate();
            table = null;
        }
        this.status = CustomerStatus.LEFT;
    }






    

 


}