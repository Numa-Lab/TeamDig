package net.numalab.teamdig.command

import dev.kotx.flylib.command.Command
import net.numalab.teamdig.config.MainConfig
import net.numalab.teamdig.stacker.DefaultBlockSet
import net.numalab.teamdig.stacker.FilledBlockSet
import net.numalab.teamdig.stacker.SquareStacker
import org.bukkit.Bukkit
import org.bukkit.Material

class FallCommand(config: MainConfig) : Command("fall") {
    init {
        description("This is a fall command of TeamDig")
        usage {
            stringArgument("TeamName")
            integerArgument("段数")
            doubleArgument("空気の割合(%)")

            executes {
                val teamName = typedArgs[0] as String
                val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
                if (team == null) {
                    fail("チーム名:${teamName}のチームが見つかりませんでした")
                    return@executes
                }

                val teamConfig = config.getPosSet().firstOrNull { it.first == teamName }
                if (teamConfig == null) {
                    fail("チーム名:${teamName}のコンフィグが見つかりませんでした")
                    return@executes
                }

                val stackHeight = typedArgs[1] as Int
                if (stackHeight < 1) {
                    fail("段数が不正です")
                    return@executes
                }
                val airRate = typedArgs[2] as Double
                if (!(0.0..100.0).contains(airRate)) {
                    fail("空気の割合が不正です")
                    return@executes
                }

                val stacker = SquareStacker()
                val blockSet = DefaultBlockSet(airRate / 100.0000)
                val world = this.world
                if (world == null) {
                    fail("ワールド取得に失敗しました")
                    return@executes
                }

                stacker.stack(world, teamConfig.second.first, teamConfig.second.second, blockSet, stackHeight, 200)

                success("召喚に成功しました")
            }
        }
    }
}