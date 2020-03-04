package com.poivrometre.driving;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.poivrometre.tools.Tools;

public class TestPeutRoulerNonCondA {
	
	String key;

	@Before
	public void setUp() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/create"), "{ poids : 80, sexe : true, publique : true, cond_a : false, mdp : \"validetest\", pseudo : \"valide\" }");
			key = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/connect"), "{ pseudo : \"valide\", mdp : \"validetest\" }");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAutorisationRouler() {
		try {
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/temps"), "{ key : " + key + ", pseudo : \"valide\" }");
			assertEquals("00h00", output);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAutorisationRoulerPlusQue0MoinsDe50() {		
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/conso"), "{ key : " + key + ", id_boisson : 2 }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/temps"), "{ key : " + key + ", pseudo : \"valide\" }");
			assertEquals("00h00", output); // Apte à rouler, taux approx. 0,17 < 0.5
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRefusDeRouler() {		
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/conso"), "{ key : " + key + ", id_boisson : 2 }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/conso"), "{ key : " + key + ", id_boisson : 2 }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/conso"), "{ key : " + key + ", id_boisson : 2 }");
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/conso"), "{ key : " + key + ", id_boisson : 2 }");
			String output = Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/temps"), "{ key : " + key + ", pseudo : \"valide\" }");
			assertNotEquals("00h00", output); // Taux approx 0.71 > 0.5 (temps restant environ 1h20)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() {
		try {
			Tools.appelWebservice(new URL("http://51.75.28.97:8080/Poivrometre_war/services/suppr"), "{ pseudo : \"valide\", key : " + key + " }");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
