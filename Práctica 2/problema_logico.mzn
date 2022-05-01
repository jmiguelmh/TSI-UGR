% Use this editor as a MiniZinc scratch book
include "globals.mzn";

% 1: te
% 2: cafe
% 3: leche
% 4: zumo
% 5: agua
array[1..5] of var 1..5: bebidas;

% 1: perro
% 2: caracol
% 3: zorro
% 4: caballo
% 5: cebra
array[1..5] of var 1..5: animales;

% 1: pintor
% 2: escultor
% 3: diplomatico
% 4: violista
% 5: medico
array[1..5] of var 1..5: profesiones;

% 1: rojo
% 2: verde
% 3: blanco
% 4: amarillo
% 5: azul
array[1..5] of var 1..5: colores;

% 1: vasco
% 2: catalan
% 3: gallego
% 4: navarro
% 5: andaluz
array[1..5] of var int: personas;

array[1..5] of string: nacionalidades = ["vasco", "catalan", "gallego", "navarro", "andaluz"];

% Restricciones
constraint personas[1] == colores[1];
constraint personas[2] == animales[1];
constraint personas[3] == profesiones[1];
constraint personas[4] == bebidas[1];
constraint personas[5] == 1;
constraint colores[2] == bebidas[2];
constraint colores[2] == colores[3] + 1;
constraint profesiones[2] == animales[2];
constraint profesiones[3] == colores[4];
constraint bebidas[3] == 3;
constraint ((personas[5] - colores[5]) == 1) \/ ((personas[5] - colores[5]) == -1);
constraint profesiones[4] == bebidas[4];
constraint ((animales[3] - profesiones[5]) == 1) \/ ((animales[3] - profesiones[5]) == -1);
constraint ((animales[4] - profesiones[3]) == 1) \/ ((animales[4] - profesiones[3]) == -1);

constraint all_different(bebidas);
constraint all_different(animales);
constraint all_different(profesiones);
constraint all_different(colores);
constraint all_different(personas);

solve satisfy;
output["La cebra está en la casa \(animales[5])\n"];
output[if fix(personas[i]) == fix(bebidas[5]) then "El \(nacionalidades[i]) bebe agua" endif | i in 1..5];