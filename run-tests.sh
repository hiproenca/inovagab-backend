#!/usr/bin/env bash
# Executa localmente toda a suíte de testes automatizados do InovaGAB.
#   ./run-tests.sh                 -> todos os testes (unitários + BDD)
#   ./run-tests.sh @negativo       -> apenas cenários BDD com a tag informada
set -euo pipefail
cd "$(dirname "$0")"

if (echo > /dev/tcp/localhost/27017) 2>/dev/null; then
  echo ">> MongoDB já está rodando em localhost:27017"
else
  echo ">> Subindo MongoDB de teste com Docker..."
  docker compose -f docker-compose.test.yml up -d --wait
fi

if [ $# -gt 0 ]; then
  ./mvnw -B test -Dtest=CucumberTest -Dcucumber.filter.tags="$1"
else
  ./mvnw -B test
fi

echo
echo ">> Relatório HTML do Cucumber: target/cucumber-reports/cucumber.html"
