package br.ce.rjogata.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.rjogata.entidades.Locacao;
import br.ce.rjogata.exceptions.NaoPodeDividirPorZeroException;
import buildermaster.BuilderMaster;

public class CalculadoraTeste {
	
	private Calculadora calc;

	@Before
	public void inicializa() {
		calc = new Calculadora();		
	}
	
	@Test
	public void deveSomarDoisValores() {
		//cenario
		int a = 5;
		int b = 10;
		
		//acao
		int resultado = calc.soma( a, b);	
		
		
		//verificacao
		Assert.assertEquals(15, resultado);
		
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//cenario
		int a = 10;
		int b = 2;
		
		//acao
		int resultado = calc.subtrair( a, b);	
		
		
		//verificacao
		Assert.assertEquals(8, resultado);
		
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 10;
		int b = 2;
		
		//acao
		int resultado = calc.dividir( a, b);	
		
		
		//verificacao
		Assert.assertEquals(5, resultado);
		
	}


	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 10;
		int b = 0;
		
		//acao
		calc.dividir( a, b);	
		
		
		//verificacao
		
	}
	
	public static void main(String args[]) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}



}
