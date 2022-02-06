package net.numalab.teamdig.config

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.*
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.Plugin


typealias BlockXZLocation = Pair<Int, Int>
typealias BlockXZRange = Pair<BlockXZLocation, BlockXZLocation>


class MainConfig(plugin: Plugin) : BaseConfig(plugin) {
    val isEnabled = BooleanValue(false)

    // ダメージを受けたときの段数
    val damageStackHeight = IntegerValue(1)

    // 死んだときの段数
    val deadStackHeight = IntegerValue(3)

    // ダメージを受けたときのブロックの割合の計算定数
    // (この定数 * 実際のダメージ量)[%]がブロックの割合になる
    val damageBlockRateConst = DoubleValue(5.0)

    // 死んだときのブロックの割合の定数[%]
    val deadBlockRate = DoubleValue(100.0)

    // One String will be like this: <TeamName>,111:255,112:256
    private val selectedPosSet = StringListValue()
    fun setPosSet(vararg poses: Pair<String, BlockXZRange>) {
        selectedPosSet.value(poses.map {
            "${it.first},${it.second.first.first}:${it.second.first.second},${it.second.second.first}:${it.second.second.second}"
        })
    }

    /**
     * @return Pair(teamName,BlockXZRange)
     */
    fun getPosSet(): List<Pair<String, BlockXZRange>> {
        return selectedPosSet.mapNotNull {
            val posString = it.split(",")
            if (posString.size != 3) {
                println("Failed to Parse Config File,String:${it}")
                return@mapNotNull null
            }
            val teamName = posString[0]
            val firstLocation = parsePos(posString[1])
            val secondLocation = parsePos(posString[2])
            if (firstLocation != null && secondLocation != null) {
                return@mapNotNull Pair(teamName, Pair(firstLocation, secondLocation))
            } else {
                println("Failed to Parse Config File,while parsing pos")
                return@mapNotNull null
            }
        }
    }

    private fun parsePos(str: String): BlockXZLocation? {
        val locString = str.split(":")
        if (locString.size != 2) return null
        else {
            val x = locString[0].toIntOrNull()
            val z = locString[1].toIntOrNull()

            if (x == null || z == null) return null
            return Pair(x, z)
        }
    }

    init {
        saveConfigIfAbsent()
        loadConfig()
    }
}