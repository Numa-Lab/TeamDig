package net.numalab.teamdig.stacker

import net.numalab.teamdig.Teamdig
import net.numalab.teamdig.config.BlockXZLocation
import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import java.lang.Integer.max

/**
 * 軽量化措置済みのスタッカー
 */
class OptimizedSquareStacker(
    val config: MainConfig,
    val plugin: Teamdig,
    private val optimizer: Optimizer = Optimizer.getInstance(plugin, config)
) : BaseStacker() {
    override fun stack(
        world: World,
        startLocation: BlockXZLocation,
        endLocation: BlockXZLocation,
        blockSet: BlockSet,
        stackHeight: Int,
        startHeight: Int
    ) {
        val range = Pair(startLocation, endLocation) as BlockXZRange

        (startHeight..startHeight + (stackHeight - 1)).forEach { height ->
            val allPos = range.allPos()
            val materials = blockSet.generate(range)
            allPos.forEachIndexed { index, location ->
                spawn(materials[index], location, world, height)
            }
        }
    }

    private fun spawn(material: Material, loc: BlockXZLocation, world: World, height: Int) {
        if (material == Material.AIR) {
            return
        }

        if (optimizer.isAccepted(loc)) {
            spawnFallingSand(
                material,
                Location(world, loc.first.toDouble() + 0.5, height.toDouble(), loc.second.toDouble() + 0.5)
            )
            optimizer.spawnedAt(loc)
        } else {
            directSetBlockAddTimer(material, loc, world, config.optimizeWaitTime.value())
        }
    }

    init {
        plugin.server.scheduler.runTaskTimer(plugin, Runnable { tick() }, 1, 1)
    }

    private val queue = mutableMapOf<Int, MutableList<Triple<Material, World, BlockXZLocation>>>()

    /**
     * キューにためとく
     */
    private fun directSetBlockAddTimer(material: Material, loc: BlockXZLocation, world: World, waitTime: Int) {
        val targetTime = plugin.server.currentTick + waitTime
        val el = queue.getOrDefault(targetTime, mutableListOf())
        el.add(Triple(material, world, loc))
        queue[targetTime] = el
    }

    private fun tick() {
        val el = queue[plugin.server.currentTick]
        if (el != null) {
            el.forEach {
                directSetBlock(it.first, it.third, it.second)
            }
            queue.remove(plugin.server.currentTick)
        }
    }

    private fun directSetBlock(material: Material, loc: BlockXZLocation, world: World) {
        val highest = world.getHighestBlockAt(loc.first, loc.second)
        val targetY = highest.location.blockY + 1
        if ((1..255).contains(targetY)) {
            world.getBlockAt(loc.first, targetY, loc.second).type = material
        } else {
            // 設置できない所に設置しないといけない
            // 握りつぶします
        }
    }

    /**
     * 同一Y軸上に何個今あるか管理するクラス
     */
    class Optimizer(val plugin: Teamdig, val config: MainConfig) : Listener {
        companion object {
            private var instance: Optimizer? = null
            fun getInstance(plugin: Teamdig, config: MainConfig): Optimizer {
                return if (instance != null) {
                    instance!!
                } else {
                    instance = Optimizer(plugin, config)
                    instance!!
                }
            }
        }

        init {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }

        private val internalMap = mutableMapOf<BlockXZLocation, Int>()

        /**
         * @return あるY軸にここからスポーンしていいか
         */
        fun isAccepted(loc: BlockXZLocation): Boolean {
            val en = internalMap[loc]
            return if (en == null) {
                true
            } else {
                config.optimizeStartStackHeight.value() > en
            }
        }

        /**
         * Y軸上にスポーンしたときの関数
         */
        fun spawnedAt(loc: BlockXZLocation) {
            val en = internalMap[loc]
            if (en == null) {
                internalMap[loc] = 1
            } else {
                internalMap[loc] = en + 1
            }
        }

        @EventHandler
        fun onEntityChangeBlockEvent(e: EntityChangeBlockEvent) {
            val loc = BlockXZLocation(e.block.location.blockX, e.block.location.blockZ)
            val en = internalMap[loc]
            if (en == null) {
                // なんでかしらんけどこうなったんやな。
            } else {
                internalMap[loc] = max(0, en - 1)
            }
        }
    }
}