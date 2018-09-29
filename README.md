# Foo Market Application

Ads with Domain Events & Event Sourcing

## Run locally

```
./gradlew bootRun
``` 

## Test locally

Using [HTTPie](https://httpie.org/):

* Create Ad
  ```
  http POST localhost:8080/ad name=name1 description=desc1 price=10
  ```

* Get Ad
  ```
  http localhost:8080/ad/1
  ```
  
* Update Ad price
  ```
  http PUT localhost:8080/ad/1/price price=20
  ```

* Get published Ads
  ```
  http localhost:8080/ads
  ```
