package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.wyr.choice.*;
import dev.architectury.event.*;
import dev.architectury.event.events.common.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.entry.*;
import net.minecraft.server.network.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class RandomBlockPlace extends Choice {
    Random random = new Random();

    public RandomBlockPlace(String id, boolean positive) {
        super(id, positive);
    }
}
