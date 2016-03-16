package chat;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class Main {
	
 public static void main(String[] args) throws IOException, ClassNotFoundException, UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
	ServidorSSL ser = new ServidorSSL();
	ClienteSSL cli = new ClienteSSL();
	System.out.println("Socket cliente aceptado en el servidor...");

}
 
}
