### Week8 2021-03-07

463 ~ 555p

## 6.4 스프링의 프록시 팩토리 빈
### 6.4.1 ProxyFactoryBean
- 자바에는 JDK 다이내믹 프록시 말고도 다양한 기술이 존재
    - 스프링은 일관된 처리를 지원하기 위해 추상 레이어를 제공
        - 프록시 오브젝트를 생성해주는 기술을 추상화한 팩토리 빈 제공
- 스프링의 빈 오브젝트로 등록하게 해줌
- ProxyFactoryBean이 생성하는 프록시의 부가기능은 `InvocationHandler`가 아닌 `MethodInterceptor`를 구현
    - 차이점은 타깃 오브젝트를 부가기능이 알고 있어야 하는가 몰라도 무방한가의 차이
        - `MethodInterceptor`의 `invoke()`는 타깃 오브젝트를 주입받기에 구체적인 타깃에 상관없이 독립적

```java
// JDK dynamicProxy
@Test
void dynamicProxy() {
    Hello proxiedHello = (Hello) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[]{Hello.class},
        new UppercaseHandler(new HelloTarget())
    );
    
    assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
    assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
    assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("THANK YOU BOB");
}
```
```java
// 스프링의 ProxyFactoryBean 이용
@Test
void proxyFactoryBean() {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget());
    pfBean.addAdvice(new UpperCaseAdvice());
    
    Hello proxiedHello = (Hello) pfBean.getObject();
    
    assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
    assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
    assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("THANK YOU BOB");
}

public class UpperCaseAdvice implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		String returnValue = (String) methodInvocation.proceed();
		return returnValue.toUpperCase();
	}
}
```

#### 어드바이스: 타깃이 필요 없는 순수한 부가기능
- 타깃 오브젝트를 주입받음 -> 부가기능에만 집중 가능
- `MethodInvocation`은 일종의 콜백 오브젝트로, `MethodInvocation` 구현 클래스는 일종의 공유 가능한 템플릿 처럼 동작
    - JDK 다이내믹 프록시와 달리 싱글톤으로 두고 재사용 가능
        - 수많은 DAO 메소드가 하나의 JdbcTemplate 오브젝트를 공유하는 것과 마찬가지
- `ProxyFactoryBean`에 `MethodInterceptor` 구현체를 DI 해줄 때는 `addAdvice()` 사용
    - 여러 개의 `MethodInterceptor`를 추가할 수 있다
        - 앞의 예제에선 Advice들을 모두 빈으로 등록해줘야 했는데, 스프링 프록시팩토리빈은 프록시팩토리빈 하나만 빈으로 등록하면 됨❓
    - 타깃 클래스가 구현하는 인터페이스를 알려주지 않아도 된다
        - `ProxyFactoryBean`은 인터페이스 자동검출이 가능하여 타깃이 구현하는 인터페이스를 알 수 있다
        - 일부 인터페이스만 구현하길 원한다면 직접 지정하는 것도 가능
    
#### 포인트컷: 부가기능 적용 대상 메소드 선정 방법
- 기존에 `InvocationHandler`가 부가기능과 메소드 선정 알고리즘을 담당했던 것과 달리, `MethodInterceptor`는 부가기능에만 집중
    - 스프링은 메소드 선정 알고리즘을 담당하는 `포인트컷`을 별도로 두고 이용
- `어드바이스`와 `포인트컷`이 프록시에 DI로 주입된다

```java
    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("sayH*");
		
    # 프록시 빈에 포인트컷과 어드바이스 주입
    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice()));
```
- 어드바이스 + 포인트컷 : 어드바이저
    - 어드바이저 단위의 조합을 프록시 빈에 주입
    
### 6.4.2 ProxyFactoryBean 적용
#### TransactionAdvice
- 리플렉션을 통한 타깃 메소드 호출 작업의 번거로움이 콜백 이용으로 제거 됨❓
- 타깃 메소드가 던지는 예외도 `InvocationTargetException`으로 래핑되지 않고 그대로 옴 ⭐

