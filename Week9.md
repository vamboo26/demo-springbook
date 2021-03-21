### Week9 2021-03-21

## 7.5 DI를 이용해 다양한 구현 방법 적용하기
- 인터페이스 분리 원칙 실제 구현
- 실시간 변경 작업 시 고려해야 할 첫 번째 : 동시성

### 7.5.1 ConcurrentHashMap을 이용한 수정 가능 SQL 레지스트리
- 멀티스레드 환경에서 HashMap을 조작하려면, Collections.synchronizedMap() 등을 이용해 외부에서 동기화 필요
  - 요청이 많은 서비스에서 성능 문제
- ConcurrentHashMap은 데이터 조작 시 전체 데이터에 대해 락을 걸지 않고 조회는 락을 아예 사용하지 않는다
  - 어느 정도 안전하면서 성능이 보장되는 동기화된 HashMap
    
#### 수정 가능 SQL 레지스트리 테스트clea
#### 수정 가능 SQL 레지스트리 구현

### 7.5.2 내장형 데이터베이스를 이용한 SQL 레지스트리 만들기
- Derby, HSQL, H2
- 애플리케이션 내에서 DB를 기동하고 초기화

#### 내장형 DB 빌더 학습 테스트
#### 내장형 DB를 이용한 SqlRegistry 만들기
- `EmbeddedDatabaseBuilder`는 빈으로 등록 후 초기화 코드가 필요
  - 초기화 코드가 필요하다면 팩토리 빈으로 만드는 것이 좋다 ❓

#### UpdatableSqlRegistry 테스트 코드의 재사용
#### XML 설정을 통한 내장형 DB의 생성과 적용

### 7.5.3 트랜잭션 적용
#### 다중 SQL 수정에 대한 트랜잭션 테스트
- `EmbeddedDbSqlRegistry`에 트랜잭션 기능 추가
#### 코드를 이용한 트랜잭션 적용

## 7.6 스프링 3.1의 DI
#### 자바 언어의 변화와 스프링
- 애노테이션의 메타정보 활용
- 정책과 관례를 이용한 프로그래밍

### 7.6.1 자바 코드를 이용한 빈 설정
#### 테스트 컨텍스트의 변경
#### <context:annotation-config \> 제거
#### <bean>의 전환
#### 전용 태그 전환

### 7.6.2 빈 스캐닝과 자동 와이어링
#### `@Autowired`를 이용한 자동와이어링
#### `@Component`를 이용한 자동 빈 등록

### 7.6.3 컨텍스트 분리와 `@Import`
#### 테스트용 컨텍스트 분리
#### `@Import`

### 7.6.4 프로파일
#### `@Profile`과 `@ActiveProfiles`
#### 컨테이너의 빈 등록 정보 확인
#### 중첩 클래스를 이용한 프로파일 적용

### 7.6.5 프로퍼티 소스
#### `@PropertySource`
#### `PropertySourcesPlaceholderConfigurer`

### 7.6.6 빈 설정의 재사용과 `@Enable*`
#### 빈 설정자
#### `@Enable*` 애노테이션


#### `@Component`를 이용한 자동 빈 등록



