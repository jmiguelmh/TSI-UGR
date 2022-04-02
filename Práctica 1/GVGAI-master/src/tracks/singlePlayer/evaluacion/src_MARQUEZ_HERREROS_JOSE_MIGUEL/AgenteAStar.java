package tracks.singlePlayer.evaluacion.src_MARQUEZ_HERREROS_JOSE_MIGUEL;

import java.util.ArrayList;
import java.util.Collections;

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
	ArrayList<ACTIONS> ruta;
	ArrayList<Observation> grid[][];
	
	// Parametros a medir
	int nodosExpandidos = 0;
	int nodosMemoria = 0;
	
	// Struct nodo
	public static class nodo {
		public Vector2d posicion;
		public ACTIONS accion;
		public nodo padre;
		int f;

		public nodo() {
			posicion = new Vector2d(0, 0);
			accion = ACTIONS.ACTION_NIL;
			padre = null;
			f = 0;
		}

		public nodo(nodo n) {
			this.posicion = new Vector2d(n.posicion);
			this.accion = n.accion;
			this.padre = n.padre;
			this.f = n.f;
			
		}
	}
	
	int g(nodo n)
	{
		nodo aux = new nodo(n);
		int g = 0;
		
		while(aux.padre != null)
		{
			aux = aux.padre;
			g++;
		}
		
		return g;
	}
	
	int h(nodo n, nodo destino)
	{
		return (int) (Math.abs(destino.posicion.x - n.posicion.x) + Math.abs(destino.posicion.y - n.posicion.y));
	}
	
	int f(nodo n, nodo destino)
	{
		return g(n) + h(n, destino);
	}
	
	nodo mejorCandidato(ArrayList<nodo> abiertos, nodo destino)
	{
		nodo mejor = abiertos.get(0);
		int mejorF = abiertos.get(0).f;
		
		for(int i = 1; i < abiertos.size(); i++)
			if(abiertos.get(i).f < mejorF)
			{
				mejorF = abiertos.get(i).f;
				mejor = abiertos.get(i);
			}
		
		return mejor;
	}
	
	boolean visitado(ArrayList<nodo> lista, nodo n)
	{
		boolean resultado = false;
		
		for(int i = 0; i < lista.size() && !resultado; i++)
			if(lista.get(i).posicion.x == n.posicion.x && lista.get(i).posicion.y == n.posicion.y)
				resultado = true;
		
		return resultado;
	}
	
	void borrarNodo(ArrayList<nodo> lista, nodo n)
	{
		for(int i = 0; i < lista.size(); i++)
			if(lista.get(i).posicion.x == n.posicion.x && lista.get(i).posicion.y == n.posicion.y)
				lista.remove(i);
	}
	
	// Pathfinding A*
	void AStarFS(nodo inicial, nodo destino) {
		ArrayList<nodo> abiertos = new ArrayList<nodo>();
		ArrayList<nodo> cerrados = new ArrayList<nodo>();
		int coste;
		
		nodo actual = new nodo();
		abiertos.add(inicial);
		
		while(true)
		{
			actual = mejorCandidato(abiertos, destino);
			if(actual.posicion.x == destino.posicion.x && actual.posicion.y == destino.posicion.y)
				break;
			
			nodosExpandidos++;
			
			abiertos.remove(actual);
			cerrados.add(actual);
			
			
			// Casilla arriba
			nodo arriba = new nodo();
			arriba.posicion = new Vector2d(actual.posicion.x, actual.posicion.y - 1);
			arriba.accion = ACTIONS.ACTION_UP;
			arriba.padre = actual;
			arriba.f = f(arriba, destino);
			coste = g(actual) + h(actual, arriba);
			
			if(!muros.contains(arriba.posicion))
			{
				if(arriba.posicion.x < grid.length && arriba.posicion.y < grid[(int) arriba.posicion.x].length)
				{
					if(grid[(int) arriba.posicion.x][(int) arriba.posicion.y].isEmpty() || grid[(int) arriba.posicion.x][(int) arriba.posicion.y].get(0).itype == 3)
					{
						if(visitado(abiertos, arriba) && coste < g(arriba))
							borrarNodo(abiertos, arriba);
						
						if(visitado(cerrados, arriba) && coste < g(arriba))
							borrarNodo(cerrados, arriba);
						
						if(!visitado(abiertos, arriba) && !visitado(cerrados, arriba))
						{
							arriba.f = coste + h(arriba, destino);
							abiertos.add(arriba);
						}
					}
				}
			}
			
			// Casilla abajo
			nodo abajo = new nodo();
			abajo.posicion = new Vector2d(actual.posicion.x, actual.posicion.y + 1);
			abajo.accion = ACTIONS.ACTION_DOWN;
			abajo.padre = actual;
			abajo.f = f(abajo, destino);
			coste = g(actual) + h(actual, abajo);
			
			if(!muros.contains(abajo.posicion))
			{
				if(abajo.posicion.x < grid.length && abajo.posicion.y < grid[(int) abajo.posicion.x].length)
				{
					if(grid[(int) abajo.posicion.x][(int) abajo.posicion.y].isEmpty() || grid[(int) abajo.posicion.x][(int) abajo.posicion.y].get(0).itype == 3)
					{
						if(visitado(abiertos, abajo) && coste < g(abajo))
							borrarNodo(abiertos, abajo);
						
						if(visitado(cerrados, abajo) && coste < g(abajo))
							borrarNodo(cerrados, abajo);
						
						if(!visitado(abiertos, abajo) && !visitado(cerrados, abajo))
						{
							abajo.f = coste + h(abajo, destino);
							abiertos.add(abajo);
						}
					}
				}	
			}
			
			// Casilla izquierda
			nodo izquierda = new nodo();
			izquierda.posicion = new Vector2d(actual.posicion.x - 1, actual.posicion.y);
			izquierda.accion = ACTIONS.ACTION_LEFT;
			izquierda.padre = actual;
			izquierda.f = f(izquierda, destino);
			coste = g(actual) + h(actual, izquierda);	
			
			if(!muros.contains(izquierda.posicion))
			{
				if(izquierda.posicion.x < grid.length && izquierda.posicion.y < grid[(int) izquierda.posicion.x].length)
				{
					if(grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].isEmpty() || grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].get(0).itype == 3)
					{
						if(visitado(abiertos, izquierda) && coste < g(izquierda))
							borrarNodo(abiertos, izquierda);
						
						if(visitado(cerrados, izquierda) && coste < g(izquierda))
							borrarNodo(cerrados, izquierda);
						
						if(!visitado(abiertos, izquierda) && !visitado(cerrados, izquierda))
						{
							izquierda.f = coste + h(izquierda, destino);
							abiertos.add(izquierda);
						}
					}
				}
			}
			
			// Casilla derecha
			nodo derecha = new nodo();
			derecha.posicion = new Vector2d(actual.posicion.x + 1, actual.posicion.y);
			derecha.accion = ACTIONS.ACTION_RIGHT;
			derecha.padre = actual;
			derecha.f = f(derecha, destino);
			coste = g(actual) + h(actual, derecha);
			
			if(derecha.posicion.x < grid.length && derecha.posicion.y < grid[(int) derecha.posicion.x].length)
			{
				if(grid[(int) derecha.posicion.x][(int) derecha.posicion.y].isEmpty() || grid[(int) derecha.posicion.x][(int) derecha.posicion.y].get(0).itype == 3)
				{
					if(visitado(abiertos, derecha) && coste < g(derecha))
						borrarNodo(abiertos, derecha);
					
					if(visitado(cerrados, derecha) && coste < g(derecha))
						borrarNodo(cerrados, derecha);
					
					if(!visitado(abiertos, derecha) && !visitado(cerrados, derecha))
					{
						derecha.f = coste + h(derecha, destino);
						abiertos.add(derecha);
					}						
				}
			}
			
			if(abiertos.size() > nodosMemoria)
				nodosMemoria = abiertos.size();
			
			if(cerrados.size() > nodosMemoria)
				nodosMemoria = cerrados.size();
		}
		
		while(actual.padre != null)
		{
			ruta.add(actual.accion);
			actual = actual.padre;
		}
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
		
		ruta = new ArrayList<ACTIONS>();
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
		
		inicio.f = f(inicio, destino);
		destino.f = 0;
		
		if(ruta.isEmpty())
		{
			AStarFS(inicio, destino);

			// La lista esta al reves, le damos la vuelta
			Collections.reverse(ruta);
			
			System.out.println("Ruta: " + ruta);
			System.out.println("Tamaño de la ruta: " + ruta.size());
			System.out.println("Tiempo de cálculo: " + elapsedTimer);
			System.out.println("Nodos expandidos: " + nodosExpandidos);
			System.out.println("Nodos en memoria: " + nodosMemoria);
		}

		accion = ruta.remove(0);
		return accion;
	}
}