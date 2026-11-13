public class Result {
    private final int servedCount;
    private final int unhappyDepartures;
    private final int revenueCents;
    private final int paidCount;
    private final int satisfactionSum;
    private final GameResult outcome;

    public Result(int servedCount, int unhappyDepartures, int revenueCents, int paidCount,
                  int satisfactionSum, GameResult outcome) {
        this.servedCount = servedCount;
        this.unhappyDepartures = unhappyDepartures;
        this.revenueCents = revenueCents;
        this.paidCount = paidCount;
        this.satisfactionSum = satisfactionSum;
        this.outcome = outcome;
    }

    public int getServedCount() { return servedCount; }
    public int getUnhappyDepartures() { return unhappyDepartures; }
    public int getPaidCount() { return paidCount; }
    public GameResult getOutcome() { return outcome; }

    public String getAverageSatisfactionText() {
        return paidCount == 0 ? "N/A" :
                String.format("%.1f", (double) satisfactionSum / paidCount);
    }

    public String getRevenueText() {
        return GameController.money(revenueCents);
    }
}
