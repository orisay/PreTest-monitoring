

<h1> CPU 모니터링 </h1>

<div>
<img src="https://github.com/orisay/PreTest-monitoring/blob/ec2245475c8154338e6ec1e284eccdba5551a182/software_architecture/State%20Diagram.jpg">
</div>
<h2>1. 분 단위 CPU 모니터링 데이터</h2>
<ul>
<li>목적: 실시간 CPU 사용률 모니터링</li>
<li>방법
  <ul>
 <li> CPU 사용률을 10초간 6회 측정 후 평균 값 계산</li>
 <li> ScheduledExecutorService 사용 이유</li>
    <ul>
      <li>잦은 데이터 입출력 발생</li>
      <li>Thread Pool로 비동기 처리를 하여 여러 작업을 동시에 처리로 동시성 문제 해결</li>
    </ul>
 <li> ConcurrentLinkedDeque 사용으로 데이터가 20개 누적되면 데이터베이스에 일괄 저장</li>
  </ul>
</li>  
<li>구현 <ul>
  <li>ScheduledExecutorService Pool size는 실제 가용 코어에 따라  Runtime.getRuntime().availableProcessors() * 2</li>
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
    <li> GlobalExceptionHandler, ProcessCustomException, CustomException, AOP, Log4j2 설정 활용</li>
    <li>ProcessCustomException은 프로그램 자체 로깅</li>
    <li>CustomException ResponseEntity<ErrorResponse> 반환으로 사용자에게 알림</li>
    <li> 예외 발생시 상세 로그 파일 작성</li>
    <li> 서버 시간에 따른 년, 월, 일별 용 폴더에 Log 파일 생성 예:2024-06-15/exceptionCurrentTime-logs-2024-06-15.log</li>
    <li>조회 로직과 저장 로직을 분리</li>
    <li>저장 작업은 비동기 처리 </li>
    <li>사용자에게 나쁜 경험을 주지 않기 위해서 ThreadConfig를 이용해서 데몬 스레드 사용</li>
    <li>비동기 처리를 더 원활하기 위해서 데몬스레드 설정한 ThreadConfig 작성</li>
    <li>읽기 쓰기에 따른 트랜잭션 분리</li>
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
   <li>잦은 타임 객체 생성을 방지하고자 TimeProvider에서 1분마다 시간 정보 갱신</li>
   <li> TimeProvider을 이용하여 프로젝트 전반에 시간 제공</li>
   <li> TimeProvider에서 잦은 객체 생성을 방지하고자 ScheduledExecutorService과 싱글톤 이용</li>
   <li> TimeProvider에서 1분마다 시간 정보 업데이트</li>
   <li> DateUtil을 이용하여 시간 객체 생성 감소</li>
</ul>

<h3>데이터 백업 및 삭제</h3>
  <ul>
     <li> 논리적 데이터 삭제 이후 데이터 백업을 통해서 데이터 유실 방지</li>
     <li> 간단한 팩토리 패턴을 활용하여 테스트 코드 작성 용이</li>
  </ul>
</div>
<hr/>
<div>
  <h1> CPU 모니터링 통계 조회 API </h1>
    <ul>
      <li>모니터링 자체가 어느정도 권한이 필요한 작업이라 보안을 생각하여 GetMapping대신에 PostMapping 일괄 사용 </li>
      <li>잘못된 데이터가 전파되지 않도록 컨트롤러에서 @Vaild를 이용하여 유효성 검사</li>
      <li>분, 시 단위는 정확하게 조회 구간이 정해져서 조회 종료 일은 Service Layer에서 계산</li>
      <li>일 단 위는 사용자가 정한 기간에 종료일이 실제 당일과 일치할 때 데이터 손실 방지를 위해 +1 추가하여 데이터 조회</li>
    </ul>
</div>

<h1>TestCode Coverage</h1>

<div>
  <h2>단위테스트</h2>
    <ul>
      <li>CpuMonitoringService 100%</li>
      <li>CpuMonitoringBackupService 100%</li>
      <li>CpuMonitoringManageService 83%</li>
    </ul>
</div>
  
<div>
  <h2>종합테스트</h2>
    <ul>
      <li>CpuMonitoringController 100%</li>
      <li>CpuMonitoringService 88%</li>
      <li>CpuMonitoringBackupService 64%</li>
      <li>CpuMonitoringManageService 43%</li>
    </ul>
  <h2>종합 테스트 커버리지가 낮은 이유</h2>
  <div>실제 코드를 HelperClase에서 직접 작성하고 처리를 했기에 유닛테스트에 비해서 커버리지가 낮습니다.</div>
  <ul>
    <li>통합 테스트는 목 객체 지양</li>
    <li>테스트를 하여 어플리케이션 전반적인 흐름과 통제를 확인하고 싶었습니다.</li>
    <li>HelperClass에서 임시 데이터를 만들어 DB에 저장</li>
    <li>전반적인 흐름과 디버깅에 도움은 되었지만 커버리지를 낮추는 결과를 가져왔습니다.</li>
    <li>부하 테스트 진행 후 테스트 코드를 HelperClass 사용보다 실제 비즈니스 로직을 활용하여 작성하여 커버리지를 높이겠습니다.</li>
  </ul>
</div>

