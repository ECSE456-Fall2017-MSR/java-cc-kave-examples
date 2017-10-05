package examples;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.Set;

import cc.kave.commons.model.events.ActivityEvent;
import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.ErrorEvent;
import cc.kave.commons.model.events.IDEEvent;
import cc.kave.commons.model.events.InfoEvent;
import cc.kave.commons.model.events.NavigationEvent;
import cc.kave.commons.model.events.SystemEvent;
import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import cc.kave.commons.model.events.userprofiles.UserProfileEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import cc.kave.commons.model.events.visualstudio.DebuggerEvent;
import cc.kave.commons.model.events.visualstudio.DocumentEvent;
import cc.kave.commons.model.events.visualstudio.EditEvent;
import cc.kave.commons.model.events.visualstudio.FindEvent;
import cc.kave.commons.model.events.visualstudio.IDEStateEvent;
import cc.kave.commons.model.events.visualstudio.SolutionEvent;
import cc.kave.commons.model.events.visualstudio.WindowEvent;
import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;


public class EventToCSV {
	private String eventsDir;

	public EventToCSV(String eventsDir) {
		this.eventsDir = eventsDir;
	}

	public void run() {

		System.out.printf("looking (recursively) for events in folder %s\n", new File(eventsDir).getAbsolutePath());

		/*
		 * Each .zip that is contained in the eventsDir represents all events that we
		 * have collected for a specific user, the folder represents the first day when
		 * the user uploaded data.
		 */
		Set<String> userZips = IoHelper.findAllZips(eventsDir);

		for (String userZip : userZips) {
			System.out.printf("\n#### processing user zip: %s #####\n", userZip);
			processUserZip(userZip);
		}
	}

	private void processUserZip(String userZip) {
		int numProcessedEvents = 0;
		// open the .zip file ...
		try (IReadingArchive ra = new ReadingArchive(new File(eventsDir, userZip))) {
			// ... and iterate over content.
			// the iteration will stop after 200 events to speed things up.
			while (ra.hasNext() && (numProcessedEvents++ < 200)) {
				/*
				 * within the userZip, each stored event is contained as a single file that
				 * contains the Json representation of a subclass of IDEEvent.
				 */
				IDEEvent e = ra.getNext(IDEEvent.class);

				// the events can then be processed individually
				processEvent(e);
			}
		}
	}

	/*
	 * if you review the type hierarchy of IDEEvent, you will realize that several
	 * subclasses exist that provide access to context information that is specific
	 * to the event type.
	 * 
	 * To access the context, you should check for the runtime type of the event and
	 * cast it accordingly.
	 * 
	 * As soon as I have some more time, I will implement the visitor pattern to get
	 * rid of the casting. For now, this is recommended way to access the contents.
	 */
	private void processEvent(IDEEvent e) {

		if (e instanceof CommandEvent) {
			process((CommandEvent) e);
		}
		else if (e instanceof CompletionEvent) {
			process((CompletionEvent) e);
		}
		else if (e instanceof ErrorEvent) {
			process((ErrorEvent) e);
		}
		else if (e instanceof EditEvent) {
			process((EditEvent) e);
		} 
		else {
			/*
			 * CommandEvent and Completion event are just two examples, please explore the
			 * type hierarchy of IDEEvent to find other types and review their API to
			 * understand what kind of context data is available.
			 * 
			 * We include this "fall back" case, to show which basic information is always
			 * available.
			 */
			processBasic(e);
		}
	}
	
	/*
 	1.1 Activity Event
	1.2 Command Event
	1.3 Completion Event
	1.4 Build Event
	1.5 Debugger Event
	1.6 Document Event
	1.7 Edit Event
	1.8 Find Event
	1.9 IDEState Event
	1.10 Solution Event
	1.11 Window Event
	1.12 Version Control Event
	1.13 User Profile Event
	1.14 Navigation Event
	1.15 System Event
	1.16 Test Run Event
	1.17 Info Event
	1.18 Error Event
	 */
	
	//new
	private void processBasic(IDEEvent e) {
		String eventType = e.getClass().getSimpleName();		
		ZonedDateTime triggerTime = e.getTriggeredAt();

		System.out.printf("found an %s that has been triggered at: %s)\n", eventType, triggerTime);
	}
	
	private void process(ActivityEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}	
	
	private void process(CommandEvent ce) {
		System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}

	private void process(CompletionEvent e) {
		ISST snapshotOfEnclosingType = e.context.getSST();
		String enclosingTypeName = snapshotOfEnclosingType.getEnclosingType().getFullName();

		System.out.printf("found a CompletionEvent (was triggered in: %s)\n", enclosingTypeName);
	}
	
	//new
	private void process(BuildEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	
	//new
	private void process(DebuggerEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	
	//new
	private void process(DocumentEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	
	private void process(EditEvent ee) {
		System.out.printf("found a Edit Event (number of changes: %s)\n", ee.NumberOfChanges);
	}
	
	//new
	private void process(FindEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(IDEStateEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(SolutionEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(WindowEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(VersionControlEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(UserProfileEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(NavigationEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	//new
	private void process(SystemEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	
	private void process(TestRunEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	
	private void process(InfoEvent ce) {
		//System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}
	
	private void process(ErrorEvent ee) {
		//test code
		System.out.printf("found a ErrorEvent (was triggered in: %s)\n", ee.Content);
		System.out.printf("ErrorEvent has Content %s\n", ee.Content);
		System.out.printf("Error Event has Stacktrace %s\n", ee.StackTrace.toString());		
	}
}
