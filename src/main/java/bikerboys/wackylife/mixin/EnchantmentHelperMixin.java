package bikerboys.wackylife.mixin;

import com.google.common.collect.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.stream.*;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Unique
    private static final Set<RegistryKey<Enchantment>> LEVEL_ONE_ONLY = Set.of(
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.FIRE_ASPECT,
            Enchantments.KNOCKBACK,
            Enchantments.SWEEPING_EDGE,
            Enchantments.POWER,
            Enchantments.PUNCH,
            Enchantments.PROTECTION,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.BLAST_PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FEATHER_FALLING,
            Enchantments.THORNS,
            Enchantments.BREACH,
            Enchantments.DENSITY,
            Enchantments.WIND_BURST,
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.QUICK_CHARGE
    );


    @Inject(method = "getPossibleEntries", at = @At("HEAD"), cancellable = true)
    private static void clampEnchantments(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {

        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        boolean isBook = stack.isOf(Items.BOOK);

        possibleEnchantments
                .filter(holder -> holder.value().isPrimaryItem(stack) || isBook)
                .forEach(holder -> {

                    Enchantment enchantment = holder.value();
                    Optional<RegistryKey<Enchantment>> key = holder.getKey();

                    int maxLevel = enchantment.getMaxLevel();

                    if (key.isPresent() && LEVEL_ONE_ONLY.contains(key.get())) {
                        maxLevel = 1;
                    }

                    for (int j = maxLevel; j >= enchantment.getMinLevel(); j--) {
                        if (level >= enchantment.getMinPower(j)
                                && level <= enchantment.getMaxPower(j)) {
                            list.add(new EnchantmentLevelEntry(holder, j));
                            break;
                        }
                    }
                });

        cir.setReturnValue(list);
    }






}
