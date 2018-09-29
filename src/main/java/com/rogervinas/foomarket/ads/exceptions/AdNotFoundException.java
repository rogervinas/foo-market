package com.rogervinas.foomarket.ads.exceptions;

public class AdNotFoundException extends RuntimeException {

  private final int id;

  public AdNotFoundException(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
