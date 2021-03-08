package br.ce.rjogata.servicos;

import static br.ce.rjogata.buiders.FilmeBuilder.umFilme;
import static br.ce.rjogata.buiders.UsuarioBuilder.umUsuario;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.rjogata.buiders.FilmeBuilder;
import br.ce.rjogata.buiders.LocacaoBuilder;
import br.ce.rjogata.dao.LocacaoDAO;
import br.ce.rjogata.entidades.Filme;
import br.ce.rjogata.entidades.Locacao;
import br.ce.rjogata.entidades.Usuario;
import br.ce.rjogata.exceptions.FilmeSemEstoqueException;
import br.ce.rjogata.exceptions.LocadoraException;
import br.ce.rjogata.matchers.MatchersProprios;
import br.ce.rjogata.utils.DataUtils;

public class LocacaoServiceRealmenteUsandoMockTest {

	@InjectMocks
	private LocacaoService service;
		
	@Mock
	private SPCService spcService;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void inicializa() {
		MockitoAnnotations.openMocks(this);
		//Nao precisa mais nada disso aqui embaixo
		//service = new LocacaoService();
		/*
		 * dao = Mockito.mock(LocacaoDAO.class); service.setLocacaoDAO(dao); spcService
		 * = Mockito.mock(SPCService.class); service.setSpcService(spcService);
		 * emailService = Mockito.mock(EmailService.class);
		 * service.setEmailService(emailService);
		 */	}
	
	@After
	public void executaAposCadaTeste() {
	}
	
	private static int contador = 0;


	@Test
	public void deveAlugarUmFilme() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilme().agora();
		
			//acao
			Locacao locacao = service.alugarFilme(usuario, filme);		
			//verificacao
			Assert.assertTrue(locacao.getValor() == 10.0);
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	
	//forma elegante
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarUmFilmeSemEstoque() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilme().semEstoque().agora();
		
		service.alugarFilme(usuario, filme);
		
	}
	
	//forma elegante + com outra chamada de builder 
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarUmFilmeSemEstoque2() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilmeSemEstoque().agora();
		
		service.alugarFilme(usuario, filme);
		
	}


	//forma robusta
	@Test
	public void naoDeveAlugarUmFilmeSemUsuario() throws FilmeSemEstoqueException{
		//cenario
		Filme filme = umFilme().agora();

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
		Usuario usuario = umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme nulo");

		//acao
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void testaLocacaoDeVariosFilmes() throws Exception {
	    Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme1 = umFilme().comValor(5.0).agora();
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
		Usuario usuario = umUsuario().agora();
		Filme filme1 = umFilme().agora();
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
		Usuario usuario = umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme nulo");

		//acao
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void deveDarDescontoDe25PorcentoNaLocacaoDoTerceiroFilme() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		Filme filme1 = umFilme().agora();
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
		Usuario usuario = umUsuario().agora();
		Filme filme1 = umFilme().agora();
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
		Usuario usuario = umUsuario().agora();
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
		Usuario usuario = umUsuario().agora();
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
		Usuario usuario = umUsuario().agora();
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
	 * No exemplo abaixo, se colocar o usuario 1 na expectativa e usuario 2 na
	 * verificacao, deveria falhar. Mas não falha. Por causa do método que foi
	 * utilizado. No exemplo abaixo, perdemos o controle do teste, assim que a
	 * excecao é lancada. 
	 * No outro exemplo, resolveremos esse problema.
	 */

	//forma nova
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC () throws Exception {
		
		//Cenario
		Usuario usuario1 = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
		
		Filme filme1 = umFilme().agora();
		Filme filme2 = new Filme("Filme 2", 1, 5.0);
		List <Filme> filmes = new ArrayList<Filme>();		
		filmes = Arrays.asList(filme1, filme2);
		
		Mockito.when(spcService.possuiNegativacao(usuario1)).thenReturn(true);
		
		//Expectativa
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuario Possui Negativacao");
		
		//acao
		service.alugarVariosFilmes(usuario1, filmes);
		
		//verificacao
		Mockito.verify(spcService).possuiNegativacao(usuario2);

	}

	/*
	 * No exemplo abaixo, se colocar o usuario 1 na expectativa e usuario 2 na
	 * verificacao, o teste ira  falhar. Como era de se esperar. 
	 */
	
	//forma robusta
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC2 () throws Exception {
		
		//Cenario
		Usuario usuario1 = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
		
		Filme filme1 = umFilme().agora();
		Filme filme2 = new Filme("Filme 2", 1, 5.0);
		List <Filme> filmes = new ArrayList<Filme>();		
		filmes = Arrays.asList(filme1, filme2);
		
		Mockito.when(spcService.possuiNegativacao(usuario1)).thenReturn(true);
					
		//acao
		try {
			service.alugarVariosFilmes(usuario1, filmes);			
			Assert.fail(); // o Assert.fail é necessario para não gerar 
			// um falso positivo caso a excecao nao seja lancada.
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario Possui Negativacao"));
		}
		
		//verificacao
		Mockito.verify(spcService).possuiNegativacao(usuario1);

	}


	
	@Test public void deveEnviarEmailParaLocacoesAtrasadas() {

		//cenario 
		Usuario usuario1 = umUsuario().comNome("usuario1").agora();
		//usuario2 utilizado apenas para se fazer a contra-prova
		//isto é verificar se nao foi gerado um falso positivo
		Usuario usuario2 = umUsuario().comNome("usuario2").agora();
		
		List <Locacao> locacoes = Arrays.asList(LocacaoBuilder
				.umLocacao().comUsuario(usuario1).comDataRetorno(DataUtils
						.obterDataComDiferencaDias(-2)).agora()); 

		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();

		//Verificacao 
		Mockito.verify(emailService).notificarAtraso(usuario1);

	}
	 
	
	@Test 
	public void deveEnviarEmailParaLocacoesAtrasadas_VersaoMelhorada() {

		//cenario 
		Usuario usuario1 = umUsuario().comNome("usuario1").agora();
		//usuario2 utilizado apenas para se fazer a contra-prova
		//isto é verificar se nao foi gerado um falso positivo
		Usuario usuario2 = umUsuario().comNome("usuario em Dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro Usuario Atrasado").agora();
		
		List <Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario1)
				.comDataRetorno(DataUtils.obterDataComDiferencaDias(-2)).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora()
				); 

		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();

		//Verificacao 
		Mockito.verify(emailService).notificarAtraso(usuario1);
		Mockito.verify(emailService).notificarAtraso(usuario3);
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		
		Mockito.verifyNoMoreInteractions(emailService);
		// para ter certeza que o teste acima esta correto, basta comentar um dos verify
		// que devem retornar true, por exemplo o do usuario3, que tambem esta atrasado e 
		// portanto deveria ter recebido o email.

	}
	
	@Test
	public void deveTratarErrosSPC() throws Exception {
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha Catrastofica"));
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("SPC fora do ar, tente novamente");
		
		//acao
		service.alugarVariosFilmes(usuario, filmes);
		
		//verificacao
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		//Cenario
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		
		//Acao
		service.prorrogarLocacao(locacao, 3);
		
		//Verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(4.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(5));
	}
	
}
