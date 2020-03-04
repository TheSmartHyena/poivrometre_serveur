package com.poivrometre.poivrofil;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestVoirSonPoivrofile {
	
	@Test
	public void testAfficherProfil() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : false, mdp : \"validetest\", pseudo : \"valide\" }");
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"validetest\", pseudo : \"valide\" }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/profil"), "{ key : " + key + ", pseudo : \"valide\" }");
			assertEquals("0.0:0.0", output); // Quand on affiche son poivrofil on reçoit son taux actuel et son taux max séparé par ":"
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
