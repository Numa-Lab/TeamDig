package net.numalab.teamdig

import dev.kotx.flylib.command.CommandContext
import net.kunmc.lab.configlib.value.BooleanValue
import net.kyori.adventure.text.Component
import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import net.numalab.teamdig.stacker.allPos
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import java.util.function.BiFunction

/**
 * ブロック数を取得してスコアボードに反映する用のクラス
 */
class ScoreBoardOperator(val config: MainConfig, val plugin: Teamdig, var world: World?) {
    private var task: BukkitTask? = null

    init {
        config.isEnabled.onModify<BooleanValue>(BiFunction { t: Boolean, u: CommandContext ->
            if (t) {
                initialize(u.world)
            }
            return@BiFunction false
        })
    }

    fun initialize(world: World?) {
        getObjective().unregister()
        getObjective().displaySlot = DisplaySlot.SIDEBAR
        if (world != null) {
            this.world = world
            config.blockWorldName.value(world.name)
        }
    }

    fun enabled() {
        if (task != null) return
        task = plugin.server.scheduler.runTaskTimer(
            plugin,
            Runnable {
                if (config.isEnabled.value()) {
                    update()
                }
            },
            1,
            20
        )
    }

    fun dispose() {
        this.task?.cancel()
        this.task = null
    }

    private fun getObjective(): Objective {
        return Bukkit.getScoreboardManager().mainScoreboard.getObjective("teamdig-display")
            ?: Bukkit.getScoreboardManager().mainScoreboard.registerNewObjective(
                "teamdig-display", "dummy",
                Component.text("残りのブロック数")
            )
    }

    private fun update() {
        val objective = getObjective()
        config.getPosSet().forEach {
            update(objective, it.first, it.second)
        }
    }

    private fun update(objective: Objective, teamName: String, range: BlockXZRange) {
        objective.getScore(teamName).score = countBlocks(range, world)
    }

    private fun countBlocks(range: BlockXZRange, world: World?): Int {
        if (world == null) return -1
        var counter = 0
        range.allPos().forEach {
            (1..255).forEach { y ->
                val x = it.first
                val z = it.second
                val block = world.getBlockAt(x, y, z)
                if (!(block.type.isAir || block.type == Material.BEDROCK)) {
                    ++counter
                }
            }
        }

        return counter
    }
}