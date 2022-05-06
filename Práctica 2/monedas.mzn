% Use this editor as a MiniZinc scratch book

int: cantidad;
set of int: NumeroMonedas = 0..cantidad;

% Array con los valores de cada moneda
array[1..8] of int: valoresMonedas = [1,2,5,10,20,50,100,200];

% Array con las cantidades de cada moneda
array[1..8] of var NumeroMonedas: cantidadMonedas;

% Cantidad de centimos quitando la parte de los euros, se utiliza en el apartado b
var int: centimos;

% Cantidad de monedas utilizadas, se utiliza en el apartado c para obtener la solucion optimo
var 0..cantidad: minimo;

% Apartado a, buscar todas las combinaciones posibles para conseguir la cantidad introducida
constraint valoresMonedas[1] * cantidadMonedas[1] + valoresMonedas[2] * cantidadMonedas[2] +
           valoresMonedas[3] * cantidadMonedas[3] + valoresMonedas[4] * cantidadMonedas[4] + 
           valoresMonedas[5] * cantidadMonedas[5] + valoresMonedas[6] * cantidadMonedas[6] +
           valoresMonedas[7] * cantidadMonedas[7] + valoresMonedas[8] * cantidadMonedas[8]
           = cantidad;

% Apartado b, si el importe es superior a 100 se fuerza al programa a utilizar monedas de 1 y 2 euros
% Obtenemos la cantidad de centimos y forzamos que sea menor a 100

constraint cantidad >= 100 -> (cantidadMonedas[7] > 0 \/ cantidadMonedas[8] > 0);
constraint centimos = valoresMonedas[1] * cantidadMonedas[1] + valoresMonedas[2] * cantidadMonedas[2] +
                      valoresMonedas[3] * cantidadMonedas[3] + valoresMonedas[4] * cantidadMonedas[4] + 
                      valoresMonedas[5] * cantidadMonedas[5] + valoresMonedas[6] * cantidadMonedas[6];
constraint centimos < 100;

% Apartado c, buscar la solucion optima, es decir la que utiliza menor numero de monedas
constraint minimo = cantidadMonedas[1] + cantidadMonedas[2] + cantidadMonedas[3] + cantidadMonedas[4] +
                    cantidadMonedas[5] + cantidadMonedas[6] + cantidadMonedas[7] + cantidadMonedas[8];
solve minimize minimo;

%solve satisfy;