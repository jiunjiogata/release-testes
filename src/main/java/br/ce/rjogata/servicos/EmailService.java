package br.ce.rjogata.servicos;

import br.ce.rjogata.entidades.Usuario;

public interface EmailService {
	
	public void notificarAtraso(Usuario usuario);

}
