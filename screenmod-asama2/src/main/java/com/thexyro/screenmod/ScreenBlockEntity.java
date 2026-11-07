package com.thexyro.screenmod;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Ekran blogunun verisi: gosterilen resim (base64 JPEG) ve en-boy orani ayari. */
public class ScreenBlockEntity extends BlockEntity {
	private String image = null;
	private boolean keepAspect = true;

	public ScreenBlockEntity(BlockPos pos, BlockState state) {
		super(ScreenMod.SCREEN_BLOCK_ENTITY, pos, state);
	}

	public String getImage() {
		return image;
	}

	public boolean isKeepAspect() {
		return keepAspect;
	}

	/** Sadece sunucuda cagrilir. null = resmi kaldir. */
	public void setImage(String base64) {
		this.image = base64;
		syncToClients();
	}

	/** Sadece sunucuda cagrilir. */
	public void setKeepAspect(boolean keepAspect) {
		this.keepAspect = keepAspect;
		syncToClients();
	}

	private void syncToClients() {
		this.setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	public void loadAdditional(ValueInput data) {
		super.loadAdditional(data);
		this.image = data.read("image", Codec.STRING).orElse(null);
		this.keepAspect = data.read("keep_aspect", Codec.BOOL).orElse(true);
	}

	@Override
	public void saveAdditional(ValueOutput data) {
		super.saveAdditional(data);
		if (this.image != null) {
			data.store("image", Codec.STRING, this.image);
		}
		data.store("keep_aspect", Codec.BOOL, this.keepAspect);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
		return this.saveCustomOnly(lookup);
	}
}
