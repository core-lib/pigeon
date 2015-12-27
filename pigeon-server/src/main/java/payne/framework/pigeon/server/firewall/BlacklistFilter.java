package payne.framework.pigeon.server.firewall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import payne.framework.pigeon.core.exception.ForbiddenInvocationException;
import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.filtration.FilterChain;
import payne.framework.pigeon.core.protocol.Channel;

public class BlacklistFilter implements Filter<Channel> {
	private final Map<String, Pattern> map = new HashMap<String, Pattern>();

	public BlacklistFilter(String path) throws IOException, PatternSyntaxException {
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				// 忽略空行 和 注释行
				if (line.trim().equals("") || line.trim().startsWith("#")) {
					continue;
				}
				map.put(line, Pattern.compile(line));
			}
		} finally {
			br.close();
			isr.close();
			in.close();
		}
	}

	public void filtrate(Channel channel, FilterChain<Channel> chain) throws Exception {
		SocketAddress address = channel.getAddress();
		if (address instanceof InetSocketAddress) {
			InetSocketAddress isa = (InetSocketAddress) address;
			String ip = isa.getAddress().getHostAddress();
			for (Pattern pattern : map.values()) {
				if (pattern.matcher(ip).matches()) {
					throw new ForbiddenInvocationException("forbidden");
				}
			}
			chain.go(channel);
		} else {
			throw new ForbiddenInvocationException("unknown client address " + address);
		}
	}

	public Pattern add(String blacklist) {
		return map.put(blacklist, Pattern.compile(blacklist));
	}

	public Pattern remove(String blacklist) {
		return map.remove(blacklist);
	}

	public void clear() {
		map.clear();
	}

}
