package edu.umich.lib.dor.replicaexperiment.messaging;

import java.nio.file.Paths;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.DepositMessage;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;

@Component
public class Receiver {

    @Autowired
    DepositFactory depositFactory;

    @RabbitListener(queues = "testQueue")
    public void listenTest(String message) {
        System.out.println("Message read from testQueue: " + message);
    }

    @RabbitListener(queues = "depositQueue")
    public void listenDeposit(DepositMessage depositMessage) throws InterruptedException {
        Thread.sleep(3000);
        depositFactory.create(
            new Curator("test", "test@example.edu"),
            depositMessage.getPackageIdentifier(),
            Paths.get(depositMessage.getDepositSourcePath()),
            depositMessage.getMessage()
        ).execute();
    }
}
