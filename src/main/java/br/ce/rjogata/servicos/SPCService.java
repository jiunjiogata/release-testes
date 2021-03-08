package br.ce.rjogata.servicos;

import br.ce.rjogata.entidades.Usuario;

public interface SPCService {
	
	public boolean possuiNegativacao(Usuario usuario) throws Exception;

}
