package com.massivecraft.factions.integration.polar;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import top.polar.api.PolarApi;
import top.polar.api.PolarApiAccessor;
import top.polar.api.event.listener.ListenerPriority;
import top.polar.api.event.listener.RegisteredListener;
import top.polar.api.exception.PolarNotLoadedException;
import top.polar.api.loader.LoaderApi;
import top.polar.api.user.event.CloudDetectionEvent;
import top.polar.api.user.event.DetectionAlertEvent;

public class EnginePolar extends Engine {
    private static final EnginePolar i = new EnginePolar();
    public static EnginePolar get() { return i; }

    RegisteredListener<DetectionAlertEvent> eventListener = null;

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if(active) {
            Factions.get().activate(PolarColl.class);
            LoaderApi.registerEnableCallback(() -> {
                PolarApi api;
                try {
                    api = PolarApiAccessor.access().get();
                } catch (PolarNotLoadedException e) {
                    Factions.get().log("Could not register Polar listener.");
                    return;
                }

                api.events().repository().registerListener(DetectionAlertEvent.class, event -> {
                    MPlayer player = MPlayer.get(event.user().uuid());
                    if(player.isAlt()) {
                        if(PolarConf.get().altIgnoreCheck.contains(event.check().type())) {
                            event.cancelled(true);
                        }
                    }

                    PS pos1 = PolarConf.get().pos1;
                    PS pos2 = PolarConf.get().pos2;
                    PS playerPos = PS.valueOf(player.getPlayer().getLocation());
                    if(     playerPos.getWorld().equals(pos1.getWorld()) &&
                            playerPos.getLocationX() < pos1.getLocationX() &&
                            playerPos.getLocationZ() < pos1.getLocationZ() &&
                            playerPos.getLocationY() < pos1.getLocationY() &&
                            playerPos.getLocationX() > pos2.getLocationX() &&
                            playerPos.getLocationZ() > pos2.getLocationZ() &&
                            playerPos.getLocationY() > pos2.getLocationY()
                    ) {
                        if(PolarConf.get().altIgnoreCheck.contains(event.check().type())) {
                            event.cancelled(true);
                        }
                    }
                }, ListenerPriority.RUN_FIRST);

                api.events().repository().registerListener(CloudDetectionEvent.class, event -> {
                    MPlayer player = MPlayer.get(event.user().uuid());
                    if(player.isAlt()) {
                        if(PolarConf.get().altIgnoreCloudCheck.contains(event.cloudCheckType())) {
                            event.cancelled(true);
                        }
                    }

                    PS pos1 = PolarConf.get().pos1;
                    PS pos2 = PolarConf.get().pos2;
                    PS playerPos = PS.valueOf(player.getPlayer().getLocation());
                    if(     playerPos.getWorld().equals(pos1.getWorld()) &&
                            playerPos.getLocationX() < pos1.getLocationX() &&
                            playerPos.getLocationZ() < pos1.getLocationZ() &&
                            playerPos.getLocationY() < pos1.getLocationY() &&
                            playerPos.getLocationX() > pos2.getLocationX() &&
                            playerPos.getLocationZ() > pos2.getLocationZ() &&
                            playerPos.getLocationY() > pos2.getLocationY()
                    ) {
                        if(PolarConf.get().altIgnoreCloudCheck.contains(event.cloudCheckType())) {
                            event.cancelled(true);
                        }
                    }
                }, ListenerPriority.RUN_FIRST);
            });
        } else if(eventListener != null) {
            try {
                PolarApiAccessor.access().get().events().repository().unregisterListener(eventListener);
            } catch (PolarNotLoadedException e) {
                return;
            }
        }
    }
}
