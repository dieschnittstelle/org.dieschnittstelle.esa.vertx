/**
 * Created by master on 16.06.16.
 */
console.log("starting verticle...");

var Vertx = require("vertx-js/vertx");

// the vertx instance we are started on is available via the global variable vertx
console.log("VerticleCRUDJS is running!!! vertx is: " + vertx);

var MongoClient = require("vertx-mongo-js/mongo_client");
var client = MongoClient.createShared(vertx, {});
console.log("MongoClient: " + client);

function handleRequest(message) {
    var crudRequest= message.body();
    // we evaluate the operation attribute (which we  and dispatch to the appropriate crud operation
    switch(crudRequest.operation + "") {
        case "READALL":
            readAll(crudRequest,message);
            break;
        case "READ":
            read(crudRequest,message);
            break;
        case "CREATE":
            create(crudRequest,message);
            break;
        default:
            console.error("cannot handle crud request with operation: " + crudRequest.operation);
            message.reply();
    }
}

var messageType = "org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest";
// we register both for unspecific crud events and for crud events directed towards ourselves
vertx.eventBus().consumer(messageType, handleRequest);
vertx.eventBus().consumer(messageType + ".CRUDVerticleJS", handleRequest);

/*
 * the actual crud operations which give minimal return values rather than CRUDResult objects -- will be handled in CRUDClient
 */
function create(crudRequest,message) {
    console.log("CRUDVerticleJS: create()");

    client.save(crudRequest.entity.getClass().getName(), JSON.parse(crudRequest.toJsonObject(crudRequest.entity).encode()), function (res, res_err) {

        if (res_err == null) {
            var id = res;
            console.log("CRUDVerticleJS: create(): saved entity of type " + crudRequest.entity.getClass().getName() + " with id " + id);
            message.reply(id);
        } else {
            res_err.printStackTrace();
            message.reply();
        }

    });
}

function readAll(crudRequest,message) {
    console.log("CRUDVerticleJS: readAll()");

    var query = {
    };

    client.find(crudRequest.entityClass.getName(), query, function (res, res_err) {
        if (res_err == null) {
            console.log("CRUDVerticleJS: readAll(): read objects of type " + crudRequest.entityClass.getName() + ": " + JSON.stringify(res));
            //if (res.length > 0) {
            //    Array.prototype.forEach.call(res, function (json) {
            //        // for supporting polymorphic types, we might need to store the concrete class name on the persisted json objects at some moment
            //        crudResult.add(crudRequest.unmarshal(JSON.stringify(json), crudRequest.entityClass));
            //    });
            //}
            //console.log("reply the result...");
            //console.log("result: " + jsonResult);
            message.reply(JSON.stringify(res));
        } else {
            res_err.printStackTrace();
            message.reply();
        }
    });
}

function read(crudRequest,message) {
    console.log("CRUDVerticleJS: read()");

    var query = {
        _id: crudRequest.entityIdString
    };

    console.log("query is: " + JSON.stringify(query));

    // TODO: we get an exeception if we try to use findOne instead...
    client.find(crudRequest.entityClass.getName(), query, /*null,*/ function (res, res_err) {
        if (res_err == null) {
            console.log("CRUDVerticleJS: read(): read object of type " + crudRequest.entityClass.getName() + ": " + res);
            if (res) {
                message.reply(JSON.stringify(res[0]));
            }
            else {
                message.reply();
            }
        } else {
            res_err.printStackTrace();
            message.reply();
        }
    });
}

