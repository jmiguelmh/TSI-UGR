% Use this editor as a MiniZinc scratch book
include "globals.mzn";

array[1..9] of int: horasAsignatura = [4,2,4,4,4,2,2,2,1];
array[1..9] of int: horasMaximas = [2,1,2,2,2,1,1,2,1];

array[1..6] of var 0..9: lunes;
array[1..6] of var 0..9: martes;
array[1..6] of var 0..9: miercoles;
array[1..6] of var 0..9: jueves;
array[1..6] of var 0..9: viernes;

% Restriccion horas a la semana
constraint (count(lunes, 1) + count(martes, 1)) == horasAsignatura[1];
constraint (count(lunes, 2) + count(martes, 2) + count(miercoles, 2) + count(jueves, 2) + count(viernes, 2)) == horasAsignatura[2];
constraint (count(miercoles, 3) + count(jueves, 3)) == horasAsignatura[3];
constraint (count(lunes, 4) + count(martes, 4)) == horasAsignatura[4];
constraint (count(jueves, 5) + count(viernes, 5)) == horasAsignatura[5];
constraint (count(miercoles, 6) + count(viernes, 6)) == horasAsignatura[6];
constraint (count(martes, 7) + count(viernes, 7)) == horasAsignatura[7];
constraint count(miercoles, 8) == horasAsignatura[8];
constraint count(lunes, 9) == horasAsignatura[9];

% Restriccion recreo
constraint lunes[4] == 0;
constraint martes[4] == 0;
constraint miercoles[4] == 0;
constraint jueves[4] == 0;
constraint viernes[4] == 0;

% Profesor 1
constraint lunes[5] == 1;
constraint lunes[6] == 1;
constraint martes[5] == 1;
constraint martes[6] == 1;
constraint miercoles[5] == 3;
constraint miercoles[6] == 3;
constraint jueves[5] == 3;
constraint jueves[6] == 3;

% Profesor 2
constraint lunes[1] == 4;
constraint lunes[2] == 4;
constraint martes[1] == 4;
constraint martes[2] == 4;
constraint jueves[1] == 5;
constraint jueves[2] == 5;
constraint viernes[1] == 5;
constraint viernes[2] == 5;

% Profesor 3
constraint miercoles[3] == 6;
constraint viernes[3] == 6;
constraint lunes[3] == 9;

solve satisfy;