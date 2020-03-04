package com.poivrometre.server;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestServer {
	
	URL url;
	HttpURLConnection connection;

	@Before
	public void setUp() {
		try {
			url = new URL("http://51.75.28.97:8080/Poivrometre_war/services/test"); // URL du webservice
			connection = (HttpURLConnection) url.openConnection(); // Ouverture de la connexion au webservice
			connection.setRequestMethod("GET"); // Type d'appel, GET car on envoie rien, on reçoit seulement une réponse du server
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * test la bonne connexion au serveur (que la communication soit ouvert)
	 * Premier test à lancer pour s'assurer que la connexion est assurée
	 */
	@Test
	public void testConnection() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()))); // Création du flux de réponse
			String output = br.readLine(); // Réception de la réponse
			
			assertEquals("test", output); // Le server indique que tout s'est bien passé en renvoyant "test", ce qui ignifie que la connexion fonctionne
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() {
		connection.disconnect(); // Fermeture de la connexion
	}
}
