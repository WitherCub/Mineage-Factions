package com.massivecraft.factions.cmd.pperm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdFactionsPpermShow extends FactionsCommand
{
	
	public CmdFactionsPpermShow()
	{
		this.addParameter(TypeMPlayer.get(), "player", "overview");
		this.addParameter(Parameter.getPage());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MPlayer mPlayer = this.readArg(null);
		int page = this.readArg();
		
		if (mPlayer == null)
		{
			final List<Mson> mplayerList = new ArrayList<>();
			
			for (MPlayer mp : msenderFaction.getMPlayers())
			{
				if (mp.isPpermsEmpty()) continue;
				
				int truePerms = 0;
				int falsePerms = 0;
				
				for (boolean value : mp.getOverridingMPermIdsValue().values())
				{
					if (value)
					{
						truePerms++;
					}
					else
					{
						falsePerms++;
					}
				}
				
				Mson mson = mson(Txt.parse("%s <n>(<g>%s <reset>| <b>%s<n>)", mp.describeTo(msender, true), truePerms, falsePerms));
				mson = mson.tooltip(Txt.parse("<v>Command: <k>/f pperm show %s 1", mp.getName()));
				mson = mson.suggest("/f pperm show " + mp.getName() + " 1");
				mplayerList.add(mson);
			}
			
			if (mplayerList.isEmpty())
			{
				msg("<b>Your faction has no per-player permissions defined.");
				return;
			}
			
			final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Players with override perms"), page, mplayerList, (Msonifier<Mson>) (mson, i) -> mplayerList.get(i));
			
			pager.message();
		}
		else
		{
			final List<Mson> mpermList = new ArrayList<>();
			
			Set<MPerm> alreadyChecked = new HashSet<>();
			
			for (String id : mPlayer.getOverridingMPermIdsValue().keySet())
			{
				MPerm mPerm = MPerm.get(id);
				
				if (mPerm == null) continue;
				if (alreadyChecked.contains(mPerm)) continue;
				
				if (mPlayer.getPlayerPermValue(mPerm) != null && mPlayer.getPlayerPermValue(mPerm))
				{
					Mson mson = mson(Txt.parse("<g>%s <i>%s", mPerm.getName().toLowerCase(), mPerm.getDesc()));
					mpermList.add(mson);
					alreadyChecked.add(mPerm);
				}
			}
			
			for (String id : mPlayer.getOverridingMPermIdsValue().keySet())
			{
				MPerm mPerm = MPerm.get(id);
				
				if (mPerm == null) continue;
				if (alreadyChecked.contains(mPerm)) continue;
				
				if (mPlayer.getPlayerPermValue(mPerm) != null && !mPlayer.getPlayerPermValue(mPerm))
				{
					Mson mson = mson(Txt.parse("<b>%s <i>%s", mPerm.getName().toLowerCase(), mPerm.getDesc()));
					mpermList.add(mson);
					alreadyChecked.add(mPerm);
				}
			}
			
			for (MPerm mPerm : MPerm.getAll())
			{
				if (alreadyChecked.contains(mPerm)) continue;
				if (mPerm.getName().toLowerCase().equalsIgnoreCase("container")) continue;
				if (mPerm.getName().toLowerCase().equalsIgnoreCase("deposit")) continue;
				if (mPerm.getName().toLowerCase().equalsIgnoreCase("claimnear")) continue;
				if (mPerm.getName().toLowerCase().equalsIgnoreCase("flags")) continue;
				
				Mson mson = null;
				
				if (mPlayer.getPlayerPermValue(mPerm) == null)
				{
					mson = mson(Txt.parse("<n>%s <i>%s", mPerm.getName().toLowerCase(), mPerm.getDesc()));
				}
				
				mpermList.add(mson);
			}
			
			final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Perms for %s", mPlayer.describeTo(msender)), page, mpermList, (Msonifier<Mson>) (mson, i) -> mpermList.get(i)).setArgs(mPlayer.getName(), String.valueOf(page));
			pager.message();
		}
	}
	
}