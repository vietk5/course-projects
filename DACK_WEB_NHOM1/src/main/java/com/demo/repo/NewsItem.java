package com.demo.repo;

public class NewsItem {
  private String title, source, url, time;
  public NewsItem(String title, String source, String url, String time){
    this.title = title; this.source = source; this.url = url; this.time = time;
  }
  public String getTitle(){ return title; }
  public String getSource(){ return source; }
  public String getUrl(){ return url; }
  public String getTime(){ return time; }
}
