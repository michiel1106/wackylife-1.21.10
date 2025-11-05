package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

public record WackySkinsActive(boolean active) implements CustomPayload {
    public static final Identifier WACKY_SKINS_ACTIVE = Identifier.of(WackyLife.MOD_ID, "wackyskinsactive");
    public static final Id<WackySkinsActive> ID = new Id<>(WACKY_SKINS_ACTIVE);
    public static final PacketCodec<RegistryByteBuf, WackySkinsActive> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOLEAN, // encode/decode list of strings
                    WackySkinsActive::active,
                    WackySkinsActive::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
