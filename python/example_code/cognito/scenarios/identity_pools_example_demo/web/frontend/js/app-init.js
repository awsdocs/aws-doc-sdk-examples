/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

document.addEventListener('DOMContentLoaded', async function() {
    try {
        await loadConfig();
        console.log('Config loaded successfully:', CONFIG);
    } catch (error) {
        console.error('Failed to load config:', error);
        alert('Configuration loading failed: ' + error.message);
    }
    
    const urlParams = new URLSearchParams(window.location.search);
    const authTypes = ['google', 'amazon', 'facebook', 'oidc', 'saml', 'userpool'];
    
    // Check for basic flow parameters
    let isBasicFlow = false;
    let basicAuthType = null;
    
    for (const type of authTypes) {
        const basicAuthStatus = urlParams.get(`${type}_basic_auth`);
        if (basicAuthStatus) {
            isBasicFlow = true;
            basicAuthType = type;
            break;
        }
    }
    

    // Check for regular auth parameters or basic auth parameters
    authTypes.forEach(type => {
        const authStatus = urlParams.get(`${type}_auth`);
        const basicAuthStatus = urlParams.get(`${type}_basic_auth`);
        
        if (authStatus === 'success' || authStatus === 'error' || basicAuthStatus === 'success' || basicAuthStatus === 'error') {
            // Handle SAML session-based result
            if (type === 'saml' && authStatus === 'success') {
                const sessionId = urlParams.get('session');
                if (sessionId) {
                    handleSAMLSession(sessionId, type, isBasicFlow);
                    return;
                }
            }
            document.querySelectorAll('.trust-card').forEach(card => card.classList.remove('selected'));
            document.querySelector('.trust-card[onclick*="authenticated"]').classList.add('selected');
            
            const providerStep = document.getElementById('provider-step');
            const providerContent = document.getElementById('provider-content');
            const stepArrow = document.getElementById('step-arrow');
            
            providerStep.style.display = 'block';
            stepArrow.style.display = 'block';
            
            providerContent.innerHTML = `
                <p class="step-description">Choose your authentication flow and select identity providers</p>
                
                <div class="flow-tabs">
                    <button class="tab-button ${!isBasicFlow ? 'active' : ''}" onclick="switchTab('enhanced')">Enhanced flow (recommended)</button>
                    <button class="tab-button ${isBasicFlow ? 'active' : ''}" onclick="switchTab('basic')">Basic flow</button>
                </div>
                
                <div id="enhanced-tab" class="tab-content ${!isBasicFlow ? 'active' : ''}">
                    ${getProviderCategories('enhanced')}
                </div>
                
                <div id="basic-tab" class="tab-content ${isBasicFlow ? 'active' : ''}">
                    ${getProviderCategories('basic')}
                </div>
            `;
            
            const resultParam = urlParams.get('result');
            if (resultParam) {
                try {
                    const result = JSON.parse(decodeURIComponent(resultParam));
                    const displayType = basicAuthType || type;
                    const successStatus = authStatus === 'success' || basicAuthStatus === 'success';
                    
                    // Check if API flow tracking is enabled (session storage or URL parameter)
                    const trackApiFlowParam = urlParams.get('track_api_flow');
                    const shouldShowAPIFlow = sessionStorage.getItem('track_api_flow') === 'true' || trackApiFlowParam === 'true';
                    
                    if (shouldShowAPIFlow && successStatus) {
                        // Fix provider name case for user pool and Google
                        let correctedDisplayType = displayType === 'userpool' ? 'UserPool' : displayType;
                        if (correctedDisplayType === 'google') correctedDisplayType = 'Google';
                        if (correctedDisplayType === 'facebook') correctedDisplayType = 'Facebook';
                        if (correctedDisplayType === 'amazon') correctedDisplayType = 'Amazon';
                        if (correctedDisplayType === 'saml') correctedDisplayType = 'SAML';
                        if (correctedDisplayType === 'userpool') correctedDisplayType = 'UserPool';
                        
                        // Store the result globally for API flow visualization
                        window.currentAuthResult = result;
                        showAuthSuccessWithOptions(result, correctedDisplayType);
                        sessionStorage.removeItem('track_api_flow');
                    } else {
                        if (successStatus) {
                            showResult(`${displayType.charAt(0).toUpperCase() + displayType.slice(1)} authentication successful!`, result);
                        } else {
                            showResult(`${displayType.charAt(0).toUpperCase() + displayType.slice(1)} authentication failed!`, result);
                        }
                    }
                } catch (e) {
                    showResult(`${type.charAt(0).toUpperCase() + type.slice(1)} authentication completed!`, null);
                }
            }
            window.history.replaceState({}, document.title, window.location.pathname);
            
            setTimeout(() => {
                document.getElementById('provider-step').scrollIntoView({ behavior: 'smooth', block: 'center' });
            }, 100);
        }
    });
    
    // Check if page content contains JSON tokens (from WAF redirect)
    setTimeout(() => {
        const pageContent = document.body.textContent || document.body.innerText;
        if (pageContent.includes('id_token') && pageContent.includes('access_token')) {
            try {
                const tokens = JSON.parse(pageContent.trim());
                if (tokens.id_token) {
                    handleTokenResponse(tokens);
                    return;
                }
            } catch (e) {
                console.log('Not JSON tokens:', e);
            }
        }
    }, 100);
    
    const authCode = urlParams.get('code');
    if (authCode) {
        document.querySelectorAll('.trust-card').forEach(card => card.classList.remove('selected'));
        document.querySelector('.trust-card[onclick*="authenticated"]').classList.add('selected');
        
        const providerStep = document.getElementById('provider-step');
        const providerContent = document.getElementById('provider-content');
        const stepArrow = document.getElementById('step-arrow');
        
        providerStep.style.display = 'block';
        stepArrow.style.display = 'block';
        
        providerContent.innerHTML = `
            <p class="step-description">Choose your authentication flow and select identity providers</p>
            
            <div class="flow-tabs">
                <button class="tab-button active" onclick="switchTab('enhanced')">Enhanced flow (recommended)</button>
                <button class="tab-button" onclick="switchTab('basic')">Basic flow</button>
            </div>
            
            <div id="enhanced-tab" class="tab-content active">
                ${getProviderCategories('enhanced')}
            </div>
            
            <div id="basic-tab" class="tab-content">
                ${getProviderCategories('basic')}
            </div>
        `;
        
        exchangeCodeForTokens(authCode);
        window.history.replaceState({}, document.title, window.location.pathname);
        
        setTimeout(() => {
            document.getElementById('provider-step').scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 100);
    }
});

// Handle token response from WAF (if successful)
async function handleTokenResponse(tokens) {
    if (tokens && tokens.id_token) {
        // Replace page content with processing message
        document.body.innerHTML = `
            <!DOCTYPE html>
            <html><head><title>Processing...</title></head>
            <body>
                <h2>Processing User Pool Authentication...</h2>
                <p>Please wait while we complete the authentication flow...</p>
                <script src="js/auth-core.js"></script>
                <script src="js/ui-display.js"></script>
            </body></html>
        `;
        
        // Wait for scripts to load
        setTimeout(async () => {
            const flowType = sessionStorage.getItem('flow_type') || 'enhanced';
            
            try {
                const response = await fetch('http://localhost:8006/api/authenticate', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ 
                        provider_type: 'UserPool', 
                        provider_token: tokens.id_token,
                        flow_type: flowType
                    })
                });
                
                const authResult = await response.json();
                
                // Redirect back to main app with result
                const resultParam = encodeURIComponent(JSON.stringify(authResult));
                window.location.href = `http://localhost:8001/?userpool_success=true&result=${resultParam}`;
                
            } catch (error) {
                window.location.href = `http://localhost:8001/?error=${encodeURIComponent(error.message)}`;
            }
        }, 500);
    }
}

