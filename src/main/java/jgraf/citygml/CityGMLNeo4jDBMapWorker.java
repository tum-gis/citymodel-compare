package jgraf.citygml;

import jgraf.core.GraphRef;

import java.util.concurrent.Callable;

public class CityGMLNeo4jDBMapWorker<T extends GraphRef> implements Callable<T> {
    private final CityGMLNeo4jDB cityGMLNeo4jDB;
    private final Object source;
    private final String partitionLabel;

    public CityGMLNeo4jDBMapWorker(CityGMLNeo4jDB cityGMLNeo4jDB, Object source, String partitionLabel) {
        this.cityGMLNeo4jDB = cityGMLNeo4jDB;
        this.source = source;
        this.partitionLabel = partitionLabel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T call() throws Exception {
        return (T) cityGMLNeo4jDB.map(source, partitionLabel);
    }
}
