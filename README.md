<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT LOGO -->
<h1 align="center">선착순 예약 구매 서비스</h1>
<h3 align="center"><a href="https://leather-hole-ee3.notion.site"><strong>Explore the docs »</strong></a></h3>
<br/>
<br/>


<!-- ABOUT THE PROJECT -->
## 프로젝트 소개

이 프로젝트는 Spring 기반의 E-Commerce를 주제로 한 개인 프로젝트입니다.
일반적인 상품 판매 기능과 함께 한정된 수량의 물품을 특정 시간에 오픈하여 선착순으로 구매할 수 있는 기능을 제공합니다.
해당 프로젝트는 동시다발적으로 들어오는 주문 요청을 적절히 제어하고, Redis의 Atomic Operation과 Caching을 통해 재고 처리 시스템을 개발했습니다.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### 사용 기술
* ![Spring]
* ![Spring Security]
* ![Postgre]
* ![Redis]
* ![Docker]
* ![AWS EC2]
* ![AWS RDS]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## 시작하기
### 사전 준비 사항

아래의 항목을 설치 후, 실행해주세요.
* JDK 21 이상
* gradle
* Docker

### 설치 방법

_아래 방법을 따라 프로젝트를 설치하고 실행해주세요._

1. 저장소를 클론합니다.
   ```sh
   git clone https://github.com/Jyoungjo/hanghae99_pre-order.git
   cd hanghae99_pre-order
   ```
2. Docker를 사용하여 PostgreSQL과 Redis를 실행합니다.
   ```sh
   docker run --env=POSTGRES_USER=계정명 --env=POSTGRES_PASSWORD=비밀번호 --network=bridge -p 5432:5432 -d postgres
   docker run --network=bridge -p 6379:6379 -d redis:latest
   ```
3. 각 모듈 내 첨부된 application-secret.yml.txt 파일을 참고하여 작성 후 resources에 첨부합니다.


4. Gradle을 사용하여 필요 의존성을 설치하고 빌드합니다.
   ```sh
   ./gradlew clean build
   ```
