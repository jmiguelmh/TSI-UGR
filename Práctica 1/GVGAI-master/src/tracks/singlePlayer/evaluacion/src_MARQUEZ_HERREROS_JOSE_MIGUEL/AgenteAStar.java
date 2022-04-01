package tracks.singlePlayer.evaluacion.src_MARQUEZ_HERREROS_JOSE_MIGUEL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteAStar extends AbstractPlayer {
	Vector2d avatar;
	Vector2d fescala;
	Vector2d portal;
	ArrayList<Vector2d> muros;
	Queue<ACTIONS> ruta;
	ArrayList<Observation> grid[][];
	
	// Struct nodo
	public static class nodo {
		public Vector2d posicion;
		public Queue<ACTIONS> secuencia;
		public nodo padre;

		public nodo() {
			posicion = new Vector2d(0, 0);
			secuencia = new LinkedList<ACTIONS>();
			padre = null;
		}
	}
	
	nodo mejorCandidato(ArrayList<nodo> abiertos, nodo inicial, nodo destino)
	{
		nodo mejorCandidato = abiertos.get(0);
		int h = (int) (Math.abs(mejorCandidato.posicion.x - destino.posicion.x) + Math.abs(mejorCandidato.posicion.y - destino.posicion.y));
		int g = mejorCandidato.secuencia.size();
		int f = g + h;
		
		for(int i = 1; i < abiertos.size(); i++)
		{
			nodo nodoAux = new nodo();
			nodoAux = abiertos.get(i);
			h = (int) (Math.abs(nodoAux.posicion.x - destino.posicion.x) + Math.abs(nodoAux.posicion.y - nodoAux.posicion.y));
			g = nodoAux.secuencia.size();
			int fAux = g + h;
			if(fAux < f)
			{
				mejorCandidato = nodoAux;
				f = fAux;
			}
		}
		
		return mejorCandidato;
	}
	
	// Pathfinding A*
	Queue<ACTIONS> AFS(nodo inicial, nodo destino, StateObservation stateObs) {
		Queue<ACTIONS> ruta = new LinkedList<ACTIONS>();
		ArrayList<nodo> abiertos = new ArrayList<nodo>();
		ArrayList<nodo> cerrados = new ArrayList<nodo>();
		
		abiertos.add(inicial);
		
		while(true)
		{
			nodo actual = mejorCandidato(abiertos, inicial, destino);
			if(actual.posicion.x == destino.posicion.x && actual.posicion.y == destino.posicion.y)
			{
				ruta = actual.secuencia;
				break;
			}
			
			abiertos.remove(actual);
			
			// Casilla arriba
			nodo arriba = new nodo();
			arriba.posicion = new Vector2d(actual.posicion);
			arriba.posicion.y -= 1;
			arriba.padre = actual;
			
			if(arriba != actual.padre && cerrados.contains(arriba) && arriba.secuencia.size() < actual.secuencia.size())
			{
				cerrados.remove(arriba);
				abiertos.add(arriba);
			}
			else if(!cerrados.contains(arriba) && !abiertos.contains(arriba))
				abiertos.add(arriba);
			else if(abiertos.contains(arriba) && arriba.secuencia.size() < actual.secuencia.size())
			{
				int i = abiertos.indexOf(arriba);
				abiertos.get(i).secuencia.add(ACTIONS.ACTION_UP);
				
			}
			
			// Casilla abajo
			nodo abajo = new nodo();
			abajo.posicion = new Vector2d(actual.posicion);
			abajo.posicion.y += 1;
			abajo.padre = actual;
			
			if(abajo != actual.padre && cerrados.contains(abajo) && abajo.secuencia.size() < actual.secuencia.size())
			{
				cerrados.remove(abajo);
				abiertos.add(abajo);
			}
			else if(!cerrados.contains(abajo) && !abiertos.contains(abajo))
				abiertos.add(abajo);
			else if(abiertos.contains(abajo) && abajo.secuencia.size() < actual.secuencia.size())
			{
				int i = abiertos.indexOf(abajo);
				abiertos.get(i).secuencia.add(ACTIONS.ACTION_DOWN);
				
			}
			
			// Casilla izquierda
			nodo izquierda = new nodo();
			izquierda.posicion = new Vector2d(actual.posicion);
			izquierda.posicion.x -= 1;
			izquierda.padre = actual;
			
			if(izquierda != actual.padre && cerrados.contains(izquierda) && izquierda.secuencia.size() < actual.secuencia.size())
			{
				cerrados.remove(izquierda);
				abiertos.add(izquierda);
			}
			else if(!cerrados.contains(izquierda) && !abiertos.contains(izquierda))
				abiertos.add(izquierda);
			else if(abiertos.contains(izquierda) && izquierda.secuencia.size() < actual.secuencia.size())
			{
				int i = abiertos.indexOf(izquierda);
				abiertos.get(i).secuencia.add(ACTIONS.ACTION_LEFT);
				
			}
			
			// Casilla derecha
			nodo derecha = new nodo();
			derecha.posicion = new Vector2d(actual.posicion);
			derecha.posicion.x -= 1;
			derecha.padre = actual;
			
			if(derecha != actual.padre && cerrados.contains(derecha) && derecha.secuencia.size() < actual.secuencia.size())
			{
				cerrados.remove(derecha);
				abiertos.add(derecha);
			}
			else if(!cerrados.contains(derecha) && !abiertos.contains(derecha))
				abiertos.add(derecha);
			else if(abiertos.contains(derecha) && derecha.secuencia.size() < actual.secuencia.size())
			{
				int i = abiertos.indexOf(derecha);
				abiertos.get(i).secuencia.add(ACTIONS.ACTION_LEFT);
				
			}
		}
		
		return ruta;
	}
	
	// Constructor
	public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		
		fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
				stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

		ArrayList<Observation>[] portales = stateObs.getPortalsPositions();
		portal = portales[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
		
		avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
		
		muros = new ArrayList<Vector2d>();
		ArrayList<Observation>[] murosAux = stateObs.getImmovablePositions();
		for(int i=0; i<murosAux[0].size(); i++) {
		        Vector2d muro = murosAux[0].get(i).position;
		        muro.x = Math.floor(muro.x / fescala.x);
		        muro.y = Math.floor(muro.y / fescala.y);
		        muros.add(muro);
		}
		
		ruta = new LinkedList<ACTIONS>();
		grid = stateObs.getObservationGrid();
	}
	
	
	// Metodo act
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		ACTIONS accion = ACTIONS.ACTION_NIL;
		
		nodo inicio = new nodo();
		inicio.posicion = avatar;
		
		nodo destino = new nodo();
		destino.posicion = portal;
		if(ruta.isEmpty())
			ruta = AFS(inicio, destino, stateObs);

		accion = ruta.remove();
		return accion;
	}
}