package org.macver.blip.mixin.client;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInventory.class)
public interface PlayerInventoryAccessor {
    @Accessor("selectedSlot")
    int getSelectedSlot();
}