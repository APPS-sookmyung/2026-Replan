# RE:PLAN

RE:PLAN은 일기를 저장하고, Gemini로 일기 내용을 분석한 뒤, 사용자의 최근 상태 키워드를 바탕으로 Todo 계획을 생성하는 Spring Boot 웹 애플리케이션입니다.

## 주요 기능

- 일기 작성 및 저장
- Gemini를 활용한 일기 분석
- 분석된 키워드 저장
- 제목, 마감일, 추가 정보를 포함한 Todo 등록
- AI 기반 계획 생성
- 계획 생성 이유와 시간대별 세부 계획 표시
- 일기 기록 및 계획 기록 조회
- 일기 기록 최신순 정렬
- 일기 기록 삭제
- 계획 기록 삭제
- 계획 기록 클릭 시 전체 계획 펼치기

## 기술 스택

- Java 17
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- MySQL
- Google GenAI Java SDK
- Gradle
- HTML, CSS, JavaScript

## 프로젝트 구조

```text
src/main/java/com/yuan/replan
├── controller
├── dto
├── entity
├── repository
└── service

src/main/resources
├── application.properties
└── static
    ├── index.html
    ├── home.html
    ├── diary.html
    ├── todo.html
    ├── records.html
    ├── css
    │   └── style.css
    └── js
        ├── diary.js
        ├── todo.js
        └── records.js
```

## 실행 전 준비

다음 항목이 필요합니다.

- JDK 17
- MySQL
- Gemini API 키

MySQL에서 사용할 데이터베이스를 먼저 생성합니다.

```sql
CREATE DATABASE replan_db;
```

## 환경변수 설정

애플리케이션 실행 전에 아래 환경변수를 설정해야 합니다.

```powershell
$env:REPLAN_DB_USERNAME="MySQL_사용자명"
$env:REPLAN_DB_PASSWORD="MySQL_비밀번호"
$env:REPLAN_GEMINI_API_KEY="Gemini_API_키"
```

## 실행 방법
브라우저에서 아래 주소로 접속합니다.

```text
http://localhost:8080
```


## 주요 API

### 일기

```http
POST /diary?content=...
GET /diary
DELETE /diary/{diaryId}
POST /diary/analyze?content=...
```

### Todo 및 계획

```http
POST /todo?title=...&deadline=...&additionalInfo=...
GET /todo
POST /todo/{todoId}/plan
GET /todo/{todoId}/plan
GET /plans
DELETE /plans/{planId}
```


