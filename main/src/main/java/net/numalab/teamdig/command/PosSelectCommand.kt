package net.numalab.teamdig.command

import dev.kotx.flylib.command.Command
import net.numalab.teamdig.config.BlockXZLocation
import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import org.bukkit.Bukkit
import org.bukkit.Location


class PosSelectCommand(config: MainConfig) : Command("selectPos") {
    init {
        description("This is a pos select command of TeamDig")
        usage {
            stringArgument("TeamName")
            locationArgument("Pos1")
            locationArgument("Pos2")

            executes {
                val teamName = typedArgs[0] as String
                val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
                if (team == null) {
                    fail("チーム名:${teamName}に該当するチームが見つかりませんでした")
                    return@executes
                }

                val firstLoc = typedArgs[1] as Location
                val secondLoc = typedArgs[2] as Location

                val firstXZ = Pair(firstLoc.x.toInt(), firstLoc.z.toInt()) as BlockXZLocation
                val secondXZ = Pair(secondLoc.x.toInt(), secondLoc.z.toInt()) as BlockXZLocation

                val XZRange = Pair(firstXZ, secondXZ) as BlockXZRange

                val existing = config.getPosSet().toMutableSet()
                existing.removeAll {
                    // すでにある物はremove
                    it.first == teamName
                }
                val result = existing.toMutableList().also { it.add(Pair(teamName, XZRange)) }

                config.setPosSet(*result.toTypedArray())

                success("チーム:${teamName},Pos1:${firstLoc},Pos2:${secondLoc}で登録しました")
            }
        }
    }
}