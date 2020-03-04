package com.poivrometre.poivrofil;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestVoirAutrePoivrofilePublique {
	
	@Test
	public void testVoirAutrePoivrofilePublique() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : false, mdp : \"validetest\", pseudo : \"valide\" }");
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"validetest\", pseudo : \"valide\" }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/profil"), "{ pseudo : \"valide\", key : \"\" }");
			assertEquals("0.0:0.0", output); // Quand on accède à un poivrofil publique le Webservice renvoie le taux actuel et le taux max séparé par des ":"
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
