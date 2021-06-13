package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao = new ImdbDAO();
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> idMap;
	private Simulatore simulatore;
	
	public Model() {
		dao = new ImdbDAO();
		idMap = new HashMap<>();
		dao.listAllActors(idMap);
	}
	
	public void creaGrafo(String genere) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo vertici
		Graphs.addAllVertices(this.grafo, this.dao.getAttoriPerGenere(genere));
		
		// aggiungo archi
		for(Arco a: this.dao.getArchi(genere, idMap)) {
			Graphs.addEdge(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
	}
	
	public List<String> getGeneri() {
		return this.dao.getGeneri();
	}
	
	public List<Actor> getVertici() {
		List<Actor> result = new LinkedList<>();
		for(Actor a: grafo.vertexSet()) {
			result.add(a);
		}
		Collections.sort(result);
		return result;
	}
	
	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Actor> cercaAttoriSimili(Actor attore1) {
		List<Actor> result = new ArrayList<>();
		
		for(Actor successivo: Graphs.successorListOf(grafo, attore1)) {
			for(Actor succ: Graphs.successorListOf(grafo, successivo)) {
				if(!result.contains(succ))
					result.add(succ);
				
			}
		}
		result.remove(attore1);
		Collections.sort(result);
		return result;
	}
	
	public void simula(int giorni) {
		simulatore = new Simulatore();
		simulatore.init(giorni, grafo);
		simulatore.run();
	}
	
	public Collection<Actor> getAttoriIntervistati(){
		return simulatore.getAttoriIntervistati();
	}
}
