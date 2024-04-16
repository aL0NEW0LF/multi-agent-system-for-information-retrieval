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
 * This is the ResourceAgent class.
 * This agent will receive the user's request from the BrokerAgent.
 * It will return the response to the BrokerAgent.
 * It will check the data in the resource its attached to.
 * It will receive the subtask from the ExecutionAgent.
 * It will send task progress to the ExecutionAgent.
 */
public class ResourceAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("ResourceAgent " + getAID().getName() + " created.");

        // Add the behaviour to receive the user's request from the BrokerAgent
        addBehaviour(new ResourceRequestBehaviour());

        // Add the behaviour to send the response to the BrokerAgent
        addBehaviour(new ResourceResponseBehaviour());

        // Add the behaviour to check the data in the resource its attached to
        addBehaviour(new ResourceDataBehaviour());

        // Add the behaviour to receive the task from the ExecutionAgent
        addBehaviour(new ResourceTaskBehaviour());

        // Add the behaviour to send task progress to the ExecutionAgent
        addBehaviour(new ResourceProgressBehaviour());

        // Register the ResourceAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ResourceAgent");
        sd.setName("ResourceAgent");
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

    private class ResourceRequestBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Receive the user's request from the BrokerAgent
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage request = myAgent.receive(mt);

            if (request != null) {
                System.out.println("ResourceAgent received the request from " + request.getSender().getName());
            } else {
                block();
            }
        }
    }

    private class ResourceResponseBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Send the response to the BrokerAgent
            ACLMessage response = new ACLMessage(ACLMessage.INFORM);
            response.addReceiver(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME));
            response.setContent("ResourceAgent response");

            send(response);
        }
    }

    private class ResourceDataBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Check the data in the resource its attached to
            System.out.println("ResourceAgent checking the data in the resource");

            // Return the data
            System.out.println("ResourceAgent returning the data");

            // Send the data to the BrokerAgent
            ACLMessage data = new ACLMessage(ACLMessage.INFORM);
            data.addReceiver(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME));
            data.setContent("ResourceAgent data");

            send(data);

            // Send the progress to the ExecutionAgent
            ACLMessage progress = new ACLMessage(ACLMessage.INFORM);
            progress.addReceiver(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME));
            progress.setContent("ResourceAgent data progress");

            send(progress);
        }
    }

    private class ResourceTaskBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the task from the ExecutionAgent
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage task = myAgent.receive(mt);

            if (task != null) {
                System.out.println("ResourceAgent received the task from " + task.getSender().getName());
            } else {
                block();
            }
        }
    }

    private class ResourceProgressBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Send task progress to the ExecutionAgent
            ACLMessage progress = new ACLMessage(ACLMessage.INFORM);
            progress.addReceiver(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME));
            progress.setContent("ResourceAgent task progress");

            send(progress);
        }
    }
}