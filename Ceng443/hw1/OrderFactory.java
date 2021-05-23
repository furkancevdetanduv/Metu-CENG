public abstract class OrderFactory {
    // TODO
    public static Order generateOrder(double x,double y,String countryName){
        Order order = null;
        order = Common.getRandomGenerator().nextBoolean() ? BuyOrderFactory.generateOrder(x,y,countryName)   :  SellOrderFactory.generateOrder(x,y,countryName) ; //decision of order type is done by random
        return order;
    }
}