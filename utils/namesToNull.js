var host = "memex";
var port = 27017;
var db = "memex";
var collection = "mergedFixedPhones";

var async = require('async');
var MongoClient = require('mongodb').MongoClient;

var winston = require('winston');
var log = new (winston.Logger)({ transports : [new (winston.transports.Console)({level:"info", timestamp: true})] });

MongoClient.connect("mongodb://" + host +":" + port + "/"+ db , function(err, db) {
	if(!err) {
		log.info("We are connected");
		var collection = db.collection("mergedFixed");
		doAction(collection);
	}
});

var doAction = function(collection) {
	log.info("Fetching rows");
	collection.find().toArray(function(err, items) {
		log.info(items.length);

		async.each(items, function(doc, docCallback) {
			if(!doc["name"] ) {

				log.info("hit");

				doc["name"] = "";

				collection.save(doc, {w:1}, function(err, savedDoc) {
					if(err) {
						log.error(err.message);
					}
					docCallback();
				});
			} else {
				docCallback();
			}
		}, function() {
			process.exit();
		});
	});
};