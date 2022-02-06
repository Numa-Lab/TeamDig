package net.numalab.teamdig.stacker

import net.numalab.teamdig.config.BlockXZLocation
import net.numalab.teamdig.config.BlockXZRange
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import kotlin.math.roundToInt

abstract class BaseStacker : Stacker {
    fun spawnFallingSand(material: Material, location: Location) {
        spawnFallingSand(Bukkit.getServer().createBlockData(material),location)
    }

    fun spawnFallingSand(blockData: BlockData, location: Location) {
        location.world.spawnFallingBlock(location, blockData)
    }
}

fun BlockXZRange.allPos(): List<BlockXZLocation> {
    return (first.first..second.first).map { x ->
        (first.second..second.second).map { z ->
            Pair(x, z) as BlockXZLocation
        }
    }.flatten()
}

fun BlockXZRange.center(): BlockXZLocation {
    val centerX = ((first.first + second.first).toDouble() / 2).roundToInt()
    val centerZ = ((first.second + second.second).toDouble() / 2).roundToInt()
    return Pair(centerX, centerZ) as BlockXZLocation
}