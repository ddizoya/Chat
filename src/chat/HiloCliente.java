package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class HiloCliente extends JFrame implements Runnable {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public HiloCliente(Socket socket) {
		System.out.println("Creado hilo con un socket.");
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
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
