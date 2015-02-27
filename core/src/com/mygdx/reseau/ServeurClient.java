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
	
		// De base, on tente de se connecter � un serveur
		// �coutant sur le port d�fini en constante
		try{
			localAddress = InetAddress.getLocalHost();
			socket = new Socket(localAddress, PORT);
			
			
		}catch(IOException e){
			// Si aucun serveur n'est trouv�, notre application va cr�er
			// un serveur �coutant sur le port d�fini en constante.
			serverSocket = new ServerSocket(PORT);
			socket = serverSocket.accept();
		}
		finally{
			// On r�cup�re les propri�t�s de notre application
			compareProperties();
		}
	}
	
	private void compareProperties() {
		myProperties = new Properties();
		
		// on r�cup�re nos propri�t�es.
		try {
			
			myProperties.load(new FileInputStream("..\\core\\properties\\tpreseau.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// On envoie nos propri�t�s � l'autre client
		try{
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream objectOut = new ObjectOutputStream(out);
		objectOut.writeObject(myProperties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// On r�cup�re les propri�t�s de l'autre client
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
		
		// On compare les propri�t�s re�us et celles envoy�es
		
		if(!(myProperties == hisProperties)){
			//throw new RuntimeException("Les propi�t�s des deux clients ne sont pas les m�mes");
			System.out.println(myProperties);
			System.out.println(hisProperties);
		}
		
		
	}

	/**
	* M�thode pour envoyer un point d'une application vers une autre
	*/
	public void envoiPoint(Point p) throws IOException{
	    OutputStream out = socket.getOutputStream();
	    ObjectOutputStream objectOut = new ObjectOutputStream(out);
	    
	    // Envoi message
		objectOut.writeObject(p);
	}
	
	/**
	* M�thode permettant de recevoir un point envoy� par une autre application
	*/
	public Point recevoirPoint() throws ClassNotFoundException, IOException{
		InputStream in = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
		Point p = (Point) ois.readObject();
		
		return p;
	}
}
