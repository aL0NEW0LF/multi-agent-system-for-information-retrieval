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
* This is the BrokerAgent class.
* This agent will receive the user's request from the UserAgent.
* It will send the user's request to the ResourceAgent.
* It will receive the response from the ResourceAgent.
* It sends the task to the ExecutionAgent.
* It will send the result to the UserAgent.
*/

public class BorkerAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("BrokerAgent " + getAID().getName() + " created.");

        // Add the behaviour to receive the user's request from the UserAgent
        addBehaviour(new BrokerRequestBehaviour());

        // Add the behaviour to send the user's request to the ResourceAgent
        addBehaviour(new BrokerResourceBehaviour());

        // Add the behaviour to receive the response from the ResourceAgent
        addBehaviour(new BrokerResponseBehaviour());

        // Add the behaviour to send the task to the ExecutionAgent
        addBehaviour(new BrokerExecutionBehaviour());

        // Add the behaviour to send the result to the UserAgent
        addBehaviour(new BrokerResultBehaviour());

        // Register the BrokerAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("BrokerAgent");
        sd.setName(getLocalName() + "-BrokerAgent");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
            System.out.println("BrokerAgent terminated.");

            try {
                // Deregister from the yellow pages
                DFService.deregister(this);
            } catch (FIPAException e) {
                e.printStackTrace();
            }

            // Printout a dismissal message
            System.out.println("BrokerAgent " + getAID().getName() + " terminating.");
    }

    private class BrokerRequestBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Send the user's request to the ResourceAgent
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME));
                request.setContent(msg.getContent());
                myAgent.send(request);
            } else {
                block();
            }
        }
    }

    private class BrokerResourceBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Send the user's request to the ResourceAgent
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME));
                request.setContent(msg.getContent());
                myAgent.send(request);
            } else {
                block();
            }
        }
    }

    private class BrokerResponseBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Send the response to the ExecutionAgent
                ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                response.addReceiver(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME));
                response.setContent(msg.getContent());
                myAgent.send(response);
            } else {
                block();
            }
        }
    }

    private class BrokerExecutionBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Send the task to the ExecutionAgent
                ACLMessage task = new ACLMessage(ACLMessage.REQUEST);
                task.addReceiver(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME));
                task.setContent(msg.getContent());
                myAgent.send(task);
            } else {
                block();
            }
        }
    }

    private class BrokerResultBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Send the result to the UserAgent
                ACLMessage result = new ACLMessage(ACLMessage.INFORM);
                result.addReceiver(new jade.core.AID("UserAgent", jade.core.AID.ISLOCALNAME));
                result.setContent(msg.getContent());
                myAgent.send(result);
            } else {
                block();
            }
        }
    }
}
