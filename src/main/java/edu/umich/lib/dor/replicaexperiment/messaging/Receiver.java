package edu.umich.lib.dor.replicaexperiment.messaging;

import java.nio.file.Paths;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.DepositMessage;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.PurgeMessage;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.UpdateMessage;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.PurgeFactory;
import edu.umich.lib.dor.replicaexperiment.service.UpdateFactory;

@Component
public class Receiver {

    @Autowired
    DepositFactory depositFactory;

    @Autowired
    UpdateFactory updateFactory;

    @Autowired
    PurgeFactory purgeFactory;

    @RabbitListener(queues = "testQueue")
    public void listenTest(String message) {
        System.out.println("Message read from testQueue: " + message);
    }

    @RabbitListener(queues = "depositQueue")
    public void listenDeposit(DepositMessage depositMessage) throws InterruptedException {
        depositFactory.create(
            new Curator(
                depositMessage.getCuratorUsername(),
                depositMessage.getCuratorEmail()
            ),
            depositMessage.getPackageIdentifier(),
            Paths.get(depositMessage.getDepositSourcePath()),
            depositMessage.getMessage()
        ).execute();
    }

    @RabbitListener(queues = "updateQueue")
    public void listenUpdate(UpdateMessage updateMessage) {
        updateFactory.create(
            new Curator(
                updateMessage.getCuratorUsername(),
                updateMessage.getCuratorEmail()
            ),
            updateMessage.getPackageIdentifier(),
            Paths.get(updateMessage.getDepositSourcePath()),
            updateMessage.getMessage()
        ).execute();
    }

    @RabbitListener(queues = "purgeQueue")
    public void listenPurge(PurgeMessage purgeMessage) {
        purgeFactory.create(purgeMessage.getPackageIdentifier()).execute();
    }

}
