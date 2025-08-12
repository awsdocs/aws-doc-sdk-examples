/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

class APIFlowManager {
    constructor() {
        this.currentStep = 0;
        this.steps = [];
        this.isEnhanced = true;
    }

    initFlow(flowType, isGuest = false, isDeveloper = false) {
        this.isEnhanced = flowType === 'enhanced';
        this.isGuest = isGuest;
        this.isDeveloper = isDeveloper;
        
        if (isDeveloper) {
            // Developer authentication uses GetOpenIdTokenForDeveloperIdentity as first call
            if (this.isEnhanced) {
                this.steps = ['GetOpenIdTokenForDeveloperIdentity', 'GetCredentialsForIdentity', 'Enhanced flow success'];
            } else {
                this.steps = ['GetOpenIdTokenForDeveloperIdentity', 'AssumeRoleWithWebIdentity', 'Basic flow Success'];
            }
        } else {
            // All other providers use standard flows
            this.steps = this.isEnhanced ? 
                (isGuest ? ['GetId', 'GetCredentialsForIdentity', 'Enhanced flow success'] : ['Provider authentication', 'GetId', 'GetCredentialsForIdentity', 'Enhanced flow success']) : 
                (isGuest ? ['GetId', 'GetOpenIdToken', 'AssumeRoleWithWebIdentity', 'Basic flow success'] : ['Provider authentication', 'GetId', 'GetOpenIdToken', 'AssumeRoleWithWebIdentity', 'Basic flow Success']);
        }
        
        this.currentStep = 0;
        this.resetSteps();
    }

    resetSteps() {
        const containerId = this.isEnhanced ? 'flowDiagram' : 'flowDiagramBasic';
        const container = document.getElementById(containerId);
        if (!container) return;
        
        // Generate dynamic step HTML
        this.generateStepHTML(container);
        
        container.querySelectorAll('.api-step').forEach((el, i) => {
            el.className = 'api-step';
            if (i === 0) el.classList.add('current');
        });
        
        container.querySelectorAll('.step-status').forEach((el, i) => {
            el.textContent = 'waiting';
            el.className = 'step-status';
            if (i === 0) el.classList.add('current');
        });
        
        container.querySelectorAll('.payload-content').forEach((el, i) => {
            if (i === 0) {
                el.innerHTML = '<div class="payload-label">Status:</div>Waiting for authentication...';
            } else {
                el.innerHTML = '<div class="payload-label">Status:</div>Waiting for previous step to complete...';
            }
        });
    }

    generateStepHTML(container) {
        const payloadPrefix = this.isEnhanced ? 'payload' : 'payload-basic';
        
        const stepsHTML = this.steps.map((stepName, index) => {
            const apiCall = stepName.includes('()') ? stepName.replace('()', '') : stepName;
            const isApiCall = stepName.includes('()') || stepName.includes('GetOpenIdTokenForDeveloperIdentity');
            
            return `
                <div class="api-step ${index === 0 ? 'current' : ''}">
                    <div class="step-indicator">${index + 1}</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name">${isApiCall ? `<span class="api-call" onclick="openApiPanel('${apiCall}')">${stepName}</span>` : stepName}</div>
                            <div class="step-status ${index === 0 ? 'current' : ''}">waiting</div>
                        </div>
                        <div class="payload-content" id="${payloadPrefix}-${index}">
                            <div class="payload-label">Status:</div>
                            ${index === 0 ? 'Waiting for authentication...' : 'Waiting for previous step to complete...'}
                        </div>
                    </div>
                </div>
            `;
        }).join('');
        
        container.innerHTML = stepsHTML;
    }

