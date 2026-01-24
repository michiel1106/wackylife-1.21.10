package bikerboys.wackylife.voicechat;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.audio.*;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.*;
import de.maxhenkel.voicechat.api.packets.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoicechatMain implements VoicechatPlugin {

    private static final Set<UUID> connectedPlayers =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private AudioConverter audioConverter;

    // one DSP + opus pipeline per player
    private final Map<UUID, DeepVoiceEffect> effects = new ConcurrentHashMap<>();
    private final Map<UUID, OpusEncoder> encoders = new ConcurrentHashMap<>();
    private final Map<UUID, OpusDecoder> decoders = new ConcurrentHashMap<>();

    @Override
    public String getPluginId() {
        return "wackylife";
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.audioConverter = api.getAudioConverter();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::onPlayerDisconnected);
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    private void onPlayerConnected(PlayerConnectedEvent event) {
        UUID uuid = event.getConnection().getPlayer().getUuid();
        connectedPlayers.add(uuid);

        DeepVoiceEffect effect = new DeepVoiceEffect();
        effect.init(audioConverter);
        effects.put(uuid, effect);

        VoicechatApi api = event.getVoicechat();
        encoders.put(uuid, api.createEncoder());
        decoders.put(uuid, api.createDecoder());
    }

    private void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        UUID uuid = event.getPlayerUuid();

        connectedPlayers.remove(uuid);
        effects.remove(uuid);
        encoders.remove(uuid);
        decoders.remove(uuid);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {

        if (!(WackyLife.wackyLife.getWildcardObj() instanceof WackySkins skins)
                || !skins.afterFirstSwap) {
            return;
        }

        VoicechatConnection sender = event.getSenderConnection();
        if (sender == null) return;

        UUID senderUUID = sender.getPlayer().getUuid();

        DeepVoiceEffect effect = effects.get(senderUUID);
        OpusDecoder decoder = decoders.get(senderUUID);
        OpusEncoder encoder = encoders.get(senderUUID);

        if (effect == null || decoder == null || encoder == null) return;

        MicrophonePacket packet = event.getPacket();

        short[] pcm;
        try {
            pcm = decoder.decode(packet.getOpusEncodedData());
        } catch (Exception e) {
            return;
        }

        if (pcm == null || pcm.length == 0) return;

        short[] processed = effect.applyEffect(pcm);

        byte[] opus;
        try {
            opus = encoder.encode(processed);
        } catch (Exception e) {
            return;
        }

        VoicechatServerApi api = event.getVoicechat();

        // stop default broadcast (CRITICAL)


        for (UUID targetUUID : connectedPlayers) {
            if (targetUUID.equals(senderUUID)) continue;

            VoicechatConnection target = api.getConnectionOf(targetUUID);
            if (target == null) continue;

            LocationalSoundPacket out =
                    packet.locationalSoundPacketBuilder()
                            .opusEncodedData(opus)
                            .position(api.createPosition(
                                    sender.getPlayer().getPosition().getX(),
                                    sender.getPlayer().getPosition().getY(),
                                    sender.getPlayer().getPosition().getZ()
                            ))
                            .distance((float) api.getBroadcastRange())
                            .build();

            api.sendLocationalSoundPacketTo(target, out);
        }
    }
}
