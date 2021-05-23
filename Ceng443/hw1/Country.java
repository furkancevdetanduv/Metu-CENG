import java.awt.*;
import java.awt.image.BufferedImage;

public class Country extends Entity {
    private BufferedImage flag;
    private String countryName;
    private int gold;
    private int cash;
    private Font font = new Font("Verdana", Font.PLAIN, 20);

    public Country(double x, double y) {
        super(x, y);
    }
    public Country(double x, double y, BufferedImage flag ,String countryName, int gold, int cash){
        super(x,y);
        this.countryName = countryName;
        this.gold = gold;
        this.cash = cash;
        this.flag = flag;
    }
    //getters
    public int getCash() {
        return cash;
    }

    public int getGold() {
        return gold;
    }
    //setters
    public void setGold(int gold){
        this.gold = gold;
    }

    public void setCash(int cash){
        this.cash=cash;
    }

    public int getDynamicWorth(){
        return (int) (cash + gold * Common.getGoldPrice().getCurrentPrice());
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.drawImage((Image) flag,position.getIntX(),position.getIntY()-150,150,100,null);
        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString(countryName, position.getIntX(), position.getIntY());
        g2d.setColor(Color.YELLOW);
        g2d.drawString(String.format("%d Gold", gold), position.getIntX(), position.getIntY() + 25);
        g2d.setColor(Color.GREEN);
        g2d.drawString(String.format("%d Cash", cash), position.getIntX(), position.getIntY() + 50);
        g2d.setColor(Color.BLUE);
        g2d.drawString(String.format("Worth : %d", getDynamicWorth()), position.getIntX(), position.getIntY() + 75);
    }

    @Override
    public void step() {

    }

}