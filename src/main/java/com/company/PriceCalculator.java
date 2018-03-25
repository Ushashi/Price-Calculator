/**
 * Created by uchakraborty on 3/15/18.
 */
package com.company;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;


public class PriceCalculator {

    static class CartItemInfo
    {
        String hashIndex;
        long artistMarkup;
        long quantity;
    }

    private static int buildPriceHash(HashMap priceHash, HashMap orderedOptionsMap, String basePricesFilePath) {
        int price = 0;
        int iterator = 0;

        StringBuilder strBu = new StringBuilder();

        JSONParser parser = new JSONParser();

        try{

            JSONArray basePrices = (JSONArray) parser.parse(new FileReader(basePricesFilePath));

            for (Object object : basePrices) {
                strBu.setLength(0);

                JSONObject jsonObject = (JSONObject) object;

                String productType = (String) jsonObject.get("product-type");
                strBu.append(productType);

                Map optionsMap = ((Map) jsonObject.get("options"));

                List<String> temp = new ArrayList<String>();

                if(!orderedOptionsMap.containsKey(productType)) {
                    temp.addAll(optionsMap.keySet());
                    orderedOptionsMap.put(productType, temp);
                }

                long base_price = (Long) jsonObject.get("base-price");

                List orderedOptionsList = new ArrayList<String>();
                orderedOptionsList = (ArrayList<String>)orderedOptionsMap.get(productType);

                hashIndexBuilder(iterator, orderedOptionsList, strBu, priceHash, optionsMap, base_price);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return price;

    }


    private static void hashIndexBuilder(int iterator, List orderedOptionsList, StringBuilder strBu, HashMap priceHash, Map optionsMap, long price){

        if(orderedOptionsList.isEmpty()) {
            //No options
            priceHash.put(strBu.toString(), price);
            return;
        }

        List currentOptions = (ArrayList<String>)optionsMap.get((orderedOptionsList.get(iterator)));

        //Save length, so we know how to remove appends
        int strBuLength = strBu.length();

        for (int i=0; i<currentOptions.size(); i++){

            strBu.append(currentOptions.get(i));

            //Check if there's another option to add (iterator+1), if not, we have our full string hash index
            if( iterator+1 >= (orderedOptionsList.size())) {
                priceHash.put(strBu.toString(), price);
            }
            else {
                hashIndexBuilder(iterator+1, orderedOptionsList, strBu, priceHash, optionsMap, price);
            }

            //Restore original str builder length i.e. remove previous append
            strBu.setLength(strBuLength);
        }
    }

    private static long getTotalCartPrice(HashMap priceHash, List<CartItemInfo> cartItemList){
        long totalCartPrice = 0;
        long basePrice = 0;

        for(CartItemInfo cartItem : cartItemList){
            basePrice = (Long)priceHash.get(cartItem.hashIndex);
            totalCartPrice += ((basePrice + Math.round(((basePrice * cartItem.artistMarkup))/100)) * cartItem.quantity) ;
        }

        return totalCartPrice;
    }

    private static List<CartItemInfo> parseCart(Map orderedOptionsMap,String cartPath){
        StringBuilder sb = new StringBuilder();
        JSONParser parser = new JSONParser();

        List<CartItemInfo> tempItemList = new ArrayList<CartItemInfo>();

        try {

            JSONArray cart = (JSONArray) parser.parse(new FileReader(cartPath));

            for (Object obj : cart)
            {
                CartItemInfo tempItem = new CartItemInfo();

                sb.setLength(0);
                JSONObject jsonObject = (JSONObject) obj;

                String productType = (String) jsonObject.get("product-type");
                sb.append(productType);

                Map options = ((Map)jsonObject.get("options"));

                //Use product type to get option order list

                List<String> tempOrderedOptionsList = new ArrayList<String>();

                tempOrderedOptionsList = (ArrayList<String>)orderedOptionsMap.get(productType);

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

    public static void main(String[] args) {
        long totalCartPrice =0;
        List<CartItemInfo> cartItemList;

        HashMap priceHash = new HashMap<String, Long>();
        HashMap orderedOptionsMap = new HashMap<String, List<String>>();

        //Builds out the priceHash from the base prices file
        buildPriceHash(priceHash, orderedOptionsMap, args[1]);

        cartItemList = parseCart(orderedOptionsMap, args[0]);

        totalCartPrice = getTotalCartPrice(priceHash, cartItemList);

        System.out.println(totalCartPrice);
    }
}
