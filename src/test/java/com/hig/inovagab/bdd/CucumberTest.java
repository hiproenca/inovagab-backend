package com.hig.inovagab.bdd;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Ponto de entrada dos testes BDD.
 * Executa todos os arquivos .feature de src/test/resources/features
 * (configurações adicionais em src/test/resources/junit-platform.properties).
 *
 * Executar só os cenários BDD:  ./mvnw test -Dtest=CucumberTest
 * Filtrar por tag:              ./mvnw test -Dtest=CucumberTest -Dcucumber.filter.tags="@negativo"
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class CucumberTest {
}
