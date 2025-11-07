package bikerboys.wackylife.mixin;


import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MutableText.class)
public interface MutableTextAccessor {
    @Accessor("content")
    void setContent(TextContent content);
}
