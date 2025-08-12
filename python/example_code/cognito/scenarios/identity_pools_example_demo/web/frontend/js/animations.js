// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Global animation handler for the entire website
(function() {
    'use strict';

    // Enhanced animation handler that supports all animation types
    function handleGlobalAnimations() {
        const animationClasses = ['.fade-in', '.fade-in-left', '.fade-in-right', '.fade-in-scale'];
        
        animationClasses.forEach(className => {
            const elements = document.querySelectorAll(className + ':not(.visible)');
            elements.forEach(element => {
                const elementTop = element.getBoundingClientRect().top;
                const elementVisible = 150;
                
                if (elementTop < window.innerHeight - elementVisible) {
                    element.classList.add('visible');
                }
            });
        });
    }

    // Initialize animations on page load
    function initAnimations() {
        // Initial animation check
        setTimeout(handleGlobalAnimations, 200);
        
        // Add scroll listener
        let ticking = false;
        window.addEventListener('scroll', function() {
            if (!ticking) {
                requestAnimationFrame(function() {
                    handleGlobalAnimations();
                    ticking = false;
                });
                ticking = true;
            }
        });
        
        // Add resize listener
        window.addEventListener('resize', handleGlobalAnimations);
    }

    // Trigger animations for dynamically loaded content
    window.triggerAnimations = function() {
        setTimeout(handleGlobalAnimations, 100);
    };

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAnimations);
    } else {
        initAnimations();
    }

    // Make the animation handler globally available
    window.handleScrollAnimation = handleGlobalAnimations;
})();