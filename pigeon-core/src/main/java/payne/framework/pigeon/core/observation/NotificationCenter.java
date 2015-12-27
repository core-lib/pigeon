package payne.framework.pigeon.core.observation;

public interface NotificationCenter {

	void attach(String event, Observer observer);

	void detach(String event, Observer observer);

	void notify(Event event);

}
