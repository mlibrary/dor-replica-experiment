package edu.umich.lib.dor.replicaexperiment.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.umich.lib.dor.replicaexperiment.controllers.dtos.InfoPackageDto;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.User;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;

@Controller
@RequestMapping(path="/package")
public class InfoPackageController {
    User testUser = new User("test", "test@example.edu");

    @Autowired
    private InfoPackageService infoPackageService;

    @Autowired
    private RepositoryManager repositoryManager;

    @PostMapping(path="/add")
    public @ResponseBody InfoPackageDto addPackageToRepository (
        @RequestParam String identifier,
        @RequestParam String depositSourcePath,
        @RequestParam String repository,
        @RequestParam String message
    ) {
        Path sourcePathRelativeToDeposit = Paths.get(depositSourcePath);
        repositoryManager.setUser(testUser);
        repositoryManager.addPackageToRepository(
            identifier, sourcePathRelativeToDeposit, repository, message
        );
        var newInfoPackage = infoPackageService.getInfoPackage(identifier);
        return new InfoPackageDto(newInfoPackage);
    }

    @PutMapping(path="/replicate")
    public @ResponseBody InfoPackageDto replicatePackageToRepository(
        @RequestParam String identifier,
        @RequestParam String sourceRepository,
        @RequestParam String targetRepository
    ) {
        repositoryManager.setUser(testUser);
        repositoryManager.replicatePackageToAnotherRepository(
            identifier, sourceRepository, targetRepository
        );
        var newInfoPackage = infoPackageService.getInfoPackage(identifier);
        return new InfoPackageDto(newInfoPackage);
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<InfoPackageDto> getAllInfoPackages() {
        List<InfoPackage> infoPackages = infoPackageService.getAllInfoPackages();
        return infoPackages
            .stream()
            .map(infoPackage -> { return new InfoPackageDto(infoPackage); })
            .toList();
    }
}
