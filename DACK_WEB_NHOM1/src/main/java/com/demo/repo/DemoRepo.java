package com.demo.repo;

import java.util.*;
import java.util.stream.Collectors;

public class DemoRepo {
  public static List<String> brands(){
    return Arrays.asList("Apple","Samsung","ASUS","Lenovo","MSI","Acer","Dell","LG","Sony","HP","Kingston","Seagate","WD");
  }

  public static List<String> categories(){
    return Arrays.asList("Laptop","Apple","PC","Linh kiện","Màn hình","Phụ kiện","Thiết bị mạng","An ninh","Văn phòng");
  }

  private static final List<Product> PRODUCTS = List.of(
      new Product("Ryzen 7 / RTX 4070", "Custom", "PC", 38990000L, 41990000L, 4.9, "assets/img/r7.jpg"),
      new Product("MacBook Air M2 13-inch", "Apple", "Laptop", 28990000L, 0L, 4.8, "assets/img/mb-a12.jpg"),
      new Product("WH-1000XM5", "Sony", "Phụ kiện", 7990000L, 8990000L, 4.8, "assets/img/wh100.jpg"),
      new Product("Legion 5", "Lenovo", "Laptop", 27990000L, 29990000L, 4.7, "assets/img/l5.jpg"),
      new Product("Vivobook 15 OLED", "ASUS", "Laptop", 16990000L, 19990000L, 4.6, "assets/img/v15.jpg"),
      new Product("i5-12400F / RTX 4060", "Custom", "PC", 22990000L, 25990000L, 4.6, "assets/img/i5.jpg"),
      new Product("Keychron K2", "Keychron", "Phụ kiện", 1890000L, 2180000L, 4.6, "assets/img/keychronk2.jpg"),
      new Product("Katana 15", "MSI", "Laptop", 23990000L, 25990000L, 4.5, "assets/img/katana15.jpg")
  );

  public static List<Product> bestSellers(int limit){
    return PRODUCTS.stream().limit(limit).collect(Collectors.toList());
  }
  public static List<Product> byCategory(String cat){
    return PRODUCTS.stream().filter(p -> p.getCategory().equalsIgnoreCase(cat)).collect(Collectors.toList());
  }
  
  public static List<Product> getAllProducts(){
    return PRODUCTS;
  }
  
  public static List<Product> searchProducts(String keyword){
    if (keyword == null || keyword.trim().isEmpty()) {
      return PRODUCTS;
    }
    final String keywordLower = keyword.toLowerCase().trim();
    return PRODUCTS.stream()
        .filter(p -> p.getName().toLowerCase().contains(keywordLower) ||
                    p.getBrand().toLowerCase().contains(keywordLower) ||
                    p.getCategory().toLowerCase().contains(keywordLower))
        .collect(Collectors.toList());
  }

  public static List<NewsItem> latestNews(int n){
    return List.of(
        new NewsItem("NVIDIA ra mắt driver tối ưu game mới", "TechNews", "#", "2 giờ"),
        new NewsItem("Apple công bố Mac mới chạy chip M", "GenK", "#", "4 giờ"),
        new NewsItem("ASUS giới thiệu dòng ROG 2025", "VnExpress", "#", "Hôm nay"),
        new NewsItem("SSD PCIe 5.0 giảm giá", "Tinhte", "#", "Hôm qua"),
        new NewsItem("RTX 50 series rò rỉ", "Tom's Hardware", "#", "1 ngày"),
        new NewsItem("Bàn phím cơ hot 2025", "Gizmo", "#", "2 ngày")
    );
  }
}
