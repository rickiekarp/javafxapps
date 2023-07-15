package net.rickiekarp.core.extensions

fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this)
        .apply {
            if (index > this.length) {
                insert(0, char)
            } else {
                insert(index, char)
            }
        }
        .toString()

fun String.removeCharAtIndex(index: Int) =
    StringBuilder(this)
        .apply {
            if (index > this.length) {
                if (isNotEmpty()) {
                    deleteCharAt(0)
                }
            } else {
                deleteCharAt(index)
            }
        }
        .toString()