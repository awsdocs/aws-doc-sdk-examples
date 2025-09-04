// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Configuration - Load from environment or backend
let CONFIG = {
    region: '',
    userPoolId: '',
    clientId: '',
    userPoolDomain: '',
    apiEndpoint: ''
};

// Domain detection helper functions
function isCustomDomain(domain) {
    return domain && domain.includes('.');
}

function buildHostedUIUrl(domain, region, clientId, redirectUri) {
    const baseUrl = isCustomDomain(domain) 
        ? `https://${domain}`
        : `https://${domain}.auth.${region}.amazoncognito.com`;
    
    return `${baseUrl}/login?client_id=${clientId}&response_type=code&scope=openid%20email%20profile&redirect_uri=${encodeURIComponent(redirectUri)}`;
}

window.flowManager = null;
window.currentAuthResult = null;

async function loadConfig() {
    try {
        const response = await fetch('http://localhost:8006/api/config.json');
        if (response.ok) {
            CONFIG = await response.json();
            console.log('Configuration loaded from backend:', CONFIG);
            return;
        } else {
            console.error('Config response not ok:', response.status, response.statusText);
        }
    } catch (error) {
        console.error('Failed to load configuration:', error.message);
    }
    
    // No fallback - force proper configuration
    throw new Error('Configuration not found. Please check your .env file and restart the server.');
}

function getBaseUrl() {
    return CONFIG.apiEndpoint.replace('/api/authenticate', '');
}

// Social identity providers - Generic function
function signInSocialProvider(provider, flowType = 'enhanced') {
    sessionStorage.setItem('track_api_flow', 'true');
    sessionStorage.setItem('flow_type', flowType);
    sessionStorage.setItem('provider', provider);
    if (flowType === 'basic') {
        window.location.href = `${getBaseUrl()}/auth/${provider}?flow=basic`;
    } else {
        window.location.href = `${getBaseUrl()}/auth/${provider}`;
    }
}

// Backward compatible wrappers
function signInGoogle() { signInSocialProvider('google', 'enhanced'); }
function signInFacebook() { signInSocialProvider('facebook', 'enhanced'); }
function signInAmazon() { signInSocialProvider('amazon', 'enhanced'); }
function signInGoogleBasic() { signInSocialProvider('google', 'basic'); }
function signInFacebookBasic() { signInSocialProvider('facebook', 'basic'); }
function signInAmazonBasic() { signInSocialProvider('amazon', 'basic'); }

// Enterprise Providers - Generic approach following AWS requirements
function signInOIDC() {
    signInSocialProvider('oidc', 'enhanced');
}

function signInOIDCBasic() {
    signInSocialProvider('oidc', 'basic');
}

function signInSAML() {
    window.location.href = `${getBaseUrl()}/auth/saml`;
}

// User pool providers - Use Hosted UI as intended
function signInUserPool(flowType = 'enhanced') {
    if (!CONFIG.userPoolDomain || !CONFIG.clientId) {
        alert('User Pool configuration missing. Please check your .env file.');
        return;
    }
    sessionStorage.setItem('track_api_flow', 'true');
    sessionStorage.setItem('flow_type', flowType);
    sessionStorage.setItem('provider', 'userpool');
    const redirectUri = window.location.origin + '/';
    const hostedUIUrl = buildHostedUIUrl(CONFIG.userPoolDomain, CONFIG.region, CONFIG.clientId, redirectUri);
    window.location.href = hostedUIUrl;
}



// Backward compatible wrappers
function signInUserPoolOIDC() { signInUserPool('enhanced'); }
function signInUserPoolBasic() { signInUserPool('basic'); }

// Test functions - Generic function
async function testProvider(flowType, providerType, userToken = null) {
    showAPIVisualizer(flowType);
    const mockTokens = { 'Developer': userToken || 'mock-developer-user-id' };
    const token = mockTokens[providerType];
    if (window.flowManager) {
        await window.flowManager.executeRealFlow(providerType, token, flowType);
    }
}

// Backward compatible wrappers
async function testEnhancedProvider(providerType, userToken = null) {
    await testProvider('enhanced', providerType, userToken);
}

async function testBasicProvider(providerType, userToken = null) {
    await testProvider('basic', providerType, userToken);
}

async function testEnhancedGuest() {
    try {
        console.log('Starting enhanced guest test...');
        console.log('API Endpoint:', CONFIG.apiEndpoint);
        
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 60000); // 60 second timeout
        
        const response = await fetch(CONFIG.apiEndpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ provider_type: 'Guest', provider_token: 'none', flow_type: 'enhanced' }),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('API result:', result);
        
        if (result.success) {
            console.log('✓ Guest authentication successful!');
            console.log('You now have temporary AWS credentials for accessing AWS services.');
            console.log('To see detailed API flow, choose "View detailed API flow" button on your web interface.');
            showResult('Guest authentication successful!', result);
        } else {
            console.log('✗ Guest authentication failed:', result.error);
            showResult(`Guest authentication failed: ${result.error}`, result);
        }
    } catch (error) {
        console.error('testEnhancedGuest error:', error);
        if (error.name === 'AbortError') {
            showResult('Request timed out. Please try again.', null);
        } else {
            showResult(`Error: ${error.message}`, null);
        }
    }
}

