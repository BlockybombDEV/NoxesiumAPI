package me.iris.noxesiumapi

import com.noxcrew.noxesium.api.qib.QibDefinition
import com.noxcrew.noxesium.paper.api.EntityRuleManager
import com.noxcrew.noxesium.paper.api.NoxesiumManager
import com.noxcrew.noxesium.paper.api.network.NoxesiumPackets
import com.noxcrew.noxesium.paper.api.rule.ServerRules as NoxesiumServerRules
import com.noxcrew.noxesium.paper.api.rule.EntityRules
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import fr.skytasul.glowingentities.GlowingBlocks
import fr.skytasul.glowingentities.GlowingEntities
import me.iris.noxesiumapi.commands.CreativeItemsCommand
import me.iris.noxesiumapi.commands.ServerRulesCommand
import me.iris.noxesiumapi.commands.Sound
import me.iris.noxesiumapi.event.NoxesiumPlayerRiptideEvent
import me.iris.noxesiumapi.event.NoxesiumQibTriggeredEvent
import me.iris.noxesiumapi.packets.OpenLinkPacket
import me.iris.noxesiumapi.serverrules.CreativeItemsManager
import me.iris.noxesiumapi.packets.SoundManager
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
class NoxesiumAPI : JavaPlugin() {

    companion object {
        val qibDefinitions: MutableMap<String, QibDefinition> = mutableMapOf()
        var Logger: Logger = LoggerFactory.getLogger("NoxesiumAPI")
        lateinit var instance: NoxesiumAPI
            private set
        lateinit var noxesiumManager: NoxesiumManager
            private set
        lateinit var entityRuleManager: EntityRuleManager
            private set
        lateinit var openLinkPacket: OpenLinkPacket
            private set
        lateinit var soundManager: SoundManager
            private set
        lateinit var creativeItemsManager: CreativeItemsManager
            private set
        lateinit var glowingEntities: GlowingEntities
            private set
        lateinit var glowingBlocks: GlowingBlocks
            private set

    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).setNamespace("noxesiumapi"))
    }

    override fun onEnable() {
        instance = this
        CommandAPI.onEnable()

        // Enable glowing APIs
        glowingEntities = GlowingEntities(this)
        glowingBlocks = GlowingBlocks(this)

        // Register all managers
        noxesiumManager = me.iris.noxesiumapi.NoxesiumManager(this, Logger)
        noxesiumManager.register()
        entityRuleManager = EntityRuleManager(noxesiumManager)
        entityRuleManager.register()
        soundManager = SoundManager(noxesiumManager)
        creativeItemsManager = CreativeItemsManager()

        // Registers all rules
        NoxesiumServerRules(noxesiumManager)
        EntityRules(noxesiumManager)

//        if (Bukkit.getPluginManager().getPlugin("CommandAPI")?.isEnabled == true) {
//            Logger.info("CommandAPI found! Attempting to load commands.")
//            registerCommands()
//        } else {
//            Logger.warn("Could not find CommandAPI! Commands will not be loaded.")
//        }

        NoxesiumPackets.SERVER_QIB_TRIGGERED.addListener(noxesiumManager) { packet, player ->
            NoxesiumQibTriggeredEvent(player, packet.behavior, packet.qibType, packet.entityId).callEvent()
        }

        NoxesiumPackets.SERVER_RIPTIDE.addListener(noxesiumManager) { packet, player ->
            NoxesiumPlayerRiptideEvent(player, packet.slot).callEvent()
        }

        Logger.info("NoxesiumAPI has been enabled!")
    }

    fun getInstance(): NoxesiumAPI {
        return instance
    }

    fun getEntityGlow(): GlowingEntities {
        return glowingEntities
    }

    fun getBlockGlow(): GlowingBlocks {
        return glowingBlocks
    }

    fun getManager(): NoxesiumManager {
        return noxesiumManager
    }

    fun getSoundManager(): SoundManager {
        return soundManager
    }

    fun getCreativeItemsManager(): CreativeItemsManager {
        return creativeItemsManager
    }

    override fun onDisable() {
        noxesiumManager.unregister()
        entityRuleManager.unregister()
        Logger.info("NoxesiumAPI has been disabled!")
    }

    private fun registerCommands() {
        ServerRulesCommand().registerCommands()
        Sound().registerCommands()
        CreativeItemsCommand().registerCommands()
        val rules = subcommand("serverrules") {
            for (command in ServerRulesCommand.RuleCommands) {
                subcommand(command)
            }
        }
        val sound = subcommand("sound") {
            for (command in Sound.SoundCommands) {
                subcommand(command)
            }
        }
        val creativeItems = subcommand("creativeItems") {
            for (command in CreativeItemsCommand.creativeItemsCommands) {
                subcommand(command)
            }
        }
        commandAPICommand("noxesiumapi", "noxesiumapi") {
            withPermission(CommandPermission.OP)
            subcommand(rules)
            subcommand(sound)
            subcommand(creativeItems)
        }
        Logger.info("/noxesiumapi command loaded!")
    }

}
