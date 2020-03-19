package edu.gmu.cs321.rekognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.cliftonlabs.json_simple.JsonException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

  private static final String TAG = "ProductActivity";


  private List<Product> productList;
  private List<String> keywords = new ArrayList<>();
  private Button refresh;
  private JsonProcessor proc = new JsonProcessor();
  private RekognitionClient rekognitionClient;
  {
    try{
      rekognitionClient = RekognitionClient.getInstance();
    }
    catch(IOException e)
    {
      if(e instanceof ProtocolException)
      {
        // TODO: print to screen "shit be fucked"
      }
      else if(e instanceof MalformedURLException)
      {
        // TODO: print to screen "more shit be fucked"
      }
      else
      {
        // TODO: print to screen "extreme amounts of shit be fucked"
      }
    }
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product);
    ListView list = (ListView) findViewById(R.id.theList);
    Log.d(TAG, "onCreate: Started.");

    getKeywords();
    getProducts();

    // Initialize Keyword Checkboxes
    Log.d(TAG, "initRecyclerView: init recyclerview");
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(layoutManager);
    final RecyclerViewAdapter rAdapter = new RecyclerViewAdapter(this, keywords);
    recyclerView.setAdapter(rAdapter);

    // Initialize Product List
    final ProductListAdapter adapter = new ProductListAdapter(this, R.layout.adapter_view_layout, productList);
    list.setAdapter(adapter);

    // Clicking a product listing sends to website
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        goToUrl(productList.get(i).getProductURL());
      }
    });

    // Un-checking a checkbox changes keyword list. Refresh to search again
    refresh = findViewById(R.id.refresh);
    refresh.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        keywords = rAdapter.getKeywords(); // checks status of checkboxes and updates this keywords
        Toast.makeText(ProductActivity.this, keywords.toString(), Toast.LENGTH_SHORT).show();
        getProducts(); // gets products
        adapter.changeDataSet(productList);
      }
    });

  }

  /**
   * Goes to the linked URL for a storefront by opening a new tab in the default browser
   * @param url The URL to navigate to
   */
  private void goToUrl (String url)
  {
    Uri uriUrl = Uri.parse(url);
    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
    startActivity(launchBrowser);
  }

  /**
   * Asks {@link JsonProcessor} for all the products and their respective storefronts returned
   * by Barcode Lookup
   */
  private void getProducts()
  {
    Thread myThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          productList = proc.getProducts(generateAPILink());
        }
        catch (IOException | JsonException e)
        {
          e.printStackTrace();
        }
      }
    });

    myThread.start();

    try {
      myThread.join();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves keywords from the Rekognition endpoint
   */
  private void getKeywords()
  {
    keywords.addAll(Arrays.asList(rekognitionClient.retrieveData().toString().split(",")));
  }

  /**
   * Generates a valid URL for a query to Barcode Lookup and passes it to {@link JsonProcessor}
   * @return A valid URL
   */
  private String generateAPILink()
  {
    // Example:
    // "https://api.barcodelookup.com/v2/products?search=GPS%20Navigation%20System&formatted=y&key=uxxr40qrtbki7q643gdmdxtrvupa7u"
    /*
     * LIST OF OLD KEYS
     * "uxxr40qrtbki7q643gdmdxtrvupa7u"
     * "0c4k75zh4s2wmyo0w9bmbnj17mt7t4"
     */
    String key = "6kb2bwqw13xatda4mgx8zjmpx7blcm";
    String api = "https://api.barcodelookup.com/v2/products?search=";

    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(api);

    /*
     * Take each keyword and split on spaces, appending them to the StringBuilder
     */
    for(String str : keywords)
    {
      String modded = str.replaceAll(" ", "%20");
      urlBuilder.append(modded);
      urlBuilder.append("%20");
    }

    int length = urlBuilder.length();
    urlBuilder.delete(length - 3, length);
    urlBuilder.append("&formatted=y&key=");
    urlBuilder.append(key);

    System.out.println("NEW LINK: " + urlBuilder.toString());
    return urlBuilder.toString();
  }
}