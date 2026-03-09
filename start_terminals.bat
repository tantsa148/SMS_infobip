@echo off
REM Script to open terminals for back-end and front-end and start applications

REM Open terminal in back-end directory and start Spring Boot
start cmd /k "cd /d %~dp0back-end && echo. && echo === Lancement de Spring Boot === && echo. && ./mvnw spring-boot:run"

REM Open terminal in front-end directory and run npm run dev
start cmd /k "cd /d %~dp0front-end && echo. && echo === Lancement du serveur Vue.js (npm run dev) === && echo. && npm run dev"

exit

