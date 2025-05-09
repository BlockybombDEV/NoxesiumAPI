package me.iris.noxesiumapi.packets

import com.noxcrew.noxesium.paper.api.NoxesiumManager
import com.noxcrew.noxesium.paper.api.network.clientbound.ClientboundCustomSoundModifyPacket
import com.noxcrew.noxesium.paper.api.network.clientbound.ClientboundCustomSoundStartPacket
import com.noxcrew.noxesium.paper.api.network.clientbound.ClientboundCustomSoundStopPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import org.bukkit.entity.Player
import org.joml.Vector3f


class SoundManager(private val manager: NoxesiumManager) {

    private val sounds: MutableMap<Int, ResourceLocation> = mutableMapOf()

    fun addSound(sound: ResourceLocation): Int {
        val size = sounds.size.inc()
        sounds[size] = sound
        return size

    }

    fun removeSound(id: Int) {
        sounds.remove(id)
    }

     fun getSound(id: Int): ResourceLocation? {
        return sounds[id]
    }

     fun getSounds(): Map<Int, ResourceLocation> {
        return sounds
    }

    fun playSound(
        player: Player,
        id: Int,
        category: SoundSource,
        looping: Boolean,
        attenuation: Boolean,
        ignoreIfPlaying: Boolean,
        volume: Float,
        pitch: Float,
        position: Vector3f,
        entityId: Int? = null,
        unix: Long? = null,
        offset: Float? = null,
        ) {
        val sound = sounds[id] as ResourceLocation
        manager.sendPacket(player, ClientboundCustomSoundStartPacket(
            id,
            sound,
            category,
            looping,
            attenuation,
            ignoreIfPlaying,
            volume,
            pitch,
            position,
            entityId,
            unix,
            offset
        )
        )
    }

    fun stopSound(player: Player, id: Int) {
        manager.sendPacket(player, ClientboundCustomSoundStopPacket(id))
    }

    fun modifySound(player: Player, id: Int, volume: Float, interpolationTicks: Int, startVolume: Float? = null) {
        manager.sendPacket(player, ClientboundCustomSoundModifyPacket(id, volume, interpolationTicks, startVolume))
    }

}