package com.ue.common.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {

	public static String versionNameAfterCheck;
	public static String downloadLinkAfterCheck;

	public enum UpdateResult {
		NO_UPDATE, UPDATE_AVAILABLE, NO_UPDATE_INFORMATION
	}

	/**
	 * Check for updates on bukkit.org.
	 * 
	 * @param currentVersion
	 * @return update result
	 */
	public static UpdateResult checkForUpdate(String currentVersion) {

		try {
			URL url = new URL("https://servermods.forgesvc.net/servermods/files?projectIds=301961");
			final URLConnection conn = url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setDoOutput(true);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String response = reader.readLine();
			final JSONArray array = (JSONArray) JSONValue.parse(response);
			if (array.isEmpty()) {
				return UpdateResult.NO_UPDATE_INFORMATION;
			}
			JSONObject latestUpdate = (JSONObject) array.get(array.size() - 1);
			String name = (String) latestUpdate.get("name");
			versionNameAfterCheck = name;
			downloadLinkAfterCheck = (String) latestUpdate.get("fileUrl");
			String version = name.substring(name.indexOf(" ") + 1);
			int result = currentVersion.compareTo(version);
			if (result >= 0) {
				return UpdateResult.NO_UPDATE;
			} else {
				return UpdateResult.UPDATE_AVAILABLE;
			}
		} catch (IOException e) {
			return UpdateResult.NO_UPDATE_INFORMATION;
		}
	}
}