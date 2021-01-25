package de.majaf.voci.boundery.contoller.utils;

import de.majaf.voci.control.service.ICallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class Scheduler {

    @Autowired
    private ICallService callService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Scheduled(cron = "0 */10 * * * *")
    public void checkForTimeout() {
        for(long callID : callService.updateTimeout(10)) {
            simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedInvitation", true);
            simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedCall", true);
        }
    }
}
