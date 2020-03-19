package edu.gmu.cs321.rekognition;

public class Product {
    public String productName;
    public Double storePrice;
    public String imageURL; // remember image can be grabbed directly from internet w/ URL
    public String storeName;
    public String productURL;
    public String currSym;

    public Product(String productName, Double storePrice, String imageURL, String storeName, String productURL, String currSym) {
        this.productName = productName;
        this.storePrice = storePrice;
        this.imageURL = imageURL;
        this.storeName = storeName;
        this.productURL = productURL;
        this.currSym = currSym;
    }

    public void setProductName(String productName) {this.productName = productName; }
    public String getProductName() { return productName; }

    public void setStorePrice(Double storePrice) { this.storePrice = storePrice; }
    public Double getStorePrice() { return storePrice; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public String getImageURL() { return imageURL; }

    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreName() { return storeName; }

    public void setProductURL(String productURL) { this.productURL = productURL; }
    public String getProductURL() { return productURL; }

    public void setCurrSym(String currSym) { this.currSym = currSym; }
    public String getCurrSym() { return currSym; }

    public String toString()
    {
        return productName + "\t" + storeName + "\t" + currSym + storePrice + "\t" + imageURL;
    }
}