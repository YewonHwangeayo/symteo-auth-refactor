# 🌿 Symteo (심터) - Back-End
Symteo Auth Refactor

소셜 로그인 인증 구조 개선 프로젝트

Overview

심터(Symteo)는 사용자의 정신건강 관리를 지원하는 셀프케어 서비스입니다.

본 리포지토리는 심터 프로젝트에서 발생한 소셜 로그인 인증 문제를 해결하기 위해 인증 구조를 변경한 경험을 정리한 프로젝트입니다.

단순한 오류 수정보다 프론트엔드와 백엔드 간 인증 책임을 재설계하고, 기존 인증 흐름의 제약을 해결하는 데 중점을 두었습니다.

1. Problem

카카오 로그인 과정에서 발생한 KOE101 오류

기존 인증 구조에서는 프론트엔드가 카카오 OAuth 인가 코드를 전달하고, 백엔드가 해당 코드를 이용해 카카오 Access Token을 발급받는 방식을 사용했습니다.

그러나 로그인 과정에서 KOE101 오류가 발생했습니다.

문제 분석 과정에서 프론트엔드의 카카오 SDK 설정과 백엔드에서 사용하는 OAuth 클라이언트 정보가 일치하지 않는 문제를 확인했습니다.

당시 프론트엔드의 SDK 설정을 직접 수정하기 어려운 상황이었기 때문에, 백엔드에서 인증 흐름을 변경하는 방안을 검토했습니다.

2. Architecture

Before: Authorization Code 기반 인증

Client
   |
   | Authorization Code
   v
Backend
   |
   | Token Request
   v
Kakao OAuth Server
   |
   | Access Token
   v
Backend
   |
   | Service JWT
   v
Client

After: Access Token 기반 인증

Client
   |
   | Kakao Access Token
   v
Backend
   |
   | Token Validation
   v
Kakao API
   |
   | User Information
   v
Backend
   |
   | Service JWT
   v
Client

변경된 구조에서는 프론트엔드가 카카오 SDK를 통해 발급받은 Access Token을 백엔드에 전달합니다.

백엔드는 전달받은 토큰을 검증하고 사용자 정보를 확인한 후, 서비스에서 사용할 JWT를 발급하도록 인증 흐름을 변경했습니다.

3. Technical Decisions

인증 책임 분리

기존 방식에서는 백엔드가 OAuth 인가 코드를 Access Token으로 교환하는 역할을 담당했습니다.

변경된 방식에서는 외부 OAuth 토큰 발급과 서비스 인증을 분리했습니다.

구분	기존 구조	변경 구조
OAuth 토큰 발급	백엔드	프론트엔드 SDK
백엔드 입력값	Authorization Code	Access Token
외부 토큰 검증	토큰 교환 과정	백엔드에서 검증
서비스 인증	JWT	JWT
변경 목적	기존 인증 흐름 유지	SDK 설정 제약 해결

기술적 고려사항

외부 Access Token을 전달받는 방식에서는 백엔드가 해당 토큰을 신뢰하기 전에 유효성을 검증해야 합니다.

따라서 외부 OAuth 인증과 내부 서비스 인증을 구분하고, 검증된 사용자 정보를 바탕으로 서비스 JWT를 발급하는 구조를 사용했습니다.

4. Troubleshooting

KOE101 오류 해결

문제

카카오 로그인 과정에서 OAuth 클라이언트 설정과 관련된 오류가 발생했습니다.

원인 분석

프론트엔드 SDK 설정과 백엔드 OAuth 요청에 사용되는 클라이언트 정보의 차이를 확인했습니다.

해결

프론트엔드에서 발급한 Access Token을 백엔드에 전달하고, 백엔드에서 검증한 뒤 서비스 JWT를 발급하도록 인증 흐름을 변경했습니다.

결과

프론트엔드의 기존 SDK 설정을 유지하면서 백엔드 인증 흐름을 변경해 로그인 문제를 해결했습니다.

5. Development Environment

프로젝트는 Spring Boot 기반 백엔드 환경에서 개발했습니다.

빌드 및 실행에 필요한 구체적인 설정은 리포지토리의 Gradle 설정과 환경변수 구성을 기준으로 확인할 수 있습니다.

6. Project Structure

프로젝트의 주요 구성 요소는 다음과 같습니다.

* Controller: 인증 요청 및 응답 처리
* Service: 인증 흐름 및 사용자 처리
* Repository: 사용자 데이터 접근
* Security: 인증 및 접근 제어 관련 설정

실제 패키지 경로와 파일별 역할은 소스코드 구조에 맞춰 추가할 예정입니다.

7. Retrospective

이번 프로젝트에서는 기존 인증 구조에서 발생한 문제를 해결하기 위해 프론트엔드와 백엔드의 역할을 재검토했습니다.

특히 기존 구현을 유지하는 것만이 최선의 선택은 아니며, 기술적 제약과 협업 상황을 고려해 시스템 구조를 변경할 수 있다는 점을 배웠습니다.

또한 외부 OAuth 인증과 내부 서비스 인증을 구분하여 설계하는 과정에서 인증 흐름과 책임 분리의 중요성을 이해하게 되었습니다.
