package com.poivrometre.poivrofil;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestResearchPoivrofile {
	
	@Test
	public void testResearchPoivrofile() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : false, mdp : \"validetest\", pseudo : \"valide1\" }");
			String key1 = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"validetest\", pseudo : \"valide1\" }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : false, mdp : \"validetest\", pseudo : \"valide2\" }");
			String key2 = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"validetest\", pseudo : \"valide2\" }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/search"), "valide");
			assertEquals("[valide1,valide2,]", output); // La liste des utilisateurs dont le pseudo commence par l'input "valide"
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide1\", key : " + key1 + " }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide2\", key : " + key2 + " }");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
