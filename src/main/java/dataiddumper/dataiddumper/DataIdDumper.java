package dataiddumper.dataiddumper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DataIdDumper implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("dataiddumper");
	public static final String workingDirectory = System.getProperty("user.dir");
	public static final String directoryPath = workingDirectory + "/data_dump"; // or any desired subdirectory

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			try {
				Files.createDirectories(Paths.get(directoryPath));
				printAllItemIDs();
				printAllTheRestIDs(server);
				LOGGER.info("All IDs have been printed successfully!");
			} catch (IOException e) {
				LOGGER.info("Failed to print all IDs");
				throw new RuntimeException(e);
			}
		});

		LOGGER.info("Hello Fabric world!");
	}

	public void printAllItemIDs() {
		// Create directory for items
		String directoryPathItems = directoryPath + "/items";
		try {
			Files.createDirectories(Paths.get(directoryPathItems));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Map to store file paths and corresponding identifier sets
		Map<String, Set<Identifier>> identifiersMap = new HashMap<>();

		// Items
		identifiersMap.put(directoryPath + "/items/items_ids.txt", filterItemIds(item -> !(item instanceof BlockItem) && item.getFoodComponent() == null && !item.getTranslationKey().contains("spawn_egg")));

		// Blocks
		identifiersMap.put(directoryPath + "/items/blocks_ids.txt", filterItemIds(item -> item instanceof BlockItem));

		// Edible items
		identifiersMap.put(directoryPath + "/items/edible_ids.txt", filterItemIds(item -> item.getFoodComponent() != null));

		// Spawn eggs
		identifiersMap.put(directoryPath + "/items/spawn_eggs_ids.txt", filterItemIds(item -> item.getTranslationKey().contains("spawn_egg")));

		// Write identifiers to files
		writeIdentifiersToFiles(identifiersMap);
	}

	public void printAllTheRestIDs(MinecraftServer server) {
		// Map to store file paths and corresponding identifier sets
		Map<String, Set<Identifier>> identifiersMap = new HashMap<>();

		// Entities
		identifiersMap.put(directoryPath + "/entities_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.ENTITY_TYPE).getIds()));
		// Biomes
		identifiersMap.put(directoryPath + "/biomes_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.BIOME).getIds()));
		// Structures
		identifiersMap.put(directoryPath + "/structures_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.STRUCTURE_TYPE).getIds()));
		// Enchantments
		identifiersMap.put(directoryPath + "/enchantments_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIds()));
		// Recipes
		identifiersMap.put(directoryPath + "/recipes_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.RECIPE_TYPE).getIds()));
		// Fluids
		identifiersMap.put(directoryPath + "/fluids_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.FLUID).getIds()));
		// Attributes
		identifiersMap.put(directoryPath + "/attributes_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.ATTRIBUTE).getIds()));
		// Stats
		identifiersMap.put(directoryPath + "/stats_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.STAT_TYPE).getIds()));
		// Block Entities
		identifiersMap.put(directoryPath + "/block_entities_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.BLOCK_ENTITY_TYPE).getIds()));
		// Particles
		identifiersMap.put(directoryPath + "/particles_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.PARTICLE_TYPE).getIds()));
		// Potions
		identifiersMap.put(directoryPath + "/items" + "/potions_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.POTION).getIds()));
		// Dimensions
		identifiersMap.put(directoryPath + "/dimensions_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.DIMENSION).getIds()));
		// Damage Types
		identifiersMap.put(directoryPath + "/damage_types_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getIds()));
		// Loot Pool Entry Types
		identifiersMap.put(directoryPath + "/loot_pool_entry_type_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.LOOT_POOL_ENTRY_TYPE).getIds()));
		// Villager Professions
		identifiersMap.put(directoryPath + "/villager_professions_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.VILLAGER_PROFESSION).getIds()));

		// Status Effects
		identifiersMap.put(directoryPath + "/status_effects_neutral_ids.txt", filterStatusEffects(server, StatusEffectCategory.NEUTRAL));
		identifiersMap.put(directoryPath + "/status_effects_beneficial_ids.txt", filterStatusEffects(server, StatusEffectCategory.BENEFICIAL));
		identifiersMap.put(directoryPath + "/status_effects_harmful_ids.txt", filterStatusEffects(server, StatusEffectCategory.HARMFUL));

		// Write identifiers to files
		writeIdentifiersToFiles(identifiersMap);
	}

	private Set<Identifier> filterItemIds(ItemFilter filter) {
		Set<Identifier> itemIds = new HashSet<>(Registries.ITEM.getIds());
		Iterator<Identifier> iterator = itemIds.iterator();
		while (iterator.hasNext()) {
			Identifier id = iterator.next();
			Item item = Registries.ITEM.get(id);
			if (!filter.accept(item)) {
				iterator.remove();
			}
		}
		return itemIds;
	}

	private Set<Identifier> filterStatusEffects(MinecraftServer server, StatusEffectCategory category) {
		Set<Identifier> statusEffectIds = new HashSet<>(Registries.STATUS_EFFECT.getIds());
		Iterator<Identifier> iterator = statusEffectIds.iterator();
		while (iterator.hasNext()) {
			Identifier id = iterator.next();
			StatusEffect effect = Registries.STATUS_EFFECT.get(id);
			if (effect == null || effect.getCategory() != category) {
				iterator.remove();
			}
		}
		return statusEffectIds;
	}

	private void writeIdentifiersToFiles(Map<String, Set<Identifier>> identifiersMap) {
		for (Map.Entry<String, Set<Identifier>> entry : identifiersMap.entrySet()) {
			String filePath = entry.getKey();
			Set<Identifier> ids = entry.getValue();

			// Convert set to sorted list
			List<Identifier> sortedIds = new ArrayList<>(ids);
			sortedIds.sort(Comparator.comparing(Identifier::toString));

			// Write to file
			try (FileWriter writer = new FileWriter(filePath)) {
				for (Identifier id : sortedIds) {
					writer.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Functional interface for item filtering
	private interface ItemFilter {
		boolean accept(Item item);
	}
}
