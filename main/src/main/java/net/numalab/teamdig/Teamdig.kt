package net.numalab.teamdig

import dev.kotx.flylib.flyLib
import net.kunmc.lab.configlib.ConfigCommandBuilder
import net.numalab.teamdig.command.MainCommand
import net.numalab.teamdig.config.MainConfig
import org.bukkit.plugin.java.JavaPlugin

class Teamdig : JavaPlugin() {
    val config = MainConfig(this)

    init {
        flyLib {
            command(MainCommand("teamdig", ConfigCommandBuilder(config).build()))
        }
    }

    override fun onEnable() {
        // Plugin startup logic
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}