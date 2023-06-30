package com.efficiencyScape;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EfficiencyScapePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(EfficiencyScapePlugin.class);
		RuneLite.main(args);
	}
}