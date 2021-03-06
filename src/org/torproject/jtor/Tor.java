package org.torproject.jtor;


import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.torproject.jtor.circuits.Circuit;
import org.torproject.jtor.circuits.impl.CircuitManagerImpl;
import org.torproject.jtor.circuits.impl.ConnectionManagerImpl;
import org.torproject.jtor.circuits.impl.StreamManager;
import org.torproject.jtor.config.impl.TorConfigImpl;
import org.torproject.jtor.control.ControlServer;
import org.torproject.jtor.control.impl.ControlServerTCP;
import org.torproject.jtor.directory.Directory;
import org.torproject.jtor.directory.Router;
import org.torproject.jtor.directory.impl.DirectoryImpl;
import org.torproject.jtor.directory.impl.DocumentParserFactoryImpl;
import org.torproject.jtor.directory.impl.NetworkStatusManager;
import org.torproject.jtor.directory.parsing.DocumentParserFactory;
import org.torproject.jtor.logging.ConsoleLogger;

public class Tor {
	
	public final static String version = "JTor 0.0.0";
	
	private final Directory directory;
	private final DocumentParserFactory parserFactory;
	private final ConnectionManagerImpl connectionManager;
	private final CircuitManagerImpl circuitManager;
	private final StreamManager streamManager;
	private final Logger logger;
	private final TorConfig config;
	private final NetworkStatusManager statusManager;
	private final ControlServer controlServer;
	
	public Tor() {
		this(new ConsoleLogger());
	}
	
	public Tor(Logger logger) {
		Security.addProvider(new BouncyCastleProvider());
		this.logger = logger;
		this.config = new TorConfigImpl(logger);
		this.directory = new DirectoryImpl(logger, config);
		parserFactory = new DocumentParserFactoryImpl(logger);
		connectionManager = new ConnectionManagerImpl();
		streamManager = new StreamManager();
		circuitManager = new CircuitManagerImpl(directory, connectionManager, streamManager, logger);
		statusManager = new NetworkStatusManager(directory, logger);
		controlServer = new ControlServerTCP(this, config, logger);
	}
	
	
	public void start() {
		config.loadDefaults();
		config.loadConf();
		directory.loadFromStore();
		statusManager.startDownloadingDocuments();
		circuitManager.startBuildingCircuits();
		controlServer.startServer();
	}
	
	public Circuit createCircuitFromNicknames(List<String> nicknamePath) {
		final List<Router> path = directory.getRouterListByNames(nicknamePath);
		return createCircuit(path);
	}
	
	public Circuit createCircuit(List<Router> path) {
		return circuitManager.createCircuitFromPath(path);
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public Directory getDirectory() {
		return directory;
	}
	
	public TorConfig getConfig() {
		return config;
	}
	
	public DocumentParserFactory getDocumentParserFactory() {
		return parserFactory;
	}

	public ConnectionManagerImpl getConnectionManager() {
		return connectionManager;
	}
	
}
