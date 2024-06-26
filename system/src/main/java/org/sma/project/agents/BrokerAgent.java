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

import java.util.concurrent.TimeUnit;

/*
* This is the BrokerAgent class.
* This agent will receive the user's request from the UserAgent.
* It will send the user's request to the ResourceAgent.
* It will receive the response from the ResourceAgent.
* It sends the task to the ExecutionAgent.
* It will send the result to the UserAgent.
*/
public class BrokerAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("BrokerAgent " + getAID().getName() + " created.");

        // Add the behaviour to receive the user's request from the UserAgent
        addBehaviour(new BrokerRequestBehaviour());

        // Add the behaviour to send the result to the UserAgent
        addBehaviour(new BrokerResultBehaviour());

        // Register the BrokerAgent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("BrokerAgent");
        sd.setName("BrokerAgent");
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
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchSender(new jade.core.AID("UserAgent", jade.core.AID.ISLOCALNAME)));
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Send the response to the ExecutionAgent
                ACLMessage task = new ACLMessage(ACLMessage.INFORM);
                task.addReceiver(new jade.core.AID("ExecutionAgent", jade.core.AID.ISLOCALNAME));
                task.setContent("Task");
                myAgent.send(task);

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

    private class BrokerResultBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(new jade.core.AID("ResourceAgent", jade.core.AID.ISLOCALNAME)));
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
