package edu.gmu.cs321.rekognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import java.util.List;

/**
 * This class handles displaying products and creating hyperlinked displays that redirect the
 * user to the product's webpage on click
 */
public class ProductListAdapter extends ArrayAdapter<Product> {

  private static final String TAG = "ProductListAdapter";

  private Context mContext;
  int mResource;

  static class ViewHolder {
    ImageView imageURL;
    TextView productName;
    TextView storeName;
    TextView storePrice;
  }

  public ProductListAdapter(Context context, int resource, List<Product> objects) {
    super(context, resource, objects);
    mContext = context;
    mResource = resource;
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    setupImageLoader();

    String productName = getItem(position).getProductName();
    String imageURL = getItem(position).getImageURL();
    String storeName = getItem(position).getStoreName();
    Double storePrice = getItem(position).getStorePrice();

    ViewHolder holder = new ViewHolder();

    if(convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(mContext);
      convertView = inflater.inflate(mResource, parent, false);

      holder = new ViewHolder();
      holder.productName = (TextView) convertView.findViewById(R.id.textView2);
      holder.storePrice = (TextView) convertView.findViewById(R.id.textView3);
      holder.storeName = (TextView) convertView.findViewById(R.id.textView4);
      holder.imageURL = (ImageView) convertView.findViewById(R.id.imageView1);

      convertView.setTag(holder);
    }
    else {
      holder = (ViewHolder) convertView.getTag();
    }

    int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",
        null, mContext.getPackageName());

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
        .cacheOnDisc(true).resetViewBeforeLoading(true)
        .showImageForEmptyUri(defaultImage)
        .showImageOnFail(defaultImage)
        .showImageOnLoading(defaultImage).build();

    imageLoader.displayImage(imageURL, holder.imageURL, options);
    System.out.println("The image URL should be: " + imageURL);
    holder.productName.setText(productName);
    holder.storePrice.setText(String.format("$%.2f", storePrice));
    holder.storeName.setText(storeName);

    return convertView;

  }

  private void setupImageLoader(){
    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheOnDisc(true).cacheInMemory(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .displayer(new FadeInBitmapDisplayer(300)).build();

    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
        .defaultDisplayImageOptions(defaultOptions)
        .memoryCache(new WeakMemoryCache())
        .discCacheSize(100 * 1024 * 1024).build();

    ImageLoader.getInstance().init(config);
  }

  /**
   * Should update an Views, or whatever the fuck these things are called, that display data from
   * this adapter thingy
   * @param newProductList The new list of products
   */
  public void changeDataSet(List<Product> newProductList)
  {
    this.clear();
    this.addAll(newProductList);
    notifyDataSetChanged();
  }
}
