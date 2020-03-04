package com.poivrometre.account;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestConnexionCompte {

	@Test
	public void testConnexionCompteValide() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"validetest\", pseudo : \"valide\" }"); // Création du compte
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"validetest\", pseudo : \"valide\" }"); // Connexion au compte, récupération de la clé
			assertEquals(true, key.length() == 20); // A la connexion, le webservice renvoie uen clé de longueur 20 pour identifier, on vérifie que l'on reçoit la clé
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }"); // On oublie pas de supprimer le compte de la db une fois qu'on a fini le test
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testConnexionCompteInexistant() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ mdp : \"compteinexistant\", pseudo : \"compteinexistant\" }");
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ mdp : \"compteinexistant\", pseudo : \"compteinexistant\" }");
			assertEquals("-1", key); // Quand on se connecte à un compte inexistant le webservice renvoie -1
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
