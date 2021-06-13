package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulatore {

	private int nGiorni;
	
	private Graph<Actor, DefaultWeightedEdge> grafo;
	
	private List<Actor> tuttiGliAttori;
	private Map<Integer, Actor> attoriIntervistati;
	

	private int numeroPause;
	
	public void init(int nGiorni, Graph<Actor, DefaultWeightedEdge> grafo) {
		this.nGiorni = nGiorni;
		this.grafo = grafo;
		tuttiGliAttori = new LinkedList<Actor>(grafo.vertexSet());
		attoriIntervistati = new HashMap<>();
		numeroPause = 0;
		
	}
	
	public void run() {
		
		for(int i = 0; i< nGiorni; i++) {
			Random rand = new Random();
			
			if(i == 1) {
				Actor attore = tuttiGliAttori.get(rand.nextInt(tuttiGliAttori.size()));
				attoriIntervistati.put(i, attore);
				// tolgo l'attore da tutti gli attori disponibili
				tuttiGliAttori.remove(attore);
				System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + attore.toString() + ")");
			}
			
			if(i>=2 && !attoriIntervistati.containsKey(i-1) && !attoriIntervistati.containsKey(i-2)
					&& attoriIntervistati.get(i-1).getGender().equals(attoriIntervistati.get(i-2).getGender())) {
				numeroPause++;
				System.out.println("[GIORNO " + i + "] - pausa!");
			}
			
			if(rand.nextFloat()<=0.6) {
				Actor attore = tuttiGliAttori.get(rand.nextInt(tuttiGliAttori.size()));
				attoriIntervistati.put(i, attore);
				// tolgo l'attore da tutti gli attori disponibili
				tuttiGliAttori.remove(attore);
				System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + attore.toString() + ")");
			}
			else {
				// chiede consiglio all'attore intervistato il giorno prima
				Actor attorePrecedente = attoriIntervistati.get(i-1);
				Actor raccomandato = this.getAttoreRaccomandato(attorePrecedente);
				
				if(raccomandato == null) {
					// scelgo ancora in modo casuale
					Actor attore = tuttiGliAttori.get(rand.nextInt(tuttiGliAttori.size()));
					attoriIntervistati.put(i, attore);
					// tolgo l'attore da tutti gli attori disponibili
					tuttiGliAttori.remove(attore);
					System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + attore.toString() + ")");
				}
				else {
					attoriIntervistati.put(i, raccomandato);
					tuttiGliAttori.remove(raccomandato);
					System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + raccomandato.toString() + ")");
				}
			}
			
		}
	}
	
	public Actor getAttoreRaccomandato(Actor ultimoIntervistato) {
		Actor res = null;
		int max = -1;
		for(Actor a: Graphs.neighborListOf(grafo, ultimoIntervistato)) {
			if(grafo.getEdgeWeight(this.grafo.getEdge(ultimoIntervistato, a))>max) {
				res = a;
				max = (int) grafo.getEdgeWeight(this.grafo.getEdge(ultimoIntervistato, a));
			}
		}
		return res;
		
	}
	
	public Collection<Actor> getAttoriIntervistati() {
		return attoriIntervistati.values();
	}
	
}
