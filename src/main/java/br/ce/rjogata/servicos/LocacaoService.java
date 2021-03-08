package br.ce.rjogata.servicos;

import static br.ce.rjogata.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.rjogata.dao.LocacaoDAO;
import br.ce.rjogata.entidades.Filme;
import br.ce.rjogata.entidades.Locacao;
import br.ce.rjogata.entidades.Usuario;
import br.ce.rjogata.exceptions.FilmeSemEstoqueException;
import br.ce.rjogata.exceptions.LocadoraException;
import br.ce.rjogata.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, Filme filme) throws FilmeSemEstoqueException, LocadoraException{
		
		int i = 0;
		
		if(usuario == null) {
			throw new LocadoraException("Usuario nulo");
		}
		
		if(filme == null) {
			throw new LocadoraException("Filme nulo");
		}
		
		if(filme.getEstoque()== 0) {
			throw new FilmeSemEstoqueException();
		}

		try {
			if(spcService.possuiNegativacao(usuario)) {
				throw new LocadoraException("Usuario Possui Negativacao");
			}
		} catch (Exception e) {
			throw new LocadoraException("SPC fora do ar, tente novamente");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...
		dao.salvar(locacao);
		
		return locacao;
	}	
	
	public void setLocacaoDAO(LocacaoDAO dao) {
		this.dao = dao;
	}
	
	public void setSpcService(SPCService spc) {
		this.spcService = spc;
	}
	
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	
	public void notificarAtrasos() { 
		List <Locacao> locacoes = dao.obterLocacoesPendentes(); 
		  
		for(Locacao locacao: locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario()); 				
			}
		} 
	}
	 
	
	public Locacao alugarVariosFilmes(Usuario usuario, List <Filme> filmes) throws FilmeSemEstoqueException, LocadoraException{
		
		int i = 0;
		
		if(usuario == null) {
			throw new LocadoraException("Usuario nulo");
		}
		
		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme nulo");
		}
		
		for(Filme filme: filmes) {
			if(filme.getEstoque()== 0) {
				throw new FilmeSemEstoqueException();
			}			
		}
		
		boolean negativado;
		
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e1) {
			throw new LocadoraException("SPC fora do ar, tente novamente");
		}

		if(negativado) {
			throw new LocadoraException("Usuario Possui Negativacao");	
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		Double valorTotal = 0d;
		Double precoFilme = 0d;
		
		for(int j = 0; j < filmes.size(); j++) {
			Filme filme = filmes.get(j);
			switch(j) {
			case 2 : precoFilme = filme.getPrecoLocacao()*0.75; break;
			case 3 : precoFilme = filme.getPrecoLocacao()*0.50; break;
			case 4 : precoFilme = filme.getPrecoLocacao()*0.25; break;
			case 5 : precoFilme = filme.getPrecoLocacao()*0; break;
			default: precoFilme = filme.getPrecoLocacao(); 
			}
			valorTotal += precoFilme;
		}
		
		locacao.setValor(valorTotal);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);			
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}	
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilme(locacao.getFilme());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		dao.salvar(novaLocacao);
	}
}