package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

import java.util.*;

public record WackyPlayerMap(Map<String, Pair<String, Integer>> players) implements CustomPayload {
    public static final Identifier WACKY_PLAYER_LIST = Identifier.of(WackyLife.MOD_ID, "wackyplayerlist");
    public static final Id<WackyPlayerMap> ID = new Id<>(WACKY_PLAYER_LIST);

    public static final PacketCodec<PacketByteBuf, WackyPlayerMap> CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, WackyPlayerMap value) {
            buf.writeVarInt(value.players.size()); // write the map size
            value.players.forEach((key, val) -> {
                buf.writeString(key);            // write map key
                buf.writeString(val.getLeft());  // write string part of pair
                buf.writeInt(val.getRight());    // write integer part of pair
            });
        }

        @Override
        public WackyPlayerMap decode(PacketByteBuf buf) {
            int size = buf.readVarInt();
            Map<String, Pair<String, Integer>> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String key = buf.readString(32767);
                String str = buf.readString(32767);
                int num = buf.readInt();
                map.put(key, new Pair<>(str, num));
            }
            return new WackyPlayerMap(map);
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
