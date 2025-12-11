
Android WebView App with Firebase Cloud Messaging support
--------------------------------------------------------

What's included:
- WebView app that loads: https://almostafa.free.nf/v1/
- FirebaseMessagingService to show native notifications
- Code that obtains FCM token and posts it to: https://almostafa.free.nf/v1/register_token.php
- JavaScript bridge: window.__fcm_token is injected into page JS

What you MUST do:
1) Create a Firebase project and add an Android app with package name: com.example.webviewapp
   - Download google-services.json and place it into app/ directory.

2) In Firebase Console, enable Cloud Messaging.

3) On your website server:
   - Create endpoint register_token.php to accept POST 'token' and store tokens in a DB/table.
   - When you want to send a push to app users, use Firebase Admin SDK or FCM HTTP v1 API to send messages to stored tokens.

Example server-side (PHP) send using FCM legacy HTTP (simpler but deprecated):

POST https://fcm.googleapis.com/fcm/send
Headers:
  Authorization: key=YOUR_SERVER_KEY
  Content-Type: application/json

Body:
{
  "registration_ids": ["token1", "token2"],
  "notification": {
    "title": "Hello",
    "body": "This is a test"
  }
}

Notes:
- Web push (Service Worker) inside WebView is NOT supported for persistent Push API; using native FCM is the reliable approach.
- App is fullscreen (No ActionBar).
- Place your logo image in app/src/main/res/drawable/logo.png to be used in app.
