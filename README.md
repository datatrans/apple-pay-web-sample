# Apple Pay Web Sample App
This small Spring Boot App demonstrates a basic Apple Pay Web integration with Datatrans.
Use it as a step by step guide to get started with Apple Pay on the web.

Demo: [https://applepay-datatrans-sample.herokuapp.com](https://applepay-datatrans-sample.herokuapp.com)

![Demo](doc/applepay-button.png "Demo App")

## What you need
1. [A Datatrans test merchantId](https://www.datatrans.ch/en/technics/test-account) and access
to the [webadmin tool](https://pilot.datatrans.biz/)
2. [An apple developer account](https://developer.apple.com/account/)
3. Java installed on your local dev system
4. `openssl` installed on your local dev system
5. A domain. For example my-shop.com (or the one Heroku assigns to you)
6. An Apple Pay ready iPhone or a late 2016 MacBook Pro with Touchbar
7. A credit card from an issuer (bank) supporting ApplePay
8. A [Heroku](https://www.heroku.com) account if you want to deploy this sample application
9. Experience with the ['Getting Started on Heroku with Java'](https://devcenter.heroku.com/articles/getting-started-with-java#define-config-vars) Guide

## Initial Apple Pay Web setup
1. [Create a new merchantId](https://developer.apple.com/account/ios/identifier/merchant/create) on developer.apple.com
2. Click on the 'Edit' button of the newly created merchantId
3. Here you see 3 'boxes'
   1. Payment Processing Certificate
   
        ![Payment processing certificate](doc/payment-processing-cert.png "Payment processing certificate")
    
        This is the certificate you need to upload in the Datatrans [webadmin tool](https://pilot.datatrans.biz/). 
        But first a CSR needs to be created. For this (also in the webadmin tool), navigate to *'UPP Administration' > 'UPP Security' >
        'Apple Pay key and certificate'* fill in the details for 'Certificate subject DN' 
        and click the 'Generate new key' button. Now download the CSR and use it to create the payment processing
        certificate. Finally, again in the webadmin tool, upload & import the certificate.
        
        Side note: Once you completed this step you should be ready to perform Apple Pay (not Web) transactions
        with the [Datatrans iOS Mobile Library](https://pilot.datatrans.biz/showcase/doc/iOS_Developers_Manual.pdf)
        
   2. Merchant Domains
   
      ![Merchant domains](doc/merchant-domains.png "Merchant domains")
      
      Apple needs to validate your shop domain. Add your fully qualified domain name there and upload the 
       verification file as instructed (or check the 'Deploy to Heroku' section if you want to use the domain
        assigned to you by Heroku). You should get your domain validated pretty easily. You can also do this
       step later. During the deployment of the app to Heroku, you will be assigned a Domain name. This one can
       then be used for validation. For example:
       
       ![Domain validated](doc/domain-validated.png "Domain validated")
         
   3. Merchant Identity Certificate
      
      ![Merchant identity certificate](doc/merchant-identity-cert.png "Merchant identity certificate")
      
      This is the certificate you need to make a connection from your server to apple to do the merchant validation.
      **Do not** re-use the CSR from above here. Instead, create your own:
      
      ```zsh
      $ openssl req -sha256 -nodes -newkey rsa:2048 -keyout applepaytls.key -out applepaytls.csr
      ```
      
      Use `applepaytls.csr` to create your merchant identity certificate. 
      
      Convert the downloaded merchant identity certificate to `.pem`
      
      ```zsh
      $ openssl x509 -inform der -in certFromApple.cer -out merchant_identity_cert.pem
      ```
      
      And finally create a `.p12` file
      
      ```zsh
      $ openssl pkcs12 -export -in ./merchant_identity_cert.pem -inkey ./applepaytls.key -out ./apple-pay.p12 -name "Datatrans Showcase ApplePay key"
      ```
            
## Prepare the sample application
1. Clone this repo
    ```zsh
    $ git clone git@github.com:datatrans/apple-pay-web-sample.git
    $ cd apple-pay-web-sample
    ```
    
2. Put the `apple-pay.p12` file into folder `src/main/resources/tls`
3. Adjust the `application.properties` in `src/main/resources`

   `ch.datatrans.applepay.merchantIdentifier`: The merchant identifier you used to create your merchantId
   on developer.apple.com
   
    `ch.datatrans.applepay.domainName`: The domain you used on developer.apple.com (where you uploaded
    the verification file) or the Heroku domain name assigned to you (see the 'Deploy to Heroku' section). 
    
    `ch.datatrans.applepay.displayName`: Will be shown on the touchbar during a payment.
    
    `ch.datatrans.merchantId`: Your Datatrans merchantId
    
    `ch.datatrans.sign`: The sign configured in the webadmin tool. Note: this sample currently
    only supports security level 1 (=static sign).
    
    ![Touchbar](doc/touchbar.png "Touchbar")
    
## Deploy to Heroku
1. Create the application
    
    ```zsh   
    $ heroku create <your-app-name>
    ```
    
2. Make sure to validate the domain assigned to you by Heroku on developer.apple.com. For this put a folder
with the name `.well-know` to `src/main/resources/static` 
and put the `apple-developer-merchantid-domain-association` file (downloaded from Apple) there. 

3. Set the `$KEYSTORE_PASSWORD` config variable used in the `Procfile`. The value should be the password you
used to create the `apple-pay.p12` file.
    
    ```zsh
    $ heroku config:set KEYSTORE_PASSWORD=password
    
    ```
    
4. Push to Heroku and launch the instance
   
   ```zsh
   $ git push heroku master
   $ heroku ps:scale web=1
   $ heroku open
   ```
   
## Testing
On your iPhone / MacBook its the easiest if you just configure a real Apple Pay enabled credit card. Authorizations
whith real cards will be declined on the Datatrans test system (https://pilot.datatrans.biz). So don't worry,
your card will not be charged. In order to get some successful transactions Datatrans has the following logic in place 
(only on the test system obviously):
- If a valid Apple Pay token is sent and the amount is < 100 (equals 1USD or 1CHF) we do the following replacements:
    
    cardno=4242 4242 4242 4242  
    expm=12      
    expy=18
    
- If an empty token is sent, Datatrans uses a test token with the following values:

    cardno=4242 4242 4242 4242  
    expm=12  
    expy=18  
    amount=100  
    currency=CHF
    
## Remarks
- Please **NEVER EVER** use this code in any form in production. 
- The client side code in `src/main/resources/static` is a 1:1 copy from the
[Apple Pay Web Emporium](https://developer.apple.com/library/content/samplecode/EmporiumWeb/) with some
small adjustments to make the actual authorization with Datatrans.
- If you have questions please raise an issues and add the label "question".


        
    
