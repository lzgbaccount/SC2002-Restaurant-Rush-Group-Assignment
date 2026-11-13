public class CriticCustomer extends Customer {
    private static final int SATISFACTION_LOSS = 12;
    private static final String PREFERRED_DISH = "Pasta";

    public CriticCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public String getTypeName() {
        return "Critic";
    }

    @Override
    protected int getSatisfactionLoss() {
        return SATISFACTION_LOSS;
    }

    @Override
    public MenuItem choosefood() {
        return new MenuItem(PREFERRED_DISH);
    }
}
