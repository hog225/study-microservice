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
### Build and start
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

### 명령어 
1. docker ps --format "{{.Image}}", docker ps --format "{{.Image}} : {{.ID}}"
1. docker-compose exec mongodb mongo product-db --quiet --eval "db.products.find()"
1. docker exec -it microservicestudy_mysql_1 bash -l //bash 로 들어가기 

## trouble shoot
1. Swagger 는 implementation 'org.springframework.boot:spring-boot-starter-web' 이 있어야 UI 가 열렸다. 
2. Swagger 3.0 은 이소스로 동작하지 않음 
3. mongoDB 쓸때 Entity @Id의 변수 이름은 반드시 id 여야한다. Id 이렇게 하면 findById 할때 조회가 안된다. ..
4. mongoDB auto-index-creation: true 가 app.yml 에 있어야 unique = true 가 먹는다. 
5. Lombok 과 MapStruct 같이 사용할때는 build.gradle 에서 선언 순서에 주의 하거나 lombok-mapstruct-binding 를 사용 한다. 
6. cloud-starter-stream-xxx 를 사용할때 spring-integration-amqp 를 추가로 Gradle에 implemantation 해줘야 정상동작 한다. 

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
    

