package payne.framework.pigeon.core;

public interface Constants {

	String CONTEXT_STARTUP_EVENT_NAME = "context-startup-event-name";
	String CONTEXT_SHUTDOWN_EVENT_NAME = "context-shutdown-event-name";
	String CONNECTION_ACCEPT_EVENT_NAME = "connection-accept-event-name";
	String CONNECTION_REFUSE_EVENT_NAME = "connection-refuse-event-name";
	String CONNECTION_ERROR_EVENT_NAME = "connection-error-event-name";
	String CONNECTION_CLOSE_EVENT_NAME = "connection-close-event-name";

	String CHANNEL_INVOCATION_ATTRIBUTE_KEY = "channel.invocation.attribute.key";
	String CHANNEL_SECRET_KEY_ATTRIBUTE_KEY = "channel.secret.key.attribute.key";
	String CHANNEL_PRIVATE_KEY_ATTRIBUTE_KEY = "channel.private.key.attribute.key";
	String CHANNEL_PUBLIC_KEY_ATTRIBUTE_KEY = "channel.public.key.attribute.key";

	int SIDE_SERVER = 0;
	int SIDE_CLIENT = 1;

	String WEB_INVOCATION_CONTEXT_ATTRIBUTE_KEY = "web.invocation.context.attribute.key";

}
