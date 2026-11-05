@echo off
REM Executa localmente toda a suite de testes automatizados do InovaGAB (Windows).
REM   run-tests.cmd             -> todos os testes (unitarios + BDD)
REM   run-tests.cmd @negativo   -> apenas cenarios BDD com a tag informada
cd /d "%~dp0"

echo ^>^> Subindo MongoDB de teste com Docker (se ja estiver rodando, nada muda)...
docker compose -f docker-compose.test.yml up -d --wait
if errorlevel 1 echo Aviso: nao foi possivel subir o MongoDB via Docker. Certifique-se de que ha um MongoDB em localhost:27017.

if "%~1"=="" (
  call mvnw.cmd -B test
) else (
  call mvnw.cmd -B test -Dtest=CucumberTest -Dcucumber.filter.tags="%~1"
)

echo.
echo ^>^> Relatorio HTML do Cucumber: target\cucumber-reports\cucumber.html
