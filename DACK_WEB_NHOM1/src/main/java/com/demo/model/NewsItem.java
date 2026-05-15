package com.demo.model;

public class NewsItem {
  private String title;
  private String source;
  private String url;
  private String time;

  public NewsItem(String t, String s, String u, String ti){
    title=t; source=s; url=u; time=ti;
  }

  public String getTitle(){return title;}
  public String getSource(){return source;}
  public String getUrl(){return url;}
  public String getTime(){return time;}
}
