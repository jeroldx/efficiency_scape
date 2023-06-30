package com.efficiencyScape.DataTracking.ActivityEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.inject.Singleton;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
@Singleton
public class FileManager
{
	private static final String EVENT_FILE = "activity-events.esevents";
	private static final File EVENT_RECORD_DIR = new File(RUNELITE_DIR, "es-event-log");
	@Setter
	private boolean flaggedDelete = false;

	public void onStartUp()
	{
		if (EVENT_RECORD_DIR.mkdir())
		{
			log.debug("Created ES Event directory.");
		}
	}

	public BufferedWriter getWriter(boolean append)
	{
		if (flaggedDelete)
		{
			return null;
		}
		File file = new File(EVENT_RECORD_DIR, EVENT_FILE);
		try
		{
			return new BufferedWriter(new FileWriter(file, append));
		}
		catch (IOException e)
		{
			log.error("'{}' in getWriter: {}\n{}", e.getClass(), e.getMessage(), e.getStackTrace());
			return null;
		}
	}

	public boolean deleteEventFile()
	{
		File file = new File(EVENT_RECORD_DIR, EVENT_FILE);
		if (file.delete())
		{
			flaggedDelete = false;
			return true;
		}
		return false;
	}

	public boolean checkFile()
	{
		if (EVENT_RECORD_DIR.mkdir())
		{
			log.debug("Created event log file {}", EVENT_FILE);
			return false;
		}
		File file = new File(EVENT_RECORD_DIR, EVENT_FILE);
		return file.isFile();
	}

	public BufferedReader getReader()
	{
		if(checkFile())
		{
			File file = new File(EVENT_RECORD_DIR, EVENT_FILE);
			try
			{
				return new BufferedReader(new FileReader(file));
			}
			catch (IOException e)
			{
				log.error("'{}' in getReader: {}\n{}", e.getClass(), e.getMessage(), e.getStackTrace());
			}
		}
		return null;
	}
}
