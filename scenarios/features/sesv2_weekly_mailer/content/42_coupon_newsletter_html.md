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
  Create the coupon-newsletter.html sample file for the workflow specification.
---
```html
<!DOCTYPE html>
<html>
<head>
    <title>Weekly Coupons Newsletter</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }
        h1 {
            color: #333;
        }
        ul {
            list-style-type: none;
            padding: 0;
        }
        li {
            background-color: #f5f5f5;
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <h1>Weekly Coupons Newsletter</h1>
    <p>Check out this week's hot deals and exclusive coupons!</p>
    <ul>
        {{#coupons}}
        <li>{{details}}</li>
        {{/coupons}}
    </ul>
    <p>
        Hurry, these offers won't last long! Visit our website or your nearest store to take advantage of these amazing deals.
    </p>
    <p>
        Happy shopping!<br>
        The Weekly Coupons Team
    </p>
</body>
</html>
```

This HTML file can be used as the `coupon-newsletter.html` sample file for the workflow specification. It includes a placeholder for inserting the coupon details using a template syntax (`{{#coupons}}{{details}}{{/coupons}}`). This placeholder will be replaced with the actual coupon data when the email template is rendered and sent using the SES v2 `SendEmail` API call.

The HTML file also includes some basic styling to make the newsletter more visually appealing.