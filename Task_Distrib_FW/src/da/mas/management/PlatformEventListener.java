package da.mas.management;

import jade.core.ContainerID;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.MovedAgent;

public interface PlatformEventListener {
	
	public void onContainerAdded(ContainerID container);

	public void onContainerRemoved(ContainerID container);

	public void onAgentBorn(BornAgent bornAgent);
	
	public void onAgentMove(MovedAgent movedAgent);
	
	public void onAgentDead(DeadAgent deadAgent);
}
