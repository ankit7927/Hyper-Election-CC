package org.x64tech;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Vote {
    @Property()
    private final String voteID;

    @Property()
    private final String electionID;

    @Property()
    private final String senderCrypto;

    @Property()
    private final String acceptorID;

    public Vote(@JsonProperty("voteID") final String voteID,
            @JsonProperty("electionID") final String electionID,
            @JsonProperty("acceptorID") final String acceptorID,
            @JsonProperty("senderCrypto") final String senderCrypto) {
        this.voteID = voteID;
        this.electionID = electionID;
        this.senderCrypto = senderCrypto;
        this.acceptorID = acceptorID;
    }

    public String getVoteID() {
        return voteID;
    }

    public String getElectionID() {
        return electionID;
    }

    public String getSenderCrypto() {
        return senderCrypto;
    }

    public String getAcceptorID() {
        return acceptorID;
    }
}
