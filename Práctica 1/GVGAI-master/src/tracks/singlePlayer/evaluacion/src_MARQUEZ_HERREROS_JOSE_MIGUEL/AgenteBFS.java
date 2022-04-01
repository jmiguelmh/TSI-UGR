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

public class AgenteBFS extends AbstractPlayer {
	Vector2d avatar;
	Vector2d fescala;
	Vector2d portal;
	ArrayList<Vector2d> muros;
	ArrayList<ACTIONS> ruta;
	ArrayList<Observation> grid[][];

	// Struct nodo
	public static class nodo {
		public Vector2d posicion;
		public ACTIONS accion;
		public nodo padre;

		public nodo() {
			posicion = new Vector2d(0, 0);
			accion = ACTIONS.ACTION_NIL;
			padre = null;
		}
	}

	// Pathfinding anchura
	ArrayList<ACTIONS> BFS(nodo inicial, nodo destino) {
		ArrayList<ACTIONS> ruta = new ArrayList<ACTIONS>();
		Queue<nodo> cola = new LinkedList<nodo>();
		ArrayList<Vector2d> visitados = new ArrayList<Vector2d>();
		cola.add(inicial);
		visitados.add(inicial.posicion);
		
		while(!cola.isEmpty())
		{
			nodo actual = cola.remove();
			if(actual.posicion.x == destino.posicion.x && actual.posicion.y == destino.posicion.y)
			{
				while(actual.padre != null)
				{
					ruta.add(actual.accion);
					actual = actual.padre;
				}
				break;
			}
			
			// Casilla arriba
			nodo arriba = new nodo();
			arriba.posicion = new Vector2d(actual.posicion.x, actual.posicion.y - 1);
			if(!visitados.contains(arriba.posicion) && !muros.contains(arriba.posicion))
			{
				if(arriba.posicion.x < grid.length && arriba.posicion.y < grid[(int) arriba.posicion.x].length)
					if(grid[(int) arriba.posicion.x][(int) arriba.posicion.y].isEmpty() || grid[(int) arriba.posicion.x][(int) arriba.posicion.y].get(0).itype == 3)
					{
						arriba.accion = ACTIONS.ACTION_UP;
						arriba.padre = actual;
						cola.add(arriba);
						visitados.add(arriba.posicion);
					}
			}
			
			// Casilla abajo
			nodo abajo = new nodo();
			abajo.posicion = new Vector2d(actual.posicion.x, actual.posicion.y + 1);
			if(!visitados.contains(abajo.posicion) && !muros.contains(abajo.posicion))
			{
				if(abajo.posicion.x < grid.length && abajo.posicion.y < grid[(int) abajo.posicion.x].length)
					if(grid[(int) abajo.posicion.x][(int) abajo.posicion.y].isEmpty() || grid[(int) abajo.posicion.x][(int) abajo.posicion.y].get(0).itype == 3)
					{
						abajo.accion = ACTIONS.ACTION_DOWN;
						abajo.padre = actual;
						cola.add(abajo);
						visitados.add(abajo.posicion);
					}
			}
			
			// Casilla izquierda
			nodo izquierda = new nodo();
			izquierda.posicion = new Vector2d(actual.posicion.x - 1, actual.posicion.y);
			if(!visitados.contains(izquierda.posicion) && !muros.contains(izquierda.posicion))
			{
				if(izquierda.posicion.x < grid.length && izquierda.posicion.y < grid[(int) izquierda.posicion.x].length)
					if(grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].isEmpty() || grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].get(0).itype == 3)
					{
						izquierda.accion = ACTIONS.ACTION_LEFT;
						izquierda.padre = actual;
						cola.add(izquierda);
						visitados.add(izquierda.posicion);
					}
			}
			
			// Casilla derecha
			nodo derecha = new nodo();
			derecha.posicion = new Vector2d(actual.posicion.x + 1, actual.posicion.y);
			if(!visitados.contains(derecha.posicion) && !muros.contains(derecha.posicion))
			{
				if(derecha.posicion.x < grid.length && derecha.posicion.y < grid[(int) derecha.posicion.x].length)
					if(grid[(int) derecha.posicion.x][(int) derecha.posicion.y].isEmpty() || grid[(int) derecha.posicion.x][(int) derecha.posicion.y].get(0).itype == 3)
					{					
						derecha.accion = ACTIONS.ACTION_RIGHT;
						derecha.padre = actual;
						cola.add(derecha);
						visitados.add(derecha.posicion);
					}
			}
		}
		
		
		return ruta;
	}

	// Constructor
	public AgenteBFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

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
		
		ruta = new ArrayList<ACTIONS>();
		grid = stateObs.getObservationGrid();
	}

	// Metodo act
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		ACTIONS accion = ACTIONS.ACTION_NIL;
		
		nodo inicio = new nodo();
		inicio.posicion = new Vector2d(avatar.x, avatar.y);
		nodo destino = new nodo();
		destino.posicion = new Vector2d(portal.x, portal.y);

		if(ruta.isEmpty())
			ruta = BFS(inicio, destino);
		
		// La ruta esta al reves, se lee del final al inicio
		accion = ruta.get(ruta.size() - 1);
		ruta.remove(ruta.size() - 1);
		return accion;
	}
}
