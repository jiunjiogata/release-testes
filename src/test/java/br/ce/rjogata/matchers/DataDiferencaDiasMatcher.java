package br.ce.rjogata.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.rjogata.utils.DataUtils;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer qtdeDias;
	
	public DataDiferencaDiasMatcher(Integer qtdeDias) {
		this.qtdeDias = qtdeDias;
	}
	
	public void describeTo(Description description) {

		Date dataEsperada = DataUtils.obterDataComDiferencaDias(qtdeDias);
		DateFormat format = new SimpleDateFormat("dd/MM/YYYY");
		description.appendText(format.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		// TODO Auto-generated method stub
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(qtdeDias));
	}

}
