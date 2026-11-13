import java.util.List;

public class Cashier extends Staff {
    
    public Cashier() {
        super("Cashier");
    }

    public Customer payOldestReadyToPay(List<Customer> customers, int currentTurn){
        requireCanAct();
        Customer oldest = null;

        for(Customer c: customers){
            if(!c.canPay(currentTurn)){
                continue;
            }
            if(oldest == null || c.getArrivalTurn() < oldest.getArrivalTurn()) {
                oldest = c;  
            }
        }

        if(oldest == null) {
            return null;
        }

        oldest.pay();
        markActed();
        return oldest; 
    }
}
