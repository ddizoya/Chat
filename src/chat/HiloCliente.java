package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class HiloCliente extends JFrame implements Runnable {
	private Socket socket;
	private PrintStream ps;
	private BufferedReader in;

	public HiloCliente(Socket socket) {
		System.out.println("Creado hilo con un socket.");
		this.socket = socket;
		try {
			ps = new PrintStream (socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Streamings creados.");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void cerrarSesion() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enviarMensaje(String mensaje) {
		ps.print("Servidor dice: " + mensaje);
		ps.flush();
		ps.close();
		System.out.println("Mensaje de servidor enviado...");
	}

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

}
