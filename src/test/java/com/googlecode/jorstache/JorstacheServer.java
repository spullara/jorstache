package com.googlecode.jorstache;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sampullara.mustache.MustacheCompiler;
import com.sampullara.mustache.MustacheException;
import jornado.Config;
import jornado.FixedRoute;
import jornado.Handler;
import jornado.JettyService;
import jornado.JornadoModule;
import jornado.Method;
import jornado.RegexRoute;
import jornado.Request;
import jornado.Response;
import jornado.RouteHandler;
import jornado.UserService;
import jornado.WebUser;
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
  private static MustacheCompiler mc = new MustacheCompiler(new File("src/main/webapp"));

  /**
   * Our jornado guice module
   */
  static class Module extends JornadoModule {
    protected Module(final Config config) {
      super(config);
    }

    protected Iterable<RouteHandler<Request>> createRoutes() {
      return Lists.newArrayList(
              new RouteHandler<Request>(new FixedRoute(Method.GET, "/"), HomeHandler.class),
              new RouteHandler<Request>(new RegexRoute(Method.GET, "/person/([A-Za-z0-9]+)", "name"), PersonHandler.class));
    }

    @Override
    protected void configure() {
      super.configure();
      bind(UserService.class).to(TinyUserService.class);

      // TODO: tried to bind using bounded wildcard generics, but no go. seems impossible with guice. blergh.
      //bind(new TypeLiteral<UserService<? extends WebUser>>(){}).to(TinyUserService.class);
      //bind(TypeLiteral.get(Types.newParameterizedType(UserService.class, Types.subtypeOf(WebUser.class)))).to(TinyUserService.class);
    }
  }

  public JorstacheServer(final Config config) {
    module = new Module(config);
  }

  public static void main(String[] args) throws Exception {
    final Config config = createConfig(args);
    JorstacheServer app = new JorstacheServer(config);
    final Injector injector = Guice.createInjector(app.module); // initialize the object tree with Guice
    injector.getInstance(JettyService.class).startAndWait(); // get the jetty service and start it
  }

  private static Config createConfig(String[] args) {
    final Config config = new Config();
    final OptionParser parser = new OptionParser(config);
    parser.parse(args);
    return config;
  }

  static class HomeHandler extends MustacheHandler {
    public HomeHandler() throws MustacheException {
      super(new File("src/main/webapp"), "index.html");
    }
  }

  static class PersonHandler implements Handler<Request> {
    static MustacheHandler mh;
    static {
      try {
        mh = new MustacheHandler(new File("src/main/webapp"), "person.html");
      } catch (MustacheException e) {
        throw new AssertionError("Could not compile: index.html");
      }
    }
    @Override
    public Response handle(Request request) {
      return mh.handle(request);
    }
  }

  static class TinyUserService implements UserService<TinyWebUser> {
    public TinyWebUser load(String id) {
      return new TinyWebUser();
    }
  }

  static class TinyWebUser implements WebUser {
    public String getWebId() {
      return "123";
    }
  }
}
