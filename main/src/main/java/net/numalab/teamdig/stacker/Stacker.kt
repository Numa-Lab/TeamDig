package net.numalab.teamdig.stacker

import net.numalab.teamdig.config.BlockXZLocation
import org.bukkit.Material
import org.bukkit.World

/**
 * 上から降らせるやつのインタフェース
 */
interface Stacker {
    /**
     * @param stackHeight 何段積み上げるか
     */
    fun stack(
        world: World,
        startLocation: BlockXZLocation,
        endLocation: BlockXZLocation,
        blockSet: BlockSet,
        stackHeight: Int,
        startHeight: Int = 256
    )
}

/**
 * 設定された確率に応じてマテリアルのリスト渡すやつ
 */
interface BlockSet {
    fun generate(size: Int): List<Material>
}