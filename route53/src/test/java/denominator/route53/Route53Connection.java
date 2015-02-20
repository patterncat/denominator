package denominator.route53;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import denominator.DNSApiManager;
import denominator.Denominator;
import feign.Logger;

import static denominator.CredentialsConfiguration.credentials;
import static feign.Util.emptyToNull;
import static java.lang.System.getProperty;

public class Route53Connection {

  final DNSApiManager manager;
  final String mutableZone;

  Route53Connection() {
    String accesskey = emptyToNull(getProperty("route53.accesskey"));
    String secretkey = emptyToNull(getProperty("route53.secretkey"));
    if (accesskey != null && secretkey != null) {
      manager = create(accesskey, secretkey);
    } else {
      manager = null;
    }
    mutableZone = emptyToNull(getProperty("route53.zone"));
  }

  static DNSApiManager create(String accesskey, String secretkey) {
    Route53Provider provider = new Route53Provider(emptyToNull(getProperty("route53.url")));
    return Denominator.create(provider, credentials(accesskey, secretkey), new Overrides());
  }

  @Module(overrides = true, library = true)
  static class Overrides {

    @Provides
    @Singleton
    Logger.Level provideLevel() {
      return Logger.Level.FULL;
    }

    @Provides
    @Singleton
    Logger provideLogger() {
      return new Logger.JavaLogger().appendToFile("build/http-wire.log");
    }
  }
}
