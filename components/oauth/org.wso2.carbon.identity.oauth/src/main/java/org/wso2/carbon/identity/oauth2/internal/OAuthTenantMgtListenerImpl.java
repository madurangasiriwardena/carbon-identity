/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2.internal;

import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dao.TokenMgtDAO;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;
import org.wso2.carbon.stratos.common.exception.StratosException;
import org.wso2.carbon.stratos.common.listeners.TenantMgtListener;

public class OAuthTenantMgtListenerImpl implements TenantMgtListener {
    @Override
    public void onTenantCreate(TenantInfoBean tenantInfoBean) throws StratosException {
        return;
    }

    @Override
    public void onTenantUpdate(TenantInfoBean tenantInfoBean) throws StratosException {
        return;
    }

    @Override
    public void onTenantDelete(int i) {
        return;
    }

    @Override
    public void onTenantRename(int i, String s, String s1) throws StratosException {
        return;
    }

    @Override
    public void onTenantInitialActivation(int i) throws StratosException {
        return;
    }

    @Override
    public void onTenantActivation(int i) throws StratosException {
        return;
    }

    @Override
    public void onTenantDeactivation(int i) throws StratosException {
        return;
    }

    @Override
    public void onSubscriptionPlanChange(int i, String s, String s1) throws StratosException {
        return;
    }

    @Override
    public int getListenerOrder() {
        return 0;
    }

    @Override
    public void onPreDelete(int tenantId) throws StratosException {
        TokenMgtDAO tokenMgtDAO = new TokenMgtDAO();
        try {
            tokenMgtDAO.revokeAccessTokensOfTenant(tenantId);
            tokenMgtDAO.revokeAuthorizationCodesOfTenant(tenantId);
        } catch (IdentityOAuth2Exception e) {
            throw new StratosException("Error occurred while revoking the access tokens in tenant " + tenantId, e);
        }
    }
}
