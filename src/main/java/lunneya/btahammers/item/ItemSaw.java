package lunneya.btahammers.item;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.data.gamerule.TreecapitatorHelper;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class ItemSaw extends ItemToolAxe {

	public ItemSaw(
		@NotNull String name,
		@NotNull String namespaceId,
		int id,
		@NotNull ToolMaterial toolMaterial
	) {
		super(name, namespaceId, id, toolMaterial);
	}

	@Override
	public boolean beforeBlockDestroyed(
		@NotNull ItemStack selfStack,
		@NotNull World world,
		@NotNull Player player,
		@NotNull Block<?> block,
		@NotNull TilePosc blockPos,
		@NotNull Side side
	) {
		if (!world.isClientSide && !player.isSneaking()) {

			Block<?> blockType = world.getBlockType(blockPos);

			if (Block.hasLogicClass(blockType, BlockLogicLog.class)) {
				return !(new TreecapitatorHelper(
					world,
					blockPos.x(),
					blockPos.y(),
					blockPos.z(),
					player
				)).chopTree();
			}
		}

		return true;
	}
}
