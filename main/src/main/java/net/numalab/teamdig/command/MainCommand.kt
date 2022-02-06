package net.numalab.teamdig.command

import dev.kotx.flylib.command.Command
import net.numalab.teamdig.config.MainConfig

class MainCommand(mainConfig: MainConfig, vararg children: Command) : Command("teamdig") {
    init {
        description("This is the main command of TeamDig Plugin.")
        children(*children)
        usage {
            selectionArgument("ON/OFF", listOf("ON", "OFF"))
            executes {
                val bool = when (this.typedArgs[0] as String) {
                    "ON" -> true
                    "OFF" -> false
                    else -> false
                }

                mainConfig.isEnabled.value(bool)
                success(
                    "${
                        if (bool) {
                            "ON"
                        } else {
                            "OFF"
                        }
                    }に変更しました"
                )
            }
        }
    }
}