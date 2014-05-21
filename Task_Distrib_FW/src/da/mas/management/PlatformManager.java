package da.mas.management;

import da.test.TestGui;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class PlatformManager{
	
	public static void initPlatform(String guiClassName){
		try {
			Runtime runtime = Runtime.instance();

			Profile profile = new ProfileImpl();
			profile.setParameter(Profile.CONTAINER_NAME, "MAIN_CONTAINER");
			profile.setParameter(Profile.GUI, "true");
			AgentContainer mc = runtime.createMainContainer(profile);
			
			AgentController agentDispatcher = mc
					.createNewAgent("agent_dispatcher",
							AgentTaskDispatcher.class.getName(),
							new Object[]{guiClassName});
			agentDispatcher.start();
			
			Profile profile2 = new ProfileImpl();
			profile2.setParameter(Profile.MAIN_HOST, "localhost");
			profile2.setParameter(Profile.CONTAINER_NAME, "LOCAL_CONTAINER");
			AgentContainer agentContainer = runtime
					.createAgentContainer(profile2);
			agentContainer.start();
			/*
			for (int i = 0; i < 10; i++) {
				AgentController agentController2 = agentContainer
						.createNewAgent("ALW_" + i,
								AgentLocalWorker.class.getName(),
								null);
				agentController2.start();
			}
			
			
			Profile profile3 = new ProfileImpl();
			profile3.setParameter(Profile.MAIN_HOST, "localhost");
			profile3.setParameter(Profile.CONTAINER_NAME, "REMOTE_CONTAINER");
			AgentContainer agentContainer2 = runtime
					.createAgentContainer(profile3);
			agentContainer2.start();
			*/
			/*
			for (int i = 0; i < 10; i++) {
				AgentController agentController3 = agentContainer2
						.createNewAgent("ARW_" + i,
								AgentRemoteWorker.class.getName(),
								null);
				agentController3.start();
			}
			*/

		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
