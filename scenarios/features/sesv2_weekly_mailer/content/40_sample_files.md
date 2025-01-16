---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: What sample files are needed for this workflow?
---
Based on the specification, the following sample files are needed for this workflow:

1. `welcome.html` - This file should contain the HTML content for the welcome email sent to new subscribers.

2. `coupon-newsletter.html` - This file should contain the HTML template for the weekly coupon newsletter email.

3. `coupon-newsletter.txt` - This file should contain the plain text version of the weekly coupon newsletter email.

4. `sample_coupons.json` - This file should contain a JSON array representing the coupon items to be included in the newsletter email. The JSON structure should be as follows:

```json
{
  "coupons": [
    {
      "details": "20% off on all electronics"
    },
    {
      "details": "Buy one, get one free on books"
    },
    {
      "details": "15% off on home appliances"
    },
    {
      "details": "Free shipping on orders over $50"
    },
    {
      "details": "25% off on outdoor gear"
    },
    {
      "details": "10% off on groceries"
    }
  ]
}
```

The workflow implementation will read the contents of these files at runtime and use them for the respective steps.