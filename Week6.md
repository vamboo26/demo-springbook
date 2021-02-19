### Week6 2021-02-14

317p ~ 399p

# 5장 서비스 추상화

## 5.1 사용자 레벨 관리 기능 추가
- [x] 사용자 레벨 BASIC, SILVER, GOLD  
- [x] 로그인 50회 이상 SILVER 레벨, SILVER && 30번 이상 추천 시 GOLD
- [x] 레벨은 활동에 따라 업그레이드, 초기 레벨은 BASIC  
- [ ] 변경 작업은 일정 주기를 가지고 일괄 진행  
### 5.1.1 필드 추가
#### Level 이늄
- 일정한 종류의 정보를 `문자열`로 넣는 것은 별로 좋아보이지 않는다❓
   - DB 용량❓
- 정수형 상수 대신 enum으로 관리 ⭐
#### User 필드 추가
#### UserDaoTest 테스트 수정
#### UserDaoJdbc 수정
- JDBC가 사용하는 SQL은 컴파일 과정에서 검증 불가
    - 테스트가 미리 오타 발견 가능 ⭐

### 5.1.2 사용자 수정 기능 추가
#### 수정 기능 테스트 추가
#### UserDao 와 UserDaoJdbc 수정
#### 수정 테스트 보완
- 러프한 테스트는 where 구문이 없다든가의 문제를 잡아내지 못한다
    - 오히려 부적절한 테스트는 잘못된 확신을 줌

### 5.1.3 UserService.upgradeLevels()
- 사용자 레벨 관리 로직은 어디에 위치하면 좋을까?
    - DAO는 데이터를 가져오고 조작
    - 비즈니스 로직을 담을 클래스를 만들자
- UserService를 구현할 때 유의할 점
    - UserDao의 구현클래스가 변경되어도 영향을 받지 않도록 해야한다‼️
#### UserService 클래스와 빈 등록
#### UserServiceTest 테스트 클래스
#### upgradeLevels() 메소드
#### upgradeLevels() 테스트

### 5.1.4 UserService.add()
- 요구사항: 처음 가입하는 사용자는 기본적으로 `BASIC` 레벨이어야 한다
    1. UserDaoJdbc  
       - UserDaoJdbc는 User 오브젝트를 DB에 넣고 읽는 방법에 관심
       - 위와 같은 요구사항은 고려하지 않는 게 적합
    2. User
       - level 필드를 `BASIC`으로 초기화
       - 처음 가입할 때를 제외하면 무의미하기에 문제가 있어 보인다❓
    3. UserService
       - add()에서 담당하면 어떨까? ⭐
### 5.1.5 코드 개선
- 코드 중복
- 코드의 역할이 명확한가
- 코드의 위치가 적절한가
- 변화에 쉽게 대응할 수 있을까 + 앞으로 어떤 변경이 예측되는가
#### upgradeLevels() 메소드 코드의 문제점
- if 블럭이 `Level`의 조건 개수마다 존재한다
  - `Level` 값이 추가되면 조건 if 블럭도 추가 될 여지가 있다
  
```java
Boolean changed = null;
if (user.getLevel() == Level.BASIC && user.getLoginSequence() >= 50) {
    user.setLevel(Level.SILVER);
    changed = true;
} else if (user.getLevel() == Level.SILVER && user.getRecommendationCount() >= 30) {
    user.setLevel(Level.GOLD);
    changed = true;
} else {
    changed = false;
}

if (changed) {
    userDao.update(user);
```

```java
public boolean canUpgradeLevel(User user) {
    Level currentLevel = user.getLevel();
    
    switch (currentLevel) {
        case BASIC:
            return user.getLoginSequence() >= UserService.MIN_LOGIN_SEQUENCE_FOR_SILVER;
        case SILVER:
            return user.getRecommendationCount() >= UserService.MIN_RECOMMEND_FOR_GOLD;
        case GOLD:
            return false;
        default:
            throw new IllegalArgumentException("Unknown Level: " + currentLevel);
    }
}
```

```java
// Level로 관련 로직을 옮기는 건 어떨까❓
// Level이 User의 등급 상승 조건을 알고 있다는 것이 부적절할까❓
@Override
public boolean canUpgradeLevel(User user) {
    return user.getLevel().isUpgradable(user);
}

// User로 관련 로직을 옮기는 건 어떨까❓
// User의 내부 정보가 바뀌는 것은 UserService보단 User가 스스로 다루는 게 적절(updateLevel())
// User의 등급이 업그레이드 가능한 조건인 지 여부를 판별하는 것은 어떨까?
@Override
public boolean canUpgradeLevel(User user) {
    return user.isUpgradable();
}

// 후에 나오는 변경처럼 등급 상승 정책을 인터페이스로 분리한다고 가정하면
// 위의 변경보다는 UserService에서 Policy를 의존하는 게 맞는걸까?
```
#### upgradeLevels() 리팩토링
#### User 테스트
#### UserServiceTest 개선

