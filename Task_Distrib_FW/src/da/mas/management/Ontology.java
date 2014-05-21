package da.mas.management;

public class Ontology {
	
	/** sent from created agents to dispatcher */
	public static final String READY_LOCAL_AGENT_INIT = "ready_local_agent_init";
	public static final String READY_REMOTE_AGENT_INIT = "ready_remote_agent_init";
	/** sent from ready agents to dispatcher */
	public static final String READY_LOCAL_AGENT = "ready_local_agent";
	public static final String READY_REMOTE_AGENT = "ready_remote_agent";
	/** send data from dispatcher  to local */
	public static final String TASK_FROM_DISPATCHER_TO_LOCAL = "task_from_dispatcher_to_local";
	/** send data from/to local  to/from remote */
	public static final String TASK_FROM_LOCAL_TO_REMOTE = "task_from_local_to_remote";
	public static final String TASK_FROM_REMOTE_TO_LOCAL = "task_from_remote_to_local";
	/** request for ready local agent sent to dispatcher */
	public static final String REQUEST_FOR_READY_LOCAL_AGENT = "request_for_ready_local_agent";
	/** not available agent . wait */ 
	public static final String NOT_AVAILABLE_WAIT = "not_available_wait";
	/** Task aborted  in remote worker due to timeout */ 
	public static final String TASK_ABORTED_IN_REMOTE = "task_aborted_in _remote";
	/** proposal for ready local agent sent to remote */
	public static final String PROPOSAL_FOR_READY_LOCAL_AGENT = "proposal_for_ready_local_agent";
	/** Task workflow messages */
	public static final String TASK_WORKFLOW = "task_workflow";
	

}
