package net.aufdemrand.denizen.scripts.commands.entity;

import java.util.Arrays;
import java.util.List;

import net.aufdemrand.denizen.exceptions.CommandExecutionException;
import net.aufdemrand.denizen.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizen.objects.aH;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dList;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.aufdemrand.denizen.scripts.commands.AbstractCommand;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.debugging.dB.Messages;

/**
 * Teleports a list of entities to a location.
 *
 * @author David Cernat
 */

public class TeleportCommand extends AbstractCommand {
	
    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        // Initialize necessary fields

    	for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {
        	
    		if (!scriptEntry.hasObject("location")
                    && arg.matchesArgumentType(dLocation.class)) {
                // Location arg
                scriptEntry.addObject("location", arg.asType(dLocation.class));
            }
            
        	else if (!scriptEntry.hasObject("entities")
                	&& arg.matchesPrefix("entity, entities, e, target, targets, t")) {
                // Entity arg
                scriptEntry.addObject("entities", ((dList) arg.asType(dList.class)).filter(dEntity.class));
            }
    		
        	else if (arg.matches("npc") && scriptEntry.hasNPC()) {
                // NPC arg for compatibility with old scripts
                scriptEntry.addObject("entities", Arrays.asList(scriptEntry.getNPC().getDenizenEntity()));
            }
        }
    	
    	// Check to make sure required arguments have been filled
        
        if (!scriptEntry.hasObject("entities"))
        	
        	// Teleport the player if no entity was specified
        	if (scriptEntry.hasPlayer()) {
        		scriptEntry.addObject("entities",
        				Arrays.asList(scriptEntry.getPlayer().getDenizenEntity()));
        	}
        	else {
        		throw new InvalidArgumentsException(Messages.ERROR_MISSING_OTHER, "ENTITIES");
        	}
    }
    
	@SuppressWarnings("unchecked")
	@Override
    public void execute(final ScriptEntry scriptEntry) throws CommandExecutionException {
        // Get objects
    	
		dLocation location = (dLocation) scriptEntry.getObject("location");
		List<dEntity> entities = (List<dEntity>) scriptEntry.getObject("entities");
		
        // Report to dB
        dB.report(getName(), aH.debugObj("location", location) +
        					 aH.debugObj("entities", entities.toString()));

		for (dEntity entity : entities) {
			if (entity.isSpawned() == true) {
				entity.teleport(location);
			}
	    }
	}
}