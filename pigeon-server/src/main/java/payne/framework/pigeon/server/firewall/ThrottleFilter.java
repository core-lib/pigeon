package payne.framework.pigeon.server.firewall;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import payne.framework.pigeon.core.exception.ForbiddenInvocationException;
import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.filtration.FilterChain;
import payne.framework.pigeon.core.protocol.Channel;

public class ThrottleFilter implements Filter<Channel> {
	private final Map<String, Frequency> map = new ConcurrentHashMap<String, Frequency>();

	/**
	 * 时间间隔
	 */
	private long interval = 100;
	/**
	 * 重复次数
	 */
	private int count = 10;
	/**
	 * 如果重复的次数内以时间间隔内请求服务端 那么将被冻结这么长的时间
	 */
	private long freeze = 10 * 1000;

	public ThrottleFilter() {
		super();
	}

	public ThrottleFilter(long interval, int count, long freeze) {
		super();
		this.interval = interval;
		this.count = count;
		this.freeze = freeze;
	}

	public void filtrate(Channel channel, FilterChain<Channel> chain) throws Exception {
		String host = channel.getAddress().toString().split(":")[0];

		if (map.containsKey(host)) {
			Frequency frequency = map.get(host);
			synchronized (frequency) {
				if (frequency.frozen) {
					if (System.currentTimeMillis() - frequency.time < freeze) {
						throw new ForbiddenInvocationException("frozen");
					} else {
						frequency.reset();
					}
				} else if (System.currentTimeMillis() - frequency.time < interval) {
					if (frequency.record() > count) {
						frequency.freeze();
						throw new ForbiddenInvocationException("request too frequently");
					}
				} else {
					frequency.reset();
				}
			}
		} else {
			map.put(host, new Frequency());
		}
		chain.go(channel);
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getFreeze() {
		return freeze;
	}

	public void setFreeze(long freeze) {
		this.freeze = freeze;
	}

	private static class Frequency {
		public long time = System.currentTimeMillis();
		public int count = 1;
		public boolean frozen = false;

		public int record() {
			time = System.currentTimeMillis();
			return ++count;
		}

		public void reset() {
			time = System.currentTimeMillis();
			count = 1;
			frozen = false;
		}

		public void freeze() {
			frozen = true;
		}

	}

}
