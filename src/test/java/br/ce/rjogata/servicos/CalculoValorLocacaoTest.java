package br.ce.rjogata.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import br.ce.rjogata.dao.LocacaoDAO;
import br.ce.rjogata.entidades.Filme;
import br.ce.rjogata.entidades.Locacao;
import br.ce.rjogata.entidades.Usuario;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	@Parameter
	public List <Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;

	
	private LocacaoService service;
	private SPCService spcService;


	private static Filme filme1 = new Filme("Filme 1", 1, 10.0);
	private static Filme filme2 = new Filme("Filme 2", 1, 10.0);
	private static Filme filme3 = new Filme("Filme 3", 1, 10.0);
	private static Filme filme4 = new Filme("Filme 4", 1, 10.0);
	private static Filme filme5 = new Filme("Filme 5", 1, 10.0);
	private static Filme filme6 = new Filme("Filme 6", 1, 10.0);

	
	@Before
	public void inicializa() {
		service = new LocacaoService();
        LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
        //Fazendo a injecao do metodo na classe LocacaoService
        service.setLocacaoDAO(dao);
        spcService = Mockito.mock(SPCService.class);
        //Fazendo a injecao do metodo na classe LocacaoService
        service.setSpcService(spcService);
	}

	@Parameters(name="{2}")
	public static Collection<Object[]> parametros(){
		//deve retornar um array de objetos da forma:
		//{{filmes, valorLocacao},{filmes, valorLocacao},{filmes, valorLocacao}}
	    /*return Arrays.asList(new Object[][]{
	    *	{},
	    *	{}
	    });*/

	    return Arrays.asList(new Object[][]{
	    	{Arrays.asList(filme1, filme2, filme3), 27.50, "3 Filmes 25%"},
	    	{Arrays.asList(filme1, filme2, filme3, filme4), 32.50, "4 Filmes 50%"},
	    	{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 35.00, "5 Filmes 75%"},
	    	{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 35.00, "6 Filmes 100%"},	    	
	    });
		
	}
	
	@Test
	public void deveCalcularValorDaLocacaoConsiderandoDescontos() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		//acao
		Locacao resultado = service.alugarVariosFilmes(usuario, filmes);		

		//verificacao
		//10+10+7,50
		assertThat(resultado.getValor(), is(valorLocacao));
	}

}
