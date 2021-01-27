package de.majaf.voci.boundery.contoller.utils;

import de.majaf.voci.control.service.ICallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class CallEndScheduler {

    @Autowired
    private ICallService callService;

    @Autowired
    private ControllerUtils controllerUtils;

    @Scheduled(cron = "0 */10 * * * *")
    public void checkForActiveCalls() {
        for(long callID : callService.checkCallsForTimeoutOrEnd(10)) {
            controllerUtils.sendSocketEndInvitationMessage(callID);
            controllerUtils.sendSocketEndCallMessage(callID, true);
        }
    }
}
