public class Chef extends Staff {
    
    public Chef() {
        super("Chef");
    }

    public void cook(Order order, int currentTurn) {
        requireCanAct();
        order.advanceTurn(currentTurn);
        markActed();
    }

}
