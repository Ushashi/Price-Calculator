/**
 * Created by uchakraborty on 3/21/18.
 */
package com.company.integrationtest;
import com.company.PriceCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import java.io.*;
import java.util.Properties;

public class PriceCalculatorIntegrationTest {

    Properties prop = new Properties();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void setup() {

        String properties = "config.properties";

        InputStream input = getClass().getClassLoader().getResourceAsStream(properties);

        try {
            prop.load(input);
        }
        catch (IOException e) {
        }
    }

    @Test
    public void testCartWithOneItemQuantityZero() {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_0"), (prop.getProperty("BASE_PRICE_PATH"))};
        PriceCalculator.main(inputArgs);

        Assert.assertEquals(0 + "\n", out.toString());
    }

    @Test
    public void testCartWithOneItemMarkupZero() {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_1"), (prop.getProperty("BASE_PRICE_PATH"))};
        PriceCalculator.main(inputArgs);

        Assert.assertEquals(3800 + "\n", out.toString());
    }

    @Test
    public void testCartWithOneItem() throws Exception {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_2"), (prop.getProperty("BASE_PRICE_PATH"))};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(4560 + "\n", out.toString());

    }

    @Test
    public void testCartWithMoreThanOneItemOfDifferentKinds() {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_3"), (prop.getProperty("BASE_PRICE_PATH"))};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(9363 + "\n", out.toString());
    }

    @Test
    public void testCartWithMoreThanOneItemOfSameKind() {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_4"), (prop.getProperty("BASE_PRICE_PATH"))};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(9500 + "\n", out.toString());
    }

    @Test
    public void testCartWithOneItemWithDifferentOptions() {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_5"), (prop.getProperty("BASE_PRICE_PATH"))};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(11356 + "\n", out.toString());
    }

    @Test
    public void testCartWithOneItemWithNoOptions() {

        System.setOut(new PrintStream(out));

        String[] inputArgs = {prop.getProperty("CART_PATH_Test_6"), (prop.getProperty("BASE_PRICE_PATH"))};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(6000 + "\n", out.toString());
    }
}

