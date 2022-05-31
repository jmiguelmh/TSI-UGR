(define (domain ejercicio4)

    (:requirements :strips :adl)

    (:types
        entidad localizacion recurso - object
        unidad edificio - entidad

        tipoUnidad - unidad
        tipoEdificio - edificio
        tipoRecurso - recurso
    )

    (:constants
        VCE Marine Soldado - tipoUnidad
        CentroDeMando Barracones Extractor - tipoEdificio
        Mineral Gas - tipoRecurso
    )

    (:predicates
        (en ?obj - entidad ?x - localizacion)
        (existeCamino ?x - localizacion ?y - localizacion)
        (edificioConstruido ?e - edificio)
        (asignarNodo ?r - recurso ?x - localizacion)
        (extrayendo ?r - recurso)
        (esTipoUnidad ?u - unidad ?tu - tipoUnidad)
        (esTipoEdificio ?e - edificio ?te - tipoEdificio)
        (libre ?u - unidad)
        (recursosNecesarios ?e - entidad ?tr - tipoRecurso)
    )

    ;Acciones
    (:action Navegar
        :parameters (?u - unidad ?x ?y - localizacion)
        :precondition (and
            (en ?u ?x)
            (existeCamino ?x ?y)
        )
        :effect (and
            (en ?u ?y)
            (not (en ?u ?x))
        )
    )

    (:action Asignar
        :parameters (?u - unidad ?x - localizacion ?r - recurso)
        :precondition (and
            (en ?u ?x)
            (asignarNodo ?r ?x)
            (libre ?u)

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
            (extrayendo ?r)
            (not (libre ?u))
        )
    )

    (:action Construir
        :parameters (?u - unidad ?e - edificio ?x - localizacion)
        :precondition (and
            (libre ?u)
            (en ?u ?x)
            (not (exists
                    (?e - edificio)
                    (en ?e ?x))
            )

            (forall
                (?tr - tipoRecurso)
                (exists
                    (?te - tipoEdificio)
                    (and
                        (esTipoEdificio ?e ?te)

                        (imply
                            (recursosNecesarios ?e ?tr)
                            (extrayendo ?tr)
                        )
                    )
                )
            )

        )
        :effect (and
            (edificioConstruido ?e)
            (en ?e ?x)
        )
    )

    (:action Reclutar
        :parameters (?e - edificio ?u - unidad ?x - localizacion)
        :precondition (and
            (not (exists(?loc - localizacion)
                (and
                    (en ?u ?loc)
                )
            )
            )

            (en ?e ?x)

            (imply (esTipoUnidad ?u VCE)
                (and
                    (extrayendo Mineral)
                    (esTipoEdificio ?e CentroDeMando)
                )
            )

            (imply (esTipoUnidad ?u Marine)
                (and
                    (extrayendo Mineral)
                    (esTipoEdificio ?e Barracones)
                )
            )

            (imply (esTipoUnidad ?u Soldado)
                (and
                    (extrayendo Mineral)
                    (extrayendo Gas)
                    (esTipoEdificio ?e Barracones)
                )
            )
        )
        :effect (and
            (en ?u ?x)
            (libre ?u)
        )
    )

)