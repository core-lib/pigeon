package payne.framework.pigeon.integration.web.spring;

import java.net.SocketAddress;
import java.util.Set;

import org.springframework.context.ApplicationContextAware;

import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.server.InvocationContext;

public interface SpringInvocationContext extends InvocationContext, ApplicationContextAware {

	void setPort(int port);

	void setAddress(SocketAddress address);

	void setAutoscan(boolean autoscan);

	void setManagements(Set<Object> managements);
	
	void setFilters(Set<Filter<Channel>> filters);

}
