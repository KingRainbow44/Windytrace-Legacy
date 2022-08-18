package lol.magix.windtrace.utils;

import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Position;

public final class GadgetUtils {
    private GadgetUtils() {
        // No construction.
    }

    /**
     * Creates a gadget.
     * @param player The player to create the gadget for.
     * @param gadgetId The ID of the gadget.
     * @param position The position to create the gadget at.
     * @param rotation The rotation to create the gadget at.
     */
    public static EntityVehicle createGadget(Player player, int gadgetId,
                                          Position position, Position rotation) {
        // Create a new game entity.
        var entity = new EntityVehicle(player.getScene(), player, gadgetId, 0, position, rotation);

        player.getScene().addEntity(entity); // Add the entity to the scene.
        return entity; // Return the entity.
    }

    /**
     * Destroys a gadget.
     * @param entity The gadget entity to destroy.
     */
    public static void removeGadget(EntityVehicle entity) {
        entity.getScene().killEntity(entity, 1);
        entity.getScene().removeEntity(entity);
    }
}