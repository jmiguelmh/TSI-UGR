package tracks.singlePlayer.evaluacion.src_MARQUEZ_HERREROS_JOSE_MIGUEL;

import java.util.ArrayList;
import java.util.Collections;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteRTAStar extends AbstractPlayer {
	
	// Constructor
	public AgenteRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		
	}
	
	// Metodo act
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		return Types.ACTIONS.ACTION_NIL;
	}
}