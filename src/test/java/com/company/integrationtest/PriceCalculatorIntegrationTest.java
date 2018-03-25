/**
 * Created by uchakraborty on 3/21/18.
 */
package com.company.integrationtest;
import com.company.PriceCalculator;
import org.junit.Assert;
import org.junit.Test;
import java.io.*;


public class PriceCalculatorIntegrationTest {

    public static final String CART_PATH_Test_0 = "/Users/uchakraborty/ShoppingCartSchemas/cart-0.json";
    public static final String CART_PATH_Test_1 = "/Users/uchakraborty/ShoppingCartSchemas/cart-4560.json";
    public static final String CART_PATH_Test_2 = "/Users/uchakraborty/ShoppingCartSchemas/cart-9363.json";
    public static final String CART_PATH_Test_3 = "/Users/uchakraborty/ShoppingCartSchemas/cart-9500.json";
    public static final String CART_PATH_Test_4 = "/Users/uchakraborty/ShoppingCartSchemas/cart-11356.json";
    public static final String BASE_PRICE_PATH  = "/Users/uchakraborty/ShoppingCartSchemas/base-prices.json";

    ByteArrayOutputStream out = new ByteArrayOutputStream();


    @Test
    public void testEmptyCart() {

        System.setOut(new PrintStream(out));
        String[] inputArgs = {CART_PATH_Test_0, BASE_PRICE_PATH};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(0 + "\n", out.toString());
    }

    @Test
    public void testCartWithOneItem() throws Exception {

        System.setOut(new PrintStream(out));
        String[] inputArgs = {CART_PATH_Test_1, BASE_PRICE_PATH};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(4560 + "\n", out.toString());

    }

    @Test
    public void testCartWithMoreThanOneItemOfDifferentKinds() {

        System.setOut(new PrintStream(out));
        String[] inputArgs = {CART_PATH_Test_2, BASE_PRICE_PATH};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(9363 + "\n", out.toString());
    }

    @Test
    public void testCartWithMoreThanOneItemOfSameKind() {

        System.setOut(new PrintStream(out));
        String[] inputArgs = {CART_PATH_Test_3, BASE_PRICE_PATH};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(9500 + "\n", out.toString());
    }

    @Test
    public void testCartWithOneItemWithDifferentOptions() {

        System.setOut(new PrintStream(out));
        String[] inputArgs = {CART_PATH_Test_4, BASE_PRICE_PATH};

        PriceCalculator.main(inputArgs);

        Assert.assertEquals(11356 + "\n", out.toString());
    }
}

