package da.mas.management;

import java.util.*;

import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.domain.introspection.MovedAgent;
import jade.domain.introspection.RemovedContainer;
import jade.gui.GuiAgent;

public abstract class AgentTaskDispatcherGui extends GuiAgent {
	protected ArrayList<ContainerID> availableContainers = new ArrayList<ContainerID>();
	protected ArrayList<AID> availableAgents = new ArrayList<AID>();
	private List<PlatformEventListener> listeners =
			new ArrayList<PlatformEventListener>();
	
	@Override
	protected void setup() {
		AMSSubscriber subscriber = new AMSSubscriber() {
			protected void installHandlers(Map handlers) {
				EventHandler addedHandler = new EventHandler() {
					public void handle(Event event) {
						AddedContainer addedContainer = (AddedContainer) event;
						availableContainers.add(addedContainer.getContainer());
						onContainerAdded(addedContainer.getContainer());
					}

				};
				handlers.put(IntrospectionVocabulary.ADDEDCONTAINER,
						addedHandler);

				EventHandler removedHandler = new EventHandler() {
					public void handle(Event event) {
						RemovedContainer removedContainer = (RemovedContainer) event;
						ArrayList<ContainerID> temp = new ArrayList<ContainerID>(
								availableContainers);
						for (ContainerID container : temp) {
							if (container.getID().equalsIgnoreCase(
									removedContainer.getContainer().getID())) {
								availableContainers.remove(container);
								onContainerRemoved(container);
							}
						}
					}

				};
				handlers.put(IntrospectionVocabulary.REMOVEDCONTAINER,
						removedHandler);
				
				EventHandler bornHandler = new EventHandler() {

					@Override
					public void handle(Event event) {
						BornAgent bornAgent = (BornAgent) event;
						availableAgents.add(bornAgent.getAgent());
						onAgentBorn(bornAgent);
					}

				};
				handlers.put(IntrospectionVocabulary.BORNAGENT,
						bornHandler);
				
				EventHandler moveHandler = new EventHandler() {

					@Override
					public void handle(Event event) {
						MovedAgent movedAgent = (MovedAgent) event;
						onAgentMove(movedAgent);
					}

				};
				handlers.put(IntrospectionVocabulary.MOVEDAGENT,
						moveHandler);
				
				EventHandler deadHandler = new EventHandler() {

					@Override
					public void handle(Event event) {
						DeadAgent deadAgent = (DeadAgent) event;
						availableAgents.remove(deadAgent.getAgent());
						onAgentDead(deadAgent);
					}

				};
				handlers.put(IntrospectionVocabulary.DEADAGENT,
						deadHandler);
			}
		};
		addBehaviour(subscriber);

	}
	
	public void addPlatformEventListener(PlatformEventListener pel){
		listeners.add(pel);
	}
	public void removePlatformEventListener(PlatformEventListener pel){
		if(listeners.contains(pel))
			listeners.remove(pel);
	}

	private void onContainerAdded(ContainerID container) {
		for(PlatformEventListener pel : listeners)
			pel.onContainerAdded(container);
	}

	private void onContainerRemoved(ContainerID container) {
		for(PlatformEventListener pel : listeners)
			pel.onContainerRemoved(container);
	}

	private void onAgentBorn(BornAgent bornAgent) {
		for(PlatformEventListener pel : listeners)
			pel.onAgentBorn(bornAgent);
	}
	

	private void onAgentMove(MovedAgent movedAgent) {
		for(PlatformEventListener pel : listeners)
			pel.onAgentMove(movedAgent);
	}
	
	private void onAgentDead(DeadAgent deadAgent) {
		for(PlatformEventListener pel : listeners)
			pel.onAgentDead(deadAgent);
	}

}
