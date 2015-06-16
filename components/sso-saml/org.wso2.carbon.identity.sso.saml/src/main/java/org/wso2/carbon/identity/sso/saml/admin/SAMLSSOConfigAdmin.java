/*
 * Copyright (c) 2007, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.sso.saml.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml1.core.NameIdentifier;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.persistence.IdentityPersistenceManager;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.saml.metadata.SAMLSSOMetadataConfigService;
import org.wso2.carbon.identity.saml.metadata.model.SAMLSSOServiceProviderDO;
import org.wso2.carbon.identity.sso.saml.dto.SAMLSSOServiceProviderDTO;
import org.wso2.carbon.identity.sso.saml.dto.SAMLSSOServiceProviderInfoDTO;
import org.wso2.carbon.identity.sso.saml.util.SAMLSSOUtil;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.session.UserRegistry;

/**
 * This class is used for managing SAML SSO providers. Adding, retrieving and removing service
 * providers are supported here.
 * In addition to that logic for generating key pairs for tenants except for tenant 0, is included
 * here.
 */
public class SAMLSSOConfigAdmin {

    private static Log log = LogFactory.getLog(SAMLSSOConfigAdmin.class);
    private UserRegistry registry;

    public SAMLSSOConfigAdmin(Registry userRegistry) {
        registry = (UserRegistry) userRegistry;
    }

    /**
     * Add a new SAML SSO service provider
     *
     * @param serviceProviderDTO service Provider DTO
     * @return true if successful, false otherwise
     * @throws IdentityException if fails to load the identity persistence manager
     */
    public boolean addRelyingPartyServiceProvider(SAMLSSOServiceProviderDTO serviceProviderDTO) throws
            IdentityException {

        SAMLSSOServiceProviderDO serviceProviderDO = createSAMLSSOServiceProviderDO(serviceProviderDTO);

        SAMLSSOMetadataConfigService samlssoMetadataConfigService = SAMLSSOUtil.getSamlssoMetadataConfigService();
        try {
            return samlssoMetadataConfigService.addServiceProvider(registry, serviceProviderDO);
        } catch (IdentityException e) {
            throw new IdentityException("Error obtaining a registry for adding a new service provider", e);
        }
    }

    /**
     * update an existing SAML SSO service provider
     *
     * @param serviceProviderDTO
     * @return
     * @throws IdentityException
     */
    public boolean updateRelyingPartyServiceProvider(SAMLSSOServiceProviderDTO serviceProviderDTO) throws
            IdentityException {

        SAMLSSOServiceProviderDO serviceProviderDO = createSAMLSSOServiceProviderDO(serviceProviderDTO);

        SAMLSSOMetadataConfigService samlssoMetadataConfigService = SAMLSSOUtil.getSamlssoMetadataConfigService();
        try {
            return samlssoMetadataConfigService.updateServiceProvider(registry, serviceProviderDO);
        } catch (IdentityException e) {
            throw new IdentityException("Error obtaining a registry for adding a new service provider", e);
        }
    }

    /**
     * upload SAML SSO service provider metadata directly
     *
     * @param metadata
     * @return
     * @throws IdentityException
     */
    public SAMLSSOServiceProviderDTO uploadRelyingPartyServiceProvider(String metadata) throws IdentityException {

        SAMLSSOMetadataConfigService samlssoMetadataConfigService = SAMLSSOUtil.getSamlssoMetadataConfigService();

        try {
            SAMLSSOServiceProviderDO serviceProviderDO = samlssoMetadataConfigService.uploadServiceProvider(registry, metadata);

            return createSAMLSSOServiceProviderDTO(serviceProviderDO);
        } catch (IdentityException e) {
            throw new IdentityException("Error obtaining a registry for adding a new service provider", e);
        }
    }

