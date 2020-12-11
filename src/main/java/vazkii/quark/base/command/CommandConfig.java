/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [15/07/2016, 05:22:28 (GMT)]
 */
package vazkii.quark.base.command;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageChangeConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandConfig extends CommandBase {

	private final Multimap<String, ConfigPropertyDescriptor> configKeys = HashMultimap.create();

	private final List<String> categoryNames = Lists.newArrayList();
	private final Multimap<String, String> keyNames = HashMultimap.create();

	public CommandConfig() {
		Configuration config = ModuleLoader.config;

		List<ConfigCategoryDescriptor> toProcess = config.getCategoryNames().stream()
				.map((name) -> new ConfigCategoryDescriptor(null, config, name))
				.collect(Collectors.toList());

		while (!toProcess.isEmpty()) {
			ConfigCategoryDescriptor key = toProcess.remove(0);
			ConfigCategory category = key.category();

			if (category != null) {
				String fullKey = key.getFullKey();

				categoryNames.add(fullKey);

				for (Property property : category.getOrderedValues()) {
					keyNames.put(fullKey, property.getName());
					configKeys.put(fullKey, new ConfigPropertyDescriptor(property.getName(), property.getType()));
				}

				for (String subKey : key.subCategories())
					toProcess.add(new ConfigCategoryDescriptor(category, config, subKey));
			}
		}
	}
	
	@Nonnull
	@Override
	public String getName() {
		return "quark-config";
	}

	@Nonnull
	@Override
	public List<String> getAliases() {
		return Collections.singletonList("quarkconfig");
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender) {
		return "commands.quarkconfig.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;	
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
		if(args.length < 3)
			 throw new WrongUsageException(getUsage(sender));
		
		boolean save = args.length >= 5 && args[4].equals("save");

		String category = args[0];
		String key = args[1];
		String value = args[2];

		if (!configKeys.containsKey(category))
			throw new CommandException("commands.quarkconfig.invalid_category", category);

		Optional<ConfigPropertyDescriptor> descriptor = configKeys.get(category).stream()
				.filter((desc) -> desc.key.equals(key))
				.findFirst();

		if (descriptor.isPresent()) {
			Property.Type type = descriptor.get().type;
			GlobalConfig.changeConfig(category, key, value, type, save);

			EntityPlayerMP player = args.length >= 4 ? getPlayer(server, sender, args[3]) : null;

			if (player != null) {
				NetworkHandler.INSTANCE.sendTo(new MessageChangeConfig(category, key, value, type, save), player);
			} else NetworkHandler.INSTANCE.sendToAll(new MessageChangeConfig(category, key, value, type, false));
		} else throw new CommandException("commands.quarkconfig.invalid_property", category, key);
	}

	@Nonnull
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, categoryNames);
		else if (args.length == 2) {
			Collection<String> keyNameSet = keyNames.get(args[0]);
			if (keyNameSet == null)
				return Collections.emptyList();
			return getListOfStringsMatchingLastWord(args, keyNameSet);
		} else if (args.length == 4)
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());

		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 3;
	}
}
