package dataiddumper.dataiddumper;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
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
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
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
				LOGGER.info("All IDs have been failed to print");
				throw new RuntimeException(e);
			}

			;
		});
		LOGGER.info("Hello Fabric world!");
	}
		public void printAllItemIDs() {

			String directoryPathItems = directoryPath + "/items";

			try {
				Files.createDirectories(Paths.get(directoryPathItems));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			//Items Start
			String filePathItems = directoryPath + "/items" + "/items_ids.txt";
			Set<Identifier> ItemsIds = new HashSet<>(Registries.ITEM.getIds());
			try (FileWriter writerEntities = new FileWriter(filePathItems)) {
				for (Identifier id : ItemsIds) {
					Item item = Registries.ITEM.get(id);
					if (!(item instanceof BlockItem) && item.getFoodComponent() == null && !id.getPath().contains("spawn_egg"))
						writerEntities.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Items End

			//Blocks Start
			String filePathBlocks = directoryPath + "/items" + "/blocks_ids.txt";
			Set<Identifier> BlocksIds = new HashSet<>(Registries.ITEM.getIds());
			try (FileWriter writerEntities = new FileWriter(filePathBlocks)) {
				for (Identifier id : BlocksIds) {
					Item item = Registries.ITEM.get(id);
					if (item instanceof BlockItem)
						writerEntities.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Blocks End

			//Edible Start
			String filePathEdible = directoryPath + "/items" + "/edible_ids.txt";
			Set<Identifier> EdibleIds = new HashSet<>(Registries.ITEM.getIds());
			try (FileWriter writerEdible = new FileWriter(filePathEdible)) {
				for (Identifier id : EdibleIds) {
					Item item = Registries.ITEM.get(id);
					if (item.getFoodComponent() != null)
						writerEdible.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Edible End

			//SpawnEggs Start
			String filePathSpawnEggs = directoryPath + "/items" + "/spawn_eggs_ids.txt";
			Set<Identifier> SpawnEggsIds = new HashSet<>(Registries.ITEM.getIds());
			try (FileWriter writerEdible = new FileWriter(filePathSpawnEggs)) {
				for (Identifier id : SpawnEggsIds) {
					Item item = Registries.ITEM.get(id);
					if (id.getPath().contains("spawn_egg"))
						writerEdible.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//SpawnEggs End
		}
		public void printAllTheRestIDs(MinecraftServer server) {

			// Map to store file paths and corresponding identifier sets
			Map<String, Set<Identifier>> identifiersMap = new HashMap<>();
			identifiersMap.put(directoryPath + "/entities_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.ENTITY_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/biomes_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.BIOME).getIds()));
			identifiersMap.put(directoryPath + "/structures_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.STRUCTURE_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/enchantments_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIds()));
			identifiersMap.put(directoryPath + "/recipes_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.RECIPE_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/fluids_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.FLUID).getIds()));
			identifiersMap.put(directoryPath + "/attributes_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.ATTRIBUTE).getIds()));
			identifiersMap.put(directoryPath + "/stats_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.STAT_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/block_entities_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.BLOCK_ENTITY_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/particles_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.PARTICLE_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/items" + "/potions_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.POTION).getIds()));
			identifiersMap.put(directoryPath + "/dimensions_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.DIMENSION).getIds()));
			identifiersMap.put(directoryPath + "/damage_types_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/loot_pool_entry_type_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.LOOT_POOL_ENTRY_TYPE).getIds()));
			identifiersMap.put(directoryPath + "/villager_professions_ids.txt", new HashSet<>(server.getRegistryManager().get(RegistryKeys.VILLAGER_PROFESSION).getIds()));
			// Add more paths and sets here if needed

			// Loop through the map and write the IDs to the files in ascending order
			for (Map.Entry<String, Set<Identifier>> entry : identifiersMap.entrySet()) {
				String filePath = entry.getKey();
				Set<Identifier> ids = entry.getValue();
				// Convert set to sorted list
				List<Identifier> sortedIds = new ArrayList<>(ids);
				sortedIds.sort(Comparator.comparing(Identifier::toString));
				try (FileWriter writer = new FileWriter(filePath)) {
					for (Identifier id : sortedIds) {
						writer.write(id.toString() + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//StatusEffectsNeutral Start
			String filePathStatusEffectsNeutral = directoryPath + "/status_effects_neutral_ids.txt";
			Set<Identifier> StatusEffectsNeutralIds = new HashSet<>(Registries.STATUS_EFFECT.getIds());
			try (FileWriter writerStatusEffectsNeutral = new FileWriter(filePathStatusEffectsNeutral)) {
				for (Identifier id : StatusEffectsNeutralIds) {
					StatusEffect effect = Registries.STATUS_EFFECT.get(id);
					if (effect != null && effect.getCategory() == StatusEffectCategory.NEUTRAL)
						writerStatusEffectsNeutral.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//StatusEffectsNeutral End

			//StatusEffectsBeneficial Start
			String filePathStatusEffectsBeneficial = directoryPath + "/status_effects_beneficial_ids.txt";
			Set<Identifier> StatusEffectsBeneficialIds = new HashSet<>(Registries.STATUS_EFFECT.getIds());
			try (FileWriter writerStatusEffectsBeneficial = new FileWriter(filePathStatusEffectsBeneficial)) {
				for (Identifier id : StatusEffectsBeneficialIds) {
					StatusEffect effect = Registries.STATUS_EFFECT.get(id);
					if (effect != null && effect.getCategory() == StatusEffectCategory.BENEFICIAL)
						writerStatusEffectsBeneficial.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//StatusEffectsBeneficial End

			//StatusEffectsHarmful Start
			String filePathStatusEffectsHarmful = directoryPath + "/status_effects_harmful_ids.txt";
			Set<Identifier> StatusEffectsHarmfulIds = new HashSet<>(Registries.STATUS_EFFECT.getIds());
			try (FileWriter writerStatusEffectsHarmful = new FileWriter(filePathStatusEffectsHarmful)) {
				for (Identifier id : StatusEffectsHarmfulIds) {
					StatusEffect effect = Registries.STATUS_EFFECT.get(id);
					if (effect != null && effect.getCategory() == StatusEffectCategory.HARMFUL)
						writerStatusEffectsHarmful.write(id.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//StatusEffectsHarmful End

		}
	}