async function testBasicGuest() {
    try {
        console.log('Starting basic guest test...');
        console.log('API Endpoint:', CONFIG.apiEndpoint);
        
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 60000); // 60 second timeout
        
        const response = await fetch(CONFIG.apiEndpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ provider_type: 'Guest', provider_token: 'none', flow_type: 'basic' }),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('API result:', result);
        
        if (result.success) {
            console.log('✓ Guest authentication successful!');
            console.log('You now have temporary AWS credentials for accessing AWS services.');
            console.log('To see detailed API flow, choose "View detailed API flow" button on your web interface.');
            showResult('Guest authentication successful!', result);
        } else {
            console.log('✗ Guest authentication failed:', result.error);
            showResult(`Guest authentication failed: ${result.error}`, result);
        }
    } catch (error) {
        console.error('testBasicGuest error:', error);
        if (error.name === 'AbortError') {
            showResult('Request timed out. Please try again.', null);
        } else {
            showResult(`Error: ${error.message}`, null);
        }
    }
}

// Core API functions
async function callEnhancedFlow(providerType, token, resultElementId) {
    try {
        const response = await fetch(CONFIG.apiEndpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ provider_type: providerType, provider_token: token })
        });
        
        const result = await response.json();
        
        if (result.success) {
            showResult(`${providerType} authentication successful!`, result);
        } else {
            showResult(`${providerType} authentication failed: ${result.error}`, null);
        }
    } catch (error) {
        showResult(`Error: ${error.message}`, null);
    }
}

async function exchangeCodeForTokens(authCode) {
    try {
        const response = await fetch(`${getBaseUrl()}/auth/userpool/callback`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                code: authCode, 
                redirect_uri: window.location.origin + '/' 
            })
        });
        
        const result = await response.json();
        
        console.log('Token exchange result:', result);
        
        if (result.note && result.note.includes('WAF bypass')) {
            console.log('WAF bypass successful - proceeding with normal flow');
        }
        
        if (result.error) {
            showResult(`⚠️ ${result.message || result.error}`, null);
            return;
        }
        
        if (result.id_token) {
            const flowType = sessionStorage.getItem('flow_type') || 'enhanced';
            
            const authResponse = await fetch(CONFIG.apiEndpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    provider_type: 'UserPool', 
                    provider_token: result.id_token,
                    flow_type: flowType
                })
            });
            
            const authResult = await authResponse.json();
            
            if (authResult.success) {
                // Redirect like other providers with success result
                const resultParam = encodeURIComponent(JSON.stringify(authResult));
                window.location.href = `${window.location.origin}/?userpool_auth=success&result=${resultParam}`;
            } else {
                showResult(`Identity Pool failed: ${authResult.error}`, null);
            }
        }
    } catch (error) {
        showResult(`Error: ${error.message}`, null);
    }
}

// Direct User Pool authentication - bypasses WAF completely
async function authenticateUserPoolDirect(username, password, flowType = 'enhanced') {
    try {
        const response = await fetch(`${getBaseUrl()}/api/userpool-direct-auth`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const result = await response.json();
        
        if (result.success) {
            // Use the ID token with Identity Pool
            const authResponse = await fetch(CONFIG.apiEndpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    provider_type: 'UserPool', 
                    provider_token: result.tokens.IdToken,
                    flow_type: flowType
                })
            });
            
            const authResult = await authResponse.json();
            
            if (authResult.success) {
                // Redirect like other providers to show API flow
                const resultParam = encodeURIComponent(JSON.stringify(authResult));
                window.location.href = `${window.location.origin}/?userpool_auth=success&result=${resultParam}`;
            } else {
                showResult(`Identity Pool failed: ${authResult.error}`, null);
            }
        } else {
            showResult(`User Pool authentication failed: ${result.error}`, null);
        }
    } catch (error) {
        showResult(`Error: ${error.message}`, null);
    }
}

// Tab switching functions that consolidates duplicate tab management logic into a single parameterized function
function switchTabUnified(flowType, isGuest = false) {
    const tabPrefix = isGuest ? 'guest-' : '';
    const tabSelectors = isGuest ? 
        ['#guest-enhanced-tab', '#guest-basic-tab'] : 
        ['#enhanced-tab', '#basic-tab'];
    
    // Hide all relevant tabs
    document.querySelectorAll(tabSelectors.join(', ')).forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    const targetTabId = tabPrefix + flowType + '-tab';
    const targetTab = document.getElementById(targetTabId);
    if (targetTab) {
        targetTab.classList.add('active');
    }
    
    if (event && event.target) {
        event.target.classList.add('active');
    }
}

// Backward compatible wrapper functions
function switchTab(flowType) {
    switchTabUnified(flowType, false);
}

function switchGuestTab(flowType) {
    switchTabUnified(flowType, true);
}

// SAML authentication
function signInSAML(flowType = 'enhanced') {
    // Check if trying to use SAML with basic flow
    if (flowType === 'basic') {
        showModal({
            title: 'SAML Not Supported in Basic Flow',
            message: 'SAML identity providers only work with the enhanced flow. Amazon Cognito requires the enhanced flow for SAML authentication because it handles role selection automatically through SAML assertions.'
        });
        return;
    }
    
    sessionStorage.setItem('track_api_flow', 'true');
    sessionStorage.setItem('flow_type', flowType);
    sessionStorage.setItem('provider', 'saml');
    
    // Redirect to backend SAML handler
    window.location.href = 'http://localhost:8006/auth/saml';
}

// SAML basic flow - will show modal instead
function signInSAMLBasic() {
    signInSAML('basic');
}

// Show API visualizer
function showAPIVisualizer(flowType) {
    const visualizerId = flowType === 'enhanced' ? 'apiVisualizer' : 'apiVisualizerBasic';
    const visualizer = document.getElementById(visualizerId);
    if (visualizer) {
        visualizer.style.display = 'block';
        visualizer.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}