package chat;

import java.io.IOException;

public class Main {
	
 public static void main(String[] args) throws IOException, ClassNotFoundException {
	ServidorSSL ser = new ServidorSSL();
	System.out.println("Socket cliente aceptado en el servidor...");
	ser.enviarMensaje("Hola, qué tal server?");
	

	
}
 
}
