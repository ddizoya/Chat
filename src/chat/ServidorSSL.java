package chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class ServidorSSL implements Runnable {
	private int puerto = 8080;
	private SSLServerSocket s;
	private SSLSocket sslc;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;


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

			// Finalmente creamos el servidor SSL mediante el objeto SSL context.
			
			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			s = (SSLServerSocket) ssf.createServerSocket(puerto);
			System.out.println("Servidor con puerto de escucha " + puerto + " mediante SSLServerFactory...");
			
			SSLSocketFactory sf = sc.getSocketFactory();
			sslc = (SSLSocket) sf.createSocket("localhost", puerto);
			System.out.println("Socket SSL creado en el puerto " + puerto + " mediante SSLSocketFactory...");
			

			sslc = (SSLSocket) s.accept();
			System.out.println("Socket aceptado");
			
			oos = new ObjectOutputStream(sslc.getOutputStream());
			ois = new ObjectInputStream(sslc.getInputStream());
			System.out.println("Input y outputs agregados del socket cliente");
			
			((HiloCliente) new HiloCliente(sslc)).run();
			System.out.println("Ejecutando hilo cliente para env�o y recepci�n de mensajes.");
			
			this.run();
			System.out.println("Listener de lo que dice cliente ON...");

		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException
				| UnrecoverableKeyException | KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enviarMensaje(String mensaje) {
		try {
			oos.writeObject("Servidor dice: " + mensaje);
			oos.flush();
			oos.close();
			System.out.println("Mensaje de servidor enviado...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			String mensaje;
			if ((mensaje = String.valueOf(ois.readObject())) != null) {
				System.out.println("Leyendo...");
				System.out.println(mensaje);
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

}