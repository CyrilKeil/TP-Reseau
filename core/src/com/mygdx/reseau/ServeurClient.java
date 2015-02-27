package com.mygdx.reseau;
import java.awt.Point;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


public class ServeurClient{
	public final String SERVEUR = "serveur";
	public final String CLIENT = "client";
	public final int PORT = 10666;
	
	public String state;
	public Socket socket;
	public ServerSocket serverSocket;
	public InetAddress localAddress;
	
	public Properties myProperties;
	public Properties hisProperties;
	
	public void init() throws IOException{
	
		// De base, on tente de se connecter à un serveur
		// écoutant sur le port défini en constante
		try{
			localAddress = InetAddress.getLocalHost();
			socket = new Socket(localAddress, PORT);
			
			
		}catch(IOException e){
			// Si aucun serveur n'est trouvé, notre application va créer
			// un serveur écoutant sur le port défini en constante.
			serverSocket = new ServerSocket(PORT);
			socket = serverSocket.accept();
		}
		finally{
			// On récupère les propriétés de notre application
			compareProperties();
		}
	}
	
	private void compareProperties() {
		myProperties = new Properties();
		
		// on récupère nos propriétées.
		try {
			
			myProperties.load(new FileInputStream("..\\core\\properties\\tpreseau.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// On envoie nos propriétés à l'autre client
		try{
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream objectOut = new ObjectOutputStream(out);
		objectOut.writeObject(myProperties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// On récupère les propriétés de l'autre client
		try {
			InputStream in = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(in);
			hisProperties = (Properties) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// On compare les propriétés reçus et celles envoyées
		
		if(!(myProperties == hisProperties)){
			//throw new RuntimeException("Les propiétés des deux clients ne sont pas les mêmes");
			System.out.println(myProperties);
			System.out.println(hisProperties);
		}
		
		
	}

	/**
	* Méthode pour envoyer un point d'une application vers une autre
	*/
	public void envoiPoint(Point p) throws IOException{
	    OutputStream out = socket.getOutputStream();
	    ObjectOutputStream objectOut = new ObjectOutputStream(out);
	    
	    // Envoi message
		objectOut.writeObject(p);
	}
	
	/**
	* Méthode permettant de recevoir un point envoyé par une autre application
	*/
	public Point recevoirPoint() throws ClassNotFoundException, IOException{
		InputStream in = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
		Point p = (Point) ois.readObject();
		
		return p;
	}
}
