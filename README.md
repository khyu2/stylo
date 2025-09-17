# Stylo - 패션 커머스 플랫폼

Spring Boot + Kotlin 기반의 패션 커머스 웹 애플리케이션입니다.

<img width="1624" height="1151" alt="Screenshot 2025-09-18 at 12 38 16 AM" src="https://github.com/user-attachments/assets/7a56bd47-5fdc-4166-86cb-912d78a0ce25" />
<img width="1624" height="1151" alt="Screenshot 2025-09-18 at 12 38 26 AM" src="https://github.com/user-attachments/assets/6255fc49-292b-48e7-8821-87fddf188aeb" />
<img width="1624" height="1151" alt="Screenshot 2025-09-18 at 12 38 31 AM" src="https://github.com/user-attachments/assets/2a5b92ad-f8f6-4131-ba32-14fe58dbab62" />
<img width="1624" height="1151" alt="Screenshot 2025-09-18 at 12 38 43 AM" src="https://github.com/user-attachments/assets/534a92a0-1e30-4d6b-b294-b7e9f14cb9d2" />

---

## 🚀 기술 스택
- Backend: Kotlin + Spring Boot (MVC, Thymeleaf)
- Frontend: Tailwind CSS + daisyUI v5, Lucide icons, Alpine.js
- Database: PostgreSQL, jOOQ
- Build: Gradle (Kotlin DSL)
- Container: Jib (Docker 데몬 불필요)

---

## 📁 프로젝트 구조
```
src/
├─ main/
│  ├─ kotlin/
│  │  └─ project/stylo/
│  │     ├─ auth/                 # 인증 및 @Auth 리졸버
│  │     ├─ common/               # 공통 설정, 예외, 유틸
│  │     ├─ web/
│  │     │  ├─ controller/        # 얇은 MVC 컨트롤러 (뷰 이름 반환)
│  │     │  ├─ dao/               # 데이터 액세스
│  │     │  ├─ domain/            # 도메인 모델
│  │     │  ├─ dto/               # 요청/응답 DTO
│  │     │  └─ service/           # 비즈니스 로직
│  │     └─ StyloApplication.kt
│  └─ resources/
│     ├─ static/
│     │  └─ css/
│     │     ├─ input.css          # Tailwind 입력 (소스)
│     │     └─ output.css         # Tailwind 결과 (서빙 경로: /css/output.css)
│     └─ templates/
│        ├─ layout/
│        │  └─ base.html          # 모든 페이지가 상속하는 레이아웃
│        ├─ components/           # 재사용 가능한 UI 조각
│        ├─ index.html            # 메인 페이지
│        └─ not-found.html        # 404 페이지
```

---

## 🛠️ 개발 가이드 (필수 컨벤션)

### 1) 템플릿 (Thymeleaf)
- 모든 페이지는 `templates/layout/base.html`을 `layout:decorate`로 확장해야 합니다.
- 페이지 본문은 반드시 `layout:fragment="content"` 안에 배치합니다.
- daisyUI v5 컴포넌트를 우선 사용합니다: `btn`, `card`, `badge`, `alert`, `join` 등.
- 데이터 바인딩은 `th:text`, `th:if`, `th:each`를 사용하고, 집계는 `list.![prop]` 프로젝션을 활용합니다.
  - 예) `#aggregates.sum(cartItems.![totalPrice])`
- Lucide 아이콘은 `<i data-lucide="heart">` 형식으로 사용하며, `base.html`에서 `lucide.createIcons()`가 1회 실행되어야 합니다.

### 2) 컨트롤러
- 최대한 얇게 유지하고 뷰 이름만 반환합니다. 모델에는 단순 속성만 추가합니다.
- 인증이 필요한 경우 `@Auth Member`를 사용합니다 (기존 컨트롤러와 동일한 패턴).
- 라우팅은 복수 자원명 사용: `/products`, `/cart`, `/orders`.

### 3) Kotlin 코드 스타일
- 작은 클래스를 선호하고, 함수는 가급적 30줄 내외로 유지합니다.
- 불변 `val` 사용, 빠른 반환(early return), 명확한 네이밍.
- 예외는 기존 `BaseException` + 타입과 예외 enum을 사용합니다.

### 4) UI/UX
- daisyUI v5 기반 레이아웃과 컨트롤을 사용하고, `<html data-theme="light">`를 기본으로 합니다.
- 간격/레이아웃은 Tailwind 유틸리티(`px-4 py-8`, `space-y-*`, `gap-*`)로 구성합니다.
- 버튼: 주 버튼 `btn btn-primary`, 보조 버튼 `btn btn-ghost`.

---

## 🎨 Tailwind & 프론트엔드 빌드
- Tailwind CSS v3.4.x + daisyUI v5 사용.
- 스타일은 `/css/output.css` 경로로 서빙됩니다(정적 리소스: `src/main/resources/static/css/output.css`).

설치 및 빌드
```bash
# 프론트 의존성 설치
npm install

# 개발용 1회 빌드
npm run build:css

# 변경 감지(Watch)
npm run watch:css
```
- Lucide 아이콘, Alpine.js 초기화는 `layout/base.html`에서 수행합니다.

---

## ⚙️ 실행 & 테스트
애플리케이션 실행
```bash
./gradlew bootRun
```

백엔드 빌드/테스트
```bash
# 전체 빌드 (테스트 포함)
./gradlew build
```
- 테스트는 빠르고(isolated) 명확한 이름으로 작성합니다.

프론트 CSS Watch는 별도 터미널에서 실행하세요:
```bash
npm run watch:css
```

---

## 🔧 환경설정
- 애플리케이션 설정: `src/main/resources/application.yml`
- 환경 변수: `.env`를 사용할 수 있으며, 로컬 개발 시 데이터베이스/외부 키를 관리합니다.
- 데이터베이스: PostgreSQL. jOOQ 사용 시 Gradle 태스크로 코드 생성하도록 구성할 수 있습니다.

---

## 🐳 Docker 이미지 (Jib)
이 프로젝트는 [Google Jib](https://github.com/GoogleContainerTools/jib) Gradle 플러그인으로 컨테이너 이미지를 생성합니다.

로컬 Docker 데몬에 이미지 빌드
```bash
./gradlew jib jibDockerBuild
```

타르 파일로 이미지 내보내기
```bash
./gradlew jib jibBuildTar
```

레지스트리로 바로 푸시
```bash
./gradlew jib jib
```

- 태그는 `${version}`과 `latest`가 함께 적용됩니다.
- Docker Hub 사용 시 Personal Access Token 사용을 권장합니다.