    async executeRealFlow(providerType, token, flowType) {
        try {
            const isGuest = providerType === 'Guest';
            const isDeveloper = providerType === 'Developer';
            
            console.log('executeRealFlow - providerType:', providerType, 'isDeveloper:', isDeveloper, 'flowType:', flowType);
            
            // Skip provider authentication step for guest access and developer authentication
            if (!isGuest && !isDeveloper) {
                await this.executeStep(0, 'Provider Authentication');
            }
            
            const response = await fetch('http://localhost:8006/api/authenticate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    provider_type: providerType, 
                    provider_token: token,
                    flow_type: flowType
                })
            });
            
            const result = await response.json();
            
            if (result.success) {
                if (flowType === 'enhanced') {
                    const isDeveloper = result.provider === 'Developer';
                    if (isGuest) {
                        await this.executeStep(0, 'GetId', result);
                        await this.executeStep(1, 'GetCredentialsForIdentity', result);
                        await this.executeStep(2, 'Enhanced flow success', result);
                    } else if (isDeveloper) {
                        await this.executeStep(0, 'GetOpenIdTokenForDeveloperIdentity', result);
                        await this.executeStep(1, 'GetCredentialsForIdentity', result);
                        await this.executeStep(2, 'Enhanced flow success', result);
                    } else {
                        await this.executeStep(0, 'Provider Authentication', result);
                        await this.executeStep(1, 'GetId', result);
                        await this.executeStep(2, 'GetCredentialsForIdentity', result);
                        await this.executeStep(3, 'Enhanced flow success', result);
                    }
                } else {
                    const isDeveloper = result.provider === 'Developer';
                    if (isGuest) {
                        await this.executeStep(0, 'GetId', result);
                        await this.executeStep(1, 'GetOpenIdToken', result);
                        await this.executeStep(2, 'AssumeRoleWithWebIdentity', result);
                        await this.executeStep(3, 'Basic flow success', result);
                    } else if (isDeveloper) {
                        await this.executeStep(0, 'GetOpenIdTokenForDeveloperIdentity', result);
                        await this.executeStep(1, 'AssumeRoleWithWebIdentity', result);
                        await this.executeStep(2, 'Basic flow success', result);
                    } else {
                        await this.executeStep(0, 'Provider Authentication', result);
                        await this.executeStep(1, 'GetId', result);
                        await this.executeStep(2, 'GetOpenIdToken', result);
                        await this.executeStep(3, 'AssumeRoleWithWebIdentity', result);
                        await this.executeStep(4, 'Basic flow success', result);
                    }
                }
                
                showResult(`${providerType} ${isGuest ? 'access' : 'authentication'} successful!`, result);
            } else {
                this.showError(result.error);
                showResult(`${providerType} ${isGuest ? 'access' : 'authentication'} failed: ${result.error}`, result);
            }
        } catch (error) {
            this.showError(error.message);
            showResult(`Error: ${error.message}`, null);
        }
    }

    async showCompletedFlow(realResult) {
        const flowType = (realResult.flow_type === 'basic_authenticated' || realResult.flow_type === 'basic_guest') ? 'basic' : 'enhanced';
        const isGuest = realResult.provider === 'Guest';
        const isDeveloper = realResult.provider === 'Developer';
        
        this.initFlow(flowType, isGuest, isDeveloper);
        
        if (flowType === 'enhanced') {
            if (isGuest) {
                await this.executeStep(0, 'GetId', realResult);
                await this.executeStep(1, 'GetCredentialsForIdentity', realResult);
                await this.executeStep(2, 'Enhanced flow success', realResult);
            } else if (isDeveloper) {
                await this.executeStep(0, 'GetOpenIdTokenForDeveloperIdentity', realResult);
                await this.executeStep(1, 'GetCredentialsForIdentity', realResult);
                await this.executeStep(2, 'Enhanced flow success', realResult);
            } else {
                await this.executeStep(0, 'Provider Authentication', realResult);
                await this.executeStep(1, 'GetId', realResult);
                await this.executeStep(2, 'GetCredentialsForIdentity', realResult);
                await this.executeStep(3, 'Enhanced flow success', realResult);
            }
        } else {
            if (isGuest) {
                await this.executeStep(0, 'GetId', realResult);
                await this.executeStep(1, 'GetOpenIdToken', realResult);
                await this.executeStep(2, 'AssumeRoleWithWebIdentity', realResult);
                await this.executeStep(3, 'Basic flow Success', realResult);
            } else if (isDeveloper) {
                await this.executeStep(0, 'GetOpenIdTokenForDeveloperIdentity', realResult);
                await this.executeStep(1, 'AssumeRoleWithWebIdentity', realResult);
                await this.executeStep(2, 'Basic flow Success', realResult);
            } else {
                await this.executeStep(0, 'Provider Authentication', realResult);
                await this.executeStep(1, 'GetId', realResult);
                await this.executeStep(2, 'GetOpenIdToken', realResult);
                await this.executeStep(3, 'AssumeRoleWithWebIdentity', realResult);
                await this.executeStep(4, 'Basic flow Success', realResult);
            }
        }
        
        await this.addFlowCompletionActions(flowType);
    }

    async executeStep(stepIndex, stepName, realData = null) {
        const containerId = this.isEnhanced ? 'flowDiagram' : 'flowDiagramBasic';
        const container = document.getElementById(containerId);
        if (!container) return;
        
        const steps = container.querySelectorAll('.api-step');
        const indicators = container.querySelectorAll('.step-indicator');
        const statuses = container.querySelectorAll('.step-status');
        const step = steps[stepIndex];
        const indicator = indicators[stepIndex];
        const status = statuses[stepIndex];
        
        if (!step || !indicator || !status) return;

        steps.forEach(el => el.classList.remove('current', 'in-progress', 'complete'));
        indicators.forEach(el => el.classList.remove('current', 'in-progress', 'complete'));
        
        step.classList.add('in-progress');
        indicator.classList.add('in-progress');
        status.textContent = 'in progress';
        status.className = 'step-status in-progress';
        
        let delay;
        switch (stepName) {
            case 'Provider Authentication': delay = 2000; break;
            case 'GetId': delay = 1200; break;
            case 'GetCredentialsForIdentity': delay = 1800; break;
            case 'GetOpenIdToken': delay = 1000; break;
            case 'GetOpenIdTokenForDeveloperIdentity': delay = 1500; break;
            case 'AssumeRoleWithWebIdentity': delay = 1500; break;
            default: delay = 800;
        }
        
        await new Promise(resolve => setTimeout(resolve, delay));
        
        step.classList.remove('in-progress');
        step.classList.add('complete');
        indicator.classList.remove('in-progress');
        indicator.classList.add('complete');
        status.textContent = 'complete';
        status.className = 'step-status complete';
        
        this.showRealPayload(stepIndex, realData);
        
        if (stepIndex + 1 < this.steps.length) {
            steps[stepIndex + 1].classList.add('current');
            indicators[stepIndex + 1].classList.add('current');
            statuses[stepIndex + 1].className = 'step-status current';
        }
    }
    
    async addFlowCompletionActions(flowType) {
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        const containerId = flowType === 'enhanced' ? 'flowDiagram' : 'flowDiagramBasic';
        const container = document.getElementById(containerId);
        if (!container) return;
        
        const isGuest = window.currentAuthResult?.provider === 'Guest';
        const replayFunction = isGuest ? 'replayGuestAPIFlowVisualization()' : 'replayAPIFlowVisualization()';
        
        const actionsHTML = `
            <div class="flow-completion-actions">
                <div class="completion-header">
                    <h4 class="completion-title"> API flow visualization complete!</h4>
                    <p class="completion-message">You've successfully seen how your ${flowType === 'enhanced' ? 'enhanced' : 'basic'} authentication flow works behind the scenes.</p>
                </div>
                
                <div class="completion-buttons">
                    <button class="btn-auth-secondary" onclick="${replayFunction}">
                        Replay visualization
                    </button>
                    <button class="btn-auth-secondary" onclick="resetToProviderSelection()">
                        Test another provider
                    </button>
                </div>
                
                <div class="completion-note">
                    <p><strong>What's next:</strong> You can replay this visualization to better understand the process, or try authenticating with a different provider to see how the flow varies.</p>
                </div>
            </div>
        `;
        
        container.insertAdjacentHTML('afterend', actionsHTML);
        
        setTimeout(() => {
            const actionsElement = document.querySelector('.flow-completion-actions');
            if (actionsElement) {
                actionsElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        }, 300);
    }

    showRealPayload(stepIndex, realData) {
        const payloadId = this.isEnhanced ? `payload-${stepIndex}` : `payload-basic-${stepIndex}`;
        const payloadEl = document.getElementById(payloadId);
        if (!payloadEl) return;

        if (stepIndex === this.steps.length - 1) {
            payloadEl.innerHTML = `
                <div class="payload-label">Status:</div>
                Authentication complete!
            `;
        } else if (realData) {
            const stepName = this.steps[stepIndex];
            let requestData, responseData;
            
            switch (stepName) {
                case 'Provider authentication':
                    console.log('Provider authentication step - realData:', realData);
                    console.log('Available keys:', Object.keys(realData || {}));
                    
                    // Skip this step for guest access and developer authentication
                    if (realData.provider === 'Guest' || realData.provider === 'Developer') {
                        requestData = realData.provider === 'Guest' ? 'No provider authentication required for guest access' : 'No provider authentication required for developer authentication';
                        responseData = { message: realData.provider === 'Guest' ? 'Guest access - no external provider authentication needed' : 'Developer authentication - uses IAM-authenticated API calls' };
                        break;
                    }
                    
                    const providerName = realData.provider || 'Google';
                    
                    // Handle SAML authentication differently
                    if (providerName === 'SAML') {
                        requestData = `User authenticated with SAML provider<br>SAML Response received via HTTP POST<br>to: ${realData.callback_url || 'callback-url'}`;
                        responseData = {
                            saml_response: realData.provider_token ? realData.provider_token.substring(0, 50) + '...' : 'SAML-Response...',
                            provider_arn: realData.provider_key || 'SAML-Provider-ARN',
                            status: 'Authentication successful'
                        };
                        break;
                    }
                    
                    // Use actual OAuth response if available
                    if (realData.oauth_response) {
                        console.log('Using actual OAuth response:', realData.oauth_response);
                        const endpoint = realData.token_endpoint || 'https://oauth2.googleapis.com/token';
                        requestData = `POST ${endpoint}<br>Content-Type: application/x-www-form-urlencoded<br><br>grant_type=authorization_code&<br>client_id=${realData.client_id || 'your-client-id'}&<br>client_secret=***&<br>code=${realData.auth_code || 'auth-code-from-callback'}&<br>redirect_uri=http://localhost:8006/auth/${(realData.provider_name || providerName).toLowerCase()}/callback`;
                        
                        // Use actual OAuth response, truncating sensitive data
                        responseData = {};
                        for (const [key, value] of Object.entries(realData.oauth_response)) {
                            if (typeof value === 'string' && value.length > 50) {
                                responseData[key] = value.substring(0, 30) + '...';
                            } else {
                                responseData[key] = value;
                            }
                        }
                    } else {
                        console.log('Using fallback template data for:', providerName);
                        const providerUrls = {
                            'Google': 'https://oauth2.googleapis.com/token',
                            'Facebook': 'https://graph.facebook.com/v18.0/oauth/access_token',
                            'Amazon': 'https://api.amazon.com/auth/o2/token',
                            'OIDC': realData.oidc_endpoint || 'OIDC-Token-Endpoint',
                            'UserPool': realData.token_endpoint || 'https://your-domain.auth.region.amazoncognito.com/oauth2/token'
                        };
                        requestData = `POST ${providerUrls[providerName] || providerUrls.Google}<br>Content-Type: application/x-www-form-urlencoded<br><br>grant_type=authorization_code&<br>client_id=${realData.client_id || 'your-client-id'}&<br>client_secret=***&<br>code=${realData.auth_code || 'auth-code-from-callback'}&<br>redirect_uri=http://localhost:8006/auth/${providerName === 'UserPool' ? 'userpool' : providerName.toLowerCase()}/callback`;
                        
                        if (providerName === 'Facebook') {
                            responseData = {
                                access_token: 'EAABwzLixnjYBO...',
                                token_type: 'bearer',
                                expires_in: 5183999
                            };
                        } else if (providerName === 'Amazon') {
                            responseData = {
                                access_token: 'Atza|IwEBIA...',
                                token_type: 'bearer',
                                expires_in: 3600,
                                refresh_token: 'Atzr|IwEBIL...'
                            };
                        } else {
                            responseData = {
                                access_token: 'ya29.a0AfH6SMC...',
                                id_token: 'eyJhbGciOiJSUzI1NiIs...',
                                token_type: 'Bearer',
                                expires_in: 3600,
                                scope: 'openid email profile'
                            };
                        }
                    }
                    break;
                case 'GetId':
                    if (realData.provider === 'Guest') {
                        requestData = `{<br>&nbsp;&nbsp;\"IdentityPoolId\": \"${realData.identity_pool_id || 'us-east-2:your-identity-pool-id'}\"<br>}`;
                        responseData = {
                            IdentityId: realData.identity_id
                        };
                        break;
                    }
                    
                    let getIdProviderKey = realData.provider_key;
                    if (!getIdProviderKey) {
                        if (realData.provider === 'Amazon') getIdProviderKey = 'www.amazon.com';
                        else if (realData.provider === 'Facebook') getIdProviderKey = 'graph.facebook.com';
                        else if (realData.provider === 'SAML') getIdProviderKey = realData.provider_key || 'SAML-Provider-ARN';
                        else if (realData.provider === 'OIDC') getIdProviderKey = realData.provider_key || 'OIDC-Provider';
                        else getIdProviderKey = 'accounts.google.com';
                    }
                    requestData = `{<br>&nbsp;&nbsp;"AccountId": "${realData.account_id || 'YOUR_ACCOUNT_ID'}",<br>&nbsp;&nbsp;"IdentityPoolId": "${realData.identity_pool_id || 'us-east-2:your-identity-pool-id'}",<br>&nbsp;&nbsp;"Logins": {<br>&nbsp;&nbsp;&nbsp;&nbsp;"${getIdProviderKey}": "${realData.provider_token ? realData.provider_token.substring(0, 20) + '...' : 'token...'}",<br>&nbsp;&nbsp;}<br>}`;
                    responseData = { IdentityId: realData.identity_id };
                    break;
                case 'GetCredentialsForIdentity':
                    if (realData.provider === 'Guest') {
                        requestData = `{<br>&nbsp;&nbsp;"IdentityId": "${realData.identity_id}"<br>}`;
                        responseData = {
                            IdentityId: realData.identity_id,
                            Credentials: {
                                AccessKeyId: realData.credentials?.AccessKeyId || 'ASIA...',
                                SecretKey: realData.credentials?.SecretKey || realData.credentials?.SecretAccessKey || 'SecretKey...',
                                SessionToken: realData.credentials?.SessionToken ? realData.credentials.SessionToken.substring(0, 50) + '...' : 'IQoJb3JpZ2luX2VjE...',
                                Expiration: realData.credentials?.Expiration || new Date().toISOString()
                            }
                        };
                        break;
                    }
                    
                    let getCredsProviderKey = realData.provider_key;
                    if (!getCredsProviderKey) {
                        if (realData.provider === 'Amazon') getCredsProviderKey = 'www.amazon.com';
                        else if (realData.provider === 'Facebook') getCredsProviderKey = 'graph.facebook.com';
                        else if (realData.provider === 'SAML') getCredsProviderKey = realData.provider_key || 'SAML-Provider-ARN';
                        else if (realData.provider === 'OIDC') getCredsProviderKey = realData.provider_key || 'OIDC-Provider';
                        else getCredsProviderKey = 'accounts.google.com';
                    }
                    requestData = `{<br>&nbsp;&nbsp;"IdentityId": "${realData.identity_id}",<br>&nbsp;&nbsp;"Logins": {<br>&nbsp;&nbsp;&nbsp;&nbsp;"${getCredsProviderKey}": "${realData.provider_token ? realData.provider_token.substring(0, 20) + '...' : 'token...'}",<br>&nbsp;&nbsp;}<br>}`;
                    responseData = {
                        IdentityId: realData.identity_id,
                        Credentials: {
                            AccessKeyId: realData.credentials.AccessKeyId,
                            SecretAccessKey: realData.credentials.SecretAccessKey || realData.credentials.SecretKey,
                            SessionToken: realData.credentials.SessionToken ? realData.credentials.SessionToken.substring(0, 50) + '...' : 'token...'
                        }
                    };
                    break;
                case 'GetOpenIdToken':
                    // Handle guest access differently
                    if (realData.provider === 'Guest') {
                        requestData = `{<br>&nbsp;&nbsp;"IdentityId": "${realData.identity_id}"<br>}`;
                        responseData = {
                            IdentityId: realData.identity_id,
                            Token: realData.open_id_token || 'eyJraWQiOiJhcGktZ3c...'
                        };
                        break;
                    }
                    
                    requestData = `{<br>&nbsp;&nbsp;"IdentityId": "${realData.identity_id}",<br>&nbsp;&nbsp;"Logins": {<br>&nbsp;&nbsp;&nbsp;&nbsp;"${realData.provider_key || 'accounts.google.com'}": "${realData.provider_token ? realData.provider_token.substring(0, 20) + '...' : 'token...'}",<br>&nbsp;&nbsp;}<br>}`;
                    responseData = {
                        IdentityId: realData.identity_id,
                        Token: realData.open_id_token || 'eyJraWQiOiJhcGktZ3c...'
                    };
                    break;
                case 'GetOpenIdTokenForDeveloperIdentity':
                    requestData = `{<br>&nbsp;&nbsp;"IdentityPoolId": "${realData.identity_pool_id || 'us-east-2:your-identity-pool-id'}",<br>&nbsp;&nbsp;"Logins": {<br>&nbsp;&nbsp;&nbsp;&nbsp;"${realData.provider_key || 'ExampleProvider01'}": "${realData.provider_token || 'developer-user-id'}",<br>&nbsp;&nbsp;},<br>&nbsp;&nbsp;"TokenDuration": 3600<br>}`;
                    responseData = {
                        IdentityId: realData.identity_id || 'us-east-2:identity-id',
                        Token: realData.open_id_token ? realData.open_id_token.substring(0, 30) + '...' : 'eyJhbGciOiJSUzI1NiIs...'
                    };
                    break;
                case 'AssumeRoleWithWebIdentity':
                    // Extract role ARN from actual credentials or use a default
                    const roleArn = realData.role_arn || (realData.provider === 'Guest' ? 
                        'arn:aws:iam::ACCOUNT_ID:role/Cognito_IdentityPoolUnauth_Role' : 
                        'arn:aws:iam::ACCOUNT_ID:role/Cognito_IdentityPoolAuth_Role');
                    requestData = `{<br>&nbsp;&nbsp;"RoleArn": "${roleArn}",<br>&nbsp;&nbsp;"WebIdentityToken": "${realData.open_id_token ? realData.open_id_token.substring(0, 20) + '...' : 'eyJhbGciOiJSUzI1NiIs...'}"<br>}`;
                    responseData = { 
                        Credentials: {
                            AccessKeyId: realData.credentials?.AccessKeyId || 'ASIA...',
                            SecretAccessKey: realData.credentials?.SecretAccessKey || realData.credentials?.SecretKey || 'SecretKey...',
                            SessionToken: realData.credentials?.SessionToken ? realData.credentials.SessionToken.substring(0, 50) + '...' : 'IQoJb3JpZ2luX2VjE...'
                        }
                    };
                    break;
                default:
                    requestData = 'API Request';
                    responseData = { status: 'success' };
            }
            
            payloadEl.innerHTML = `
                <div class="payload-label">Request:</div>
                ${requestData}<br><br>
                <div class="payload-label">Response:</div>
                ${JSON.stringify(responseData, null, 2).replace(/\n/g, '<br>&nbsp;&nbsp;')}
            `;
        }
    }

    showError(errorMessage) {
        const containerId = this.isEnhanced ? 'flowDiagram' : 'flowDiagramBasic';
        const container = document.getElementById(containerId);
        if (!container) return;
        
        const currentStep = container.querySelector('.api-step.in-progress') || container.querySelector('.api-step.current');
        if (currentStep) {
            const status = currentStep.querySelector('.step-status');
            const payload = currentStep.querySelector('.payload-content');
            
            status.textContent = 'error';
            status.className = 'step-status error';
            payload.innerHTML = `
                <div class="payload-label">Error:</div>
                ${errorMessage}
            `;
        }
    }
}

