package net.numalab.teamdig

import dev.kotx.flylib.flyLib
import net.kunmc.lab.configlib.ConfigCommandBuilder
import net.numalab.teamdig.command.FallCommand
import net.numalab.teamdig.command.MainCommand
import net.numalab.teamdig.command.PosSelectCommand
import net.numalab.teamdig.config.MainConfig
import org.bukkit.plugin.java.JavaPlugin

class Teamdig : JavaPlugin() {
    val config = MainConfig(this)
    lateinit var scoreboard: ScoreBoardOperator

    init {
        flyLib {
            command(
                MainCommand(
                    config, ConfigCommandBuilder(config).build(), PosSelectCommand(config),
                    FallCommand(config)
                )
            )
        }
    }

    override fun onEnable() {
        FallCaller.getInstance(config, this)
        ForceBlockStacker.getInstance(config, this)
        scoreboard = ScoreBoardOperator(config, this, server.getWorld(config.blockWorldName.value()))
        // Plugin startup logic
    }

    override fun onDisable() {
        // Plugin shutdown logic
        config.saveConfigIfPresent()
    }
}