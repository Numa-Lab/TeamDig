package net.numalab.teamdig

import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import net.numalab.teamdig.stacker.FilledBlockSet
import net.numalab.teamdig.stacker.SquareStacker
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

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

    private val stacker = SquareStacker()
    private val blockSet = FilledBlockSet(Material.STONE)

    @EventHandler
    fun onDamage(e: EntityDamageEvent){
        if (e.entity is Player) {
            onDamage(e.entity as Player, e.damage)
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
        doFall(teamRange.second, player.world)
    }

    private fun onDeath(player: Player) {
        val teamRange = getPlayerTeamConfig(player) ?: return
        doFall(teamRange.second, player.world)
    }

    private fun doFall(range: BlockXZRange, world: World) {
        if (config.isEnabled.value()) {
            stacker.stack(world, range.first, range.second, blockSet, 1, 256)
        }
    }
}