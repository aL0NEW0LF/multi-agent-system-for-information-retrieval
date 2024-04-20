/**
 * Created by MOHAMED AMINE FAKHRE-EDDINE
 * Email: mohamedfakhreeddine2019@gmail.com
 * Github: github.com/aL0NEW0LF
 * Date: 4/20/2024
 * Time: 2:12 PM
 * Project Name: multi-agent-system-for-information-retrieval
 */

package org.sma.project;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MainContainer {
    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.instance();
            Properties properties = new ExtendedProperties();
            properties.setProperty(Profile.GUI, "true");
            Profile profile = new ProfileImpl(properties);
            AgentContainer mainContainer = runtime.createMainContainer(profile);
            mainContainer.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
