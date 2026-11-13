public abstract class Staff {
    private final String role;
    private boolean hasActedThisTurn; 
    
    protected Staff(String role) {
        this.role = role; 
    }

    public String getRole(){
        return role;
    }

    public void resetTurn() {
        hasActedThisTurn = false; 
    }

    public boolean canAct() {
        return !hasActedThisTurn;
    }

    protected void markActed(){
        hasActedThisTurn = true;
    }

    protected void requireCanAct() {
        if(hasActedThisTurn) {
            throw new IllegalStateException(getRole() + " has already acted this turn.");
        }
    }

    public void waitTurn() {
        requireCanAct();
        markActed();
    }
}
