package edu.umich.lib.dor.replicaexperiment.messaging.messages;

public class DepositMessage {
    private String packageIdentifier;
    private String depositSourcePath;
    private String message;

    public DepositMessage() {}

    public DepositMessage(
        String packageIdentifier,
        String depositSourcePath,
        String message
    ) {
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
}
