package br.ce.rjogata.matchers;

import java.util.Calendar;

public class MatchersProprios {

	public static DiaDaSemanaMatcher caiEm(Integer diaDaSemana) {
		return new DiaDaSemanaMatcher(diaDaSemana);
	}
	
	public static DiaDaSemanaMatcher caiNumaSegunda() {
		return new DiaDaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DataDiferencaDiasMatcher ehHojeComDiferencaDias(Integer qtdeDias) {
		return new DataDiferencaDiasMatcher(qtdeDias);
	}
	
	public static DataDiferencaDiasMatcher ehHoje() {
		return new DataDiferencaDiasMatcher(0);
	}


}