## 5.2 트랜잭션 서비스 추상화
### 5.2.1 모 아니면 도
#### 테스트용 UserService 대역
- 예외 상황을 테스트하고 싶다
    - 어떻게? 테스트 대역을 이용
#### 강제 예외 발생을 통한 테스트
#### 테스트 실패의 원인

### 5.2.2 트랜잭션 경계설정
- 더 이상 쪼개서 이뤄질 수 없는 원자와 같은 성질의 작업 단위
#### JDBC 트랜잭션의 트른잭션 경계설정
- 로컬 트랜잭션: 하나의 DB 커넥션 안에서 만들어지는 트랜잭션
    - setAutoCommit(false) ~ commit()/rollback() 까지
#### UserService와 UserDao의 트랜잭션 문제
#### 비즈니스 로직 내의 트랜잭션 경계설정
#### UserService 트랜잭션 경계설정의 문제점

### 5.2.3 트랜잭션 동기화
- UserService에서 트랜잭션을 시작하며 만든 Connection을 특별한 저장소에 보곤해두고, 이후에 호출되는 DAO의 메소드에서는 저장된 Connection을 fetch하여 사용하는 방식
- 이 때 저장소는 스레드마다 독립적으로 저장, 관리하기에 멀티스레드 환경에서 이슈가 없음
#### Connection 파라미터 제거
#### 트랜잭션 동기화 적용
#### 트랜잭션 테스트 보완
#### JdbcTemplate과 트랜잭션 동기화
- JdbcTemplate은 트랜잭션 적용 여부에 따라 영리하게 동작하기에 코드에서의 처리가 필요하지 않다

### 5.2.4 트랜잭션 서비스 추상화
- 로컬 트랜잭션으로 처리 불가능한 경우에는?
    - 하나의 트랜잭션 안에서 여러 개의 DB에 데이터 작업
    - 로컬 트랜잭션 <- 하나의 커넥션에 종속
- 글로벌 트랜잭션을 통해 처리 필요
    - 별도의 트랜잭션 관리자, JTA(Java Transaction API)
#### 기술과 환경에 종속되는 트랜잭션 경계설정 코드
- 분산 트랜잭션, 11장에서 보자 ✈️
- 벤더에 따라 로컬 트랜잭션, 글로벌 트랜잭션 필요가 다른데
    - 변경하려면 코드 수정이 필요하다
```java
InitialContext ctx = new InitialContext();
UserTransaction tx = (UserTransaction) ctx.lookup(USER_TX_JNDI_NAME);
tx.begin();
Connection c = dataSource.getConnection();
try {
    // 데이터 액세스 코드
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
} finally {
    c.close();
}
```
#### 트랜잭션 API의 의존관계 문제와 해결책
#### 스프링의 트랜잭션 서비스 추상화
- `PlatformTransactionManager`
    - `DataSourceTxmanager`
        - `JDBC/Connection`
        - `JTA/UserTransaction`
    - `HibernateTxManager`
        - `Hibernate/Transaction`
#### 트랜잭션 기술 설정의 분리
- UserService는 PlatformTransactionManager를 사용
    - JDBC의 로컬 트랜잭션: DataSourceTransactionManager 사용
    - JTA 이용 글로벌 트랜잭션: JTATransactionManager
    - ...
## 5.3 서비스 추상화와 단일 책임 원칙
#### 수직, 수평 계층구조와 의존관계
- 추상화 -> 특정 기술환경, 벤더에 종속되지 않는 코드
- 수평적, 수직적 분리가 가져오는 낮은 결합도, 자유로운 확장
#### 단일 책임 원칙
- 변경의 이유가 명확, 변경의 범위가 작음, 기술환경에 의해 변경이 일어나지 않음
#### 단일 책임 원칙의 장점
- 적절한 추상화, 관심의 분리 with 스프링 DI
- 모두가 밀접하다
    - 설계 - 원칙 - 변경 - 책임 - 테스트

## 5.4 메일 서비스 추상화
### 5.4.1 JavaMail을 이용한 메일 발송 기능
#### JavaMail 메일 발송

### 5.4.2 JavaMail이 포함된 코드의 테스트

### 5.4.3 테스트를 위한 서비스 추상화
#### JavaMail을 이용한 테스트의 문제점
#### 메일 발송 기능 추상화
#### 테스트용 메일 발송 오브젝트
#### 테스트와 서비스 추상화

### 5.4.4 테스트 대역
#### 의존 오브젝트의 변경을 통한 테스트 방법
#### 테스트 대역의 종류와 특징
- 테스트 더블(대역): 테스트 대상이 되는 오브젝트의 기능에만 충실, 자주 테스트를 실행할 수 있도록 사용
    - 테스트 스텁: 테스트 대상 오브젝트의 의존객체
    - 목 오브젝트: 스텁과 달리 행위 검증
#### 목 오브젝트를 이용한 테스트

## 정리

---
### TBU
- https://docs.spring.io/spring-framework/docs/2.5.x/reference/transaction.html
- https://docs.spring.io/spring-framework/docs/2.5.x/reference/aop.html
