package net.sacredlabyrinth.Phaed.PreciousStones.listeners;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.field.Field;
import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.ChunkVec;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * PreciousStones world listener
 *
 * @author Phaed
 */
public class PSWorldListener implements Listener {
    private final PreciousStones plugin;

    /**
     *
     */
    public PSWorldListener() {
        plugin = PreciousStones.getInstance();
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onChunkUnload(ChunkUnloadEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();

        if (plugin.getSettingsManager().isBlacklistedWorld(world)) {
            return;
        }

        List<Field> fields = plugin.getForceFieldManager().getSourceFieldsInChunk(new ChunkVec(event.getChunk()), FieldFlag.KEEP_CHUNKS_LOADED);
        // todo find location where it should be marked
        if (!fields.isEmpty()) {
                chunk.setForceLoaded(true);
            // event.setCancelled(true);
        }
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        if (plugin.getSettingsManager().isBlacklistedWorld(world)) {
            return;
        }

        plugin.getStorageManager().loadWorldFields(world);
        plugin.getStorageManager().loadWorldUnbreakables(world);
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPortalCreate(PortalCreateEvent event) {
        List<BlockState> blocks = event.getBlocks();

        if (event.getReason().equals(PortalCreateEvent.CreateReason.FIRE)) {
            Field field = plugin.getForceFieldManager().getEnabledSourceField(blocks.get(0).getLocation(), FieldFlag.PREVENT_PORTAL_CREATION);

            if (field != null) {
                event.setCancelled(true);
            }
        }

        if (event.getReason().equals(PortalCreateEvent.CreateReason.NETHER_PAIR)) {
            Field field = plugin.getForceFieldManager().getEnabledSourceField(blocks.get(0).getLocation(), FieldFlag.PREVENT_PORTAL_DESTINATION);

            if (field != null) {
                event.setCancelled(true);
            }
        }
    }
}
