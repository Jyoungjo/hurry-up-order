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
<h1 align="center">Hurry Up Order!</h1>
<h2 align="center">E-Commerce MSA 프로젝트</h2>
<h3 align="center"><a href="https://leather-hole-ee3.notion.site"><strong>docs 확인하러 가기 »</strong></a></h3>
<br/>
<br/>


<!-- ABOUT THE PROJECT -->
## 📌 프로젝트 소개

### 이 프로젝트는 Spring Boot 기반의 E-Commerce 쇼핑몰 구현을 목표로 한 개인 프로젝트입니다.
### 상품 조회부터 주문, 결제까지 일반적인 커머스 기능을 제공하며, 실제 PG사 API 연동을 통한 결제 테스트도 포함되어 있습니다.

### 서비스는 MSA + 이벤트 기반 아키텍처(EDA) 로 구성되었으며, 고트래픽/장애 상황에서도 안정적인 동작을 목표로 다음을 설계했습니다:

- #### Kafka 기반 비동기 이벤트 처리를 통한 서비스 간 결합도 최소화

- #### Redis + Lua Script 기반 재고 선점 및 동시성 제어

- #### Redis Sentinel을 통한 고가용성 확보

- #### Outbox 패턴 + Kafka 재시도 + DLQ 처리로 메시지 손실 방지

- #### PG사 장애 대응을 위한 Circuit Breaker 및 Retry 기반 회복 탄력성 확보

### ➡ 실제 장애 상황(Redis 다운, Kafka 이벤트 유실, PG API 오류 등)에 대비하여, 확장성·복원력·일관성을 고려한 백엔드 설계를 학습하고 구현한 프로젝트입니다



### 사용 기술
* ![Spring]
* ![Spring Security]
* ![Postgre]
* ![Redis]
* ![Docker]
* ![AWS EC2]
* ![AWS RDS]
* ![KAFKA]

---

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
   git clone https://github.com/Jyoungjo/hurry-up-order.git
   cd hurry-up-order
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

---

<!-- API SPECIFICATION -->
## API 명세

