package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.UpgradeInfo;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

@EditorName("config")
public class MissionUpgradeConf extends Entity<MissionUpgradeConf>
{
	private static MissionUpgradeConf i;
	public static void set(MissionUpgradeConf newI) { i = newI; }
	
	public List<ConfMission> challenges = MUtil.list(
		new ConfMission("Sugarcane", "&aHarvest Sugarcane", "&7Harvest 25,000 Sugarcane", 25000D, 5000, Material.SUGAR_CANE, 0),
		new ConfMission("Blaze", "&eKill Blazes", "&7Kill 2,500 Blazes", 2500D, 5000, Material.BLAZE_ROD, 0),
		new ConfMission("Trench", "&bTrench", "&7Mine 20,000 Blocks", 20000D, 5000, Material.DIAMOND_PICKAXE, 0),
		new ConfMission("EXP", "&6Earn EXP", "&7Earn 100 EXP Levels (29,315 EXP)", 29315D, 5000, Material.EXP_BOTTLE, 0),
		new ConfMission("Cactus", "&2Place Cactus", "&7Place 25,000 Cactus", 25000D, 5000, Material.CACTUS, 0),
		new ConfMission("Travel", "&bTravel", "&7Take 60,000 Steps", 60000D, 5000, Material.DIAMOND_BOOTS, 0)
	);

	public int factionUpgradesInventorySize = 18;
	public boolean factionUpgradesBorderGlassEnabled = true;
	public int factionUpgradesBorderGlassDurabilityId = 7;
	public boolean factionUpgradesBorderGlassGlowEnabled = true;
	public String factionUpgradesInventoryName = "&a&lFaction Upgrades";

	public UpgradeInfo spawnerRate = new UpgradeInfo(
			Txt.parse("<i>Spawner Rate"),
			Material.MOB_SPAWNER,
			5,
			new String[]{
					"Mob spawner rates boosted by a total /nof 2% in your claimed land",
					"Mob spawner rates boosted by a total /nof 6% in your claimed land",
					"Mob spawner rates boosted by a total /nof 16% in your claimed land",
					"Mob spawner rates boosted by a total /nof 24% in your claimed land",
					"Mob spawner rates boosted by a total /nof 30% in your claimed land"
			},
			new String[]{
					"Increases mob spawner rates by 2% /nin your claimed land",
					"Increases mob spawner rates by 4% /nfor a total of 6% in your claimed land",
					"Increases mob spawner rates by 6% /nfor a total of 12% in your claimed land",
					"Increases mob spawner rates by 8% /nfor a total of 20% in your claimed land",
					"Increases mob spawner rates by 10% /nfor a total of 30% in your claimed land"
			},
			new Integer[]{
					1000000,
					2000000,
					3000000,
					4000000,
					5000000
			},
			new Double[]{
					0.02,
					0.06,
					0.12,
					0.20,
					0.30,
			},
			1);

	public UpgradeInfo cropGrowth = new UpgradeInfo(
			Txt.parse("<i>Crop Growth"),
			Material.CACTUS,
			5,
			new String[]{
					"Crop growth rates boosted by a total /nof 2% in your claimed land",
					"Crop growth rates boosted by a total /nof 6% in your claimed land",
					"Crop growth rates boosted by a total /nof 12% in your claimed land",
					"Crop growth rates boosted by a total /nof 20% in your claimed land",
					"Crop growth rates boosted by a total /nof 30% in your claimed land"
			},
			new String[]{
					"Increases crop growth rates by 2% /nin your claimed land",
					"Increases crop growth rates by 4% /nfor a total of 6% in your claimed land",
					"Increases crop growth rates by 6% /nfor a total of 12% in your claimed land",
					"Increases crop growth rates by 8% /nfor a total of 20% in your claimed land",
					"Increases crop growth rates by 10% /nfor a total of 30% in your claimed land"
			},
			new Integer[]{
					1000000,
					2000000,
					3000000,
					4000000,
					5000000
			},
			new Double[]{
					0.02,
					0.06,
					0.12,
					0.20,
					0.30,
			},
			2);


	public Map<Integer, Double> outpostCapSpeedLevelAndMultiplier =MUtil.map(
			1,1.1,
			2,1.2,
			3,1.3
	);
	public UpgradeInfo outpostCapSpeed = new UpgradeInfo(
			Txt.parse("<i>Outpost Cap Speed"),
			Material.OBSIDIAN,
			3,
			new String[]{
					"Total of 1.1x outpost cap speed multiplier",
					"Total of 1.2x outpost cap speed multiplier",
					"Total of 1.3x outpost cap speed multiplier"
			},
			new String[]{
					"Unlocks an extra 0.1x outpost multiplier for a total of 1.1x speed",
					"Unlocks an extra 0.1x outpost multiplier for a total of 1.2x speed",
					"Unlocks an extra 0.1x outpost multiplier for a total of 1.3x speed"
			},
			new Integer[]{
					1000000,
					2000000,
					3000000
			},
			new Double[]{
					0.1,
					0.2,
					0.3,
			},
			3);

