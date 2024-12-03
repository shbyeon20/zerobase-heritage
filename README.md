# 국가문화유산 지도앱 만들기
국가문화유산 공공API 데이터를 기반으로 문화유산의 '지도상위치', '답사경로추천', '방문이력'을 관리하는 지도앱을 만들고자 합니다.


## 주제선택 배경
- 기술적 고려 : 최적화에 관심있어, 컴퓨팅자원 최적화 외 로직 차원에서 최적화문제(경로탐색)를 경험해보고 싶었습니다. 
- 주제적 고려 : 역사와 여행에 관심이 있어 탐험적 요소가 있는 지도앱을 만들어보고 싶었습니다.


## 프로젝트 기능 및 설계
- 외부API데이터 초기화
  - 프로그램 동작시 최초 1회, 국가문화유산 API로부터 데이터 호출 후 내부 데이터 모델에 따라 가공하여 DB에 저장한다.
  - 국가문화유산 API : <https://www.khs.go.kr/cha/SearchKindOpenapiDt.do?ccbaKdcd=11&ccbaAsno=00030000&ccbaCtcd=11>

- 내 주변 문화재 검색
  - 사용자 위치기반으로 일정 거리 내 존재하는 주변 문화유산을 검색한다. 방문한 문화재는 별도로 구분되어 표시된다.

- 문화재 방문처리
  - 회원은 특정 문화유산을 선택하여 문화재 방문처리 할 수 있다. 이때, 회원의 현위치와 문화유산의 거리는 500m이내 여야한다.
 
- 문화재 지도조회
  - 사용자는 특정지역의 지도를 요청할 수 있다.
  - 해당 지역의 지도는 지도 내 문화재를 표시하며 방문 문화재는 미방문 문화재와 구분되어 표시된다.
  - 해당 지역의 지도는 Grid로 나누어지며 방문한 문화재가 없는 Grid는 검은색으로 표기되어 보이지 않고, 방문 문화재가 있는 Grid는 지도의 내용이 표시된다.
 
- 문화재답사 경로추천
  - 유저의 위치기반으로 주변에 있는 문화유산을 방문하고 돌아오는 최적경로를 탐색하여 추천해준다. 이때, 최적화 경로의 기준은 경로 내 문화유산의 중요도점수(문화재 등급에 따른 차등점수)*문화재개수/총소요시간(문화재별 관람시간 1시간 + 이동경로시간)으로 한다. 단, 이미 유저가 방문한 문화유산은 제외한다.

  

## ERD

<img width="751" alt="image" src="https://github.com/user-attachments/assets/1ff8fc64-e61c-4ee9-ba3c-810d811bf416">



## 최적화 고려
- 캐쉬
  - 유저의 방문문화유산은 [문화재 지도조회], [문화재답사 경로추천], [내 주변 문화재 검색]에 공통적으로 사용되므로 로그인시 캐쉬에 보관 refactoring 고려중
  - 문화재 밀집지역인 서울과 경주의 좌표를 확정하고, 해당 좌표에 대한 문화재 정보는  캐쉬에 보관하여 즉시응답 refactoring 고려중


- 스레드
  - 다수의 외부 API호출이 필요하며 Latency 감소를 위해서 해당 task를 멀티스레드로 refactoring 고려중
  - API 호출대기 시간이 다소 길어 스레드 활용도 증가를 위해 비동기화 refactoring 고려중


## 프로젝트 기획과정 기록
- <https://bsh6226.tistory.com/132>