5. 애플리케이션을 실행합니다.
   ```sh
   ./gradlew preorder-eureka:bootRun
   ./gradlew preorder-gateway:bootRun
   ./gradlew preorder-user:bootRun
   ./gradlew preorder-item:bootRun
   ./gradlew preorder-order:bootRun
   ./gradlew preorder-payment:bootRun
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- API SPECIFICATION -->
## API 명세

API 명세의 경우 [📗API 명세서](https://htmlpreview.github.io/?https://github.com/Jyoungjo/hanghae99_pre-order/blob/docs/%EB%A6%AC%EB%93%9C%EB%AF%B8_%EC%9E%91%EC%84%B1/docs/API.html)에서 확인하실 수 있습니다.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ARCHITECTURE -->
## 프로젝트 아키텍처

### ERD
![image](https://github.com/user-attachments/assets/99c0ec45-f4db-4cb3-b801-f563e6c8be65)

### 서비스 아키텍처
![image](https://github.com/user-attachments/assets/0b2dbfa8-61d0-4990-9f4a-5965f007d17f)

### 파일 구조도
<details>
  <summary>파일 구조도</summary>

```bash
📦hanghae99_pre-order
 ┣ 📂gradle
 ┃ ┗ 📂wrapper
 ┃ ┃ ┣ 📜gradle-wrapper.jar
 ┃ ┃ ┗ 📜gradle-wrapper.properties
 ┣ 📂preorder-core
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂generated
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BusinessException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ExceptionCode.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂util
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AesUtils.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomCookieManager.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JwtParser.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┗ 📜application-secret.yml
 ┃ ┣ 📜application-secret.yml.txt
 ┃ ┗ 📜build.gradle
 ┣ 📂preorder-eureka
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂generated
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜EurekaServerApplication.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┗ 📜application.yml
 ┃ ┣ 📜build.gradle
 ┃ ┗ 📜Dockerfile
 ┣ 📂preorder-gateway
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂generated
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂hanghae99_gateway
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ErrorExceptionConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomErrorResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂filter
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜AuthorizationFilter.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂util
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtValidator.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RouteValidator.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GatewayApplication.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┣ 📜application-secret.yml
 ┃ ┃ ┃ ┃ ┗ 📜application.yml
 ┃ ┣ 📜application-secret.yml.txt
 ┃ ┣ 📜build.gradle
 ┃ ┗ 📜Dockerfile
 ┣ 📂preorder-item
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂docs
 ┃ ┃ ┃ ┗ 📂asciidoc
 ┃ ┃ ┃ ┃ ┣ 📜item.adoc
 ┃ ┃ ┃ ┃ ┗ 📜stock.adoc
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂generated
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BaseEntity.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AsyncConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JpaConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂create
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqCreateItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResCreateItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂delete
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂read
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResReadItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂update
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqUpdateItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResUpdateItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Item.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ItemServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂stock
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqStockDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResStockDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Stock.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜StockController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜StockRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜StockService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜StockServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorfulController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ItemServiceApplication.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┃ ┃ ┗ 📂docs
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜item.html
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜stock.html
 ┃ ┃ ┃ ┃ ┣ 📜application-secret.yml
 ┃ ┃ ┃ ┃ ┗ 📜application.yml
 ┃ ┃ ┗ 📂test
 ┃ ┃ ┃ ┣ 📂generated_tests
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JacksonConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PasswordEncoderTestConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RedisEmbeddedConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisRepositoryConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ItemServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂stock
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜StockControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜StockRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜StockServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ItemServiceApplicationTests.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┣ 📜application-secret.yml.txt
 ┃ ┣ 📜build.gradle
 ┃ ┗ 📜Dockerfile
 ┣ 📂preorder-order
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂docs
 ┃ ┃ ┃ ┗ 📂asciidoc
 ┃ ┃ ┃ ┃ ┣ 📜cart.adoc
 ┃ ┃ ┃ ┃ ┗ 📜order.adoc
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂generated
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂cart
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqCartDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ResCartDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResCartItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Cart.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CartServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂cart_item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartItem.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartItemRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CartItemService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂client
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂response
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜StockResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemClient.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentClient.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqPaymentDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserClient.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BaseEntity.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AsyncConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JpaConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RedisConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SchedulingConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂order
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqLimitedOrderDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqOrderDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqOrderItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ResOrderDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResOrderItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Order.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OrderServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂order_item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderItem.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderItemRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OrderItemService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂shipment
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Shipment.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ShipmentRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ShipmentScheduler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ShipmentService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ShipmentStatus.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OrderServiceApplication.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┃ ┃ ┗ 📂docs
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜cart.html
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜order.html
 ┃ ┃ ┃ ┃ ┣ 📜application-secret.yml
 ┃ ┃ ┃ ┃ ┗ 📜application.yml
 ┃ ┃ ┗ 📂test
 ┃ ┃ ┃ ┣ 📂generated_tests
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂cart
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CartServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂cart_item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CartItemRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CartItemServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PasswordEncoderTestConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RedisEmbeddedConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisRepositoryConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂order
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderServiceConcurrencyTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OrderServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂order_item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OrderItemRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OrderItemServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂shipment
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ShipmentRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ShipmentServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OrderServiceApplicationTests.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┣ 📜application-secret.yml.txt
 ┃ ┣ 📜build.gradle
 ┃ ┗ 📜Dockerfile
 ┣ 📂preorder-payment
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂docs
 ┃ ┃ ┃ ┗ 📂asciidoc
 ┃ ┃ ┃ ┃ ┗ 📜payment.adoc
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂generated
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜BaseEntity.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AppConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JpaConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqPaymentDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResPaymentDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂payment
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Payment.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentStatus.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentServiceApplication.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┃ ┃ ┗ 📂docs
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜payment.html
 ┃ ┃ ┃ ┃ ┣ 📜application-secret.yml
 ┃ ┃ ┃ ┃ ┗ 📜application.yml
 ┃ ┃ ┗ 📂test
 ┃ ┃ ┃ ┣ 📂generated_tests
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂payment
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentServiceApplicationTests.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┣ 📜application-secret.yml.txt
 ┃ ┣ 📜build.gradle
 ┃ ┗ 📜Dockerfile
 ┣ 📂preorder-user
 ┃ ┣ 📂src
 ┃ ┃ ┣ 📂docs
 ┃ ┃ ┃ ┗ 📂asciidoc
 ┃ ┃ ┃ ┃ ┣ 📜user.adoc
 ┃ ┃ ┃ ┃ ┗ 📜wishlist.adoc
 ┃ ┃ ┣ 📂main
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂client
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ItemClient.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ItemResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BaseEntity.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtUtils.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AsyncConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JpaConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PasswordEncoderConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂email
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmailController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmailDtoFactory.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmailService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResEmailDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂create
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqUserCreateDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResUserCreateDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂delete
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ReqUserDeleteDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂login
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqLoginDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResLoginDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂read
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResUserInfoDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂update
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqUserInfoUpdateDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReqUserPasswordUpdateDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ResUserPwUpdateDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResUserUpdateDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜User.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserRole.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂wishlist
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ResWishListDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResWishListItemDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Wishlist.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WishlistServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂wishlist_item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistItem.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistItemRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WishlistItemService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorTestController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceApplication.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┃ ┃ ┗ 📂docs
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜user.html
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜wishlist.html
 ┃ ┃ ┃ ┃ ┣ 📜application-secret.yml
 ┃ ┃ ┃ ┃ ┗ 📜application.yml
 ┃ ┃ ┗ 📂test
 ┃ ┃ ┃ ┣ 📂generated_tests
 ┃ ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┃ ┗ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂preorder
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PasswordEncoderTestConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RedisEmbeddedConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RedisRepositoryConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂wishlist
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WishlistServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂wishlist_item
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜WishlistItemRepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WishlistItemServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceApplicationTests.java
 ┃ ┃ ┃ ┗ 📂resources
 ┃ ┣ 📜application-secret.yml.txt
 ┃ ┣ 📜build.gradle
 ┃ ┗ 📜Dockerfile
 ┣ 📜.gitignore
 ┣ 📜build.gradle
 ┣ 📜gradlew
 ┣ 📜gradlew.bat
 ┣ 📜README.md
 ┗ 📜settings.gradle
