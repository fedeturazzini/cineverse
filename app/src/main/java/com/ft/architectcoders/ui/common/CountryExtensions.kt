package com.ft.architectcoders.ui.common

fun String.toFlagEmoji(): String {
    if (this.length != 2) return ""

    val firstChar = Character.codePointAt(this, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(this, 1) - 0x41 + 0x1F1E6

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}
