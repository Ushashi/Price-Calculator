# Price-Calculator
=================

## What does this program do?
* This program takes two command-line arguments:
*        - a JSON file representing a cart, and
*        - a JSON file representing a list of base prices.
* It has been assumed that the options for a product-type are constant.
* The price for one item is calculated as :

        (base_price + round(base_price * artist_markup in cents)) * quantity

* The program outputs the total price of a given cart in cents followed by a newline character.

## How to run
* Please make sure you have installed java (I have used java version "1.8.0_45") and maven (You can download from https://maven.apache.org/)
* Download "PriceCalculatorSubmission" from Dropbox
* Open terminal
* Go to the required path under ~/Downloads/PriceCalculatorSubmission
* Please run:

      mvn install

* The above command will create our executable
* To run the program through command prompt/terminal, now run the following command with the cart file path as the
  first argument and base prices file path as the second argument. For example:

      java -jar target/PriceCalculator-1.0-SNAPSHOT-jar-with-dependencies.jar "./src/main/resources/cart-4560.json" "./src/main/resources/base-prices.json"

* To run all the integration tests separately (although they get run already when we do mvn install) :

       mvn clean test