package com.aliyesilkanat.stalker.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.aliyesilkanat.stalker.trigger.instagram.InstagramFollowingsTrigger;

public class Starter {

	public static void main(String[] args) {
		List<String> setStalkedPeople = setStalkedPeople();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				for (String userId : setStalkedPeople) {
					new InstagramFollowingsTrigger(userId).execute();
				}

			}
		}, 0, 15, TimeUnit.MINUTES);
	}

	private static List<String> setStalkedPeople() {
		List<String> users = new ArrayList<String>();
		users.add("239984780");
		users.add("219462066");
		users.add("1037882254");
		users.add("1431667276");
		users.add("1374288377");
		return users;
	}

}
