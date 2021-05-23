
public abstract class Order extends Entity {
    protected static Position destination;
    protected static int amount;
    protected static int speed;
    protected static String countryName;
    //private static Vector2d path;
    public Order(double x, double y, Position destination, int amount, int speed,String countryName) {
        super(x, y);
        this.destination = destination;
        this.amount = amount;
        this.speed = speed;
        this.countryName = countryName;
    }

    public static int getAmount() {
        return amount;
    }
}