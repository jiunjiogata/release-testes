package br.ce.rjogata.servicos;

import static br.ce.rjogata.matchers.MatchersProprios.ehHoje;
import static br.ce.rjogata.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.rjogata.dao.LocacaoDAO;
import br.ce.rjogata.entidades.Filme;
import br.ce.rjogata.entidades.Locacao;
import br.ce.rjogata.entidades.Usuario;
import br.ce.rjogata.exceptions.FilmeSemEstoqueException;
import br.ce.rjogata.exceptions.LocadoraException;
import br.ce.rjogata.matchers.MatchersProprios;
import br.ce.rjogata.utils.DataUtils;

public class TestesComMatchersPropriosTest {

	private LocacaoService service;
	private static int contador = 0;
	private SPCService spcService;

	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void inicializa() {
		service = new LocacaoService();
		contador++;
		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
		//Fazendo a injecao do metodo na classe LocacaoService
		service.setLocacaoDAO(dao);		
		spcService = Mockito.mock(SPCService.class);
        //Fazendo a injecao do metodo na classe LocacaoService
        service.setSpcService(spcService);
	}
	
	@After
	public void executaAposCadaTeste() {
		System.out.println("Executa apos a execucao de cada teste");
	}

	@Test
	public void deveAlugarUmFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List <Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0), 
											new Filme("Filme 2", 1, 5.0),
											new Filme("Filme 3", 1, 5.0));
		
		//acao
		Locacao locacao = service.alugarVariosFilmes(usuario, filmes);		

		//verificacao
		//error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		//A de baixo eh igual a de cima, mas esta mais legivel
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
		//A de baixo eh igual a de cima, mas estac mais legivel
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
	}
	
	
	//forma elegante
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarUmFilmeSemEstoque() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		service.alugarFilme(usuario, filme);
		
	}

	//forma robusta
	@Test
	public void naoDeveAlugarUmFilmeSemUsuario() throws FilmeSemEstoqueException{
		//cenario
		Filme filme = new Filme("Filme 1", 0, 5.0);

		//acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario nulo"));
		}
		
	}
	
	//forma nova
	@Test
	public void naoDeveAludarUmFilmeSemUmObjetoFilme() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme nulo");

		//acao
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void testaLocacaoDeVariosFilmes() throws Exception {
	    Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 2", 1, 10.0);
		
		List <Filme> filmes = new ArrayList<Filme>();
		
		filmes.add(filme1);
		filmes.add(filme2);
		
			//acao
			Locacao locacao = service.alugarVariosFilmes(usuario, filmes);		
			//verificacao
			Assert.assertTrue(locacao.getValor() == 15.0);
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarFilmeNoSabado() throws Exception {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));	
		
		//Cenario
		Usuario usuario = new Usuario("Usuario 1");
		List <Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0),
											new Filme("Filme 2", 1, 5.0));
		
		//acao
		Locacao resultado = service.alugarVariosFilmes(usuario, filmes);
		
		//Verificacao
		//Assert.assertThat(resultado.getDataRetorno(), new DiaDaSemanaMatcher(Calendar.MONDAY));
		Assert.assertThat(resultado.getDataRetorno(), MatchersProprios.caiEm(Calendar.SUNDAY));
		Assert.assertThat(resultado.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
}
