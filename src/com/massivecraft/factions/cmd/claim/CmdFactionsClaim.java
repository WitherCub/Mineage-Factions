package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;

import java.util.Collections;

public class CmdFactionsClaim extends FactionsCommand
{
	private static CmdFactionsClaim i = new CmdFactionsClaim();
	public static CmdFactionsClaim get() { return i; }
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsSetOne cmdFactionsClaimOne = new CmdFactionsSetOne(true);
	public CmdFactionsSetLine cmdFactionsClaimLine = new CmdFactionsSetLine(true);
	public CmdFactionsSetAuto cmdFactionsClaimAuto = new CmdFactionsSetAuto(true);
	public CmdFactionsSetFill cmdFactionsClaimFill = new CmdFactionsSetFill(true);
	public CmdFactionsSetSquare cmdFactionsClaimSquare = new CmdFactionsSetSquare(true);
	public CmdFactionsSetCircle cmdFactionsClaimCircle = new CmdFactionsSetCircle(true);
	public CmdFactionsSetAll cmdFactionsClaimAll = new CmdFactionsSetAll(true);
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		cmdFactionsClaimOne.execute(sender, Collections.<String>emptyList());
		Mson moreInfo = Mson.mson(mson("Click here to see other options for claiming land.").color(ChatColor.YELLOW).command("/f claim ?"));
		msender.message(moreInfo);
	}
	
}
