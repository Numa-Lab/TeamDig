package net.numalab.teamdig.config

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.BooleanValue
import org.bukkit.plugin.Plugin

class MainConfig(plugin: Plugin) : BaseConfig(plugin) {
    val isEnabled = BooleanValue(false)
    init {
        saveConfigIfAbsent()
        loadConfig()
    }
}