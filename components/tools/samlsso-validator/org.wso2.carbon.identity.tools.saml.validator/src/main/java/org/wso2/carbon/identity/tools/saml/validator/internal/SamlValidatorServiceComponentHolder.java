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

import org.wso2.carbon.identity.saml.metadata.SAMLSSOMetadataConfigService;

public class SamlValidatorServiceComponentHolder {

    private static SamlValidatorServiceComponentHolder instance=new
            SamlValidatorServiceComponentHolder();

    private SAMLSSOMetadataConfigService samlssoMetadataConfigService;

    private SamlValidatorServiceComponentHolder(){
    }

    public static SamlValidatorServiceComponentHolder getInstance(){
        return instance;
    }

    public SAMLSSOMetadataConfigService getSamlssoMetadataConfigService() {
        return samlssoMetadataConfigService;
    }

    public void setSamlssoMetadataConfigService(SAMLSSOMetadataConfigService samlssoMetadataConfigService) {
        this.samlssoMetadataConfigService = samlssoMetadataConfigService;
    }


}
