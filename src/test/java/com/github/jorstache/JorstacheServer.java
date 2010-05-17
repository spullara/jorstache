package com.github.jorstache;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sampullara.mustache.MustacheException;
import jornado.Config;
import jornado.ErrorResponse;
import jornado.FixedRoute;
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
  public static Injector injector;

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
              new RouteHandler<Request>(new FixedRoute(Method.GET, "/fred"), FredHandler.class),
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
      super(new File("src/test/webapp"), "index.html");
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
      super(new File("src/test/webapp"), "person.html");
    }

    @Override
    public Response handle(final Request request) {
      return super.handle(new Object() {
        String name = request.getPathParameter("name");
      });
    }
  }

  @Singleton
  static class FredHandler extends JorstacheHandler {
    public FredHandler() throws MustacheException {
      super(new File("src/test/webapp"), "fred.html", "com/sampullara/fred/Fred.java", "com.sampullara.fred.Fred");
    }
  }

  static class JorstacheHandler extends MustacheHandler {
    public JorstacheHandler(File root, String templatePath, String codePath, String classname) throws MustacheException {
      super(root, templatePath, codePath, classname);
    }

    @Override
    public Response handle(Request request) {
      try {
        Object o = getCode().getConstructor(Request.class, Injector.class).newInstance(request, injector);
        return super.handle(o);
      } catch (Exception e) {
        e.printStackTrace();
        return new ErrorResponse(e.getMessage());
      }
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
