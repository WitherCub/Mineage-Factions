package com.massivecraft.factions.integration.polar;

import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import top.polar.api.user.event.type.CheckType;
import top.polar.api.user.event.type.CloudCheckType;

import java.util.List;

public class PolarConf extends Entity<PolarConf> {
    private static PolarConf i = new PolarConf();
    public static PolarConf get() { return i; }
    public static void set(PolarConf conf) { i = conf; }

    public List<CheckType> altIgnoreCheck = MUtil.list(CheckType.values());
    public List<CloudCheckType> altIgnoreCloudCheck = MUtil.list(CloudCheckType.values());

    public PS pos1 = PS.valueOf("NewSpawn", null, null, null, 3D, 70D, 3D, null, null, null, null, null, null, null);
    public PS pos2 = PS.valueOf("NewSpawn", null, null, null, -2D, 66D, -2D, null, null, null, null, null, null, null);
}
