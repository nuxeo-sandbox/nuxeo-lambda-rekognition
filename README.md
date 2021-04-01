**NOTE: Rekognition integration is now provided by (Nuxeo AI)[https://doc.nuxeo.com/nxdoc/nuxeo-ai/#aws]**


# Description
This plugin provides an integration between AWS Rekognition and the Nuxeo Platform using AWS Lambda.

# Important Note

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

# Requirements
Building requires the following software:
- git
- maven
- npm

# How to build
```
git clone https://github.com/nuxeo-sandbox/nuxeo-lambda-rekognition
cd nuxeo-lambda-rekognition
mvn clean install
```

# Deploying
* Install the marketplace package from the admin center or using nuxeoctl

# AWS Configuration
This plugin shares AWS credentials with the [S3 storage package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/amazon-s3-online-storage)

* Package the function
```
cd lambda/nuxeo-picture-rekognition
npm install
```
Zip the content of lambda/nuxeo-picture-rekognition

*(Note for Nuxeo Presales: this function is already deployed in us-west-1, us-west-2, and us-east-1)*

* Create a lambda function nuxeo-picture-rekognition
  * Upload the function zip file generated in the previous step
  * Add one environment variables in the function Configuration
    * rekognitionRegion: the region to use for rekognition (which is not available in all regions)

# Support

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

# License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

# About Nuxeo

Nuxeo Platform is an open source Content Services platform, written in Java. Data can be stored in both SQL & NoSQL databases.

The development of the Nuxeo Platform is mostly done by Nuxeo employees with an open development model.

The source code, documentation, roadmap, issue tracker, testing, benchmarks are all public.

Typically, Nuxeo users build different types of information management solutions for [document management](https://www.nuxeo.com/solutions/document-management/), [case management](https://www.nuxeo.com/solutions/case-management/), and [digital asset management](https://www.nuxeo.com/solutions/dam-digital-asset-management/), use cases. It uses schema-flexible metadata & content models that allows content to be repurposed to fulfill future use cases.

More information is available at [www.nuxeo.com](https://www.nuxeo.com).
