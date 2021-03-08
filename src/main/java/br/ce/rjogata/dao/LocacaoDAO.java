package br.ce.rjogata.dao;

import java.util.List;

import br.ce.rjogata.entidades.Locacao;

public interface LocacaoDAO {
	
	public void salvar (Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();

}
