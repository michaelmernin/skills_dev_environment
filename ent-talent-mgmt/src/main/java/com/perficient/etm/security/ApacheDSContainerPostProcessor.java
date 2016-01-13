package com.perficient.etm.security;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.naming.NamingException;

import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.entry.DefaultServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.schema.SchemaEntityFactory;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.schema.registries.Registries;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.entry.client.DefaultClientAttribute;
import org.apache.directory.shared.ldap.ldif.ChangeType;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

@Component
public class ApacheDSContainerPostProcessor implements ObjectPostProcessor<ApacheDSContainer> {

    private static final String EMPLOYEE_ID_OID = "1.2.840.113556.1.4.35";

    private static final String EMPLOYEE_ID = "employeeID";

    private static final String USERS_FILE = "classpath:auth/users-dev.ldif";

    private final Logger log = LoggerFactory.getLogger(ApacheDSContainerPostProcessor.class);

    @Inject
    private ApplicationContext context;

    @Override
    public <O extends ApacheDSContainer> O postProcess(O object) {
        ApacheDSContainer ldapContainer = object;
        DefaultDirectoryService service = ldapContainer.getService();
        CoreSession adminSession = service.getAdminSession();
        try {
            createAttributeTypes(adminSession);
            addOptionalAttributes(adminSession);
        } catch (Exception e) {
            log.error("Error adding custom attributes to organizationalPerson", e);
        }
        loadLdif(adminSession, USERS_FILE);
        return object;
    }

    private void createAttributeTypes(CoreSession adminSession) throws Exception {
        ServerEntry attributeEntry = attributeEntry(adminSession, EMPLOYEE_ID_OID, EMPLOYEE_ID);
        adminSession.add(attributeEntry);
        registerAttribute(adminSession, attributeEntry);
    }

    private void addOptionalAttributes(CoreSession adminSession) throws Exception {
        LdifEntry objectClassMod = objectClassMod(adminSession, EMPLOYEE_ID);
        adminSession.modify(objectClassMod.getDn(), objectClassMod.getModificationItems());
    }

    private void registerAttribute(CoreSession adminSession, ServerEntry attributeEntry) throws Exception {
        Registries registries = adminSession.getDirectoryService().getRegistries();
        registries.getAttributeTypeRegistry().register(createAttributeType(registries, attributeEntry));
    }

    private AttributeType createAttributeType(Registries registries, ServerEntry attributeEntry) throws NamingException {
        SchemaEntityFactory factory = new SchemaEntityFactory(registries);
        return factory.getAttributeType(attributeEntry, registries, "core");
    }

    private LdifEntry objectClassMod(CoreSession adminSession, String... attributeNames) throws Exception {
        LdifEntry entry = new LdifEntry();
        entry.setDn("m-oid=2.5.6.7, ou=objectclasses, cn=core, ou=schema");
        entry.setChangeType(ChangeType.Modify);
        for (String attributeName : attributeNames) {
            entry.addModificationItem(ModificationOperation.ADD_ATTRIBUTE, new DefaultClientAttribute("m-may", attributeName));
        }
        return entry;
    }

    private ServerEntry attributeEntry(CoreSession adminSession, String oid, String name) throws Exception {
        LdifEntry entry = new LdifEntry();
        entry.setDn("m-oid=" + oid + ", ou=attributetypes, cn=core, ou=schema");
        entry.addAttribute("objectclass", "metaAttributeType");
        entry.addAttribute("objectclass", "metaTop");
        entry.addAttribute("objectclass", "top");
        entry.addAttribute("m-oid", oid);
        entry.addAttribute("m-name", name);
        entry.addAttribute("m-syntax", "1.3.6.1.4.1.1466.115.121.1.15");
        return new DefaultServerEntry(adminSession.getDirectoryService().getRegistries(), entry.getEntry());
    }

    private void loadLdif(CoreSession adminSession, String file) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Resource ldif = context.getResource(file);

            File ldifFile;
            LdifFileLoader loader;
            try {
                ldifFile = ldif.getFile();
            } catch (IOException e) {
                ldifFile = File.createTempFile(ldif.getFilename(), null);
                Files.copy(() -> {return ldif.getInputStream();}, ldifFile);
            }

            loader = new LdifFileLoader(adminSession, ldifFile, null, classLoader);
            loader.execute();
        } catch (IOException e) {
            log.warn("Failed to load " + file);
        }
    }
}