```java
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object ret = methodInvocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
```
#### 스프링 XML 설정파일
- xml 설정
#### 테스트
```java
    # AS-IS
    TransactionHandler txHandler = new TransactionHandler();
    txHandler.setTarget(testUserService);
    txHandler.setTransactionManager(transactionManager);
    txHandler.setPattern("upgradeLevels");

    UserService txUserService = (UserService) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[]{UserService.class},
        txHandler
    );
    
    # TO-BE
    ProxyFactoryBean txProxyFactoryBean = context.getBean(
        "&userService",
        ProxyFactoryBean.class
    );
    
    txProxyFactoryBean.setTarget(testUserService);
    UserService txUserService = (UserService) txProxyFactoryBean.getObject();
```
#### 어드바이스와 포인트컷의 재사용
- ProxyFactoryBean은 스프링의 DI, 템플릿/콜백 패턴, 서비스 추상화 기법이 적용
    - 독립적, 재사용 가능
    
---

## 6.5 스프링 AOP
- 투명한 형태로 부가기능 제공하기

### 6.5.1 자동 프록시 생성
- 프록시 팩토리 빈 방식의 접근방법 한계
    1. 부가기능이 타깃 오브젝트마다 생성 됨  
       -> 스프링 ProxyFactoryBean의 어드바이스로 해결
    2. 타깃 오브젝트마다 비슷한 내용의 ProxyFactoryBean 설정정보 추가 필요  
       -> 어떻게 해결?

#### 중복 문제의 접근 방법
- 앞서 처리했던 중복문제를 돌이켜보자
    - JDBC API 사용하는 DAO
        - 템플릿과 콜백, 클라이언트로 나눠서 해결
        - 전략패턴, DI
    - 반복적인 위임 코드가 필요한 프록시 클래스 코드
        - 다이내믹 프록시라는 런타임 코드 자동생성 기업
        - 변하지 않는 부분은 런타임에 자동 생성하고
        - 변하는 부가기능은 다이내믹 프록시 생성 팩토리에 DI
    - 반복적인 ProxyFactoryBean 설정정보 추가
        - 어떻게?

#### 빈 후처리기를 이용한 자동 프록시 생성기
- 스프링은 컨테이너로서 제공하는 기능 중 변하지 않는 핵심적인 부분 외에는 대부분 확장 포인트를 제공
- `BeanPostProcessor` 인터페이스를 구현한 빈 후처리기
    - 빈 오브젝트가 생성된 이후 가공
    - `DefaultAdvisorAutoProxyCreator`
    - 어드바이저를 이용한 자동 프록시 생성기
    - 빈 후처리기를 빈으로 등록해두면 자동으로 빈이 생성될 때마다 후처리기를 거친다
        - 이를 이용해서 빈의 일부를 프록시로 포장하거나, 프록시를 빈으로 등록할 수도 있다
- 반복적인 ProxyFactoryBean 설정정보 추가를 자동으로 해결

#### 확장된 포인트컷
- 포인트컷의 두 가지 기능
    - 타깃 메소드 중 어떤 메소드에 부가기능을 적용할지 선별
    - 등록된 빈 중에서 어떤 빈에 프록시를 적용할지 선별
```java
package org.springframework.aop;

public interface Pointcut {
    Pointcut TRUE = TruePointcut.INSTANCE;
    
    ClassFilter getClassFilter();
    MethodMatcher getMethodMatcher();
}
```
- 앞서 ProxyFactoryBean에서 포인트컷을 사용할 땐, 타깃이 이미 정해져있음
    - 따라서 MethodMatcher만 수행하는 `NameMatchMethodPointcut`을 사용했을 뿐

#### 포인트컷 테스트
```java
NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
    @Override
    public ClassFilter getClassFilter() {
        return aClass -> aClass.getSimpleName().startsWith("HelloT");
    }
};
```

### 6.5.2 DefaultAdvisorAutoProxyCreator 의 적용
#### 클래스 필터를 적용한 포인트컷 작성
```java
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {

    public void setMappedClassName(String mappedClassName) {
        this.setClassFilter(new SimpleClassFilter(mappedClassName));
    }

    static class SimpleClassFilter implements ClassFilter {

        private final String mappedName;

        public SimpleClassFilter(String mappedName) {
            this.mappedName = mappedName;
        }

        @Override
        public boolean matches(Class<?> aClass) {
            return PatternMatchUtils.simpleMatch(mappedName, aClass.getSimpleName());
        }
    }
}
```

