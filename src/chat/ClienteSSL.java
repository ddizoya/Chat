package chat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ClienteSSL {
	private PrintWriter out;
	private BufferedReader in;
	private String host = "localhost";
	private int puerto = 8080;
	

	public ClienteSSL() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		try {
			// Creamos un objeto de tipo KeyStore y le pasamos el fichero con su
			// clave que previamente hemos creado
			// en la terminal con keytool.
			System.out.println("Añadiendo fichero al objeto KeyStore...");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("C:\\Users\\David\\workspace\\ChatSSL\\src\\chat\\mySrvKeystore"),
					"password".toCharArray());

			// Gestionamos la clave de acceso al certificado.
			System.out.println("Comprobando <mykey>...");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "password".toCharArray());

			// Le pasamos el objeto KeyStore con el certificado al objeto
			// manejador de confiabilidad
			System.out.println("Añadiendo credenciales de fiabilidad al keyStore con TrustManagerFactory...");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);

			// El contexto SSL es lo más importante, porque una vez con el
			// keystore y el truststore asignados para un mismo
			// certificado, podremos enlazar el server con los sockets que
			// queramos.
			System.out.println("Generando el contexto SSL de transmisión de datos...");
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			// Finalmente creamos el servidor SSL mediante el objeto SSL
			// context.

			SSLSocketFactory sf = sc.getSocketFactory();
			SSLSocket sslSocket = (SSLSocket) sf.createSocket(host, puerto);

			out = new PrintWriter(sslSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

			leer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void leer() {
		Thread lectura = new Thread() {
			@Override
			public void run() {
				try {
					String mensaje;
					while ((mensaje = in.readLine()) != null) {
						System.out.println("Leyendo...");
						System.out.println(mensaje);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		lectura.start();
	}
}
