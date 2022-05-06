% Use this editor as a MiniZinc scratch book
include "globals.mzn";

% Los array en este problema son de tipo int y representan la posicion de la casa
% La resolucion de este problema se basa en las posiciones

% El indice del array representa la bebida:
% 1: te
% 2: cafe
% 3: leche
% 4: zumo
% 5: agua
array[1..5] of var 1..5: bebidas;

% El indice del array representa el animal:
% 1: perro
% 2: caracol
% 3: zorro
% 4: caballo
% 5: cebra
array[1..5] of var 1..5: animales;

% El indice del array representa la profesion:
% 1: pintor
% 2: escultor
% 3: diplomatico
% 4: violista
% 5: medico
array[1..5] of var 1..5: profesiones;

% El indice del array representa el color:
% 1: rojo
% 2: verde
% 3: blanco
% 4: amarillo
% 5: azul
array[1..5] of var 1..5: colores;

% El indice del array representa la persona (nacionalidad):
% 1: vasco
% 2: catalan
% 3: gallego
% 4: navarro
% 5: andaluz
array[1..5] of var int: personas;

% Array de string para imprimir la solucion
array[1..5] of string: nacionalidades = ["vasco", "catalan", "gallego", "navarro", "andaluz"];

% Restricciones
% El vasco vive en la casa roja
constraint personas[1] == colores[1];

% El catalán tiene un perro
constraint personas[2] == animales[1];

% El gallego es un pintor
constraint personas[3] == profesiones[1];

% El navarro bebe te
constraint personas[4] == bebidas[1];

% El andaluz vive en la primera casa de la izquierda
constraint personas[5] == 1;

% El de la casa verde bebe café
constraint colores[2] == bebidas[2];

% La casa verde está al lado de la blanca y a su derecha
constraint colores[2] == colores[3] + 1;

% El escultor cría caracoles
constraint profesiones[2] == animales[2];

% El diplomático vive en la casa amarilla
constraint profesiones[3] == colores[4];

% En la casa central se bebe leche
constraint bebidas[3] == 3;

% La casa del andaluz está al lado de la azul
constraint ((personas[5] - colores[5]) == 1) \/ ((personas[5] - colores[5]) == -1);

% El violinista bebe zumo
constraint profesiones[4] == bebidas[4];

%  El zorro está en una casa al lado de la del médico
constraint ((animales[3] - profesiones[5]) == 1) \/ ((animales[3] - profesiones[5]) == -1);

% El caballo está en una casa al lado de la del diplomático
constraint ((animales[4] - profesiones[3]) == 1) \/ ((animales[4] - profesiones[3]) == -1);

% Todos los arrays deben contener posiciones diferentes (no pueden haber dos iguales en dos casas diferentes)
constraint all_different(bebidas);
constraint all_different(animales);
constraint all_different(profesiones);
constraint all_different(colores);
constraint all_different(personas);

solve satisfy;

% Imprimir por pantalla la solucion
% El indice 5 pertenece a la cebra, el contenido del array en la posicion 5 es la posicion de la casa,
output["La cebra está en la casa \(animales[5])\n"];

% Comprobamos que la persona vive en la misma casa que el agua (indice 5 de bebidas)
% Utilizamos el array de string para imprimir la nacionalidad de la persona
output[if fix(personas[i]) == fix(bebidas[5]) then "El \(nacionalidades[i]) bebe agua" endif | i in 1..5];