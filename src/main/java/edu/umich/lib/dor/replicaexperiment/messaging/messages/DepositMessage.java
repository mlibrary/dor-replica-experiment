package edu.umich.lib.dor.replicaexperiment.messaging.messages;

public class DepositMessage {
    private String curatorUsername;
    private String curatorEmail;
    private String packageIdentifier;
    private String depositSourcePath;
    private String message;

    public DepositMessage() {}

    public DepositMessage(
        String curatorUsername,
        String curatorEmail,
        String packageIdentifier,
        String depositSourcePath,
        String message
    ) {
        this.curatorUsername = curatorUsername;
        this.curatorEmail = curatorEmail;
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

    public String getCuratorUsername() {
        return curatorUsername;
    }

    public String getCuratorEmail() {
        return curatorEmail;
    }
}
