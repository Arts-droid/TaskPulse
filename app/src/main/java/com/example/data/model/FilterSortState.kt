package com.example.data.model

enum class ProcessFilter(val label: String) {
    ALL("All Tasks"),
    BACKGROUND("Background"),
    USER_ONLY("User Apps"),
    SYSTEM_ONLY("System"),
    WHITELISTED("Protected"),
    HIGH_RAM("High RAM")
}

enum class ProcessSort(val label: String) {
    RAM_DESC("RAM: High to Low"),
    RAM_ASC("RAM: Low to High"),
    NAME_ASC("Name: A to Z"),
    IMPORTANCE("Importance")
}