#### 어드바이저를 이용하는 자동 프록시 생성기 등록
1. `DefaultAdvisorAutoProxyCreator`는 등록된 빈들 중 `Advisor`인터페이스를 구현한 것을 모두 찾음 
2. 생성된 모든 빈에 어드바이저의 포인트컷을 적용해보면서 프록시 적용 대상 유무를 판별
3. 적용 대상 빈이라면 프록시를 만들어 원래의 빈 오브젝트와 바꿔치기

#### 포인트컷 등록
- 예제코드 구현 확인필요❓❓❓

#### 어드바이스와 어드바이저
- 명시적으로 어드바이저를 DI하지 않아도 됨
- 자동으로 프록시 생성되어 DI

#### ProxyFactoryBean 제거와 서비스 빈의 원상복구

#### 자동 프록시 생성기를 사용하는 테스트

#### 자동생성 프록시 확인
1. 트랜잭션이 필요한 빈에 적용됐는지 확인
2. 아무 빈에나 트랜잭션이 적용된 것은 아닌지 확인

```log
# 로그를 찍어보면 빈에 적용되는 어드바이저 목록을 확인할 수 있다

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;

import java.util.List;

public class MyDefaultAdvisorAutoProxyCreator extends DefaultAdvisorAutoProxyCreator {
	
	@Override
	protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		System.out.println("beanClass = " + beanClass);
		System.out.println("beanName = " + beanName);
		System.out.println("super.findEligibleAdvisors(beanClass, beanName); = " + super.findEligibleAdvisors(beanClass, beanName));
		System.out.println("============================");
		return super.findEligibleAdvisors(beanClass, beanName);
	}
}

============================
beanClass = class io.zingoworks.demospringbook.user.service.JavaMailSenderImpl
beanName = javaMailSenderImpl
super.findEligibleAdvisors(beanClass, beanName); = []
============================
beanClass = class io.zingoworks.demospringbook.user.service.TestUserService
beanName = testUserService
super.findEligibleAdvisors(beanClass, beanName); = [org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [io.zingoworks.demospringbook.hello.NameMatchClassMethodPointcut: [upgrade*]]; advice [io.zingoworks.demospringbook.user.service.TransactionAdvice@ca66933]]
============================
beanClass = class io.zingoworks.demospringbook.user.service.UserServiceImpl
beanName = userService
super.findEligibleAdvisors(beanClass, beanName); = [org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [io.zingoworks.demospringbook.hello.NameMatchClassMethodPointcut: [upgrade*]]; advice [io.zingoworks.demospringbook.user.service.TransactionAdvice@ca66933]]
============================
beanClass = class io.zingoworks.demospringbook.hello.message.MessageFactoryBean
beanName = message
super.findEligibleAdvisors(beanClass, beanName); = []
============================
```

### 6.5.3 포인트컷 표현식을 이용한 포인트컷
- 포인트컷 표현식을 이용하면 간단하고 효과적으로 클래스와 메소든 선정 알고리즘 작성 가능
#### 포인트컷 표현식
- `AspectJExpressionPointcut` 클래스 사용
#### 포인트컷 표현식 테스트
#### 포인트컷 표현식을 이용하는 포인트컷 적용
```java
@Bean
public AspectJExpressionPointcut transactionPointcut() {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("execution(* *..*ServiceImpl.upgrade*(..))");
    return pointcut;
}
```
#### 타입 패턴과 클래스 이름 패턴
- 포인트컷 표현식의 클래스 이름에 적용되는 패턴은 타입 패턴(클래스 이름 패턴이 아님!)

### 6.5.4 AOP란 무엇인가?
#### 트랜잭션 서비스 추상화
- 인터페이스와 DI를 통해 무엇을 하는지는 남기고
- 어떻게 하는지는 분리한다
    - 어떻게 하는지는 비즈니스 로직으로부터 독립
#### 프록시와 데코레이터 패턴
- 무엇을 하는지는 남아있음
    - DI를 이용해 데코레이터 패턴 적용
        - 투명한 부가기능 부여
        - 프록시 역할의 트랜잭션 데코레이터를 거쳐 타깃에 접근
            - 타깃에는 트랜잭션 관련 코드가 남지 않음
