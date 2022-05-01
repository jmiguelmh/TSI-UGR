% Use this editor as a MiniZinc scratch book

int: cantidad;
set of int: NumeroMonedas = 0..infinity;

array[1..8] of int: valoresMonedas = [1,2,5,10,20,50,100,200];
array[1..8] of var NumeroMonedas: cantidadMonedas;

constraint valoresMonedas[1] * cantidadMonedas[1] + valoresMonedas[2] * cantidadMonedas[2] +
           valoresMonedas[3] * cantidadMonedas[3] + valoresMonedas[4] * cantidadMonedas[4] + 
           valoresMonedas[5] * cantidadMonedas[5] + valoresMonedas[6] * cantidadMonedas[6] +
           valoresMonedas[7] * cantidadMonedas[7] + valoresMonedas[8] * cantidadMonedas[8]
           = cantidad;

solve satisfy;