
Task Distribution Framework **TDF**
===================================
Framework de distribution de taches basé sur le système multi-agents **Jade**.

Pour utiliser le framwork, vous devez définir les données et les traitements de vos taches.


Mise en route
=============
## Données de la tache
Eendre la classe ``AbstractTaskDataObject`` pour définir vos propres structures de données. 
``` java

import da.mas.task.AbstractTaskDataObject;

public class DummyDataObject extends AbstractTaskDataObject{
	
	  // Définir votre structure ici.
	
}
```

## Traitements de la tache
Une tache est exécutée en trois étapes :

1. Initialisation (**LocalPreTask**)
2. Exécution (**RemoteTask**)
3. Finalisation (**LocalPostTask**)

###Initialisation
L'initialisation s'exécute sur la machine locale. Il suffit d'étendre la classe ``AbstractPreLocalTask``

``` java

import da.mas.task.AbstractLocalPreTask;

public class DummyLocalPreTask extends AbstractLocalPreTask {

	@Override
	public void doLocalPreTask() {
	
			// Définir ici les actions à effectuer
		
	}

}


```

###Exécution
L'exécution s'effectue sur l'une des machines distantes connectées. Il suffit d'étendre la classe ``AbstractRemoteTask``

``` java


import da.mas.task.AbstractRemoteTask;

public class DummyRemoteTask extends AbstractRemoteTask{
	
	@Override
	public void doRemoteTask() {
		// Définir ici les actions à effectuer
		// C'est à ce niveau qu'il faut définir les actions
		// consommatrices de ressources
	}
	
}


```

###Finalisation
La finalisation de la tache s'exécute sur la machine locale. Il suffit d'étendre la classe ``AbstractPostLocalTask``

``` java

import da.mas.task.AbstractLocalPostTask;

public class DummyLocalPostTask extends AbstractLocalPostTask{
	
	@Override
	public void doLocalPostTask() {
		// Définir ici les actions à effectuer
	}

	
}


```

##Définir l'interface de l'application.

Le plus simple est d'étendre la classe ``da.gui.JFrameGui``

Cette classe implémente les interfaces ``PlatformEventListener`` et ``TaskWorkflowEvent``

* ``PlatformEventListener`` permet la gestion de la plateforme multi-agents Jade. 
* ``TaskWorkflowEvent`` permet de suivre le processus d'exécution des taches.