	public UpgradeInfo factionChestUpgrade = new UpgradeInfo(
			Txt.parse("<i>Faction Chest"),
			Material.CHEST,
			3,
			new String[]{
				"Total of 18 slots of virtual chest storage in /f chest",
				"Total of 36 slots of virtual chest storage in /f chest",
				"Total of 54 slots of virtual chest storage in /f chest"
			},
			new String[]{
				"Unlocks a total of 18 slots of /nvirtual chest storage in /f chest",
				"Unlocks 18 extra slots for a total of 36 /nslots of virtual chest storage in /f chest",
				"Unlocks 18 extra slots for a total of 54 /nslots of virtual chest storage in /f chest"
			},
			new Integer[]{
				1000000,
				2000000,
				3000000
			},
			new Double[]{
					18D,
					36D,
					54D
			},
			4);
	
	public UpgradeInfo tntUpgrade = new UpgradeInfo(
			Txt.parse("<i>Virtual TNT Storage"),
			Material.TNT,
			10,
			new String[]{
				"Total of 10,000 slots of virtual tnt storage in /f tnt",
				"Total of 30,000 slots of virtual tnt storage in /f tnt",
				"Total of 50,000 slots of virtual tnt storage in /f tnt",
				"Total of 75,000 slots of virtual tnt storage in /f tnt",
				"Total of 100,000 slots of virtual tnt storage in /f tnt",
				"Total of 125,000 slots of virtual tnt storage in /f tnt",
				"Total of 150,000 slots of virtual tnt storage in /f tnt",
				"Total of 200,000 slots of virtual tnt storage in /f tnt",
				"Total of 250,000 slots of virtual tnt storage in /f tnt",
				"Total of 300,000 slots of virtual tnt storage in /f tnt"
			},
			new String[]{
				"Unlocks a total of 10,000 slots of /nvirtual tnt storage in /f tnt",
				"Unlocks 20,000 extra slots for a total of /n30,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 20,000 extra slots for a total of /n50,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 25,000 extra slots for a total of /n75,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 25,000 extra slots for a total of /n100,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 25,000 extra slots for a total of /n125,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 25,000 extra slots for a total of /n150,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 50,000 extra slots for a total of /n200,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 50,000 extra slots for a total of /n250,000 slots of virtual tnt storage in /f tnt",
				"Unlocks 50,000 extra slots for a total of /n300,000 slots of virtual tnt storage in /f tnt"
			},
			new Integer[]{
				1000000,
				2000000,
				3000000,
				4000000,
				5000000,
				6000000,
				7000000,
				8000000,
				9000000,
				10000000
			},
			new Double[]{
					10000D,
					30000D,
					50000D,
					750000D,
					100000D,
					125000D,
					150000D,
					200000D,
					250000D,
					300000D,
			},
			5);

	public UpgradeInfo warpUpgrade = new UpgradeInfo(
			Txt.parse("<i>More Warps"),
			Material.ENDER_PORTAL_FRAME,
			5,
			new String[]{
				"Total of 7 faction warps",
				"Total of 9 faction warps",
				"Total of 11 faction warps",
				"Total of 13 faction warps",
				"Total of 15 faction warps",
			},
			new String[]{
				"Unlocks 2 extra faction warps for a total of 7",
				"Unlocks 2 extra faction warps for a total of 9",
				"Unlocks 2 extra faction warps for a total of 11",
				"Unlocks 2 extra faction warps for a total of 13",
				"Unlocks 2 extra faction warps for a total of 15"
			},
			new Integer[]{
				1000000,
				2000000,
				3000000,
				4000000,
				5000000
			},
			new Double[]{
					2D,
					4D,
					6D,
					8D,
					10D
			},
			6
	);

	public UpgradeInfo sandbotUpgrade = new UpgradeInfo(
			Txt.parse("<i>Sandbots"),
			Material.SAND,
			8,
			new String[]{
					"Total of 1 sandbot",
					"Total of 2 sandbot",
					"Total of 3 sandbot",
					"Total of 4 sandbot",
					"Total of 5 sandbot",
					"Total of 6 sandbot",
					"Total of 7 sandbot",
					"Total of 8 sandbot",
			},
			new String[]{
					"Unlocks a total of 1 sandbots",
					"Unlocks a total of 2 sandbots",
					"Unlocks a total of 3 sandbots",
					"Unlocks a total of 4 sandbots",
					"Unlocks a total of 5 sandbots",
					"Unlocks a total of 6 sandbots",
					"Unlocks a total of 7 sandbots",
					"Unlocks a total of 8 sandbots"
			},
			new Integer[]{
					1000000,
					2000000,
					3000000,
					4000000,
					5000000,
					6000000,
					7000000,
					8000000
			},
			new Double[]{},
			7
	);

	public static MissionUpgradeConf get()
	{
		return i;
	}
	
	@Override
	public MissionUpgradeConf load(MissionUpgradeConf that)
	{
		super.load(that);
		return this;
	}
}