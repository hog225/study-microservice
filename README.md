# microserviceStudy
microservice study

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

## 명령어

### 자바 버전 변경 
1. Gradle 버전 마다 지원하는 Java 버전이 있음으로 적절하게 맞춰준다. 
```
1. sudo update-alternatives --config java // 자바 버전 변경
2. sudo update-alternatives --config javac // 자바 버전 변경
3. export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64(원하는 버전)

```


### Spring Anotation
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


