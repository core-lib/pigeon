package payne.framework.pigeon.core.observation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SharedNotificationCenter implements NotificationCenter {
	private static SharedNotificationCenter instance;

	private final Map<String, Set<Observer>> observations;

	private SharedNotificationCenter() {
		this.observations = new HashMap<String, Set<Observer>>();
	}

	public static SharedNotificationCenter getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (SharedNotificationCenter.class) {
			if (instance != null) {
				return instance;
			}
			instance = new SharedNotificationCenter();
		}
		return instance;
	}

	public synchronized void attach(String event, Observer observer) {
		Set<Observer> observers = observations.get(event);
		if (observers == null) {
			observers = new LinkedHashSet<Observer>();
			observations.put(event, observers);
		}
		observers.add(observer);
	}

	public synchronized void detach(String event, Observer observer) {
		Set<Observer> observers = observations.get(event);
		if (observers == null) {
			return;
		}
		observers.remove(observer);
	}

	public synchronized void notify(Event event) {
		Set<Observer> observers = observations.get(event.getName());
		if (observers == null) {
			return;
		}
		for (Observer observer : observers) {
			observer.react(this, event);
		}
	}

}
