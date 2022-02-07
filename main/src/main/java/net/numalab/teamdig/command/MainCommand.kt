package net.numalab.teamdig.command

import dev.kotx.flylib.command.Command
import net.numalab.teamdig.ScoreBoardOperator
import net.numalab.teamdig.config.MainConfig
import org.bukkit.command.BlockCommandSender
import org.bukkit.entity.Player

class MainCommand(mainConfig: MainConfig, private val score: ScoreBoardOperator,vararg children: Command) :
    Command("teamdig") {
    init {
        description("This is the main command of TeamDig Plugin.")
        children(*children)
        usage {
            selectionArgument("ON/OFF", listOf("ON", "OFF"))
            executes {
                if (sender !is Player) {
                    fail("プレイヤーが実行してください")
                }

                val bool = when (this.typedArgs[0] as String) {
                    "ON" -> true
                    "OFF" -> false
                    else -> false
                }

                mainConfig.isEnabled.value(bool)

                if (bool) {
                    // Trueになった
                    score.initialize(world)
                }

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