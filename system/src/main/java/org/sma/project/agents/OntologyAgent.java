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
* This is the OntologyAgent class.
* This agent will receive ontology updates from the ExecutionAgent.
* It will Validate concepts of the ResourceAgent.
*/
public class OntologyAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("OntologyAgent " + getAID().getName() + " created.");

        // Add the behaviour to receive ontology updates from the ExecutionAgent
        addBehaviour(new OntologyUpdateBehaviour());

        // Add the behaviour to validate concepts of the ResourceAgent
        //addBehaviour(new OntologyValidationBehaviour());

        // Register the OntologyAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("OntologyAgent");
        sd.setName("OntologyAgent");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("OntologyAgent terminated.");

        try {
            // Deregister from the yellow pages
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private class OntologyUpdateBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Process the ontology update
                String ontologyUpdate = msg.getContent();
                System.out.println("OntologyAgent " + getAID().getName() + " received ontology update: " + ontologyUpdate);

                // Send the response to the ResourceAgent
                ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                response.addReceiver(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME));
                response.setOntology("Concept-Validation");
                send(response);
            } else {
                block();
            }
        }
    }

    /*private class OntologyValidationBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Validate concepts of the ResourceAgent
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchSender(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME))));
            ACLMessage request = myAgent.receive(mt);

            if (request != null) {
                // Process the request

            } else {
                block();
            }
        }
    }*/
}