// Initialize flow manager globally
window.flowManager = new APIFlowManager();

// Add API data for GetOpenIdTokenForDeveloperIdentity
if (typeof apiData !== 'undefined') {
    apiData['GetOpenIdTokenForDeveloperIdentity'] = {
        service: 'Amazon Cognito identity pool',
        description: 'Returns an OpenID Connect token and identity ID for a developer authenticated identity. This is the entry point for developer authentication flows.',
        purpose: 'IAM-authenticated API call (requires AWS credentials) that exchanges developer user identifiers for Cognito identity tokens. This is the secure starting point for developer authentication.',
        docUrl: 'https://docs.aws.amazon.com/cognitoidentity/latest/APIReference/API_GetOpenIdTokenForDeveloperIdentity.html'
    };
}

// Functions needed by other modules
function showDetailedAPIFlow() {
    if (!window.currentAuthResult) {
        alert("No authentication data found. Please authenticate first.");
        return;
    }
    
    const result = window.currentAuthResult;
    const flowType = result.flow_type === 'basic_authenticated' ? 'basic' : 'enhanced';
    const container = document.querySelector('.tab-content.active');
    
    if (!container) {
        alert("Container not found!");
        return;
    }
    
    showAPIFlowExplanation(result, flowType, container);
}

function showAPIFlowExplanation(result, flowType, container) {
    const provider = result.provider || 'Unknown';
    const isBasicFlow = flowType === 'basic';
    const isGuest = provider === 'Guest';
    
    const explanationHTML = `
        <div class="api-flow-explanation-container">
            <div class="explanation-header">
                <h3 class="explanation-title">API flow visualization</h3>
                <p class="explanation-subtitle">Understanding your ${isGuest ? 'guest access' : provider + ' authentication'} process</p>
            </div>
            
            <div class="explanation-content">
                <div class="flow-info-box">
                    <h4 class="info-title">What you'll see:</h4>
                    <ul class="explanation-list">
                        <li><strong>Step-by-step API calls</strong> that happened during your ${isGuest ? 'guest access' : 'authentication'}</li>
                        <li><strong>Real request/response data</strong> from AWS Cognito services</li>
                    </ul>
                </div>
                
                <div class="flow-details-box">
                    <h4 class="info-title">Your ${isGuest ? 'guest access' : 'authentication'} flow:</h4>
                    <div class="flow-type-badge ${isBasicFlow ? 'basic' : 'enhanced'}">
                        ${isBasicFlow ? 
                            (isGuest ? 'Basic guest flow (3 steps)' : 'Basic flow (4 steps)') : 
                            (isGuest ? 'Enhanced guest flow (2 steps)' : 'Enhanced flow (3 steps)')
                        }
                    </div>
                    <p class="flow-description">
                        ${isGuest ? 
                            (isBasicFlow 
                                ? 'Guest access using basic flow: GetId() → GetOpenIdToken() → AssumeRoleWithWebIdentity(). No external provider authentication required.'
                                : 'Guest access using enhanced flow: GetId() → GetCredentialsForIdentity(). No external provider authentication required.'
                            ) :
                            (isBasicFlow 
                                ? 'The basic flow uses a 4-step process: Provider authentication → GetId() → GetOpenIdToken() → AssumeRoleWithWebIdentity(). This is the traditional method that provides more granular control.'
                                : 'The enhanced flow uses a streamlined 3-step process: Provider Authentication → GetId() → GetCredentialsForIdentity(). This is the recommended modern approach for better performance.'
                            )
                        }
                    </p>
                </div>
                
                <div class="learning-objectives-box">
                    <h4 class="info-title">What you'll learn:</h4>
                    <ul class="explanation-list">
                        ${isGuest ? 
                            '<li>Understand how unauthenticated identities work in Cognito</li><li>See how guest users get temporary AWS credentials</li>' :
                            '<li>Understand how OAuth tokens are exchanged for AWS credentials</li><li>See the difference between enhanced and basic authentication flows</li>'
                        }
                        <li>Learn about AWS Cognito identity pool API calls</li>
                    </ul>
                </div>
                
                <div class="visualization-note">
                    <p><strong>Note:</strong> The visualization will replay your actual authentication process with the real data that was used to get your AWS credentials.</p>
                </div>
            </div>
            
            <div class="explanation-actions">
                <button class="btn-auth-secondary" onclick="startAPIFlowVisualization()">
                    Start API flow visualization
                </button>
                <button class="btn-auth-secondary" onclick="showJustCredentials()">
                    Just show credentials instead
                </button>
                <button class="btn-auth-secondary" onclick="resetToProviderSelection()">
                    Try another provider
                </button>
            </div>
        </div>
    `;
    container.innerHTML = explanationHTML;
    
    setTimeout(() => {
        container.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
}

function startAPIFlowVisualization() {
    if (!window.currentAuthResult) {
        alert("Authentication data lost. Please authenticate again.");
        return;
    }
    
    const result = window.currentAuthResult;
    const flowType = (result.flow_type === 'basic_authenticated' || result.flow_type === 'basic_guest') ? 'basic' : 'enhanced';
    const isGuest = result.provider === 'Guest';
    const container = document.querySelector('.tab-content.active');
    
    if (!container) return;
    
    container.innerHTML = `
        <div class="loading-visualization">
            <h3 class="loading-title">Preparing API flow visualization...</h3>
            <p>Setting up the step-by-step demonstration of your ${isGuest ? 'guest access' : 'authentication'} process.</p>
            <div class="loading-spinner"></div>
        </div>
    `;
    
    setTimeout(() => {
        if (isGuest) {
            const visualizerHTML = flowType === 'enhanced' ? getGuestEnhancedVisualizer() : getGuestBasicVisualizer();
            container.innerHTML = visualizerHTML;
        } else {
            container.innerHTML = getProviderCategories(flowType);
        }
        
        const visualizerId = flowType === 'enhanced' ? 'apiVisualizer' : 'apiVisualizerBasic';
        const visualizer = document.getElementById(visualizerId);
        
        if (visualizer) {
            visualizer.style.display = 'block';
            const isDeveloper = result.provider === 'Developer';
            window.flowManager.initFlow(flowType, isGuest, isDeveloper);
            
            setTimeout(() => {
                visualizer.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 300);
            
            setTimeout(() => {
                window.flowManager.showCompletedFlow(result);
            }, 800);
        }
    }, 1500);
}

function replayAPIFlowVisualization() {
    if (!window.currentAuthResult) {
        alert("No authentication data found. Please authenticate first.");
        return;
    }
    
    const existingActions = document.querySelector('.flow-completion-actions');
    if (existingActions) {
        existingActions.remove();
    }
    
    const container = document.querySelector('.tab-content.active');
    if (container) {
        const loadingHTML = `
            <div class="loading-visualization">
                <h3 class="loading-title">Replaying API flow visualization...</h3>
                <p>Restarting the step-by-step demonstration of your authentication process.</p>
                <div class="loading-spinner"></div>
            </div>
        `;
        container.innerHTML = loadingHTML;
    }
    
    setTimeout(() => {
        startAPIFlowVisualization();
    }, 1500);
}

function replayGuestAPIFlowVisualization() {
    if (!window.currentAuthResult) {
        alert("No authentication data found. Please authenticate first.");
        return;
    }
    
    const existingActions = document.querySelector('.flow-completion-actions');
    if (existingActions) {
        existingActions.remove();
    }
    
    const result = window.currentAuthResult;
    const flowType = (result.flow_type === 'basic_authenticated' || result.flow_type === 'basic_guest') ? 'basic' : 'enhanced';
    const container = document.querySelector('.tab-content.active');
    
    if (container) {
        container.innerHTML = `
            <div class="loading-visualization">
                <h3 class="loading-title">Replaying API flow visualization...</h3>
                <p>Restarting the step-by-step demonstration of your guest access process.</p>
                <div class="loading-spinner"></div>
            </div>
        `;
    }
    
    setTimeout(() => {
        const visualizerHTML = flowType === 'enhanced' ? getGuestEnhancedVisualizer() : getGuestBasicVisualizer();
        container.innerHTML = visualizerHTML;
        
        const visualizerId = flowType === 'enhanced' ? 'apiVisualizer' : 'apiVisualizerBasic';
        const visualizer = document.getElementById(visualizerId);
        
        if (visualizer) {
            visualizer.style.display = 'block';
            const isDeveloper = result.provider === 'Developer';
            window.flowManager.initFlow(flowType, true, isDeveloper);
            
            setTimeout(() => {
                visualizer.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 300);
            
            setTimeout(() => {
                window.flowManager.showCompletedFlow(result);
            }, 800);
        }
    }, 1500);
}