package edu.umich.lib.dor.replicaexperiment.messaging.messages;

public class PurgeMessage {
    String packageIdentifier;

    public PurgeMessage() {}

    public PurgeMessage(String packageIdentifier) {
        this.packageIdentifier = packageIdentifier;
    }
    
    public String getPackageIdentifier() {
        return packageIdentifier;
    }
}
