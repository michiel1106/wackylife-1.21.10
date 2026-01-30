package bikerboys.wackylife.util;


import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.loot.context.*;
import net.minecraft.registry.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;

import java.util.*;

public class ItemSpawner {
    HashMap<ItemStack, Integer> lootTable = new HashMap<>();
    private static final Random random = new Random();

    public void addItem(ItemStack item, int weight) {
        lootTable.put(item.copy(), weight);
    }

    public ItemStack getRandomItem() {
        if (lootTable.isEmpty()) {
            return null;
        }

        int totalWeight = lootTable.values().stream().mapToInt(Integer::intValue).sum();

        int randomWeight = random.nextInt(totalWeight);

        for (Map.Entry<ItemStack, Integer> entry : lootTable.entrySet()) {
            randomWeight -= entry.getValue();
            if (randomWeight < 0) {
                return entry.getKey().copy();
            }
        }

        return null;
    }


    public static List<ItemStack> getRandomItemsFromLootTable(MinecraftServer server, ServerWorld level, ServerPlayerEntity player, Identifier lootTableId, boolean silent) {
        if (server == null || level == null || player == null) return new ArrayList<>();
        try {


            LootTable lootTable = level.getServer()
                    .getReloadableRegistries()
                    .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId));

            LootWorldContext worldContext = new LootWorldContext.Builder(level)
                    .add(LootContextParameters.ORIGIN, player.getEntityPos())
                    .add(LootContextParameters.THIS_ENTITY, player)
                    .build(LootContextTypes.COMMAND);

            if (lootTable == null) {
                return new ArrayList<>();
            }

            List<ItemStack> generatedLoot = lootTable.generateLoot(worldContext);

            if (generatedLoot == null || generatedLoot.isEmpty()) {

                return new ArrayList<>();
            }

            return generatedLoot;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
