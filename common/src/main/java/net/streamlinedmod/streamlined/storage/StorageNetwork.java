package net.streamlinedmod.streamlined.storage;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.cable.part.PowerSupplyPart;
import net.streamlinedmod.streamlined.cable.part.StorageTerminalPart;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public final class StorageNetwork {

    private static final Comparator<StoredItem> ORDER = Comparator.comparingLong(StoredItem::count).reversed()
            .thenComparing(item -> item.item().item().getRegisteredName());

    private final List<Cable> cables;
    private final List<StorageProvider> providers;
    private final List<StorageTerminalPart> terminals;
    private final List<PowerSupplyPart> supplies;
    private final long channels;

    private NetworkStatus status = NetworkStatus.OFFLINE;

    StorageNetwork(List<Cable> cables, List<StorageProvider> providers, List<StorageTerminalPart> terminals, List<PowerSupplyPart> supplies) {
        this.cables = cables;
        this.providers = providers;
        this.terminals = terminals;
        this.supplies = supplies;
        this.channels = cables.stream().mapToLong(Cable::rate).min().orElse(0);
    }

    public @NonNull NetworkStatus status() {
        return status;
    }

    public boolean isOnline() {
        return status == NetworkStatus.ONLINE;
    }

    public int devices() {
        return providers.size() + terminals.size();
    }

    public long channels() {
        return channels;
    }

    public long powerUsage() {
        long usage = 0;

        for (var provider : providers) {
            usage += provider.powerUsage();
        }

        for (var terminal : terminals) {
            usage += terminal.powerUsage();
        }

        return usage;
    }

    void inherit(@NonNull StorageNetwork previous) {
        status = previous.status;
    }

    void tick() {
        if (devices() > channels) {
            status = NetworkStatus.NO_CHANNELS;
            return;
        }

        long usage = powerUsage();
        long available = 0;

        for (var supply : supplies) {
            available += supply.drain(usage - available, true);
        }

        if (available < usage) {
            status = NetworkStatus.NO_POWER;
            return;
        }

        long remaining = usage;

        for (var supply : supplies) {
            remaining -= supply.drain(remaining, false);
        }

        status = NetworkStatus.ONLINE;
    }

    boolean isRemoved() {
        for (var cable : cables) {
            if (cable.isRemoved()) {
                return true;
            }
        }

        for (var provider : providers) {
            if (provider instanceof BlockEntity be && be.isRemoved()) {
                return true;
            }
        }

        return false;
    }

    public @NonNull List<StoredItem> items() {
        if (!isOnline()) {
            return List.of();
        }

        var totals = new LinkedHashMap<ItemStackTemplate, Long>();

        for (var provider : providers) {
            provider.collect(item -> totals.merge(item.item(), item.count(), Long::sum));
        }

        var items = new ArrayList<StoredItem>(totals.size());

        totals.forEach((item, count) -> items.add(new StoredItem(item, count)));
        items.sort(ORDER);

        return items;
    }

    public long insert(@NonNull ItemStack stack, long amount, boolean simulate) {
        if (!isOnline() || stack.isEmpty()) {
            return 0;
        }

        long inserted = 0;

        for (var provider : providers) {
            inserted += provider.insert(stack, amount - inserted, simulate);

            if (inserted >= amount) {
                break;
            }
        }

        return inserted;
    }

    public long extract(@NonNull ItemStack stack, long amount, boolean simulate) {
        if (!isOnline() || stack.isEmpty()) {
            return 0;
        }

        long extracted = 0;

        for (var provider : providers) {
            extracted += provider.extract(stack, amount - extracted, simulate);

            if (extracted >= amount) {
                break;
            }
        }

        return extracted;
    }
}
