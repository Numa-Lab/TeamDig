package net.numalab.teamdig.command

import dev.kotx.flylib.command.Command

class MainCommand(name: String, vararg children: Command) : Command(name) {
    init {
        description("This is the main command of TeamDig Plugin.")
        children(*children)
    }
}