    private SAMLSSOServiceProviderDTO createSAMLSSOServiceProviderDTO(org.wso2.carbon.identity.saml.metadata.model
                                                                              .SAMLSSOServiceProviderDO serviceProviderDO)
            throws IdentityException {
        SAMLSSOServiceProviderDTO serviceProviderDTO = new SAMLSSOServiceProviderDTO();

        if (serviceProviderDO.getIssuer() == null || serviceProviderDO.getIssuer().equals("")) {
            String message = "A value for the Issuer is mandatory";
            log.error(message);
            throw new IdentityException(message);
        }

        if (serviceProviderDO.getIssuer().contains("@")) {
            String message = "\'@\' is a reserved character. Cannot be used for Service Provider Entity ID";
            log.error(message);
            throw new IdentityException(message);
        }

        serviceProviderDTO.setIssuer(serviceProviderDO.getIssuer());
        serviceProviderDTO.setAssertionConsumerUrl(serviceProviderDO.getAssertionConsumerUrl());
        serviceProviderDTO.setCertAlias(serviceProviderDO.getCertAlias());
        serviceProviderDTO.setUseFullyQualifiedUsername(serviceProviderDO.isUseFullyQualifiedUsername());
        serviceProviderDTO.setDoSingleLogout(serviceProviderDO.isDoSingleLogout());
        serviceProviderDTO.setLoginPageURL(serviceProviderDO.getLoginPageURL());
        serviceProviderDTO.setLogoutURL(serviceProviderDO.getLogoutURL());
        serviceProviderDTO.setDoSignResponse(serviceProviderDO.isDoSignResponse());
        serviceProviderDTO.setDoSignAssertions(serviceProviderDO.isDoSignAssertions());
        serviceProviderDTO.setNameIdClaimUri(serviceProviderDO.getNameIdClaimUri());
        serviceProviderDTO.setEnableAttributesByDefault(serviceProviderDO.isEnableAttributesByDefault());

        if (serviceProviderDO.getNameIDFormat() == null) {
            serviceProviderDO.setNameIDFormat(NameIdentifier.EMAIL);
        } else {
            serviceProviderDO.setNameIDFormat(serviceProviderDO.getNameIDFormat().replace("/",
                    ":"));
        }

        serviceProviderDTO.setNameIDFormat(serviceProviderDO.getNameIDFormat());

        if (serviceProviderDO.getAttributeConsumingServiceIndex() != null && !serviceProviderDO
                .getAttributeConsumingServiceIndex().equals("")) {
            serviceProviderDTO.setAttributeConsumingServiceIndex(serviceProviderDO.getAttributeConsumingServiceIndex());
        }

        if (serviceProviderDO.getRequestedAudiences() != null && serviceProviderDO.getRequestedAudiences().length !=
                0) {
            serviceProviderDTO.setRequestedAudiences(serviceProviderDO.getRequestedAudiences());
        }
        if (serviceProviderDO.getRequestedRecipients() != null && serviceProviderDO.getRequestedRecipients().length
                != 0) {
            serviceProviderDTO.setRequestedRecipients(serviceProviderDO.getRequestedRecipients());
        }
        serviceProviderDTO.setIdPInitSSOEnabled(serviceProviderDO.isIdPInitSSOEnabled());
        serviceProviderDTO.setDoEnableEncryptedAssertion(serviceProviderDO.isDoEnableEncryptedAssertion());
        serviceProviderDTO.setDoValidateSignatureInRequests(serviceProviderDO.isDoValidateSignatureInRequests());

        return serviceProviderDTO;
    }

