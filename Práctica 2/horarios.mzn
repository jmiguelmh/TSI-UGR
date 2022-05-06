% Use this editor as a MiniZinc scratch book
include "globals.mzn";

array[1..9] of int: horasAsignatura = [4,2,4,4,4,2,2,2,1];
array[1..9] of int: horasMaximas = [2,1,2,2,2,1,1,2,1];

array[1..6] of var 0..9: lunes;
array[1..6] of var 0..9: martes;
array[1..6] of var 0..9: miercoles;
array[1..6] of var 0..9: jueves;
array[1..6] of var 0..9: viernes;

% Restriccion maximas horas diarias
constraint forall(i in 1..9)(count(lunes,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(martes,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(miercoles,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(jueves,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(viernes,i) <= horasMaximas[i]);

% Restriccion maximas horas semanales
constraint forall(i in 1..9)((count(lunes,i) + count(martes,i) + count(miercoles,i) + count(jueves,i) + count(viernes,i)) == horasAsignatura[i]);

% Restriccion recreo
constraint lunes[4] == 0 /\ count(lunes,0) == 1;
constraint martes[4] == 0 /\ count(martes,0) == 1;
constraint miercoles[4] == 0 /\ count(miercoles,0) == 1;
constraint jueves[4] == 0 /\ count(jueves,0) == 1;
constraint viernes[4] == 0 /\ count(viernes,0) == 1;

% Restriccion horas consecutivas
constraint forall(i in 2..5)(horasMaximas[lunes[i]] == 2 -> (lunes[i+1] == lunes[i] \/ lunes[i-1] == lunes[i]));
constraint forall(i in 2..5)(horasMaximas[martes[i]] == 2 -> (martes[i+1] == martes[i] \/ martes[i-1] == martes[i]));
constraint forall(i in 2..5)(horasMaximas[miercoles[i]] == 2 -> (miercoles[i+1] == miercoles[i] \/ miercoles[i-1] == miercoles[i]));
constraint forall(i in 2..5)(horasMaximas[jueves[i]] == 2 -> (jueves[i+1] == jueves[i] \/ jueves[i-1] == jueves[i]));
constraint forall(i in 2..5)(horasMaximas[viernes[i]] == 2 -> (viernes[i+1] == viernes[i] \/ viernes[i-1] == viernes[i]));

% Restricciones de profesores (solo un bloque al dia)
% Profesor 1
constraint (count(lunes,1) > 0) -> (count(lunes,3) == 0);
constraint (count(martes,1) > 0) -> (count(martes,3) == 0);
constraint (count(miercoles,1) > 0) -> (count(miercoles,3) == 0);
constraint (count(jueves,1) > 0) -> (count(jueves,3) == 0);

% Profesor 2
constraint (count(lunes,4) > 0) -> (count(lunes,5) == 0);
constraint (count(martes,4) > 0) -> (count(martes,5) == 0);
constraint (count(jueves,4) > 0) -> (count(jueves,5) == 0);
constraint (count(viernes,4) > 0) -> (count(viernes,5) == 0);

% Profesor 3
constraint (count(lunes,6) > 0) -> (count(lunes,9) == 0);
constraint (count(miercoles,6) > 0) -> (count(miercoles,9) == 0);
constraint (count(viernes,6) > 0) -> (count(viernes,9) == 0);

% Restricciones de profesores (horas del dia)
% Profesor 1
constraint forall(i in 5..6)(lunes[i] == 1 \/ lunes[i] == 3);
constraint forall(i in 5..6)(martes[i] == 1 \/ martes[i] == 3);
constraint forall(i in 5..6)(miercoles[i] == 1 \/ miercoles[i] == 3);
constraint forall(i in 5..6)(jueves[i] == 1 \/ jueves[i] == 3);

% Profesor 2
constraint forall(i in 1..2)(lunes[i] == 4 \/ lunes[i] == 5);
constraint forall(i in 1..2)(martes[i] == 4 \/ martes[i] == 5);
constraint forall(i in 1..2)(jueves[i] == 4 \/ jueves[i] == 5);
constraint forall(i in 1..2)(viernes[i] == 4 \/ viernes[i] == 5);

% Profesor 3
constraint (lunes[3] == 6 \/ lunes[3] == 9);
constraint (miercoles[3] == 6 \/ miercoles[3] == 9);
constraint (viernes[3] == 6 \/ viernes[3] == 9);

% Restricciones de asignaturas por dias
% Asignatura 1
constraint (count(lunes,1) == horasMaximas[1]);
constraint (count(martes,1) == horasMaximas[1]);

% Asignatura 3
constraint (count(miercoles,3) == horasMaximas[3]);
constraint (count(jueves,3) == horasMaximas[3]);

% Asignatura 4
constraint (count(lunes,4) == horasMaximas[4]);
constraint (count(martes,4) == horasMaximas[4]);

% Asignatura 5
constraint (count(jueves,5) == horasMaximas[5]);
constraint (count(viernes,5) == horasMaximas[5]);

% Asignatura 6
constraint (count(miercoles,6) == horasMaximas[6]);
constraint (count(viernes,6) == horasMaximas[6]);

% Asignatura 7
constraint (count(martes,7) == horasMaximas[7]);
constraint (count(viernes,7) == horasMaximas[7]);

% Asignatura 8
constraint (count(miercoles,8) == horasMaximas[8]);

% Asignatura 9
constraint (count(lunes,9) == horasMaximas[9]);