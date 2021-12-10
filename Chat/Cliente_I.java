package Chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;





public class Cliente_I {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Marco m = new Marco();
		m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
class Marco extends JFrame{
	public Marco() {
		setTitle(" S K R I V E R   R U M M E T ");
		setBounds(800,400,600,350);
		add(new lamina());
		setVisible(true);
		estableceConexionServidor();
	}
	
	//Siguiente método es para enviar antes que nada la Ip al servidor y éste nos detecte la Ip
	void estableceConexionServidor() {
		try {
			//Creamos un socket que cree el puente entre cliente y servidor
			Socket mi_socket = new Socket("192.168.0.6" , 9999);
			//Creamos el objeto paquete que tiene en su interior toda la información empaquetada
			EnvioPaqueteDatos datos = new EnvioPaqueteDatos();
			//A modo de prueba enviamos esté texto para saber que todo sale correcto
			datos.setTextoCliente(" online");
			//Creamos el flujo de datos de/ canal por donde viaje los datos
			ObjectOutputStream flujosSalidaPaquete = new ObjectOutputStream(mi_socket.getOutputStream());
			//Introduccimos en el canal / enviamos el paquete que tenemos para el servidor
			flujosSalidaPaquete.writeObject(datos);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

class lamina extends JPanel implements Runnable{
	public lamina() {
		//Ordenación de láminas
		setLayout(new BorderLayout());
		
		// Láminas que vamos a usar para distribuir los diferentes componentes
		Superior= new JPanel();
		medio= new JPanel();
		inferior= new JPanel();
		
		// COMPONENTES
		
		// JTEXTFIELD DEL NICK
		//nick = new JTextField (10);
		nick = new JLabel("");
		nick.setText("Inloggad: " + JOptionPane.showInputDialog("Vilket nick vill du ha:: "));
		nick.setFont(new Font("Arial", Font.BOLD, 18));
		nick.setForeground(new Color(135,206,235));
		// LABEL
		
		cliente = new JLabel("UPPKOPPLAD--> * ");
		cliente.setFont(new Font("Arial", Font.BOLD, 16));
		cliente.setForeground(new Color(0, 150, 125)); 
		
		
		
		//JTEXTFIELD DEL IP
		// JTEXTFIELD DEL NICK
		ip= new JComboBox ();
		ip.addItem("192.168.0.1");
		ip.addItem("192.168.0.10");
		ip.addItem("192.168.0.7");
		ip.addItem("192.168.0.4");
		
		// ARETEXT
		areaChat = new JTextArea(15,36);
		
		//JTEXTFIELD
		texto = new JTextField(36);
		
		//BUTTON
		enviar = new JButton ("SKICKA");
		enviar.setFont(new Font("Marker Felt", Font.BOLD, 14));
		enviar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//System.out.println(texto.getText());
				
				//Creación de Socket (vía de comunicación).
				try {
					// Creamos el Socket
					Socket mi_socket = new Socket("192.168.0.6" , 9999);
					
					//Instaciamos y creamos el objeto que viajará hasta el servidor. 
					// * Posteriormente será serializado
					EnvioPaqueteDatos datos = new EnvioPaqueteDatos();
					
					// Añadimos en los respectivos setters de la clase EnvioPaqueteDatos la info de los campos de texto por parámetro:
					datos.setNick(nick.getText());
					datos.setIp(ip.getSelectedItem().toString());
					datos.setTextoCliente(texto.getText());
					
					
					// Debemos de crear el flujo de datos. Al ser un objeto debemos de usar otra clase disferente
					ObjectOutputStream flujosSalidaPaquete = new ObjectOutputStream(mi_socket.getOutputStream());
					
					// Ahora debemos decirle que tipo de datos pasará por el flujo
					flujosSalidaPaquete.writeObject(datos);
					
					//Cerramos el Scocket
					mi_socket.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		//SE AÑADE LOS COMPONENTES EN SUS DIFERENTES LÁMINAS
		Superior.add(nick);
		
		Superior.add(cliente);
		Superior.add(ip);
		Superior.add(areaChat);
		medio.add(texto);
		inferior.add(enviar);
		
		//SE AÑADE LAS DIFERENTES LÁMINAS A LA LÁMINA CENTRAL (JPANEL)
		add(Superior, BorderLayout.NORTH);
		add(medio, BorderLayout.CENTER);
		add(inferior, BorderLayout.SOUTH);
		
		
		Thread mihilo = new Thread(this);
		mihilo.start();
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket escuchaCliente = new ServerSocket(9090);
			Socket cliente;
			EnvioPaqueteDatos paqueteRecibido;
			while(true) {
				cliente = escuchaCliente.accept();
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
				paqueteRecibido = (EnvioPaqueteDatos) flujoEntrada.readObject();
				areaChat.append("\n" + paqueteRecibido.getNick() + "\n" + paqueteRecibido.getTextoCliente());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private JLabel cliente,nick;
	private JTextField texto;
	private JComboBox ip;
	private JButton enviar;
	private JPanel Superior,medio, inferior;
	private JTextArea areaChat;
	
}


// C L A S E   D E  E M P A Q U E T A D O   I N F O
// Después de crear el socket, flujo de datos, y de decirle que tipo de datos va a psar, debemos serializar la clase

class EnvioPaqueteDatos implements Serializable{
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTextoCliente() {
		return textoCliente;
	}

	public void setTextoCliente(String textoCliente) {
		this.textoCliente = textoCliente;
	}
	
	

	public ArrayList<String> getIPs() {
		return IPs;
	}

	public void setIPs(ArrayList<String> iPs) {
		IPs = iPs;
	}



	private String nick,ip,textoCliente;
	private ArrayList<String> IPs;
}