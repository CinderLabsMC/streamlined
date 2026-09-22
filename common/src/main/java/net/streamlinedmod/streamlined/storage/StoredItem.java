package net.streamlinedmod.streamlined.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.NonNull;

public record StoredItem(@NonNull ItemStackTemplate item, long count) {

    public static final Codec<StoredItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(StoredItem::item),
            ExtraCodecs.POSITIVE_LONG.fieldOf("count").forGetter(StoredItem::count)
    ).apply(instance, StoredItem::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StoredItem> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, StoredItem::item,
            ByteBufCodecs.VAR_LONG, StoredItem::count,
            StoredItem::new);

    public static @NonNull ItemStackTemplate key(@NonNull ItemStack stack) {
        return ItemStackTemplate.fromNonEmptyStack(stack, 1);
    }

    public boolean matches(@NonNull ItemStack stack) {
        return !stack.isEmpty() && item.equals(key(stack));
    }

    public @NonNull ItemStack create(int count) {
        return item.create().copyWithCount(count);
    }

    public @NonNull StoredItem withCount(long count) {
        return new StoredItem(item, count);
    }
}
