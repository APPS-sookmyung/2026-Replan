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
- Spring Security OAuth2 / OIDC (Google, HTTP 세션)
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
$env:GOOGLE_CLIENT_ID="Google_OAuth_Client_ID"
$env:GOOGLE_CLIENT_SECRET="Google_OAuth_Client_Secret"
```

## 실행 방법
Google Cloud Console에서 웹 애플리케이션 OAuth 클라이언트를 만들고 승인된 리디렉션 URI에
`http://localhost:8080/login/oauth2/code/google`을 등록합니다. 배포 시에는 실제 HTTPS 도메인을 사용합니다.
동의 화면이 테스트 상태이면 로그인할 계정을 테스트 사용자에 등록합니다.

IDE 실행 구성 또는 실행 프로세스에 위 환경변수를 전달한 후 `./gradlew.bat bootRun`으로 실행합니다.
`.env` 파일은 Spring Boot가 자동으로 읽지 않습니다. 실제 키나 비밀번호를 코드, 문서, 실행 구성 파일에 커밋하지 마세요.
기존 `.gitignore`의 `.env*`, `application-local.properties`, `application-local.yml` 제외 설정을 유지합니다.

브라우저에서 아래 주소로 접속합니다.

```text
http://localhost:8080
```


## 배포 (Render + TiDB)

Render에서 GitHub 저장소를 연결해 **Web Service → Docker → Free**로 생성합니다.
루트 `Dockerfile`이 Gradle Wrapper 9.5.1로 실행 JAR를 빌드하고 Java 17에서 실행합니다.
Docker Command는 비워두고, 헬스 체크 경로는 `/index.html`을 사용합니다.

Render에 아래 환경변수를 등록합니다. 실제 비밀값은 파일에 저장하지 않습니다.

| 환경변수 | 설정값 |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `render` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<HOST>:<PORT>/replan_db?sslMode=VERIFY_IDENTITY` |
| `SPRING_DATASOURCE_USERNAME` | TiDB 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | TiDB 비밀번호 |
| `REPLAN_GEMINI_API_KEY` | 기존 Gemini API 키 |
| `GOOGLE_CLIENT_ID` | 기존 Google OAuth Client ID |
| `GOOGLE_CLIENT_SECRET` | 기존 Google OAuth Client Secret |

`PORT`는 Render가 제공합니다. 로컬에서는 `render` 프로필 없이 기존 MySQL 설정과 기본 포트 8080을 사용합니다.
미리 생성한 빈 `replan_db`에는 기존 `ddl-auto=update` 설정으로 최초 실행 시 테이블이 생성됩니다.
DB 계정에는 테이블·인덱스·제약조건 생성 및 변경 권한이 필요하며, 로컬 데이터는 자동 이전되지 않습니다.

Google Cloud의 승인된 리디렉션 URI에 다음 주소를 추가합니다.

```text
https://<서비스명>.onrender.com/login/oauth2/code/google
```

`render` 프로필은 HTTPS 프록시 헤더와 Secure 세션 쿠키를 적용합니다.
실제 로그인·데이터 저장·Gemini·사용자별 접근 분리는 배포된 HTTPS 주소에서 확인합니다.
재배포·재시작 시 다시 로그인해야 하며, Render 무료 서버는 미사용 시 절전 상태가 됩니다.
빌드 확인은 `./gradlew.bat test bootJar`, Docker 빌드는 `docker build -t replan .`로 실행합니다.

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

모든 기록 API는 로그인한 세션이 필요합니다. 비로그인 API 요청은 401, 소유하지 않은 ID는 404,
CSRF 토큰이 없는 변경 요청은 403을 반환합니다. 개인 화면은 비로그인 시 시작 화면으로 이동합니다.
로그인은 `/oauth2/authorization/google`, 성공 후 이동은 `/home.html`입니다.
로그아웃은 CSRF 토큰을 포함한 `POST /logout`으로 세션을 무효화합니다.
Google 계정 자체의 로그아웃은 수행하지 않습니다.

프론트엔드 공통 `auth.js`의 `apiFetch`는 변경 요청 전 `GET /api/csrf`로 토큰을 받은 뒤
응답의 `headerName`과 `token`을 헤더에 넣습니다. 직접 API를 호출할 때도 동일 세션 쿠키와 토큰이 필요합니다.

## 기존 데이터 보존

MySQL 연결 및 Gemini 설정은 유지합니다. 기존 `ddl-auto=update` 설정으로 `app_user` 테이블과
`diary`, `keyword`, `todo`의 nullable `user_id` 외래 키를 추가합니다. 테이블 삭제나 초기화 코드는 없습니다.
계획의 소유자는 `Plan → Todo → User`, 세부 항목의 소유자는 `PlanItem → Plan → Todo → User`로 확인합니다.
기존 소유자 없는 데이터는 보존하지만 어느 사용자에게도 표시하지 않습니다. 과거 기록을 사용하려면
운영자가 실제 소유자를 확인한 후 해당 데이터의 `user_id`를 별도로 연결해야 합니다.

## 검증

`./gradlew.bat test`는 테스트 전용 H2 메모리 DB와 Gemini 모의 응답을 사용합니다.
`src/test/resources/application.properties`의 `create-drop`은 테스트 DB에만 적용됩니다.
실제 MySQL과 외부 Google/Gemini API를 테스트에서 호출하지 않습니다.

자동 테스트는 비로그인 차단, CSRF 토큰 발급 및 세션 요청, 로그아웃 세션 폐기, Google sub 재사용,
사용자별 일기·Todo·계획 조회, 타인 데이터 조회·삭제·AI 계획 생성 차단, 사용자별 키워드 입력,
일기 분석 및 Todo 저장, 계획 재생성과 세부 항목 삭제를 확인합니다.

실제 연동은 다음 순서로 확인하세요.

1. 환경변수를 설정하고 실행한 뒤 Google 로그인 버튼을 누릅니다. 로그인 성공 시 `home.html`로 이동하는지 확인합니다.
2. A 계정으로 일기를 저장·분석하고 Todo와 AI 계획을 생성합니다. 기록 화면의 목록·펼치기·삭제를 확인합니다.
3. 로그아웃 후 개인 화면과 API에 접근할 수 없는지 확인합니다. 같은 계정으로 다시 로그인해 `app_user`의 ID가 유지되는지 확인합니다.
4. 별도 브라우저 또는 시크릿 창에서 B 계정으로 로그인해 A의 기록이 보이지 않는지 확인합니다.
5. B 세션으로 A의 일기·계획 삭제 및 Todo 계획 조회·생성 URL을 호출하면 404인지 확인합니다. 변경 요청에는 B의 CSRF 토큰을 사용해야 합니다.
6. 개발자 도구에서 POST/DELETE에 CSRF 헤더가 붙는지 확인하고, 헤더 없이 재전송하면 403인지 확인합니다.

Google 실제 동의·콜백과 Gemini 실제 응답, 기존 MySQL 스키마에 대한 적용은 위 수동 검증이 필요합니다.


