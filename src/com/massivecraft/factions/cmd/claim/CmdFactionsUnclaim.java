package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;

import java.util.Collections;

public class CmdFactionsUnclaim extends FactionsCommand
{
	private static CmdFactionsUnclaim i = new CmdFactionsUnclaim();
	public static CmdFactionsUnclaim get() { return i; }
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsSetOne cmdFactionsUnclaimOne = new CmdFactionsSetOne(false);
	public CmdFactionsSetLine cmdFactionsUnclaimLine = new CmdFactionsSetLine(false);
	public CmdFactionsSetAuto cmdFactionsUnclaimAuto = new CmdFactionsSetAuto(false);
	public CmdFactionsSetFill cmdFactionsUnclaimFill = new CmdFactionsSetFill(false);
	public CmdFactionsSetSquare cmdFactionsUnclaimSquare = new CmdFactionsSetSquare(false);
	public CmdFactionsSetCircle cmdFactionsUnclaimCircle = new CmdFactionsSetCircle(false);
	public CmdFactionsSetAll cmdFactionsUnclaimAll = new CmdFactionsSetAll(false);
	
	@Override
	public void perform() throws MassiveException
	{
		cmdFactionsUnclaimOne.execute(sender, Collections.<String>emptyList());
		Mson moreInfo = Mson.mson(mson("Click here to see other options for unclaiming land.").color(ChatColor.YELLOW).command("/f unclaim ?"));
		msender.message(moreInfo);
	}
	
}
