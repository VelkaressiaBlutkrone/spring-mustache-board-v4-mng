# Spring Board v4 - 인증 기반 게시판

Spring Boot 기반의 세션 인증 게시판 애플리케이션입니다. 회원가입/로그인, 게시글 CRUD, 댓글, YouTube 링크 임베딩 기능을 제공합니다.

---

## 기술 스택

| 구분       | 기술                         |
| ---------- | ---------------------------- |
| Framework  | Spring Boot 4.0.2            |
| Language   | Java 21                      |
| Build      | Gradle                       |
| Database   | H2 (In-Memory)               |
| ORM        | Spring Data JPA / Hibernate  |
| Template   | Mustache                     |
| Validation | Jakarta Validation           |
| AOP        | AspectJ                      |
| Editor     | Summernote (WYSIWYG)         |
| Parsing    | JSoup 1.22.1                 |
| CSS        | Bootstrap 5.3.3              |

---

## 실행 방법

```bash
# 빌드
gradlew build

# 개발 서버 실행 (자동 재시작)
gradlew bootRun --continuous

# 전체 테스트
gradlew test

# 단일 테스트 클래스 실행
gradlew test --tests "com.example.v4.user.repository.UserRepositoryTest"
```

- 서버: `http://localhost:8080`
- H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:test`)
- 샘플 데이터가 `db/data.sql`에서 자동 로드됩니다.

---

## 기능

### 회원

- 회원가입 (아이디 중복 체크, 유효성 검증)
- 로그인 / 로그아웃 (세션 기반 인증, 쿠키 저장)

### 게시판

- 게시글 목록 조회 (최신순 정렬)
- 게시글 작성 (인증 사용자만, Summernote 에디터)
- 게시글 상세보기 (작성자 정보, 댓글 목록 포함)
- 게시글 수정 / 삭제 (작성자 본인만 가능)
- YouTube URL 자동 임베딩 (iframe 변환)

### 댓글

- 댓글 작성 (인증 사용자만)
- 댓글 삭제 (작성자 본인만)

---

## API 엔드포인트

### 페이지 (HTML)

| Method | URL                        | 설명          | 인증   |
| ------ | -------------------------- | ------------- | ------ |
| GET    | `/`                        | 게시글 목록   | -      |
| GET    | `/join-form`               | 회원가입 폼   | -      |
| GET    | `/login-form`              | 로그인 폼     | -      |
| POST   | `/join`                    | 회원가입 처리 | -      |
| POST   | `/login`                   | 로그인 처리   | -      |
| GET    | `/logout`                  | 로그아웃      | -      |
| GET    | `/board/save-form`         | 글쓰기 폼     | 필요   |
| GET    | `/board/detail/{id}`       | 게시글 상세   | -      |
| GET    | `/board/update-form/{id}`  | 수정 폼       | 작성자 |
| POST   | `/board/insert`            | 게시글 작성   | 필요   |
| POST   | `/board/{id}/update`       | 게시글 수정   | 작성자 |
| POST   | `/board/{id}/delete`       | 게시글 삭제   | 작성자 |
| POST   | `/reply/save`              | 댓글 작성     | 필요   |
| POST   | `/reply/{id}/delete`       | 댓글 삭제     | 작성자 |

### REST API (JSON)

| Method | URL                              | 설명                          |
| ------ | -------------------------------- | ----------------------------- |
| GET    | `/api/user/info?writerId={id}`   | 사용자 정보 조회 (내부 API)   |

---

## 프로젝트 구조

```text
src/main/java/com/example/v4/
├── SpringBoardApplication.java
├── user/                          # 회원 도메인
│   ├── controller/
│   │   ├── UserController.java        # 회원가입, 로그인, 로그아웃
│   │   └── UserRestController.java    # 사용자 정보 REST API
│   ├── service/UserService.java
│   ├── repository/UserRepository.java
│   ├── entity/User.java
│   └── dto/
│       ├── UserRequestDto.java        # Join, Login 레코드
│       └── UserResponseDto.java
├── board/                         # 게시판 도메인
│   ├── controller/BoardController.java
│   ├── service/BoardService.java      # YouTube 파싱, 권한 체크
│   ├── repository/BoardRepository.java
│   ├── entity/Board.java
│   ├── mapper/BoardMapper.java        # Entity ↔ DTO 변환
│   └── dto/
│       ├── BoardRequestDto.java
│       └── BoardReponseDto.java
├── reply/                         # 댓글 도메인
│   ├── controller/ReplyController.java
│   ├── service/ReplyService.java
│   ├── repository/ReplyRepository.java
│   ├── entity/Reply.java
│   └── dto/
│       ├── ReplyRequestDto.java
│       └── ReplyResponseDto.java
└── global/                        # 공통 모듈
    ├── annotation/
    │   ├── LoginUser.java             # 세션 사용자 주입 어노테이션
    │   └── Loggable.java              # AOP 로깅 어노테이션
    ├── aop/LoggingAspect.java         # 메서드 실행 로깅
    ├── config/
    │   ├── WebMvcConfig.java          # 인터셉터, 리졸버 등록
    │   ├── RestClientConfig.java      # RestClient 빈 설정
    │   ├── SessionUserArgumentResolver.java
    │   └── LoginUserInterceptor.java  # 템플릿 세션 정보 노출
    ├── dto/
    │   ├── SessionUser.java           # 세션 저장용 레코드
    │   └── Dto.java                   # 내부 API용 DTO
    ├── exception/                     # 도메인별 커스텀 예외
    │   ├── GlobalExceptionHandler.java    # HTML/JSON 분기 처리
    │   ├── BoardAccessDeniedException.java
    │   ├── BoardNotFoundException.java
    │   ├── BoardValidationException.java
    │   ├── InvalidBoardIdException.java
    │   ├── RestClientErrorException.java
    │   ├── RestServerErrorException.java
    │   ├── UserDuplicationException.java
    │   ├── UserNotFoundException.java
    │   └── UserValidationException.java
    └── resetclient/
        └── RestClients.java           # RestClient 래퍼 (GET/POST/PUT/DELETE)
