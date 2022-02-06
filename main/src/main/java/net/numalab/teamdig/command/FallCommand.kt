package net.numalab.teamdig.command

import dev.kotx.flylib.command.Command
import net.numalab.teamdig.config.MainConfig
import net.numalab.teamdig.stacker.FilledBlockSet
import net.numalab.teamdig.stacker.SquareStacker
import org.bukkit.Bukkit
import org.bukkit.Material

class FallCommand(config: MainConfig) : Command("fall") {
    init {
        description("This is a fall command of TeamDig")
        usage {
            stringArgument("TeamName")
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
                val stacker = SquareStacker()
                val blockSet = FilledBlockSet(Material.STONE)
                val world = this.world
                if (world == null) {
                    fail("ワールド取得に失敗しました")
                    return@executes
                }

                stacker.stack(world, teamConfig.second.first, teamConfig.second.second, blockSet, 1, 200)

                success("召喚に成功しました")
            }
        }
    }
}