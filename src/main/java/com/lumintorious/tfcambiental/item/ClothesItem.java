package com.lumintorious.tfcambiental.item;

import com.lumintorious.tfcambiental.api.EquipmentTemperatureProvider;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.world.item.*;

public class ClothesItem extends ArmorItem
{
    public ClothesItem(Holder<ArmorMaterial> material, Type type, Properties pProperties) {
        super(material, type, pProperties);
    }

    public EquipmentTemperatureProvider getProvider() {
        return ClothesMaterial.temperatureOf(material.value());
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 1000;
    }
}
