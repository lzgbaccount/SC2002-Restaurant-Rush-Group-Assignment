public class Settings {
    private final int maxTurns;
    private final int tableCount;
    private final int minPaidCustomers;
    private final int minRevenueCents;
    private final int minAverageSatisfaction;

    public Settings(int maxTurns, int tableCount, int minPaidCustomers, int minRevenueCents, int minAverageSatisfaction) {
        this.maxTurns = maxTurns;
        this.tableCount = tableCount;
        this.minPaidCustomers = minPaidCustomers;
        this.minRevenueCents = minRevenueCents;
        this.minAverageSatisfaction = minAverageSatisfaction;
    }

    public static Settings base() {
        return new Settings(18,
                2, 4, 4500,
                60);
    }

    public int getMaxTurns() { return maxTurns; }
    public int getTableCount() { return tableCount; }
    public int getMinPaidCustomers() { return minPaidCustomers; }
    public int getMinRevenueCents() { return minRevenueCents; }
    public int getMinAverageSatisfaction() { return minAverageSatisfaction; }
}
