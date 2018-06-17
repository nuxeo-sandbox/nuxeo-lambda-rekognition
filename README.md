## Description
This plugin provides an integration between AWS Rekognition and the Nuxeo Platform using AWS Lambda.

## Important Note

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Requirements
Building requires the following software:
- git
- maven
- npm

## How to build
```
git clone https://github.com/nuxeo-sandbox/nuxeo-lambda-rekognition
cd nuxeo-lambda-rekognition
mvn clean install
```

## Deploying
* Install the marketplace package from the admin center or using nuxeoctl

## AWS Configuration
This plugin shares AWS credentials with the [S3 storage package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/amazon-s3-online-storage)

* Package the function
```
cd lambda/nuxeo-picture-rekognition
npm install
```
Zip the content of lambda/nuxeo-picture-rekognition

* Create a lambda function nuxeo-picture-rekognition
  * Upload the function zip file generated in the previous step
  * Add one environment variables in the function Configuration
    * rekognitionRegion: the region to use for rekognition (which is not available in all regions)


## Known limitations
This plugin is a work in progress.

## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
