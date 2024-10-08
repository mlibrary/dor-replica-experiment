package edu.umich.lib.dor.replicaexperiment.service;

import java.util.HashMap;
import java.util.List;

import edu.umich.lib.dor.replicaexperiment.exception.RepositoryNotRegisteredException;

public class RepositoryClientRegistry {
    HashMap<String, RepositoryClient> clientMap = new HashMap<>();

    public void register(String name, RepositoryClient client) {
        clientMap.put(name, client);
    }

    public List<String> listClients(){
        return clientMap.keySet().stream().toList();
    }

    public RepositoryClient getClient(String name) {
        RepositoryClient client = clientMap.get(name);
        if (client == null) {
            throw new RepositoryNotRegisteredException(
                String.format("\"%s\" is not a registered repository.", name)
            );
        }
        return client;
    }
}
