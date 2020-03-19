package edu.gmu.cs321.rekognition;

import com.github.cliftonlabs.json_simple.*;

public enum JsonKeyEnum implements JsonKey
{
  PRODUCTS, PRODUCT_NAME, FEATURES, IMAGES, STORES, STORE_NAME, STORE_PRICE, PRODUCT_URL, CURRENCY_SYMBOL;

  /**
   * Returns key name as lower-case version
   * @return Key name as lower-case version
   */
  public String toString()
  {
    return super.toString().toLowerCase();
  }

  /**
   * Returns this key
   * @return This key
   */
  @Override
  public String getKey()
  {
    return this.toString();
  }

  /**
   * Returns a reasonable, default value for this thing
   * @return Null
   */
  @Override
  public Object getValue()
  {
    return null;
  }
}
