### Week1 2021-01-03

~ 94p

# 스프링이란 무엇인가?

- 스프링의 핵심가치
- 핵심가치를 위한 세 가지 핵심기술

# 1장 오브젝트와 의존관계

- 스프링은 자바 기반
  - 자바에서 중요하게 여기는 객체지향 개념
    - 객체지향 → 오브젝트 설계

## 1.1 초난감 DAO

### 1.1.1 User

- PlantUML

  ![1](https://user-images.githubusercontent.com/41411406/103472196-09537c00-4dce-11eb-8df8-20538c87c27d.png)

- 자바빈 규약을 따르는 오브젝트 구현

자바빈
간단히 두 가지 관례를 따라 만들어진 오브젝트
1. 디폴트 생성자(No args constructor) ← for reflections
2. 프로퍼티(variables with getter, setter)

### 1.1.2 UserDao

- PlantUML

  ![2](https://user-images.githubusercontent.com/41411406/103472197-0a84a900-4dce-11eb-8aaa-8ca512eca956.png)

- JDBC를 이용하는 작업의 플로우
  1. DB연결을 위한 Connection
  2. SQL을 담은 Statement(PreparedStatement)
    - 어떤 차이일까?
  3. Execute statement
  4. Make resultset
  5. Close resources
  6. Catch or throw exceptions

### 1.1.3 main()을 이용한 DAO 테스트 코드

- PlantUML

  ![3](https://user-images.githubusercontent.com/41411406/103472198-0b1d3f80-4dce-11eb-8404-5e8c8f7a974d.png)

- 코드를 완성하고 생각해봐야할 점들
  1. 기능이 동작하는 코드를 개선해야하는 이유는?
  2. 당장의 이점은?
  3. 미래의 이점은?
  4. 객체지향 설계의 원칙에 부합?
  5. DAO를 개선하는 경우와 그대로 사용하는 경우, 스프링 환경에서 차이는?
- `이 책은 위의 질문들을 끊임없이 하면서 읽어보자`

## 1.2 DAO의 분리

### 1.2.1 관심사의 분리

- '현실세계를 그대로 가져온다' 라는 개념보다는 **`가상세계를 효과적으로 구성한다`에 집중해보자**
- 변화를 대비하는 방법
  - 변화의 폭을 줄인다
    - 분리와 확장을 고려
      - 관심사의 분리

### 1.2.2 커넥션 만들기의 추출

- UserDao의 관심사항
  1. DB와 연결을 위한 커넥션
  2. 쿼리실행 및 결과 바인딩
  3. 공유 리소스 해제
- 중복 코드의 추출
- 변경사항에 대한 검증 : 테스트

### 1.2.3 DB 커넥션 만들기의 독립

- PlantUML

  ![4](https://user-images.githubusercontent.com/41411406/103472199-0b1d3f80-4dce-11eb-97b0-3252da222151.png)

- 상속을 통한 확장
- 상속의 단점
  - 이미 다른 목적을 상속한 오브젝트라면?
  - 상속을 통해 서브클래스를 나눈다고 하더라도 이미 너무 밀접한 관계
  - 슈퍼클래스의 변경에 서브클래스가 영향을 받을 여지도 있음
  - 슈퍼클래스에서 확장한 기능을, 상속받지 않은 다른 클래스에서 별도로 구현하려면 중복 발생
- 템플릿 메소드 패턴

  어떤 작업을 처리하는 일부분을 서브 클래스로 캡슐화해 전체 일을 수행하는 구조는 바꾸지 않으면서 특정 단계에서 수행하는 내역을 바꾸는 패턴
  https://gmlwjd9405.github.io/2018/07/13/template-method-pattern.html

- 팩토리 메소드 패턴

  객체 생성 처리를 서브 클래스로 분리해 처리하도록 캡슐화하는 패턴
  https://gmlwjd9405.github.io/2018/08/07/factory-method-pattern.html

- 번외 : 커넥션 풀, 쓰레드 풀 사용경험

## 1.3 DAO의 확장

- 상속의 한계를 알았으니 다른 방법으로 확장해보자

### 1.3.1 클래스의 분리

- PlantUML

  ![5](https://user-images.githubusercontent.com/41411406/103472200-0bb5d600-4dce-11eb-9c08-abed24cd6f90.png)

- 독립적인 클래스 분리
- 별도 역할의 클래스를 만들고 UserDao 이 클래스를 `사용`하도록 해보자
- 한계 : UserDao가 SimpleConenctionMaker에 종속적이다
  - SimpleConenctionMaker의 메서드가 변경되면 UserDao의 코드도 변경 필요
  - 만약 다른 벤더사에서 Simple2ConenctionMaker라는 클래스를 구현하면, UserDao의 코드도 변경 필요

### 1.3.2 인터페이스의 도입

- PlantUML

  ![6](https://user-images.githubusercontent.com/41411406/103472201-0c4e6c80-4dce-11eb-8ebd-4426c520dca2.png)

- 해결 : UserDao - SimpleConnectionMaker 사이에 추상적인 연결고리를 두어 느슨하게 만들어주기

### 1.3.3 관계설정 책임의 분리

- PlantUML

  ![7](https://user-images.githubusercontent.com/41411406/103472202-0ce70300-4dce-11eb-9fbd-5b5073d0c955.png)

- 한계 : UserDao가 벤더 구현체를 알고 있음 → UserDao 소스변경 없이 확장이 자유롭지 못함
- 해결 : 관계설정의 책임을 분리한다
- 여기서 UserDao는 구현체 클래스는 모르고 ConnectionMaker라는 인터페이스 타입을 가지는데, 이게 가능한 개념이 바로 `다형성`
- 인터페이스를 활용한 분리는 상속보다 유연함을 느껴보자, 공감되나요?

### 1.3.4 원칙과 패턴

- 개방 폐쇄 원칙 (Open-Closed Priciple)
  - 클래스나 모듈은 확장에는 열려 있어야 하고 변경에는 닫혀 있어야 한다
- 높은 응집도와 낮은 결합도
  - 높은 응집도
  - 낮은 결합도
- 전략 패턴

UserDao는 DB 연결 방법이라는 기능을 확장하는 데는 열려 있다

UserDao 상관없이 ConnectionMaker를 구현한 구현체들만 다양해지면 됨..

UserDao의 핵심 기능은 외부의 영향을 받지 않음 = 변경에 닫혀 있음

## 1.4 제어의 역전(IoC)

- Inversion of Control

### 1.4.1 오브젝트 팩토리

- PlantUML

  ![8](https://user-images.githubusercontent.com/41411406/103472203-0d7f9980-4dce-11eb-8351-4fc40ff95dd3.png)

- 한계 : UserDaoTest는 지금 원래의 역할인 `UserDao 동작 검증` 에 더해 `UserDao - DB 커넥션 관계 설정`라는 역할도 하고 있다
- 해결 : 해당 역할(관심사)을 새로운 클래스로 분리해주자
  - 이 때 오브젝트 팩토리를 활용해본다

### 1.4.2 오브젝트 팩토리의 활용

- 팩토리에서 생성하는 Dao의 종류가 많아지면, 그에 따라 DB 커넥션 관계 설정하는 코드가 중복된다
  - 이를 메서드로 분리한다

### 1.4.3 제어권의 이전을 통한 제어관계 역전

- 서블릿, JSP, EJB 등 컨테이너 구조 개념
- 템플릿 메소드 패턴
  - 서브클래스의 getConnection에 대한 제어를 슈퍼클래스가 가져감
- 프레임워크

  라이브러리와 프레임워크는 철학이 다르다

- UserDao - DaoFactory
  - UserDao는 자신이 어떤 ConnectionMaker를 받아서 사용할 지 모름
    - 그건 DaoFactory가 제어
- IoC 쓰면 깔끔해진다 + 유연성과 확장성에 좋다, 공감하나?

---