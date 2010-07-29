package org.jdiscript.events;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.MonitorContendedEnterEvent;
import com.sun.jdi.event.MonitorContendedEnteredEvent;
import com.sun.jdi.event.MonitorWaitEvent;
import com.sun.jdi.event.MonitorWaitedEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.EventRequest;


public class DebugEventDispatcher {
	
	private List<DebugEventHandler> handlers 
		= new ArrayList<DebugEventHandler>();

	public void addHandler(DebugEventHandler handler) {
		handlers.add(handler);
	}
	
	/**
	 * Dispatch incoming events
	 */
	public void dispatch(Event event, int suspendPolicy) {
		final EventRequest request = event.request();
		if (request == null) {
	    	for(DebugEventHandler handler: handlers) {
	    		handler.notifySuspendPolicy(suspendPolicy);
	    		//VM Events
	    		if(event instanceof VMStartEvent) {
		    		handler.vmStart((VMStartEvent)event);
		        } else if(event instanceof VMDisconnectEvent) {
		    		handler.vmDisconnect((VMDisconnectEvent)event);
		    	} else if(event instanceof VMDeathEvent) {
		    		handler.vmDeath((VMDeathEvent)event);
		    	} else {
		    		throw new RuntimeException(
		    			"Event " + event + " missing EventRequest"
		    		);
		    	}
	    	}
	    	return;
		}
		
		final DebugEventHandler handler = 
			(DebugEventHandler)request.getProperty(DebugEventHandler.PROP_KEY);
		
		if(handler == null) {
			throw new RuntimeException("No handler specified for event " + event);
		}
		
		handler.notifySuspendPolicy(suspendPolicy);
		
        if(event instanceof BreakpointEvent) {
            handler.breakpoint((BreakpointEvent)event);
        }

        else if(event instanceof StepEvent) {
            handler.step((StepEvent)event);
        }

        else if(event instanceof ExceptionEvent) {
            handler.exception((ExceptionEvent)event);
        }

        //Method Events
        else if(event instanceof MethodEntryEvent) {
            handler.methodEntry((MethodEntryEvent)event);
        } else if(event instanceof MethodExitEvent) {
            handler.methodExit((MethodExitEvent)event);
        }

        //Monitor Events
        else if(event instanceof MonitorWaitEvent) {
            handler.monitorWait((MonitorWaitEvent)event);
        } else if(event instanceof MonitorWaitedEvent) {
            handler.monitorWaited((MonitorWaitedEvent)event);
        } else if(event instanceof MonitorContendedEnterEvent) {
            handler.monitorContendedEnter((MonitorContendedEnterEvent)event);
        } else if(event instanceof MonitorContendedEnteredEvent) {
            handler.monitorContendedEntered((MonitorContendedEnteredEvent)event);
        }

        //Watchpoint Events
        else if(event instanceof AccessWatchpointEvent) {
        	handler.accessWatchpoint((AccessWatchpointEvent)event);
        } else if(event instanceof ModificationWatchpointEvent) {
            handler.modificationWatchpoint((ModificationWatchpointEvent)event);
        }

		//Threading Events
        else if(event instanceof ThreadStartEvent) {
            handler.threadStart((ThreadStartEvent)event);
        } else if(event instanceof ThreadDeathEvent) {
            handler.threadDeath((ThreadDeathEvent)event);
        }

		//Class Events
        else if(event instanceof ClassPrepareEvent) {
            handler.classPrepare((ClassPrepareEvent)event);
        } else if(event instanceof ClassUnloadEvent) {
            handler.classUnload((ClassUnloadEvent)event);
        }

		//VM Events
        else if(event instanceof VMStartEvent) {
            handler.vmStart((VMStartEvent)event);
        } else if(event instanceof VMDisconnectEvent) {
            handler.vmDisconnect((VMDisconnectEvent)event);
        } else if(event instanceof VMDeathEvent) {
            handler.vmDeath((VMDeathEvent)event);
        }

        //Catchalls
        else if(event instanceof WatchpointEvent) {
            handler.watchpoint((WatchpointEvent)event);
        } else if(event instanceof LocatableEvent) {
            handler.locatable((LocatableEvent)event);
        } else {
			throw new Error("Unexpected event type");
		}
	}
}
