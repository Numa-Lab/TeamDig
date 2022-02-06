package net.numalab.teamdig.stacker

import net.numalab.teamdig.config.BlockXZLocation
import org.bukkit.Material
import kotlin.random.Random


/**
 * 自然地形を生成するや～つ
 * @param airRate 全体のどのぐらい空気にするか[0~1]
 */
class DefaultBlockSet(var airRate: Double) : BlockSet {
    /**
     * ノイズ生成の数値を振り分ける用
     * @param rate そのブロックにあたるレート
     */
    enum class DefaultBlockSetting(val material: Material, val rate: Double) {
        STONE(Material.STONE, 1.0),
        DIRT(Material.DIRT, 1.0),
        SAND(Material.SAND, 1.0),
        ANDESITE(Material.ANDESITE, 0.5)
        ;

        companion object {
            /**
             * @param noiseValue ノイズ生成の数値[-1~1]
             */
            fun getByNoiseValue(noiseValue: Double): DefaultBlockSetting {
                val allRate = DefaultBlockSetting.values().sumOf { it.rate }
                // [0~1]までに変換
                val compressedValue = (noiseValue + 1.0) / 2.0
                val targetValue = allRate * compressedValue
                if (!(0.0..allRate).contains(targetValue)) {
                    // なぜか範囲内に数値がいない
                    // 多分noiseValueがでかすぎ
                    throw IllegalArgumentException()
                } else {
                    var added = 0.0
                    var index = 0
                    while (added < targetValue) {
                        added += values()[index].rate   // IndexOutOfBoundsExceptionは起きないと信じてる
                        if (added >= targetValue) {
                            break
                        }
                        ++index
                    }
                    return values()[index]
                }
            }
        }
    }


    override fun generate(vararg locations: BlockXZLocation): List<Material> {
        return locations.toMutableList().mapWithRate(airRate, { Material.AIR }, { getAt(it).material })
    }

    private fun <T, E> List<T>.mapWithRate(rate: Double, contained: (T) -> E, notContained: (T) -> E): List<E> {
        val filtered = this.filterWithRate(rate)
        return this.toMutableList().map {
            if (filtered.contains(it)) {
                contained(it)
            } else {
                notContained(it)
            }
        }
    }

    /**
     * @return rateの割合で中身を返す
     */
    private fun <T> List<T>.filterWithRate(rate: Double): List<T> {
        if (!(0.0..1.0).contains(rate)) {
            throw IllegalArgumentException("Rate:${rate}is not valid.")
        }
        val returnSize = (this.size * rate).toInt() //切り捨て
        return this.toMutableList().shuffled().drop(this.size - returnSize)
    }

    private fun getAt(location: BlockXZLocation): DefaultBlockSetting {
        return DefaultBlockSetting.getByNoiseValue(
            Random.nextDouble(-1.0, 1.0)
        )
    }
}