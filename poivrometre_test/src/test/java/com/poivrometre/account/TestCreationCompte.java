package com.poivrometre.account;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestCreationCompte {
	
	@Test
	public void testCreationCompteValide() {
		try {			
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"validetest\", pseudo : \"valide\" }");
			assertEquals("0", output); // Si tout se passe bien lors de la création le webservice renvoie 0
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ pseudo : \"valide\", mdp : \"validetest\" }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreationComptePseudoTropPetit() {		
		try {
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"valideMDP\", pseudo : \"az\" }");
			assertEquals("2", output); // Lorsqu'il y a une erreur dans les données, le webservice renvoie 2
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreationCompteMDPTropPetit() {		
		try {
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"aze\", pseudo : \"valide\" }");
			assertEquals("2", output); // Lorsqu'il y a une erreur dans les données, le webservice renvoie 2
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreationCompteDejaExistant() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"validetest\", pseudo : \"valide\" }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : true, mdp : \"validetest\", pseudo : \"valide\" }");
			assertEquals("3", output); // Lorsque le compte existe déjà, le webservice renvoie 3
			String key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ pseudo : \"valide\", mdp : \"validetest\" }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreationComptePoidsNegatif() {		
		try {
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : -80, sexe : true, publique : true, cond_a : true, mdp : \"test\", pseudo : \"test\" }");
			assertEquals("2", output); // Lorsqu'il y a une erreur dans les données, le webservice renvoie 2
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
