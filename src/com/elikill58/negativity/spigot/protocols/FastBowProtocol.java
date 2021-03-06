package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer.FlyingReason;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.ItemUseBypass;

public class FastBowProtocol implements Listener {
	
	public static final Cheat CHEAT = Cheat.FASTBOW;
	
	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(p.getItemInHand() != null)
			if(ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType());
				if(ib.getWhen().isClick() && ib.isForThisCheat(CHEAT))
					if(e.getAction().name().toLowerCase().contains(ib.getWhen().name().toLowerCase()))
						return;
			}
		if (p.getItemInHand().getType().equals(Material.BOW)) {
			np.flyingReason = FlyingReason.BOW;
			long actual = System.currentTimeMillis(), dif = actual - np.LAST_SHOT_BOW;
			if (np.LAST_SHOT_BOW != 0) {
				int ping = Utils.getPing(p);
				if (dif < (200 + ping)) {
					np.addWarn(CHEAT);
					boolean mayCancel = false;
					if (dif < (50 + ping))
						mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, CHEAT,
								Utils.parseInPorcent(200 - dif - ping), "Player use Bow, last shot: " + np.LAST_SHOT_BOW
										+ " Actual time: " + actual + " Difference: " + dif + ", Warn: " + np.getWarn(CHEAT), "Time between last shot: " + dif + " (in milliseconds).");
					else
						mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, CHEAT,
								Utils.parseInPorcent(100 - dif - ping), "Player use Bow, last shot: " + np.LAST_SHOT_BOW
								+ " Actual time: " + actual + " Difference: " + dif + ", Warn: " + np.getWarn(CHEAT), "Time between last shot: " + dif + " (in milliseconds)");
					if(CHEAT.isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
			np.LAST_SHOT_BOW = actual;
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onShot(EntityShootBowEvent e){
		if(e.getEntity() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).flyingReason = FlyingReason.BOW;
	}
}
