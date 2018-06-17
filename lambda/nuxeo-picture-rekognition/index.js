const url = require('url');
const request = require('request');

// Load the AWS SDK for Node.js
const AWS = require('aws-sdk');
const rekognition = new AWS.Rekognition({
    apiVersion: '2016-06-27',
    region: process.env.rekognitionRegion
});
const s3 = new AWS.S3({
    region: process.env.AWS_REGION
});


// Get Image
function getObject(bucket,key) {
    return new Promise(function (resolve, reject) {
        const params = {
            Key: key,
            Bucket: bucket
        };
        console.log('Getting Object for ', params);
        s3.getObject(params, function (err,data) {
            if (err) {
                reject('Error:', err);
            } else {
                resolve(data.Body);
            }
        });
    });
}

// Call Rekognition
function callRekognition(image) {
    return new Promise(function (resolve, reject) {
        const params = {
            Image: {
                Bytes: new Buffer(image)
            }
        };
        console.log('Calling Rekognition');
        rekognition.recognizeCelebrities(params, function(err, data) {
            if (err) {
                reject('Error:', err);
            } else {
                resolve(data);
            }
        });
    });
}

// Callback
function buildPath(host, cb, success) {
    const domain = host + '/site/lambda/';
    if (success) {
        return domain + 'success/' +  cb;
    } else {
        return domain + 'error/' + cb;
    }
}


function callREST(result, host, cb, success=true, error=null) {
    var json = {};
    json.result = result;
    json.error = error;

    console.log('JSON: ', json);
    const path = buildPath(host, cb, success);
    console.log("request: ", path);
    return new Promise(function (resolve, reject) {
        request.post(path, {
            json: json,
            headers: {
                'Content-Type': 'application/json'
            }
        }, function (err, response, body) {
            if (err) {
                console.log('Error on callREST', err);
                reject(err);
                return;
            }
            if (success) {
                resolve(response);
            } else {
                resolve(error);
            }
        });
    });
}

exports.handler = (event, context, callback) => {

    const host = event.host;
    const bucket = event.input.bucket;
    const digest = event.input.key;
    const cbId= event.cbId;

    getObject(bucket,digest).then(function (result) {
        console.log('S3 Object', JSON.stringify(result));
        return callRekognition(result);
    })
    .then(function (result) {
        console.log('Rekognition Result', JSON.stringify(result));
        return callREST(result, host, cbId);
    })
    .then(function (resp) {
        console.log('Response',JSON.stringify(resp));
        callback(null,resp);
    })
    .catch(function (err) {
        console.log(err);
        callback(err);
    });
};
