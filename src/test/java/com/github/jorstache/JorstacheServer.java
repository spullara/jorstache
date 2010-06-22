package com.github.jorstache;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sampullara.mustache.MustacheException;
import jornado.Config;
import jornado.FixedRoute;
import jornado.JettyService;
import jornado.JornadoModule;
import jornado.Method;
import jornado.RegexRoute;
import jornado.Request;
import jornado.RequestFactory;
import jornado.Response;
import jornado.RouteHandler;
import jornado.UserService;
import org.joptparse.OptionParser;

import java.io.File;

/**
 * TODO: Edit this
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 1:55:42 PM
 */
public class JorstacheServer {
  private final Module module;
  public static Injector injector;

  /**
   * Our jornado guice module
   */
  static class Module extends JornadoModule {
    protected Module(final Config config) {
      super(config, Lists.newArrayList(
              new RouteHandler<Request>(new FixedRoute(Method.GET, "/"), HomeHandler.class),
              new RouteHandler<Request>(new FixedRoute(Method.GET, "/fred"), FredHandler.class),
              new RouteHandler<Request>(new FixedRoute(Method.GET, "/timeout"), TimeoutHandler.class),
              new RouteHandler<Request>(new FixedRoute(Method.GET, "/fred2"), CodebehindFredHandler.class),
              new RouteHandler<Request>(new RegexRoute(Method.GET, "/person/([A-Za-z0-9]+)", "name"), PersonHandler.class)));
    }

    @Override
    protected void configure() {
      super.configure();
      bind(UserService.class).to(JorstacheUserService.class);
      bind(RequestFactory.class).to(JorstacheRequestFactory.class);
    }
  }

  public JorstacheServer(final Config config) {
    module = new Module(config);
  }

  public static void main(String[] args) throws Exception {
    final Config config = createConfig(args);
    JorstacheServer app = new JorstacheServer(config);
    injector = Guice.createInjector(app.module); // initialize the object tree with Guice
    injector.getInstance(JettyService.class).startAndWait(); // get the jetty service and start it
  }

  private static Config createConfig(String[] args) {
    final Config config = new Config();
    final OptionParser parser = new OptionParser(config);
    parser.parse(args);
    return config;
  }

  @Singleton
  static class HomeHandler extends MustacheHandler {
    public HomeHandler() throws MustacheException {
      super(new File("webapp/src"), "index.html");
    }

    @Override
    public Response handle(final Request request) {
      return super.handle(new Object() {
        String url = request.getReconstructedUrl();
      });
    }
  }

  @Singleton
  static class PersonHandler extends MustacheHandler {
    public PersonHandler() throws MustacheException {
      super(new File("webapp/src"), "person.html");
    }

    @Override
    public Response handle(final Request request) {
      return super.handle(new Object() {
        String name = request.getPathParameter("name");
      });
    }
  }

  @Singleton
  static class FredHandler extends CodebehindHandler {
    public FredHandler() throws MustacheException {
      super(new File("webapp/src"), "fred.html", "com/sampullara/fred/Fred.java", "com.sampullara.fred.Fred");
    }
  }

  @Singleton
  static class CodebehindFredHandler extends CodebehindHandler {
    public CodebehindFredHandler() throws MustacheException {
      super(new File("webapp/src"), "fred.html", CodeBehindFred.class);
    }
  }

  @Singleton
  static class TimeoutHandler extends CodebehindHandler {
    public TimeoutHandler() throws MustacheException {
      super(new File("webapp/src"), "timedout.html", "com.sampullara.fred.TimedOut");
    }
  }

  static class JorstacheUserService implements UserService<JorstacheUser> {
    public JorstacheUser load(String id) {
      return new JorstacheUser(id);
    }
  }

}
