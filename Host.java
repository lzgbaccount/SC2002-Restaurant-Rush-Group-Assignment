public class Host extends Staff {

    public Host() {
        super("Host");
    }

    public void seat(Customer customer, Table table) {
        requireCanAct();
        customer.seatAt(table);
        markActed();
    }

}
