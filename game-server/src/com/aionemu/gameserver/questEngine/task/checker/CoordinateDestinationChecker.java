package com.aionemu.gameserver.questEngine.task.checker;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer
 * @modified Neon
 */
public class CoordinateDestinationChecker extends DestinationChecker {

	protected final float x;
	protected final float y;
	protected final float z;

	public CoordinateDestinationChecker(Creature follower, float x, float y, float z) {
		super(follower);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean check() {
		return MathUtil.isNearCoordinates(follower, x, y, z, 20);
	}
}
