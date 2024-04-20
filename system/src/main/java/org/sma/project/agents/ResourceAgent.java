/**
 * Created by MOHAMED AMINE FAKHRE-EDDINE
 * Email: mohamedfakhreeddine2019@gmail.com
 * Github: github.com/aL0NEW0LF
 * Date: 4/20/2024
 * Time: 2:12 PM
 * Project Name: multi-agent-system-for-information-retrieval
 */

package org.sma.project.agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.sma.project.ui.search;

import java.util.*;
import java.util.concurrent.TimeUnit;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
/*
 * This is the ResourceAgent class.
 * This agent will receive the user's request from the BrokerAgent.
 * It will return the response to the BrokerAgent.
 * It will check the data in the resource its attached to.
 * It will receive the subtask from the ExecutionAgent.
 * It will send task progress to the ExecutionAgent.
 */
public class ResourceAgent extends Agent {
    private search userInterface;
    @Override
    protected void setup() {
        System.out.println("ResourceAgent " + getAID().getName() + " created.");

        // Add the behaviour to receive the user's request from the BrokerAgent
        addBehaviour(new ResourceRequestBehaviour());

        // Register the ResourceAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ResourceAgent");
        sd.setName("ResourceAgent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("ResourceAgent terminated.");

        try {
            // Deregister from the yellow pages
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Printout a dismissal message
        System.out.println("ResourceAgent " + getAID().getName() + " terminating.");
    }

    private class ResourceRequestBehaviour extends CyclicBehaviour {
        ACLMessage NotNullRequest = null;
        ACLMessage NotNullSubtask = null;
        ACLMessage NotNullConcept = null;
        @Override
        public void action() {
            // Receive the user's request from the BrokerAgent
            MessageTemplate requestMT = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchSender(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage request = myAgent.receive(requestMT);

            MessageTemplate subtaskMT = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchSender(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage subtask = myAgent.receive(subtaskMT);

            MessageTemplate conceptMT = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(new jade.core.AID("OntologyAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage concept = myAgent.receive(conceptMT);

            if (request != null) {
                NotNullRequest = request;
                System.out.println("ResourceAgent received the request from " + NotNullRequest.getSender().getName());
            }
            if (subtask != null) {
                NotNullSubtask = subtask;
                System.out.println("ResourceAgent received the task from " + NotNullSubtask.getSender().getName());
            }
            if (concept != null) {
                NotNullConcept = concept;
                System.out.println("ResourceAgent received the concept from " + NotNullConcept.getSender().getName());
            }

            if (NotNullRequest != null && NotNullSubtask != null && NotNullConcept != null) {
                // Check the data in the resources (resources)
                System.out.println("ResourceAgent checking the data in the resource");

                String requestQuery = NotNullRequest.getContent();
                // Read the data from the resource
                try {
                    int foundCount = 0;

                    for (int i = 1; i <= 3; i++) {
                        File myObj = new File("src/main/resources/resource" + i + ".txt");
                        System.out.println("Reading file: " + myObj.getAbsolutePath());
                        if (!myObj.exists()) {
                            System.out.println("File not found: " + myObj.getAbsolutePath());
                            continue;
                        }

                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();

                            if (data.contains(requestQuery)) {
                                // Send the data to the BrokerAgent
                                ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                                response.addReceiver(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME));
                                response.setContent(data);
                                send(response);

                                foundCount++;
                            }
                        }
                        myReader.close();
                    }

                    if (foundCount == 0) {
                        // Send the data to the BrokerAgent
                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME));
                        response.setContent("No data found");
                        send(response);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();

                    // Send the error to the BrokerAgent
                    ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                    response.addReceiver(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME));
                    response.setContent("An error occurred");
                    send(response);
                }

                // Return the data
                System.out.println("ResourceAgent returning the data");

                NotNullRequest = null;
                NotNullSubtask = null;
                NotNullConcept = null;
            } else {
                block();
            }
        }
    }
}