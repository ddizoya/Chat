package chat;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;

public class ClienteSSL extends JFrame{
	private PrintWriter out;
	private BufferedReader in;
	private String host = "localhost";
	private int puerto = 8080;
	private JTextArea pantallaChat = new JTextArea();
	

	public ClienteSSL() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		
		try {
			//Interfaz gráfica generada con la paleta de Eclipse
			setMinimumSize(new Dimension(450, 290));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setResizable(false);
			setTitle("ClienteSSL");
			getContentPane().setLayout(null);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 0, 434, 175);
			getContentPane().add(scrollPane);

			pantallaChat.setEditable(false);
			
			scrollPane.setViewportView(pantallaChat);
			
			JTextArea campoMensaje = new JTextArea();
			campoMensaje.setBounds(10, 186, 306, 64);
			getContentPane().add(campoMensaje);
			
			/*
			 * La escritura en el streaming del socket lo hacemos cada vez que pulsamos enviar. 
			 * Si escribimos /exit, la ejecución del programa finalizará para el cliente y el servidor. 
			 */
			JButton btnNewButton = new JButton("Enviar");
			btnNewButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					String mensaje = ">> Cliente dice: " + campoMensaje.getText();
					String checking = campoMensaje.getText();
					if (checking.equalsIgnoreCase("/exit")){
						System.out.println("Cerrando la ejecución del programa tras escribir /exit .");
						System.exit(0);
					} else {
						pantallaChat.append("\n" +mensaje);
						out.println(mensaje);
						out.flush();
						campoMensaje.setText("");
					}
					
				}
			});

			btnNewButton.setBounds(324, 184, 100, 66);
			getContentPane().add(btnNewButton);
			setVisible(true);
			
			/* READ ME
			 * 
			 * Para crear los sockets SSL, en vez de hacerlo por terminal, he empleado objetos de esas clases concretas.
			 * Si no fuese así, a la hora de crear sockets SSL me pediría implementar muchos métodos abstractos, cosa que puedo
			 * solventar implementando clases concretas para creación de sockets y serversockets SSL (SSLSocketFactory y 
			 * SSLServerSocketFactory).
			 * 
			 * Lo que hacemos con el codigo que viene a continuación es registrar en la KeyStore la nueva clave generada mediante keytool.
			 * A su vez, le damos la clave («mykey») para tener acceso al fichero mediante la clase KeyManagerFactory, y le damos
			 * las credenciales de fiabilidad que necesita con TrustManagerFactory.
			 * 
			 * Una vez tenemos todo ese proceso hecho, podemos crear un contexto común SSL para el servidor y el socket, creando a partir
			 * de ese concreto los SSLSocket y SSLServerSocket.
			 * 
			 * A continuación se hará lo arriba mencionado:
			 */
			
			System.out.println("Añadiendo fichero al objeto KeyStore...");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("C:\\Users\\David\\workspace\\ChatSSL\\src\\chat\\mySrvKeystore"),
					"password".toCharArray());

			
			System.out.println("Comprobando <mykey>...");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "password".toCharArray());

			
			System.out.println("Añadiendo credenciales de fiabilidad al keyStore con TrustManagerFactory...");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);

			
			System.out.println("Generando el contexto SSL de transmisión de datos...");
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);


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
	
	//Lectura en bucle del socket. Si hay contenido nuevo, lo imprime en la pantalla del chat. 
	public void leer() {
		Thread lectura = new Thread() {
			@Override
			public void run() {
				try {
					String mensaje;
					while ((mensaje = in.readLine()) != null) {
							System.out.println("Leyendo...");
							pantallaChat.append("\n" +mensaje);					
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		lectura.start();
	}
}
