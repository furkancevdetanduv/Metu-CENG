import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    private static final String title = "Gold Wars";
    private static final int windowWidth = 1650;
    private static final int windowHeight = 1000;

    private static final GoldPrice goldPrice = new GoldPrice(588, 62);

    private static final Random randomGenerator = new Random(1234);
    private static final int upperLineY = 100;
    private static List<Country> countryList = new ArrayList<Country>(); // all countries added in a single country list because they will be used together
    private static BufferedImage flagChina;
    private static BufferedImage flagRussia;
    private static BufferedImage flagTurkey;
    private static BufferedImage flagIsrael;
    private static BufferedImage flagUSA;
    private static File currentDirectory;
    private static File imageFile;
    private static Country china;
    private static Country russia;
    private static Country turkey;
    private static Country israel;
    private static Country USA;

    static  {
        // TODO: Here, you can initialize the fields you have declared
        try{
            currentDirectory = new File(System.getProperty("user.dir"));

            imageFile = new File(currentDirectory.getParent()+"/images/china.jpg");
            flagChina = new BufferedImage(10, 10,BufferedImage.TYPE_INT_ARGB);
            flagChina = ImageIO.read(imageFile);

            imageFile = new File(currentDirectory.getParent()+"/images/russia.jpg");
            flagRussia = new BufferedImage(10, 10,BufferedImage.TYPE_INT_ARGB);
            flagRussia = ImageIO.read(imageFile);

            imageFile = new File(currentDirectory.getParent()+"/images/turkey.jpg");
            flagTurkey = new BufferedImage(10, 10,BufferedImage.TYPE_INT_ARGB);
            flagTurkey = ImageIO.read(imageFile);

            imageFile = new File(currentDirectory.getParent()+"/images/israel.jpg");
            flagIsrael = new BufferedImage(10, 10,BufferedImage.TYPE_INT_ARGB);
            flagIsrael = ImageIO.read(imageFile);

            imageFile = new File(currentDirectory.getParent()+"/images/usa.jpg");
            flagUSA = new BufferedImage(10, 10,BufferedImage.TYPE_INT_ARGB);
            flagUSA = ImageIO.read(imageFile);
        }
        catch (Exception e){
            System.out.println(e);
        }

        china = new Country(1275,800, flagChina,"China",50,1000);
        russia = new Country(1000,800,flagRussia, "Russia",50,1000);
        turkey = new Country(725,800,flagTurkey, "Turkey",50,1000);
        israel = new Country(450,800, flagIsrael,"Israel",50,1000);
        USA = new Country(175,800, flagUSA,"USA",50,1000);

        countryList.add(USA);
        countryList.add(israel);
        countryList.add(turkey);
        countryList.add(russia);
        countryList.add(china);
    }

    // getters
    public static String getTitle() { return title; }
    public static int getWindowWidth() { return windowWidth; }
    public static int getWindowHeight() { return windowHeight; }
    public static List<Country> getCountryList(){return countryList;}

    // getter
    public static GoldPrice getGoldPrice() { return goldPrice; }

    // getters
    public static Random getRandomGenerator() { return randomGenerator; }
    public static int getUpperLineY() { return upperLineY; }

    public static void stepAllEntities() {
        if (randomGenerator.nextInt(200) == 0) goldPrice.step();
        // TODO
    }
}