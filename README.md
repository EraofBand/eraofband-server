commit 규칙
1. type : [파일명] 수정한 내용
2. type 
    - add : 새로운 기능을 추가할 때
    - modify : 기존 기능을 수정할 떄
3. ex) add : [UserController] 유저 등록 기능추가, modify : [PostProvider] 게시물 기능 수정

코드 주석 규칙 (java 파일에 사용)
1. 코드 위에 어떤 기능인지 설명  ex) 유저 삭제, 유저 추가
2.공통적으로 사용하는 변수를 제외한 애들은 선언 옆에  // 이 주석을 사용해서 설명해주기
3.각 함수 옆에 실제 url 경로 사용 예시 주석으로 추가 ex) //https://eraofband.shop/users/2

클래스 및 함수명 : 앞글자들만 대문자 ex) UserController

카카오 로그인 API
클라이언트로부터 access token을 받거나, 이메일을 직접 받는 방식 사용 예정

API는 스웨거 이용 - 잼의 추천
