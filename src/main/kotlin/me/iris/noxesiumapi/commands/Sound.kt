package me.iris.noxesiumapi.commands

import com.noxcrew.noxesium.api.protocol.NoxesiumFeature
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import org.bukkit.NamespacedKey
import dev.jorel.commandapi.kotlindsl.*
import me.iris.noxesiumapi.NoxesiumAPI
import me.iris.noxesiumapi.NoxesiumAPI.Companion.Logger
import me.iris.noxesiumapi.NoxesiumAPI.Companion.noxesiumManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.tryBySeparator
import net.minecraft.sounds.SoundSource
import org.bukkit.entity.Player
import org.joml.Vector3f

public class Sound {

    public companion object {
        public var SoundCommands: MutableList<CommandAPICommand> = mutableListOf()
    }

    private val soundManager = NoxesiumAPI.soundManager

    private fun play(): CommandAPICommand {
        return subcommand("play") {
            entitySelectorArgumentManyPlayers("players", false, false)
            integerArgument("id")
            booleanArgument("ignore", false)
            withArguments(StringArgument("source")
                .replaceSuggestions(ArgumentSuggestions.strings(
                    "master", "music", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice")).setOptional(true))
            floatArgument("volume", optional = true)
            floatArgument("pitch", optional = true)
            playerExecutor { sender, commandArguments ->
                val players = commandArguments["players"] as Collection<Player>
                val id = commandArguments["id"] as Int
                val sourceArg: String = commandArguments["source"] as String? ?: "master"
                val ignore = commandArguments["ignore"] as Boolean
                val volume: Float = commandArguments["volume"] as Float? ?: 1F
                val pitch: Float = commandArguments["pitch"] as Float? ?: 1F
                var affected = 0
                var source: SoundSource
                source = SoundSource.valueOf(sourceArg.uppercase())
                if (soundManager.getSound(id) == null) {
                    sender.sendRichMessage("<red>No sound was registered at id <dark_red>$id")
                } else {
                    for (player in players) {
                        if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                        soundManager.playSound(player, id, source, false, false, ignore, volume, pitch, Vector3f())
                        affected++
                    }
                    sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                }
            }
        }
    }

    private fun playLoop(): CommandAPICommand {
        return subcommand("playloop") {
            entitySelectorArgumentManyPlayers("players", false, false)
            integerArgument("id")
            booleanArgument("ignore", false)
            withArguments(StringArgument("source")
                .replaceSuggestions(ArgumentSuggestions.strings(
                    "master", "music", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice")).setOptional(true))
            floatArgument("volume", optional = true)
            floatArgument("pitch", optional = true)
            playerExecutor { sender, commandArguments ->
                val players = commandArguments["players"] as Collection<Player>
                val id = commandArguments["id"] as Int
                val sourceArg: String = commandArguments["source"] as String? ?: "master"
                val ignore = commandArguments["ignore"] as Boolean
                val volume: Float = commandArguments["volume"] as Float? ?: 1F
                val pitch: Float = commandArguments["pitch"] as Float? ?: 1F
                var affected = 0
                var source: SoundSource
                source = SoundSource.valueOf(sourceArg.uppercase())
                if (soundManager.getSound(id) == null) {
                    sender.sendRichMessage("<red>No sound was registered at id <dark_red>$id")
                } else {
                    for (player in players) {
                        if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                        soundManager.playSound(player, id, source, true, false, ignore, volume, pitch, Vector3f())
                        affected++
                    }
                    sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                }
            }
        }
    }

    private fun modify(): CommandAPICommand {
        return subcommand("modify") {
            entitySelectorArgumentManyPlayers("players", false, false)
            integerArgument("id")
            floatArgument("volume", optional = false)
            integerArgument("interpolation", optional = false)
            floatArgument("startvolume", optional = true)
            playerExecutor { sender, commandArguments ->
                val players = commandArguments["players"] as Collection<Player>
                val id = commandArguments["id"] as Int
                val volume: Float = commandArguments["volume"] as Float
                val interpolation = commandArguments["interpolation"] as Int
                val startVolume: Float? = commandArguments["startvolume"] as Float?
                var affected = 0
                if (soundManager.getSound(id) == null) {
                    sender.sendRichMessage("<red>No sound was registered at id <dark_red>$id")
                } else {
                    for (player in players) {
                        if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                        soundManager.modifySound(player, id, volume, interpolation, startVolume)
                        affected++
                    }
                    sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                }
            }
        }
    }

    private fun stop(): CommandAPICommand {
        return subcommand("stop") {
            entitySelectorArgumentManyPlayers("players", false, false)
            integerArgument("id")
            playerExecutor { sender, commandArguments ->
                val players = commandArguments["players"] as Collection<Player>
                val id = commandArguments["id"] as Int
                var affected = 0
                if (soundManager.getSound(id) == null) {
                    sender.sendRichMessage("<red>No sound was registered at id <dark_red>$id")
                } else {
                    for (player in players) {
                        if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                        soundManager.stopSound(player, id)
                        affected++
                    }
                    sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                }
            }
        }
    }

    private fun add(): CommandAPICommand {
        return subcommand("add") {
            soundArgument("sound", true, false)
            playerExecutor { sender, commandArguments ->
                val sound = commandArguments["sound"] as NamespacedKey
                val parsedSound = tryBySeparator(sound.asString(), ':')
                if (parsedSound != null) {
                    if (soundManager.getSounds().containsValue(parsedSound)) {
                        sender.sendRichMessage("<red>This sound was already registered")
                    } else {
                        val result = soundManager.addSound(parsedSound)
                        sender.sendRichMessage("<green>Added <dark_green>${sound.asString()}<green> at id <dark_green>$result")
                    }
                } else {
                    sender.sendRichMessage("<red>There was an error parsing the sound!")
                }
            }
        }
    }

    private fun remove(): CommandAPICommand {
        return subcommand("remove") {
            integerArgument("id", optional = false)
            playerExecutor { sender, commandArguments ->
                val id = commandArguments["id"] as Int
                val sound: ResourceLocation? = soundManager.getSound(id)
                if (sound == null) {
                    sender.sendRichMessage("<red>No sound was registered at id <dark_red>$id")
                } else {

                    soundManager.removeSound(id)
                    sender.sendRichMessage("<green>Removed sound <dark_green>$sound <green>from id <dark_green>$id")
                }
            }
        }
    }

    private fun list(): CommandAPICommand {
        return subcommand("list") {
            playerExecutor { sender, _ ->
                if (soundManager.getSounds().isNotEmpty()) {
                    soundManager.getSounds().forEach { id, sound ->
                        sender.sendRichMessage("<aqua>Id: <blue>$id <aqua>Sound: <blue>$sound")
                    }
                } else {
                    sender.sendRichMessage("<red>No sounds have been registered! Use <dark_red>'/noxesiumapi sound add'<red> to add one.")
                }
            }
        }
    }

    public fun registerCommands() {
        Logger.info("Creating sound subcommands...")
        SoundCommands.add(play())
        SoundCommands.add(playLoop())
        SoundCommands.add(modify())
        SoundCommands.add(stop())
        SoundCommands.add(add())
        SoundCommands.add(remove())
        SoundCommands.add(list())
        Logger.info("Created sound subcommands!")
    }

}