// Handle SAML session-based authentication result
async function handleSAMLSession(sessionId, type, isBasicFlow) {
    try {
        const response = await fetch(`http://localhost:8006/api/saml-session/${sessionId}`);
        if (response.ok) {
            const result = await response.json();
            
            // Set up the UI like other providers
            document.querySelectorAll('.trust-card').forEach(card => card.classList.remove('selected'));
            document.querySelector('.trust-card[onclick*="authenticated"]').classList.add('selected');
            
            const providerStep = document.getElementById('provider-step');
            const providerContent = document.getElementById('provider-content');
            const stepArrow = document.getElementById('step-arrow');
            
            providerStep.style.display = 'block';
            stepArrow.style.display = 'block';
            
            providerContent.innerHTML = `
                <p class="step-description">Choose your authentication flow and select identity providers</p>
                
                <div class="flow-tabs">
                    <button class="tab-button ${!isBasicFlow ? 'active' : ''}" onclick="switchTab('enhanced')">Enhanced flow (recommended)</button>
                    <button class="tab-button ${isBasicFlow ? 'active' : ''}" onclick="switchTab('basic')">Basic flow</button>
                </div>
                
                <div id="enhanced-tab" class="tab-content ${!isBasicFlow ? 'active' : ''}">
                    ${getProviderCategories('enhanced')}
                </div>
                
                <div id="basic-tab" class="tab-content ${isBasicFlow ? 'active' : ''}">
                    ${getProviderCategories('basic')}
                </div>
            `;
            
            // Check if API flow tracking is enabled
            if (sessionStorage.getItem('track_api_flow') === 'true') {
                window.currentAuthResult = result;
                if (result.success) {
                    showAuthSuccessWithOptions(result, 'SAML');
                } else {
                    showResult('SAML authentication failed!', result);
                }
                sessionStorage.removeItem('track_api_flow');
            } else {
                if (result.success) {
                    showResult('SAML authentication successful!', result);
                } else {
                    showResult('SAML authentication failed!', result);
                }
            }
            
            // Clean up URL
            window.history.replaceState({}, document.title, window.location.pathname);
            
            document.getElementById('provider-step').scrollIntoView({ behavior: 'smooth', block: 'center' });
        } else {
            showResult('SAML session retrieval failed', null);
        }
    } catch (error) {
        showResult(`SAML session error: ${error.message}`, null);
    }
}