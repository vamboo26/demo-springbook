### Week4

209p ~ 277p

# 3장 템플릿
- 개방 폐쇄 원칙
- `변경이 거의 일어나지 않는 코드`를 자유롭게 변경되는 부분으로부터 `독립`

## 3.1 다시 보는 초난감 DAO
### 3.1.1 예외처리 기능을 갖춘 DAO
- DB 커넥션과 같이 `제한적인 리소스`를 `공유`해 사용하는 경우, 해당 리소스의 `반환`이 보장되어야 한다
    - 이를 위해 필요한 것이 **예외처리**
    
- JDBC 수정 기능의 예외처리 코드
- JDBC 조회 기능의 예외처리 코드

## 3.2 변하는 것과 변하지 않는 것
### 3.2.1 JDBC try/catch/finally 코드의 문제점
- 코드폭탄 🔥
- 핵심은 `변하지 않으며 중복으로 많은 곳에서 쓰이는 코드 분리`

### 3.2.2 분리와 재사용을 위한 디자인 패턴 적용
#### 메소드 추출
- 변하지 않는 부분이 변하는 부분을 감싸고 있을 때 추출하기 어렵다 💩
#### 템플릿 메소드 패턴의 적용
- 상속을 통한 기능의 확장
    - 변하지 않는 부분은 슈퍼클래스, 변하는 부분은 추상 메소드로 서브 클래스
    - DAO 로직마다 새로운 클래스(서브클래스, 구현체)를 만들어야한다 💩
    - 확장 구조가 설계 시점에 고정, 컴파일 시점에 정해진 관계 -> 떨어지는 관계의 유연성 💩
#### 전략 패턴의 적용
- 오브젝트를 분리하고 **인터페이스**를 통해 의존
    - 필요에 따라 `컨텍스트`는 유지되면서 `구체적인 전략`은 바뀌는 것이 전략
    - 예제에선 구현 클래스를 직접 알고 사용한다 💩

    #### DI 적용을 위한 클라이언트/컨텍스트 분리
    - 앞선 문제 해결을 위해 고민해보자
        - 클라이언트가 컨텍스트가 필요로 하는 전략을 만들어서 제공하도록 한다
        - deleteAll()은 strategy만 정해서 **주입** ⭐

## 3.3 JDBC 전략 패턴의 최적화
- 클라이언트 : `UserDao.*` 컨텍스트에 적절한 전략을 주입(메소드에 해당하는) 
- 컨텍스트 : `UserDao.jdbcContextWithStatementStrategy()` PreparedStatement를 실행하는 JDBC의 작업 흐름
- 전략 : `StatementStrategy.makePreparedStatement()` PreparedStatement의 생성

### 3.3.1 전략 클래스의 추가 정보

### 3.3.2 전략과 클라이언트의 동거
- 아직 부족하다
    - DAO 메소드마다 구현 클래스(전략)를 만들어야 한다
        - 템플릿 메소드 패턴 때 처럼 클래스가 많아진다(**차이점은 런타임 시에 다이나믹하게 DI 해준다는 점**) 💩
        - User와 같이 부가적인 정보가 있는 경우, 전략 클래스에 인스턴스 변수가 있어야 한다는 점 💩

#### 로컬 클래스
- 특정 메소드에서만 사용된다면 별도 클래스 파일을 생성하지 않고, 메소드 내부에서 로컬 클래스를 사용 가능
    - 책에는 일단 장점만 소개됐는데, 잘 쓰이지 않는 이유는 뭘까
#### 익명 내부 클래스
    

```text
중첩 클래스(nested class)
1. 독립적으로 만들어질 수 있는 static class
2. 자신이 정의된 클래스 내부에서만 만들어질 수 있는 inner class
    2-1. member inner class
    2-2. local class
    2-3. anonymous inner class
```

## 3.4 컨텍스트와 DI
- 클라이언트 : `UserDao.*` 컨텍스트에 적절한 전략을 주입(메소드에 해당하는)
- 컨텍스트 : `UserDao.jdbcContextWithStatementStrategy()` PreparedStatement를 실행하는 JDBC의 작업 흐름
- 전략 : `StatementStrategy.makePreparedStatement()`의 concrete methods, PreparedStatement의 생성
### 3.4.1 JdbcContext의 분리
#### 클래스 분리
- UserDao.jdbcContextWithStatementStrategy()를 JdbcContext.workWithStatementStrategy()로 분리
#### 빈 의존관계 변경

