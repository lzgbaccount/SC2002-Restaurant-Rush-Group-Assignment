public class MenuItem {
    private final String name;
    private final int priceCents;   
    private final int prepUnit;

    public MenuItem(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Menu item name cannot be null");
        }
        switch (name.toLowerCase()){
            case "salad":
                this.name = "Salad";
                this.priceCents = 900;
                this.prepUnit =1;
                break;
            case "burger":
                this.name = "Burger";
                this.priceCents = 1500;
                this.prepUnit =1;
                break;
            case "pasta":
                this.name = "Pasta";
                this.priceCents = 1800;
                this.prepUnit =2;
                break;
            default:
                throw new IllegalArgumentException("Invalid menu item: " + name);
        }
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getPrepUnit() {
        return prepUnit;
    }

    public static int percentOf(int cents, int percent) {
        return (cents * percent + 50) / 100;
    }
}
