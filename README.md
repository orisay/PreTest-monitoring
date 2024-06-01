<h1> CPU 모니터링 </h1>

<div>
<h2>1. 분 단위 CPU 모니터링 데이터</h2>
<ul>
<li>목적: 실시간 CPU 사용률 모니터링</li>
<li>방법
  <ul>
 <li> CPU 사용률을 10초간 6회 측정 후 평균 값 계산</li>
 <li> 정확한 시간 시간 설정을 위해서 ScheduledExecutorService 사용</li>
 <li> ConcurrentLinkedDeque 사용으로 데이터가 20개 누적되면 데이터베이스에 일괄 저장</li>
  </ul>
</li>  
<li>구현 <ul>
 <li> MinuteStatDataBufferManager.Class 에서 데이터 ReentrantLock통해 동시성 문제 관리</li>
 <li> CentralProcessor 활용하여 10초간 CPU 사용률 측정</li>
  <li>잦은 데이터베이스 접근 횟수를 줄이고자 ConcurrentLinkedDeque 이용 </li>
</ul>
</li>  
 <ul>
</div>
   <div>
<h2>2. 시 단위 CPU 모니터링 데이터</h2>
     <ul>
<li> 목적: 특정 날에 시간 단위 CPU 사용률 통계</li>
<li> 방법: 분 단위 데이터를 활용한 통계 평균, 최대, 최소 사용률 계산</li>
     </ul>
   </div>
 <div>
<h2>3. 일 단위 CPU 모니터링 데이터</h2>
   <ul>
<li> 목적: 1년간 CPU 사용률 통계를 가지고 기간 내 데이터 조회</li>
<li> 방법: 시 단위 데이터 통계 활요</li>
<li> 주의: 분 단위 통계보다 정확성에서 차이가 발생</li>
   </ul>
 </div>

<div>
<h2>4. 공통 구현 사항</h2>
<h3> 로깅 및 예외 처리</h3>
  <ul>
  <li> GlobalExceptionHandler, ProcessCustomException, AOP, Log4j2 설정 활용</li>
  <li> 예외 발생시 상세 로그 파일 작성</li>
  <li> 년, 월, 일별 용 폴더에 Log 파일 생성</li>
  </ul>

<h3>작업 스케쥴링</h3>
<ul>
  <li>실시간 모니터링에서 정확한 실행 시간을 위해 ScheduledExecutorService 이용</li>
  <li> @Scheduled에서 cron을 이용한 작업 분산을 통한 대용량 처리 해결</li>
</ul>

<h3>시스템 종료 관리</h3>
<ul>
  <li> ShutdownManager 이용하여 프로젝트 전역 관리</li>
  <li> JVM 종료 전 자원 회수를 통한 메모리 누수 방지</li>
</ul>

<h3>시간 객체 관리</h3>
<ul>
   <li> application.yml에서 역직렬화 시 특정 타임존을 설정</li>
   <li> application.yml과 ZoneDateTimeFormatConfig을 이용하여 ZonedDateTime 포맷을 ISO-8601으로 설정</li>
   <li> TimeProvider을 이용하여 프로젝트 전반에 시간 제공</li>
   <li> TimeProvider에서 잦은 객체 생성을 방지하고자 ScheduledExecutorService과 싱글톤 이용</li>
   <li> TimeProvider에서 1분마다 시간 정보 갱신</li>
   <li> DateUtil을 이용하여 시간 객체 생성 감소</li>
</ul>

<h3>데이터 백업 및 삭제</h3>
<ul>
   <li> 논리적 데이터 삭제 이후 데이터 백업을 통해서 데이터 유실 방지</li>
   <li> 간단한 팩토리 패턴을 활용하여 테스트 코드 작성 용이</li>
</ul>
</div>
<hr/>
<h1> CPU 모니터링 통계 조회 API </h1>
<h1>작성 중입니다.</h1>
1.GET을 사용해도 되지만 모니터링을 해야한다면 보안이 중요하다고 생각하고 추후에 사용자 권한 확인이 필요하다고 생각하여 POST로 작성했습니다.
2. 클라이언트가 보낸 정보를 검사하기 위해서 @Valid 사용 이후 DTO에서 처리
3. 분, 시, 일 단위는 데이터 제공 제한이 걸려있어 그에 따른 플래그 조건을 추가 하려했으나 그 부분을 놓쳤습니다.
3. 분,시 단위는 조회 구간이 정해져서  +1시, +1일를 하기 때문에 클라이언트가 정보를 주어 데이터 전송량을 늘리는 것보다 Service에서 처리했습니다.
-> 단 일 단위는 클라이언트가 종료일을 보내야하기에 종료일 정보를 받아야합니다.
-> 추가로 조회 종료일 +1을 자정으로 데이터 조회가 가능케 했습니다.

