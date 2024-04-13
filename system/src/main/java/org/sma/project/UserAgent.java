package org.sma.project;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.*;

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
        sd.setName(getLocalName() + "-UserAgent");
        dfd.addServices(sd);

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
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage response = myAgent.receive(mt);

            if (response != null) {
                // Print the response
                System.out.println("UserAgent " + getAID().getName() + " received the response: " + response.getContent());
            } else {
                block();
            }
        }
    }

    private class UserExecutionBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the task from the ExecutionAgent
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage task = myAgent.receive(mt);

            if (task != null) {
                // Print the task
                System.out.println("UserAgent " + getAID().getName() + " received the task: " + task.getContent());

                // Create the result
                ACLMessage result = new ACLMessage(ACLMessage.INFORM);
                result.addReceiver(new jade.core.AID("UserInterface", jade.core.AID.ISLOCALNAME));
                result.setContent("The result is ready");

                // Send the result to the UserInterface
                send(result);
            } else {
                block();
            }
        }
    }
}

