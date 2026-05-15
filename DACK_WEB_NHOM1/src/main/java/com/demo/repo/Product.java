package com.demo.repo;

public class Product {
  private String name;
  private String brand;
  private String category;
  private long price;       // VND
  private Long oldPrice;    // có thể null
  private double rating;
  private String image;

  public Product(String name, String brand, String category, long price, Long oldPrice, double rating, String image) {
    this.name = name;
    this.brand = brand;
    this.category = category;
    this.price = price;
    this.oldPrice = oldPrice;
    this.rating = rating;
    this.image = image;
  }

  public String getName() { return name; }
  public String getBrand() { return brand; }
  public String getCategory() { return category; }
  public long getPrice() { return price; }
  public Long getOldPrice() { return oldPrice; }
  public double getRating() { return rating; }
  public String getImage() { return image; }
}
