(define (domain ejercicio8)

    (:requirements :strips :adl :fluents)

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
    )

    (:functions
        (cantidadRecurso ?tp - tipoRecurso)
        (topeRecurso ?tp - tipoRecurso)
        (recursosNecesarios ?e - entidad ?r - recurso)
        (unidadesExtrayendo ?tr - tipoRecurso)
        (cantidadRecolectada)
        (costeTiempo)
        (distanciaEntreLocalizaciones)
        (velocidadUnidad ?u)
        (tiempoPorEntidad ?e)
        (tiempoRecolectar)
    )

    ;Acciones
    (:action Navegar
        :parameters (?u - unidad ?x ?y - localizacion)
        :precondition (and
            (en ?u ?x)
            (existeCamino ?x ?y)
            (libre ?u)
        )
        :effect (and
            (en ?u ?y)
            (not (en ?u ?x))
            (increase
                (costeTiempo)
                (/
                    (distanciaEntreLocalizaciones)
                    (velocidadUnidad ?u)
                )
            )
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
            (increase (unidadesExtrayendo ?r) 1)
        )
    )

    (:action Construir
        :parameters (?u - unidad ?e - edificio ?x - localizacion)
        :precondition (and
            (libre ?u)
            (en ?u ?x)
            (not (exists
                    (?edificio - edificio)
                    (en ?edificio ?x))
            )
            
            (esTipoUnidad ?u VCE)
            (not (edificioConstruido ?e))

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
            (edificioConstruido ?e)
            (en ?e ?x)
            (decrease (cantidadRecurso Mineral) (recursosNecesarios ?e Mineral))
            (decrease (cantidadRecurso Gas) (recursosNecesarios ?e Gas))

            (increase
                (costeTiempo)
                (tiempoPorEntidad ?e)
            )
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

            (imply (esTipoEdificio ?e CentroDeMando)
                (esTipoUnidad ?u VCE)
            )

            (imply (esTipoEdificio ?e Barracones)
                (or
                    (esTipoUnidad ?u Marine)
                    (esTipoUnidad ?u Soldado)
                )
            )

            (imply (esTipoEdificio ?e Extractor)
                (and
                    (not (esTipoUnidad ?u VCE))
                    (not (esTipoUnidad ?u Soldado))
                    (not (esTipoUnidad ?u Marine))
                )
            )

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
            (en ?u ?x)
            (libre ?u)

            (decrease (cantidadRecurso Mineral) (recursosNecesarios ?u Mineral))
            (decrease (cantidadRecurso Gas) (recursosNecesarios ?u Gas))

            (increase
                (costeTiempo)
                (tiempoPorEntidad ?u)
            )
        )
    )

    (:action Recolectar
        :parameters (?x - localizacion ?r - recurso)
        :precondition (and
            (exists (?u - unidad)
                (and
                    (esTipoUnidad ?u VCE)
                    (en ?u ?x)
                )
            )

            (asignarNodo ?r ?x)
            (extrayendo ?r)

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
            (increase
                (cantidadRecurso ?r)
                (*
                    (unidadesExtrayendo ?r)
                    (cantidadRecolectada)
                )
            )

            (increase 
                (costeTiempo)
                (tiempoRecolectar)
            )
        )
    )
    
)