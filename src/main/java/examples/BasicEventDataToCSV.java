package examples;

import java.io.File;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Set;

import cc.kave.commons.model.events.IDEEvent;

import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;

//for writing into csv file
import java.io.FileWriter;
import java.io.IOException;

public class BasicEventDataToCSV {

	private String eventsDir;
	private static final String COMMA_DELIMITER = ",";		
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FILE_NAME = "EventData.csv";
	private static final String FILE_HEADER = "ID,Event,TriggerTime,DurationTime";
	
	private FileWriter fileWriter = null;
	
	public BasicEventDataToCSV(String eventsDir) {
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
			//while (ra.hasNext() && (numProcessedEvents++ < 200)) {
			while (ra.hasNext()) {
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
		//FileWriter fileWriter = null;		
		
		try {
			String ideSessionUUID = e.IDESessionUUID;
			String eventType = e.getClass().getSimpleName();	
			ZonedDateTime triggerTime = e.getTriggeredAt();
			Duration eventDuration = e.Duration; //check API why no getter function
			
			fileWriter = new FileWriter(FILE_NAME,true);			
			fileWriter.append(ideSessionUUID);			
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(eventType);			
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(String.valueOf(triggerTime));
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(String.valueOf(eventDuration));
			fileWriter.append(NEW_LINE_SEPARATOR);
		}
		catch (Exception exception) {
			System.out.println("Error in CsvFileWriter !!!");
			exception.printStackTrace();
		}
		finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			}
			catch (IOException exception) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				exception.printStackTrace();
			}
		}
	}	
	
}
