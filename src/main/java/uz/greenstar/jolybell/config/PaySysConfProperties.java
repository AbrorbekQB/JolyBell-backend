package uz.greenstar.jolybell.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "paysys")
public class PaySysConfProperties {
    private String url;
    private String serviceId;
    private String secretKey;
    private String vendorId;
}
