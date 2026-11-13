public class VIPCustomer extends Customer {
    private static final int SATISFACTION_LOSS = 5;
    private static final int DISCOUNT_PERCENT = 10;

    public VIPCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public String getTypeName() {
        return "VIP";
    }

    @Override
    protected int getSatisfactionLoss() {
        return SATISFACTION_LOSS;
    }


    @Override
    protected int calculateBill(MenuItem item) {
        return MenuItem.percentOf(item.getPriceCents(), 100 - DISCOUNT_PERCENT);
    }
}
