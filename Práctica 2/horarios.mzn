% Use this editor as a MiniZinc scratch book
include "globals.mzn";

% Array con las horas semanales de cada asignatura
array[1..9] of int: horasAsignatura = [4,2,4,4,4,2,2,2,1];

% Array con las horas diarias maximas de cada asignatura
array[1..9] of int: horasMaximas = [2,1,2,2,2,1,1,2,1];

% Un array para cada dia de la semana para representar las 6 horas
array[1..6] of var 0..9: lunes;
array[1..6] of var 0..9: martes;
array[1..6] of var 0..9: miercoles;
array[1..6] of var 0..9: jueves;
array[1..6] of var 0..9: viernes;

% Restricciones
% No se pueden exceder el numero de horas diarias para cada asignatura
constraint forall(i in 1..9)(count(lunes,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(martes,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(miercoles,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(jueves,i) <= horasMaximas[i]);
constraint forall(i in 1..9)(count(viernes,i) <= horasMaximas[i]);

% No se pueden exceder el numero de horas semanales para cada asignatura
constraint forall(i in 1..9)((count(lunes,i) + count(martes,i) + count(miercoles,i) + count(jueves,i) + count(viernes,i)) == horasAsignatura[i]);

% Todos los dias hay recrea a cuarta hora, solo una hora de recreo
constraint lunes[4] == 0 /\ count(lunes,0) == 1;
constraint martes[4] == 0 /\ count(martes,0) == 1;
constraint miercoles[4] == 0 /\ count(miercoles,0) == 1;
constraint jueves[4] == 0 /\ count(jueves,0) == 1;
constraint viernes[4] == 0 /\ count(viernes,0) == 1;

% Las asignaturas que tienen 2 horas al dia (1,3,4,5,8) tienen que impartirse de forma consecutiva
constraint forall(i in 2..5)(horasMaximas[lunes[i]] == 2 -> (lunes[i+1] == lunes[i] \/ lunes[i-1] == lunes[i]));
constraint forall(i in 2..5)(horasMaximas[martes[i]] == 2 -> (martes[i+1] == martes[i] \/ martes[i-1] == martes[i]));
constraint forall(i in 2..5)(horasMaximas[miercoles[i]] == 2 -> (miercoles[i+1] == miercoles[i] \/ miercoles[i-1] == miercoles[i]));
constraint forall(i in 2..5)(horasMaximas[jueves[i]] == 2 -> (jueves[i+1] == jueves[i] \/ jueves[i-1] == jueves[i]));
constraint forall(i in 2..5)(horasMaximas[viernes[i]] == 2 -> (viernes[i+1] == viernes[i] \/ viernes[i-1] == viernes[i]));

% El profesor 1 solo puede impartir las asignaturas 1 o 3 en un dia
constraint (count(lunes,1) > 0) -> (count(lunes,3) == 0);
constraint (count(martes,1) > 0) -> (count(martes,3) == 0);
constraint (count(miercoles,1) > 0) -> (count(miercoles,3) == 0);
constraint (count(jueves,1) > 0) -> (count(jueves,3) == 0);

% El profesor 2 solo puede impartir las asignaturas 4 o 5 en un dia
constraint (count(lunes,4) > 0) -> (count(lunes,5) == 0);
constraint (count(martes,4) > 0) -> (count(martes,5) == 0);
constraint (count(jueves,4) > 0) -> (count(jueves,5) == 0);
constraint (count(viernes,4) > 0) -> (count(viernes,5) == 0);

% El profesor 3 solo puede impartir las asignaturas 6 o 9 en un dia
constraint (count(lunes,6) > 0) -> (count(lunes,9) == 0);
constraint (count(miercoles,6) > 0) -> (count(miercoles,9) == 0);
constraint (count(viernes,6) > 0) -> (count(viernes,9) == 0);

% El profesor 1 solo puede dar las dos ultimas horas del dia (quinta y sexta)
constraint forall(i in 5..6)(lunes[i] == 1 \/ lunes[i] == 3);
constraint forall(i in 5..6)(martes[i] == 1 \/ martes[i] == 3);
constraint forall(i in 5..6)(miercoles[i] == 1 \/ miercoles[i] == 3);
constraint forall(i in 5..6)(jueves[i] == 1 \/ jueves[i] == 3);

% El profesor 2 solo puede dar las dos primeras horas del dia (primera y segunda)
constraint forall(i in 1..2)(lunes[i] == 4 \/ lunes[i] == 5);
constraint forall(i in 1..2)(martes[i] == 4 \/ martes[i] == 5);
constraint forall(i in 1..2)(jueves[i] == 4 \/ jueves[i] == 5);
constraint forall(i in 1..2)(viernes[i] == 4 \/ viernes[i] == 5);

% El profesor 3 solo puede dar la hora antes del recreo (tercera)
constraint (lunes[3] == 6 \/ lunes[3] == 9);
constraint (miercoles[3] == 6 \/ miercoles[3] == 9);
constraint (viernes[3] == 6 \/ viernes[3] == 9);

% La asignatura 1 solo puede impartirse los lunes y martes
constraint (count(lunes,1) == horasMaximas[1]);
constraint (count(martes,1) == horasMaximas[1]);

% La asignatura 3 solo puede impartirse los miercoles y jueves
constraint (count(miercoles,3) == horasMaximas[3]);
constraint (count(jueves,3) == horasMaximas[3]);

% La asignatura 4 solo puede impartirse los lunes y martes
constraint (count(lunes,4) == horasMaximas[4]);
constraint (count(martes,4) == horasMaximas[4]);

% La asignatura 5 solo puede impartirse los jueves y viernes
constraint (count(jueves,5) == horasMaximas[5]);
constraint (count(viernes,5) == horasMaximas[5]);

% La asignatura 6 solo puede impartirse los miercoles y viernes
constraint (count(miercoles,6) == horasMaximas[6]);
constraint (count(viernes,6) == horasMaximas[6]);

% La asignatura 7 solo puede impartirse los martes y viernes
constraint (count(martes,7) == horasMaximas[7]);
constraint (count(viernes,7) == horasMaximas[7]);

% La asignatura 8 solo puede impartirse los miercoles
constraint (count(miercoles,8) == horasMaximas[8]);

% La asignatura 9 solo puede impartirse los lunes
constraint (count(lunes,9) == horasMaximas[9]);

solve satisfy;