package com.massivecraft.factions.comparator;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.comparator.ComparatorAbstract;
import com.massivecraft.massivecore.comparator.ComparatorComparable;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.command.CommandSender;

import java.lang.ref.WeakReference;

public class ComparatorFactionList extends ComparatorAbstract<Faction> {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final WeakReference<CommandSender> watcher;

	public ComparatorFactionList(Object watcherObject) {
		this.watcher = new WeakReference<>(IdUtil.getSender(watcherObject));
	}

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	public static ComparatorFactionList get(Object watcherObject) {
		return new ComparatorFactionList(watcherObject);
	}

	public CommandSender getWatcher() {
		return this.watcher.get();
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(Faction f1, Faction f2) {
		// None a.k.a. Wilderness
		if (f1.isNone() && f2.isNone()) return 0;
		if (f1.isNone()) return -1;
		if (f2.isNone()) return 1;

		// Players Online
		int onlineAltsF1 = (int) f1.getOnlinePlayers().stream().filter(follower -> MPlayer.get(follower).isAlt()).count();
		int onlineAltsF2 = (int) f2.getOnlinePlayers().stream().filter(follower -> MPlayer.get(follower).isAlt()).count();

		int ret = (f2.getMPlayersWhereOnlineTo(this.getWatcher()).size() - onlineAltsF2) - (f1.getMPlayersWhereOnlineTo(this.getWatcher()).size() - onlineAltsF1);
		if (ret != 0) return ret;

		// Players Total
		int altsF1 = (int) f1.getMPlayers().stream().filter(MPlayer::isAlt).count();
		int altsF2 = (int) f2.getMPlayers().stream().filter(MPlayer::isAlt).count();

		ret = (f2.getMPlayers().size() - altsF2) - (f1.getMPlayers().size() - altsF1);
		if (ret != 0) return ret;

		// Tie by Id
		return ComparatorComparable.get().compare(f1.getId(), f2.getId());
	}

}