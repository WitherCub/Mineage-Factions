package com.massivecraft.factions.cmd.type;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypeAbstract;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public class TypeStatistic extends TypeAbstract<String>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeStatistic i = new TypeStatistic();
	
	private List<String> statisticTypes = MUtil.list("blocksBroken", "blocksPlaced", "playersKilled", "mobsKilled", "deaths", "fishCaught", "timePlayed");
	
	public TypeStatistic()
	{
		super(String.class);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	public static TypeStatistic get()
	{
		return i;
	}
	
	@Override
	public String read(String str, CommandSender sender) throws MassiveException
	{
		String ret = null;
		
		if (statisticTypes.contains(str)) ret = str;
		if (ret != null) return ret;
		
		throw new MassiveException().addMsg("<b>No statistic found matching \"<p>%s<b>\"<b>. Use tab-complete to autofill statistic names.", str);
	}
	
	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		// Return
		return statisticTypes;
	}
}
