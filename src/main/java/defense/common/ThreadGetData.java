package defense.common;

import mekanism.common.util.MekanismUtils;

/**
 * Thread used to retrieve data from the Mekanism server.
 * @author AidanBrady
 *
 */
public class ThreadGetData extends Thread
{
	public ThreadGetData()
	{
		setDaemon(true);
		start();
	}

	@Override
	public void run()
	{
		DefenseTech.latestVersionNumber = getLatestVersion();
		DefenseTech.recentNews = getRecentNews();

		MekanismUtils.updateDonators();
	}
	
	public static String getLatestVersion()
	{
		String[] text = MekanismUtils.merge(MekanismUtils.getHTML("https://dl.dropbox.com/u/90411166/Mod%20Versions/DefenseTech.txt")).split(":");
		if(text.length > 1 && !text[0].contains("UTF-8") && !text[0].contains("HTML") && !text[0].contains("http")) return text[0];
		return "null";
	}

	public static String getRecentNews()
	{
		String[] text = MekanismUtils.merge(MekanismUtils.getHTML("https://dl.dropbox.com/u/90411166/Mod%20Versions/DefenseTech.txt")).split(":");
		if(text.length > 1 && !text[1].contains("UTF-8") && !text[1].contains("HTML") && !text[1].contains("http")) return text[1];
		return "null";
	}
}
