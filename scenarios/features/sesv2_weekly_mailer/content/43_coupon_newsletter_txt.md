---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: |
  Create the coupon-newsletter.txt sample file for the workflow specification.
---
```
Weekly Coupons Newsletter

Check out this week's hot deals and exclusive coupons!

{{#coupons}}
- {{details}}
{{/coupons}}

Hurry, these offers won't last long! Visit our website or your nearest store to take advantage of these amazing deals.

Happy shopping!
The Weekly Coupons Team
```

This plain text file can be used as the `coupon-newsletter.txt` sample file for the workflow specification. It follows a similar structure to the `coupon-newsletter.html` file but without any HTML tags or styling.

The `{{#coupons}}{{details}}{{/coupons}}` placeholder will be replaced with the actual coupon details when the email template is rendered and sent using the SES v2 `SendEmail` API call.

This text version of the newsletter can be used by email clients that do not support HTML or for recipients who prefer to receive plain text emails.