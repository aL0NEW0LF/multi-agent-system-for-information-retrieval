package org.sma.project;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class SystemContainer {
    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.instance();
            Profile profile = new ProfileImpl(false);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            AgentContainer agentContainer = runtime.createAgentContainer(profile);
            AgentController UserAgentController = agentContainer.
                    createNewAgent("UserAgent", "org.sma.project.agents.UserAgent", new Object[]{});
            AgentController BrokerAgentController = agentContainer.
                    createNewAgent("BrokerAgent", "org.sma.project.agents.BrokerAgent", new Object[]{});
            AgentController ResourceAgentController = agentContainer.
                    createNewAgent("ResourceAgent", "org.sma.project.agents.ResourceAgent", new Object[]{});
            AgentController ExecutionAgentController = agentContainer.
                    createNewAgent("ExecutionAgent", "org.sma.project.agents.ExecutionAgent", new Object[]{});
            AgentController OntologyAgentController = agentContainer.
                    createNewAgent("OntologyAgent", "org.sma.project.agents.OntologyAgent", new Object[]{});

            UserAgentController.start();
            BrokerAgentController.start();
            ResourceAgentController.start();
            ExecutionAgentController.start();
            OntologyAgentController.start();

        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
