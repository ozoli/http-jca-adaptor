package net.luminis.httpjca.util;

import net.luminis.httpjca.HttpConnectionTestCase;
import net.luminis.httpjca.HttpConnection;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.util.UUID;

/**
 * A utility class to create the HTTP Resource Adaptor EAR deployment.
 */
public class ArquillianTestUtil {

  /**
   * Default constructor for utility class
   */
  private ArquillianTestUtil() {

  }

  /**
   * Create the EAR deployment containing the Resource Adapter.
   * @return a {@link EnterpriseArchive} containing the HTTP Resource Adpator.
   */
  public static EnterpriseArchive createDeployment() {
    ResourceAdapterArchive raa =
        ShrinkWrap.create(ResourceAdapterArchive.class, "ConnectorTestCase.rar");
    JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
    ja.addPackages(true, Package.getPackage(HttpConnection.class.getPackage().getName()));
    raa.addAsLibrary(ja);

    raa.addAsManifestResource("META-INF/ironjacamar.xml", "ironjacamar.xml");

    JavaArchive libjar = ShrinkWrap.create(JavaArchive.class, "lib.jar")
        .addClasses(HttpConnectionTestCase.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

    return ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
        .addAsModules(raa)
        .addAsLibraries(libjar)
        .addAsLibraries(
            Maven.resolver().resolve("io.undertow:undertow-core:1.1.3.Final")
                .withTransitivity().asFile())
        .addAsLibraries(
            Maven.resolver().resolve("org.apache.commons:commons-lang3:3.3.2")
                .withTransitivity().asFile())
        .addAsLibraries(
            Maven.resolver().resolve("org.apache.httpcomponents:httpclient:4.4")
                .withTransitivity().asFile())
        .addAsLibraries(
            Maven.resolver().resolve("org.apache.httpcomponents:httpcore:4.4")
                .withTransitivity().asFile());

  }
}
