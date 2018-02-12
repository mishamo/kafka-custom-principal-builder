package de.thmshmm.kafka;

import com.sun.security.auth.UserPrincipal;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.network.TransportLayer;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.Principal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommonNamePrincipalBuilderTest {

    @Mock TransportLayer transportLayer;
    @Rule public ExpectedException expectedException = ExpectedException.none();

    CommonNamePrincipalBuilder builder = new CommonNamePrincipalBuilder();

    @Test
    public void whenPeerPrincipalIsNotX500ThenSkipPrincipalBuilder() throws IOException {
        Principal nonX500Principal = new UserPrincipal("Some name");

        when(transportLayer.peerPrincipal()).thenReturn(nonX500Principal);
        Principal principal = builder.buildPrincipal(transportLayer, null);
        assertThat(principal, is(nonX500Principal));
    }

    @Test
    public void commonNamePrincipalIsBuilt() throws Exception {
        Principal x500Principal = new X500Principal("CN=Banana,L=Hull");
        Principal expectedPrincipal = new CommonNamePrincipal("User", "Banana");

        when(transportLayer.peerPrincipal()).thenReturn(x500Principal);
        Principal principal = builder.buildPrincipal(transportLayer, null);
        assertThat(principal, is(expectedPrincipal));
    }

    @Test
    public void throwsExceptionWhenCommonNameNotPresent() throws Exception {
        expectedException.expect(KafkaException.class);
        expectedException.expectMessage("Failed to build principal");

        Principal principalWithNoCommonName = new X500Principal("L=Hull,OU=Something");

        when(transportLayer.peerPrincipal()).thenReturn(principalWithNoCommonName);
        builder.buildPrincipal(transportLayer, null);
    }
}