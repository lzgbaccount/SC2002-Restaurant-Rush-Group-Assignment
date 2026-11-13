public class ComboMeal extends MenuItem {
    private static final int COMBO_PRICE_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(first.getName() + " + " + second.getName() + " Combo",
              comboPrice(first, second),
              first.getPrepUnit() + second.getPrepUnit());
        this.first = first;
        this.second = second;
    }

    private static int comboPrice(MenuItem first, MenuItem second) {
        if (first == null || second == null) throw new IllegalArgumentException("A combo needs two menu items");
        int combined = first.getPriceCents() + second.getPriceCents();
        return MenuItem.percentOf(combined, COMBO_PRICE_PERCENT);
    }
}