```

```text
src/main/resources/
├── application.properties
├── db/data.sql                    # 샘플 데이터
├── templates/
│   ├── header.mustache            # 네비게이션 바
│   ├── bottom.mustache            # 푸터, 알림 메시지
│   ├── index.mustache             # 게시글 목록
│   ├── user/
│   │   ├── join-form.mustache     # 회원가입 폼
│   │   └── login-form.mustache    # 로그인 폼
│   └── board/
│       ├── save-form.mustache     # 글쓰기 폼
│       ├── update-form.mustache   # 수정 폼
│       └── detail.mustache        # 상세보기 (댓글 포함)
└── static/js/
    ├── script.js                  # 알림, 댓글 로그인 체크
    └── summernote-init.js         # Summernote 에디터 초기화
```

---

## 아키텍처 및 주요 패턴

### 계층 구조

`Controller → Service → Repository` 패턴으로 도메인별(user, board, reply) 모듈 분리

### 인증/인가

- **세션 기반 인증**: `SessionUser` 레코드를 HttpSession에 저장
- **`@LoginUser` 어노테이션**: `SessionUserArgumentResolver`가 컨트롤러 파라미터에 자동 주입
- **`LoginUserInterceptor`**: 모든 요청에서 `isLogin`, `user` 정보를 템플릿에 노출
- **권한 체크**: Service 계층에서 작성자 본인 여부 확인 후 `BoardAccessDeniedException` 발생

### DTO 패턴

- **Request DTO**: Java Record + Jakarta Validation (`@NotBlank`, `@Size`, `@Email`)
- **Response DTO**: 민감 정보 제외, 화면 표시용 데이터만 포함
- **BoardMapper**: Entity ↔ DTO 변환 전담

### 예외 처리

- `GlobalExceptionHandler`에서 `Accept` 헤더 기반으로 HTML/JSON 응답 분기
- `FlashMap`을 활용한 리다이렉트 간 메시지 전달
- 필드 단위 유효성 에러를 템플릿에 표시

### YouTube 임베딩

- `BoardService.jsoupYouTubeParser()`가 게시글 저장 시 YouTube URL을 iframe으로 변환
- `youtube.com/watch?v=ID`, `youtu.be/ID` 형식 지원

### AOP 로깅

- `LoggingAspect`가 Controller/Service/Repository 메서드 실행을 자동 로깅
- 실행 시간, 인자, 반환값 기록 (민감 정보 마스킹)

### 내부 REST API 호출

- `RestClients` 래퍼를 통해 `/api/user/info` 내부 API 호출
- 작성자 이름 조회 시 사용 (Entity 직접 참조 대신)

---

## 데이터베이스 스키마

### user_tb

| 컬럼          | 타입           | 설명              |
| ------------- | -------------- | ----------------- |
| id            | INTEGER (PK)   | 자동 증가         |
| user_name     | VARCHAR(50)    | 사용자명 (UNIQUE) |
| user_email    | VARCHAR        | 이메일            |
| user_password | VARCHAR(100)   | 비밀번호          |
| created_at    | TIMESTAMP      | 생성일시          |

### board_tb

| 컬럼       | 타입         | 설명       |
| ---------- | ------------ | ---------- |
| id         | INTEGER (PK) | 자동 증가  |
| title      | VARCHAR      | 제목       |
| content    | TEXT         | 내용       |
| writer_id  | INTEGER (FK) | 작성자 ID  |
| created_at | TIMESTAMP    | 생성일시   |

### reply_tb

| 컬럼       | 타입         | 설명       |
| ---------- | ------------ | ---------- |
| id         | INTEGER (PK) | 자동 증가  |
| comment    | VARCHAR      | 댓글 내용  |
| user_id    | INTEGER (FK) | 작성자 ID  |
| board_id   | INTEGER (FK) | 게시글 ID  |
| created_at | TIMESTAMP    | 생성일시   |

---

## 샘플 계정

| 사용자명 | 비밀번호 | 이메일           |
| -------- | -------- | ---------------- |
| ssar     | 1234     | `ssar@email.com` |
| cos      | 1234     | `cos@email.com`  |
