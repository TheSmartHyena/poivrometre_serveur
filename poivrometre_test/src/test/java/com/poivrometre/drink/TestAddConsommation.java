package com.poivrometre.drink;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestAddConsommation {
	
	@Test
	public void testAddConso() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"validetest\", pseudo : \"valide\" }");
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ pseudo : \"valide\", mdp : \"validetest\" }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/conso"), "{ key : " + key + ", id_boisson : 2 }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/taux"), "{ pseudo : \"valide\", key : " + key + " }");
			assertEquals(true, Double.parseDouble(output) > 0); // On test que le taux ne soit plus nul après ajout d'une consommation (le comtpe est à 0 après la création)
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}