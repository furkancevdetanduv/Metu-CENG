import java.awt.*;

public class BuyOrder extends Order {
    public BuyOrder(double x, double y, Position destination, int amount, int speed,String countryName) {
        super(x, y,destination,amount,speed,countryName);
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.drawOval(position.getIntX(), position.getIntX(),10,10 );
        g2d.drawString(Integer.toString(amount), position.getIntX()-10, position.getIntY()-10);
        g2d.setColor(Color.BLACK);
        switch (countryName){
            case "Turkey":
                g2d.drawString("TR", position.getIntX(), position.getIntY());
            case "Israel":
                g2d.drawString("IL", position.getIntX(), position.getIntY());
            case "China":
                g2d.drawString("CN", position.getIntX(), position.getIntY());
            case "Russia":
                g2d.drawString("RU", position.getIntX(), position.getIntY());
            case "USA":
                g2d.drawString("US", position.getIntX(), position.getIntY());
        }

    }

    @Override
    public void step() {

    }
    // TODO
}