# microserviceStudy
microservice study

Hands-On Microservices with spring boot and spring cloud...

## 구조
microservices - 통합 서비스 포함 핵심 서비스 
api - 핵심 서비스에 접근하기 위한 Rest API 
util - 예외 처리 
product - MongoDB
Recommendation - MongoDB
Review - SQL

## RUN
```
java -jar microservices/product-composite-service/build/libs/*.jar &
// profile 설정하여 Jar 파일 돌릴때 
java -jar -Dspring.profiles.active=docker microservices\product-composite-service\build\libs\product-composite-service-0.0.1-SNAPSHOT.jar
```



1.del
rmdir /s /Q api\build
rmdir /s /Q util\build
rmdir /s /Q microservices\product-composite-service\build
rmdir /s /Q microservices\product-service\build
rmdir /s /Q microservices\recommendation-service\build
rmdir /s /Q microservices\review-service\build

## Request
1. curl http://yglocalhost:7000/product-composite/1 -s | jq .
2. http://localhost:7000/swagger-ui.html
3. http://localhost:15672/#/queues - rabbit mq guest/guest
4. curl http://localhost:8080/headerrouting -H "Host: i.feel.lucky:8080" // host 변경 Request


## 환경 
Ubuntu 20.04.2

### 자바 설치 
```
sudo add-apt-repository ppa:linuxuprising/java
sudo apt update
sudo apt install oracle-java15-installer
```

### sdkman
```
curl -s "https://get.sdkman.io" | bash
source /home/yghong/.sdkman/bin/sdkman-init.sh
```

### spring boot cli
[설치 가이드](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started-installing-the-cli)
```
sdk install springboot
```

## Gradle 
1. Gradle refresh => .\gradlew --refresh-dependencies
2. annotationProcessor 자동으로 생성되는 코드를 Build 시 성능면에서(Compiler가 코드 전체를 다 봐야 함으로 ) 불리한데 이를 개선해 주는것

## 명령어

### 자바 버전 변경 
1. Gradle 버전 마다 지원하는 Java 버전이 있음으로 적절하게 맞춰준다. 
```
1. sudo update-alternatives --config java // 자바 버전 변경
2. sudo update-alternatives --config javac // 자바 버전 변경
3. export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64(원하는 버전)

```
### Spring Boot 
1. [application.properties](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties-data-migration)


