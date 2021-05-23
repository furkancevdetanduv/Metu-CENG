public class SellOrderFactory extends OrderFactory {
    // TODO
    public static Order generateOrder(double x,double y,String countryName) {
        Order order = null;
        int orderAmount = Common.getRandomGenerator().nextInt(5);
        int destinationX = Common.getRandomGenerator().nextInt(1650);
        int speed = Common.getRandomGenerator().nextInt(100);
        Position destination = new Position(destinationX, Common.getUpperLineY());
        order = new SellOrder(x, y, destination, orderAmount, speed, countryName);
        return order;
    }
}