    private SAMLSSOServiceProviderDO createSAMLSSOServiceProviderDO(SAMLSSOServiceProviderDTO serviceProviderDTO)
            throws IdentityException {

        SAMLSSOServiceProviderDO serviceProviderDO = new SAMLSSOServiceProviderDO();

        if (serviceProviderDTO.getIssuer() == null || "".equals(serviceProviderDTO.getIssuer())) {
            String message = "A value for the Issuer is mandatory";
            log.error(message);
            throw new IdentityException(message);
        }

        if (serviceProviderDTO.getIssuer().contains("@")) {
            String message = "\'@\' is a reserved character. Cannot be used for Service Provider Entity ID";
            log.error(message);
            throw new IdentityException(message);
        }

        serviceProviderDO.setIssuer(serviceProviderDTO.getIssuer());
        serviceProviderDO.setAssertionConsumerUrl(serviceProviderDTO.getAssertionConsumerUrl());
        serviceProviderDO.setCertAlias(serviceProviderDTO.getCertAlias());
        serviceProviderDO.setUseFullyQualifiedUsername(serviceProviderDTO.isUseFullyQualifiedUsername());
        serviceProviderDO.setDoSingleLogout(serviceProviderDTO.isDoSingleLogout());
        serviceProviderDO.setLoginPageURL(serviceProviderDTO.getLoginPageURL());
        serviceProviderDO.setLogoutURL(serviceProviderDTO.getLogoutURL());
        serviceProviderDO.setDoSignResponse(serviceProviderDTO.isDoSignResponse());
        serviceProviderDO.setDoSignAssertions(serviceProviderDTO.isDoSignAssertions());
        serviceProviderDO.setNameIdClaimUri(serviceProviderDTO.getNameIdClaimUri());
        serviceProviderDO.setEnableAttributesByDefault(serviceProviderDTO.isEnableAttributesByDefault());

        if (serviceProviderDTO.getNameIDFormat() == null) {
            serviceProviderDTO.setNameIDFormat(NameIdentifier.EMAIL);
        } else {
            serviceProviderDTO.setNameIDFormat(serviceProviderDTO.getNameIDFormat().replace("/",
                    ":"));
        }

        serviceProviderDO.setNameIDFormat(serviceProviderDTO.getNameIDFormat());

        if (serviceProviderDTO.isEnableAttributeProfile()) {
            String attributeConsumingIndex = serviceProviderDTO.getAttributeConsumingServiceIndex();
            if (StringUtils.isNotEmpty(attributeConsumingIndex)) {
                serviceProviderDO.setAttributeConsumingServiceIndex(attributeConsumingIndex);
            } else {
                serviceProviderDO.setAttributeConsumingServiceIndex(Integer.toString(IdentityUtil.getRandomInteger()));
            }
        } else {
            serviceProviderDO.setAttributeConsumingServiceIndex("");
        }

        if (serviceProviderDTO.getRequestedAudiences() != null && serviceProviderDTO.getRequestedAudiences().length
                != 0) {
            serviceProviderDO.setRequestedAudiences(serviceProviderDTO.getRequestedAudiences());
        }
        if (serviceProviderDTO.getRequestedRecipients() != null && serviceProviderDTO.getRequestedRecipients().length
                != 0) {
            serviceProviderDO.setRequestedRecipients(serviceProviderDTO.getRequestedRecipients());
        }
        serviceProviderDO.setIdPInitSSOEnabled(serviceProviderDTO.isIdPInitSSOEnabled());
        serviceProviderDO.setDoEnableEncryptedAssertion(serviceProviderDTO.isDoEnableEncryptedAssertion());
        serviceProviderDO.setDoValidateSignatureInRequests(serviceProviderDTO.isDoValidateSignatureInRequests());

        return serviceProviderDO;
    }

