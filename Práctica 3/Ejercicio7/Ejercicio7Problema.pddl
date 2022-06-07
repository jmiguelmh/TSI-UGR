(define (problem ejercicio7)
    (:domain ejercicio7)
    (:objects
        VCE1 VCE2 VCE3 Soldado1 Marine1 Marine2 - tipoUnidad
        CentroDeMando1 Extractor1 Barracones1 - tipoEdificio
        LOC11 LOC12 LOC13 LOC14 LOC21 LOC22 LOC23 LOC24 LOC31 LOC32 LOC33 LOC34 LOC44 - localizacion
    )

    (:init
        ; Se define el mapa utilizando el predicado existeCamino para
        ; indicar entre que localizaciones se puede mover una unidad
        (existeCamino LOC11 LOC12)
        (existeCamino LOC11 LOC21)
        (existeCamino LOC12 LOC11)
        (existeCamino LOC12 LOC22)
        (existeCamino LOC13 LOC14)
        (existeCamino LOC13 LOC23)
        (existeCamino LOC14 LOC13)
        (existeCamino LOC14 LOC24)
        (existeCamino LOC21 LOC11)
        (existeCamino LOC21 LOC31)
        (existeCamino LOC22 LOC12)
        (existeCamino LOC22 LOC23)
        (existeCamino LOC22 LOC32)
        (existeCamino LOC23 LOC13)
        (existeCamino LOC23 LOC22)
        (existeCamino LOC23 LOC33)
        (existeCamino LOC24 LOC14)
        (existeCamino LOC24 LOC34)
        (existeCamino LOC31 LOC21)
        (existeCamino LOC31 LOC32)
        (existeCamino LOC32 LOC22)
        (existeCamino LOC32 LOC31)
        (existeCamino LOC33 LOC23)
        (existeCamino LOC33 LOC34)
        (existeCamino LOC34 LOC24)
        (existeCamino LOC34 LOC33)
        (existeCamino LOC34 LOC44)
        (existeCamino LOC44 LOC34)

        ; Se definen los tipos de cada unidad
        (esTipoUnidad VCE1 VCE)
        (esTipoUnidad VCE2 VCE)
        (esTipoUnidad VCE3 VCE)
        (esTipoUnidad Marine1 Marine)
        (esTipoUnidad Marine2 Marine)
        (esTipoUnidad Soldado1 Soldado)

        ; Se definen los tipos de cada edificio
        (esTipoEdificio CentroDeMando1 CentroDeMando)
        (esTipoEdificio Extractor1 Extractor)
        (esTipoEdificio Barracones1 Barracones)

        ; Se definen las localizaciones de cada unidad
        (en VCE1 LOC11)

        ; Se definen las localizaciones de cada edificio
        (en CentroDeMando1 LOC11)
        
        ; Se definen los estados de las unidades
        (libre VCE1)

        ; Se definen las localizaciones de los recursos
        (asignarNodo Mineral LOC22)
        (asignarNodo Mineral LOC32)
        (asignarNodo Gas LOC44)

        ; Se definen los recursos necesarios para cada unidad
        (= (recursosNecesarios VCE2 Mineral) 5)
        (= (recursosNecesarios VCE2 Gas) 0)
        (= (recursosNecesarios VCE3 Mineral) 5)
        (= (recursosNecesarios VCE3 Gas) 0)
        (= (recursosNecesarios Marine1 Mineral) 10)
        (= (recursosNecesarios Marine1 Gas) 15)
        (= (recursosNecesarios Marine2 Mineral) 10)
        (= (recursosNecesarios Marine2 Gas) 15)
        (= (recursosNecesarios Soldado1 Mineral) 30)
        (= (recursosNecesarios Soldado1 Gas) 30)

        ; Se definen los recursos necesarios para cada edificio
        (= (recursosNecesarios Extractor1 Mineral) 10)
        (= (recursosNecesarios Extractor1 Gas) 0)   
        (= (recursosNecesarios Barracones1 Mineral) 30)
        (= (recursosNecesarios Barracones1 Gas) 10)

        ; Se definen los edificios construidos
        (edificioConstruido CentroDeMando1)

        ; Se define la cantidad de cada recurso
        (= (cantidadRecurso Mineral) 0)
        (= (cantidadRecurso Gas) 0)

        ; Se define el tope de cada recurso
        (= (topeRecurso Mineral) 60)
        (= (topeRecurso Gas) 60)

        ; Se define el numero de unidades extrayendo cada recurso
        (= (unidadesExtrayendo Mineral) 0)
        (= (unidadesExtrayendo Gas) 0)

        ; Se define el numero de recursos que recoge cada VCE cuando realiza la accion recolectar
        (= (cantidadRecolectada) 10)
    )

    (:goal
        (and
            ; Construir Barracones1 en LOC32
            (edificioConstruido Barracones1)
            (en Barracones1 LOC32)
            
            ; Reclutar a Marine1, Marine2 y Soldado1, y que esten en LOC31, LOC24 y LOC32 respectivamente
            (en Marine1 LOC31)
            (en Marine2 LOC24)
            (en Soldado1 LOC12)
        )
    )
)