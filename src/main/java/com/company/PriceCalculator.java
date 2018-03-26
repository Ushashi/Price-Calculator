/**
 * Created by uchakraborty on 3/26/18.
 * This program takes two command-line arguments:
 *        - a JSON file representing a cart, and
 *        - a JSON file representing a list of base prices.
 * It has been assumed that the options for a product-type are constant.
 * The price for one item is calculated as : (base_price + round(base_price * artist_markup in cents)) * quantity
 * The program outputs the total price of a given cart in cents followed by a newline character.
 */

package com.company;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;


/*
Primary class containing main and supporting methods
*/
public class PriceCalculator {

    //Object class containing pertinent cart item information
    static class CartItemInfo{
        String hashIndex;
        long artistMarkup;
        long quantity;
    }

    /*
     * Builds out a HashMap that contains prices corresponding to all product types and their permutations of options from
     * a base price file. Additionally, a second HashMap is built that tracks the first ordering as the product options are
     * discovered. Since, two hashes are getting built, this method's output occurs by reference for consistency.
     *
     * @param (HashMap priceHash) HashMap passed by reference that is to be built, linking product/option combinations
     *                            (key) with their base price (value)
     * @param (HashMap orderedOptionsMap) HashMap passed by reference that links a product type to its list of options.
     *                                    Since the order of the options could change, it's important to store the
     *                                    option order to ensure we're creating equivalent option keys for priceHash.
     *                                    In this implementation we build the order based on the first product type found.
     * @param (String basePricesFilePath) The file path to the base prices as input from command line.
     */
    private static void buildPriceHash(HashMap priceHash, HashMap orderedOptionsMap, String basePricesFilePath) {

        int iterator = 0;
        StringBuilder strBu = new StringBuilder(); //Builds the key for our priceHash (ProductType+Option1+Option2..)
        JSONParser parser = new JSONParser();
        ArrayList<String> orderedOptionsList;

        try{
            JSONArray basePrices = (JSONArray) parser.parse(new FileReader(basePricesFilePath));

            for (Object object : basePrices) {
                strBu.setLength(0); //Reset string builder

                JSONObject jsonObject = (JSONObject) object;

                String productType = (String) jsonObject.get("product-type");
                strBu.append(productType);

                Map optionsMap = ((Map) jsonObject.get("options"));

                List<String> temp = new ArrayList<String>();

                /* If we don't have a productType entry in our orderedOptionsMap
                 * Then on this first encounter we need to save the options order in a list */
                if(!orderedOptionsMap.containsKey(productType)) {
                    temp.addAll(optionsMap.keySet());
                    orderedOptionsMap.put(productType, temp);
                }

                long base_price = (Long) jsonObject.get("base-price");

                orderedOptionsList = (ArrayList<String>)orderedOptionsMap.get(productType);

                if(orderedOptionsList.isEmpty()) { //Product type has no options, need to map productType->price
                    priceHash.put(strBu.toString(), base_price);
                }
                else {
                    hashIndexBuilder(iterator, orderedOptionsList, strBu, priceHash, optionsMap, base_price);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

     /*
     * Recursive method that creates a uniform ordering of the various options for a product type as a string
     * (ProductTypeName+Option1+Option2..) irrespective of their option order in the base price file. This method
     * permutes through all option combinations (ie Option1a+Option2a, Option1a+Option2b, etc.) with a given base price
     * entry. Additionally this method adds each index string to the priceHash with its associated base price value. This
     * is done inline in this method for efficiency sake.
     *
     * @param (int iterator) Keeps track of which option we are currently running the method on.
     * @param (List orderedOptionsList) Used as the key for the optionsMap, to ensure options are correctly ordered when
     *                                  creating the index string (ie Size+Color+Placement).
     * @param (StringBuilder strBu) Progressively builds the final index string, which is saved as the key in the priceHash
     * @param (HashMap priceHash) HashMap being built with the newly created index string, linking it to its base price.
     * @param (Map optionsMap) Mapping of each option (ie Size) to its option values (ie small, medium, large, etc.)
     * @param (long price) base price
     */
    private static void hashIndexBuilder(int iterator, List orderedOptionsList, StringBuilder strBu, HashMap priceHash, Map optionsMap, long price){

        /* Gets the actual options (ie Small, Medium, Large) into a list, this list is retrieved based on the order of
         * which option we are on (ie Size, Color, Placement) */
        List currentOptions = (ArrayList<String>)optionsMap.get((orderedOptionsList.get(iterator)));

        //Save length, so we know how to revert appends
        int strBuLength = strBu.length();

        for (int i=0; i<currentOptions.size(); i++){

            strBu.append(currentOptions.get(i));

            //Check if there's another option to add (iterator+1), if not, we have our full string hash index
            if( iterator+1 >= (orderedOptionsList.size())) {
                priceHash.put(strBu.toString(), price);
            }
            else {//Continue to recurse until we're at the last option
                hashIndexBuilder(iterator+1, orderedOptionsList, strBu, priceHash, optionsMap, price);
            }

            //Remove previous append
            strBu.setLength(strBuLength);
        }
    }

    /*
     * Scrapes through the cart and builds out, for each item, a key containing a string of each product-type appended with their
     * corresponding options, along with artist markup and quantity.
     *
     * @param (HashMap orderedOptionsMap) HashMap that returns an ordered list of options when given a product type as key.
     * @param (String cartPath) The file path to the cart as input from command line.
     * @return (List<CartItemInfo> tempItemList) List of cart item information.
     */
    private static List<CartItemInfo> parseCart(HashMap orderedOptionsMap, String cartPath){

        StringBuilder sb = new StringBuilder();
        JSONParser parser = new JSONParser();
        List<CartItemInfo> tempItemList = new ArrayList<CartItemInfo>();

        try {
            JSONArray cart = (JSONArray) parser.parse(new FileReader(cartPath));

            for (Object obj : cart)
            {
                CartItemInfo tempItem = new CartItemInfo();

                sb.setLength(0); //Reset string builder
                JSONObject jsonObject = (JSONObject) obj;

                String productType = (String) jsonObject.get("product-type");
                sb.append(productType);

                Map options = ((Map)jsonObject.get("options"));

                List<String> tempOrderedOptionsList = new ArrayList<String>();

                tempOrderedOptionsList = (ArrayList<String>)orderedOptionsMap.get(productType);

                //Build hash index string based on a previously determined options order
                for(int i = 0; i < tempOrderedOptionsList.size(); i++) {
                    sb.append( options.get(tempOrderedOptionsList.get(i)));
                }

                tempItem.hashIndex = sb.toString();

                tempItem.artistMarkup = (Long) jsonObject.get("artist-markup");

                tempItem.quantity = (Long) jsonObject.get("quantity");

                tempItemList.add(tempItem);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return tempItemList;
    }

    /*
     * Compute total price as per formula given
     *
     * @param (HashMap priceHash) Map of all product+options combination strings (key) to their prices (value).
     * @param (List<CartItemInfo> cartItemList) List of cart item information for each cart item.
     * @return (long totalCartPrice) computed total cart price.
     */
    private static long getTotalCartPrice(HashMap priceHash, List<CartItemInfo> cartItemList){

        long totalCartPrice = 0;
        long basePrice = 0;

        for(CartItemInfo cartItem : cartItemList){
            basePrice = (Long)priceHash.get(cartItem.hashIndex);
            totalCartPrice += ((basePrice + Math.round(((basePrice * cartItem.artistMarkup))/100)) * cartItem.quantity) ;
        }

        return totalCartPrice;
    }

    /*
     * Main program call. Takes in two command line arguments that path to json files.
     * args[0] = Cart path
     * args[1] = Base price path
     *
     * Outputs the resulting total price of the cart
     */
    public static void main(String[] args) {

        long totalCartPrice =0;
        List<CartItemInfo> cartItemList;
        HashMap priceHash = new HashMap<String, Long>();
        HashMap orderedOptionsMap = new HashMap<String, List<String>>();

        //Builds out the priceHash and orderedOptionsMap from the base prices file
        buildPriceHash(priceHash, orderedOptionsMap, args[1]);

        //Parses cart information from the cart file
        cartItemList = parseCart(orderedOptionsMap, args[0]);

        totalCartPrice = getTotalCartPrice(priceHash, cartItemList);

        System.out.println(totalCartPrice);
    }
}
