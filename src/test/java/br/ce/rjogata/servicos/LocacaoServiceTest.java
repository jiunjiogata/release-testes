package br.ce.rjogata.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import br.ce.rjogata.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	private static int contador = 0;
	private SPCService spcService;
	private LocacaoDAO dao;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void inicializa() {
		service = new LocacaoService();
        dao = Mockito.mock(LocacaoDAO.class);
        service.setLocacaoDAO(dao);
        spcService = Mockito.mock(SPCService.class);
        service.setSpcService(spcService);
	}
	
	@After
	public void executaAposCadaTeste() {
		System.out.println("Executa apos a execucao de cada teste");
	}

	@Test
	public void deveAlugarUmFilme() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 1, 5.0);
		
			//acao
			Locacao locacao = service.alugarFilme(usuario, filme);		
			//verificacao
			Assert.assertTrue(locacao.getValor() == 5.0);
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC () throws Exception {
		
		//Cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		Filme filme1 = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 2", 1, 5.0);
		List <Filme> filmes = new ArrayList<Filme>();		
		filmes = Arrays.asList(filme1, filme2);
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		//Expectativa
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuario Possui Negativacao");
		
		//acao
		service.alugarVariosFilmes(usuario, filmes);

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
	
	
	//forma elegante
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarVariosFilmesSemEstoque() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 2", 0, 10.0);
		
		List <Filme> filmes = new ArrayList <Filme>();
		
		filmes.add(filme1);
		filmes.add(filme2);
		
		service.alugarVariosFilmes(usuario, filmes);
		
	}

	
	//forma nova
	@Test
	public void deveLancarExcecaoDeFilmeNuloNaTentativaDeLocarFilmeNulo() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme nulo");

		//acao
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void deveDarDescontoDe25PorcentoNaLocacaoDoTerceiroFilme() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 10.0);
		Filme filme2 = new Filme("Filme 2", 1, 10.0);
		Filme filme3 = new Filme("Filme 3", 1, 10.0);
		
		List <Filme> filmes = new ArrayList<Filme>();
		
		filmes = Arrays.asList(filme1, filme2, filme3);
		
			//acao
			Locacao locacao = service.alugarVariosFilmes(usuario, filmes);		
			
			//verificacao
			//10+10+7,50
			assertThat(locacao.getValor(), is(27.5));
	}

	@Test
	public void deveDarDescontoDe50PorcentoNaLocacaoDoTerceiroFilme() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 10.0);
		Filme filme2 = new Filme("Filme 2", 1, 10.0);
		Filme filme3 = new Filme("Filme 3", 1, 10.0);
		Filme filme4 = new Filme("Filme 4", 1, 10.0);
		
		List <Filme> filmes = new ArrayList<Filme>();
		
		filmes = Arrays.asList(filme1, filme2, filme3, filme4);
		
			//acao
			Locacao locacao = service.alugarVariosFilmes(usuario, filmes);		
			
			//verificacao
			//10.0 + 10.0 + 7.5 + 5.0
			assertThat(locacao.getValor(), is(32.5));
	}
	
	@Test
	public void deveDarDescontoDe75PorcentoNaLocacaoDoTerceiroFilme() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 10.0);
		Filme filme2 = new Filme("Filme 2", 1, 10.0);
		Filme filme3 = new Filme("Filme 3", 1, 10.0);
		Filme filme4 = new Filme("Filme 4", 1, 10.0);
		Filme filme5 = new Filme("Filme 5", 1, 10.0);
		
		List <Filme> filmes = new ArrayList<Filme>();
		
		filmes = Arrays.asList(filme1, filme2, filme3, filme4, filme5);
		
			//acao
			Locacao locacao = service.alugarVariosFilmes(usuario, filmes);		
			
			//verificacao
			//10.0 + 10.0 + 7.5 + 5.0 + 2.5
			assertThat(locacao.getValor(), is(35.0));
	}

	@Test
	public void deveDarDescontoDe100PorcentoNaLocacaoDoTerceiroFilme() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 10.0);
		Filme filme2 = new Filme("Filme 2", 1, 10.0);
		Filme filme3 = new Filme("Filme 3", 1, 10.0);
		Filme filme4 = new Filme("Filme 4", 1, 10.0);
		Filme filme5 = new Filme("Filme 5", 1, 10.0);
		Filme filme6 = new Filme("Filme 6", 1, 10.0);
		
		List <Filme> filmes = new ArrayList<Filme>();
		
		filmes = Arrays.asList(filme1, filme2, filme3, filme4, 
				filme5, filme6);
		
			//acao
			Locacao locacao = service.alugarVariosFilmes(usuario, filmes);		
			
			//verificacao
			//10.0 + 10.0 + 7.5 + 5.0 + 2.5
			assertThat(locacao.getValor(), is(35.0));
	}
	
	@Test
	public void deveDevolverOFilmeNaSegundaSeAlugarNoSabado() throws Exception {
	    Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme1 = new Filme("Filme 1", 1, 10.0);
		
		List <Filme> filmes = new ArrayList<Filme>();
		
		filmes = Arrays.asList(filme1);
		
		//acao
		Locacao retorno = service.alugarVariosFilmes(usuario, filmes);		
			
		//verificacao
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
		
	}
	
	/*
	 * @Test public void deveEnviarEmailParaLocacoesAtrasadas() {
	 * 
	 * //cenario List <Locacao> locacoes = Arrays.asList() //acao
	 * service.notificarAtrasos();
	 * 
	 * //Verificacao }
	 */
}
