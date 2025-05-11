package me.iris.noxesiumapi.commands

import com.noxcrew.noxesium.api.protocol.NoxesiumFeature
import com.noxcrew.noxesium.api.protocol.rule.ServerRuleIndices
import com.noxcrew.noxesium.paper.api.rule.GraphicsType
import com.noxcrew.noxesium.paper.api.rule.RemoteServerRule
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.*
import me.iris.noxesiumapi.NoxesiumAPI
import me.iris.noxesiumapi.NoxesiumAPI.Companion.noxesiumManager
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Suppress("unchecked_cast")
class ServerRulesCommand {

    companion object {
        var RuleCommands: MutableList<CommandAPICommand> = mutableListOf()
    }

    private var booleanServerRules: MutableMap<String, Int> = mutableMapOf(
        "disableSpinAttackCollision" to ServerRuleIndices.DISABLE_SPIN_ATTACK_COLLISIONS,
        "cameraLocked" to ServerRuleIndices.CAMERA_LOCKED,
        "disableVanillaMusic" to ServerRuleIndices.DISABLE_VANILLA_MUSIC,
        "disableBoatCollisions" to ServerRuleIndices.DISABLE_BOAT_COLLISIONS,
        "showMapInUi" to ServerRuleIndices.SHOW_MAP_IN_UI,
        "disableDeferredChunkUpdates" to ServerRuleIndices.DISABLE_DEFERRED_CHUNK_UPDATES,
        "enableSmootherClientTrident" to ServerRuleIndices.ENABLE_SMOOTHER_CLIENT_TRIDENT,
        "disableMapUi" to ServerRuleIndices.DISABLE_MAP_UI,
        "riptidePreCharging" to ServerRuleIndices.RIPTIDE_PRE_CHARGING,
    )

    private var integerServerRules: MutableMap<String, Int> = mutableMapOf(
        "heldItemNameOffset" to ServerRuleIndices.HELD_ITEM_NAME_OFFSET,
        "riptideCoyoteTime" to ServerRuleIndices.RIPTIDE_COYOTE_TIME
    )

    private var itemStackServerRules: MutableMap<String, Int> = mutableMapOf(
        "handItemOverride" to ServerRuleIndices.HAND_ITEM_OVERRIDE,
    )

    private var optionalEnumServerRules: MutableMap<String, Int> = mutableMapOf(
        "overrideGraphicsMode" to ServerRuleIndices.OVERRIDE_GRAPHICS_MODE
    )

    private var intListServerRule: MutableMap<String, Int> = mutableMapOf(
        "restrictDebugOptions" to ServerRuleIndices.RESTRICT_DEBUG_OPTIONS
    )

    private var allRules: MutableMap<String, Int> = mutableMapOf()

    fun graphicsArgument(type: String): Argument<GraphicsType> {
        return CustomArgument(StringArgument(type)) { info ->
            val graphicsType = GraphicsType.valueOf(type)

            if (graphicsType == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(CustomArgument.MessageBuilder("Unknown GraphicsType: ").appendArgInput())
            } else {
                graphicsType
            }
        }.replaceSuggestions(ArgumentSuggestions.strings { _ ->
            GraphicsType.entries.map { it.name }.toTypedArray()
        })
    }

    fun registerCommands() {
        NoxesiumAPI.Logger.info("Creating server rules subcommands...")
        allRules.putAll(booleanServerRules)
        allRules.putAll(integerServerRules)
        allRules.putAll(itemStackServerRules)

        // Boolean server rules
        for (rules in booleanServerRules) {
            RuleCommands.add(
                subcommand(rules.key) {
                    entitySelectorArgumentManyPlayers("players", allowEmpty = false, optional = false)
                    booleanArgument("enabled", false)
                    anyExecutor { sender, commandArguments ->
                        val players = commandArguments["players"] as Collection<Player>
                        val value = commandArguments["enabled"] as Boolean
                        var affected = 0
                        for (player in players) {
                            if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                            val rule: RemoteServerRule<Any>? = noxesiumManager.getServerRule(player, rules.value)
                            rule!!.value = value
                            affected++
                        }
                        sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                    }
                }
            )
        }

        // Integer server rules
        for (rules in integerServerRules) {
            RuleCommands.add(
                subcommand(rules.key) {
                    entitySelectorArgumentManyPlayers("players", allowEmpty = false, optional = false)
                    integerArgument("value")
                    anyExecutor { sender, commandArguments ->
                        val players = commandArguments["players"] as Collection<Player>
                        val value = commandArguments["value"] as Int
                        var affected = 0
                        for (player in players) {
                            if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                            val rule: RemoteServerRule<Any>? = noxesiumManager.getServerRule(player, rules.value)
                            rule!!.value = value
                            affected++
                        }
                        sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                    }
                }
            )
        }

        // Item stack server rules
        for (rules in optionalEnumServerRules) {
            RuleCommands.add(
                subcommand(rules.key) {
                    entitySelectorArgumentManyPlayers("players", allowEmpty = false, optional = false)
                    if (rules.key == "overrideGraphicsMode") {
                        graphicsArgument("value")
                    }
                    anyExecutor { sender, commandArguments ->
                        val players = commandArguments["players"] as Collection<Player>
                        val value = commandArguments["value"] as ItemStack
                        var affected = 0
                        for (player in players) {
                            if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                            val rule: RemoteServerRule<Any>? = noxesiumManager.getServerRule(player, rules.value)
                            rule!!.value = value
                            affected++
                        }
                        sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                    }
                }
            )
        }

        for (rules in itemStackServerRules) {
            RuleCommands.add(
                subcommand(rules.key) {
                    entitySelectorArgumentManyPlayers("players", allowEmpty = false, optional = false)
                    itemStackArgument("value")
                    anyExecutor { sender, commandArguments ->
                        val players = commandArguments["players"] as Collection<Player>
                        val value = commandArguments["value"] as ItemStack
                        var affected = 0
                        for (player in players) {
                            if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                            val rule: RemoteServerRule<Any>? = noxesiumManager.getServerRule(player, rules.value)
                            rule!!.value = value
                            affected++
                        }
                        sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                    }
                }
            )
        }

        // Reset server rules
        RuleCommands.add(
            subcommand("reset") {
                entitySelectorArgumentManyPlayers("players", allowEmpty = false, optional = false)
                anyExecutor { sender, commandArguments ->
                    val players = commandArguments["players"] as Collection<Player>
                    var affected = 0
                    for (player in players) {
                        if (!noxesiumManager.isUsingNoxesium(player, NoxesiumFeature.API_V2)) continue
                        for (rules in allRules) {
                            val rule: RemoteServerRule<Any>? = noxesiumManager.getServerRule(player, rules.value)
                            rule!!.reset()
                        }
                        affected++
                    }
                    sender.sendRichMessage("<dark_green>$affected <green>player(s) affected")
                }
            }
        )

        NoxesiumAPI.Logger.info("Created server rules subcommands!")

    }

}