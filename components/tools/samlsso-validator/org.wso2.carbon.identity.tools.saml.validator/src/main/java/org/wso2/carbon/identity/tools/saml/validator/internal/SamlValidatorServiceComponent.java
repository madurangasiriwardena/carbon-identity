/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
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

package org.wso2.carbon.identity.tools.saml.validator.internal;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementServiceImpl;
import org.wso2.carbon.identity.application.mgt.ApplicationMgtSystemConfig;
import org.wso2.carbon.identity.saml.metadata.SAMLSSOMetadataConfigService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @scr.component name="identity.application.management.component" immediate="true"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService"
 * cardinality="1..1" policy="dynamic" bind="setRegistryService"
 * unbind="unsetRegistryService"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService"
 * unbind="unsetRealmService"
 * @scr.reference name="configuration.context.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService"
 * cardinality="1..1" policy="dynamic"
 * bind="setConfigurationContextService"
 * unbind="unsetConfigurationContextService"
 * @scr.reference name="metadata.SAMLSSOMetadataConfigService" interface="org.wso2.carbon.identity.saml.metadata.SAMLSSOMetadataConfigService"
 * cardinality="1..1" policy="dynamic" bind="setSAMLSSOMetadataConfigService"
 * unbind="unsetSAMLSSOMetadataConfigService"
 */
public class SamlValidatorServiceComponent {
    private static Log log = LogFactory.getLog(SamlValidatorServiceComponent.class);

    protected void activate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Identity ApplicationManagementComponent bundle is activated");
        }
    }

    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Identity ApplicationManagementComponent bundle is deactivated");
        }
    }

    protected void setSAMLSSOMetadataConfigService(SAMLSSOMetadataConfigService samlssoMetadataConfigService){
        if (log.isDebugEnabled()) {
            log.debug("SAML SSO Metadata Config Service is set in the SAML SSO bundle");
        }
        SamlValidatorServiceComponentHolder.getInstance().setSamlssoMetadataConfigService(samlssoMetadataConfigService);
    }

    protected void unsetSAMLSSOMetadataConfigService(SAMLSSOMetadataConfigService samlssoMetadataConfigService){
        if (log.isDebugEnabled()) {
            log.debug("SAML SSO Metadata Config Service is unset in the SAML SSO bundle");
        }
        SamlValidatorServiceComponentHolder.getInstance().setSamlssoMetadataConfigService(null);
    }

}
