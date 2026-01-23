package bikerboys.wackylife.voicechat;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.audio.*;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.*;
import de.maxhenkel.voicechat.api.packets.*;


import java.util.*;

public class VoicechatMain implements VoicechatPlugin {

    private static List<UUID> connectedPlayers = new ArrayList<>();

    private OpusEncoder encoder;
    private OpusDecoder decoder;
    private AudioConverter audioConverter;


    private final Map<UUID, DeepVoiceEffect> playerEffects = new HashMap<>();


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
    }

    private void onPlayerConnected(PlayerConnectedEvent event) {
        UUID uuid = event.getConnection().getPlayer().getUuid();
        if (!connectedPlayers.contains(uuid)) {
            connectedPlayers.add(uuid);

            // create a new instance of DeepVoiceEffect for this player
            DeepVoiceEffect effect = new DeepVoiceEffect();
            effect.init(audioConverter);
            playerEffects.put(uuid, effect);
        }
    }

    public static boolean isConnectedToSVC(UUID uuid) {
        return connectedPlayers.contains(uuid);
    }

    private void onAudioPacket(MicrophonePacketEvent event) {

        if (WackyLife.wackyLife.getWildcardObj() != null
                && WackyLife.wackyLife.getWildcardObj() instanceof WackySkins wackySkins
                && wackySkins.afterFirstSwap) {

            VoicechatConnection senderConnection = event.getSenderConnection();
            if (senderConnection == null) return;

            UUID senderUUID = senderConnection.getPlayer().getUuid();
            MicrophonePacket originalPacket = event.getPacket();
            byte[] opusData = originalPacket.getOpusEncodedData().clone();

            // get the player's DeepVoiceEffect instance
            DeepVoiceEffect effect = playerEffects.get(senderUUID);
            byte[] processedOpus = (effect != null) ? processOpusAudioWithEffect(opusData, effect) : opusData;

            VoicechatServerApi api = event.getVoicechat();
            for (UUID targetUUID : connectedPlayers) {
                if (targetUUID.equals(senderUUID)) continue;

                VoicechatConnection targetConnection = api.getConnectionOf(targetUUID);
                if (targetConnection == null) continue;

                LocationalSoundPacket newPacket = originalPacket.locationalSoundPacketBuilder()
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

            originalPacket.setOpusEncodedData(processedOpus);
        }
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
