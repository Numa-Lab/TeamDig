package net.numalab.teamdig.stacker

import net.numalab.teamdig.config.BlockXZLocation
import net.numalab.teamdig.config.BlockXZRange
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World

/**
 * 四角形の範囲で降らせるやつ
 * rangeが一辺の**半分の長さ**になるよ
 */
class SquareStacker : BaseStacker() {
    override fun stack(
        world: World,
        startLocation: BlockXZLocation,
        endLocation: BlockXZLocation,
        blockSet: BlockSet,
        stackHeight: Int,
        startHeight: Int
    ) {
        val range = Pair(startLocation, endLocation) as BlockXZRange

        (startHeight..startHeight+(stackHeight - 1)).forEach { height ->
            val allPos =
                range.allPos()
                    .map { Location(world, it.first.toDouble() + 0.5, height.toDouble(), it.second.toDouble() + 0.5) }
            val materials = blockSet.generate(range)

            allPos.forEachIndexed { index, location ->
                spawnFallingSand(materials[index], location)
            }
        }
    }
}

/**
 * 一面敷き詰めるやつのインタフェース
 */
class FilledBlockSet(private val material: Material) : BlockSet {
    override fun generate(vararg locations: BlockXZLocation): List<Material> {
        return List(locations.size) { material }
    }
}