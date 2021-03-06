package org.torproject.jtor.circuits;

import java.util.List;

import org.torproject.jtor.directory.Router;

public interface CircuitManager {
	void startBuildingCircuits();
	Stream getDirectoryStream();
	Circuit createCircuitFromNicknames(List<String> nicknamePath);
	Circuit createCircuitFromPath(List<Router> path);
}
