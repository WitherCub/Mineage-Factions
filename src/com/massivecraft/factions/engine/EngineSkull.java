package com.massivecraft.factions.engine;

import com.google.common.collect.Iterables;
import com.massivecraft.factions.entity.Skulls;
import com.massivecraft.massivecore.Engine;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GameProfileSerializer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class EngineSkull extends Engine {

    private static final EngineSkull i = new EngineSkull();

    public static EngineSkull get() {
        return i;
    }

    public ItemStack getSkullItem(String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (texture != null) {
            net.minecraft.server.v1_8_R3.ItemStack vanillaStack = CraftItemStack.asNMSCopy(head);
            NBTTagCompound baseCompound = vanillaStack.getTag();
            if (baseCompound == null) {
                baseCompound = new NBTTagCompound();
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), "pepega");
            profile.getProperties().put("textures", new Property("textures", texture));
            NBTTagCompound skullOwner = new NBTTagCompound();
            GameProfileSerializer.serialize(skullOwner, profile);
            baseCompound.set("SkullOwner", skullOwner);
            vanillaStack.setTag(baseCompound);
            return CraftItemStack.asCraftCopy(CraftItemStack.asBukkitCopy(vanillaStack));
        }

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner("pepega");
        head.setItemMeta(skullMeta);
        return head;
    }

    public ItemStack getSkullItem(UUID uuid, String name) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        String texture = Skulls.get().getSkullTextureCache().getOrDefault(uuid, null);

        if (texture != null) {
            net.minecraft.server.v1_8_R3.ItemStack vanillaStack = CraftItemStack.asNMSCopy(head);
            NBTTagCompound baseCompound = vanillaStack.getTag();
            if (baseCompound == null) {
                baseCompound = new NBTTagCompound();
            }
            GameProfile profile = new GameProfile(uuid, name);
            profile.getProperties().put("textures", new Property("textures", texture));
            NBTTagCompound skullOwner = new NBTTagCompound();
            GameProfileSerializer.serialize(skullOwner, profile);
            baseCompound.set("SkullOwner", skullOwner);
            vanillaStack.setTag(baseCompound);
            return CraftItemStack.asCraftCopy(CraftItemStack.asBukkitCopy(vanillaStack));
        }

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner(name);
        head.setItemMeta(skullMeta);
        return head;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Property textureProperty = Iterables.getFirst(entityPlayer.getProfile().getProperties().get("textures"), null);
        if (textureProperty != null) {
            Skulls.get().getSkullTextureCache().put(player.getUniqueId(), textureProperty.getValue());
        }
    }
}