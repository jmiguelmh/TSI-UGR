% Use this editor as a MiniZinc scratch book

int: cantidad;
set of int: NumeroMonedas = 0..cantidad;

array[1..8] of int: valoresMonedas = [1,2,5,10,20,50,100,200];
array[1..8] of var NumeroMonedas: cantidadMonedas;

var int: centimos;
var 0..cantidad: minimo;

%var 0..cantidad: minimo;

% Ejercicio base

constraint valoresMonedas[1] * cantidadMonedas[1] + valoresMonedas[2] * cantidadMonedas[2] +
           valoresMonedas[3] * cantidadMonedas[3] + valoresMonedas[4] * cantidadMonedas[4] + 
           valoresMonedas[5] * cantidadMonedas[5] + valoresMonedas[6] * cantidadMonedas[6] +
           valoresMonedas[7] * cantidadMonedas[7] + valoresMonedas[8] * cantidadMonedas[8]
           = cantidad;

% Primera modificacion: usar monedas de 1 y 2 €

constraint cantidad >= 100 -> (cantidadMonedas[7] > 0 \/ cantidadMonedas[8] > 0);
constraint centimos = valoresMonedas[1] * cantidadMonedas[1] + valoresMonedas[2] * cantidadMonedas[2] +
                      valoresMonedas[3] * cantidadMonedas[3] + valoresMonedas[4] * cantidadMonedas[4] + 
                      valoresMonedas[5] * cantidadMonedas[5] + valoresMonedas[6] * cantidadMonedas[6];
constraint centimos < 100;

% Segunda modificacion: usar minimo numero de monedas
constraint minimo = cantidadMonedas[1] + cantidadMonedas[2] + cantidadMonedas[3] + cantidadMonedas[4] +
                    cantidadMonedas[5] + cantidadMonedas[6] + cantidadMonedas[7] + cantidadMonedas[8];
solve minimize minimo;

%solve satisfy;