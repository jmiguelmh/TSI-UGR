package tracks.singlePlayer.evaluacion.src_MARQUEZ_HERREROS_JOSE_MIGUEL;

import java.util.ArrayList;
import java.util.Collections;

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
	ArrayList<ACTIONS> ruta;

	// ArrayList donde se almacena la informaci�n de todas las casillas
	// del mapa, se utiliza para comprobar si en una casilla hay una trampa
	ArrayList<Observation> grid[][];

	// ArrayList donde se van a almacenar las casillas visitadas,
	// es la que se usar� en el algoritmo de b�squeda para comprobar
	// si una casilla se ha visitado previamente, tambi�n se utilizar�
	// para medir el consumo de memoria
	ArrayList<Vector2d> visitados;

	// Parametros a medir
	int nodosExpandidos = 0;
	int nodosMemoria = 0;

	// Estructura nodo que se va a utilizar
	// Esta estructura contiene la siguiente informaci�n:
	// posicion de la casilla, la �ltima acci�n que se realiz� para llegar
	// a ese nodo, esto es para la facilitar la construcci�n de la ruta
	// recorriendo los padres y un nodo padre.
	public static class nodo {
		public Vector2d posicion;
		public ACTIONS accion;
		public nodo padre;
		
		// Constructor por defecto de nodo
		public nodo() {
			posicion = new Vector2d(0, 0);
			accion = ACTIONS.ACTION_NIL;
			padre = null;
		}
	}

	// Pathfinding profundidad
	void DFS(nodo inicial, nodo destino) {
		// Lo a�adidos a visitados para no volver a explorarlo
		if (!visitados.contains(inicial.posicion))
			visitados.add(inicial.posicion);
		
		// Llamamos a la funci�n auxiliar
		DFS_search(inicial, destino);
	}
	
	// Funci�n auxiliar recursiva encargada de la b�squeda en profundidad
	void DFS_search(nodo actual, nodo destino) {
		// Un nodo m�s explorado
		nodosExpandidos++;
		
		// Comprobamos si es el nodo destino
		if (actual.posicion.x == destino.posicion.x && actual.posicion.y == destino.posicion.y) {
			// Reconstruimos la ruta a trav�s de los padres
			while (actual.padre != null) {
				ruta.add(actual.accion);
				actual = actual.padre;
			}
			
			// Actualizamos el par�metro a medir
			nodosMemoria = visitados.size();
		} else {
			
			// A partir del nodo obtenido como par�metro exploramos en todas las direcciones
			// Se sigue la prioridad de exploraci�n: arriba, abajo, izquierda, derecha
			// Para cada nodo a expandir se crea un nuevo nodo con el nombre de la direccion en la
			// que vamos a expandir y le asignamos toda la informaci�n necesaria (nueva posici�n, 
			// acci�n requerida y el padre). Mediante unos if comprobamos si se puede expandir en esa
			// direcci�n (no se haya visitado, no se un muro y no sea una trampa). Al final se a�ade 
			// a visitados y se hace una llamada recursiva a ese nodo.

			// Casilla arriba
			nodo arriba = new nodo();
			arriba.posicion = new Vector2d(actual.posicion.x, actual.posicion.y - 1);
			if (!visitados.contains(arriba.posicion) && !muros.contains(arriba.posicion)) {
				if (arriba.posicion.x < grid.length && arriba.posicion.y < grid[(int) arriba.posicion.x].length)
					if (grid[(int) arriba.posicion.x][(int) arriba.posicion.y].isEmpty()
							|| grid[(int) arriba.posicion.x][(int) arriba.posicion.y].get(0).itype == 3) {
						arriba.accion = ACTIONS.ACTION_UP;
						arriba.padre = actual;
						visitados.add(arriba.posicion);
						DFS(arriba, destino);
					}
			}

			// Casilla abajo
			nodo abajo = new nodo();
			abajo.posicion = new Vector2d(actual.posicion.x, actual.posicion.y + 1);
			if (!visitados.contains(abajo.posicion) && !muros.contains(abajo.posicion)) {
				if (abajo.posicion.x < grid.length && abajo.posicion.y < grid[(int) abajo.posicion.x].length)
					if (grid[(int) abajo.posicion.x][(int) abajo.posicion.y].isEmpty()
							|| grid[(int) abajo.posicion.x][(int) abajo.posicion.y].get(0).itype == 3) {
						abajo.accion = ACTIONS.ACTION_DOWN;
						abajo.padre = actual;
						visitados.add(abajo.posicion);
						DFS(abajo, destino);
					}
			}

			// Casilla izquierda
			nodo izquierda = new nodo();
			izquierda.posicion = new Vector2d(actual.posicion.x - 1, actual.posicion.y);
			if (!visitados.contains(izquierda.posicion) && !muros.contains(izquierda.posicion)) {
				if (izquierda.posicion.x < grid.length
						&& izquierda.posicion.y < grid[(int) izquierda.posicion.x].length)
					if (grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].isEmpty()
							|| grid[(int) izquierda.posicion.x][(int) izquierda.posicion.y].get(0).itype == 3) {
						izquierda.accion = ACTIONS.ACTION_LEFT;
						izquierda.padre = actual;
						visitados.add(izquierda.posicion);
						DFS(izquierda, destino);
					}
			}

			// Casilla derecha
			nodo derecha = new nodo();
			derecha.posicion = new Vector2d(actual.posicion.x + 1, actual.posicion.y);
			if (!visitados.contains(derecha.posicion) && !muros.contains(derecha.posicion)) {
				if (derecha.posicion.x < grid.length && derecha.posicion.y < grid[(int) derecha.posicion.x].length)
					if (grid[(int) derecha.posicion.x][(int) derecha.posicion.y].isEmpty()
							|| grid[(int) derecha.posicion.x][(int) derecha.posicion.y].get(0).itype == 3) {
						derecha.accion = ACTIONS.ACTION_RIGHT;
						derecha.padre = actual;
						visitados.add(derecha.posicion);
						DFS(derecha, destino);
					}
			}
		}
	}

	// Constructor
	public AgenteDFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
		// Obtenci�n de la escala del mundo
		fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
				stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
		
		// Obtenci�n de la posici�n del portal (meta)
		ArrayList<Observation>[] portales = stateObs.getPortalsPositions();
		portal = portales[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
		
		// Obtenci�n de la posici�n del avatar
		avatar = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
		
		// Inicializaci�n de un ArrayList que contiene las posiciones de todos los muros del mapa
		muros = new ArrayList<Vector2d>();
		ArrayList<Observation>[] murosAux = stateObs.getImmovablePositions();
		for (int i = 0; i < murosAux[0].size(); i++) {
			Vector2d muro = murosAux[0].get(i).position;
			muro.x = Math.floor(muro.x / fescala.x);
			muro.y = Math.floor(muro.y / fescala.y);
			muros.add(muro);
		}
		
		// Inicializaci�n de ciertas estructuras
		ruta = new ArrayList<ACTIONS>();
		grid = stateObs.getObservationGrid();
		visitados = new ArrayList<Vector2d>();
	}

	// Metodo act
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// Acci�n por defecto
		ACTIONS accion = ACTIONS.ACTION_NIL;
		
		// Creamos los nodos incio y destino con sus valores asociados
		nodo inicio = new nodo();
		inicio.posicion = new Vector2d(avatar.x, avatar.y);
		nodo destino = new nodo();
		destino.posicion = new Vector2d(portal.x, portal.y);
		
		// El c�lculo de la ruta solo se debe a ejecutar una vez en cada ejecuci�n del m�todo act
		if (ruta.isEmpty()) {
			DFS(inicio, destino);

			// La lista esta al reves porque la reconstruimos a trav�s de los nodos padre, le damos la vuelta
			Collections.reverse(ruta);
			
			// Mostramos los resultados por pantalla
			System.out.println("Ruta: " + ruta);
			System.out.println("Tama�o de la ruta: " + ruta.size());
			System.out.println("Tiempo de c�lculo: " + elapsedTimer);
			System.out.println("Nodos expandidos: " + nodosExpandidos);
			System.out.println("Nodos en memoria: " + visitados.size());
		}
		
		// Obtenemos la acci�n de la ruta calculada
		accion = ruta.remove(0);
		return accion;
	}
}