package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.access.CmdFactionsAccess;
import com.massivecraft.factions.cmd.access.CmdFactionsAccessall;
import com.massivecraft.factions.cmd.alt.CmdFactionsAltjoin;
import com.massivecraft.factions.cmd.alt.CmdFactionsAlts;
import com.massivecraft.factions.cmd.chest.CmdFactionsChest;
import com.massivecraft.factions.cmd.claim.CmdFactionsClaim;
import com.massivecraft.factions.cmd.claim.CmdFactionsClaimmap;
import com.massivecraft.factions.cmd.claim.CmdFactionsUnclaim;
import com.massivecraft.factions.cmd.claim.CmdFactionsUnclaimall;
import com.massivecraft.factions.cmd.credits.CmdFactionsCredits;
import com.massivecraft.factions.cmd.flag.CmdFactionsFlag;
import com.massivecraft.factions.cmd.money.CmdFactionsMoney;
import com.massivecraft.factions.cmd.perm.CmdFactionsPerm;
import com.massivecraft.factions.cmd.powerboost.CmdFactionsPowerboost;
import com.massivecraft.factions.cmd.pperm.CmdFactionsPperm;
import com.massivecraft.factions.cmd.relations.CmdFactionsRelation;
import com.massivecraft.factions.cmd.relations.CmdFactionsRelationOld;
import com.massivecraft.factions.cmd.tnt.CmdFactionsTnt;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.command.MassiveCommandDeprecated;

import java.util.List;

