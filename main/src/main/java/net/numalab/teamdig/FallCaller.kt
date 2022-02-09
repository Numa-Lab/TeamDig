package net.numalab.teamdig

import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import net.numalab.teamdig.stacker.DefaultBlockSet
import net.numalab.teamdig.stacker.OptimizedSquareStacker
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import kotlin.math.max
import kotlin.math.min

/**
 * 誰かが死んだら/ダメージを受けたらfall関数を呼ぶ
 */
class FallCaller(val config: MainConfig, plugin: Teamdig) : Listener {
    companion object {
        var instance: FallCaller? = null
        fun getInstance(config: MainConfig, plugin: Teamdig): FallCaller {
            return if (instance != null) {
                instance!!
            } else {
                FallCaller(config, plugin)
            }
        }
    }

    init {
        if (instance != null) {
            throw IllegalStateException("FallCaller already have instance")
        }
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val stacker = OptimizedSquareStacker(config,plugin)
    private val blockSet = DefaultBlockSet(0.0)

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity is Player) {
            onDamage(e.entity as Player, e.finalDamage)
        }
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        onDeath(e.entity)
    }

    private fun getPlayerTeamConfig(player: Player): Pair<String, BlockXZRange>? {
        val team = Bukkit.getScoreboardManager().mainScoreboard.getEntryTeam(player.name)
            ?: // Player is not in any team
            return null
        return config.getPosSet().firstOrNull { it.first == team.name }
    }

    private fun onDamage(player: Player, amount: Double) {
        val teamRange = getPlayerTeamConfig(player) ?: return
        doFall(
            teamRange.second,
            config.damageBlockRateConst.value() * amount / 100.0,
            config.damageStackHeight.value(),
            player.world
        )
    }

    private fun onDeath(player: Player) {
        val teamRange = getPlayerTeamConfig(player) ?: return
        doFall(teamRange.second, config.deadBlockRate.value() / 100.0, config.deadStackHeight.value(), player.world)
    }

    /**
     * @param blockRate[%]
     */
    private fun doFall(range: BlockXZRange, blockRate: Double, stackHeight: Int, world: World) {
        if (config.isEnabled.value()) {
            blockSet.airRate = min(1.0, max(1 - blockRate, 0.0))
            stacker.stack(world, range.first, range.second, blockSet, stackHeight)
        }
    }
}