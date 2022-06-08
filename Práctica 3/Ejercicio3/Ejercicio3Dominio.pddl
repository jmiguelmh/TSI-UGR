(define (domain ejercicio3)

    (:requirements :strips :adl)

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

    ; Se define las constantes VCE de tipo unidad, CentroDeMando, Barracones y Extractor de tipo edificio, y
    ; Mineral y Gas de tipo recurso
    (:constants
        VCE - tipoUnidad
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

        ; El predicado recursosNecesario determina para un tipo de edificio ?te el tipo de recurso ?tr necesario
        (recursosNecesarios ?te - tipoEdificio ?tr - tipoRecurso)
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
        )
        :effect (and
            ; La unidad pasa estar en la localizacion destino
            (en ?u ?y)

            ; La unidad deja de estar en la localizacion origen
            (not (en ?u ?x))
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
                (exists (?e - edificio)
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

            ; Se disponen de todos los recursos necesarios para construir el edificio
            (forall (?tr - tipoRecurso)
                (exists (?te - tipoEdificio)
                    (and
                        (esTipoEdificio ?e ?te)

                        (imply (recursosNecesarios ?e ?tr)
                           (extrayendo ?tr)
                        )
                    )
                )
            )

        )
        :effect (and
            ; El edificio se ha construido
            (edificioConstruido ?e)

            ; El edificio se encuentra en la localizacion
            (en ?e ?x)
        )
    )

)