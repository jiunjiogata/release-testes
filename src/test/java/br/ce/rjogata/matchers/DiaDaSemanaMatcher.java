package br.ce.rjogata.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.rjogata.utils.DataUtils;

public class DiaDaSemanaMatcher extends TypeSafeMatcher <Date> {

	private Integer diaDaSemana;
	
	public DiaDaSemanaMatcher(Integer diaDaSemana) {
		this.diaDaSemana = diaDaSemana;
	}
	
	public void describeTo(Description desc) {
		Calendar data = Calendar.getInstance();
		data.set(Calendar.DAY_OF_WEEK, diaDaSemana);
		String dataExtenso = data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		desc.appendText(dataExtenso);

	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.verificarDiaSemana(data, diaDaSemana);
	}

}
