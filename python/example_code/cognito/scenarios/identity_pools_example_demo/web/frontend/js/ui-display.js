// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Global variable to store current result - make it globally accessible
window.currentAuthResult = null;

function showResult(message, result) {
    console.log('showResult called with message:', message);
    console.log('showResult called with result:', result);
    console.log('result.credentials:', result?.credentials);
    console.log('result.flow_type:', result?.flow_type);
    
    // Handle null result (error case)
    if (!result) {
        const container = document.querySelector('.tab-content.active') || document.getElementById('basic-tab') || document.getElementById('enhanced-tab');
        if (container) {
            container.innerHTML = `
                <div class="error-container">
                    <h3 class="error-title">Error</h3>
                    <p class="error-message">${message}</p>
                    <button class="btn-auth-secondary" onclick="location.reload()">
                        Try Again
                    </button>
                </div>
            `;
        }
        return;
    }
    
    // Store result globally
    window.currentAuthResult = result;
    
    // Force switch to appropriate tab if this is a basic flow result
    if (result && (result.flow_type === 'basic_authenticated' || result.flow_type === 'basic_guest')) {
        const isGuest = result.provider === 'Guest';
        
        document.querySelectorAll('.tab-content').forEach(tab => {
            if (tab && tab.classList) tab.classList.remove('active');
        });
        document.querySelectorAll('.tab-button').forEach(btn => {
            if (btn && btn.classList) btn.classList.remove('active');
        });
        
        if (isGuest) {
            // For guest access, activate guest-basic-tab
            const guestBasicTab = document.getElementById('guest-basic-tab');
            if (guestBasicTab && guestBasicTab.classList) guestBasicTab.classList.add('active');
        } else {
            // For authenticated access, activate basic-tab
            const basicTab = document.getElementById('basic-tab');
            if (basicTab && basicTab.classList) basicTab.classList.add('active');
        }
        
        const basicTabButton = document.querySelector('.tab-button[onclick*="basic"]') || document.querySelectorAll('.tab-button')[1];
        if (basicTabButton && basicTabButton.classList) basicTabButton.classList.add('active');
    }
    
    let container = document.querySelector('.tab-content.active') || document.getElementById('basic-tab') || document.getElementById('enhanced-tab');
    console.log('Container found:', container);
    
    if (!container) {
        console.log('No container found, using document.body');
        container = document.body;
    }
    
    console.log('Checking conditions:');
    console.log('result && result.credentials:', result && result.credentials);
    console.log('flow_type check:', result && (result.flow_type === 'basic_authenticated' || result.flow_type === 'basic_guest'));
    
    if (result && result.credentials) {
        // Scenario 1: Guest Access Success 
        if (result.flow_type === 'basic_guest' || result.flow_type === 'enhanced_guest') {
            showAuthSuccessWithOptions(result, 'Guest');
            return;
        }
        if (result.flow_type === 'basic_authenticated' || result.flow_type === 'basic_authenticated') {
            showAuthSuccessWithOptions(result, result.provider);
            return;
        } else {
            container.innerHTML = `
                <div class="flow-success-container fade-in">
                    <h4 class="flow-title fade-in-scale"> Enhanced flow success!</h4>
                    <p class="fade-in fade-in-delay-1"><strong>Identity Provider:</strong> ${result.provider}</p>
                    <p class="fade-in fade-in-delay-1"><strong>Authorization Flow:</strong> ${result.flow_type} (2-Step Process)</p>
                    <p class="fade-in fade-in-delay-2"><strong>Step 1:</strong> <span class="api-call">GetId()</span> → Identity ID: <code>${result.identity_id}</code></p>
                    <p class="fade-in fade-in-delay-2"><strong>Step 2:</strong> <span class="api-call">GetCredentialsForIdentity()</span> → AWS credentials</p>
                    <h4 class="section-title fade-in fade-in-delay-3">AWS credentials:</h4>
                    <pre class="credentials-json-basic fade-in fade-in-delay-3"><code>{
  "IdentityId": "${result.identity_id}",
  "Credentials": {
    "AccessKeyId": "${result.credentials.AccessKeyId}",
    "SecretAccessKey": "${result.credentials.SecretKey || result.credentials.SecretAccessKey}",
    "SessionToken": "${result.credentials.SessionToken}",
    "Expiration": "${result.credentials.Expiration}"
  }
}</code></pre>
                    <div class="next-steps-box fade-in fade-in-delay-3">
                        <h4 class="flow-title">Next Steps</h4>
                        <p>You can use these credentials to:</p>
                        <ul class="info-list">
                            <li>Make authenticated AWS API calls</li>
                            <li>Access AWS resources according to your IAM role permissions</li>
                            <li>Use the AWS SDK in your application</li>
                        </ul>
                    </div>
                    ${result.provider !== 'Guest' ? `<button onclick="resetToProviderSelection()" class="btn-auth-tertiary fade-in fade-in-delay-3">Try Another Provider</button>` : ''}
                </div>
            `;
            setTimeout(() => handleScrollAnimation(), 100);
        }
    } else {
        let errorDetails = '';
        if (result && result.error) {
            errorDetails = `
                <p><strong>Error:</strong> ${result.error}</p>
                ${result.details ? `<p><strong>Details:</strong> ${result.details}</p>` : ''}
                ${result.error_type ? `<p><strong>Type:</strong> ${result.error_type}</p>` : ''}
            `;
        }
        
        // Handle OIDC configuration errors specially
        if (result && result.error_type === 'configuration_error') {
            container.innerHTML = `
                <div class="error-container">
                    <h4 class="error-title">⚙️ OIDC Setup Required</h4>
                    <p><strong>${result.error}</strong></p>
                    <div class="setup-instructions">
                        <p>${result.details}</p>
                        <div class="help-links">
                            <a href="https://console.aws.amazon.com/iam/home#/identity_providers" target="_blank">→ Configure IAM OIDC Provider</a><br>
                            <a href="https://docs.aws.amazon.com/cognito/latest/developerguide/open-id.html" target="_blank">→ View AWS Documentation</a>
                        </div>
                    </div>
                    <button onclick="resetToProviderSelection()" class="btn-auth-tertiary">Choose Different Provider</button>
                </div>
            `;
        } else {
            container.innerHTML = `
                <div class="error-container">
                    <h4 class="error-title">Authentication failed</h4>
                    <p>${message}</p>
                    ${errorDetails}
                    <button onclick="resetToProviderSelection()" class="btn-auth-tertiary">Try Again</button>
                </div>
            `;
        }
    }
}

