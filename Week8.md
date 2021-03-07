### Week7 2021-03-07

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
# JDK dynamicProxy
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
# 스프링의 ProxyFactoryBean 이용
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
beanClass = class io.zingoworks.demospringbook.user.service.TestUserServiceImpl
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
#### 프록시와 데코레이터 패턴
#### 다이내믹 프록시와 프록시 팩토리 빈
#### 자동 프록시 생성 방법과 포인트컷
#### 부가기능의 모듈화
#### AOP: 애스펙트 지향 프로그래밍

### 6.5.5 AOP 적용기술
#### 프록시를 이용한 AOP
#### 바이트코드 생성과 조작을 통한 AOP

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
