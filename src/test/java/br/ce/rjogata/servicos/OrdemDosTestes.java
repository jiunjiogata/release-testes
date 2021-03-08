package br.ce.rjogata.servicos;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemDosTestes {
	
	public static int contador = 0;
	
	@Test
	public void inicia() {
		contador++;
	}
	
	@Test
	public void verifica() {
		Assert.assertEquals(1, contador);	
	}
	

}
