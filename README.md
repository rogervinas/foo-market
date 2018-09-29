[![Build Status](https://travis-ci.com/rogervinas/foo-market.svg?branch=master)](https://travis-ci.com/rogervinas/foo-market)

# Foo Market Application

Ads with Domain Events & Event Sourcing

## Run locally

```
./gradlew bootRun
``` 

## Test locally

Using [HTTPie](https://httpie.org/):

* Create Ads
  ```
  http POST localhost:8080/ad name=Car description='A race car' price=100
  http POST localhost:8080/ad name=Bike description='A mountain bike' price=15
  http POST localhost:8080/ad name=Computer description='A linux computer' price=30
  http POST localhost:8080/ad name=Phone description='A nokia phone' price=50
  ```

* Get Ad
  ```
  http localhost:8080/ad/1
  ```
 
* Remove Ad
  ```
  http DELETE localhost:8080/ad/1
  ```
   
* Update Ad price
  ```
  http PUT localhost:8080/ad/1/price price=20
  ```

* Add Ad product
  ```
  http PUT localhost:8080/ad/1/product product=publish
  http PUT localhost:8080/ad/1/product product=top
  ```
  
* Remove Ad product
  ```
  http DELETE localhost:8080/ad/1/product product=publish
  http DELETE localhost:8080/ad/1/product product=top
  ```
  
* Get published Ads
  ```
  http localhost:8080/ads
  ```