#### 다이내믹 프록시와 프록시 팩토리 빈
- 트랜잭션 필요 메소드마다 프록시 클래스 구현이 필요
    - 런타임 시 자동생성해주는 JDK 다이내믹 프록시 기술 적용
        - 오브젝트 단위의 중복은 여전히 문제
            - 프록시 기술을 추상화한 스프링의 프록시 팩토리 빈을 이용, DI 도입
                - 어드바이스와 포인트컷의 재사용
#### 자동 프록시 생성 방법과 포인트컷
- 트랜잭션 적용 대상 빈에 일일히 프록시 팩토리 빈 설정 필요
    - 빈 생성 후처리 기법으로 자동화
        - 포인트컷 표현식으로 설정 자동화
#### 부가기능의 모듈화
- 다양한 기법으로 `TransactionAdvice` 이름으로 모듈화
    - 재사용 가능, 변경의 집중
#### AOP: 애스펙트 지향 프로그래밍
- 에스펙트 : 부가기능 어드바이스 + 적용대상 포인트컷
    - 어드바이저는 단순한 형태의 에스펙트라고 볼 수 있음
- 핵심로직의 개발, 부가기능의 개발이 분리

### 6.5.5 AOP 적용기술
#### 프록시를 이용한 AOP
#### 바이트코드 생성과 조작을 통한 AOP
- AspectJ 와 같은 AOP 프레임워크는 프록시를 사용하지 않고 바이트코드 조작
    - 컴파일된 타깃 클래스 파일 자체를 수정
    - 클래스가 JVM에 로딩되는 시점에 가로채서 바이트코드 조작
        - 장점1. 스프링의 DI 컨테이너의 도움을 받지 않아도 AOP 적용 가능
        - 장점2. 강력하고 유연함, 적용가능한 대상과 시점이 넓어짐

### 6.5.6 AOP의 용어
- 타깃
- 어드바이스
- 조인포인트
- 포인트컷
- 프록시
- 어드바이저
- 애스펙트

### 6.5.7 AOP 네임스페이스
- 자동 프록시 생성기
- 어드바이스
- 포인트컷
- 어드바이저

#### AOP 네임스페이스
#### 어드바이저 내장 포인트컷

---

## 6.6 트랜잭션 속성
- `DefaultTransactionDefinition`의 용도는 무엇일까?
### 6.6.1 트랜잭션 정의
- `DefaultTransactionDefinition`이 구현하고 있는 `TransactionDefinition`인터페이스는 트랜잭션의 동작방식에 영향을 주는 네 가지 속성을 정의
```java
public class DefaultTransactionDefinition implements TransactionDefinition, Serializable {
```
```java
package org.springframework.transaction;

import org.springframework.lang.Nullable;

public interface TransactionDefinition {
    int PROPAGATION_REQUIRED = 0;
    int PROPAGATION_SUPPORTS = 1;
    int PROPAGATION_MANDATORY = 2;
    int PROPAGATION_REQUIRES_NEW = 3;
    int PROPAGATION_NOT_SUPPORTED = 4;
    int PROPAGATION_NEVER = 5;
    int PROPAGATION_NESTED = 6;
    int ISOLATION_DEFAULT = -1;
    int ISOLATION_READ_UNCOMMITTED = 1;
    int ISOLATION_READ_COMMITTED = 2;
    int ISOLATION_REPEATABLE_READ = 4;
    int ISOLATION_SERIALIZABLE = 8;
    int TIMEOUT_DEFAULT = -1;

    default int getPropagationBehavior() {
        return 0;
    }

    default int getIsolationLevel() {
        return -1;
    }

    default int getTimeout() {
        return -1;
    }

    default boolean isReadOnly() {
        return false;
    }

    @Nullable
    default String getName() {
        return null;
    }

    static TransactionDefinition withDefaults() {
        return StaticTransactionDefinition.INSTANCE;
    }
}
```
#### 트랜잭션 전파
- 경계에서 진행 중인 트랜잭션의 유무에 따라 어떻게 동작할 것인가를 결정
- PROPAGATION_REQUIRED
    - 있으면 참여하고, 없으면 새로 시작
