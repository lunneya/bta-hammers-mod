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

public class ItemHammer extends ItemToolPickaxe {

	// Радиус области разрушения:
	// 1 = 3x3
	// 2 = 5x5
	private final int radius;

	public ItemHammer(
		@NotNull String name,
		@NotNull String namespaceId,
		int id,
		@NotNull ToolMaterial toolMaterial,
		int radius
	) {
		super(name, namespaceId, id, toolMaterial);
		this.radius = radius;
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
		// Обычный износ инструмента — только один раз
		// за центральный блок.
		boolean result = super.onBlockDestroyed(
			selfStack,
			world,
			mob,
			removedBlock,
			blockPos,
			side
		);

		// Блоки ломает игрок, поэтому для дропа нам нужен Player.
		if (!(mob instanceof Player player)) {
			return result;
		}

		breakArea(world, player, blockPos, side);

		return result;
	}

	private void breakArea(
		@NotNull World world,
		@NotNull Player player,
		@NotNull TilePosc center,
		@NotNull Side side
	) {
		for (int a = -radius; a <= radius; a++) {
			for (int b = -radius; b <= radius; b++) {

				// Центральный блок уже сломан обычной механикой игры.
				if (a == 0 && b == 0) {
					continue;
				}

				TilePos targetPos;

				switch (side) {
					// Пол или потолок:
					// ломаем плоскость X/Z.
					case TOP:
					case BOTTOM:
						targetPos = new TilePos(
							center.x() + a,
							center.y(),
							center.z() + b
						);
						break;

					// Северная или южная стена:
					// ломаем плоскость X/Y.
					case NORTH:
					case SOUTH:
						targetPos = new TilePos(
							center.x() + a,
							center.y() + b,
							center.z()
						);
						break;

					// Западная или восточная стена:
					// ломаем плоскость Y/Z.
					case WEST:
					case EAST:
						targetPos = new TilePos(
							center.x(),
							center.y() + a,
							center.z() + b
						);
						break;

					default:
						continue;
				}

				breakExtraBlock(world, player, targetPos, side);
			}
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

		// Не ломаем принципиально неразрушаемые блоки
		// вроде bedrock.
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

		// Обычный дроп, если текущий режим игры его предусматривает.
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
