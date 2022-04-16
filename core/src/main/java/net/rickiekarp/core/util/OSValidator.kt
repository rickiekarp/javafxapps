package net.rickiekarp.core.util

object OSValidator {

    val os: OperatingSystem?
        get() {
            val OS = System.getProperty("os.name").toLowerCase()
            if (isWindows(OS)) {
                return OperatingSystem.WINDOWS
            } else if (isMac(OS)) {
                return OperatingSystem.MAC
            } else if (isUnix(OS)) {
                return OperatingSystem.UNIX
            } else if (isSolaris(OS)) {
                return OperatingSystem.SOLARIS
            } else {
                println("Could not detect operating system!")
                return null
            }
        }

    enum class OperatingSystem {
        WINDOWS,
        MAC,
        UNIX,
        SOLARIS
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val os = os
        if (os != null) {
            when (os) {
                OSValidator.OperatingSystem.WINDOWS -> println("This is Windows")
                OSValidator.OperatingSystem.MAC -> println("This is Mac")
                OSValidator.OperatingSystem.UNIX -> println("This is Unix or Linux")
                OSValidator.OperatingSystem.SOLARIS -> println("This is Solaris")
            }
        }
    }

    private fun isWindows(os: String): Boolean {
        return os.contains("win")
    }

    private fun isMac(os: String): Boolean {
        return os.contains("mac")
    }

    private fun isUnix(os: String): Boolean {
        return os.contains("nix") || os.contains("nux") || os.indexOf("aix") > 0
    }

    private fun isSolaris(os: String): Boolean {
        return os.contains("sunos")
    }

}