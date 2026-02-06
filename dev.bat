@echo off
REM 코드 변경 시 자동 재시작되는 개발 서버 실행
call gradlew.bat bootRun --continuous
