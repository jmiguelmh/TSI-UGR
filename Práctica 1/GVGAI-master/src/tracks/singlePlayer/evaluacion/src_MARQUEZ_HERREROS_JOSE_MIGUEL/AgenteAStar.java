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
	
	// ArrayList donde se almacena la informaci�n de todas las casillas
	// del mapa, se utiliza para comprobar si en una casilla hay una trampa
	ArrayList<Observation> grid[][];
	
	// Parametros a medir
	int nodosExpandidos = 0;
	int nodosMemoria = 0;
	
	// Estructura nodo que se va a utilizar
	// Esta estructura contiene la siguiente informaci�n:
	// posicion de la casilla, la �ltima acci�n que se realiz� para llegar
	// a ese nodo, esto es para la facilitar la construcci�n de la ruta
	// recorriendo los padres, un nodo padre y f (valor heur�stico de la casilla).
	public static class nodo {
		public Vector2d posicion;
		public ACTIONS accion;
		public nodo padre;
		int f;
		
		// Constructor por defecto de nodo
		public nodo() {
			posicion = new Vector2d(0, 0);
			accion = ACTIONS.ACTION_NIL;
			padre = null;
			f = 0;
		}
		
		// Constructor de copia de nodo
		public nodo(nodo n) {
			this.posicion = new Vector2d(n.posicion);
			this.accion = n.accion;
			this.padre = n.padre;
			this.f = n.f;
			
		}
	}
	
	// Funci�n para calcular el valor g de un nodo (distancia desde el inicio)
	// Se calcula contando el n�mero de padres que tiene el nodo, se hace sobre
	// un nodo auxiliar para no modificarlo
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
	
	// Funci�n para calcular el valor h de un nodo (distancia manhattan al nodo destino)
	int h(nodo n, nodo destino)
	{
		return (int) (Math.abs(destino.posicion.x - n.posicion.x) + Math.abs(destino.posicion.y - n.posicion.y));
	}
	
	// Funci�n para calcular el valor f de un nodo (g+h), se ahce uso de las dos funciones anteriores
	int f(nodo n, nodo destino)
	{
		return g(n) + h(n, destino);
	}
	
	// Funci�n que busca el mejor candidato (nodo con la menor f) en la lista de abiertos
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
	
	// Funci�n que nos indica si la posicion de un nodo ya ha sido visitada, no se puede utilizar el
	// contains de ArrayList ya que no solo en cuenta la posici�n he introduce nodos repetidos en la
	// lista de abiertos
	boolean visitado(ArrayList<nodo> lista, nodo n)
	{
		boolean resultado = false;
		
		for(int i = 0; i < lista.size() && !resultado; i++)
			if(lista.get(i).posicion.x == n.posicion.x && lista.get(i).posicion.y == n.posicion.y)
				resultado = true;
		
		return resultado;
	}
	
	// Func�on que borra los nodos de un ArrayList que tienen su misma posicion
	void borrarNodo(ArrayList<nodo> lista, nodo n)
	{
		for(int i = 0; i < lista.size(); i++)
			if(lista.get(i).posicion.x == n.posicion.x && lista.get(i).posicion.y == n.posicion.y)
				lista.remove(i);
	}
	
	// Pathfinding A*
	void AStarFS(nodo inicial, nodo destino) {
		// Inicializaci�n de abiertos y cerrados
		ArrayList<nodo> abiertos = new ArrayList<nodo>();
		ArrayList<nodo> cerrados = new ArrayList<nodo>();
		int coste;
		
		// A�adimos el nodo actual en abiertos
		nodo actual = new nodo();
		abiertos.add(inicial);
		
		while(true)
		{
			// Busca el mejor candidato
			actual = mejorCandidato(abiertos, destino);
			
			// Comprueba si es un nodo objetivo
			if(actual.posicion.x == destino.posicion.x && actual.posicion.y == destino.posicion.y)
				break;
			
			// Un nodo m�s explorado
			nodosExpandidos++;
			
			// Lo eliminamos de abiertos para a�adirlo a cerrados
			abiertos.remove(actual);
			cerrados.add(actual);
			
			// A partir del nodo obtenido como mejor candidato exploramos en todas las direcciones
			// Se sigue la prioridad de exploraci�n: arriba, abajo, izquierda, derecha
			// Para cada nodo a expandir se crea un nuevo nodo con el nombre de la direccion en la
			// que vamos a expandir y le asignamos toda la informaci�n necesaria (nueva posici�n, 
			// acci�n requerida, el padre y su valor f). Mediante unos if comprobamos si se puede 
			// expandir en esa direcci�n (no se haya visitado, no se un muro y no sea una trampa). 
			// Al final se a�ade a visitados y se hace una llamada recursiva a ese nodo.
			
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
						// Si est� en abiertos y su coste el coste del nodo actual es menor que la g de 
						// del nodo arriba lo borramos de abiertos
						if(visitado(abiertos, arriba) && coste < g(arriba))
							borrarNodo(abiertos, arriba);
						
						// Si est� en cerrados y su coste el coste del nodo actual es menor que la g de 
						// del nodo arriba lo borramos de cerrados
						if(visitado(cerrados, arriba) && coste < g(arriba))
							borrarNodo(cerrados, arriba);
						
						// Si no est� ni en abiertos ni cerrados, calculamos su coste y lo metemos en abiertos
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
						// Si est� en abiertos y su coste el coste del nodo actual es menor que la g de 
						// del nodo abajo lo borramos de abiertos
						if(visitado(abiertos, abajo) && coste < g(abajo))
							borrarNodo(abiertos, abajo);
						
						// Si est� en cerrados y su coste el coste del nodo actual es menor que la g de 
						// del nodo abajo lo borramos de cerrados
						if(visitado(cerrados, abajo) && coste < g(abajo))
							borrarNodo(cerrados, abajo);
						
						// Si no est� ni en abiertos ni cerrados, calculamos su coste y lo metemos en abiertos
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
						// Si est� en abiertos y su coste el coste del nodo actual es menor que la g de 
						// del nodo izquierda lo borramos de abiertos
						if(visitado(abiertos, izquierda) && coste < g(izquierda))
							borrarNodo(abiertos, izquierda);
						
						// Si est� en cerrados y su coste el coste del nodo actual es menor que la g de 
						// del nodo izquierda lo borramos de cerrados
						if(visitado(cerrados, izquierda) && coste < g(izquierda))
							borrarNodo(cerrados, izquierda);
						
						// Si no est� ni en abiertos ni cerrados, calculamos su coste y lo metemos en abiertos
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
					// Si est� en abiertos y su coste el coste del nodo actual es menor que la g de 
					// del nodo derecha lo borramos de abiertos
					if(visitado(abiertos, derecha) && coste < g(derecha))
						borrarNodo(abiertos, derecha);
					
					// Si est� en cerrados y su coste el coste del nodo actual es menor que la g de 
					// del nodo derecha lo borramos de cerrados
					if(visitado(cerrados, derecha) && coste < g(derecha))
						borrarNodo(cerrados, derecha);
					
					// Si no est� ni en abiertos ni cerrados, calculamos su coste y lo metemos en abiertos
					if(!visitado(abiertos, derecha) && !visitado(cerrados, derecha))
					{
						derecha.f = coste + h(derecha, destino);
						abiertos.add(derecha);
					}						
				}
			}
			
			// Actualizamos el valor de nodosMemoria siempre que el tama�o de abiertos sea mayor.
			// De esta manera, nos quedamos con el tama�o m�ximo que tuvo abiertos.
			if(abiertos.size() > nodosMemoria)
				nodosMemoria = abiertos.size();
			
			// Actualizamos el valor de nodosMemoria siempre que el tama�o de cerrados sea mayor.
			// De esta manera, nos quedamos con el tama�o m�ximo que tuvo cerrados.
			if(cerrados.size() > nodosMemoria)
				nodosMemoria = cerrados.size();
			
			// nodosMemoria al final de la ejecuci�n tiene el tama�o m�s grande abiertos o cerrados
		}
		
		// Reconstruimos la ruta a trav�s de los padres
		while(actual.padre != null)
		{
			ruta.add(actual.accion);
			actual = actual.padre;
		}
	}
	
	// Constructor
	public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		
		// Obtenci�n de la escala del mundo
		fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
				stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
		
		// Obtenci�n de la posici�n del portal (meta)
		ArrayList<Observation>[] portales = stateObs.getPortalsPositions();
		portal = portales[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
		
		// Obtenci�n de la posici�n del avatar
		avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
		
		// Inicializaci�n de un ArrayList que contiene las posiciones de todos los muros del mapa
		muros = new ArrayList<Vector2d>();
		ArrayList<Observation>[] murosAux = stateObs.getImmovablePositions();
		for(int i=0; i<murosAux[0].size(); i++) {
		        Vector2d muro = murosAux[0].get(i).position;
		        muro.x = Math.floor(muro.x / fescala.x);
		        muro.y = Math.floor(muro.y / fescala.y);
		        muros.add(muro);
		}
		
		// Inicializaci�n de ciertas estructuras
		ruta = new ArrayList<ACTIONS>();
		grid = stateObs.getObservationGrid();
	}
	
	
	// Metodo act
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// Acci�n por defecto
		ACTIONS accion = ACTIONS.ACTION_NIL;
		
		// Creamos los nodos incio y destino con sus valores asociados
		nodo inicio = new nodo();
		inicio.posicion = avatar;
		
		nodo destino = new nodo();
		destino.posicion = portal;
		
		inicio.f = f(inicio, destino);
		destino.f = 0;
		
		// El c�lculo de la ruta solo se debe a ejecutar una vez en cada ejecuci�n del m�todo act
		if(ruta.isEmpty())
		{
			AStarFS(inicio, destino);

			// La lista esta al reves porque la reconstruimos a trav�s de los nodos padre, le damos la vuelta
			Collections.reverse(ruta);
			
			// Mostramos los resultados por pantalla
			System.out.println("Ruta: " + ruta);
			System.out.println("Tama�o de la ruta: " + ruta.size());
			System.out.println("Tiempo de c�lculo: " + elapsedTimer);
			System.out.println("Nodos expandidos: " + nodosExpandidos);
			System.out.println("Nodos en memoria: " + nodosMemoria);
		}
		
		// Obtenemos la acci�n de la ruta calculada
		accion = ruta.remove(0);
		return accion;
	}
}