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

    private static final List<UUID> connectedPlayers = Collections.synchronizedList(new ArrayList<>());

    private OpusEncoder encoder;
    private OpusDecoder decoder;
    private AudioConverter audioConverter;

    // THREAD-SAFE: one effect per player
    private final Map<UUID, DeepVoiceEffect> playerEffects = new ConcurrentHashMap<>();

    @Override
    public String getPluginId() {
        return "wackylife";
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.encoder = api.createEncoder();
        this.decoder = api.createDecoder();
        this.audioConverter = api.getAudioConverter();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onAudioPacket);
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::onPlayerDisconnected);
    }

    private void onPlayerConnected(PlayerConnectedEvent event) {
        UUID uuid = event.getConnection().getPlayer().getUuid();

        if (!connectedPlayers.contains(uuid)) {
            connectedPlayers.add(uuid);

            DeepVoiceEffect effect = new DeepVoiceEffect();
            effect.init(audioConverter);
            playerEffects.put(uuid, effect);
        }
    }

    private void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        UUID uuid = event.getPlayerUuid();

        connectedPlayers.remove(uuid);

        DeepVoiceEffect effect = playerEffects.remove(uuid);
        if (effect != null) {
            effect.flushAndGetRemaining();
        }
    }

    public static boolean isConnectedToSVC(UUID uuid) {
        return connectedPlayers.contains(uuid);
    }

    private void onAudioPacket(MicrophonePacketEvent event) {

        if (!(WackyLife.wackyLife.getWildcardObj() instanceof WackySkins wackySkins)
                || !wackySkins.afterFirstSwap) {
            return;
        }

        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) return;

        UUID senderUUID = senderConnection.getPlayer().getUuid();
        DeepVoiceEffect effect = playerEffects.get(senderUUID);
        if (effect == null) return;

        MicrophonePacket originalPacket = event.getPacket();
        byte[] opusData = originalPacket.getOpusEncodedData().clone();

        byte[] processedOpus = processOpusAudioWithEffect(opusData, effect);

        VoicechatServerApi api = event.getVoicechat();

        synchronized (connectedPlayers) {
            for (UUID targetUUID : connectedPlayers) {
                if (targetUUID.equals(senderUUID)) continue;

                VoicechatConnection targetConnection = api.getConnectionOf(targetUUID);
                if (targetConnection == null) continue;

                LocationalSoundPacket newPacket =
                        originalPacket.locationalSoundPacketBuilder()
                                .opusEncodedData(processedOpus)
                                .position(api.createPosition(
                                        senderConnection.getPlayer().getPosition().getX(),
                                        senderConnection.getPlayer().getPosition().getY(),
                                        senderConnection.getPlayer().getPosition().getZ()
                                ))
                                .distance((float) api.getBroadcastRange())
                                .build();

                api.sendLocationalSoundPacketTo(targetConnection, newPacket);
            }
        }

        originalPacket.setOpusEncodedData(processedOpus);
    }

    private byte[] processOpusAudioWithEffect(byte[] opusData, DeepVoiceEffect effect) {
        try {
            short[] pcmData = decoder.decode(opusData);
            if (pcmData == null) return opusData;

            short[] processed = effect.applyEffect(pcmData);
            return encoder.encode(processed);
        } catch (Exception e) {
            e.printStackTrace();
            return opusData;
        }
    }
}