- PROPAGATION_REQUIRES_NEW
    - 항상 새로 시작
- PROPAGATION_NOT_SUPPORTED
    - 트랜잭션 없이 동작
- ...
- 트랜잭션 매니저에서 `getTransaction()`을 사용하는 이유도 전파 속성에 따라 항상 새로운 트랜잭션을 시작하지 않기 때문

#### 격리수준
- 모든 DB 트랜잭션은 격리수준을 갖고 있어야 한다.
- 동시에 많은 트랜잭션을 진행시키면서도 문제 없도록 제어하기 위함
- DB
    - JDBC 드라이버, DataSource 설정
        - 트랜잭션 단위 조정
- `DefaultTransactionDefinition`의 기본 수준은 `ISOLATION_DEFAULT`
```java
public class DefaultTransactionDefinition implements TransactionDefinition, Serializable {
    public static final String PREFIX_PROPAGATION = "PROPAGATION_";
    public static final String PREFIX_ISOLATION = "ISOLATION_";
    public static final String PREFIX_TIMEOUT = "timeout_";
    public static final String READ_ONLY_MARKER = "readOnly";
    static final Constants constants = new Constants(TransactionDefinition.class);
    private int propagationBehavior = 0;
    private int isolationLevel = -1;
	
public interface TransactionDefinition {
    int PROPAGATION_REQUIRED = 0;
    int PROPAGATION_SUPPORTS = 1;
    int PROPAGATION_MANDATORY = 2;
    int PROPAGATION_REQUIRES_NEW = 3;
    int PROPAGATION_NOT_SUPPORTED = 4;
    int PROPAGATION_NEVER = 5;
    int PROPAGATION_NESTED = 6;
    int ISOLATION_DEFAULT = -1; // DefaultTransactionDefinition default
```

#### 제한시간
- 트랜잭션 수행의 제한시간을 설정
    - 트랜잭션을 직접 시작하는 전파속성일 경우에만 유효
#### 읽기전용
- 트랜잭션 내에서 데이터 조작하는 시도를 막아줌
    - \+ 성능 향상의 여지 존재

### 6.6.2 트랜잭션 인터셉터와 트랜잭션 속성
- 메소드별로 다른 트랜잭션 정의를 사용하려면 어드바이스의 기능을 확장
#### TransactionInterceptor
- 트랜잭션 정의를 통한 네가지 조건 + 롤백 대상 예외 종료  
  -> 합쳐서 `TransactionAttribute` 속성 
#### 메소드 이름 패턴을 이용한 트랜잭션 속성 지정
- PROPAGATION_NAME (required)
- ISOLATION_NAME
- readOnly
- timeout_NNNN
- -Exception1 (rollback)
- +Exception2 (commit)

#### tx 네임스페이스를 이용한 설정 방법

### 6.6.3 포인트컷과 트랜잭션 속성의 적용 전략

#### 트랜잭션 포인트컷 표현식은 타입 패턴이나 빈 이름을 이용한다
- 조회는 읽기전용으로 설정하면 성능 향상을 가져올 수도 있다
- 복잡한 조회는 제한시간 지정 가능
- 포인트컷 표현식 대신 스프링의 bean() 표현식도 가능
    - bean(*Service) 형태
#### 공통된 메소드 이름 규칙을 통해 최소한의 트랜잭션 어드바이스와 속성을 정의한다
- 가장 단순한 디폴트 속성으로 전역설정 후에 추가적인 속성을 더해나가는 것이 좋다
#### 프록시 방식 AOP는 같은 타깃 오브젝트 내의 메소드를 호출할 때는 적용되지 않는다
- 프록시를 통한 부가기능 적용은 클라이언트로부터 호출이 일어날 때만 가능
- 타깃 오브젝트가 자기 자신의 메소드를 호출할 때는 일어나지 않음
- 복잡한 트랜잭션 속성 설정 시에, 예상과 달리 무시되는 경우를 주의해야 함
- 타깃 안에서의 호출에 프록시를 적용하는 법
    1. 스프링 API를 통해 프록시 사용을 강제하는 방법
    2. AspectJ와 같이 바이트코드를 조작하여 AOP 적용

