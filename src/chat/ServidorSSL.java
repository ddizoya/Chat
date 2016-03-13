package chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ServidorSSL {
	private int puerto = 8080;
	private SSLServerSocket s;
	private Socket sslc;
	private PrintWriter out;
	private BufferedReader in;

	public ServidorSSL() {
		try {
			// Creamos un objeto de tipo KeyStore y le pasamos el fichero con su
			// clave que previamente hemos creado
			// en la terminal con keytool.
			System.out.println("A�adiendo fichero al objeto KeyStore...");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("C:\\Users\\David\\workspace\\ChatSSL\\src\\chat\\mySrvKeystore"),
					"password".toCharArray());

			// Gestionamos la clave de acceso al certificado.
			System.out.println("Comprobando <mykey>...");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "password".toCharArray());

			// Le pasamos el objeto KeyStore con el certificado al objeto
			// manejador de confiabilidad
			System.out.println("A�adiendo credenciales de fiabilidad al keyStore con TrustManagerFactory...");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);

			// El contexto SSL es lo m�s importante, porque una vez con el
			// keystore y el truststore asignados para un mismo
			// certificado, podremos enlazar el server con los sockets que
			// queramos.
			System.out.println("Generando el contexto SSL de transmisi�n de datos...");
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			// Finalmente creamos el servidor SSL mediante el objeto SSL
			// context.

			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			s = (SSLServerSocket) ssf.createServerSocket(puerto);
			System.out.println("Servidor con puerto de escucha " + puerto + " mediante SSLServerFactory...");

			leer();

		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException
				| UnrecoverableKeyException | KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enviarMensaje(String mensaje) {
		out.println("Servidor dice: " + mensaje);
		out.flush();

	}

	public void leer() {
		Thread lectura = new Thread() {
			@Override
			public void run() {
				try {
					sslc = (SSLSocket) s.accept();
					System.out.println("Socket aceptado");

					out = new PrintWriter(sslc.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(sslc.getInputStream()));
					System.out.println("Input y outputs agregados del socket cliente");
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
