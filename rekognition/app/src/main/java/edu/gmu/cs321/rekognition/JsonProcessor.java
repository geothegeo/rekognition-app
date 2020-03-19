package edu.gmu.cs321.rekognition;

import com.github.cliftonlabs.json_simple.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class JsonProcessor {

  private List<JsonObject> productList;
  private URL barcodeURL;
  private HttpURLConnection barcodeConnection;
  private final int MAX_STRING_LEN = 36;

  /**
   * Extracts the JSON array of Product objects in BarcodeLookup's JSON data model
   *
   * @param jsonString The string of JSON to be processed
   * @return A {@link JsonArray} containing all Product JSON objects
   */
  private void extractProductList(String jsonString) throws JsonException
  {
    JsonObject mainObj = (JsonObject) Jsoner.deserialize(jsonString);
    productList = mainObj.getCollection(JsonKeyEnum.PRODUCTS);
  }

  /**
   * Retrieves the JSON array containing Store objects
   * @param product A JSON Product object
   */
  private List<JsonObject> extractStoreObject(JsonObject product)
  {
     return product.getCollection(JsonKeyEnum.STORES);
  }

  /**
   * Returns store name from a store object
   * @param storeObject The Store whose name is to be extracted
   * @return The name of the store, or null if there is no name
   */
  private String getStoreName(JsonObject storeObject)
  {
    return storeObject.getString(JsonKeyEnum.STORE_NAME);
  }

  /**
   * Return the price of the item at a specific store
   * @param storeObject The store to extract a price from
   * @return The price of the product
   */
  private double getStorePrice(JsonObject storeObject)
  {
    return storeObject.getDouble(JsonKeyEnum.STORE_PRICE);
  }

  /**
   * Return a URL link to the storefront
   * @param storeObject The store to extract a link from
   * @return The link to the storefront where the product is located
   */
  private String getProductURL(JsonObject storeObject)
  {
    return storeObject.getString(JsonKeyEnum.PRODUCT_URL);
  }

  /**
   * The currency symbol for the product
   * @param storeObject The store object to extract a currency symbol from
   * @return The currency symbol
   */
  private String getCurrSym(JsonObject storeObject)
  {
    return storeObject.getString(JsonKeyEnum.CURRENCY_SYMBOL);
  }

  /**
   * Private helper method to connect to barcode URL given
   *
   * @param urlString A URL to query for a JSON data model, targeting BarcodeLookup
   * @return barcodeConnection
   * @throws MalformedURLException
   * @throws IOException
   * @throws ProtocolException
   */
  private HttpURLConnection barcodeConnect(String urlString) throws MalformedURLException, IOException, ProtocolException
  {
    barcodeURL = new URL(urlString);
    barcodeConnection = (HttpURLConnection) barcodeURL.openConnection();
    barcodeConnection.setRequestMethod("POST");
    barcodeConnection.setRequestProperty("Content-Type", "application/json");

    return barcodeConnection;
  }

  /**
   * Retrieves JSON data model from BarcodeLookup
   *
   * @param urlString The URL
   * @return barcodeBuilder
   * @throws IOException
   */
  private void retrieveJSON(String urlString) throws IOException, JsonException
  {
    HttpURLConnection barcodeConnection = barcodeConnect(urlString);
    BufferedReader barcodeReader = new BufferedReader(new InputStreamReader(barcodeConnection.getInputStream()));
    StringBuilder barcodeBuilder = new StringBuilder();

    String line = null;
    while ((line = barcodeReader.readLine()) != null)
    {
      barcodeBuilder.append(line);
    }

    barcodeConnection.disconnect();

    extractProductList(barcodeBuilder.toString());
  }

  /**
   * Retrieves the first image listed for this product
   * @param productObject A product object
   * @return The first image listed for this product, or null if no images
   */
  private String getImageUrl(JsonObject productObject)
  {
    JsonArray imageArray = productObject.getCollection(JsonKeyEnum.IMAGES);

    return imageArray != null && imageArray.size() > 0 ? imageArray.getString(0) : null;
  }

  /**
   * Retrieves the name of a product from the JSON data model extracted from the intial
   * BarcodeLookup JSON payload
   * @param productObject A product object
   * @return The name of the product
   */
  private String getProductName(JsonObject productObject)
  {
    return productObject.getString(JsonKeyEnum.PRODUCT_NAME);
  }

  /**
   * Public interface to JsonProcessor, returns a list of Product instances
   * @param url The URL to query for keyword search results
   * @return A list of Product instances
   */
  public List<Product> getProducts(String url) throws IOException, JsonException
  {
    retrieveJSON(url);

    List<Product> _productList = new ArrayList<>();


    for(JsonObject product : productList)
    {
      List<JsonObject> storeList = extractStoreObject(product);

      for (JsonObject storeEntry : storeList) {
        _productList.add(productGenerator(storeEntry, product));
      }
    }

    return _productList;
  }

  /**
   * Given a Store object from the BarcodeLookup JSON data model, generate {@link Product} instances
   * @param store The JSON Store object from BarcodeLookup
   * @return A {@link Product} instance
   */
  private Product productGenerator(JsonObject store, JsonObject product)
  {
    String storeName = getStoreName(store);
    double itemPrice = getStorePrice(store);
    String imageUrl = getImageUrl(product);
    String productName = ellipsizeName(getProductName(product));
    String productUrl = getProductURL(store);
    String currSym = getCurrSym(store);

    return new Product(productName, itemPrice, imageUrl, storeName, productUrl, currSym);
  }

  /**
   * Ellipsizes product names if they are greater than the maximum string length
   * @param name The name of the product
   * @return The original product name, or an ellipsized version, e.g. "Nike Red Shoes - Size..."
   */
  private String ellipsizeName(String name)
  {
    return name.length() > MAX_STRING_LEN ? name.substring(0, MAX_STRING_LEN - 3) +
      "..." : name;
  }

}