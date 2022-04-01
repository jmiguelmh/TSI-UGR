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

public class AgenteDFS extends AbstractPlayer {
	Vector2d avatar;
	Vector2d fescala;
	Vector2d portal;
	ArrayList<Vector2d> muros;
	Queue<ACTIONS> ruta;
	ArrayList<Observation> grid[][];
	ArrayList<Vector2d> visitados; // TODO pasarlo como copia a las llamadas recursivas, hay posiciones repetidas
	int nodosExpandidos;
	
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
	
	// Pathfinding profundidad
		void DFS(nodo inicial, nodo destino, StateObservation stateObs) {
			visitados.add(inicial.posicion);
			inicial.padre = null;
			DFS_search(inicial, destino, stateObs);
		}
		
		void DFS_search(nodo actual, nodo destino, StateObservation stateObs)
		{
			if(actual.posicion.x == destino.posicion.x && actual.posicion.y == destino.posicion.y)
			{
				ruta = actual.secuencia;
			}
			else
			{				
				// Casilla arriba
				nodo arriba = new nodo();
				arriba.posicion = new Vector2d(actual.posicion);
				arriba.posicion.y -= 1;
				
				// No es un muro y no ha sido visitado
				if(!muros.contains(arriba.posicion) && !visitados.contains(arriba.posicion))
				{
					// Evitar pinchos
					if(grid[(int) arriba.posicion.x][(int) arriba.posicion.y].isEmpty() || grid[(int) arriba.posicion.x][(int) arriba.posicion.y].get(0).itype == 3)
					{
						arriba.padre = actual;
						visitados.add(arriba.posicion);
						arriba.secuencia = new LinkedList<ACTIONS>(arriba.padre.secuencia);
						arriba.secuencia.add(ACTIONS.ACTION_UP);
						nodosExpandidos++;
						DFS(arriba, destino, stateObs);
					}
				}
				
				// Casilla abajo
				nodo abajo = new nodo();
				abajo.posicion = new Vector2d(actual.posicion);
				abajo.posicion.y += 1;
				
				// No es un muro y no ha sido visitado
				if(!muros.contains(abajo.posicion) && !visitados.contains(abajo.posicion))
				{
					// Evitar pinchos
					if(grid[(int) abajo.posicion.x][(int) abajo.posicion.y].isEmpty() || grid[(int) abajo.posicion.x][(int) abajo.posicion.y].get(0).itype == 3)
					{
						abajo.padre = actual;
						visitados.add(abajo.posicion);
						abajo.secuencia = new LinkedList<ACTIONS>(abajo.padre.secuencia);
						abajo.secuencia.add(ACTIONS.ACTION_DOWN);
						nodosExpandidos++;
						DFS(abajo, destino, stateObs);
					}
				}
				
				// Casilla izquierda
				nodo izquierda = new nodo();
				izquierda.posicion = new Vector2d(actual.posicion);
				izquierda.posicion.x -= 1;
				
				// No es un muro y no ha sido visitado
				if(!muros.contains(izquierda.posicion) && !visitados.contains(izquierda.posicion))
				{
					// Evitar pinchos
					if(grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].isEmpty() || grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].get(0).itype == 3)
					{
						izquierda.padre = actual;
						visitados.add(izquierda.posicion);
						izquierda.secuencia = new LinkedList<ACTIONS>(izquierda.padre.secuencia);
						izquierda.secuencia.add(ACTIONS.ACTION_LEFT);
						nodosExpandidos++;
						DFS(izquierda, destino, stateObs);
					}
				}
				
				// Casilla derecha
				nodo derecha = new nodo();
				derecha.posicion = new Vector2d(actual.posicion);
				derecha.posicion.x += 1;
				
				// No es un muro y no ha sido visitado
				if(!muros.contains(derecha.posicion) && !visitados.contains(derecha.posicion))
				{
					// Evitar pinchos
					if(grid[(int) derecha.posicion.x][(int) derecha.posicion.y].isEmpty() || grid[(int) derecha.posicion.x][(int) derecha.posicion.y].get(0).itype == 3)
					{
						derecha.padre = actual;
						visitados.add(derecha.posicion);
						derecha.secuencia = new LinkedList<ACTIONS>(derecha.padre.secuencia);
						derecha.secuencia.add(ACTIONS.ACTION_RIGHT);
						nodosExpandidos++;
						DFS(derecha, destino, stateObs);
					}
				}
			}
		}
	
	// Constructor
		public AgenteDFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

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
			visitados = new ArrayList<Vector2d>();
			nodosExpandidos = 0;
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
			{
				DFS(inicio, destino, stateObs);
	
				System.out.println("Ruta: " + ruta);
				System.out.println("Tamaño de la ruta: " + ruta.size());
				System.out.println("Nodos expandidos: " + nodosExpandidos);
				System.out.println("Nodos en memoria: " + visitados.size());
			}

			accion = ruta.remove();
			return accion;
		}
}