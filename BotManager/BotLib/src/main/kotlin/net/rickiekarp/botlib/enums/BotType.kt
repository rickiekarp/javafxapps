package net.rickiekarp.botlib.enums

class BotType {

    enum class Bot private constructor(val label: String, val botPlatform: BotPlatforms) : BotTypeInterface {

        NONE("none", BotPlatforms.NONE),
        CHROME("chrome", BotPlatforms.WEB),
        FIREFOX("firefox", BotPlatforms.WEB),
        ANDROID("android", BotPlatforms.ANDROID);


        override fun getDisplayableType(): String {
            return botPlatform.getDisplayableType()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            println("All bot types")
            for (type in BotPlatforms.values()) {
                displayType(type)
                println()
            }
            println("All bot platforms")
            for (bot in Bot.values()) {
                displayBot(bot)
                println()
            }
        }

        private fun displayBot(bot: Bot) {
            displayType(bot)
            print(" - ")
            print(bot.label)
        }

        private fun displayType(displayable: BotTypeInterface) {
            print(displayable.getDisplayableType())
        }
    }
}
