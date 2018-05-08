package io.spring.up.jhipster.config.audit;

import io.spring.up.jhipster.domain.PersistentAuditEvent;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AuditEventConverter {

    /**
     * Convert a list of PersistentAuditEvent to a list of AuditEvent
     *
     * @param persistentAuditEvents the list to convert
     * @return the converted list.
     */
    public List<AuditEvent> convertToAuditEvent(final Iterable<PersistentAuditEvent> persistentAuditEvents) {
        if (persistentAuditEvents == null) {
            return Collections.emptyList();
        }
        final List<AuditEvent> auditEvents = new ArrayList<>();
        for (final PersistentAuditEvent persistentAuditEvent : persistentAuditEvents) {
            auditEvents.add(this.convertToAuditEvent(persistentAuditEvent));
        }
        return auditEvents;
    }

    /**
     * Convert a PersistentAuditEvent to an AuditEvent
     *
     * @param persistentAuditEvent the event to convert
     * @return the converted list.
     */
    public AuditEvent convertToAuditEvent(final PersistentAuditEvent persistentAuditEvent) {
        if (persistentAuditEvent == null) {
            return null;
        }
        return new AuditEvent(persistentAuditEvent.getAuditEventDate(), persistentAuditEvent.getPrincipal(),
                persistentAuditEvent.getAuditEventType(), this.convertDataToObjects(persistentAuditEvent.getData()));
    }

    /**
     * Internal conversion. This is needed to support the current SpringBoot actuator AuditEventRepository interface
     *
     * @param data the data to convert
     * @return a map of String, Object
     */
    public Map<String, Object> convertDataToObjects(final Map<String, String> data) {
        final Map<String, Object> results = new HashMap<>();

        if (data != null) {
            for (final Map.Entry<String, String> entry : data.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }

    /**
     * Internal conversion. This method will allow to save additional data.
     * By default, it will save the object as string
     *
     * @param data the data to convert
     * @return a map of String, String
     */
    public Map<String, String> convertDataToStrings(final Map<String, Object> data) {
        final Map<String, String> results = new HashMap<>();

        if (data != null) {
            for (final Map.Entry<String, Object> entry : data.entrySet()) {
                // Extract the data that will be saved.
                if (entry.getValue() instanceof WebAuthenticationDetails) {
                    final WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) entry.getValue();
                    results.put("remoteAddress", authenticationDetails.getRemoteAddress());
                    results.put("sessionId", authenticationDetails.getSessionId());
                } else {
                    results.put(entry.getKey(), Objects.toString(entry.getValue()));
                }
            }
        }
        return results;
    }
}
