public class HappyHourPowerUp {
    public static final int DURATION_TURNS = 2;
    public static final int DISCOUNT_PERCENT = 20;
    public static final int SATISFACTION_RECOVERY = 15;

    private boolean used = false;
    private int remainingTurns = 0;   
    public boolean isAvailable() {
        return !used;
    }

    public boolean isActive() {
        return remainingTurns > 0;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public void activate() {
        if (used) throw new IllegalStateException("Happy Hour Power Up can only be used once per game.");
        used = true;
        remainingTurns = DURATION_TURNS;
    }

    public int applyTo(int priceCents) {
        if (!isActive()) return priceCents;
        return MenuItem.percentOf(priceCents, 100 - DISCOUNT_PERCENT);
    }

    public void endTurn() {
        if (remainingTurns > 0) remainingTurns--;
    }
}
