package org.sma.project.agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
 * This is the UserAgent class. This agent takes care of the user's requests.
 * It is responsible for sending the user's request to the BrokerAgent.
 * It receives the response from the BrokerAgent.
 * It sends the result to the UserInterface.
 * */
public class UserAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("UserAgent " + getAID().getName() + " created.");

        // Add the behaviour to send the user's request to the BrokerAgent
        addBehaviour(new UserRequestBehaviour());

        // Add the behaviour to receive the response from the BrokerAgent
        addBehaviour(new UserResponseBehaviour());

        // Register the UserAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("UserAgent");
        sd.setName("UserAgent");
        dfd.addServices(sd);
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("UserAgent terminated.");

        try {
            // Deregister from the yellow pages
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Printout a dismissal message
        System.out.println("UserAgent " + getAID().getName() + " terminating.");
    }

    private class UserRequestBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Create the user's request
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME));
            request.setContent("Requesting a service");

            // Send the request to the BrokerAgent
            send(request);
        }
    }

    private class UserResponseBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the response from the BrokerAgent
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage response = myAgent.receive(mt);

            if (response != null) {
                // Print the response
                System.out.println("UserAgent " + getAID().getName() + " received the response: " + response.getContent());
            } else {
                block();
            }
        }
    }
}

