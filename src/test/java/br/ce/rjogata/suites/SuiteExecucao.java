package br.ce.rjogata.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.rjogata.servicos.AssetTests;
import br.ce.rjogata.servicos.CalculadoraTeste;
import br.ce.rjogata.servicos.CalculoValorLocacaoTest;
import br.ce.rjogata.servicos.LocacaoServiceTest;
import br.ce.rjogata.servicos.OrdemDosTestes;
import br.ce.rjogata.servicos.TestesComMatchersPropriosTest;

@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTeste.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class,
	OrdemDosTestes.class,
	TestesComMatchersPropriosTest.class
})
public class SuiteExecucao {

}