    /**
     * Retrieve all the relying party service providers
     *
     * @return set of RP Service Providers + file path of pub. key of generated key pair
     */
    public SAMLSSOServiceProviderInfoDTO getServiceProviders() throws IdentityException {
        SAMLSSOServiceProviderDTO[] serviceProviders = null;
        try {
            SAMLSSOMetadataConfigService samlssoMetadataConfigService = SAMLSSOUtil.getSamlssoMetadataConfigService();
            SAMLSSOServiceProviderDO[] providersSet = samlssoMetadataConfigService.getServiceProviders(registry);
            serviceProviders = new SAMLSSOServiceProviderDTO[providersSet.length];

            for (int i = 0; i < providersSet.length; i++) {
                SAMLSSOServiceProviderDO providerDO = providersSet[i];
                SAMLSSOServiceProviderDTO providerDTO = new SAMLSSOServiceProviderDTO();
                providerDTO.setIssuer(providerDO.getIssuer());
                providerDTO.setAssertionConsumerUrl(providerDO.getAssertionConsumerUrl());
                providerDTO.setCertAlias(providerDO.getCertAlias());
                providerDTO.setAttributeConsumingServiceIndex(providerDO.getAttributeConsumingServiceIndex());
                providerDTO.setUseFullyQualifiedUsername(providerDO.isUseFullyQualifiedUsername());
                providerDTO.setDoSignResponse(providerDO.isDoSignResponse());
                providerDTO.setDoSignAssertions(providerDO.isDoSignAssertions());
                providerDTO.setDoSingleLogout(providerDO.isDoSingleLogout());

                if (providerDO.getLoginPageURL() == null || "null".equals(providerDO.getLoginPageURL())) {
                    providerDTO.setLoginPageURL("");
                } else {
                    providerDTO.setLoginPageURL(providerDO.getLoginPageURL());
                }

                if (providerDO.getLogoutURL() == null || "null".equals(providerDO.getLogoutURL())) {
                    providerDTO.setLogoutURL("");
                } else {
                    providerDTO.setLogoutURL(providerDO.getLogoutURL());
                }

                providerDTO.setRequestedClaims(providerDO.getRequestedClaims());
                providerDTO.setRequestedAudiences(providerDO.getRequestedAudiences());
                providerDTO.setRequestedRecipients(providerDO.getRequestedRecipients());
                providerDTO.setEnableAttributesByDefault(providerDO.isEnableAttributesByDefault());
                providerDTO.setNameIdClaimUri(providerDO.getNameIdClaimUri());
                providerDTO.setNameIDFormat(providerDO.getNameIDFormat());

                if (providerDTO.getNameIDFormat() == null) {
                    providerDTO.setNameIDFormat(NameIdentifier.EMAIL);
                }
                providerDTO.setNameIDFormat(providerDTO.getNameIDFormat().replace(":", "/"));

                providerDTO.setIdPInitSSOEnabled(providerDO.isIdPInitSSOEnabled());
                providerDTO.setDoEnableEncryptedAssertion(providerDO.isDoEnableEncryptedAssertion());
                providerDTO.setDoValidateSignatureInRequests(providerDO.isDoValidateSignatureInRequests());
                serviceProviders[i] = providerDTO;
            }
        } catch (IdentityException e) {
            log.error("Error obtaining a registry intance for reading service provider list", e);
            throw new IdentityException("Error obtaining a registry intance for reading service provider list", e);
        }

        SAMLSSOServiceProviderInfoDTO serviceProviderInfoDTO = new SAMLSSOServiceProviderInfoDTO();
        serviceProviderInfoDTO.setServiceProviders(serviceProviders);

        //if it is tenant zero
        if (registry.getTenantId() == 0) {
            serviceProviderInfoDTO.setTenantZero(true);
        }
        return serviceProviderInfoDTO;
    }

    /**
     * Remove an existing service provider.
     *
     * @param issuer issuer name
     * @return true is successful
     * @throws IdentityException
     */
    public boolean removeServiceProvider(String issuer) throws IdentityException {
        try {
            SAMLSSOMetadataConfigService samlssoMetadataConfigService = SAMLSSOUtil.getSamlssoMetadataConfigService();
            return samlssoMetadataConfigService.removeServiceProvider(registry, issuer);
        } catch (IdentityException e) {
            log.error("Error removing a Service Provider");
            throw new IdentityException("Error removing a Service Provider", e);
        }
    }

}
