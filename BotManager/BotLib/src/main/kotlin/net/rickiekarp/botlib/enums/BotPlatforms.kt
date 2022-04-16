package net.rickiekarp.botlib.enums

enum class BotPlatforms private constructor(private val type: String) : BotTypeInterface {
    NONE("none"),
    ANDROID("ANDROID"),
    WEB("WEB");

    override fun getDisplayableType(): String {
        return type
    }
}