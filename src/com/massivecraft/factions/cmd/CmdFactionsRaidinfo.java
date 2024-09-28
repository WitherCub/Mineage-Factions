package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.engine.EngineSpawnerchunk;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRaidinfo extends FactionsCommand {

    public CmdFactionsRaidinfo()
    {
        this.addParameter(TypeFaction.get(), "faction", "you");
    }

    @Override
    public void perform() throws MassiveException
    {
        Faction faction = this.readArg(msenderFaction);

        if (faction.isSystemFaction()) {
            msender.msg("<b>You cannot raid a system faction.");
            return;
        }

        msender.message(Txt.titleize(Txt.parse("<i>Raid info for %s", faction.describeTo(msender, true))));
        msender.message(mson(Txt.parse("<a>Raidable min Y: <i>%s", MConf.get().raidTimerStartMinY)).tooltip(Txt.parse("<i>You must explode TNT in a spawner chunk <a>above <i>this Y level to breach")));
        msender.message(mson(Txt.parse("<a>Raidable max Y: <i>%s",255)).tooltip(Txt.parse("<i>You must explode TNT in a spawner chunk <a>below <i>this Y level to breach")));

        if (!EngineSpawnerchunk.get().canBeBreached(faction)) {
            long timeSinceBreach = System.currentTimeMillis() - EngineSpawnerchunk.get().getLatestBreachTime(faction);
            msender.msg("<a>Time until next allowed breach: <i>%s", Txt.parse("<b>%s", TimeUtil.formatTime(MConf.get().breachCooldown - timeSinceBreach)) );
        }

        msender.msg("<a>They have been breached <i>%s <a>times and have breached <i>%s <a>other factions.", EngineSpawnerchunk.get().getTimesBreached(faction), EngineSpawnerchunk.get().getFactionsBreached(faction));
    }

}