package bikerboys.wackylife.attachements;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import io.netty.buffer.*;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.*;

/**
 * This record holds the PLAYER-SPECIFIC DATA that is saved.
 * It now includes two CompoundTag fields to store data (like cooldowns)
 * for the positive and negative choices.
 */
public record ChoiceAttachment(
        String positiveChoiceId,
        String negativeChoiceId,
        NbtCompound positiveData,
        NbtCompound negativeData
) {

    // The CODEC must be updated to save our new data fields.
    // CompoundTag already has a codec: CompoundTag.CODEC
    public static Codec<ChoiceAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("positiveChoiceId").forGetter(ChoiceAttachment::positiveChoiceId),
            Codec.STRING.fieldOf("negativeChoiceId").forGetter(ChoiceAttachment::negativeChoiceId),
            NbtCompound.CODEC.optionalFieldOf("positiveData", new NbtCompound()).forGetter(ChoiceAttachment::positiveData),
            NbtCompound.CODEC.optionalFieldOf("negativeData", new NbtCompound()).forGetter(ChoiceAttachment::negativeData)
    ).apply(instance, ChoiceAttachment::new));

    public static PacketCodec<ByteBuf, ChoiceAttachment> PACKET_CODEC = PacketCodecs.codec(CODEC);

    // The DEFAULT instance now provides empty tags.
    public static ChoiceAttachment DEFAULT = new ChoiceAttachment("", "", new NbtCompound(), new NbtCompound());

    // Helper method to set choices, which now resets the data tags.
    public ChoiceAttachment setChoices(String positivechoice, String negativeChoice) {
        return new ChoiceAttachment(positivechoice, negativeChoice, new NbtCompound(), new NbtCompound());
    }

    public ChoiceAttachment clearChoices() {
        return DEFAULT;
    }
}