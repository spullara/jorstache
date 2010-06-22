package com.sampullara.fred;

import com.github.jorstache.TimeoutHelper;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Show how timeouts work.
 * <p/>
 * User: sam
 * Date: Jun 21, 2010
 * Time: 3:25:17 PM
 */
public class TimedOut extends TimeoutHelper {

  Object content() {
    return new Object() {

      int timeout = 200;

      Random r = new SecureRandom();

      long milliseconds() {
        long start = System.currentTimeMillis();
        try {
          Thread.sleep(r.nextInt(2000) + 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return System.currentTimeMillis() - start;
      }

    };
  }

}