public class CmdFactions extends FactionsCommand
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static CmdFactions i = new CmdFactions();

	public CmdFactions()
	{
		this.addChild(new CmdFactionsFaction());
		this.addChild(new CmdFactionsPlayer());
		this.addChild(new CmdFactionsJoin());
		this.addChild(CmdFactionsLeave.get());
		this.addChild(CmdFactionsHome.get());
		this.addChild(new CmdFactionsStealth());
		this.addChild(new CmdFactionsClaimmap());
		this.addChild(new CmdFactionsFriendlyfire());
		this.addChild(new CmdFactionsSetwarp());
		this.addChild(CmdFactionsWarp.get());
		this.addChild(new CmdFactionsDeletewarp());
		this.addChild(new CmdFactionsListwarps());
		this.addChild(new CmdFactionsCredits());
		this.addChild(new CmdFactionsUpgrade());
		this.addChild(new CmdFactionsChest());
		this.addChild(new CmdFactionsTnt());
		this.addChild(CmdFactionsInspect.get());
		this.addChild(new CmdFactionsChat());
		this.addChild(new CmdFactionsStats());
		this.addChild(new CmdFactionsStatslb());
		this.addChild(new CmdFactionsGetbanner());
		this.addChild(new CmdFactionsSetbanner());
		this.addChild(CmdFactionsAssist.get());
		this.addChild(CmdFactionsMap.get());
		this.addChild(CmdFactionsCreate.get());
		this.addChild(CmdFactionsName.get());
		this.addChild(CmdFactionsDescription.get());
		this.addChild(new CmdFactionsMotd());
		this.addChild(CmdFactionsSethome.get());
		this.addChild(new CmdFactionsUnsethome());
		this.addChild(CmdFactionsInvite.get());
		this.addChild(CmdFactionsDeinvite.get());
		this.addChild(new CmdFactionsListinvites());
		this.addChild(new CmdFactionsKick());
		this.addChild(CmdFactionsTitle.get());
		this.addChild(CmdFactionsRank.get());
		this.addChild(new CmdFactionsRankOld("leader"));
		this.addChild(new CmdFactionsRankOld("owner"));
		this.addChild(new CmdFactionsRankOld("coleader"));
		this.addChild(new CmdFactionsRankOld("officer"));
		this.addChild(new CmdFactionsRankOld("moderator"));
		this.addChild(new CmdFactionsRankOld("member"));
		this.addChild(new CmdFactionsRankOld("recruit"));
		this.addChild(new CmdFactionsRankOld("promote"));
		this.addChild(new CmdFactionsRankOld("demote"));
		this.addChild(new CmdFactionsMoney());
		this.addChild(new CmdFactionsSeechunk());
		this.addChild(new CmdFactionsSeechunkold());
		this.addChild(new CmdFactionsTerritorytitles());
		this.addChild(new CmdFactionsStatus());
		this.addChild(new CmdFactionsAccess());
		this.addChild(new CmdFactionsRelation());
		this.addChild(new CmdFactionsRelationOld("ally"));
		this.addChild(new CmdFactionsRelationOld("truce"));
		this.addChild(new CmdFactionsRelationOld("neutral"));
		this.addChild(new CmdFactionsRelationOld("enemy"));
		this.addChild(new CmdFactionsPerm());
		this.addChild(new CmdFactionsUnstuck());
		this.addChild(CmdFactionsOverride.get());
		this.addChild(new CmdFactionsListclaims());
		this.addChild(new CmdFactionsDisband());
		this.addChild(new CmdFactionsPowerboost());
		this.addChild(new CmdFactionsSetpower());
		this.addChild(new CmdFactionsConfig());
		this.addChild(new CmdFactionsClean());
		this.addChild(CmdFactionsCheck.get());
		this.addChild(new CmdFactionsAlarm());
		this.addChild(new CmdFactionsClear());
		this.addChild(new CmdFactionsBan());
		this.addChild(new CmdFactionsUnban());
		this.addChild(new CmdFactionsBanlist());
		this.addChild(new CmdFactionsCoords());
		this.addChild(new CmdFactionsIgnore());
		this.addChild(new CmdFactionsUnignore());
		this.addChild(new CmdFactionsIgnorelist());
		this.addChild(new CmdFactionsMission());
		this.addChild(new CmdFactionsAltjoin());
		this.addChild(new CmdFactionsAlts());
		this.addChild(new CmdFactionsAccessall());
		this.addChild(new CmdFactionsPperm());
		this.addChild(new CmdFactionsFly());
		this.addChild(new CmdFactionsSetdiscord());
		this.addChild(new CmdFactionsSetwarpitem());
		this.addChild(new CmdFactionsUnclaimall());
		this.addChild(new CmdFactionsDrain());
		this.addChild(new CmdFactionsDrainlist());
		this.addChild(new CmdFactionsDraintoggle());
		this.addChild(new CmdFactionsReserve());
		this.addChild(new CmdFactionsUnreserve());
		this.addChild(new CmdFactionsLogins());
		this.addChild(CmdFactionsShield.get());
		this.addChild(new CmdFactionsShieldtoggle());
		this.addChild(new CmdFactionsRaidtimer());
		this.addChild(new CmdFactionsRaids());
		this.addChild(new CmdFactionsRoster());
		this.addChild(new CmdFactionsCorner());
		this.addChild(new CmdFactionsCornerlist());
		this.addChild(new CmdFactionsGrace());
		this.addChild(CmdFactionsRaidclaim.get());
		this.addChild(new CmdFactionsRaidinfo());
		this.addChild(new CmdFactionsBreachlog());
		this.addChild(new CmdFactionsSandbots());
		this.addChild(CmdFactionsUnclaim.get());
		this.addChild(new CmdFactionsList());

		this.addChild(new CmdFactionsSetBaseRegion());

		CmdFactionsClaim claim = CmdFactionsClaim.get();
		CmdFactionsFlag flag = CmdFactionsFlag.get();
		this.addChild(claim);
		this.addChild(flag);
		// Deprecated Commands
		this.addChild(new MassiveCommandDeprecated(claim.cmdFactionsClaimAuto, "autoclaim"));
		//this.addChild(new MassiveCommandDeprecated(this.cmdFactionsUnclaim.cmdFactionsUnclaimAll, "unclaimall"));
		this.addChild(new MassiveCommandDeprecated(flag, "open"));
		
		if (MConf.get().enableFactionsTop)
		{
			this.addChild(new CmdFactionsTop());
			this.addChild(new CmdFactionsToprecalculate());
			this.addChild(new CmdFactionsPtop());
		}
		
		if (MConf.get().enableSetPaypal)
		{
			this.addChild(new CmdFactionsCheckpaypal());
			this.addChild(new CmdFactionsSetpaypal());
		}

		if (MConf.get().enableStrikes) {
			this.addChild(new CmdFactionsStrike());
			this.addChild(new CmdFactionsStrikes());
			this.addChild(new CmdFactionsDeletestrike());
		}

		if (MConf.get().enableSpawnerChunks) {
			this.addChild(CmdFactionsSpawnerchunk.get());
			this.addChild(new CmdFactionsSpawnerchunks());
		}
	}
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public static CmdFactions get()
	{
		return i;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public List<String> getAliases()
	{
		return MConf.get().aliasesF;
	}
	
}
