package edu.umich.lib.dor.replicaexperiment.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.umich.lib.dor.replicaexperiment.controllers.dtos.InfoPackageDto;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackageRepository;
import edu.umich.lib.dor.replicaexperiment.domain.User;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;

@Controller
@RequestMapping(path="/package")
public class InfoPackageController {
    @Autowired
    private InfoPackageRepository infoPackageRepository;

    @Autowired
    private RepositoryManager repositoryManager;

    @Autowired
    private Environment environment;

    @PostMapping(path="/add")
    public @ResponseBody InfoPackageDto addPackageToRepository (
        @RequestParam String identifier,
        @RequestParam String depositSourcePath,
        @RequestParam String repository,
        @RequestParam String message
    ) {
		Path depositPath = Paths.get(
			environment.getRequiredProperty("repository.deposit.path")
		);
        Path fullSourcePath = depositPath.resolve(depositSourcePath);

        repositoryManager.setUser(new User("test", "test@example.edu"));
        repositoryManager.addPackageToRepository(
            identifier, fullSourcePath, repository, message
        );
        var newInfoPackage = repositoryManager.getInfoPackage(identifier);
        return new InfoPackageDto(newInfoPackage);
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<InfoPackageDto> getAllInfoPackages() {
        List<InfoPackage> infoPackages = infoPackageRepository.findAll();
        return infoPackages
            .stream()
            .map(infoPackage -> { return new InfoPackageDto(infoPackage); })
            .toList();
    }
}
