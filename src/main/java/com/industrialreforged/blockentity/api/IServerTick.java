package com.industrialreforged.blockentity.api;

/**
 * Block entities that want a server-side tick.
 *
 * <p>The default implementation is a no-op so that a block entity can declare
 * the capability without implementing machinery it does not need yet.
 */
public interface IServerTick {
   default void serverTick() {
   }
}
