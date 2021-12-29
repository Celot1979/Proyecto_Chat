package Chat;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;



public class Servidor_Central {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoServidorChat m_s = new  MarcoServidorChat();
		m_s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

class MarcoServidorChat extends JFrame implements Runnable {
	
	public MarcoServidorChat(){
		setTitle(" S E R V E R ");
		setBounds(1100,400,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);

		setVisible(true);	
		
		
		Thread miHilo = new Thread(this);
		
		
		miHilo.start();	
		
		}	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		
		try {
			ServerSocket  miServidor = new ServerSocket(9999);// Puerto a la escucha
			// Creamos 3 Variables para almacenar la info que vendrá en el paquete serializado.
			String nick,ip,mensaje; 
			/*
			 *  Creamos una ArrayList para añadir las IPs que se vayan conectando.
			 *  Aquí nos aseguramos que sólo se lea una vez y que sume todas las ips nuevas.
			 */
			ArrayList<String> listaIpConectados = new ArrayList<String>();
			EnvioPaqueteDatos paqueteRecibido;
			while(true) {  
				 // Aceptamos todas las conexiones que entren en el ServerSocket
				Socket miSocket =  miServidor.accept(); 
				
				
				// Creamos el flujo de entrada de datos
				ObjectInputStream flujoDatosEntrada = new ObjectInputStream( miSocket.getInputStream());
				// Reconstruimos el paquete que viene por el flujo. Formaremos un objeto con el contenido que lea del paquete de datos de entrada
				paqueteRecibido =  (EnvioPaqueteDatos) flujoDatosEntrada.readObject();
				nick = paqueteRecibido.getNick();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getTextoCliente();
				/*
				 * Se ha ralizado una modificación significativa en el código:
				 * - Se ha modificado el orden de la creación del socket que envía la ip al Else del condional.
				 * + Antes Estaba justo después de la creación de aceptar todas las conexiones.
				 * 
				 * +Se crea un condicional invvertido para comparar si el mensaje que recoge lo que viaje en el texto
				 * del socket que envía la Ip es igual al mensaje que enviamos en ese socket.
				 * Si no lo es, el programa debe de ejecutar el 2º socket y no hacer caso a la Ip. Porque
				 * ya está conectado de segundas.
				 * 
				 * + El else entra en funcionamiento. sólo si es la 1ª vez que conecta y tiene que enviar la Ip al servidor.
				 * Así el servidor sabrá el cliente que se conecta.
				 */
				if(!mensaje.equals(" online")) {
					areatexto.append("\n" + "Nick: " + nick + "\n" + "MEDDELANDE MOTTAGET : " + "\n" + mensaje + "\n" + " IP: " + ip);
				
					// * Importante: Aquí vamos a crear un nuevo socket que será el encargado de enviar el paquete recibido del servidor al destinatario
					Socket reenvioDestinatario = new Socket(ip , 9090);
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(reenvioDestinatario.getOutputStream());
					paqueteReenvio .writeObject(paqueteRecibido);
					reenvioDestinatario.close();
					miSocket.close();
				}else {
					/*
					 * Después de crear las líneas referentes a enviar un Ip justo cuando
					 * se inicie el cliente, nos dará un error por lo que tenemos descrito en los apuntes
					 * La solución es implementar un condicional para distiguir si es la 1ª vez
					 * que se conecta ó no.
					 */

					//---------------------------------------------------------------------------
					// Código que está implementado a continuación es para saber que Ip se conecta
					InetAddress dirClientes = miSocket.getInetAddress();// ahora tenemos la Ip pero en un formato no valido.
					String ipClientesConectados = dirClientes.getHostAddress();// En está variable ya tenemos la Ip en String
					System.out.print("Dirección remota conectada: " + ipClientesConectados);
					// Incluiremos el método add para la ArrayList. Cada vez que se conecte un nuevo usuario, se añadirá a la lista
					listaIpConectados.add(ipClientesConectados);
					//Agregamos al paquete destinatario la ArrayList que hemos creado
					paqueteRecibido.setIp(ipClientesConectados);
					for (String IP : listaIpConectados) {
						System.out.println("ArrayList: " + IP);
						Socket reenvioDestinatario = new Socket(IP , 9090);
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(reenvioDestinatario.getOutputStream());
						paqueteReenvio .writeObject(paqueteRecibido);
						reenvioDestinatario.close();
						miSocket.close();
					}
					//---------------------------------------------------------------------------
					
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private	JTextArea areatexto;

}