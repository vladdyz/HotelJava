package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;

public interface WaitlistObserver {
    void onWaitlistMatch(Waitlist entry);
}