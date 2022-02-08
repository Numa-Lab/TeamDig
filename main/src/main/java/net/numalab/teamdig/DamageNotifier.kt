package net.numalab.teamdig

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title.title
import net.numalab.teamdig.config.BlockXZRange
import net.numalab.teamdig.config.MainConfig
import net.numalab.teamdig.config.MainConfig.Type.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.scoreboard.Team
import kotlin.math.roundToInt

/**
 * ダメージ発生時にお知らせする機能
 */
class DamageNotifier(val config: MainConfig, private val plugin: Teamdig) : Listener {
    private var isRegistered = false

    fun onEnable() {
        if (isRegistered) return
        plugin.server.pluginManager.registerEvents(this, plugin)
        isRegistered = true
    }

    fun onDisable() {
        isRegistered = false
    }

    private fun getPlayerTeamConfig(player: Player): Pair<Team, BlockXZRange>? {
        val team = Bukkit.getScoreboardManager().mainScoreboard.getEntryTeam(player.name)
            ?: // Player is not in any team
            return null
        val conf = config.getPosSet().firstOrNull { it.first == team.name }
        if (conf == null) return null   // Config is not found.
        else return Pair(team, conf.second)
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity is Player) {
            val config = getPlayerTeamConfig(e.entity as Player) ?: return
            // チーム内にいる　→　ちゃんと降ってくる
            notifyDamage(e.entity as Player, e.finalDamage, config.first)
        }
    }

    private fun notifyDamage(entity: Player, amount: Double, team: Team) {
        if (amount <= 0.0) return
        if (!config.isEnabled.value()) return
        val component = notifyDamageComponent(entity, (amount * 10.0).roundToInt() / 10.0, team)
        when (config.damageLoggingType.value()) {
            CHAT -> {
                Bukkit.broadcast(component)
            }
            SUBTITLE -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.showTitle(title(Component.empty(), component))
                }
            }
            null -> {
                println("[DamageNotifier]Something happened while parsing config.")
                println("[DamageNotifier]This is may because of the config lib.")
            }
        }
    }

    private fun notifyDamageComponent(entity: Player, amount: Double, team: Team): Component {
        try {
            val teamColor = team.color()
            return entity.displayName().color(teamColor)
                .append(Component.text("が${amount}ダメージ受けた").color(NamedTextColor.WHITE))
        } catch (e: IllegalStateException) {
            // 握りつぶします
            // 多分色が登録されてない時になる
        }

        return entity.displayName().color(NamedTextColor.WHITE).append(Component.text("が${amount}ダメージ受けた"))
    }
}