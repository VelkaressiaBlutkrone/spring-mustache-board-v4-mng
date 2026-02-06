# 인증 블로그

## 기술 스택

- 세션, 쿠키
- ORM
- Lazy Loading
- Response DTO
- Optional, Stream
- 인증 (403, 401 에러 체크)

---

## 리팩토링

- ResponseDTO를 내부 클래스로 수정

---

## 기능

- 회원가입 (아이디 중복 체크)
- 로그인 (쿠키)
- 게시글 작성 (인증된 사용자만 가능)
- 게시글 상세보기 (인증 권한 체크, DTO 생성)
- 게시글 수정/삭제 (인증 권한 체크)

---

## 작업(Task)

### 회원가입

- [x] mustache 파일 생성
- [x] Controller에서 화면 호출
- [x] user 테이블 생성
- [x] Repository DB 테스트
- [x] Controller, Service, Repository 연결 및 기능 완료
- [x] 로그인 완료

### 로그인

- [x] 세션 만들기
- [x] 로그인 사용자 세션 저장

### 게시판

- [x] mustache 파일 수정
- [x] board 테이블에 user 테이블 조인
- [x] mustache 사용자 정보 출력
- [x] 사용자 세션 정보로 게시글 확인인

---
