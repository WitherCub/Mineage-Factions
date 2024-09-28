package com.massivecraft.factions.integration.polar;

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
}