### 6.6.4 트랜잭션 속성 적용
#### 트랜잭션 경계설정의 일원화
- 경계설정의 부가기능을 여러 계층에서 적용하는 건 좋지 않다
    - 특정 계층을 트랜잭션 경계로 정했다면, 다른 계층에서 DAO에 직접 접근하지 않도록 해야한다

#### 서비스 빈에 적용되는 포인트컷 표현식 등록
- xml 코드

#### 트랜잭션 속성을 가진 트랜잭션 어드바이스 등록
- xml 코드

#### 트랜잭션 속성 테스트
- 학습테스트 작성
- `TransientDataAccessResourceException`
    - `DataAccessException`의 한 종류로 일시적인 예외상황(재시도 시 성공 가능성 존재)
        - 쓰기작업에 읽기전용 트랜잭션이 걸려있어서 실패
- 이거 구현이 안되넹❓❓❓

## 6.7 애노테이션 트랜잭션 속성과 포인트컷
- 기본적으로 일괄적인 적용
    - 클래스/메소드에 따라 적용이 필요한 경우도 있다
        - 직접 타깃에 애노테이션을 통해 트랜잭션 속성 부여

### 6.7.1 트랜잭션 애노테이션

#### @Transactional
- 타깃 : 메소드, 타입
    - `TransactionAttributeSourcePointcut`

#### 트랜잭션 속성을 이용하는 포인트컷
- `TransactionInterceptor`
- `AnnotationTransactionAttributeSource`

#### 대체 정책(fallback)
- 메소드 적용 우선순위
    1. 타깃 메소드
    2. 타깃 클래스
    3. 선언 메소드
    4. 선언 타입(클래스, 인터페이스)

```java
[우선순위]

[4]
public interface Service {
	[3]
	void method1();
	[3]
	void method2();
}

[2]
public class ServiceImpl implements Service {
	[1]
	public void method1() {};
	[1]
	public void method2() {};
}
```

- 기본적으로 @Transactional 적용 대상은 클라이언트가 사용하는 인터페이스로 애노테이션도 인터페이스에 두는 게 바람직
    - 하지만 인터페이스를 사용하는 프록시 방식의 AOP가 아닌 경우 무시됨❓❓❓
    - 따라서 안전하게 타깃 클래스에 두는 것을 권장
        - 프록시 방식 AOP 종류와 특징, 비 프록시 방식 AOP 동작원리를 잘 이해한다면 인터페이스에 적용 가능

#### 트랜잭션 애노테이션 사용을 위한 설정
- xml 설정

### 6.7.2 트랜잭션 애노테이션 적용

## 6.8 트랜잭션 지원 테스트
### 6.8.1 선언적 트랜잭션과 트랜잭션 전파 속성
- AOP를 이용해 코드 외부에서 트랜잭션 기능을 부여해주고 속성을 지정 : 선언적 트랜잭션
- `TransactionTemplate`이나 개별 데이터 기술의 트랜잭션 API를 통해 직접 코드 안에서 사용하는 방법 : 프로그램에 의한 트랜잭션

### 6.8.2 트랜잭션 동기화와 테스트
- AOP 덕분에 프록시를 이용한 트랜잭션 부가기능 적용
    - 추상화 덕분에 데이터 액세스 기술과 상관없이 AOP를 통한 선언택 트랜잭션, 트랜잭션 전파 가능

#### 트랜잭션 매니저와 트랜잭션 동기화
- 트랜잭션 추상화 기술의 핵심
    - 트랜잭션 매니저
        - 데이터 액세스 기술의 종류에 상관없이 일관된 트랜잭션 제어
    - 트랜잭션 동기화
        - 트랜잭션 전파라는 개념을 가능케함

#### 트랜잭션 매니저를 이용한 테스트용 트랜잭션 제어
#### 트랜잭션 동기화 검증
#### 롤백 테스트

### 6.8.3 테스트를 위한 트랜잭션 애노테이션
#### @Transactional
#### @Rollback
#### @TransactionConfiguration
#### NotTransactional 과 Propagation.NEVER
#### 효과적인 DB 테스트

---
테스트에 빈 설정해도 컴포넌트 스캔하는지?
상속,확장한거까지 포인트컷 표현에서 다 잡아가는지?
