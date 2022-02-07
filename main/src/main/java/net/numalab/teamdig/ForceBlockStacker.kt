package net.numalab.teamdig

import net.numalab.teamdig.config.BlockXZLocation
import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent

/**
 * 松明対策
 */
class ForceBlockStacker(val config: MainConfig, plugin: Teamdig) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    companion object {
        var instance: ForceBlockStacker? = null
        fun getInstance(config: MainConfig, plugin: Teamdig): ForceBlockStacker {
            return if (instance != null) {
                instance!!
            } else {
                ForceBlockStacker(config, plugin)
            }
        }
    }

    @EventHandler
    fun onDrop(e: EntityDropItemEvent) {
        if (e.entityType == EntityType.FALLING_BLOCK) {
            if (config.getPosSet().any { it.second.contains(e.entity.location) }) {
                e.isCancelled = true
                e.entity.location.block.location.clone().add(.0, 1.0, .0).block.type =
                    (e.entity as FallingBlock).blockData.material
                e.entity.remove()
            }
        }

    }

    private fun BlockXZRange.contains(location: BlockXZLocation): Boolean {
        return (first.first..second.first).contains(location.first) && (first.second..second.second).contains(location.second)
    }

    private fun BlockXZRange.contains(location: Location): Boolean =
        this.contains(BlockXZLocation(location.blockX, location.blockZ))
}
