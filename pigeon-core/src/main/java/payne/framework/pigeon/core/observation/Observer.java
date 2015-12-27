package payne.framework.pigeon.core.observation;

public interface Observer {

	void react(NotificationCenter notificationCenter, Event event);

}
