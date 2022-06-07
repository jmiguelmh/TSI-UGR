(define (domain ejercicio8)

    (:requirements :strips :adl :fluents)

    ; Todos los tipos derivan de object, entidad localizacion y recurso son los principales tipos
    ; A partir de entidad se definen los tipos unidad y edificio
    ; Se define un subtipo para unidad, edificio y recurso que son de un tipo concreto (constantes)
    (:types
        entidad localizacion recurso - object
        unidad edificio - entidad

        tipoUnidad - unidad
        tipoEdificio - edificio
        tipoRecurso - recurso
    )

    ; Se define las constantes VCE, Marine y Soldado de tipo unidad, CentroDeMando, Barracones
    ; y Extractor de tipo edificio, y Mineral y Gas de tipo recurso
    (:constants
        VCE Marine Soldado - tipoUnidad
        CentroDeMando Barracones Extractor - tipoEdificio
        Mineral Gas - tipoRecurso
    )

    (:predicates
        ; El predicado en determina que una entidad se encuentra en una localizacion
        (en ?obj - entidad ?x - localizacion)

        ; El predicado existeCamino determina que se puede navegar entre dos localizaciones
        (existeCamino ?x - localizacion ?y - localizacion)

        ; El predicado edificioConstruido determina si un edificio ya esta construido
        (edificioConstruido ?e - edificio)

        ; El predicado asignarNodo determina que en una localizacion esta presente un recurso
        (asignarNodo ?r - recurso ?x - localizacion)

        ; El predicado extrayendo determina que se obtenido un recurso concreto
        (extrayendo ?r - recurso)

        ; El predicado esTipoUnidad determina si una unidad es de un tipo concreto
        (esTipoUnidad ?u - unidad ?tu - tipoUnidad)

        ; El predicado esTipoEdificio determina si un edificio es de un tipo concreto
        (esTipoEdificio ?e - edificio ?te - tipoEdificio)

        ; El predicado libre determina si una unidad no esta realizando ninguna tarea
        (libre ?u - unidad)
    )

    (:functions
        ; Cantidad disponible de un tipo de recurso ?tr
        (cantidadRecurso ?tp - tipoRecurso)

        ; Cantidad maxima del que se puede disponer de un tipo de recurso ?tr
        (topeRecurso ?tp - tipoRecurso)

        ; Cantidad de recursos ?r necesarios para crear una entidad ?e (unidad o edificio)
        (recursosNecesarios ?e - entidad ?r - recurso)

        ; Cantidad de unidades VCE extrayendo un tipo de recurso ?tr
        (unidadesExtrayendo ?tr - tipoRecurso)

        ; Cantidad que de recursos que se recolecta al realizar la accion recolectar
        (cantidadRecolectada)

        ; Coste para calcular la solucion optima
        (costeTiempo)

        ; Coste para navegar entre dos localizaciones
        (distanciaEntreLocalizaciones)

        ; Velocidad de cada unidad, se utiliza para calcular el coste de navegacion
        (velocidadUnidad ?u)

        ; Coste en tiempo para crear una entidad
        (tiempoPorEntidad ?e)

        ; Coste en tiempo que se tarda en recolectar
        (tiempoRecolectar)
    )

    ; Acciones
    ; Navegar permite mover una unidad ?u de una localizacion ?x a otra ?y
    (:action Navegar
        :parameters (?u - unidad ?x ?y - localizacion)
        :precondition (and
            ; La unidad esta en la localizacion de origen
            (en ?u ?x)

            ; Existe un camino entre el origen y el destino
            (existeCamino ?x ?y)

            ; La unidad debe esta libre para navegar
            (libre ?u)
        )
        :effect (and
            ; La unidad pasa estar en la localizacion destino
            (en ?u ?y)

            ; La unidad deja de estar en la localizacion origen
            (not (en ?u ?x))

            ; Se incrementa costeTiempo en funcion de los que tarda en moverse la unidad
            (increase
                (costeTiempo)
                (/
                    (distanciaEntreLocalizaciones)
                    (velocidadUnidad ?u)
                )
            )
        )
    )

    ; Asignar permite a una unidad ?u extraer un recurso ?r en una localizacion ?x
    (:action Asignar
        :parameters (?u - unidad ?x - localizacion ?r - recurso)
        :precondition (and
            ; La unidad esta en la localizacion
            (en ?u ?x)

            ; Dicha localizacion tiene asignado el recurso
            (asignarNodo ?r ?x)

            ; La unidad esta libre para extraer
            (libre ?u)

            ; Para poder extraer Gas debe existir un Extractor
            (or
                (exists
                    (?e - edificio)
                    (and
                        (en ?e ?x)
                        (esTipoEdificio ?e Extractor)
                    )
                )
                (not (asignarNodo Gas ?x))
            )
        )
        :effect (and
            ; Se obtiene el recurso
            (extrayendo ?r)

            ; La unidad deja de estar libre
            (not (libre ?u))

            ; Se incrementa en 1 el numero de VCE extrayendo el recurso
            (increase (unidadesExtrayendo ?r) 1)
        )
    )

    ; Construir permite a una unidad ?u construir un edificio ?e en una localizacion ?x con un recurso ?r
    (:action Construir
        :parameters (?u - unidad ?e - edificio ?x - localizacion)
        :precondition (and
            ; La unidad esta libre para construir
            (libre ?u)

            ; La unidad esta en la localizacion
            (en ?u ?x)

            ; El edificio no ha sido construido anteriormente
            (not (edificioConstruido ?e))


            ; No hay otro edificio construido en la localizacion
            (not (exists (?e - edificio)
                (en ?e ?x))
            )
            
            ; Solo los VCE pueden construir
            (esTipoUnidad ?u VCE)

            ; Si el edificio es Barracones se comprueba si se dispone de los recursos necesarios
            (imply (esTipoEdificio ?e Barracones)
                (and
                    (>=
                        (cantidadRecurso Mineral)
                        (recursosNecesarios ?e Mineral)
                    )
                    (>=
                        (cantidadRecurso Gas)
                        (recursosNecesarios ?e Gas)
                    )
                )
            )

            ; Si el edificio es Extractor se comprueba si se dispone de los recursos necesarios
            (imply (esTipoEdificio ?e Extractor)
                (and
                    (asignarNodo Gas ?x)
                    (>=
                        (cantidadRecurso Mineral)
                        (recursosNecesarios ?e Mineral)
                    )
                    (>=
                        (cantidadRecurso Gas)
                        (recursosNecesarios ?e Gas)
                    )
                )
            )
        )
        :effect (and
            ; El edificio se ha construido
            (edificioConstruido ?e)

            ; El edificio se encuentra en la localizacion
            (en ?e ?x)

            ; Se decrementa la cantidad de Mineral en funcion de lo que necesita el edificio
            (decrease (cantidadRecurso Mineral) (recursosNecesarios ?e Mineral))

            ; Se decrementa la cantidad de Gas en funcion de lo que necesita el edificio
            (decrease (cantidadRecurso Gas) (recursosNecesarios ?e Gas))

            ; Se incrementa costeTiempo en funcion de los que tarda en construirse el edificio
            (increase
                (costeTiempo)
                (tiempoPorEntidad ?e)
            )
        )
    )

    ; Reclutar en un edificio ?e una unidad ?u en una localizacion ?x permite que esa unidad se pueda utilizar
    (:action Reclutar
        :parameters (?e - edificio ?u - unidad ?x - localizacion)
        :precondition (and
            ; No existe una localizacion en la que este la unidad
            (not (exists(?loc - localizacion)
                    (and
                        (en ?u ?loc)
                    )
                )
            )

            ; El edificio esta en la localizacion
            (en ?e ?x)

            ; Si el edificio es un CentroDeMando solo se puede reclutar VCE
            (imply (esTipoEdificio ?e CentroDeMando)
                (esTipoUnidad ?u VCE)
            )

            ; Si el edificio es Barracones solo se puede reclutar Marine y Soldado
            (imply (esTipoEdificio ?e Barracones)
                (or
                    (esTipoUnidad ?u Marine)
                    (esTipoUnidad ?u Soldado)
                )
            )

            ; Si el edificio es un Extractor no se puede reclutar
            (imply (esTipoEdificio ?e Extractor)
                (and
                    (not (esTipoUnidad ?u VCE))
                    (not (esTipoUnidad ?u Soldado))
                    (not (esTipoUnidad ?u Marine))
                )
            )

            ; Si la unidad es un VCE se comprueba si se dispone de los recursos necesarios
            (imply (esTipoUnidad ?u VCE)
                (and
                    (>=
                        (cantidadRecurso Mineral)
                        (recursosNecesarios ?u Mineral)
                    )
                    (>=
                        (cantidadRecurso Gas)
                        (recursosNecesarios ?u Gas)
                    )
                )
            )

            ; Si la unidad es un Marine se comprueba si se dispone de los recursos necesarios
            (imply (esTipoUnidad ?u Marine)
                (and
                    (>=
                        (cantidadRecurso Mineral)
                        (recursosNecesarios ?u Mineral)
                    )
                    (>=
                        (cantidadRecurso Gas)
                        (recursosNecesarios ?u Gas)
                    )
                )
            )

            ; Si la unidad es un Soldado se comprueba si se dispone de los recursos necesarios
            (imply (esTipoUnidad ?u Soldado)
                (and
                    (>=
                        (cantidadRecurso Mineral)
                        (recursosNecesarios ?u Mineral)
                    )
                    (>=
                        (cantidadRecurso Gas)
                        (recursosNecesarios ?u Gas)
                    )
                )
            )    
        )
        :effect (and
            ; La unidad se situa en la localizacion
            (en ?u ?x)

            ; La unidad esta libre para realizar acciones
            (libre ?u)

            ; Se decrementa la cantidad de Mineral en funcion de lo que necesita la unidad
            (decrease (cantidadRecurso Mineral) (recursosNecesarios ?u Mineral))

            ; Se decrementa la cantidad de Gas en funcion de lo que necesita la unidad
            (decrease (cantidadRecurso Gas) (recursosNecesarios ?u Gas))

            ; Se incrementa costeTiempo en funcion de los que tarda en reclutarse la unidad
            (increase
                (costeTiempo)
                (tiempoPorEntidad ?u)
            )
        )
    )

    ; Recolectar permite en una localizacion ?x obtener un recurso ?r
    (:action Recolectar
        :parameters (?x - localizacion ?r - recurso)
        :precondition (and
            ; Existe un VCE que se encuentre en la localizacion
            (exists (?u - unidad)
                (and
                    (esTipoUnidad ?u VCE)
                    (en ?u ?x)
                )
            )

            ; Dicha localizacion tiene asignado el recurso
            (asignarNodo ?r ?x)

            ; Se obtiene el recurso
            (extrayendo ?r)

            ; Se comprueba que el obtener el recurso no exceda el tope
            (<=
                (+
                    (cantidadRecurso ?r)
                    (*
                        (unidadesExtrayendo ?r)
                        (cantidadRecolectada)
                    )
                )
                (topeRecurso ?r)
            )
        )
        :effect (and
            ; Se incrementa la cantidad disponible del recurso
            (increase
                (cantidadRecurso ?r)
                (*
                    (unidadesExtrayendo ?r)
                    (cantidadRecolectada)
                )
            )

            ; Se incrementa costeTiempo en funcion de los que tarda en recolectar el recurso
            (increase 
                (costeTiempo)
                (tiempoRecolectar)
            )
        )
    )
    
)