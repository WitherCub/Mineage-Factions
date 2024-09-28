package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypeAbstract;
import com.massivecraft.massivecore.comparator.ComparatorCaseInsensitive;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class TypeFactionWarp extends TypeAbstract<String>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeFactionWarp i = new TypeFactionWarp();
	public static TypeFactionWarp get() { return i; }
	public TypeFactionWarp() { super(String.class); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String read(String str, CommandSender sender) throws MassiveException
	{
		String ret;
		
		// Faction and Player
		Player player = (Player) sender;
		MPlayer mPlayer = MPlayer.get(player);
		Faction faction = mPlayer.getFaction();
		
		// Faction Name Exact
		ret = faction.getAllWarps().contains(str) ? str : null;
		if (ret != null) return ret;
		
		throw new MassiveException().addMsg("<b>%s<b> does not have a faction warp with the name \"<p>%s<b>\".", faction.describeTo(mPlayer, true), str);
	}
	
	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		// Create
		Set<String> ret = new TreeSet<>(ComparatorCaseInsensitive.get());
		
		// Only allow players to tab region names
		if (sender instanceof Player)
		{
			// Player and Faction
			Player player = (Player) sender;
			MPlayer mPlayer = MPlayer.get(player);
			Faction faction = mPlayer.getFaction();
			
			if (!faction.isNone()) {
				// Fill
				ret.addAll(faction.getAllWarps());
			}
		}
		
		// Return
		return ret;
	}
}
