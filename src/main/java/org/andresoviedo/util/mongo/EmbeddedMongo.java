package org.andresoviedo.util.mongo;

import org.andresoviedo.util.io.IOUtils;
import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * An embedded instance of mongo server
 * 
 * @author andres
 * 
 * @see https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
 */
public class EmbeddedMongo {

	private static final MongodStarter STARTER = MongodStarter.getDefaultInstance();

	public enum Version {
		@Deprecated
		V_2_6(de.flapdoodle.embed.mongo.distribution.Version.Main.V2_6);
		final de.flapdoodle.embed.mongo.distribution.Version.Main v;

		Version(de.flapdoodle.embed.mongo.distribution.Version.Main v) {
			this.v = v;
		}

		public de.flapdoodle.embed.mongo.distribution.Version.Main getV() {
			return v;
		}

	}

	private final Version version;
	/**
	 * Port for mongo server
	 */
	private final int port;

	private final MongodExecutable _mongodExe;
	private final MongodProcess _mongod;
	private final MongoClient mongoClient;

	public EmbeddedMongo(Version version) {
		this(IOUtils.freePort(), Version.V_2_6);
	}

	public EmbeddedMongo(int port, Version version) {
		this.port = port;
		this.version = version;
		try {
			_mongodExe = STARTER.prepare(new MongodConfigBuilder().version(version.getV())
					.net(new Net(port, Network.localhostIsIPv6())).build());
			_mongod = _mongodExe.start();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		mongoClient = new MongoClient("localhost", port);
	}

	/**
	 * @return the port where the mongo server is started
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Stop mongo server process
	 */
	public void close() {
		mongoClient.close();
		_mongod.stop();
		_mongodExe.stop();
	}

	/**
	 * Insert an object to database
	 * 
	 * @param dbName
	 * @param collectionName
	 * @param obj
	 */
	public ObjectId insert(String dbName, String collectionName, DBObject obj) {
		DB db = mongoClient.getDB(dbName);
		DBCollection collection = db.getCollection(collectionName);
		collection.save(obj);
		return (ObjectId) obj.get("_id");
	}

	public DBObject get(String dbName, String collectionName, ObjectId id) {
		DB db = mongoClient.getDB(dbName);
		DBCollection collection = db.getCollection(collectionName);
		return collection.findOne(id);

	}

}
