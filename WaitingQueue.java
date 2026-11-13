import java.util.Queue;
import java.util.LinkedList;

public class WaitingQueue{
    private Queue<Customer> customers;

    public WaitingQueue(){
        customers = new LinkedList<>();
    }

    public void add(Customer customer){
        customers.offer(customer);
    }

    public Customer removeNext(){
        return customers.poll();
    }



}