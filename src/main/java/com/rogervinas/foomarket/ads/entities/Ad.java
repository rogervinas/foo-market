package com.rogervinas.foomarket.ads.entities;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class Ad {

  private int id;
  private String name;
  private String description;
  private float price;
  private List<String> products;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public float getPrice() {
    return price;
  }

  public List<String> getProducts() {
    return products;
  }

  public static final class Builder {
    private int id;
    private String name;
    private String description;
    private float price;
    private List<String> products;

    private Builder() {
    }

    public static Builder anAd(Ad ad) {
      Builder builder = new Builder();
      builder.id = ad.getId();
      builder.name = ad.getName();
      builder.description = ad.getDescription();
      builder.price = ad.getPrice();
      builder.products = ad.getProducts();
      return builder;
    }

    public static Builder anAd() {
      return new Builder();
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withPrice(float price) {
      this.price = price;
      return this;
    }

    public Builder withProducts(List<String> products) {
      this.products = products;
      return this;
    }

    public Ad build() {
      Ad ad = new Ad();
      ad.id = this.id;
      ad.price = this.price;
      ad.description = this.description;
      ad.products = this.products == null ? emptyList() : unmodifiableList(this.products);
      ad.name = this.name;
      return ad;
    }
  }
}
