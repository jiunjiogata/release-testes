package br.ce.rjogata.servicos;

import br.ce.rjogata.exceptions.NaoPodeDividirPorZeroException;

public class Calculadora {

	public int soma(int a, int b) {
		// TODO Auto-generated method stub
		System.out.println("Chamou a funcao soma()");
		return a + b;
	}

	public int subtrair(int a, int b) {
		// TODO Auto-generated method stub
		return a - b;
	}

	public int dividir(int a, int b) throws NaoPodeDividirPorZeroException {
		// TODO Auto-generated method stub
		if(b == 0) {
			throw new NaoPodeDividirPorZeroException();
		}
		return a/b;
	}
	
	public void imprime() {
		System.out.println("Passei pela funcao imprime()");
	}

}