function showDetailedAPIFlow() {
    if (!window.currentAuthResult) {
        alert('No authentication result available');
        return;
    }
    
    const result = window.currentAuthResult;
    const flowType = result.flow_type === 'basic_guest' || result.flow_type === 'basic_authenticated' ? 'basic' : 'enhanced';
    
    let container = document.querySelector('.tab-content.active') || document.getElementById('basic-tab') || document.getElementById('enhanced-tab');
    if (!container) {
        container = document.body;
    }
    
    container.innerHTML = getProviderCategories(flowType);
    
    const visualizerId = flowType === 'enhanced' ? 'apiVisualizer' : 'apiVisualizerBasic';
    const visualizer = document.getElementById(visualizerId);
    
    if (visualizer && window.flowManager) {
        visualizer.style.display = 'block';
        window.flowManager.initFlow(flowType);
        
        setTimeout(() => {
            visualizer.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 300);
        
        setTimeout(() => {
            window.flowManager.showCompletedFlow(result);
        }, 800);
    }
}

function showGuestDetailedAPIFlow() {
    if (!window.currentAuthResult) {
        alert('No authentication result available');
        return;
    }
    
    const result = window.currentAuthResult;
    const flowType = result.flow_type === 'basic_guest' || result.flow_type === 'basic_authenticated' ? 'basic' : 'enhanced';
    
    let container = document.querySelector('.tab-content.active') || document.getElementById('basic-tab') || document.getElementById('enhanced-tab');
    if (!container) {
        container = document.body;
    }
    
    showAPIFlowExplanation(result, flowType, container);
}

function getEnhancedVisualizer() {
    return `
        <div id="apiVisualizer" class="api-visualizer" style="display: none;">
            <h3>Enhanced flow progress</h3>
            <div class="flow-diagram" id="flowDiagram">
                <div class="api-step current">
                    <div class="step-indicator">0</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name">Provider Authentication</div>
                            <div class="step-status current">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-0">
                            <div class="payload-label">Status:</div>
                            Waiting for authentication...
                        </div>
                    </div>
                </div>
                <div class="api-step">
                    <div class="step-indicator">1</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name"><span class="api-call" onclick="openApiPanel('GetId')">GetId()</span></div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-1">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
                <div class="api-step">
                    <div class="step-indicator">2</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name"><span class="api-call" onclick="openApiPanel('GetCredentialsForIdentity')">GetCredentialsForIdentity()</span></div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-2">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
                <div class="api-step fade-in fade-in-delay-3">
                    <div class="step-indicator">3</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name">Enhanced flow Success</div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-3">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Unified guest visualizer generation
function getGuestVisualizerUnified(flowType, isGuest = true) {
    const flowConfig = {
        enhanced: {
            steps: ['GetId()', 'GetCredentialsForIdentity()', 'Enhanced flow Success'],
            title: 'Enhanced flow progress',
            visualizerId: 'apiVisualizer',
            diagramId: 'flowDiagram',
            payloadPrefix: 'payload'
        },
        basic: {
            steps: ['GetId()', 'GetOpenIdToken()', 'AssumeRoleWithWebIdentity()', 'Basic flow Success'],
            title: 'Basic flow progress',
            visualizerId: 'apiVisualizerBasic',
            diagramId: 'flowDiagramBasic',
            payloadPrefix: 'payload-basic'
        }
    };

    const config = flowConfig[flowType];
    const guestSuffix = isGuest ? ' (Guest Access)' : '';

    const stepsHtml = config.steps.map((stepName, index) => `
                <div class="api-step ${index === 0 ? 'current' : ''}">
                    <div class="step-indicator">${index + 1}</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name"><span class="api-call" onclick="openApiPanel('${stepName.replace('()', '')}')">${stepName}</span></div>
                            <div class="step-status ${index === 0 ? 'current' : ''}">waiting</div>
                        </div>
                        <div class="payload-content" id="${config.payloadPrefix}-${index}">
                            <div class="payload-label">Status:</div>
                            ${index === 0 ? 'Waiting for authentication...' : 'Waiting for previous step to complete...'}
                        </div>
                    </div>
                </div>`).join('');

    return `
        <div id="${config.visualizerId}" class="api-visualizer" style="display: none;">
            <h3>${config.title}${guestSuffix}</h3>
            <div class="flow-diagram" id="${config.diagramId}">
${stepsHtml}
            </div>
        </div>
    `;
}

// Backward compatible wrapper functions 
function getGuestEnhancedVisualizer() {
    return getGuestVisualizerUnified('enhanced', true);
}

function getGuestBasicVisualizer() {
    return getGuestVisualizerUnified('basic', true);
}

function getBasicVisualizer() {
    return `
        <div id="apiVisualizerBasic" class="api-visualizer" style="display: none;">
            <h3>Basic flow progress</h3>
            <div class="flow-diagram" id="flowDiagramBasic">
                <div class="api-step current">
                    <div class="step-indicator">0</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name">Provider authentication</div>
                            <div class="step-status current">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-basic-0">
                            <div class="payload-label">Status:</div>
                            Waiting for authentication...
                        </div>
                    </div>
                </div>
                <div class="api-step">
                    <div class="step-indicator">1</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name">GetId()</div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-basic-1">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
                <div class="api-step">
                    <div class="step-indicator">2</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name"><span class="api-call" onclick="openApiPanel('GetOpenIdToken')">GetOpenIdToken()</span></div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-basic-2">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
                <div class="api-step">
                    <div class="step-indicator">3</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name"><span class="api-call" onclick="openApiPanel('AssumeRoleWithWebIdentity')">AssumeRoleWithWebIdentity()</span></div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-basic-3">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
                <div class="api-step">
                    <div class="step-indicator">4</div>
                    <div class="step-content">
                        <div class="step-header">
                            <div class="api-name">Basic flow success</div>
                            <div class="step-status">waiting</div>
                        </div>
                        <div class="payload-content" id="payload-basic-4">
                            <div class="payload-label">Status:</div>
                            Waiting for previous step to complete...
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function getProviderCategories(flowType) {
    if (flowType === 'enhanced') {
        return `
            <div class="provider-categories">
                <div class="provider-category">
                    <h4 class="category-title">Social identity providers</h4>
                    <p class="category-description">Amazon Cognito provides direct integration with Login with Amazon, Sign in with Apple, Google, Facebook, and Twitter. Provide information about your developer app or project to your identity pools, and Amazon Cognito trusts OAuth 2.0 tokens that the social provider issues on behalf of your app.</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="signInGoogle();">
                            <span class="provider-item-name">Sign in with Google</span>
                        </div>
                        <div class="provider-item" onclick="signInFacebook();">
                            <span class="provider-item-name">Sign in with Facebook</span>
                        </div>
                        <div class="provider-item" onclick="signInAmazon();">
                            <span class="provider-item-name">Login with Amazon</span>
                        </div>
                    </div>
                </div>
                
                <div class="provider-category">
                    <h4 class="category-title">Enterprise providers (OIDC or SAML)</h4>
                    <p class="category-description">You can trust authenticated claims from any identity provider that issues SAML 2.0 assertions or OIDC tokens. Amazon Cognito integrates with identity providers that you have configured in AWS Identity and Access Management (IAM).</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="signInOIDC()">
                            <span class="provider-item-name">OpenID Connect (OIDC)</span>
                        </div>
                        <div class="provider-item" onclick="signInSAML()">
                            <span class="provider-item-name">SAML 2.0</span>
                        </div>
                    </div>
                </div>
                
                <div class="provider-category">
                    <h4 class="category-title">Custom developer provider</h4>
                    <p class="category-description">Issue credentials to users who authenticate with your own developer provider.</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="showDeveloperAuthForm('enhanced')">
                            <span class="provider-item-name">Custom developer provider</span>
                        </div>
                    </div>
                </div>
                
                <div class="provider-category">
                    <h4 class="category-title">Amazon Cognito user pool</h4>
                    <p class="category-description">Issue credentials to users who authenticate through an Amazon Cognito user pool. Your users can sign in to a user pool using the built-in user directory or through a third-party identity provider.</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="signInUserPoolOIDC()">
                            <span class="provider-item-name">Cognito user pool</span>
                        </div>
                    </div>
                </div>
            </div>
            <div id="apiVisualizer" class="api-visualizer" style="display: none;">
                <h3>Enhanced flow progress</h3>
                <div class="flow-diagram" id="flowDiagram">
                    <!-- Dynamic steps will be generated by flow-visualizer.js -->
                </div>
            </div>
        `;
    } else {
        return `
            <div class="provider-categories">
                <div class="provider-category">
                    <h4 class="category-title">Social identity providers</h4>
                    <p class="category-description">Amazon Cognito provides direct integration with Login with Amazon, Sign in with Apple, Google, Facebook, and Twitter. Provide information about your developer app or project to your identity pools, and Amazon Cognito trusts OAuth 2.0 tokens that the social provider issues on behalf of your app.</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="signInGoogleBasic();">
                            <span class="provider-item-name">Sign in with Google</span>
                        </div>
                        <div class="provider-item" onclick="signInFacebookBasic();">
                            <span class="provider-item-name">Sign in with Facebook</span>
                        </div>
                        <div class="provider-item" onclick="signInAmazonBasic();">
                            <span class="provider-item-name">Login with Amazon</span>
                        </div>
                    </div>
                </div>
                
                <div class="provider-category">
                    <h4 class="category-title">Enterprise providers (OIDC or SAML)</h4>
                    <p class="category-description">You can trust authenticated claims from any identity provider that issues SAML 2.0 assertions or OIDC tokens. Amazon Cognito integrates with identity providers that you have configured in AWS Identity and Access Management (IAM).</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="signInOIDCBasic()">
                            <span class="provider-item-name">OpenID Connect (OIDC)</span>
                        </div>
                        <div class="provider-item" onclick="signInSAMLBasic()">
                            <span class="provider-item-name">SAML 2.0</span>
                        </div>
                    </div>
                </div>
                
                <div class="provider-category">
                    <h4 class="category-title">Custom developer provider</h4>
                    <p class="category-description">Issue credentials to users who authenticate with your own developer provider.</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="showDeveloperAuthForm('basic')">
                            <span class="provider-item-name">Custom developer provider</span>
                        </div>
                    </div>
                </div>
                
                <div class="provider-category">
                    <h4 class="category-title">Amazon Cognito user pool</h4>
                    <p class="category-description">Issue credentials to users who authenticate through an Amazon Cognito user pool. Your users can sign in to a user pool using the built-in user directory or through a third-party identity provider.</p>
                    <div class="provider-list">
                        <div class="provider-item" onclick="signInUserPoolBasic()">
                            <span class="provider-item-name">Cognito user pool</span>
                        </div>
                    </div>
                </div>
            </div>
            <div id="apiVisualizerBasic" class="api-visualizer" style="display: none;">
                <h3>Basic flow progress</h3>
                <div class="flow-diagram" id="flowDiagramBasic">
                    <!-- Dynamic steps will be generated by flow-visualizer.js -->
                </div>
            </div>
        `;
    }
}

// Trigger animations after dynamic content is loaded
function triggerAnimationsAfterLoad() {
    setTimeout(() => {
        if (typeof handleScrollAnimation === 'function') {
            handleScrollAnimation();
        }
    }, 100);
}



// Success options after OAuth completion
function showAuthSuccessWithOptions(result, provider) {
    window.currentAuthResult = result;
    
    const isGuest = provider === 'Guest';
    const isBasicFlow = result && (result.flow_type === 'basic_authenticated' || result.flow_type === 'basic_guest');
    
    // Force switch to appropriate tab
    if (isBasicFlow) {
        document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
        document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
        
        if (isGuest) {
            // For guest access, use guest-basic-tab
            const guestBasicTab = document.getElementById('guest-basic-tab');
            if (guestBasicTab) {
                guestBasicTab.classList.add('active');
            }
            // Also activate the basic tab button
            const basicTabButton = document.querySelector('.tab-button[onclick*="basic"]') || document.querySelectorAll('.tab-button')[1];
            if (basicTabButton) basicTabButton.classList.add('active');
        } else {
            // For authenticated access, use basic-tab
            const basicTab = document.getElementById('basic-tab');
            if (basicTab) {
                basicTab.classList.add('active');
            }
            const basicTabButton = document.querySelector('.tab-button[onclick*="basic"]') || document.querySelectorAll('.tab-button')[1];
            if (basicTabButton) basicTabButton.classList.add('active');
        }
    }
    
    // Get the active container
    let container = document.querySelector('.tab-content.active');
    
    // Fallback to appropriate tab if no active container found
    if (!container) {
        if (isGuest && isBasicFlow) {
            container = document.getElementById('guest-basic-tab');
        } else if (isGuest) {
            container = document.getElementById('guest-enhanced-tab');
        } else if (isBasicFlow) {
            container = document.getElementById('basic-tab');
        } else {
            container = document.getElementById('enhanced-tab');
        }
    }
    
    // Final fallback
    if (!container) {
        container = document.body;
    }
    
    container.innerHTML = `
        <div class="auth-success-container fade-in">
            <h3 class="success-title fade-in-scale">${provider.charAt(0).toUpperCase() + provider.slice(1)} authentication successful!</h3>
            <p class="fade-in fade-in-delay-1">You now have temporary AWS credentials for accessing AWS services.</p>
            
            <div class="action-buttons fade-in fade-in-delay-2">
                <button class="btn-auth-secondary" onclick="${isGuest ? 'showGuestDetailedAPIFlow()' : 'showDetailedAPIFlow()'}">
                    View detailed API flow
                </button>
                <button class="btn-auth-secondary" onclick="showJustCredentials()">
                    View AWS credentials only
                </button>
                ${!isGuest ? `<button class="btn-auth-secondary" onclick="resetToProviderSelection()">
                    Test another provider
                </button>` : ''}
            </div>
        </div>
    `;
    setTimeout(() => handleScrollAnimation(), 100);
}

function showJustCredentials() {
    if (!currentAuthResult) return;
    const result = currentAuthResult;
    const container = document.querySelector('.tab-content.active');
    
    const credentialsJson = {
        "IdentityId": result.identity_id,
        "Credentials": {
            "AccessKeyId": result.credentials.AccessKeyId,
            "SecretAccessKey": result.credentials.SecretAccessKey || result.credentials.SecretKey,
            "SessionToken": result.credentials.SessionToken,
            "Expiration": result.credentials.Expiration
        }
    };
    
    container.innerHTML = `
        <div class="credentials-display-container fade-in">
            <h3 class="credentials-title fade-in-scale">AWS credentials</h3>
            <p class="fade-in fade-in-delay-1">Your temporary AWS credentials for accessing AWS services:</p>
            
            <pre class="credentials-json fade-in fade-in-delay-2"><code>${JSON.stringify(credentialsJson, null, 2)}</code></pre>
            
            <div class="info-box-blue fade-in fade-in-delay-3">
                <h4 class="credentials-title">Next Steps</h4>
                <p>You can use these credentials to:</p>
                <ul class="info-list">
                    <li>Make authenticated AWS API calls</li>
                    <li>Access AWS resources according to your IAM role permissions</li>
                    <li>Use with AWS SDK in your applications</li>
                </ul>
            </div>
            
            <div class="action-buttons fade-in fade-in-delay-3">
                <button class="btn-auth-secondary" onclick="${result.provider === 'Guest' ? 'showGuestDetailedAPIFlow()' : 'showDetailedAPIFlow()'}">
                    Show API flow details
                </button>
                ${result.provider !== 'Guest' ? `<button class="btn-auth-secondary" onclick="resetToProviderSelection()">
                    Test another provider
                </button>` : ''}
            </div>
        </div>
    `;
    setTimeout(() => handleScrollAnimation(), 100);
}

function showDeveloperAuthForm(flowType) {
    const container = flowType === 'enhanced' ? document.getElementById('enhanced-tab') : document.getElementById('basic-tab');
    
    container.innerHTML = `
        <div style="width: 100%; max-width: none;">
            <div style="width: 100%; max-width: none; margin: 0;">
                <h4 style="font-size: 20px; font-weight: 600; margin-bottom: 15px; color: #333;">You are authenticating with custom developer provider</h4>
                <p style="font-size: 16px; color: #5f6368; margin-bottom: 25px; line-height: 1.5;">Step 1: To proceed with custom developer provider authentication, first enter a user identifier for your custom developer provider. This simulates authenticating a user through your own authentication system.</p>
                
                <div style="width: 100%; max-width: none;">
                    <div style="padding: 30px; background: #f8f9fa; border-radius: 12px; margin-bottom: 20px; width: 100%; box-sizing: border-box;">
                        <label for="developerUserId" style="display: block; margin-bottom: 12px; font-weight: 500; font-size: 16px;">User Identifier:</label>
                        <input type="text" id="developerUserId" style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 25px; font-size: 14px; box-sizing: border-box;" placeholder="Enter user ID (e.g., user123)" value="demo-user-123">
                        
                        <div style="display: flex !important; gap: 20px; flex-wrap: wrap; width: 100%;">
                            <button class="btn-auth-secondary" onclick="handleDeveloperDetailedFlow('${flowType}')" style="flex: 1; min-width: 200px;">
                                View detailed API flow
                            </button>
                            
                            <button class="btn-auth-secondary" onclick="handleDeveloperCredentialsOnly('${flowType}')" style="flex: 1; min-width: 200px;">
                                View AWS credentials only
                            </button>
                            
                            <button class="btn-auth-secondary" onclick="resetToProviderSelection()" style="flex: 1; min-width: 200px;">
                                Test another provider
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function authenticateDeveloperUser(flowType, userId) {
    try {
        const response = await fetch('http://localhost:8006/api/authenticate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                provider_type: 'Developer', 
                provider_token: userId,
                flow_type: flowType
            })
        });
        
        const result = await response.json();
        window.currentAuthResult = result;
        return result;
    } catch (error) {
        return { success: false, error: error.message };
    }
}

async function handleDeveloperDetailedFlow(flowType) {
    const userIdInput = document.getElementById('developerUserId');
    const userId = userIdInput.value.trim();
    
    if (!userId) {
        alert('Please enter a user identifier');
        return;
    }
    
    // Authenticate in background
    const result = await authenticateDeveloperUser(flowType, userId);
    
    if (result.success) {
        showDeveloperAPIFlowExplanation(result, flowType);
    } else {
        showResult(`Developer authentication failed: ${result.error}`, result);
    }
}

async function handleDeveloperCredentialsOnly(flowType) {
    const userIdInput = document.getElementById('developerUserId');
    const userId = userIdInput.value.trim();
    
    if (!userId) {
        alert('Please enter a user identifier');
        return;
    }
    
    // Authenticate in background
    const result = await authenticateDeveloperUser(flowType, userId);
    
    if (result.success) {
        showResult('Developer authentication successful!', result);
    } else {
        showResult(`Developer authentication failed: ${result.error}`, result);
    }
}

function showDeveloperAPIFlowExplanation(result, flowType) {
    const container = document.querySelector('.tab-content.active');
    const isBasicFlow = flowType === 'basic';
    
    const explanationHTML = `
        <div class="api-flow-explanation-container">
            <div class="explanation-header">
                <h3 class="explanation-title">API flow visualization</h3>
                <p class="explanation-subtitle">Understanding Your Developer Authentication Process</p>
            </div>
            
            <div class="explanation-content">
                <div class="flow-info-box">
                    <h4 class="info-title">What you'll see:</h4>
                    <ul class="explanation-list">
                        <li><strong>Step-by-step API calls</strong> that happened during your authentication</li>
                        <li><strong>Real request/response data</strong> from AWS Cognito services</li>
                    </ul>
                </div>
                
                <div class="flow-details-box">
                    <h4 class="info-title">Your authentication flow:</h4>
                    <div class="flow-type-badge ${isBasicFlow ? 'basic' : 'enhanced'}">
                        ${isBasicFlow ? 'Basic flow (3 steps)' : 'Enhanced flow (2 steps)'}
                    </div>
                    <p class="flow-description">
                        ${isBasicFlow 
                            ? 'Developer authentication with basic flow uses: GetOpenIdTokenForDeveloperIdentity() → GetOpenIdToken() → AssumeRoleWithWebIdentity(). This provides granular control over the authentication process.'
                            : 'Developer authentication with enhanced flow uses: GetOpenIdTokenForDeveloperIdentity() → GetCredentialsForIdentity(). This is a streamlined approach for custom developer providers.'
                        }
                    </p>
                </div>
                
                <div class="learning-objectives-box">
                    <h4 class="info-title">What you'll learn:</h4>
                    <ul class="explanation-list">
                        <li>How custom developer providers integrate with AWS Cognito</li>
                        <li>The difference between developer authentication and social providers</li>
                        <li>AWS API calls specific to developer authenticated identities</li>
                    </ul>
                </div>
                
                <div class="visualization-note">
                    <p><strong>Note:</strong> The visualization will replay your actual authentication process with the real data that was used to get your AWS credentials.</p>
                </div>
            </div>
            
            <div class="explanation-actions">
                <button class="btn-auth-secondary" onclick="startDeveloperAPIVisualization('${flowType}')">
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
}

function startDeveloperAPIVisualization(flowType) {
    if (!window.currentAuthResult) {
        alert('Authentication data lost. Please try again.');
        return;
    }
    
    const container = document.querySelector('.tab-content.active');
    
    container.innerHTML = `
        <div class="loading-visualization">
            <h3 class="loading-title">Preparing API flow visualization...</h3>
            <p>Setting up the step-by-step demonstration of your developer authentication process.</p>
            <div class="loading-spinner"></div>
        </div>
    `;
    
    setTimeout(() => {
        container.innerHTML = getProviderCategories(flowType);
        
        const visualizerId = flowType === 'enhanced' ? 'apiVisualizer' : 'apiVisualizerBasic';
        const visualizer = document.getElementById(visualizerId);
        
        if (visualizer) {
            visualizer.style.display = 'block';
            window.flowManager.initFlow(flowType);
            
            setTimeout(() => {
                visualizer.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 300);
            
            setTimeout(() => {
                window.flowManager.showCompletedFlow(window.currentAuthResult);
            }, 800);
        }
    }, 1500);
}

function resetToProviderSelection() {
    const enhancedTab = document.getElementById('enhanced-tab');
    const basicTab = document.getElementById('basic-tab');
    
    if (enhancedTab) {
        enhancedTab.innerHTML = getProviderCategories('enhanced');
    }
    if (basicTab) {
        basicTab.innerHTML = getProviderCategories('basic');
    }
    
    // Reset any global state
    window.currentAuthResult = null;
}