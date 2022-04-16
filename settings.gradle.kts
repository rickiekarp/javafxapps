rootProject.name = "JavaFXApps"

include("AppUpdater")
include("BotManager")
include("BotLib")
include("ColorPuzzle")
include("FilelistCreator")
include("QAAccountManager")
include("SHA1Pass")
include("SnakeFX")
include("core")

for (project in rootProject.children) {
    when (project.name) {
        "BotLib" -> {
            project.apply {
                projectDir = file("BotManager/$name")
            }
        }
    }
}