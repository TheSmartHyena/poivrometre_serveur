package com.poivrometre.account;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestSuppressionCompte {

	@Test
	public void testSuppressionCompteValide() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"validetest\", pseudo : \"valide\" }");
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"validetest\", pseudo : \"valide\" }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ pseudo : \"valide\", mdp : \"validetest\" }");
			assertEquals("-1", output); // On test que la connexion soit impossible après suppression
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
