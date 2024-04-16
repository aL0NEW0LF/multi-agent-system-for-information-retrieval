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
* This is the ExecutionAgent class.
* This agent will receive the task from the BrokerAgent.
* It will send the task to the ResourceAgent.
* It will receive the task progress from the ResourceAgent.
* It will assign the task to the ResourceAgent.
* It will send ontology updates to the OntologyAgent.
*/
public class ExecutionAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("ExecutionAgent " + getAID().getName() + " created.");

        // Add the behaviour to receive the task from the BrokerAgent
        addBehaviour(new ExecutionTaskBehaviour());

        // Add the behaviour to send the task to the ResourceAgent
        addBehaviour(new ExecutionResourceBehaviour());

        // Add the behaviour to receive the task progress from the ResourceAgent
        addBehaviour(new ExecutionProgressBehaviour());

        // Add the behaviour to assign the task to the ResourceAgent
        addBehaviour(new ExecutionAssignBehaviour());

        // Add the behaviour to send ontology updates to the OntologyAgent
        addBehaviour(new ExecutionOntologyBehaviour());

        // Register the ExecutionAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ExecutionAgent");
        sd.setName("ExecutionAgent");
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
        System.out.println("ExecutionAgent terminated.");

        try {
            // Deregister from the yellow pages
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private class ExecutionTaskBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the task from the BrokerAgent
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Task received. Process it
                String task = msg.getContent();
                System.out.println("Task received: " + task);
            } else {
                block();
            }
        }
    }

    private class ExecutionResourceBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Send the task to the ResourceAgent
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME));
            request.setContent("Task");
            myAgent.send(request);
        }
    }

    private class ExecutionProgressBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the task progress from the ResourceAgent
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Task progress received. Process it
                String progress = msg.getContent();
                System.out.println("Task progress received: " + progress);
            } else {
                block();
            }
        }
    }

    private class ExecutionAssignBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Assign the task to the ResourceAgent
            System.out.println("Task assigned to the ResourceAgent");
        }
    }

    private class ExecutionOntologyBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Send ontology updates to the OntologyAgent
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new jade.core.AID("OntologyAgent", jade.core.AID.ISLOCALNAME));
            request.setContent("Ontology updates");
            myAgent.send(request);
        }
    }
}
