#### 访问内网服务

````


./ngrok.exe config add-authtoken 3i5oi8hfYa1V13uj3NpoA_5W3vS2i9SjPL5zcWT1Q9V


./ngrok.exe http http://localhost:1960



https://106f-2409-8a62-f11-d5b0-8c54-86e-9e23-4dbe.ngrok-free.app/swagger-ui

https://106f-2409-8a62-f11-d5b0-8c54-86e-9e23-4dbe.ngrok-free.app/telegramBot/callback
secret_token: zYCn88NYyzsjG9QGd8626BMGQ5y7DFBc
6968916542:AAFseuM2BiI1WhPI5YvIR32CTMyqYU6qyfU
 
 
 https://api.telegram.org/bot6968916542:AAFseuM2BiI1WhPI5YvIR32CTMyqYU6qyfU/getWebhookInfo
 
 
curl -X "POST" "https://api.telegram.org/bot{httpApiToken}/setWebhook?url={webhookUrl}&secret_token={secretToken}"
curl -X "POST" "https://api.telegram.org/bot6968916542:AAFseuM2BiI1WhPI5YvIR32CTMyqYU6qyfU/setWebhook?url=https://106f-2409-8a62-f11-d5b0-8c54-86e-9e23-4dbe.ngrok-free.app/telegramBot/callback&secret_token=zYCn88NYyzsjG9QGd8626BMGQ5y7DFBc"
 
````