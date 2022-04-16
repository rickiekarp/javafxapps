package net.rickiekarp.botlib.plugin

import javafx.scene.Node

class BotSetting(builder: Builder) {
    val title: String?
    val desc: String?
    var isVisible: Boolean = false
    val node: Node?

    init {
        this.title = builder.nameString
        this.desc = builder.descString
        this.isVisible = builder.isShownBoolean
        this.node = builder.nodeBuilder
    }

    class Builder {
        var nameString: String? = null
        var descString: String? = null
        var isShownBoolean: Boolean = false
        var nodeBuilder: Node? = null

        fun setName(s: String): Builder {
            nameString = s
            return this
        }

        fun setDescription(s: String): Builder {
            descString = s
            return this
        }

        fun setVisible(b: Boolean): Builder {
            isShownBoolean = b
            return this
        }

        fun setNode(n: Node): Builder {
            nodeBuilder = n
            return this
        }


        fun build(): BotSetting {
            if (this.nodeBuilder == null) {
                throw RuntimeException("NetworkAction requires a host")
            }
            return BotSetting(this)
        }

        companion object {

            fun create(): Builder {
                return Builder()
            }
        }
    }

}
