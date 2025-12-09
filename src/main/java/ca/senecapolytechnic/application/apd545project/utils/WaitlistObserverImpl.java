package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;
import ca.senecapolytechnic.application.apd545project.services.WaitlistService;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class WaitlistObserverImpl {
    private final WaitlistService service;
    private final List<WaitlistObserver> observers = new ArrayList<>();


    @Inject
    public WaitlistObserverImpl(WaitlistService service) {
        this.service = service;
    }

    public void addObserver(WaitlistObserver o) { observers.add(o); }
    public void removeObserver(WaitlistObserver o) { observers.remove(o); }

    // observer
    public void checkAndNotify() {
        List<Waitlist> matches = service.findMatchingWaitlistEntries();
        for (Waitlist w : matches) {
            RoomType availableType = w.getRequestedType();
            onRoomTypeAvailable(w, availableType);
            service.markNotified(w);
        }
    }
    // notifies the current admin whenever the observer detects that a room type is available
    public void onRoomTypeAvailable(Waitlist w, RoomType availableType) {
        String msg = "URGENT: A " + availableType + " room is now available for "
                + w.getGuest().getName();
        Observable.getInstance().notify(msg);
    }
}
