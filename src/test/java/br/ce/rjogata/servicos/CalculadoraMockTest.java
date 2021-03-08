package br.ce.rjogata.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void initSetup() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencasEntreMockESpy() {
		
		//Expectativa
		Mockito.when(calcMock.soma(1, 2)).thenReturn(8);
		Mockito.when(calcSpy.soma(1, 2)).thenReturn(8);
		//doNothing faz com que nao seja executado o metodo imprime, nem para o mock nem para o Spy
		Mockito.doNothing().when(calcSpy).imprime();
		
		System.out.println("*************************************");
		System.out.println();
		System.out.println("devoMostrarDiferencasEntreMockESpy");
		System.out.println("Mock - " + calcMock.soma(1, 2));
		System.out.println("Spy  - " + calcSpy.soma(1, 2));
		System.out.println();
		System.out.println("Mock:");
		calcMock.imprime();
		System.out.println("Spy");
		calcSpy.imprime();
		System.out.println();
		System.out.println("*************************************");
	}
	
	
	@Test
	public void teste() {
		System.out.println("teste1");
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.soma(1, 2)).thenReturn(5);
		
		System.out.println(calc.soma(1, 2));
		//A execucao abaixo retornara 0, pois so "ensinamos" o mockito a somar
		// 1 e 2. Quando passamos 1 e 1 o mockito nao sabe o que fazer entao retorna
		// o valor padrao 0
		System.out.println(calc.soma(1, 1));
		Assert.assertEquals(5, calc.soma(1, 2));
		
	}
	
	@Test
	public void teste2() {
		System.out.println("teste2");
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.soma(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
		
		System.out.println(calc.soma(1, 2));
		System.out.println(calc.soma(1, 4));
		System.out.println(calc.soma(3, 3));
		//As execucoes acima retornaram 5, pois agora "ensinamos" o mockito a somar
		// quaisquer 2 inteiros. E o resultado sera sempre 5
		Assert.assertEquals(5, calc.soma(1, 2));
		Assert.assertEquals(5, calc.soma(12345, 24567));

	}


	@Test
	public void teste3() {
		System.out.println("teste3");
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.soma(Mockito.eq(2), Mockito.anyInt())).thenReturn(5);
		
		System.out.println(calc.soma(2, 2));
		System.out.println(calc.soma(2, 3));
		System.out.println(calc.soma(4, 3));
		//As duas primeiras execucoes retornaram 5, pois agora "ensinamos" o mockito a somar
		// 2 com qualquer inteiro. Mas nao ensinamos o mockito a somar, quando o primeiro valor
		// for diferente de 2
		Assert.assertEquals(5, calc.soma(2, 2));
		Assert.assertEquals(5, calc.soma(2, 10000000));

	}

}
