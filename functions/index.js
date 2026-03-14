const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// M-Pesa Callback URL - Receives payment notifications from Safaricom
exports.mpesaCallback = functions.https.onRequest(async (req, res) => {
  console.log('📲 M-Pesa Callback Received:', JSON.stringify(req.body));

  try {
    // Safaricom sends the callback data in the request body
    const callbackData = req.body;

    // Extract the main body of the callback
    const body = callbackData.Body;
    if (!body) {
      console.error('❌ Invalid callback: Missing Body');
      return res.status(400).json({ ResultCode: 1, ResultDesc: 'Invalid callback' });
    }

    const stkCallback = body.stkCallback;
    if (!stkCallback) {
      console.error('❌ Invalid callback: Missing stkCallback');
      return res.status(400).json({ ResultCode: 1, ResultDesc: 'Invalid callback' });
    }

    // Extract key information
    const checkoutRequestID = stkCallback.CheckoutRequestID;
    const resultCode = stkCallback.ResultCode;
    const resultDesc = stkCallback.ResultDesc;

    console.log(`📦 Checkout ID: ${checkoutRequestID}`);
    console.log(`🔢 Result Code: ${resultCode}`);
    console.log(`📝 Result Desc: ${resultDesc}`);

    // Store the raw callback in Firestore for auditing
    await admin.firestore().collection('mpesa_callbacks').add({
      checkoutRequestID: checkoutRequestID,
      resultCode: resultCode,
      resultDesc: resultDesc,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      fullData: callbackData
    });

    // Handle successful payment
    if (resultCode === 0) {
      console.log('✅ Payment successful!');

      // Extract payment details from metadata
      const metadata = stkCallback.CallbackMetadata?.Item || [];
      let amount = 0;
      let receipt = '';
      let phone = '';

      metadata.forEach(item => {
        if (item.Name === 'Amount') amount = item.Value;
        if (item.Name === 'MpesaReceiptNumber') receipt = item.Value;
        if (item.Name === 'PhoneNumber') phone = item.Value;
      });

      console.log(`💰 Amount: ${amount}, Receipt: ${receipt}, Phone: ${phone}`);

      // TODO: Find user by CheckoutRequestID and activate their account
      // For now, we'll just store the transaction

      await admin.firestore().collection('transactions').add({
        type: 'activation',
        amount: amount,
        receipt: receipt,
        phone: phone,
        checkoutId: checkoutRequestID,
        status: 'completed',
        timestamp: admin.firestore.FieldValue.serverTimestamp()
      });

      console.log('✅ Transaction saved to Firestore');

    } else {
      // Payment failed
      console.log('❌ Payment failed:', resultDesc);

      await admin.firestore().collection('failed_payments').add({
        checkoutId: checkoutRequestID,
        resultCode: resultCode,
        resultDesc: resultDesc,
        timestamp: admin.firestore.FieldValue.serverTimestamp()
      });
    }

    // Always acknowledge receipt (required by Safaricom)
    return res.json({
      ResultCode: 0,
      ResultDesc: 'Success'
    });

  } catch (error) {
    console.error('💥 Error processing callback:', error);

    // Still return success to Safaricom (prevents retries)
    return res.json({
      ResultCode: 0,
      ResultDesc: 'Accepted'
    });
  }
});

// Test endpoint to verify the function is working
exports.test = functions.https.onRequest((req, res) => {
  res.json({
    message: 'M-Pesa callback function is working!',
    timestamp: new Date().toISOString()
  });
});