# Stylo - 패션 커머스 플랫폼

## 🚀 기술 스택

- **Backend**: Spring Boot (Kotlin)
- **Frontend**: Thymeleaf + Tailwind CSS
- **Database**: JOOQ
- **Build Tool**: Gradle

## 🎨 Tailwind CSS 설정

이 프로젝트는 Tailwind CSS v3.4.1을 사용합니다.

### 설치 및 설정

1. **의존성 설치**
   ```bash
   npm install
   ```

2. **CSS 빌드**
   ```bash
   # 개발용 (한 번 빌드)
   npm run build:css
   
   # 개발용 (파일 변경 감지)
   npm run watch:css
   ```

### 커스텀 컴포넌트

프로젝트에는 다음과 같은 커스텀 컴포넌트가 포함되어 있습니다:

- `.btn` - 기본 버튼 스타일
- `.btn-primary` - 주요 액션 버튼
- `.btn-secondary` - 보조 액션 버튼
- `.card` - 카드 컨테이너
- `.input-field` - 입력 필드

### 커스텀 색상

프로젝트 브랜딩을 위한 커스텀 색상이 정의되어 있습니다:

- `primary-50` ~ `primary-900` - 브랜드 메인 컬러

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── kotlin/
│   │   └── project/stylo/
│   │       ├── auth/          # 인증 관련
│   │       ├── common/        # 공통 설정 및 유틸리티
│   │       ├── web/
│   │       │   ├── controller/
│   │       │   ├── dao/       # 데이터 액세스
│   │       │   ├── domain/    # 도메인 모델
│   │       │   ├── dto/       # 데이터 전송 객체
│   │       │   └── service/   # 비즈니스 로직
│   │       └── StyloApplication.kt
│   └── resources/
│       ├── static/
│       │   └── css/
│       │       ├── input.css  # Tailwind 소스
│       │       └── output.css # 빌드된 CSS
│       └── templates/
│           ├── index.html     # 메인 페이지
│           └── not-found.html # 404 페이지
```

## 🛠️ 개발 가이드

### 새로운 페이지 추가

1. `src/main/resources/templates/`에 HTML 파일 생성
2. Tailwind CSS 클래스 사용하여 스타일링
3. Thymeleaf 템플릿 엔진 활용

### CSS 수정

1. `src/main/resources/static/css/input.css` 파일 수정
2. `npm run build:css` 실행하여 CSS 빌드
3. 또는 `npm run watch:css`로 자동 빌드

### 커스텀 스타일 추가

`input.css` 파일의 `@layer` 지시어를 사용하여 스타일을 추가할 수 있습니다:

```css
@layer components {
  .my-custom-component {
    @apply bg-blue-500 text-white px-4 py-2 rounded;
  }
}
```

## 🚀 실행 방법

1. **Spring Boot 애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

2. **CSS 빌드 (별도 터미널에서)**
   ```bash
   npm run watch:css
   ```

3. 브라우저에서 `http://localhost:8080` 접속

