package lunneya.btahammers.item;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class ItemExcavator extends ItemToolPickaxe {

	// Сколько блоков инструмент копает влево и вправо.
	private static final int HORIZONTAL_RADIUS = 15;

	// Сколько блоков копает вверх от центрального блока.
	private static final int BLOCKS_UP = 15;

	// Сколько блоков копает вниз от центрального блока.
	private static final int BLOCKS_DOWN = 1;

	public ItemExcavator(
		@NotNull String name,
		@NotNull String namespaceId,
		int id,
		@NotNull ToolMaterial toolMaterial
	) {
		super(name, namespaceId, id, toolMaterial);
	}

	@Override
	public boolean onBlockDestroyed(
		@NotNull ItemStack selfStack,
		@NotNull World world,
		@NotNull Mob mob,
		@NotNull Block<?> removedBlock,
		@NotNull TilePosc blockPos,
		@NotNull Side side
	) {
		// Центральный блок ломается обычной механикой.
		// Прочность инструмента также расходуется здесь один раз.
		boolean result = super.onBlockDestroyed(
			selfStack,
			world,
			mob,
			removedBlock,
			blockPos,
			side
		);

		// Для дополнительного разрушения блоков нужен игрок.
		if (!(mob instanceof Player player)) {
			return result;
		}

		// Shift отключает массовое разрушение.
		// В этом режиме инструмент работает как обычная кирка.
		if (player.isSneaking()) {
			return result;
		}

		breakExcavatorArea(world, player, blockPos, side);

		return result;
	}

	private void breakExcavatorArea(
		@NotNull World world,
		@NotNull Player player,
		@NotNull TilePosc center,
		@NotNull Side side
	) {
		switch (side) {

			// Северная или южная стена.
			// Ширина идёт по X.
			// Высота идёт по Y.
			case NORTH:
			case SOUTH:
				for (int x = -HORIZONTAL_RADIUS; x <= HORIZONTAL_RADIUS; x++) {
					for (int y = -BLOCKS_DOWN; y <= BLOCKS_UP; y++) {

						// Центральный блок уже сломан.
						if (x == 0 && y == 0) {
							continue;
						}

						TilePos targetPos = new TilePos(
							center.x() + x,
							center.y() + y,
							center.z()
						);

						breakExtraBlock(world, player, targetPos, side);
					}
				}
				break;

			// Западная или восточная стена.
			// Ширина идёт по Z.
			// Высота идёт по Y.
			case WEST:
			case EAST:
				for (int z = -HORIZONTAL_RADIUS; z <= HORIZONTAL_RADIUS; z++) {
					for (int y = -BLOCKS_DOWN; y <= BLOCKS_UP; y++) {

						// Центральный блок уже сломан.
						if (z == 0 && y == 0) {
							continue;
						}

						TilePos targetPos = new TilePos(
							center.x(),
							center.y() + y,
							center.z() + z
						);

						breakExtraBlock(world, player, targetPos, side);
					}
				}
				break;

			// При копании пола или потолка
			// массовое разрушение не происходит.
			//
			// Инструмент работает как обычная кирка.
			case TOP:
			case BOTTOM:
			default:
				break;
		}
	}

	private void breakExtraBlock(
		@NotNull World world,
		@NotNull Player player,
		@NotNull TilePos targetPos,
		@NotNull Side side
	) {
		Block<?> block = world.getBlockType(targetPos);

		// Воздух пропускаем.
		if (block == Blocks.AIR) {
			return;
		}

		// Не ломаем неразрушаемые блоки вроде bedrock.
		if (block.getHardness() < 0.0F) {
			return;
		}

		int data = world.getBlockData(targetPos);
		TileEntity tileEntity = world.getTileEntity(targetPos);

		// Удаляем блок из мира.
		if (!world.setBlockTypeNotify(targetPos, Blocks.AIR)) {
			return;
		}

		// Повторяем стандартную логику разрушения блока игроком.
		block.onDestroyedByPlayer(
			world,
			targetPos,
			side,
			data,
			player,
			this
		);

		// Создаём обычный дроп блока.
		if (player.getGamemode().hasItemDrops()) {
			block.onHarvest(
				world,
				player,
				targetPos,
				data,
				tileEntity
			);
		}
	}
}
