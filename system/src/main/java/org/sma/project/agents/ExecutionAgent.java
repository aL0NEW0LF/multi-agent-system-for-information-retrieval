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

        // Add the behaviour to receive the task progress from the ResourceAgent
        addBehaviour(new ExecutionProgressBehaviour());

        // Register the ExecutionAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ExecutionAgent");
        sd.setName("ExecutionAgent");
        dfd.addServices(sd);

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
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(new jade.core.AID("BrokerAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Task received. Process it
                String task = msg.getContent();
                System.out.println("Task received: " + task);

                // Add the behaviour to send the task to the ResourceAgent
                addBehaviour(new ExecutionAssignTaskBehaviour());

                // Add the behaviour to send ontology updates to the OntologyAgent
                addBehaviour(new ExecutionOntologyBehaviour());
            } else {
                block();
            }
        }
    }

    private class ExecutionAssignTaskBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Send the task to the ResourceAgent
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME));
            request.setContent("Subtask");
            myAgent.send(request);
        }
    }

    private class ExecutionProgressBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the task progress from the ResourceAgent
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME)));
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

    private class ExecutionOntologyBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Send ontology updates to the OntologyAgent
            ACLMessage request = new ACLMessage(ACLMessage.INFORM);
            request.addReceiver(new jade.core.AID("OntologyAgent", jade.core.AID.ISLOCALNAME));
            request.setContent("Ontology updates");
            myAgent.send(request);
        }
    }
}
