package org.x64tech;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.json.JSONObject;

import com.owlike.genson.Genson;

@Contract(
        name = "Vote Transfer",
        info = @Info(
                title = "Vote Transfer",
                description = "The hyperlegendary Vote Transfer Smart Contract",
                version = "3.5.2",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                contact = @Contact(
                        email = "code7lancer@gmial.com",
                        name = "Ankit Prajapati",
                        url = "https://ankit84-tech.github.io/my-portfolio")))
@Default
public final class VoteTransfer implements ContractInterface {

        private final Genson genson = new Genson();

        /**
         * this method will create a vote instance for owner.
         * @param ctx            transaction context
         * @param voteID         this is equal to userID. senderID ==> voteID
         * @param electonID      electionID
         * @param acceptorID candidate cryptoID
         * @return vote
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public Vote transferVote(final Context ctx, final String voteID, final String electionID,
                        final String acceptorID) {
                ChaincodeStub stub = ctx.getStub();
                String voteString = stub.getStringState(voteID);

                if (!stringIsNullOrEmpty(voteString)) {
                        String errorMessage = String.format("you %s alredy voted", voteID);
                        System.out.println(errorMessage);
                        throw new ChaincodeException(errorMessage, "alredy voted");
                }

                String senderCrypto = stub.getChannelId();
                Vote vote = new Vote(voteID, electionID, acceptorID, senderCrypto);
                stub.putStringState(voteID, genson.serialize(vote));

                return vote;
        }

        /**
         * this transaction delete the votes of election.
         * @param ctx       transaction context
         * @param electonID electionID
         * @return result of election.
         */
        @Transaction(intent = Transaction.TYPE.SUBMIT)
        public String disposeVotes(final Context ctx, final String electionID) {
                ChaincodeStub stub = ctx.getStub();
                QueryResultsIterator<KeyValue> stateByRange = stub.getStateByRange("", "");

                List<Vote> listVotes = new ArrayList<Vote>();
                List<String> listCand = new ArrayList<String>();

                for (KeyValue result : stateByRange) {
                        Vote vote = genson.deserialize(result.getStringValue(), Vote.class);
                        if (vote.getElectionID().equals(electionID)) {
                                listVotes.add(vote);
                                if (!listCand.contains(vote.getAcceptorID())) {
                                        listCand.add(vote.getAcceptorID());
                                }
                        }
                }

                JSONObject result = new JSONObject();

                for (String cand : listCand) {
                        int count = 0;
                        for (Vote vote : listVotes) {
                                if (vote.getAcceptorID().equals(cand)) {
                                        count++;
                                        stub.delState(vote.getVoteID());
                                }
                        }
                        result.put(cand, count);
                        count = 0;
                }

                return result.toString();
        }

        /**
         * this method will return object of
         * candiate state.
         * @param ctx
         * @param electionID
         * @return state of candidates
         */
        @Transaction(intent = Transaction.TYPE.EVALUATE)
        public String getElectionState(final Context ctx, final String electionID) {
                ChaincodeStub stub = ctx.getStub();
                QueryResultsIterator<KeyValue> stateByRange = stub.getStateByRange("", "");

                List<Vote> listVotes = new ArrayList<Vote>();
                List<String> listCand = new ArrayList<String>();

                for (KeyValue result : stateByRange) {
                        Vote vote = genson.deserialize(result.getStringValue(), Vote.class);
                        if (vote.getElectionID().equals(electionID)) {
                                listVotes.add(vote);
                                if (!listCand.contains(vote.getAcceptorID())) {
                                        listCand.add(vote.getAcceptorID());
                                }
                        }
                }

                JSONObject result = new JSONObject();

                for (String cand : listCand) {
                        int count = 0;
                        for (Vote vote : listVotes) {
                                if (vote.getAcceptorID().equals(cand)) {
                                        count++;
                                }
                        }
                        result.put(cand, count);
                        count = 0;
                }

                return result.toString();
        }

        /**
         * this method checks whether the string is null or empty
         * @param string
         * @return boolean
         */
        public static boolean stringIsNullOrEmpty(final String string) {
                return string == null || string.isEmpty();
        }
}