### Spring Anotation
0. [Spring 동작 방식](http://server-engineer.tistory.com/253)
1. @GetMapping
	Post, Put, Delete, Patch 와 같이 메서드 위에 쓰인다. 
2. @RequestMappring
	Class 에 사용됨, @RequestMappring("/user")
3. @RestController
	View 가 아닌 JSON으로 데이터를 리턴 할 필요가 있을때 
4. @Autowired 
	의존성 주입에 사용됨 web.xml constructor-agrg 태그 선언하고 @Bean으로 의존성 주입하는 방법과 동일
	의존성 주입이란 클래스 내부에서 객체를 할당하는게 아닌 외부에서 받는것
5. @Component
	Class 에서 빈을 직접 등록하기 위함 @ComponentScan 애노테이션으로 @Component 애노테이션을 스캔 하여 빈으로 등록해 줄 수 있음 
6. @RestControllerAdvice

  @ControllerAdvice 간단하게 말하자면 @ExceptionHandler, @ModelAttribute, @InitBinder 가 적용된 메서드들을 AOP를 적용해 컨트롤러 단에 적용하기 위해 고안된 애너테이션 입니다.
  @ResponseBody + @ControllerAdvice => @RestControllerAdvice 
7. @Controller
  View를 반환하기 위해 주로 사용 됨 Request가 들어오면 Dispatcher Servlet 이 Handler Mapping 에게 메시지를 주고 Controller 는 View 를 반환 여기서 ViewResolver가 사용되며 ViewResolver 설정에 맞게 View를 찾아 렌더링 한다. 
8. @Transactional
  Transaction을 도와주는 Anotation 만약 없다면 아래 코드 처럼 영속성 컨텍스트를 선언해서 수동으로 커밋 등을 해줘야 한다. 엔티티메니져 세션을 유지하기 위해서도 붙혀주는듯 하다. getOne 의 경우 Transactional을 안붙혀 주면 LazyInitializationExcetion 발생 한다. 
  ```
  import java.sql.*; import javax.sql.*; // ... DataSource ds = obtainDataSource(); Connection conn = ds.getConnection(); conn.setAutoCommit(false); // ... pstmt = conn.prepareStatement("UPDATE MOVIES ..."); pstmt.setString(1, "The Great Escape"); pstmt.executeUpdate(); // ... conn.commit(); // ...

  ```
  transaction 과정 
  Active --성공--> Partially Committed --commit-->Committed
         |                   |
         |                  중단
         |                   |
         |                   |
         |                   V
         --오류---------> Failed ---------Rollback->Aborted     
  
  Transaction 특징 
  원자성(트랜잭션이 일부만 반영되어서는 안됨), 
  일관성(작업 처리 결과는 항상 일관성이 있어야 한다.), 
  독립성(둘 이상의 트랜젝션이 동시에 병행 실행 되고 있을 때 어떤 트랜젝션도 연산에 끼어들 수 없다.), 영속성 (성공하면 결과는 영구적으로 반영 되어야 한다.)
9. @Repository
  퍼시스턴스 레이어, DB나 파일같은 외부 I/O 작업을 처리함
10. @PostConstruct 
  의존성 주입이 이루어진 후 초기화를 수행하는 매서드 리소스에서 호출 하지 않아도 초기화를 한다. 
  사용예 - App Init 코드 
11. @Builder
  lombok, VO.builder().id('ff')...name('df').build(); toBuild = true 로 하게 되면 새로운 객체를 생성 
  



### Spring Legacy

1.component-scan
```
default annotaion(Component Controller Service Repository)이 붙은 클래스를 찾아 빈을 생성한다. 
<context:component-scan base-package="" use-default-filters="false">
  <context:include-filter type="annotation" expression="org.springframework.stereotype.Repository"/>
</context:component-scan>
```
2. mvc:annotation-driven
```
spring MVC 컴포넌트들을 디폴트 설정으로 활성화 HandlerMapping 과 Adaptor 를 Bean으로 등록 해줌 
```
4. mvc:interceptors
```
DispatchServlet 이후 실행되면 특정 혹은 모든 요청을 가로채서 처리하는 인터셉터를 지정한다. 인터셉터 구현은 HandlerInterceptor 인터페이스를 구현하던지 HandlerInterceptorAdapter 클래스를 상속 받아서 구현한다.
<mvc:interceptors>
  <mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/image/**"/>
    
    <bean class="package.myInterceptor" />
  </mvc:interceptor>
</mvc:interceptors>
```
5. constructor-arg
```
생성자를 통해 빈을 주입 받는 경우 생성자의 Value를 지정할 수 있다.  ref=? value=?
<bean id="hello" class="org.springframework.core.io.FileSystemResource">
  <constructor-arg>
    <value>/home/web/temp</value>
  </constructor-arg>
</bean>
```

## 도구 
1. Spring profile
도커, 로털머신, 개발서버, 운영서버등 환경별 구성을 가능하게 해줌 
2. actuator
어플리케이션 상태를 관리 해주는 역할 health, meter 등등 
3. docker 컴포즈 
  전체 마이크로서비비스 시스템 환경을 관리할 수 있다. docker-compose up/down 명령어로 살리고 죽일 수 있다. 
  docker-compose.yml 에 마이크로서비스별 세팅을 할 수 있다. 

4. 스프링폭스 
공개 API 문서화, 스웨거 기반의 문서를 런타임에 생성하는 스프링 폭스를 사용 swagger $HOST:$PORT/swagger-ui/index.html

## Docker
1. 스프링 프로파일 설정 resource 및에 application.yml
2. docker 파일 설정 
1. 팻자 파일 빌드 => gradlew :mi...:pro...:build (요렇게 하면 의존 프로젝트 까지 빌드됨)
1. ./gradlew build ,  gradle.bat build -x test
1. docker-compose build
1. docker-compose up -d
2. docker build -t product-service . 
3. docker run --rm -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" product-service
4. docker run -d -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --name my-prd-srv product-service
5. docker logs my-prd-srv -f
5. .\gradlew.bat build -x test && docker-compose build && docker-compose up
6. docker-compose -f [docker-compose.yml] up 
7. docker-compose up -d --scale review=2 // review 서비스를 두개로 스케일 업 한다. 
8. 위 커맨트 안되면  docker-compose up -d --scale review=2 --remove-orphans
9. 

### 명령어 
1. docker ps --format "{{.Image}}", docker ps --format "{{.Image}} : {{.ID}}"
1. docker-compose exec mongodb mongo product-db --quiet --eval "db.products.find()"
1. docker exec -it microservicestudy_mysql_1 bash -l //bash 로 들어가기 

## mongodb 
1. mongo product-db --quiet --eval "db.products.find()"
2. mongo recommendation-db --quiet --eval "db.recommendations.find()"

## reactive 프로그래밍 
1. 동기/ 비동기 : 작업을 수행하는 주체가 두개 이상일 경우 
    - 두개 이상의 작업이 서로 시간을 맞춘다면 동기 그렇지 않다면 비동기
    - 동시에 시작하는 두개의 작업, 한개의 작업이 끝나자 마자 시작되는 또 다른 작업 
2. 블로킹/ 논블로킹 : 작업의 대상이 두개 이상일때 
    - 한작업이 다른 작업의 종료나 시작을 기다려주는 경우 블로킹 그렇지 않다면 논 블록킹

- Funtional 인터페이스  
https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html
  
1. 비동기에서의 문제 
    - 소비자 그룹 
        - 토픽에 대응하는 서비스(인스턴스) 를 늘리면 서비스가 동일한 메시지를 소비하는 문제가 있다. 이를 해결하기 위해 스프링 클라우드 스트림을 이용하여 서비스당 한개의 메시지를 소비하게 한다. 
    - 재시도 및 데드레터 대기열 
        - 네트워크 문제로 메시지 처리가 안될때 Retry 횟수 를 정하고 지정된 횟수동안 처리가 안되면 데드레터 큐로 이동시킨다. 보통 MQ 의 기능이다. 
    - 순서 보장 및 파티션 
        - 비지니스 로직 메시지는 순서가 보장될 필요가 있는 것들이 있다. 이런 메시지 들에 Key를 부여하여 해당 키가 소비될 수 있는 서비스를 지정한다. Pub, Sub 둘다 설정이 필요 하다.
    

## 서비스 검색 
---
- 살아있는 마이크로서비스르 검색하고 보여준다. 
- 로드 밸런싱을 한다. 
- 상태가 비정상인 마이크로 서비스를 감지한다. 
- 마이크로 서비스를 자동으로 등록하거나 해지 한다. 
- DNS 서버의 경우 리졸브된 IP 주소를 캐시하여 사용하기 때문에 IP 가 동작하는한 계속 사용한다. 로드에 대한 설정을 할 수 없다. 
- 서비스 검색 서버는 아래 조건을 만족해야 한다. 
    - 언제든지 인스턴스가 시작, 종료, 장애  상태가 될 수 있다. 
    - 실패한 인스턴스는 복구 될 수 있다. 복구 되지 못한 인스턴스는 버려야 한다. 
    - 언제든 네트워크 오류가 발생 할 수 있다. 

- Spring Cloud 는 서비스 검색 서버의 검색 서비스와 통신하는 방법을 추상화한 DiscoveryClient 를 제공한다. 
- Load Balancer 를 통해 검색 서비스에 등록된 인스턴스로 요청을 보내는 방법을 추상화 한 LoadBalanceClient 도 있다. 
### Netflix Eureka
시나리오 

1. Review 서비스는 시작시 Eureka에 등록한다. 
2. 정기적으로 Eureka에게 Hearbeat를 날린다. 
3. Product-composite 서비스는 사용가능한 서비스 정보를 정기적으로 유레카에게 전달 
4. Product-composite 서비스 내에서 사용가능한 인스턴스 목록에서 대상을 선택할 수 있다. (라운드 로빈으로)

- Client 별로 Eureka Client 가 있어야 한다.
- 상용 환경에서 적절한 기본 구성 값을 가지고 있다. 하지만 이럴경우 유레카 서버와 클라이언트가 연결되는데 많은 시간이 소비될 수 있다.
- Eureka Server 가 죽으면 MicroService 들은 Caching 된 데이터를 가지고 있어 계속하여 라운드 로빈으로 Request를 보내지만 여기서 인스턴스 한개가 죽게 되면 죽었는지 알 수 없다. 이 때문에 이미 죽은 인스턴스에게 Requet를 보내는 문제가 생길 수 있다. 그럼으로 검색 서버는 절대 죽어서는 안된다.   

유레카 변수 그룹 
- eureka.server 서버 
- eureka.client 유레카 서버와의 통신을 위한 것
- eureka.instance 유레카 서버에 자신을 등록하여는 마이크로 서비스 인스턴스를 위한 것 

APIs
- localhost:8761/eureka/apps

#### Spring Cloud Load Balancer
- product-composite 에서 처럼 Client-Side(Middle?) 에서 Round Robbin으로 로드를 분산해주는 기능이다.  

### kubernetes

### Apache ZooKeeper

### Hashicorp Consul

## Edge Server
---
- 엣지 서버는 주요서비스를 외부에서 접근하지 못하도록 보호한다.
- Product-composite 서비스 같은 외부 공개용 서비스를 여렇개 붙힐 수 있을 것 같다. 

![https://subscription.packtpub.com/book/web_development/9781789613476/10/ch10lvl1sec81/adding-an-edge-server-to-our-system-landscape](/img/edgeserver.png)


### Spring Cloud Gateway
- Zuul 의 대체제, API Gateway Server 이며 Edge 서버 역할을 한다.
- URL 경로 기반 라우팅, OAuth 2.0, OIDC 에 기반한 엔드포인트 보호 기능 
- 논 블로킹 API 사용, Zuul 은 블로킹 API

### Netflix Zuul
- zuul ref https://docs.spring.io/spring-cloud-netflix/docs/2.2.9.RELEASE/reference/html/#router-and-filter-zuul
- https://coe.gitbook.io/guide/gateway/zuul
- https://spring.io/guides/gs/routing-and-filtering/

### Spring Security Oauth

## 구성중앙화 
---
- 마이크로 서비스 환경의 구성 정보를 중앙 집중식으로 관리한다. 
- 구성을 로컬, 다른 서버 등에 저장 할 수 있다.
- 스프링 클라우드 버스를 이용해 구성 변경사항을 실서버에 업데이트 할 수 있다. 
- 구성 정보의 암호화도 진행 한다. 

![https://subscription.packtpub.com/book/web_development/9781789613476/10/ch10lvl1sec81/adding-an-edge-server-to-our-system-landscape](/img/cloud-bus-config.png)

### Spring Cloud Config Server 

## 서킷 브레이커 
---
용어 정리 

- 서킷브레이커: 원격서비스가 응답하지 않을때 연쇄 장애를 방지
- rate limiter: 지정한 시간 동안의 서비스 요청 수를 제한하고자 사용 
- bulkhead: 서비스에 대한 동시 요청 수 제한
- 재시도: 임의적 오류를 처리할때 
- timeout: 응답없는 서비스 처리 

### Resilience4j
- 기존엔 넷플릭스 히스트릭스(현제 Maintenance 모드) 였음 그러나 Resilience4j 로 변경을 권고

  ![핸즈온 마이크로서비스](/img/resilience4j-s.png)
 
- 동작 
  1. Close 상태에서 시작해서 요청처리 요청이 성공적이면 회로의 닫힘 상태를 유지 해야겠지 
  2. Threshold 가 넘어서는 실패가 발생시 카운터가 증가하고 5번이 되면 트립이라는 Action을 통해 서킷을 Open 한다. 
  3. 설정한 시간이 지나면 반열림 상태로 전환되고 프로브 요청을 보내서 장애가 해결 되었는지 확인 
  4. 프로브 요청이 실패 하면 다시 열림 상태가 됨 
  5. 프로브 요청이 성공하면 닫힘 상태가 되어 새 요청을 처리 
  


## 분산추적
---

### Spring Seluth
- 구글 Dapper 기반이다. 
- Request 에 추적 ID(Trace ID) 같은 것을 붙혀서 복잡한 Request를 추적할 수 있도록 해준다. 
- Span ID, Trace ID 의 개념이 있다. 
  - Span ID: 작업단위의 ID 라고 생각하면 되는데 MicroService를 넘나들때마다 변경된다.
  - Trace ID: 최초의 Request 와 마지막 Request 가 동일한 값을 같는다. 
- 추적트리: 전체 워크플로의 추적 정보  
- 네이버에는 Pinpoint 가 있다. 

### Zipkin
- 트워터에서 만듦
- Seluth 의 추적 데이터를 수집 한다. 



## trouble shoot
1. Swagger 는 implementation 'org.springframework.boot:spring-boot-starter-web' 이 있어야 UI 가 열렸다.
2. Swagger 3.0 은 이소스로 동작하지 않음
3. mongoDB 쓸때 Entity @Id의 변수 이름은 반드시 id 여야한다. Id 이렇게 하면 findById 할때 조회가 안된다. ..
4. mongoDB auto-index-creation: true 가 app.yml 에 있어야 unique = true 가 먹는다.
5. Lombok 과 MapStruct 같이 사용할때는 build.gradle 에서 선언 순서에 주의 하거나 lombok-mapstruct-binding 를 사용 한다.
6. cloud-starter-stream-xxx 를 사용할때 spring-integration-amqp 를 추가로 Gradle에 implemantation 해줘야 정상동작 한다. 
7. zuul .. 버전을 명시하지 않으니 동작하지 않았다... 그리고 Cloud Hoxton.SR10 은 부트 2.3.x 에서 동작한다. [참조](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-Hoxton-Release-Notes) 그런데 SR12, boot 2.5.5, zuul 2.2.9.RELEASE 환경에서 실행이 안됐음...

