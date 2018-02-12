package de.thmshmm.kafka;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.network.Authenticator;
import org.apache.kafka.common.network.TransportLayer;
import org.apache.kafka.common.security.auth.PrincipalBuilder;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Thomas Hamm on 21.07.17.
 */
public class CommonNamePrincipalBuilder implements PrincipalBuilder {
    static Logger logger = Logger.getLogger(CommonNamePrincipalBuilder.class.getName());

    public void configure(Map<String, ?> map) { }

    public Principal buildPrincipal(TransportLayer transportLayer, Authenticator authenticator) throws KafkaException {
        try {
            if ((transportLayer.peerPrincipal() instanceof X500Principal)) {
                return buildPrincipal(transportLayer);
            } else {
                logger.info("skipping CommonNamePrincipalBuilder, detected user ANONYMOUS or non SSL principal");
                return transportLayer.peerPrincipal();
            }
        } catch (Exception e) {
            throw new KafkaException("Failed to build principal due to: ", e);
        }
    }

    private Principal buildPrincipal(TransportLayer transportLayer) {
        try {
            String commonNameValue = Arrays.stream(transportLayer.peerPrincipal().getName().split(","))
                    .filter(certificateAttribute -> certificateAttribute.startsWith("CN"))
                    .map(certificateAttribute -> certificateAttribute.split("=")[1])
                    .findFirst().orElseThrow(() -> new IllegalStateException("Could not find CN attribute in principal"));

            return new CommonNamePrincipal("User", commonNameValue);
        } catch (Exception e) {
            throw new KafkaException("failed to build principal");
        }
    }

    public void close() throws KafkaException { }
}
