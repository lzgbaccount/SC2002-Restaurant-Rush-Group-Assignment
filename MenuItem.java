public class MenuItem {
    private final String name;
    private final float price;
    private final int prepUnit;

    public MenuItem(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Menu item name cannot be null");
        }
        switch (name.toLowerCase()){
            case "salad":
                this.name = "Salad";
                this.price = 9.00f;
                this.prepUnit =1;
                break;
            case "burger":
                this.name = "Burger";
                this.price = 15.00f;
                this.prepUnit =1;
                break;
            case "pasta":
                this.name = "Pasta";
                this.price = 18.00f;
                this.prepUnit =2;
                break;
            default:
                throw new IllegalArgumentException("Invalid menu item: " + name);
        }
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getPrepUnit() {
        return prepUnit;
    }
}
