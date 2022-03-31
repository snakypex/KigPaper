package org.bukkit.block;

import org.bukkit.World;

/**
 * Overwritten to only use the methods we need
 */
public interface Block {
    int getX();

    int getY();

    int getZ();

    World getWorld();
}
