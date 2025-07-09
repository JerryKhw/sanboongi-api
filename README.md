# Sanboongi API

"산붕이" API 프로젝트입니다.

## ✨ 주요 기능

- **사용자 인증**: JWT 기반의 소셜 로그인 (Google, Kakao)
- **문서 관리**: 개인별 복무상황부 생성, 조회, 수정, 삭제 (CRUD)

## 🛠️ 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot 3, Spring Security, Spring Data JPA
- **데이터베이스**: PostgreSQL
- **DB 마이그레이션**: Flyway
- **인증**: JWT (JSON Web Token)
- **API 문서**: Swagger (Springdoc)
- **빌드 도구**: Gradle

## 🚀 시작하기

### 사전 준비

- Java 21
- PostgreSQL

### 1. 프로젝트 클론

```bash
git clone https://github.com/jerrykhw/sanboongi-api.git
cd sanboongi-api
```

### 2. 환경 변수 설정

프로젝트 루트의 `.env.example` 파일을 복사하여 `.env` 파일을 생성한 후, 자신의 환경에 맞게 값을 채워주세요.

```bash
cp .env.example .env
```

### 3. 빌드 및 실행

```bash
# Gradle 빌드
./gradlew build

# 애플리케이션 실행
java -jar build/libs/app.jar
```

## 📄 API 문서

애플리케이션 실행 후, `local` 또는 `dev` 프로필에서는 아래 주소로 접속하여 Swagger API 문서를 확인할 수 있습니다.

- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🗄️ 데이터베이스 스키마 관리

이 프로젝트는 **Flyway**를 사용하여 데이터베이스 스키마를 관리합니다.

- **마이그레이션 파일 위치**: `src/main/resources/db/migration`
- **작성 규칙**: `V{버전}___{설명}.sql` (예: `V2__add_user_role.sql`)

스키마를 변경해야 할 경우, 위 경로에 새로운 SQL 스크립트 파일을 추가하면 애플리케이션 실행 시 자동으로 데이터베이스에 반영됩니다.