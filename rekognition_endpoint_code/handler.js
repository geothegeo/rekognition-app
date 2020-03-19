'use strict';

const AWS = require("aws-sdk");
var rekognition = new AWS.Rekognition({apiVersion: '2016-06-27'});

module.exports.hello = async event => {
  
  console.log(JSON.stringify(event, null, 2));
  
  return {
    statusCode: 200,
    body: JSON.stringify(
          "Hi has run"   
      )
  };
  
  // return {
  //   statusCode: 200,
  //   body: JSON.stringify(
  //     {
  //       message: 'Go Serverless v1.0! Your function executed successfully!',
  //       input: event,
  //     },
  //     null,
  //     2
  //   ),
  // };

  // Use this code if you don't use the http event with the LAMBDA-PROXY integration
  // return { message: 'Go Serverless v1.0! Your function executed successfully!', event };
};

module.exports.analyzeImage = (event, context, callback) => {
  
  console.log('analyzeImage run');
  
  console.log(JSON.stringify(event.body, null, 2));
  // console.log(JSON.stringify(event.body.img, null, 2));
  
  var params = {
    Image: { /* required */
      Bytes: Buffer.from(event.body, 'base64') /* Strings will be Base-64 encoded on your behalf */,
    },
    // MaxLabels: 5,
    MinConfidence: 70
  };
  rekognition.detectLabels(params, function(err, data) {
    if (err) {
      console.log(err, err.stack);
      callback({
        statusCode: 403,
        body: JSON.stringify(err, null, 2) 
      });
      return err;
    }
    else {
      console.log(data);           // successful response
      callback(null, {
        statusCode: 201,
        body: JSON.stringify(data, null, 2) 
      });
      return data;
    }
  });
  
  // return {
  //   statusCode: 201,
  //   body: JSON.stringify({
  //     msg: 'lambda not complete'
  //   }, null, 2)
  // };
  
};

// module.exports = {
//   hi123: () => {
//     console.log('hi123 has run');
//   },
//   yomama: 5
// };