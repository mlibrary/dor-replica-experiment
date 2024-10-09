package edu.umich.lib.dor.replicaexperiment.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.umich.lib.dor.replicaexperiment.controllers.dtos.InfoPackageDto;
import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.DepositMessage;
import edu.umich.lib.dor.replicaexperiment.service.Deposit;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.Purge;
import edu.umich.lib.dor.replicaexperiment.service.PurgeFactory;
import edu.umich.lib.dor.replicaexperiment.service.Update;
import edu.umich.lib.dor.replicaexperiment.service.UpdateFactory;

@Controller
@RequestMapping(path = "/package")
public class InfoPackageController {
    Curator testCurator = new Curator("test", "test@example.edu");

    @Autowired
    private InfoPackageService infoPackageService;

    @Autowired
    private DepositFactory depositFactory;

    @Autowired
    private UpdateFactory updateFactory;

    @Autowired
    private PurgeFactory purgeFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping(path = "/test")
    public @ResponseBody String test() {
        rabbitTemplate.convertAndSend("testQueue", "Hello, world!");
        return "sent";
    }

    @PostMapping(path = "/deposit-message")
    public @ResponseBody String depositMessage(
        @RequestParam String packageIdentifier,
        @RequestParam String depositSourcePath,
        @RequestParam String message
    ) {
        DepositMessage depositMessage = new DepositMessage(
            packageIdentifier, depositSourcePath, message
        );
        rabbitTemplate.convertAndSend("depositQueue", depositMessage);
        return "depositing";
    }

    @PostMapping(path = "/deposit")
    public @ResponseBody InfoPackageDto deposit(
        @RequestParam String identifier,
        @RequestParam String depositSourcePath,
        @RequestParam String message
    ) {
        Path sourcePathRelativeToDeposit = Paths.get(depositSourcePath);
        Deposit deposit = depositFactory.create(
            testCurator, identifier, sourcePathRelativeToDeposit, message
        );
        deposit.execute();
        var newInfoPackage = infoPackageService.getInfoPackage(identifier);
        return new InfoPackageDto(newInfoPackage);
    }

    @PutMapping(path = "/update")
    public @ResponseBody InfoPackageDto update(
        @RequestParam String identifier,
        @RequestParam String depositSourcePath,
        @RequestParam String message
    ) {
        Path sourcePathRelativeToDeposit = Paths.get(depositSourcePath);
        Update update = updateFactory.create(
            testCurator, identifier, sourcePathRelativeToDeposit, message
        );
        update.execute();
        var newInfoPackage = infoPackageService.getInfoPackage(identifier);
        return new InfoPackageDto(newInfoPackage);
    }

    @DeleteMapping(path = "/purge")
    public @ResponseBody String purge(
        @RequestParam String identifier
    ) {
        Purge purge = purgeFactory.create(identifier);
        purge.execute();
        return "Purged";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<InfoPackageDto> getAllInfoPackages() {
        List<InfoPackage> infoPackages = infoPackageService.getAllInfoPackages();
        return infoPackages
            .stream()
            .map(infoPackage -> { return new InfoPackageDto(infoPackage); })
            .toList();
    }
}