```
</details>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- PROJECT FEATURES -->
## 프로젝트 주요 기능

1. 유저
   - 회원가입 및 로그인, 로그아웃
   - 회원 정보 및 위시리스트 조회
   - 회원 정보 수정 및 탈퇴
   - 위시리스트 조회 및 상품 추가, 삭제
   - 위시리스트에서 상품 주문
2. 상품
   - 상품 목록 페이징 조회 및 개별 조회
3. 주문
   - **일반 상품, 수량 한정 상품 주문**
   - 주문 상품에 대한 정보 조회 및 수정
   - **주문 취소 및 반품**
   - 장바구니 조회 및 상품 추가, 삭제
   - **장바구니에서 상품 주문**
4. 재고
   - 재고 조회 및 추가, 감소
5. 결제
   - 결제 시도 및 완료
6. 테스트
   - 각 레이어 별 Unit Test 진행(Code Coverage 75%)
<!-- - 동시성 테스트 코드 작성했다는거 증명하기! -->

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- TROUBLE SHOOTING -->
## 트러블 슈팅
1. 이메일 인증 로직 속도 개선

- 이메일 인증 비동기 처리 + Redis를 통한 13s -> 0.69s 개선

2. 재고 동시성 문제 해결
<!--동시성 테스트 코드 작성해서 증명-->

- 기존 선착순 주문 로직 실행 시 재고 처리 부분에서 예상 재고보다 많이 감소되는 부분 확인
- 이를 Redis의 Atomic Operation을 사용하여 동시성 문제 해결

[✅ 자세한 해결방안 보러가기](https://www.notion.so/Docs-b52e69594faf418e8be2e900024e8419?pvs=4#3873982447e94b3281cf12f2cf48af9e)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- TROUBLE SHOOTING -->
## 기술적 의사결정
1. PostgreSQL 선택 이유

-> 대용량 트래픽과 동시성 제어에 뛰어난 성능을 제공

2. Feign Client 선택 이유

-> 간결하고 직관적인 사용법 + Spring Cloud와의 통합 용이성

3. ID 간접 참조 선택 이유

-> 데이터 독립성, 낮은 결합도, 높은 유연성

4. 동시성 문제 해결을 위해 Redis를 선택한 이유

-> 높은 성능, 확장성, 데이터 일관성

[✅ 자세한 선택 이유 보러가기](https://www.notion.so/Docs-b52e69594faf418e8be2e900024e8419?pvs=4#6249be082a524b159d9e1d69d1028edb)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## 기여

혹여 프로젝트의 개선사항을 발견하셨을 경우, 기여해주시면 **정말 감사드리겠습니다!**

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/branch_name`)
3. Commit your Changes (`git commit -m 'commit!'`)
4. Push to the Branch (`git push origin feature/branch_name`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## 연락처

Email - [zimbote0411@gmail.com](mailto:zimbote0411@gmail.com)

Project Link: [https://github.com/Jyoungjo/hanghae99_pre-order](https://github.com/Jyoungjo/hanghae99_pre-order)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Spring]: https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[Spring Security]: https://img.shields.io/badge/spring_security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white
[Redis]: https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white
[Postgre]: https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white
[Docker]: https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[AWS EC2]: https://img.shields.io/badge/amazon_ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white
[AWS RDS]: https://img.shields.io/badge/amazon_rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white