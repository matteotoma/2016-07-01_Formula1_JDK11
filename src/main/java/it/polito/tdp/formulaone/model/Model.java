package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private FormulaOneDAO dao;
	private Graph<Driver, DefaultWeightedEdge> grafo;
	private Map<Integer, Driver> idMap;
	private Integer punteggio;
	private List<Driver> best;
	private Integer tasso;
	
	public Model() {
		this.dao = new FormulaOneDAO();
		this.punteggio = 0;
	}
	
	public List<Integer> getYears(){
		return dao.getAllYearsOfRace();
	}
	
	public void creaGrafo(Integer anno) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		dao.getDrivers(idMap);
		
		List<Integer> racesId = dao.getRacesId(anno);
		for(Integer id: racesId) {
			List<Driver> piloti = dao.getVertici(id, idMap);
			Driver vincitore = piloti.get(0);
			for(Driver d: piloti) {
				if(!grafo.containsVertex(d))
					grafo.addVertex(d);
				if(!d.equals(vincitore)) {
					if(grafo.getEdge(vincitore, d)==null)
						Graphs.addEdge(grafo, vincitore, d, 1);
					else {
						double pesoVecchio = grafo.getEdgeWeight(grafo.getEdge(vincitore, d));
						grafo.setEdgeWeight(grafo.getEdge(vincitore, d), pesoVecchio+1);
					}
				}
			}
		}
	}
	
	public Driver getBest() {
		int sommaE=0;
		int sommaU=0;
		Driver migliore = null;
		for(Driver d: grafo.vertexSet()) {
			for(Driver u: Graphs.successorListOf(grafo, d))
				sommaU += grafo.getEdgeWeight(grafo.getEdge(d, u));
			for(Driver e: Graphs.predecessorListOf(grafo, d))
				sommaE += grafo.getEdgeWeight(grafo.getEdge(e, d));
			int diff = sommaU - sommaE;
			if(diff > this.punteggio) {
				punteggio = diff;
				migliore = d;
			}
		}
		return migliore;
	}
	
	public List<Driver> trovaDreamTeam(Integer k) {
		this.best = new ArrayList<>();
		List<Driver> parziale = new ArrayList<>();
		this.tasso = Integer.MAX_VALUE;
		cerca(parziale, k, null);
		return best;
	}
	
	private void cerca(List<Driver> parziale, Integer k, Object object) {
		if(parziale.size() == k) {
			Integer t = calcolaTasso(parziale);
			if(t < tasso) {
				tasso = t;
				best = new ArrayList<>(parziale);
			}
			return;
		}
		
		for(Driver d: grafo.vertexSet()) {
			if(!parziale.contains(d)) {
				parziale.add(d);
				cerca(parziale, k, null);
				parziale.remove(d);
			}
		}
	}

	private Integer calcolaTasso(List<Driver> parziale) {
		List<Driver> sconfitti = new ArrayList<>(grafo.vertexSet());
		sconfitti.removeAll(parziale);
		int sommaU=0;
		for(Driver d: sconfitti)
			for(Driver u: Graphs.successorListOf(grafo, d))
				if(parziale.contains(u))
					sommaU += grafo.getEdgeWeight(grafo.getEdge(d, u));
		return sommaU;
	}

	public Integer getPunteggio() {
		return punteggio;
	}

	public int nVertici() {
		return grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return grafo.edgeSet().size();
	}

}
