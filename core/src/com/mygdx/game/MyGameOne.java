package com.mygdx.game;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mygdx.reseau.ServeurClient;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyGameOne extends ApplicationAdapter implements Runnable {
	
	
	SpriteBatch batch;
	Texture img;
	ShapeRenderer shapeRenderer;
	ArrayList<Point> cercles;
	ServeurClient serveurClient;
	int tailleCercle;
	String couleurCercle;
	
	
	@Override
	public void create () {
		// Notre application devient un client par défaut, ou un serveur si aucun serveur n'est trouvé.
		serveurClient = new ServeurClient();
		try {
			serveurClient.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// On récupère nos propriétés
		tailleCercle = (Integer.parseInt(serveurClient.myProperties.getProperty("cercle.taille")));
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(Color.BLUE);
		cercles = new ArrayList<Point>();
		(new Thread(this)).start();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		draw();
		inputHandler();
	}
	
	public void draw(){
		// On dessine nos cercles à chaque frame.
		shapeRenderer.begin(ShapeType.Filled);
		for(int i = 0; i < cercles.size(); i++){
			shapeRenderer.circle(cercles.get(i).x,cercles.get(i).y, tailleCercle);
		}
		shapeRenderer.end();
	}
	
	public void inputHandler(){
		// Quand on clique sur la fenetre, on rajoute un cercle à notre fenetre
		boolean leftPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		if ( leftPressed ){
			Point p = new Point(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			// Pour éviter d'avoir deux fois le même cercle à dessiner
			if(!cercles.contains(p)){
				cercles.add(p);
				try {
					// On envoie les coordonées du nouveau point créé à notre deuxième application
					serveurClient.envoiPoint(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void run() {
	// Notre thread créé dans la méthode run va se charger de 
	// récupérer les points créés et envoyés par notre deuxième application
		Point p;
		synchronized(this){
			try {
				while ((p = serveurClient.recevoirPoint()) != null){
						cercles.add(p);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
