
Task Distribution Framework **TDF**
===================================
Task distribution framework based on the **Jade** multi-agent system.


Getting started
=============

Using the framework allows you to : 

1. Define data and processing details for your tasks.
2. Define an interface for :

 	- managing the multi-agent platform
 	- preparing tasks and adding them to the queue
 	- configuring, running and monitoring task execution.


## Task data
Expand the class ``AbstractTaskDataObject`` to define your own data structures.
``` java

import da.mas.task.AbstractTaskDataObject;

public class DummyDataObject extends AbstractTaskDataObject{
	
	  // Define your structure here..
	
}
```

## Task processing
There are three stages in executing a task:

1. Initialization (**LocalPreTask**)
2. Execution (**RemoteTask**)
3. Finalization (**LocalPostTask**)

### Initialization
Tasks are initialized on the local machine. Simply expand the class  ``AbstractPreLocalTask``

``` java

import da.mas.task.AbstractLocalPreTask;

public class DummyLocalPreTask extends AbstractLocalPreTask {

	@Override
	public void doLocalPreTask() {
	
			// Define the actions to be carried out here
		
	}

}


```

### Execution
Tasks are executed on one of the connected remote machines. Simply expand the class ``AbstractRemoteTask``

``` java


import da.mas.task.AbstractRemoteTask;

public class DummyRemoteTask extends AbstractRemoteTask{
	
	@Override
	public void doRemoteTask() {
		// Define the actions to be carried out here
        // This is where resource-consuming actions
        // must be defined
	}
	
}


```

### Finalization
Tasks are finalized on the local machine. Simply expand the class ``AbstractPostLocalTask``

``` java

import da.mas.task.AbstractLocalPostTask;

public class DummyLocalPostTask extends AbstractLocalPostTask{
	
	@Override
	public void doLocalPostTask() {
		// Define the actions to be carried out here
	}

	
}


```

### Define the application interface.

The easiest way to do this is to expand the class  ``da.gui.JFrameGui``

This class implements the ``PlatformEventListener`` and ``TaskWorkflowEvent`` interfaces.

* ``PlatformEventListener`` allows you to manage the Jade multi-agent platform.
* ``TaskWorkflowEvent`` allows you to monitor the task execution process.

