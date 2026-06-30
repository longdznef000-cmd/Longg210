package com.example.autototem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoTotemMod implements ModInitializer {

    public static final String MOD_ID = "autototem";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayerEntity)) {
                return;
            }
            if (!isCrystalOrAnchorKill(damageSource)) {
                return;
            }
            if (!(damageSource.getAttacker() instanceof ServerPlayerEntity killer)) {
                return;
            }
            equipTotemToMainHand(killer);
        });
    }

    private boolean isCrystalOrAnchorKill(DamageSource damageSource) {
        return damageSource.isOf(DamageTypes.PLAYER_EXPLOSION)
                || damageSource.isOf(DamageTypes.EXPLOSION);
    }

    private void equipTotemToMainHand(ServerPlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }
        var inventory = player.getInventory();
        int selectedSlot = inventory.getSelectedSlot();
        for (int slot = 0; slot < 9; slot++) {
            if (slot == selectedSlot) {
                continue;
            }
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                inventory.setStack(slot, mainHandStack);
                player.setStackInHand(Hand.MAIN_HAND, stack);
                LOGGER.info("Da chuyen totem ra tay chinh cho " + player.getName().getString());
                return;
            }
        }
        inventory.setSelectedSlot(0);
        ItemStack slotOneStack = inventory.getStack(0);
        if (!slotOneStack.isEmpty()) {
            slotOneStack.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME, net.minecraft.text.Text.literal("longg210"));
            LOGGER.info("Khong co totem, da doi ten slot 1 thanh longg210 cho " + player.getName().getString());
        }
    }
            }
