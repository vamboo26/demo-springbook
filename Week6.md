### Week6

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
    - `Level`의 변경은 `Level`에서 처리하면 어떨까❓
  
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
#### 강제 예외 발생을 통한 테스트
#### 테스트 실패의 원인

### 5.2.2 트랜잭션 경계설정
#### JDBC 트랜잭션의 트른잭션 경계설정
#### UserService와 UserDao의 트랜잭션 문제
#### 비즈니스 로직 내의 트랜잭션 경계설정
#### UserService 트랜잭션 경계설정의 문제점

### 5.2.3 트랜잭션 동기화
#### Connection 파라미터 제거
#### 트랜잭션 동기화 적용
#### 트랜잭션 테스트 보완
#### JdbcTemplate과 트랜잭션 동기화

### 5.2.4 트랜잭션 서비스 추상화
#### 기술과 환경에 종속되는 트랜잭션 경계설정 코드
#### 트랜잭션 API의 의존관계 문제와 해결책
#### 스프링의 트랜잭션 서비스 추상화
#### 트랜잭션 기술 설정의 분리

## 5.3 서비스 추상화와 단일 책임 원칙
#### 수직, 수평 계층구조와 의존관계
#### 단일 책임 원칙
#### 단일 책임 원칙의 장점

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













