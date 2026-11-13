public class Table{
    private int number;
    private Customer customer;

    public Table(int number){
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    public Customer getCustomer(){
        return customer;
    }

    public void occupy(Customer customer){
        if(customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        if (isOccupied()) {
            throw new IllegalStateException("Table " + number + " is already occupied");
        }

        this.customer = customer; 
    }

    public void vacate(){
        customer = null;
    }

    public boolean isOccupied(){
        return customer != null;
    }


}