API 명세의 경우 [📗API 명세서](https://htmlpreview.github.io/?https://github.com/Jyoungjo/hurry-up-order/blob/main/docs/API.html)에서 확인하실 수 있습니다.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- ARCHITECTURE -->
## 프로젝트 아키텍처

### ERD
![image](https://github.com/user-attachments/assets/a1a792ce-546e-470e-b88d-0f4414eae526)

### 프로젝트 아키텍처
![Image](https://github.com/user-attachments/assets/ace03da8-4476-42bd-99e7-74727853731c)

### CI/CD Flow
![Image](https://github.com/user-attachments/assets/31c3d673-d6b8-46ed-9fa1-9bcbcc5acfcb)

---

<!-- PROJECT FEATURES -->
## 🎯 주요 기능 요약

👤 사용자 관리
- 회원가입 시 정보 암호화 저장
- 로그아웃 시 refresh token 블랙리스트 처리로 재발급 차단
- 사용자 정보 조회, 수정, 탈퇴 기능
- 위시리스트 추가/삭제 및 위시리스트에서 바로 주문 가능

🛒 상품/주문/장바구니 관리

- 판매자 상품 등록 및 페이징/상세 조회
- 일반 상품 및 수량 한정 상품 주문 처리
- 주문 내역 조회, 취소 및 반품 처리
- 장바구니 기능 + 장바구니에서 바로 주문 연결

📦 재고 관리
- 실시간 재고 조회, 증가/차감
- Redis + Lua Script를 통한 동시성 제어

💳 결제 및 정산 처리
- TossPayments / NicePayments API 연동
- Circuit Breaker + Retry로 장애 시 복원력 확보
- 결제 실패 시 Kafka 기반 보상 트랜잭션 처리
- 결제 완료 후 정산 저장 및 스케줄링 처리

✅ 테스트
- 각 레이어별 Unit Test (Coverage 75%)
- 동시성 테스트 코드로 **[재고 제어 유효성 입증](https://www.notion.so/Docs-b52e69594faf418e8be2e900024e8419?pvs=4#8b002fe0ca9f497984b09b1f7bb92cf0)**

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- IMPROVED PERFORMANCE -->
## 성능 개선

|         항목          |                     처리 내용                      | 개선 전  | 개선 후  |           향상도           |
|:-------------------:|:----------------------------------------------:|:-----:|:-----:|:-----------------------:|
|       이메일 인증        |                   비동기 이벤트 처리                   |  13s  | 0.69s | ✅ 약 18.8배 향상 / 94.6% 감소 |
|     상품 및 재고 조회      |                 Redis Caching                  | 180ms | 14ms  | ✅ 약 13배 향상 / 약 92.2% 감소 |
|         로그인         |        로컬 Cache + Interface Projection         | 1.3s  |  8ms  | ✅ 약 162배 향상 / 99.4% 감소  |
| 주문 생성 (상품 1000건 기준) | Batch INSERT + 외부 API 병렬 호출(CompletableFuture) | 5.3s  | 1.23s | ✅ 약 4.3배 향상 / 76.8% 감소  |

[🔗 📄 성능 개선 상세 보기](https://www.notion.so/Docs-b52e69594faf418e8be2e900024e8419?pvs=4#4514b232e6f3435392fcc62ca5723fc5)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- TROUBLE SHOOTING -->
## 트러블 슈팅

|             문제              |        원인 및 현상         |            해결 방법             |            효과             |
|:---------------------------:|:----------------------:|:----------------------------:|:-------------------------:|
|          재고 동시성 문제          |   트랜잭션 충돌로 초과 판매 발생    |    Redis + Lua + 낙관적 락 적용    |       ✅ 재고 초과 0건 달성       |
|        Eureka 등록 누락         | Eureka 기동 전 다른 서비스 실행  | HealthCheck + Compose 의존성 설정 |       ✅ 서비스 인식 안정화        |
| Test 환경 내 Embedded Redis 충돌 |      테스트 중 포트 중복       |       빈 포트 동적 할당 로직 도입       | ✅ 해당 문제로 인한 테스트 실패율 0% 달성 |
|          Redis 장애           | 단일 Redis 장애 시 전체 주문 실패 |   Redis Sentinel + 커넥션 재시도   |    ✅ 주문 실패율 100% → 0%     |
|       Kafka 이벤트 소비 누락       |   중복/장애 시 이벤트 재처리 불가   | ProcessedEvent 테이블 + DLQ 처리  |       ✅ 데이터 정합성 확보        |
|          주문 롤백 불가           |   결제 실패 시 재고가 복구 안됨    |     SAGA 패턴 + 보상 이벤트 도입      |      ✅ 전체 흐름 복원력 강화       |

[🔗 📄 트러블 슈팅 상세 보기](https://www.notion.so/Docs-b52e69594faf418e8be2e900024e8419?pvs=4#3873982447e94b3281cf12f2cf48af9e)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- TECHNICAL DECISION-MAKING -->
## 🧠 기술적 의사결정

### 📦 데이터 저장 및 일관성
- **PostgreSQL** → 대용량 트래픽과 동시성 제어에 강함
- **ID 간접 참조** → 낮은 결합도, 높은 유연성 확보
- **Outbox 패턴** → DB 트랜잭션과 Kafka 이벤트의 정합성 보장

### 🔗 서비스 간 통신 구조
- **Feign Client** → 직관적이며 Spring Cloud와 통합 용이
- **Kafka 메시징** → 비동기 처리 및 장애 격리에 유리

### 🚀 성능 최적화 / 병렬 처리
- **Redis** → 빠른 응답성과 데이터 일관성 확보
- **Batch Insert** → 주문 처리 I/O 최소화
- **CompletableFuture** → 외부 서비스 병목 제거

### 🛡 장애 대응 및 회복 탄력성
- **Circuit Breaker** → 외부 결제 API 장애 전파 방지
- **Redis Sentinel + Docker** → 자동 Failover 구성으로 고가용성 확보

[✅ 기술 의사결정 상세 보기](https://www.notion.so/Docs-b52e69594faf418e8be2e900024e8419?pvs=4#6249be082a524b159d9e1d69d1028edb)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---


<!-- CONTRIBUTING -->
## 기여

혹여 프로젝트의 개선사항을 발견하셨을 경우, 기여해주시면 **정말 감사드리겠습니다!**

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/branch_name`)
3. Commit your Changes (`git commit -m 'commit!'`)
4. Push to the Branch (`git push origin feature/branch_name`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

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
[KAFKA]: https://img.shields.io/badge/apache_kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white
