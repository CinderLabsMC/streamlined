package net.streamlinedmod.streamlined.block;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntityType;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.cable.part.Attachment;
import net.streamlinedmod.streamlined.cable.part.CablePart;
import net.streamlinedmod.streamlined.cable.part.PartType;
import net.streamlinedmod.streamlined.cable.part.PowerSupplyPart;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.item.CablePartItem;
import net.streamlinedmod.streamlined.item.FacadeItem;
import net.streamlinedmod.streamlined.storage.StorageNetworks;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class Cable extends CinderGeoBlockEntity implements EnergyProvider {

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final PartType[] PART_TYPES = PartType.values();

    private static final String PARTS = "parts";
    private static final String PART_TYPE = "type";
    private static final String FACADES = "facades";
    private static final String WINDOW_SUFFIX = "_window";
    private static final double HIT_TOLERANCE = 0.001;
    private static final int FACADE_THICKNESS = 2;
    private static final VoxelShape[] FACADE_SHAPES = createFacadeShapes();

    public static final CinderBlockEntityType<Cable> BLOCK_ENTITY = ModBlocks.REGISTRAR.blockEntity("cable", Cable::new);

    static {
        EnergyBridge.register(BLOCK_ENTITY);
    }

    private final Map<Direction, CablePart> parts = new EnumMap<>(Direction.class);
    private final Map<Direction, BlockState> facades = new EnumMap<>(Direction.class);

    public static @NonNull CinderBlockType<Cable> register(@NonNull CableType type) {
        var geometry = type.geometry();
        var shapes = createShapes(geometry.collisionWidth());

        return CinderBlock.builder(ModBlocks.REGISTRAR, type.name())
                .block(CableBlock::new)
                .blockEntity(BLOCK_ENTITY)
                .data(type)
                .dynamicShape((state, level, pos, context) -> shape(level, pos, type.type(), shapes, context))
                .geo(geo -> geo
                        .model(geometry.blockModel())
                        .itemModel(geometry.itemModel())
                        .animation(geometry.animation())
                        .texture(type.texture()))
                .register();
    }

    public Cable(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    public @NonNull CableType type() {
        return data(CableType.class);
    }

    public long rate() {
        return type().rate();
    }

    public @Nullable CablePart part(@NonNull Direction side) {
        return parts.get(side);
    }

    public boolean hasPart(@NonNull Direction side) {
        return parts.containsKey(side);
    }

    public boolean isBlocked(@NonNull Direction side) {
        var part = parts.get(side);
        return part != null && part.blocksConnection();
    }

    public @NonNull Collection<CablePart> parts() {
        return Collections.unmodifiableCollection(parts.values());
    }

    public @Nullable BlockState facade(@NonNull Direction side) {
        return facades.get(side);
    }

    public @NonNull Map<Direction, BlockState> facades() {
        return Collections.unmodifiableMap(facades);
    }

    public @Nullable Attachment attachmentAt(@NonNull BlockHitResult hit) {
        var local = hit.getLocation().subtract(Vec3.atLowerCornerOf(worldPosition));

        for (var part : parts.values()) {
            var side = part.side();
            boolean covered = facades.containsKey(side);

            if (covered ? part.type().windowContains(side, local) : part.type().contains(side, local)) {
                return new Attachment(side, part);
            }
        }

        for (var side : facades.keySet()) {
            if (FACADE_SHAPES[side.ordinal()].bounds().inflate(HIT_TOLERANCE).contains(local)) {
                return Attachment.facade(side);
            }
        }

        return null;
    }

    public @Nullable Attachment targetAttachment(@NonNull Player player) {
        if ((parts.isEmpty() && facades.isEmpty()) || level == null) {
            return null;
        }

        var hit = pick(player, getBlockState().getShape(level, worldPosition));

        return hit == null ? null : attachmentAt(hit);
    }

    public @NonNull ItemStack attachmentItem(@NonNull Attachment attachment) {
        var part = attachment.part();

        if (part != null) {
            return part.toItem();
        }

        var facade = facades.get(attachment.side());

        return facade == null ? ItemStack.EMPTY : FacadeItem.of(facade);
    }

    public void breakAttachment(@NonNull Attachment attachment, @NonNull Player player) {
        if (level == null) {
            return;
        }

        var drop = removeAttachment(attachment);

        if (!drop.isEmpty() && !player.hasInfiniteMaterials()) {
            Block.popResource(level, worldPosition, drop);
        }
    }

    private ItemStack removeAttachment(Attachment attachment) {
        var drop = attachmentItem(attachment);
        boolean removed = attachment.isFacade()
                ? facades.remove(attachment.side()) != null
                : parts.remove(attachment.side(), attachment.part());

        if (!removed) {
            return ItemStack.EMPTY;
        }

        attachmentsChanged();

        return drop;
    }

    private VoxelShape attachmentShape(Attachment attachment) {
        var part = attachment.part();

        if (part == null) {
            return FACADE_SHAPES[attachment.side().ordinal()];
        }

        return facades.containsKey(part.side()) ? part.type().window(part.side()) : part.type().shape(part.side());
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        if (side != null && parts.get(side) instanceof PowerSupplyPart supply) {
            return supply.energy();
        }

        return null;
    }

    @Override
    public @NonNull InteractionResult onUseItem(@NonNull ItemStack stack, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        var side = hit.getDirection();

        if (stack.getItem() instanceof FacadeItem) {
            var block = FacadeItem.block(stack);

            if (block == null || facades.containsKey(side)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            if (level != null && !level.isClientSide()) {
                facades.put(side, block);
                stack.consume(1, player);
                attachmentsChanged();
            }

            return InteractionResult.SUCCESS;
        }

        if (!(stack.getItem() instanceof CablePartItem item) || !item.part().allowedOn(type().type())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        var target = attachmentAt(hit);

        if (hasPart(side) || (target != null && !target.isFacade())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (level != null && !level.isClientSide()) {
            var part = item.part().create(this, side);
            part.onPlaced(stack);
            parts.put(side, part);
            stack.consume(1, player);
            attachmentsChanged();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull InteractionResult onUse(@NonNull Player player, @NonNull BlockHitResult hit) {
        var attachment = attachmentAt(hit);

        if (attachment == null) {
            return InteractionResult.PASS;
        }

        if (!player.isSecondaryUseActive()) {
            var part = attachment.part();
            return part == null ? InteractionResult.PASS : part.onUse(player);
        }

        if (level != null && !level.isClientSide()) {
            var drop = removeAttachment(attachment);

            if (!drop.isEmpty() && !player.getInventory().add(drop)) {
                Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), drop);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void adjustBones(@NonNull CinderBones bones) {
        int connections = level == null ? 0 : connectionMask(level, worldPosition, type().type());

        for (var dir : DIRECTIONS) {
            bones.visible(dir.getSerializedName(), (connections & (1 << dir.ordinal())) != 0);
        }

        for (var partType : PART_TYPES) {
            for (var dir : DIRECTIONS) {
                var part = parts.get(dir);
                boolean present = part != null && part.type() == partType;

                bones.visible(partType.bone(dir), present);
                bones.visible(partType.bone(dir) + WINDOW_SUFFIX, present && facades.containsKey(dir));
            }
        }
    }

    @Override
    protected @Nullable Container getDroppedContents() {
        if (parts.isEmpty() && facades.isEmpty()) {
            return null;
        }

        var drops = new SimpleContainer(parts.size() + facades.size());
        int slot = 0;

        for (var part : parts.values()) {
            drops.setItem(slot++, part.toItem());
        }

        for (var facade : facades.values()) {
            drops.setItem(slot++, FacadeItem.of(facade));
        }

        return drops;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (level instanceof ServerLevel server) {
            EnergyCableNetworks.add(server, this);
            StorageNetworks.add(server, this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level instanceof ServerLevel server) {
            EnergyCableNetworks.remove(server, this);
            StorageNetworks.remove(server, this);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        if (!facades.isEmpty()) {
            var facadesOutput = output.child(FACADES);
            facades.forEach((side, block) -> facadesOutput.store(side.getSerializedName(), BlockState.CODEC, block));
        }

        if (parts.isEmpty()) {
            return;
        }

        var partsOutput = output.child(PARTS);

        parts.forEach((side, part) -> {
            var partOutput = partsOutput.child(side.getSerializedName());
            partOutput.putString(PART_TYPE, part.type().id());
            part.save(partOutput);
        });
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        parts.clear();
        facades.clear();

        var facadesInput = input.childOrEmpty(FACADES);

        for (var side : DIRECTIONS) {
            facadesInput.read(side.getSerializedName(), BlockState.CODEC).ifPresent(block -> facades.put(side, block));
        }

        var partsInput = input.childOrEmpty(PARTS);

        for (var side : DIRECTIONS) {
            partsInput.child(side.getSerializedName()).ifPresent(partInput -> {
                var partType = PartType.byId(partInput.getStringOr(PART_TYPE, ""));

                if (partType == null) {
                    return;
                }

                var part = partType.create(this, side);
                part.load(partInput);
                parts.put(side, part);
            });
        }
    }

    private void attachmentsChanged() {
        sync();

        if (level != null) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            StorageNetworks.markDirty(level);
            EnergyCableNetworks.markDirty(level);
        }
    }

    public static int connectionMask(@NonNull BlockGetter level, @NonNull BlockPos pos, CableType.@NonNull Type type) {
        int mask = 0;

        for (var dir : DIRECTIONS) {
            if (isConnected(level, pos, dir, type)) {
                mask |= 1 << dir.ordinal();
            }
        }

        return mask;
    }

    public static boolean isConnected(@NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction dir, CableType.@NonNull Type type) {
        if (level.getBlockEntity(pos) instanceof Cable self && self.isBlocked(dir)) {
            return false;
        }

        var target = pos.relative(dir);
        var neighbor = level.getBlockEntity(target);

        if (neighbor instanceof Cable cable && cable.type().type() == type) {
            return !cable.isBlocked(dir.getOpposite());
        }

        return switch (type) {
            case ENERGY -> {
                if (neighbor instanceof EnergyProvider provider && provider.getEnergy(dir.getOpposite()) != null) {
                    yield true;
                }
                if (neighbor instanceof Cable) {
                    yield false;
                }
                if (level instanceof Level real) {
                    yield EnergyBridge.findExternal(real, target, dir.getOpposite()) != null;
                }
                yield false;
            }
            case STORAGE -> !(neighbor instanceof Cable) && (neighbor instanceof StorageProvider || neighbor instanceof Container);
        };
    }

    private static VoxelShape shape(BlockGetter level, BlockPos pos, CableType.Type type, VoxelShape[] shapes, CollisionContext context) {
        int mask = connectionMask(level, pos, type);

        if (!(level.getBlockEntity(pos) instanceof Cable cable) || (cable.parts.isEmpty() && cable.facades.isEmpty())) {
            return shapes[mask];
        }

        int partMask = mask;

        for (var side : cable.parts.keySet()) {
            partMask |= 1 << side.ordinal();
        }

        var full = shapes[partMask];

        for (var part : cable.parts.values()) {
            full = Shapes.or(full, part.type().shape(part.side()));
        }

        for (var side : cable.facades.keySet()) {
            full = Shapes.or(full, FACADE_SHAPES[side.ordinal()]);
        }

        if (!(context instanceof EntityCollisionContext entityContext) || !(entityContext.getEntity() instanceof Player player)) {
            return full;
        }

        var hit = cable.pick(player, full);

        if (hit == null) {
            return full;
        }

        var attachment = cable.attachmentAt(hit);

        return attachment == null ? shapes[mask] : cable.attachmentShape(attachment);
    }

    private @Nullable BlockHitResult pick(Player player, VoxelShape shape) {
        var eye = player.getEyePosition();
        var reach = eye.add(player.getViewVector(1).scale(player.blockInteractionRange() + 1));

        return shape.clip(eye, reach, worldPosition);
    }

    private static VoxelShape[] createFacadeShapes() {
        var shapes = new VoxelShape[DIRECTIONS.length];

        shapes[Direction.NORTH.ordinal()] = Block.box(0, 0, 0, 16, 16, FACADE_THICKNESS);
        shapes[Direction.SOUTH.ordinal()] = Block.box(0, 0, 16 - FACADE_THICKNESS, 16, 16, 16);
        shapes[Direction.WEST.ordinal()] = Block.box(0, 0, 0, FACADE_THICKNESS, 16, 16);
        shapes[Direction.EAST.ordinal()] = Block.box(16 - FACADE_THICKNESS, 0, 0, 16, 16, 16);
        shapes[Direction.DOWN.ordinal()] = Block.box(0, 0, 0, 16, FACADE_THICKNESS, 16);
        shapes[Direction.UP.ordinal()] = Block.box(0, 16 - FACADE_THICKNESS, 0, 16, 16, 16);

        return shapes;
    }

    private static VoxelShape[] createShapes(int width) {
        double min = (16 - width) / 2d;
        double max = min + width;

        var core = Block.box(min, min, min, max, max, max);

        var arms = new VoxelShape[DIRECTIONS.length];

        arms[Direction.NORTH.ordinal()] = Block.box(min, min, 0, max, max, min);
        arms[Direction.SOUTH.ordinal()] = Block.box(min, min, max, max, max, 16);
        arms[Direction.WEST.ordinal()] = Block.box(0, min, min, min, max, max);
        arms[Direction.EAST.ordinal()] = Block.box(max, min, min, 16, max, max);
        arms[Direction.DOWN.ordinal()] = Block.box(min, 0, min, max, min, max);
        arms[Direction.UP.ordinal()] = Block.box(min, max, min, max, 16, max);

        var shapes = new VoxelShape[1 << DIRECTIONS.length];

        for (int mask = 0; mask < shapes.length; mask++) {
            var shape = core;

            for (var dir : DIRECTIONS) {
                if ((mask & (1 << dir.ordinal())) != 0) {
                    shape = Shapes.or(shape, arms[dir.ordinal()]);
                }
            }

            shapes[mask] = shape;
        }

        return shapes;
    }
}
