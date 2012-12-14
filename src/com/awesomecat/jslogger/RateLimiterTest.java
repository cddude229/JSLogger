package com.awesomecat.jslogger;

import org.junit.Test;
import static org.junit.Assert.*;


public class RateLimiterTest {
	@Test
	public void testIsUserLimitedByData() throws Exception {
		RateLimiter rateLimiter = new RateLimiter();
		int sessionId = 1;
		rateLimiter.addData(sessionId, 0, RateLimiter.dataLimit-1);
		assertFalse("User should not be rate limited if not at dataLimit", rateLimiter.isUserLimited(sessionId));
		rateLimiter.addData(sessionId, 0, 1);
		assertTrue("User should be rate limited exactly at dataLimit", rateLimiter.isUserLimited(sessionId));
		rateLimiter.addData(sessionId, 0, 1);
		assertTrue("User should be rate limited just after dataLimit", rateLimiter.isUserLimited(sessionId));
	}

	@Test
	public void testIsUserLimitedByLogs() throws Exception {
		RateLimiter rateLimiter = new RateLimiter();
		int sessionId = 1;
		rateLimiter.addData(sessionId, RateLimiter.logsLimit-1, 0);
		assertFalse("User should not be rate limited if not at logsLimit", rateLimiter.isUserLimited(sessionId));
		rateLimiter.addData(sessionId, 1, 0);
		assertTrue("User should be rate limited exactly at logsLimit", rateLimiter.isUserLimited(sessionId));
		rateLimiter.addData(sessionId, 1, 0);
		assertTrue("User should be rate limited just after logsLimit", rateLimiter.isUserLimited(sessionId));
	}

	@Test
	public void testIsUserLimitedByEither() throws Exception {
		RateLimiter rateLimiter = new RateLimiter();
		int sessionId = 1;
		rateLimiter.addData(sessionId, RateLimiter.logsLimit, RateLimiter.dataLimit);
		assertTrue("User should be rate limited if exactly at logsLimit and dataLimit", rateLimiter.isUserLimited(sessionId));
		rateLimiter.addData(sessionId, 1, 1);
		assertTrue("User should be rate limited just after logsLimit and dataLimit", rateLimiter.isUserLimited(sessionId));
	}

}
