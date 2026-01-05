package com.ft.architectcoders.domain.model


object TmdbGenres {
    const val ACTION = 28
    const val ADVENTURE = 12
    const val ANIMATION = 16
    const val COMEDY = 35
    const val CRIME = 80
    const val DOCUMENTARY = 99
    const val DRAMA = 18
    const val FAMILY = 10751
    const val FANTASY = 14
    const val HISTORY = 36
    const val HORROR = 27
    const val MUSIC = 10402
    const val MYSTERY = 9648
    const val ROMANCE = 10749
    const val SCIFI = 878
    const val THRILLER = 53
    const val WAR = 10752
    const val WESTERN = 37

    val ID_TO_NAME: Map<Int, String> = mapOf(
        ACTION to "Acción",
        ADVENTURE to "Aventura",
        ANIMATION to "Animación",
        COMEDY to "Comedia",
        CRIME to "Crimen",
        DOCUMENTARY to "Documental",
        DRAMA to "Drama",
        FAMILY to "Familia",
        FANTASY to "Fantasía",
        HISTORY to "Historia",
        HORROR to "Terror",
        MUSIC to "Música",
        MYSTERY to "Misterio",
        ROMANCE to "Romance",
        SCIFI to "Ciencia Ficción",
        THRILLER to "Thriller",
        WAR to "Guerra",
        WESTERN to "Western",
    )

    fun nameForId(id: Int): String? = ID_TO_NAME[id]

    fun idForName(name: String): Int? = ID_TO_NAME.entries
        .find { it.value.equals(name, ignoreCase = true) }?.key
}

