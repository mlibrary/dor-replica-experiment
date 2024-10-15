package edu.umich.lib.dor.replicaexperiment.messaging.messages;

import java.util.Map;

public class UpdateMessage {
    private Map<String, String> curator;
    private String packageIdentifier;
    private String depositSourcePath;
    private String message;

    public UpdateMessage() {}

    public UpdateMessage(
        Map<String, String> curator,
        String packageIdentifier,
        String depositSourcePath,
        String message
    ) {
        this.curator = curator;
        this.packageIdentifier = packageIdentifier;
        this.depositSourcePath = depositSourcePath;
        this.message = message;
    }

    public String getPackageIdentifier() {
        return packageIdentifier;
    }

    public String getDepositSourcePath() {
        return depositSourcePath;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getCurator() {
        return curator;
    }
}
